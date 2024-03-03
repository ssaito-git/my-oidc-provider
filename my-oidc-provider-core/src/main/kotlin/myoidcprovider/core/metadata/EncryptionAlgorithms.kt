package myoidcprovider.core.metadata

/**
 * サポートしている JWE の暗号化アルゴリズム（alg）。
 */
enum class EncryptionAlgorithms(
    /**
     * パラメーター値。
     */
    val value: String,
) {
    /**
     * RSA1_5
     *
     * RSAES-PKCS1-v1_5
     */
    RSA1_5("RSA1_5"),

    /**
     * RSA-OAEP
     *
     * RSAES OAEP using default parameters
     */
    RSA_OAEP("RSA-OAEP"),

    /**
     * RSA-OAEP-256
     *
     * RSAES OAEP using SHA-256 and MGF1 with SHA-256
     */
    RSA_OAEP_256("RSA-OAEP-256"),

    /**
     * A128KW
     *
     * AES Key Wrap with default initial value using 128-bit key
     */
    A128KW("A128KW"),

    /**
     * A192KW
     *
     * AES Key Wrap with default initial value using 192-bit key
     */
    A192KW("A192KW"),

    /**
     * A256KW
     *
     * AES Key Wrap with default initial value using 256-bit key
     */
    A256KW("A256KW"),

    /**
     * dir
     *
     * Direct use of a shared symmetric key as the CEK
     */
    DIR("dir"),

    /**
     * ECDH-ES
     *
     * Elliptic Curve Diffie-Hellman Ephemeral Static key agreement using Concat KDF
     */
    ECDH_ES("ECDH-ES"),

    /**
     * ECDH-ES+A128KW
     *
     * ECDH-ES using Concat KDF and CEK wrapped with "A128KW"
     */
    ECDH_ES_A128KW("ECDH-ES+A128KW"),

    /**
     * ECDH-ES+A192KW
     *
     * ECDH-ES using Concat KDF and CEK wrapped with "A192KW"
     */
    ECDH_ES_A192KW("ECDH-ES+A192KW"),

    /**
     * ECDH-ES+A256KW
     *
     * ECDH-ES using Concat KDF and CEK wrapped with "A256KW"
     */
    ECDH_ES_A256KW("ECDH-ES+A256KW"),

    /**
     * A128GCMKW
     *
     * Key wrapping with AES GCM using 128-bit key
     */
    A128GCMKW("A128GCMKW"),

    /**
     * A192GCMKW
     *
     * Key wrapping with AES GCM using 192-bit key
     */
    A192GCMKW("A192GCMKW"),

    /**
     * A256GCMKW
     *
     * Key wrapping with AES GCM using 256-bit key
     */
    A256GCMKW("A256GCMKW"),

    /**
     * PBES2-HS256+A128KW
     *
     * PBES2 with HMAC SHA-256 and "A128KW" wrapping
     */
    PBES2_HS256_A128KW("PBES2-HS256+A128KW"),

    /**
     * PBES2-HS384+A192KW
     *
     * PBES2 with HMAC SHA-384 and "A192KW" wrapping
     */
    PBES2_HS384_A192KW("PBES2-HS384+A192KW"),

    /**
     * PBES2-HS512+A256KW
     *
     * PBES2 with HMAC SHA-512 and "A256KW" wrapping
     */
    PBES2_HS512_A256KW("PBES2-HS512+A256KW"),
}
