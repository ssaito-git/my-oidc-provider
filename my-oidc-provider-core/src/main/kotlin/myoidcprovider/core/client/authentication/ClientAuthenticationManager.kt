package myoidcprovider.core.client.authentication

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig

/**
 * クライアント認証のマネージャー。
 */
class ClientAuthenticationManager(
    /**
     * クライアント認証のリスト。
     */
    private val clientAuthenticators: List<ClientAuthenticator>,
) {
    /**
     * クライアント認証を行う。
     *
     * @param issuerConfig Issuer
     * @param httpRequest HTTP リクエスト
     * @return クライアント認証に成功した場合は [ClientConfig]。失敗した場合は [ClientAuthenticationError]。
     */
    fun authenticate(
        issuerConfig: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<ClientConfig, ClientAuthenticationError> {
        val count = clientAuthenticators.count { it.match(issuerConfig, httpRequest) }

        if (count == 0) {
            return Err(ClientAuthenticationError.UnmatchedAuthenticationMethod)
        }

        if (count > 1) {
            return Err(ClientAuthenticationError.InvalidRequest("Duplicate client authentication credentials."))
        }

        return clientAuthenticators.first {
            it.match(
                issuerConfig,
                httpRequest,
            )
        }.authenticate(issuerConfig, httpRequest)
    }
}
