package myoidcprovider.core.storage

import myoidcprovider.core.request.authorization.AuthorizationRequestData

/**
 * 認可リクエストのストレージ。
 */
interface AuthorizationRequestDataStorage {
    /**
     * 認可リクエストを保存する。
     *
     * @param authorizationRequestData 認可リクエスト
     */
    fun save(authorizationRequestData: AuthorizationRequestData)

    /**
     * キーで検索する。
     *
     * @param issuer Issuer
     * @param key キー
     * @return キーが一致する [AuthorizationRequestData]。存在しない場合は null。
     */
    fun findByKey(issuer: String, key: String): AuthorizationRequestData?

    /**
     * 認可リクエストを削除する。
     *
     * @return 削除に成功した場合は true。失敗した場合は false。
     */
    fun delete(authorizationRequestData: AuthorizationRequestData): Boolean
}
