package myoidcprovider.core.authorization

import myoidcprovider.core.metadata.AccessTokenType

/**
 * アクセストークン
 */
data class AccessToken(
    /**
     * Issuer
     */
    val issuer: String,
    /**
     * Client ID
     */
    val clientId: String,
    /**
     * 識別子
     */
    val subject: String?,
    /**
     * トークン
     */
    val token: String,
    /**
     * トークンタイプ
     */
    val tokenType: AccessTokenType,
    /**
     * 有効期間（秒）
     */
    val expiresIn: Long,
    /**
     * 有効期限（UTC）
     */
    val expiresAt: Long,
    /**
     * 発行日時（UTC）
     */
    val issuedAt: Long,
    /**
     * スコープ
     */
    val scope: List<String>?,
)
