package myoidcprovider.core.storage

import myoidcprovider.core.authorization.AuthorizationCode

/**
 * 認可コードのストレージ。
 */
interface AuthorizationCodeStorage {
    /**
     * 認可コードを保存する。
     *
     * @param authorizationCode 認可コード
     */
    fun save(authorizationCode: AuthorizationCode)

    /**
     * コードで検索する。
     *
     * @param issuer Issuer
     * @param code コード
     * @return コードが一致する [AuthorizationCode]。存在しない場合は null。
     */
    fun findByCode(issuer: String, code: String): AuthorizationCode?

    /**
     * 認可コードを削除する。
     *
     * @param authorizationCode 認可コード
     * @return 削除に成功した場合は true。失敗した場合は false。
     */
    fun delete(authorizationCode: AuthorizationCode): Boolean
}
