package myoidcprovider.core.client.authentication

import com.github.michaelbull.result.Result
import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig

/**
 * クライアント認証
 */
interface ClientAuthenticator {
    /**
     * 指定されたパラメーターでクライアント認証が可能か確認する。
     *
     * @param issuerConfig Issuer
     * @param httpRequest HTTP リクエスト
     * @return クライアント認証が可能な場合は true。不可能な場合は false。
     */
    fun match(issuerConfig: IssuerConfig, httpRequest: HttpRequest): Boolean

    /**
     * 指定されたパラメーターでクライアント認証を行います。
     *
     * @param issuerConfig Issuer
     * @param httpRequest HTTP リクエスト
     * @return 認証が成功した場合は [ClientConfig]。失敗した場合は [ClientAuthenticationError]。
     */
    fun authenticate(
        issuerConfig: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<ClientConfig, ClientAuthenticationError>
}
