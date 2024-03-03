package myoidcprovider.core.request.authorization

/**
 * 認可リクエストの情報。
 */
data class AuthorizationRequestData(
    /**
     * Issuer
     */
    val issuer: String,
    /**
     * 一意に識別するキー。
     */
    val key: String,
    /**
     * 有効期限（UTC）
     */
    val expiresAt: Long,
    /**
     * 認可リクエスト。
     */
    val authorizationRequest: AuthorizationRequest,
    /**
     * 認証リクエスト
     */
    val authenticationRequest: AuthenticationRequest?,
)
