package myoidcprovider.core.metadata

/**
 * Prompt
 */
enum class Prompt(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * none
     */
    NONE("none"),

    /**
     * login
     */
    LOGIN("login"),

    /**
     * consent
     */
    CONSENT("consent"),

    /**
     * select_account
     */
    SELECT_ACCOUNT("select_account"),
}
