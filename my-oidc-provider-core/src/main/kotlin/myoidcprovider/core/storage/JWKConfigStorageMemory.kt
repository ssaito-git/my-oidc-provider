package myoidcprovider.core.storage

import myoidcprovider.core.jwk.JWKConfig

/**
 * JWK コンフィグストレージのインメモリ実装。
 */
class JWKConfigStorageMemory(
    private val jwkConfigMap: Map<String, Map<String, JWKConfig>>,
) : JWKConfigStorage {
    override fun findByIssuer(issuer: String): List<JWKConfig> =
        jwkConfigMap[issuer]?.values?.toList() ?: emptyList()

    override fun findByPrimary(issuer: String): JWKConfig? =
        jwkConfigMap[issuer]?.values?.firstOrNull { it.isPrimary }

    override fun findByKid(issuer: String, kid: String): JWKConfig? =
        jwkConfigMap[issuer]?.get(kid)
}
