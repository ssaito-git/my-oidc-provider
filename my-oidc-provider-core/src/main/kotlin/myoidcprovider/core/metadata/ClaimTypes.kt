package myoidcprovider.core.metadata

/**
 * サポートするクレームタイプ。
 */
enum class ClaimTypes(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * normal
     */
    NORMAL("normal"),

    /**
     * aggregated
     */
    AGGREGATED("aggregated"),

    /**
     * distributed
     */
    DISTRIBUTED("distributed"),
}
