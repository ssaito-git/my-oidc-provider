package myoidcprovider.core.storage

import myoidcprovider.core.client.ClientConfig

/**
 * クライアントコンフィグストレージのインメモリ実装。
 */
class ClientConfigStorageMemory(
    private val clientConfigs: Map<String, Map<String, ClientConfig>>,
) : ClientConfigStorage {
    override fun findById(issuer: String, id: String): ClientConfig? = clientConfigs[issuer]?.get(id)
}
