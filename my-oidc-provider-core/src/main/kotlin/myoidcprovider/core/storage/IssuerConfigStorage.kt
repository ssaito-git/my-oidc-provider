package myoidcprovider.core.storage

import myoidcprovider.core.issuer.IssuerConfig

/**
 * Issuer コンフィグのストレージ。
 */
interface IssuerConfigStorage {
    /**
     * Issuer で Issuer コンフィグを取得する。
     *
     * @param issuer Issuer
     * @return Issuer が一致する [IssuerConfig]。存在しない場合は null。
     */
    fun findByIssuer(issuer: String): IssuerConfig?
}
