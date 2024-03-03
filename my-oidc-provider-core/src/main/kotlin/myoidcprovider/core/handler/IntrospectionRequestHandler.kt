package myoidcprovider.core.handler

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.getOr
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.or
import com.github.michaelbull.result.toResultOr
import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.client.authentication.ClientAuthenticationError
import myoidcprovider.core.client.authentication.ClientAuthenticationManager
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.metadata.TokenTypeHint
import myoidcprovider.core.request.introspection.IntrospectionErrorCode
import myoidcprovider.core.request.introspection.IntrospectionRequest
import myoidcprovider.core.request.introspection.IntrospectionRequestConverter
import myoidcprovider.core.request.introspection.IntrospectionRequestError
import myoidcprovider.core.request.introspection.IntrospectionResponse
import myoidcprovider.core.storage.AccessTokenStorage
import myoidcprovider.core.storage.RefreshTokenStorage
import myoidcprovider.core.util.Clock

/**
 * イントロスペクションリクエストのハンドラー。
 */
class IntrospectionRequestHandler(
    /**
     * クライアント認証マネージャー
     */
    private val clientAuthenticationManager: ClientAuthenticationManager,
    /**
     * アクセストークンストレージ。
     */
    private val accessTokenStorage: AccessTokenStorage,
    /**
     * リフレッシュトークンストレージ。
     */
    private val refreshTokenStorage: RefreshTokenStorage,
    /**
     * クロック。
     */
    private val clock: Clock,
) {
    private val introspectionRequestConverter = IntrospectionRequestConverter()

    /**
     * リクエストを処理する。
     *
     * @param issuer Issuer
     * @param httpRequest HTTP リクエスト
     * @return 成功した場合は [IntrospectionResponse]。失敗した場合は [IntrospectionRequestError]。
     */
    fun handle(
        issuer: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<IntrospectionResponse, IntrospectionRequestError> = binding {
        authenticateClient(issuer, httpRequest).bind()

        val introspectionRequest = introspectionRequestConverter.convert(httpRequest).bind()

        when (introspectionRequest.tokenTypeHint) {
            TokenTypeHint.ACCESS_TOKEN, null -> {
                introspectAccessToken(issuer, introspectionRequest)
                    .or(introspectRefreshToken(issuer, introspectionRequest))
            }
            TokenTypeHint.REFRESH_TOKEN -> {
                introspectRefreshToken(issuer, introspectionRequest)
                    .or(introspectAccessToken(issuer, introspectionRequest))
            }
        }.getOr(IntrospectionResponse(false))
    }

    private fun authenticateClient(
        issuer: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<ClientConfig, IntrospectionRequestError> {
        return clientAuthenticationManager.authenticate(issuer, httpRequest)
            .mapError {
                when (it) {
                    ClientAuthenticationError.InvalidCredentials -> IntrospectionRequestError.ErrorResponse(
                        IntrospectionErrorCode.INVALID_CLIENT,
                        "Invalid credentials.",
                    )
                    is ClientAuthenticationError.InvalidRequest -> IntrospectionRequestError.ErrorResponse(
                        IntrospectionErrorCode.INVALID_CLIENT,
                        it.errorDescription,
                    )
                    ClientAuthenticationError.UnmatchedAuthenticationMethod -> IntrospectionRequestError.ErrorResponse(
                        IntrospectionErrorCode.INVALID_CLIENT,
                        "Client authentication is required.",
                    )
                }
            }
    }

    private fun introspectAccessToken(
        issuer: IssuerConfig,
        introspectionRequest: IntrospectionRequest,
    ): Result<IntrospectionResponse, Unit> {
        return accessTokenStorage.findByToken(issuer.issuer, introspectionRequest.token)?.let {
            if (it.expiresAt < clock.getEpochSecond()) {
                IntrospectionResponse(false)
            } else {
                IntrospectionResponse(
                    true,
                    it.scope,
                    it.clientId,
                    null,
                    it.tokenType,
                    it.expiresAt,
                    it.issuedAt,
                    null,
                    it.subject,
                    null,
                    it.issuer,
                    null,
                )
            }
        }.toResultOr { }
    }

    private fun introspectRefreshToken(
        issuer: IssuerConfig,
        introspectionRequest: IntrospectionRequest,
    ): Result<IntrospectionResponse, Unit> {
        return refreshTokenStorage.findByToken(issuer.issuer, introspectionRequest.token)?.let { refreshToken ->
            if (refreshToken.expiresAt < clock.getEpochSecond()) {
                IntrospectionResponse(false)
            } else {
                IntrospectionResponse(
                    true,
                    refreshToken.scope,
                    refreshToken.clientId,
                    null,
                    refreshToken.tokenType,
                    refreshToken.expiresAt,
                    refreshToken.issuedAt,
                    null,
                    refreshToken.subject,
                    null,
                    refreshToken.issuer,
                    null,
                )
            }
        }.toResultOr { }
    }
}
