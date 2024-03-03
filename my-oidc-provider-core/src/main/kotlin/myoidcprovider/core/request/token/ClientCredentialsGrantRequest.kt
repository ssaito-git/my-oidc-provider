package myoidcprovider.core.request.token

import myoidcprovider.core.metadata.GrantType

/**
 * クライアントクレデンシャルグラントリクエスト。
 */
data class ClientCredentialsGrantRequest(
    /**
     * grant_type (REQUIRED)
     */
    val grantType: GrantType,
    /**
     * scope (OPTIONAL)
     */
    val scope: List<String>?,
)