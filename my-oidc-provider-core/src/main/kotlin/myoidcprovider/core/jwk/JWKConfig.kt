package myoidcprovider.core.jwk

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWK

/**
 * JWK の設定。
 */
data class JWKConfig(
    /**
     * プライマリの可否。
     */
    val isPrimary: Boolean,
    /**
     * 署名アルゴリズム。
     */
    val algorithm: JWSAlgorithm,
    /**
     * JWK。
     */
    val jwk: JWK,
)
