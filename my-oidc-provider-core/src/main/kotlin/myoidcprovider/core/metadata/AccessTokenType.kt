package myoidcprovider.core.metadata

/**
 * アクセストークンタイプ
 *
 * [OAuth Access Token Types](https://www.iana.org/assignments/oauth-parameters/oauth-parameters.xml#token-types)
 */
enum class AccessTokenType(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * Bearer
     *
     * [RFC 6750](https://www.rfc-editor.org/rfc/rfc6750.html)
     */
    BEARER("Bearer"),

    /**
     * N_A
     *
     * [RFC 8693 - OAuth 2.0 Token Exchange](https://datatracker.ietf.org/doc/html/rfc8693)
     */
    N_A("N_A"),
}
