package myoidcprovider.core.handler

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapError
import myoidcprovider.core.authorization.AccessTokenGenerator
import myoidcprovider.core.client.authentication.ClientAuthenticationError
import myoidcprovider.core.client.authentication.ClientAuthenticationManager
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.metadata.AccessTokenType
import myoidcprovider.core.request.token.ClientCredentialsGrantRequestConverter
import myoidcprovider.core.request.token.TokenErrorCode
import myoidcprovider.core.request.token.TokenRequestError
import myoidcprovider.core.request.token.TokenResponse
import myoidcprovider.core.storage.AccessTokenStorage
import myoidcprovider.core.util.Clock

/**
 * クライアントクレデンシャルグラントのハンドラー。
 */
class ClientCredentialsGrantHandler(
    /**
     * クライアント認証マネージャー
     */
    private val clientAuthenticationManager: ClientAuthenticationManager,
    /**
     * アクセストークンストレージ
     */
    private val accessTokenStorage: AccessTokenStorage,
    /**
     * クロック
     */
    clock: Clock,
) {
    private val clientCredentialsGrantRequestConverter = ClientCredentialsGrantRequestConverter()
    private val accessTokenGenerator = AccessTokenGenerator(clock)

    /**
     * クライアントクレデンシャルグラントのリクエストを処理する。
     *
     * @param issuer Issuer
     * @param httpRequest HTTP リクエスト
     * @return 成功した場合は [TokenResponse]。失敗した場合は [TokenRequestError]。
     */
    fun handle(issuer: IssuerConfig, httpRequest: HttpRequest): Result<TokenResponse, TokenRequestError> = binding {
        val authenticatedClient = clientAuthenticationManager.authenticate(issuer, httpRequest).mapError {
            when (it) {
                is ClientAuthenticationError.InvalidCredentials,
                ClientAuthenticationError.UnmatchedAuthenticationMethod,
                -> TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_CLIENT,
                    "Invalid client.",
                )

                is ClientAuthenticationError.InvalidRequest -> TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_REQUEST,
                    it.errorDescription,
                )
            }
        }.bind()

        val clientCredentialsGrantRequest = clientCredentialsGrantRequestConverter.convert(
            issuer,
            authenticatedClient,
            httpRequest,
        ).bind()

        val accessToken =
            accessTokenGenerator.generate(issuer, authenticatedClient, clientCredentialsGrantRequest.scope, null)

        accessTokenStorage.save(accessToken)

        TokenResponse(
            accessToken.token,
            AccessTokenType.BEARER,
            accessToken.expiresIn,
            null,
            clientCredentialsGrantRequest.scope,
            null,
        )
    }
}
