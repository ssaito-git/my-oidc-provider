package myoidcprovider.core.storage

import myoidcprovider.core.authorization.AccessToken

/**
 * アクセストークンのストレージ。
 */
interface AccessTokenStorage {
    /**
     * アクセストークンを保存する。
     *
     * @param accessToken アクセストークン
     */
    fun save(accessToken: AccessToken)

    /**
     * トークンで検索する。
     *
     * @param issuer Issuer
     * @param token トークン
     * @return トークンが一致する [AccessToken]。存在しない場合は null。
     */
    fun findByToken(issuer: String, token: String): AccessToken?

    /**
     * アクセストークンを削除する。
     *
     * @param accessToken アクセストークン
     * @return 削除に成功した場合は true。失敗した場合は false。
     */
    fun delete(accessToken: AccessToken): Boolean
}
