package myoidcprovider.core.metadata

/**
 * トークンタイプ
 *
 * [Token Type](https://www.iana.org/assignments/oauth-parameters/oauth-parameters.xml#uri)
 */
enum class TokenType(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * アクセストークン
     */
    ACCESS_TOKEN("urn:ietf:params:oauth:token-type:access_token"),

    /**
     * リフレッシュトークン
     */
    REFRESH_TOKEN("urn:ietf:params:oauth:token-type:refresh_token"),

    /**
     * ID トークン
     */
    ID_TOKEN("urn:ietf:params:oauth:token-type:id_token"),

    /**
     * SAML 1.1 assertion
     */
    SAML1("urn:ietf:params:oauth:token-type:saml1"),

    /**
     * SAML 2.0 assertion
     */
    SAML2("urn:ietf:params:oauth:token-type:saml2"),

    /**
     * JWT
     */
    JWT("urn:ietf:params:oauth:token-type:jwt"),
}