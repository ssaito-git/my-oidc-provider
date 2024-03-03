package myoidcprovider.core.authorization

import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.request.authorization.AuthenticationRequest
import myoidcprovider.core.request.authorization.AuthorizationRequest
import myoidcprovider.core.util.Clock
import myoidcprovider.core.util.TokenGenerator

/**
 * 認可コードジェネレーター
 */
class AuthorizationCodeGenerator(
    private val clock: Clock,
) {
    companion object {
        private const val AUTHORIZATION_CODE_SIZE = 32
    }

    /**
     * 認可コードを作成する。
     *
     * @param issuer Issuer
     * @param client Client
     * @param authorizationRequest 認可リクエスト
     * @param authenticationRequest 認証リクエスト
     * @param subject 識別子
     * @return 認可コード。
     */
    fun generate(
        issuer: IssuerConfig,
        client: ClientConfig,
        authorizationRequest: AuthorizationRequest,
        authenticationRequest: AuthenticationRequest?,
        subject: String,
    ): AuthorizationCode {
        val authorizationCodeDuration = client.authorizationCodeDuration ?: issuer.authorizationCodeDuration
        val expiresAt = clock.getEpochSecond() + authorizationCodeDuration
        return AuthorizationCode(
            issuer.issuer,
            client.id,
            TokenGenerator.generate(AUTHORIZATION_CODE_SIZE),
            expiresAt,
            subject,
            authorizationRequest,
            authenticationRequest,
        )
    }
}
