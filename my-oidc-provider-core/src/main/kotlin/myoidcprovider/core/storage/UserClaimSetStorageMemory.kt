package myoidcprovider.core.storage

import myoidcprovider.core.authentication.UserClaimSet

/**
 * ユーザーのクレームのストレージ。
 */
class UserClaimSetStorageMemory(
    private val userClaimSetMap: Map<String, Map<String, UserClaimSet>>,
) : UserClaimSetStorage {
    override fun findBySubject(issuer: String, subject: String): UserClaimSet? =
        userClaimSetMap[issuer]?.get(subject)
}
