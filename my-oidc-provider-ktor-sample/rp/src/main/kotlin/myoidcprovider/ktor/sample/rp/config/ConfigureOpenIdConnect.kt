package myoidcprovider.ktor.sample.rp.config

import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.oauth
import myoidcprovider.ktor.sample.rp.applicationHttpClient

/**
 * OpenID Connect の設定
 *
 * @param httpClient HTTP クライアント
 */
fun Application.configureOpenIdConnect(httpClient: HttpClient = applicationHttpClient) {
    install(Authentication) {
        oauth("auth-openid") {
            urlProvider = { "http://localhost:8081/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "id provider",
                    authorizeUrl = "http://localhost:8080/auth",
                    accessTokenUrl = "http://localhost:8080/token",
                    requestMethod = HttpMethod.Post,
                    clientId = "sample-rp",
                    clientSecret = "secret",
                    defaultScopes = listOf("openid"),
                )
            }
            client = httpClient
        }
    }
}
