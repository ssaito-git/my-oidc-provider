package myoidcprovider.core.handler

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.toResultOr
import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.client.ClientType
import myoidcprovider.core.client.authentication.ClientAuthenticationError
import myoidcprovider.core.client.authentication.ClientAuthenticationManager
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.metadata.TokenTypeHint
import myoidcprovider.core.request.revocation.RevocationErrorCode
import myoidcprovider.core.request.revocation.RevocationRequest
import myoidcprovider.core.request.revocation.RevocationRequestConverter
import myoidcprovider.core.request.revocation.RevocationRequestError
import myoidcprovider.core.storage.AccessTokenStorage
import myoidcprovider.core.storage.ClientConfigStorage
import myoidcprovider.core.storage.RefreshTokenStorage

/**
 * リヴォケーションリクエストのハンドラー。
 */
class RevocationRequestHandler(
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
) {
    private val revocationRequestConverter = RevocationRequestConverter()

    /**
     * リクエストを処理する。
     *
     * @param issuer Issuer
     * @param httpRequest HTTP リクエスト
     * @return 成功した場合は [Unit]。失敗した場合は [RevocationRequestError]。
     */
    fun handle(
        issuer: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<Unit, RevocationRequestError> = binding {
        val revocationRequest = revocationRequestConverter.convert(httpRequest).bind()

        val authenticatedClient = authenticateClient(issuer, httpRequest).bind()

        when (revocationRequest.tokenTypeHint) {
            TokenTypeHint.ACCESS_TOKEN, null -> {
                if (!revokeAccessToken(issuer, revocationRequest, authenticatedClient).bind()) {
                    revokeRefreshToken(issuer, revocationRequest, authenticatedClient).bind()
                }
            }
            TokenTypeHint.REFRESH_TOKEN -> {
                if (!revokeRefreshToken(issuer, revocationRequest, authenticatedClient).bind()) {
                    revokeAccessToken(issuer, revocationRequest, authenticatedClient).bind()
                }
            }
        }
    }

    private fun authenticateClient(
        issuerConfig: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<ClientConfig?, RevocationRequestError> {
        return clientAuthenticationManager.authenticate(issuerConfig, httpRequest).fold({ Ok(it) }) {
            when (it) {
                is ClientAuthenticationError.InvalidCredentials -> Err(
                    RevocationRequestError.ErrorResponse(
                        RevocationErrorCode.INVALID_CLIENT,
                        "Invalid credentials.",
                    ),
                )

                is ClientAuthenticationError.InvalidRequest -> Err(
                    RevocationRequestError.ErrorResponse(
                        RevocationErrorCode.INVALID_REQUEST,
                        it.errorDescription,
                    ),
                )

                ClientAuthenticationError.UnmatchedAuthenticationMethod -> Ok(null)
            }
        }
    }

    private fun revokeAccessToken(
        issuer: IssuerConfig,
        revocationRequest: RevocationRequest,
        authenticatedClient: ClientConfig?,
    ): Result<Boolean, RevocationRequestError> = binding {
        accessTokenStorage.findByToken(issuer.issuer, revocationRequest.token)?.let {
            verifyClient(issuer, it.clientId, authenticatedClient).bind()
            accessTokenStorage.delete(it)
        } ?: false
    }

    private fun revokeRefreshToken(
        issuer: IssuerConfig,
        revocationRequest: RevocationRequest,
        authenticatedClient: ClientConfig?,
    ): Result<Boolean, RevocationRequestError> = binding {
        refreshTokenStorage.findByToken(issuer.issuer, revocationRequest.token)?.let {
            verifyClient(issuer, it.clientId, authenticatedClient).bind()
            refreshTokenStorage.delete(it)
        } ?: false
    }

    private fun verifyClient(
        issuer: IssuerConfig,
        clientId: String,
        authenticatedClient: ClientConfig?,
    ): Result<Unit, RevocationRequestError> = binding {
        val client = clientConfigStorage.findById(issuer.issuer, clientId)
            .toResultOr {
                RevocationRequestError.ErrorResponse(
                    RevocationErrorCode.INVALID_GRANT,
                    "Unknown client.",
                )
            }.bind()

        if (authenticatedClient != null && authenticatedClient.id != client.id) {
            Err(
                RevocationRequestError.ErrorResponse(
                    RevocationErrorCode.INVALID_GRANT,
                    "Invalid client.",
                ),
            ).bind<Unit>()
        }

        if (client.type == ClientType.CONFIDENTIAL && authenticatedClient == null) {
            Err(
                RevocationRequestError.ErrorResponse(
                    RevocationErrorCode.INVALID_CLIENT,
                    "Client authentication required.",
                ),
            ).bind<Unit>()
        }
    }
}
