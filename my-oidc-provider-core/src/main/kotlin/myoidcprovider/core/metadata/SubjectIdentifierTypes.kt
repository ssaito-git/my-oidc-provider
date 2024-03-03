package myoidcprovider.core.metadata

/**
 * サポートしているサブジェクトのタイプ。
 */
enum class SubjectIdentifierTypes(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * public
     *
     * すべてのクライアントに対して同一の識別子（sub）を提供する。
     */
    PUBLIC("public"),

    /**
     * pairwise
     *
     * それぞれのクライアントに対して異なる識別子（sub）を提供する。
     */
    PAIRWISE("pairwise"),
}
