package myoidcprovider.core.storage

import myoidcprovider.core.authentication.UserClaimSet

/**
 * ユーザーのクレームのストレージ。
 */
interface UserClaimSetStorage {
    /**
     * 識別子で検索する。
     *
     * @param issuer Issuer
     * @param subject 識別子
     * @return 識別子が一致する [UserClaimSet]。存在しない場合は null。
     */
    fun findBySubject(issuer: String, subject: String): UserClaimSet?
}
