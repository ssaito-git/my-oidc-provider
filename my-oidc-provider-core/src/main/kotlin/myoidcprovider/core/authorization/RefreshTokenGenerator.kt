package myoidcprovider.core.authorization

import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.metadata.AccessTokenType
import myoidcprovider.core.util.Clock
import myoidcprovider.core.util.TokenGenerator

/**
 * リフレッシュトークンジェネレーター。
 */
class RefreshTokenGenerator(
    /**
     * クロック
     */
    private val clock: Clock,
) {
    companion object {
        private const val REFRESH_TOKEN_SIZE = 32
    }

    /**
     * リフレッシュトークンを作成する。
     *
     * @param issuer Issuer
     * @param client Client
     * @param scope スコープ
     * @param subject 識別子
     * @return リフレッシュトークン。
     */
    fun generate(
        issuer: IssuerConfig,
        client: ClientConfig,
        scope: List<String>?,
        subject: String?,
    ): RefreshToken {
        val refreshTokenDuration = client.refreshTokenDuration ?: issuer.refreshTokenDuration
        val issuedAt = clock.getEpochSecond()
        val expiresAt = issuedAt + refreshTokenDuration

        return RefreshToken(
            issuer.issuer,
            client.id,
            subject,
            TokenGenerator.generate(RefreshTokenGenerator.REFRESH_TOKEN_SIZE),
            AccessTokenType.BEARER,
            refreshTokenDuration,
            expiresAt,
            issuedAt,
            scope,
        )
    }
}
