package myoidcprovider.core.handler

import myoidcprovider.core.config.Endpoint
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.metadata.OAuth2Metadata
import myoidcprovider.core.metadata.PKCECodeChallengeMethod
import myoidcprovider.core.metadata.ResponseMode
import myoidcprovider.core.metadata.TokenEndpointAuthMethod

/**
 * OAuth 2.0 Authorization Server Metadata リクエストのハンドラー。
 *
 * @param endpoint エンドポイント。
 */
class OAuth2MetadataRequestHandler(
    private val endpoint: Endpoint,
) {
    /**
     * リクエストを処理する。
     *
     * @param issuer Issuer
     * @return Issuer が一致する [OAuth2Metadata]。エラーの場合は [OAuth2MetadataRequestHandleError]。
     */
    fun handle(issuer: IssuerConfig): OAuth2Metadata {
        return OAuth2Metadata(
            issuer = issuer.issuer,
            authorizationEndpoint = "${issuer.issuer}${endpoint.authorizationEndpoint}",
            tokenEndpoint = "${issuer.issuer}${endpoint.tokenEndpoint}",
            jwksUri = "${issuer.issuer}${endpoint.jwksEndpoint}",
            responseTypesSupported = issuer.supportedResponseTypes,
            responseModesSupported = listOf(ResponseMode.QUERY),
            grantTypesSupported = issuer.supportedGrantTypes,
            tokenEndpointAuthMethodsSupported = listOf(TokenEndpointAuthMethod.CLIENT_SECRET_BASIC),
            revocationEndpoint = "${issuer.issuer}${endpoint.revocationEndpoint}",
            revocationEndpointAuthMethodsSupported = listOf(TokenEndpointAuthMethod.CLIENT_SECRET_BASIC),
            introspectionEndpoint = "${issuer.issuer}${endpoint.introspectionEndpoint}",
            introspectionEndpointAuthMethodsSupported = listOf(TokenEndpointAuthMethod.CLIENT_SECRET_BASIC),
            codeChallengeMethodsSupported = listOf(PKCECodeChallengeMethod.S256),
        )
    }
}
