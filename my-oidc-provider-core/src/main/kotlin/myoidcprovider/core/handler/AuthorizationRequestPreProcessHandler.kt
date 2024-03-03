package myoidcprovider.core.handler

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.request.authorization.AuthenticationRequestConverter
import myoidcprovider.core.request.authorization.AuthorizationRequestConverter
import myoidcprovider.core.request.authorization.AuthorizationRequestData
import myoidcprovider.core.request.authorization.AuthorizationRequestError
import myoidcprovider.core.storage.AuthorizationRequestDataStorage
import myoidcprovider.core.storage.ClientConfigStorage
import myoidcprovider.core.util.Clock
import myoidcprovider.core.util.TokenGenerator

/**
 * 認可リクエストのハンドラー。
 */
class AuthorizationRequestPreProcessHandler(
    clientConfigStorage: ClientConfigStorage,
    private val authorizationRequestDataStorage: AuthorizationRequestDataStorage,
    private val clock: Clock,
) {
    companion object {
        private const val AUTHORIZATION_REQUEST_DATA_KEY_SIZE = 32
    }

    private val authorizationRequestConverter = AuthorizationRequestConverter(clientConfigStorage)

    private val authenticationRequestConverter = AuthenticationRequestConverter(clientConfigStorage)

    /**
     * リクエストを処理する。
     *
     * @param issuer Issuer
     * @param httpRequest HTTP リクエスト
     * @return 成功した場合は [AuthorizationRequestData]。失敗した場合は [AuthorizationRequestError]。
     */
    fun handle(
        issuer: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<AuthorizationRequestData, AuthorizationRequestError> = binding {
        val authorizationRequest = authorizationRequestConverter.convert(issuer, httpRequest).bind()

        val authenticationRequest = authenticationRequestConverter.convert(
            issuer,
            authorizationRequest,
            httpRequest,
        ).bind()

        val expiresAt = clock.getEpochSecond() + issuer.authorizationRequestDataDuration

        val authorizationRequestData = AuthorizationRequestData(
            issuer.issuer,
            TokenGenerator.generate(AUTHORIZATION_REQUEST_DATA_KEY_SIZE),
            expiresAt,
            authorizationRequest,
            authenticationRequest,
        )

        authorizationRequestDataStorage.save(authorizationRequestData)

        authorizationRequestData
    }
}
