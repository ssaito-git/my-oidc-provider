package myoidcprovider.core.storage

import myoidcprovider.core.authorization.RefreshToken
import java.util.concurrent.ConcurrentHashMap

/**
 * リフレッシュトークンのインメモリストレージ。
 */
class RefreshTokenStorageMemory : RefreshTokenStorage {
    private val storage = ConcurrentHashMap<String, ConcurrentHashMap<String, RefreshToken>>()

    override fun save(refreshToken: RefreshToken) {
        storage.computeIfAbsent(
            refreshToken.issuer,
        ) { ConcurrentHashMap() }.putIfAbsent(refreshToken.token, refreshToken)
    }

    override fun findByToken(issuer: String, token: String): RefreshToken? =
        storage.computeIfAbsent(issuer) { ConcurrentHashMap() }[token]

    override fun delete(refreshToken: RefreshToken): Boolean =
        storage.computeIfAbsent(refreshToken.issuer) { ConcurrentHashMap() }.remove(refreshToken.token) != null
}
