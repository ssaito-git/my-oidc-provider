package myoidcprovider.core.metadata

/**
 * Response Mode
 */
enum class ResponseMode(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * query
     */
    QUERY("query"),

    /**
     * fragment
     */
    FRAGMENT("fragment"),

    /**
     * form_post
     */
    FORM_POST("form_post"),
}
