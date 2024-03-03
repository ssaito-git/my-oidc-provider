package myoidcprovider.core.handler

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.binding
import myoidcprovider.core.authorization.SecurityTokenGenerator
import myoidcprovider.core.client.authentication.ClientAuthenticationManager
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.metadata.GrantType
import myoidcprovider.core.request.authorization.required
import myoidcprovider.core.request.authorization.single
import myoidcprovider.core.request.token.TokenErrorCode
import myoidcprovider.core.request.token.TokenRequestError
import myoidcprovider.core.request.token.TokenRequestParameter
import myoidcprovider.core.request.token.TokenResponse
import myoidcprovider.core.storage.AccessTokenStorage
import myoidcprovider.core.storage.AuthorizationCodeStorage
import myoidcprovider.core.storage.ClientConfigStorage
import myoidcprovider.core.storage.JWKConfigStorage
import myoidcprovider.core.storage.RefreshTokenStorage
import myoidcprovider.core.storage.UserClaimSetStorage
import myoidcprovider.core.util.Clock

/**
 * トークンリクエストのハンドラー。
 */
class TokenRequestHandler(
    clientAuthenticationManager: ClientAuthenticationManager,
    authorizationCodeStorage: AuthorizationCodeStorage,
    clientConfigStorage: ClientConfigStorage,
    accessTokenStorage: AccessTokenStorage,
    refreshTokenStorage: RefreshTokenStorage,
    jwkConfigStorage: JWKConfigStorage,
    userClaimSetStorage: UserClaimSetStorage,
    securityTokenGenerator: SecurityTokenGenerator,
    clock: Clock,
) {
    private val clientCredentialsGrantHandler =
        ClientCredentialsGrantHandler(clientAuthenticationManager, accessTokenStorage, clock)

    private val authorizationCodeGrantHandler = AuthorizationCodeGrantHandler(
        clientAuthenticationManager,
        authorizationCodeStorage,
        clientConfigStorage,
        accessTokenStorage,
        refreshTokenStorage,
        jwkConfigStorage,
        userClaimSetStorage,
        clock,
    )

    private val refreshTokenGrantHandler = RefreshTokenGrantHandler(
        clientAuthenticationManager,
        clientConfigStorage,
        accessTokenStorage,
        refreshTokenStorage,
        clock,
    )

    private val tokenExchangeGrantHandler = TokenExchangeGrantHandler(
        clientAuthenticationManager,
        accessTokenStorage,
        refreshTokenStorage,
        securityTokenGenerator,
        clock,
    )

    /**
     * リクエストを処理する。
     *
     * @param issuer Issuer
     * @param httpRequest HTTP リクエスト
     * @return 成功した場合は [TokenResponse]。失敗した場合は [TokenRequestError]。
     */
    fun handle(
        issuer: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<TokenResponse, TokenRequestError> = binding {
        val grantType = convertGrantType(httpRequest.formParameters).bind()

        when (grantType) {
            GrantType.AUTHORIZATION_CODE -> authorizationCodeGrantHandler.handle(issuer, httpRequest)
            GrantType.CLIENT_CREDENTIALS -> clientCredentialsGrantHandler.handle(issuer, httpRequest)
            GrantType.REFRESH_TOKEN -> refreshTokenGrantHandler.handle(issuer, httpRequest)
            GrantType.TOKEN_EXCHANGE -> tokenExchangeGrantHandler.handle(issuer, httpRequest)
            else -> Err(
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.UNSUPPORTED_GRANT_TYPE,
                    "Unsupported grant type.",
                ),
            )
        }.bind()
    }

    private fun convertGrantType(
        parameters: Map<String, List<String>>,
    ): Result<GrantType, TokenRequestError> {
        return parameters[TokenRequestParameter.GRANT_TYPE]
            .required {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'grant_type' is required.",
                )
            }.single {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    "'grant_type' is duplicated.",
                )
            }.andThen {
                when (it) {
                    GrantType.AUTHORIZATION_CODE.value -> Ok(GrantType.AUTHORIZATION_CODE)
                    GrantType.IMPLICIT.value -> Ok(GrantType.IMPLICIT)
                    GrantType.PASSWORD.value -> Ok(GrantType.PASSWORD)
                    GrantType.CLIENT_CREDENTIALS.value -> Ok(GrantType.CLIENT_CREDENTIALS)
                    GrantType.REFRESH_TOKEN.value -> Ok(GrantType.REFRESH_TOKEN)
                    GrantType.JWT_BEARER.value -> Ok(GrantType.JWT_BEARER)
                    GrantType.SAML2_BEARER.value -> Ok(GrantType.SAML2_BEARER)
                    GrantType.TOKEN_EXCHANGE.value -> Ok(GrantType.TOKEN_EXCHANGE)
                    else -> Err(
                        TokenRequestError.ErrorResponse(
                            TokenErrorCode.UNSUPPORTED_GRANT_TYPE,
                            "'grant_type' value is unknown.",
                        ),
                    )
                }
            }
    }
}
