package myoidcprovider.core.request.introspection

import myoidcprovider.core.metadata.AccessTokenType

/**
 * イントロスペクションレスポンス
 */
data class IntrospectionResponse(
    /**
     * active
     *
     * トークンの状態（必須）
     */
    val active: Boolean,
    /**
     * scope
     *
     * スコープ（任意）
     */
    val scope: List<String>? = null,
    /**
     * client_id
     *
     * トークンを要求したクライアントの ID（任意）
     */
    val clientId: String? = null,
    /**
     * username
     *
     * トークンを承認したリソースオーナーの名前（任意）
     */
    val username: String? = null,
    /**
     * token_type
     *
     * トークンタイプ（任意）
     */
    val tokenType: AccessTokenType? = null,
    /**
     * exp
     *
     * トークンの有効期限（秒）（任意）
     */
    val exp: Long? = null,
    /**
     * iat
     *
     * トークンを発行した日時（秒）（任意）
     */
    val iat: Long? = null,
    /**
     * nbf
     *
     * トークンが有効になる日時（秒）（任意）
     */
    val nbf: Long? = null,
    /**
     * sub
     *
     * トークンを承認したリソースオーナーの識別子（任意）
     */
    val sub: String? = null,
    /**
     * aud
     *
     * トークンの利用者の識別子（任意）
     */
    val aud: String? = null,
    /**
     * iss
     *
     * トークンの発行者（任意）
     */
    val iss: String? = null,
    /**
     * jti
     *
     * トークンの一意な識別子（任意）
     */
    val jti: String? = null,
)
