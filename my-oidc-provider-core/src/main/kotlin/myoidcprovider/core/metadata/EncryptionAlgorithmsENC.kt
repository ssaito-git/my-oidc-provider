package myoidcprovider.core.metadata

/**
 * サポートしている JWE の対称暗号化アルゴリズム（enc）。
 */
enum class EncryptionAlgorithmsENC(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * A128CBC-HS256
     *
     * AES_128_CBC_HMAC_SHA_256
     */
    A128CBC_HS256("A128CBC-HS256"),

    /**
     * A192CBC-HS384
     *
     * AES_192_CBC_HMAC_SHA_384
     */
    A192CBC_HS384("A192CBC-HS384"),

    /**
     * A256CBC-HS512
     *
     * AES_256_CBC_HMAC_SHA_512
     */
    A256CBC_HS512("A256CBC-HS512"),

    /**
     * A128GCM
     *
     * AES GCM using 128-bit key
     */
    A128GCM("A128GCM"),

    /**
     * A192GCM
     *
     * AES GCM using 192-bit key
     */
    A192GCM("A192GCM"),

    /**
     * A256GCM
     *
     * AES GCM using 256-bit key
     */
    A256GCM("A256GCM"),
}
