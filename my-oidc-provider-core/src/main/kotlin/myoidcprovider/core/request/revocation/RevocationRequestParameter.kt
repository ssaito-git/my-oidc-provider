package myoidcprovider.core.request.revocation

/**
 * リヴォケーションリクエストパラメーター。
 */
object RevocationRequestParameter {
    /**
     * token (REQUIRED)
     *
     * トークン
     */
    const val TOKEN = "token"

    /**
     * token_type_hint (OPTIONAL)
     *
     * トークンタイプヒント
     */
    const val TOKEN_TYPE_HINT = "token_type_hint"
}
