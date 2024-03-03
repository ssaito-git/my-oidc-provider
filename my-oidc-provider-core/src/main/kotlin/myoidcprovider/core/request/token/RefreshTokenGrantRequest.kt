package myoidcprovider.core.request.token

import myoidcprovider.core.metadata.GrantType

/**
 * リフレッシュトークングラントのリクエスト。
 */
data class RefreshTokenGrantRequest(
    /**
     * grant_type (REQUIRED)
     *
     * グラントタイプ
     */
    val grantType: GrantType,

    /**
     * refresh_token (REQUIRED)
     *
     * リフレッシュトークン
     */
    val refreshToken: String,

    /**
     * scope (OPTIONAL)
     *
     * スコープ
     */
    val scope: List<String>?,
)
