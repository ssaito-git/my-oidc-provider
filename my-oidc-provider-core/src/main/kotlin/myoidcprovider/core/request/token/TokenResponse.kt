package myoidcprovider.core.request.token

import myoidcprovider.core.metadata.AccessTokenType

/**
 * トークンレスポンス
 */
data class TokenResponse(
    /**
     * access_token (REQUIRED)
     *
     * アクセストークン
     */
    val accessToken: String,
    /**
     * token_type (REQUIRED)
     *
     * トークンタイプ
     */
    val tokenType: AccessTokenType,
    /**
     * expires_in (RECOMMENDED)
     *
     * アクセストークン有効期限
     */
    val expiresIn: Long?,
    /**
     * refresh_token (OPTIONAL)
     *
     * リフレッシュトークン
     */
    val refreshToken: String?,
    /**
     * scope
     *
     * スコープ（認可リクエストのスコープと異なる場合は必須。それ以外は任意）
     */
    val scope: List<String>?,
    /**
     * id_token
     *
     * ID トークン（場合、必須。）
     */
    val idToken: String?,
)
