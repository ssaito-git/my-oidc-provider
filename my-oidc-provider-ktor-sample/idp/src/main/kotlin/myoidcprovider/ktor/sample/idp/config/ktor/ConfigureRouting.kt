package myoidcprovider.ktor.sample.idp.config.ktor

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.application
import io.ktor.server.application.call
import io.ktor.server.application.plugin
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import myoidcprovider.core.metadata.ResponseMode
import myoidcprovider.core.request.authorization.AuthorizationResponseError
import myoidcprovider.ktor.plugin.OidcProvider
import myoidcprovider.ktor.routing.authorizationEndpoint
import myoidcprovider.ktor.routing.authorizationServerMetadataEndpoint
import myoidcprovider.ktor.routing.introspectionEndpoint
import myoidcprovider.ktor.routing.jwksEndpoint
import myoidcprovider.ktor.routing.openIdProviderConfigurationEndpoint
import myoidcprovider.ktor.routing.revocationEndpoint
import myoidcprovider.ktor.routing.tokenEndpoint
import myoidcprovider.ktor.sample.idp.session.SignInSession
import myoidcprovider.ktor.sample.idp.users
import myoidcprovider.ktor.sample.idp.viewmodel.ConsentFormScopeViewModel
import myoidcprovider.ktor.sample.idp.viewmodel.ConsentFormViewModel
import myoidcprovider.ktor.sample.idp.viewmodel.ErrorPageViewModel
import myoidcprovider.ktor.sample.idp.viewmodel.LoginFormViewModel
import myoidcprovider.ktor.util.toFormPostHtml
import myoidcprovider.ktor.util.toRedirectUrl

/**
 * ルーティングの設定
 */
fun Application.configureRouting() {
    routing {
        authorizationServerMetadataEndpoint()
        openIdProviderConfigurationEndpoint()
        jwksEndpoint()
        authorizationEndpoint()
        tokenEndpoint()
        introspectionEndpoint()
        revocationEndpoint()
    }

    routing {
        get("/sign-in") {
            if (call.sessions.get<SignInSession>() != null) {
                call.respondRedirect("/consent")
            } else {
                call.respond(FreeMarkerContent("SignInForm.ftl", mapOf("viewModel" to LoginFormViewModel())))
            }
        }
        post("/sign-in") {
            val parameters = call.receiveParameters()
            val username = parameters["username"]
            val password = parameters["password"]

            if (username.isNullOrEmpty()) {
                call.respond(
                    FreeMarkerContent(
                        "SignInForm.ftl",
                        mapOf("viewModel" to LoginFormViewModel(usernameInputError = "Username is required.")),
                    ),
                )
                return@post
            }

            if (password.isNullOrEmpty()) {
                call.respond(
                    FreeMarkerContent(
                        "SignInForm.ftl",
                        mapOf(
                            "viewModel" to LoginFormViewModel(
                                username = username,
                                passwordInputError = "Password is required.",
                            ),
                        ),
                    ),
                )
                return@post
            }

            val plugin = application.plugin(OidcProvider)
            val issuer = plugin.issuerResolver(call)
            val user = users[issuer]?.firstOrNull { it.name == username && it.password == password }

            if (user == null) {
                call.respond(
                    FreeMarkerContent(
                        "SignInForm.ftl",
                        mapOf(
                            "viewModel" to LoginFormViewModel(
                                username = username,
                                passwordInputError = "Username or password is incorrect.",
                            ),
                        ),
                    ),
                )
                return@post
            }

            call.sessions.set(SignInSession(user.subject))
            call.respondRedirect("/consent")
        }
        authenticate("auth-user-session") {
            get("/consent") {
                val key = call.request.cookies["key"]

                if (key == null) {
                    call.respond(
                        FreeMarkerContent(
                            "ErrorPage.ftl",
                            mapOf("viewModel" to ErrorPageViewModel("Invalid Request.", "")),
                        ),
                    )
                    return@get
                }

                val plugin = application.plugin(OidcProvider)
                val issuer = plugin.issuerResolver(call)

                val authorizationRequestData = plugin.provider.config.authorizationRequestDataStorage.findByKey(
                    issuer,
                    key,
                )

                if (authorizationRequestData == null) {
                    call.respond(
                        FreeMarkerContent(
                            "ErrorPage.ftl",
                            mapOf("viewModel" to ErrorPageViewModel("Invalid Request.", "")),
                        ),
                    )
                    return@get
                }

                val client = plugin.provider.config.clientConfigStorage.findById(
                    issuer,
                    authorizationRequestData.authorizationRequest.clientId,
                )

                if (client == null) {
                    call.respond(
                        FreeMarkerContent(
                            "ErrorPage.ftl",
                            mapOf("viewModel" to ErrorPageViewModel("Invalid Request.", "")),
                        ),
                    )
                    return@get
                }

                val scopes = authorizationRequestData.authorizationRequest.scope?.map {
                    ConsentFormScopeViewModel(it, "")
                } ?: emptyList()

                call.respond(
                    FreeMarkerContent(
                        "ConsentForm.ftl",
                        mapOf("viewModel" to ConsentFormViewModel(client.name, scopes)),
                    ),
                )
            }
            post("/consent") {
                val key = call.request.cookies["key"]

                if (key == null) {
                    call.respond(
                        FreeMarkerContent(
                            "ErrorPage.ftl",
                            mapOf("viewModel" to ErrorPageViewModel("Invalid Request.", "")),
                        ),
                    )
                    return@post
                }

                val userSession = call.sessions.get<SignInSession>()

                if (userSession == null) {
                    call.respond(
                        FreeMarkerContent(
                            "ErrorPage.ftl",
                            mapOf("viewModel" to ErrorPageViewModel("Invalid Request.", "")),
                        ),
                    )
                    return@post
                }

                val plugin = application.plugin(OidcProvider)
                val issuer = plugin.issuerResolver(call)
                val consent = call.receiveParameters()["action"] == "accept"

                plugin.provider.handleAuthorizationRequestPostProcessHandler(
                    issuer,
                    userSession.subject,
                    key,
                    consent,
                )
                    .onSuccess {
                        when (it.responseMode) {
                            ResponseMode.FORM_POST -> {
                                call.respondText(ContentType.Text.Html) { it.toFormPostHtml() }
                            }

                            ResponseMode.QUERY, ResponseMode.FRAGMENT, null -> {
                                call.respondRedirect(it.toRedirectUrl())
                            }
                        }
                    }
                    .onFailure {
                        when (it) {
                            is AuthorizationResponseError.ErrorResponse -> call.respondRedirect(it.toRedirectUrl())
                            AuthorizationResponseError.InvalidRequest -> call.respond(
                                FreeMarkerContent(
                                    "ErrorPage.ftl",
                                    mapOf("viewModel" to ErrorPageViewModel("Invalid Request.", "")),
                                ),
                            )
                        }
                    }
            }
        }
    }
}
