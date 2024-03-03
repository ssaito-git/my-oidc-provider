package myoidcprovider.core.handler

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.toResultOr
import myoidcprovider.core.authorization.AccessTokenGenerator
import myoidcprovider.core.authorization.RefreshToken
import myoidcprovider.core.authorization.RefreshTokenGenerator
import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.client.ClientType
import myoidcprovider.core.client.authentication.ClientAuthenticationError
import myoidcprovider.core.client.authentication.ClientAuthenticationManager
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.request.token.RefreshTokenGrantRequest
import myoidcprovider.core.request.token.RefreshTokenGrantRequestConverter
import myoidcprovider.core.request.token.TokenErrorCode
import myoidcprovider.core.request.token.TokenRequestError
import myoidcprovider.core.request.token.TokenResponse
import myoidcprovider.core.storage.AccessTokenStorage
import myoidcprovider.core.storage.ClientConfigStorage
import myoidcprovider.core.storage.RefreshTokenStorage
import myoidcprovider.core.util.Clock

/**
 * リフレッシュトークングラントのハンドラー。
 */
class RefreshTokenGrantHandler(
    /**
     * クライアント認証マネージャー
     */
    private val clientAuthenticationManager: ClientAuthenticationManager,
    /**
     * クライアントストレージ
     */
    private val clientConfigStorage: ClientConfigStorage,
    /**
     * アクセストークンストレージ
     */
    private val accessTokenStorage: AccessTokenStorage,
    /**
     * リフレッシュトークンストレージ
     */
    private val refreshTokenStorage: RefreshTokenStorage,
    /**
     * クロック
     */
    clock: Clock,
) {
    private val refreshTokenGrantRequestConverter = RefreshTokenGrantRequestConverter()
    private val accessTokenGenerator = AccessTokenGenerator(clock)
    private val refreshTokenGenerator = RefreshTokenGenerator(clock)

    /**
     * 認可コードグラントのリクエストを処理する。
     *
     * @param issuer Issuer
     * @param httpRequest HTTP リクエスト
     * @return 成功した場合は [TokenResponse]。失敗した場合は [TokenRequestError]。
     */
    fun handle(issuer: IssuerConfig, httpRequest: HttpRequest): Result<TokenResponse, TokenRequestError> = binding {
        val authenticatedClient = authenticateClient(issuer, httpRequest).bind()

        val refreshTokenGrantRequest = refreshTokenGrantRequestConverter.convert(issuer, httpRequest).bind()

        val refreshToken = refreshTokenStorage.findByToken(
            issuer.issuer,
            refreshTokenGrantRequest.refreshToken,
        ).toResultOr {
            TokenRequestError.ErrorResponse(
                TokenErrorCode.INVALID_GRANT,
                "Refresh token is invalid.",
            )
        }.bind()

        verifyRefreshToken(refreshToken).bind()
        val client = verifyClient(issuer, refreshToken, authenticatedClient).bind()
        val scope = verifyScope(issuer, client, refreshToken, refreshTokenGrantRequest).bind()
        val accessToken = accessTokenGenerator.generate(issuer, client, scope, refreshToken.subject)
        val newRefreshToken = refreshTokenGenerator.generate(issuer, client, scope, refreshToken.subject)

        refreshTokenStorage.delete(refreshToken)
        refreshTokenStorage.save(newRefreshToken)
        accessTokenStorage.save(accessToken)

        TokenResponse(
            accessToken.token,
            accessToken.tokenType,
            accessToken.expiresIn,
            refreshToken.token,
            scope,
            null,
        )
    }

    private fun authenticateClient(
        issuerConfig: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<ClientConfig?, TokenRequestError> {
        return clientAuthenticationManager.authenticate(issuerConfig, httpRequest).fold({ Ok(it) }) {
            when (it) {
                is ClientAuthenticationError.InvalidCredentials -> Err(
                    TokenRequestError.ErrorResponse(
                        TokenErrorCode.INVALID_CLIENT,
                        "Invalid credentials.",
                    ),
                )

                is ClientAuthenticationError.InvalidRequest -> Err(
                    TokenRequestError.ErrorResponse(
                        TokenErrorCode.INVALID_REQUEST,
                        it.errorDescription,
                    ),
                )

                ClientAuthenticationError.UnmatchedAuthenticationMethod -> Ok(null)
            }
        }
    }

    private fun verifyRefreshToken(refreshToken: RefreshToken): Result<Unit, TokenRequestError> = binding {
        if (refreshToken.expiresAt < Clock.getEpochSecond()) {
            Err(
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_GRANT,
                    "Refresh token expired.",
                ),
            ).bind<Unit>()
        }

        Ok(Unit)
    }

    private fun verifyClient(
        issuerConfig: IssuerConfig,
        refreshToken: RefreshToken,
        authenticatedClient: ClientConfig?,
    ): Result<ClientConfig, TokenRequestError> = binding {
        val client = clientConfigStorage.findById(issuerConfig.issuer, refreshToken.clientId)
            .toResultOr {
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_GRANT,
                    "Unknown client.",
                )
            }.bind()

        if (authenticatedClient != null && authenticatedClient.id != client.id) {
            Err(
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_GRANT,
                    "Refresh token is invalid.",
                ),
            ).bind<Unit>()
        }

        if (client.type == ClientType.CONFIDENTIAL && authenticatedClient == null) {
            Err(
                TokenRequestError.ErrorResponse(
                    TokenErrorCode.INVALID_CLIENT,
                    "Client authentication required.",
                ),
            ).bind<Unit>()
        }

        client
    }

    private fun verifyScope(
        issuerConfig: IssuerConfig,
        clientConfig: ClientConfig,
        refreshToken: RefreshToken,
        refreshTokenGrantRequest: RefreshTokenGrantRequest,
    ): Result<List<String>?, TokenRequestError> = binding {
        val scope = refreshTokenGrantRequest.scope ?: refreshToken.scope

        scope?.let {
            if (!issuerConfig.scopes.containsAll(it) || !clientConfig.scopes.containsAll(it)) {
                Err(
                    TokenRequestError.ErrorResponse(
                        TokenErrorCode.INVALID_SCOPE,
                        "Invalid scope.",
                    ),
                )
            }

            if (refreshToken.scope != null && !refreshToken.scope.containsAll(scope)) {
                Err(
                    TokenRequestError.ErrorResponse(
                        TokenErrorCode.INVALID_SCOPE,
                        "Invalid scope.",
                    ),
                )
            }

            it
        }
    }
}
