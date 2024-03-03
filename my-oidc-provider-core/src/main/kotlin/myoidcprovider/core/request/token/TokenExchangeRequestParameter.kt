package myoidcprovider.core.request.token

/**
 * トークンエクスチェンジリクエストパラメーター
 */
object TokenExchangeRequestParameter {
    /**
     * grant_type (REQUIRED)
     *
     * グラントタイプ
     */
    const val GRANT_TYPE = "grant_type"

    /**
     * resource (OPTIONAL)
     *
     * リソース
     */
    const val RESOURCE = "resource"

    /**
     * audience (OPTIONAL)
     *
     * オーディエン
     */
    const val AUDIENCE = "audience"

    /**
     * scope (OPTIONAL)
     *
     * スコープ
     */
    const val SCOPE = "scope"

    /**
     * requested_token_type (OPTIONAL)
     *
     * 要求トークンタイプ
     */
    const val REQUESTED_TOKEN_TYPE = "requested_token_type"

    /**
     * subject_token (REQUIRED)
     *
     * サブジェクトトークン
     */
    const val SUBJECT_TOKEN = "subject_token"

    /**
     * subject_token_type (OPTIONAL)
     *
     * サブジェクトトークンタイプ
     */
    const val SUBJECT_TOKEN_TYPE = "subject_token_type"

    /**
     * actor_token (OPTIONAL)
     *
     * アクタートークン
     */
    const val ACTOR_TOKEN = "actor_token"

    /**
     * actor_token_type
     *
     * アクタートークンタイプ。actor_token が含まれている場合は必須。それ以外は含めてはならない。
     */
    const val ACTOR_TOKEN_TYPE = "actor_token_type"
}