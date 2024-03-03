package myoidcprovider.core.request.introspection

import myoidcprovider.core.metadata.TokenTypeHint

/**
 * イントロスペクションリクエスト。
 */
data class IntrospectionRequest(
    /**
     * token (REQUIRED)
     */
    val token: String,
    /**
     * token_type_hint (OPTIONAL)
     */
    val tokenTypeHint: TokenTypeHint?,
)