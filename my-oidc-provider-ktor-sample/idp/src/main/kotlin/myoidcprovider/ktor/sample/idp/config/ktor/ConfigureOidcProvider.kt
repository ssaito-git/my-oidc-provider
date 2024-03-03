package myoidcprovider.ktor.sample.idp.config.ktor

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import kotlinx.serialization.json.Json
import myoidcprovider.core.request.authorization.AuthenticationErrorResponse
import myoidcprovider.core.request.authorization.AuthorizationErrorResponse
import myoidcprovider.core.request.authorization.InvalidClient
import myoidcprovider.core.request.authorization.InvalidRedirectUri
import myoidcprovider.ktor.plugin.OidcProvider
import myoidcprovider.ktor.sample.idp.User
import myoidcprovider.ktor.sample.idp.config.ProviderConfig
import myoidcprovider.ktor.sample.idp.users
import myoidcprovider.ktor.util.toRedirectUrl
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * OIDC Provider の設定
 */
fun Application.configureOidcProvider() {
    val configFilePath = environment.config.propertyOrNull("providerConfig")?.getString()

    val configJson = if (configFilePath != null) {
        File(configFilePath).readText()
    } else {
        this.javaClass.classLoader.getResource("provider-config.json")?.readText()
            ?: error("provider-config.json を読み込めませんでした。")
    }

    val providerConfig = Json.decodeFromString<ProviderConfig>(configJson)

    providerConfig.issuerConfigs.forEach { issuer ->
        users.computeIfAbsent(issuer.issuer) { mutableListOf() }.let { userList ->
            issuer.users.forEach {
                userList.add(User(it.subject, it.username, it.password))
            }
        }
    }

    val provider = providerConfig.createProvider()

    install(OidcProvider) {
        this.provider = provider
        issuerResolver = {
            "${it.request.local.scheme}://${it.request.local.serverHost}:${it.request.local.serverPort}"
        }
        authorizationRequestErrorHandler = {
            when (it) {
                is AuthenticationErrorResponse -> call.respondRedirect(it.toRedirectUrl())
                is AuthorizationErrorResponse -> call.respondRedirect(it.toRedirectUrl())
                is InvalidClient -> call.respondText { "invalid client" }
                is InvalidRedirectUri -> call.respondText { "invalid redirect uri" }
            }
        }
        authorizationRequestSuccessHandler = {
            call.response.cookies.append("key", it.key)
            call.respondRedirect("/sign-in")
        }
    }
}
