package myoidcprovider.core.storage

import myoidcprovider.core.jwk.JWKConfig

/**
 * JWK コンフィグのストレージ。
 */
interface JWKConfigStorage {
    /**
     * Issuer に登録されている JWK を取得する。
     *
     * @param issuer Issuer
     * @return Issuer に登録されている [JWKConfig] のリスト。
     */
    fun findByIssuer(issuer: String): List<JWKConfig>

    /**
     * Issuer に登録されているプライマリの JWK を取得する。
     *
     * @param issuer Issuer
     * @return Issuer に登録されているプライマリの [JWKConfig]。
     */
    fun findByPrimary(issuer: String): JWKConfig?

    /**
     * Key ID で JWK を検索する。
     *
     * @param issuer Issuer
     * @param kid Key ID
     * @return Key ID が一致する [JWKConfig]。存在しない場合は null。
     */
    fun findByKid(issuer: String, kid: String): JWKConfig?
}
