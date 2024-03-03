package myoidcprovider.ktor.sample.idp.config.ktor

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.session
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.response.respond
import io.ktor.server.sessions.SessionStorageMemory
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import myoidcprovider.ktor.sample.idp.session.SignInSession
import myoidcprovider.ktor.sample.idp.session.WebAuthnSignInSession
import myoidcprovider.ktor.sample.idp.session.WebAuthnSignUpSession
import myoidcprovider.ktor.sample.idp.viewmodel.ErrorPageViewModel

/**
 * セッションの設定
 */
fun Application.configureAuthentication() {
    install(Sessions) {
        cookie<SignInSession>("sign_in_session", SessionStorageMemory())
        cookie<WebAuthnSignInSession>("webauthn_sign_in_session", SessionStorageMemory())
        cookie<WebAuthnSignUpSession>("webauthn_sign_up_session", SessionStorageMemory())
    }

    install(Authentication) {
        session<SignInSession>("auth-user-session") {
            validate {
                it
            }
            challenge {
                call.respond(
                    FreeMarkerContent(
                        "ErrorPage.ftl",
                        mapOf("viewModel" to ErrorPageViewModel("Invalid Request.", "")),
                    ),
                )
            }
        }
    }
}
