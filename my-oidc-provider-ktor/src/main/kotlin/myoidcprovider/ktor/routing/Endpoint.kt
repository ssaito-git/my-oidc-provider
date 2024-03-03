package myoidcprovider.ktor.routing

import com.github.michaelbull.result.mapBoth
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.application.plugin
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import myoidcprovider.core.request.introspection.IntrospectionRequestError
import myoidcprovider.core.request.revocation.RevocationRequestError
import myoidcprovider.core.request.token.TokenRequestError
import myoidcprovider.ktor.plugin.OidcProvider
import myoidcprovider.ktor.response.IntrospectionErrorResponseJson
import myoidcprovider.ktor.response.IntrospectionResponseJson
import myoidcprovider.ktor.response.OAuth2MetadataJson
import myoidcprovider.ktor.response.OidcMetadataJson
import myoidcprovider.ktor.response.RevocationErrorResponseJson
import myoidcprovider.ktor.response.TokenErrorResponseJson
import myoidcprovider.ktor.response.TokenResponseJson
import myoidcprovider.ktor.util.toHttpRequest

/**
 * OAuth 2.0 Authorization Server Metadata エンドポイント。
 */
fun Routing.authorizationServerMetadataEndpoint() {
    val plugin = application.plugin(OidcProvider)

    get(plugin.provider.config.endpoint.authorizationServerMetadataEndpoint) {
        val metadata = plugin.provider.handleOAuth2MetadataRequest(plugin.issuerResolver(call))
        call.respond(OAuth2MetadataJson.from(metadata))
    }
}

/**
 * OpenID Connect Discovery エンドポイント
 */
fun Routing.openIdProviderConfigurationEndpoint() {
    val plugin = application.plugin(OidcProvider)

    get(plugin.provider.config.endpoint.openIdProviderConfigurationEndpoint) {
        val metadata = plugin.provider.handleOidcMetadataRequest(plugin.issuerResolver(call))
        call.respond(OidcMetadataJson.from(metadata))
    }
}

/**
 * JWKs エンドポイント
 *
 */
fun Routing.jwksEndpoint() {
    val plugin = application.plugin(OidcProvider)

    get(plugin.provider.config.endpoint.jwksEndpoint) {
        val jwks = plugin.provider.handleJWKsRequest(plugin.issuerResolver(call))
        call.respondText(jwks, ContentType.Application.Json)
    }
}

/**
 * 認可エンドポイント
 */
fun Routing.authorizationEndpoint() {
    val plugin = application.plugin(OidcProvider)

    route(plugin.provider.config.endpoint.authorizationEndpoint) {
        get {
            val httpRequest = call.toHttpRequest()
            plugin.provider.handleAuthorizationRequestPreProcessHandler(
                plugin.issuerResolver(call),
                httpRequest,
            ).mapBoth(
                { plugin.authorizationRequestSuccessHandler(this, it) },
                { plugin.authorizationRequestErrorHandler(this, it) },
            )
        }
        post {
            plugin.provider.handleAuthorizationRequestPreProcessHandler(
                plugin.issuerResolver(call),
                call.toHttpRequest(),
            ).mapBoth(
                { plugin.authorizationRequestSuccessHandler(this, it) },
                { plugin.authorizationRequestErrorHandler(this, it) },
            )
        }
    }
}

/**
 * トークンエンドポイント
 */
fun Routing.tokenEndpoint() {
    val plugin = application.plugin(OidcProvider)

    post(plugin.provider.config.endpoint.tokenEndpoint) {
        plugin.provider.handleTokenRequest(
            plugin.issuerResolver(call),
            call.toHttpRequest(),
        ).mapBoth(
            { call.respond(TokenResponseJson.from(it)) },
            { call.respond(TokenErrorResponseJson.from(it as TokenRequestError.ErrorResponse)) },
        )
    }
}

/**
 * イントロスペクションエンドポイント
 *
 */
fun Routing.introspectionEndpoint() {
    val plugin = application.plugin(OidcProvider)

    post(plugin.provider.config.endpoint.introspectionEndpoint) {
        plugin.provider.handleIntrospectionRequest(
            plugin.issuerResolver(call),
            call.toHttpRequest(),
        ).mapBoth(
            { call.respond(IntrospectionResponseJson.from(it)) },
            { call.respond(IntrospectionErrorResponseJson.from(it as IntrospectionRequestError.ErrorResponse)) },
        )
    }
}

/**
 * リヴォケーションエンドポイント
 */
fun Routing.revocationEndpoint() {
    val plugin = application.plugin(OidcProvider)

    post(plugin.provider.config.endpoint.revocationEndpoint) {
        plugin.provider.handleRevocationRequest(
            plugin.issuerResolver(call),
            call.toHttpRequest(),
        ).mapBoth(
            { call.respondBytes(ByteArray(0)) },
            { call.respond(RevocationErrorResponseJson.from(it as RevocationRequestError.ErrorResponse)) },
        )
    }
}
