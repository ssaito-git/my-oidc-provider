package myoidcprovider.core.handler

import com.nimbusds.jose.jwk.JWKSet
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.storage.JWKConfigStorage

/**
 * JWKs リクエストのハンドラー。
 *
 * @param jwkConfigStorage JWK コンフィグのストレージ。
 */
class JWKsRequestHandler(
    private val jwkConfigStorage: JWKConfigStorage,
) {
    /**
     * リクエストを処理する。
     *
     * @param issuer Issuer
     * @return Issuer の JWKs（文字列）。
     */
    fun handle(issuer: IssuerConfig): String {
        val keys = jwkConfigStorage.findByIssuer(issuer.issuer).map { it.jwk }

        return JWKSet(keys).toString()
    }
}
