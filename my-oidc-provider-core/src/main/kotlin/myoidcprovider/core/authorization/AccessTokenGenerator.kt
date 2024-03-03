package myoidcprovider.core.authorization

import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.metadata.AccessTokenType
import myoidcprovider.core.util.Clock
import myoidcprovider.core.util.TokenGenerator

/**
 * アクセストークンジェネレーター。
 */
class AccessTokenGenerator(
    private val clock: Clock,
) {
    companion object {
        private const val ACCESS_TOKEN_SIZE = 32
    }

    /**
     * アクセストークンを作成する。
     *
     * @param issuer Issuer
     * @param client Client
     * @param scope スコープ
     * @param subject 識別子
     * @return アクセストークン。
     */
    fun generate(
        issuer: IssuerConfig,
        client: ClientConfig,
        scope: List<String>?,
        subject: String?,
    ): AccessToken {
        val accessTokenDuration = client.accessTokenDuration ?: issuer.accessTokenDuration
        val issuedAt = clock.getEpochSecond()
        val expiresAt = issuedAt + accessTokenDuration
        return AccessToken(
            issuer.issuer,
            client.id,
            subject,
            TokenGenerator.generate(ACCESS_TOKEN_SIZE),
            AccessTokenType.BEARER,
            accessTokenDuration,
            expiresAt,
            issuedAt,
            scope,
        )
    }
}
