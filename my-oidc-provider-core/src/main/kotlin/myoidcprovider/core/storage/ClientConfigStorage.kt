package myoidcprovider.core.storage

import myoidcprovider.core.client.ClientConfig

/**
 * クライアントコンフィグのストレージ。
 */
interface ClientConfigStorage {
    /**
     * クライアント ID で検索する。
     *
     * @param issuer Issuer
     * @param id クライアント ID
     * @return クライアント ID が一致するクライアントコンフィグ。存在しない場合は null。
     */
    fun findById(issuer: String, id: String): ClientConfig?
}
