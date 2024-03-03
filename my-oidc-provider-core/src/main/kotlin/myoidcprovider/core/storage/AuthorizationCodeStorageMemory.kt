package myoidcprovider.core.storage

import myoidcprovider.core.authorization.AuthorizationCode
import java.util.concurrent.ConcurrentHashMap

/**
 * 認可コードのインメモリストレージ。
 */
class AuthorizationCodeStorageMemory : AuthorizationCodeStorage {
    private val storage = ConcurrentHashMap<String, ConcurrentHashMap<String, AuthorizationCode>>()

    override fun save(authorizationCode: AuthorizationCode) {
        storage.computeIfAbsent(authorizationCode.issuer) { ConcurrentHashMap() }
            .putIfAbsent(authorizationCode.code, authorizationCode)
    }

    override fun findByCode(issuer: String, code: String): AuthorizationCode? =
        storage.computeIfAbsent(issuer) { ConcurrentHashMap() }[code]

    override fun delete(authorizationCode: AuthorizationCode): Boolean =
        storage.computeIfAbsent(authorizationCode.issuer) { ConcurrentHashMap() }.remove(authorizationCode.code) != null
}
