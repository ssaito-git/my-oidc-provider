package myoidcprovider.core.authorization

import myoidcprovider.core.request.authorization.AuthenticationRequest
import myoidcprovider.core.request.authorization.AuthorizationRequest

/**
 * 認可コード
 */
data class AuthorizationCode(
    /**
     * Issuer
     */
    val issuer: String,
    /**
     * Client ID
     */
    val clientId: String,
    /**
     * コード
     */
    val code: String,
    /**
     * 有効期限（UTC）
     */
    val expiresAt: Long,
    /**
     * 識別子
     */
    val subject: String,
    /**
     * 認可リクエスト
     */
    val authorizationRequest: AuthorizationRequest,
    /**
     * 認証リクエスト
     */
    val authenticationRequest: AuthenticationRequest?,
)
