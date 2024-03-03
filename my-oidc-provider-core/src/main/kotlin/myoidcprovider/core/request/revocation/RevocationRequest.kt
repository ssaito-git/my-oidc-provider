package myoidcprovider.core.request.revocation

import myoidcprovider.core.metadata.TokenTypeHint

/**
 * リヴォケーションリクエスト。
 */
data class RevocationRequest(
    /**
     * token (REQUIRED)
     */
    val token: String,
    /**
     * token_type_hint (OPTIONAL)
     */
    val tokenTypeHint: TokenTypeHint?,
)
