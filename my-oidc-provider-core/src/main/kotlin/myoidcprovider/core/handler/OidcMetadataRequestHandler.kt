package myoidcprovider.core.handler

import myoidcprovider.core.config.Endpoint
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.metadata.OidcMetadata
import myoidcprovider.core.metadata.ResponseType
import myoidcprovider.core.metadata.SigningAlgorithms
import myoidcprovider.core.metadata.SubjectIdentifierTypes

/**
 * OpenID Provider Metadata リクエストのハンドラー。
 *
 * @param endpoint エンドポイント。
 */
class OidcMetadataRequestHandler(
    private val endpoint: Endpoint,
) {
    /**
     * リクエストを処理する。
     *
     * @param issuer Issuer
     * @return Issuer の [OidcMetadata]。
     */
    fun handle(issuer: IssuerConfig): OidcMetadata {
        return OidcMetadata(
            issuer = issuer.issuer,
            authorizationEndpoint = "${issuer.issuer}${endpoint.authorizationEndpoint}",
            tokenEndpoint = "${issuer.issuer}${endpoint.tokenEndpoint}",
            userinfoEndpoint = null,
            jwksUri = "${issuer.issuer}${endpoint.jwksEndpoint}",
            registrationEndpoint = null,
            scopesSupported = null,
            responseTypesSupported = listOf(
                listOf(ResponseType.CODE),
                listOf(ResponseType.CODE, ResponseType.ID_TOKEN),
                listOf(ResponseType.TOKEN),
                listOf(ResponseType.TOKEN, ResponseType.ID_TOKEN),
                listOf(ResponseType.ID_TOKEN),
            ),
            responseModesSupported = null,
            grantTypesSupported = null,
            acrValuesSupported = null,
            subjectTypesSupported = listOf(SubjectIdentifierTypes.PUBLIC),
            idTokenSigningAlgValuesSupported = listOf(SigningAlgorithms.RS256),
            idTokenEncryptionAlgValuesSupported = null,
            idTokenEncryptionEncValuesSupported = null,
            userinfoSigningAlgValuesSupported = null,
            userinfoEncryptionAlgValuesSupported = null,
            userinfoEncryptionEncValuesSupported = null,
            requestObjectSigningAlgValuesSupported = null,
            requestObjectEncryptionAlgValuesSupported = null,
            requestObjectEncryptionEncValuesSupported = null,
            tokenEndpointAuthMethodsSupported = null,
            tokenEndpointAuthSigningAlgValuesSupported = null,
            displayValuesSupported = null,
            claimTypesSupported = null,
            claimsSupported = null,
            serviceDocumentation = null,
            claimsLocalesSupported = null,
            uiLocalesSupported = null,
            claimsParameterSupported = null,
            requestParameterSupported = null,
            requestUriParameterSupported = null,
            requireRequestUriRegistration = null,
            opPolicyUri = null,
            opTosUri = null,
        )
    }
}
