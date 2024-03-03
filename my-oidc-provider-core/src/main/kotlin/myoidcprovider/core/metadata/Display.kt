package myoidcprovider.core.metadata

/**
 * サポートするディスプレイパラメーター。
 */
enum class Display(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * page
     */
    PAGE("page"),

    /**
     * popup
     */
    POPUP("popup"),

    /**
     * touch
     */
    TOUCH("touch"),

    /**
     * wap
     */
    WAP("wap"),
}
