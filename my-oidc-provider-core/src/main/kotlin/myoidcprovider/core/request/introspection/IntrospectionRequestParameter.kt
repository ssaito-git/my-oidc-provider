package myoidcprovider.core.request.introspection

/**
 * イントロスペクションリクエストのパラメーター。
 */
object IntrospectionRequestParameter {
    /**
     * token
     *
     * トークン
     */
    const val TOKEN = "token"

    /**
     * token_type_hint
     *
     * トークンタイプヒント
     */
    const val TOKEN_TYPE_HINT = "token_type_hint"
}