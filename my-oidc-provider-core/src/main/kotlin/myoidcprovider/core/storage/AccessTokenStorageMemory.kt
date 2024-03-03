package myoidcprovider.core.storage

import myoidcprovider.core.authorization.AccessToken
import java.util.concurrent.ConcurrentHashMap

/**
 * アクセストークンのインメモリストレージ。
 */
class AccessTokenStorageMemory : AccessTokenStorage {
    private val storage = ConcurrentHashMap<String, ConcurrentHashMap<String, AccessToken>>()

    override fun save(accessToken: AccessToken) {
        storage.computeIfAbsent(accessToken.issuer) { ConcurrentHashMap() }.putIfAbsent(accessToken.token, accessToken)
    }

    override fun findByToken(issuer: String, token: String): AccessToken? =
        storage.computeIfAbsent(issuer) { ConcurrentHashMap() }[token]

    override fun delete(accessToken: AccessToken): Boolean =
        storage.computeIfAbsent(accessToken.issuer) { ConcurrentHashMap() }.remove(accessToken.token) != null
}
