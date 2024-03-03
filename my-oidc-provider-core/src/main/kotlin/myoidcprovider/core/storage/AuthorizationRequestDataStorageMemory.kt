package myoidcprovider.core.storage

import myoidcprovider.core.request.authorization.AuthorizationRequestData
import java.util.concurrent.ConcurrentHashMap

/**
 * 認可リクエストのインメモリストレージ。
 */
class AuthorizationRequestDataStorageMemory : AuthorizationRequestDataStorage {
    private val storage = ConcurrentHashMap<String, ConcurrentHashMap<String, AuthorizationRequestData>>()

    override fun save(authorizationRequestData: AuthorizationRequestData) {
        storage.computeIfAbsent(
            authorizationRequestData.issuer,
        ) { ConcurrentHashMap() }[authorizationRequestData.key] = authorizationRequestData
    }

    override fun findByKey(issuer: String, key: String): AuthorizationRequestData? =
        storage.computeIfAbsent(issuer) { ConcurrentHashMap() }[key]

    override fun delete(authorizationRequestData: AuthorizationRequestData): Boolean =
        storage[authorizationRequestData.issuer]?.remove(authorizationRequestData.key) != null
}
