package myoidcprovider.core.metadata

/**
 * サポートしている JWS の署名アルゴリズム。
 */
enum class SigningAlgorithms(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * HS256
     *
     * HMAC using SHA-256
     */
    HS256("HS256"),

    /**
     * HS384
     *
     * HMAC using SHA-384
     */
    HS384("HS384"),

    /**
     * HS512
     *
     * HMAC using SHA-512
     */
    HS512("HS512"),

    /**
     * RS256
     *
     * RSASSA-PKCS1-v1_5 using SHA-256
     */
    RS256("RS256"),

    /**
     * RS384
     *
     * RSASSA-PKCS1-v1_5 using SHA-384
     */
    RS384("RS384"),

    /**
     * RS512
     *
     * RSASSA-PKCS1-v1_5 using SHA-512
     */
    RS512("RS512"),

    /**
     * ES256
     *
     * ECDSA using P-256 and SHA-256
     */
    ES256("ES256"),

    /**
     * ES384
     *
     * ECDSA using P-384 and SHA-384
     */
    ES384("ES384"),

    /**
     * ES512
     *
     * ECDSA using P-521 and SHA-512
     */
    ES512("ES512"),

    /**
     * PS256
     *
     * RSASSA-PSS using SHA-256 and MGF1 with SHA-256
     */
    PS256("PS256"),

    /**
     * PS384
     *
     * RSASSA-PSS using SHA-384 and MGF1 with SHA-384
     */
    PS384("PS384"),

    /**
     * PS512
     *
     * RSASSA-PSS using SHA-512 and MGF1 with SHA-512
     */
    PS512("PS512"),

    /**
     * none
     *
     * No digital signature or MAC performed
     */
    NONE("none"),
}
