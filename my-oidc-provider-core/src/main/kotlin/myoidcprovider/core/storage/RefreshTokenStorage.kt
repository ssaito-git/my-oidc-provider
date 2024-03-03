package myoidcprovider.core.storage

import myoidcprovider.core.authorization.RefreshToken

/**
 * リフレッシュトークンのストレージ。
 */
interface RefreshTokenStorage {
    /**
     * リフレッシュトークンを保存する。
     *
     * @param refreshToken リフレッシュトークン
     */
    fun save(refreshToken: RefreshToken)

    /**
     * トークンで検索する。
     *
     * @param issuer Issuer
     * @param token トークン
     * @return トークンが一致する [RefreshToken]。存在しない場合は null。
     */
    fun findByToken(issuer: String, token: String): RefreshToken?

    /**
     * リフレッシュトークンを削除する。
     *
     * @param refreshToken リフレッシュトークン
     * @return 削除に成功した場合は true。失敗した場合は false。
     */
    fun delete(refreshToken: RefreshToken): Boolean
}
