package myoidcprovider.core.storage

import myoidcprovider.core.issuer.IssuerConfig

/**
 * Issuer コンフィグストレージのインメモリ実装。
 */
class IssuerConfigStorageMemory(private val issuerConfigs: Map<String, IssuerConfig>) : IssuerConfigStorage {
    override fun findByIssuer(issuer: String): IssuerConfig? = issuerConfigs[issuer]
}
