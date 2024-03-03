package myoidcprovider.core.metadata

/**
 * トークンエンドポイントでサポートするクライアント認証方式。
 * [OAuth Token Endpoint Authentication Methods](https://www.iana.org/assignments/oauth-parameters/oauth-parameters.xhtml#token-endpoint-auth-method)
 */
enum class TokenEndpointAuthMethod(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * none
     *
     * なし
     */
    NONE("none"),

    /**
     * client_secret_post
     *
     * HTTP POST パラメーター
     */
    CLIENT_SECRET_POST("client_secret_post"),

    /**
     * client_secret_basic
     *
     * HTTP Basic 認証
     */
    CLIENT_SECRET_BASIC("client_secret_basic"),

    /**
     * client_secret_jwt
     *
     * JWT（共通鍵方式）
     */
    CLIENT_SECRET_JWT("client_secret_jwt"),

    /**
     * private_key_jwt
     *
     * JWT（非対称鍵方式）
     */
    PRIVATE_KEY_JWT("private_key_jwt"),

    /**
     * tls_client_auth
     *
     * mTLS
     */
    TLS_CLIENT_AUTH("tls_client_auth"),

    /**
     * self_signed_tls_client_auth
     *
     * 自己署名 TLS
     */
    SELF_SIGNED_TLS_CLIENT_AUTH("self_signed_tls_client_auth"),
}
