package myoidcprovider.core.authentication

/**
 * ユーザーのクレーム。
 */
data class UserClaimSet(
    /**
     * スタンダードクレーム。
     */
    val standardClaim: StandardClaim,
    /**
     * カスタムクレーム。
     */
    val customClaim: Map<String, Any> = emptyMap(),
)
