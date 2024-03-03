package myoidcprovider.core.client.authentication

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.http.HttpMethod
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.request.authorization.required
import myoidcprovider.core.request.authorization.single
import myoidcprovider.core.storage.ClientConfigStorage

/**
 * クライアントシークレットポスト認証。
 */
class ClientSecretPostAuthenticator(private val clientConfigStorage: ClientConfigStorage) : ClientAuthenticator {
    override fun match(issuerConfig: IssuerConfig, httpRequest: HttpRequest): Boolean {
        if (httpRequest.method != HttpMethod.POST) {
            return false
        }

        return httpRequest.formParameters.contains("client_id") && httpRequest.formParameters.contains("client_secret")
    }

    override fun authenticate(
        issuerConfig: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<ClientConfig, ClientAuthenticationError> {
        if (httpRequest.method != HttpMethod.POST) {
            return Err(ClientAuthenticationError.InvalidRequest())
        }

        return binding {
            val clientId = convertClientId(httpRequest.formParameters).bind()
            val clientSecret = convertClientSecret(httpRequest.formParameters).bind()
            val clientConfig = clientConfigStorage.findById(issuerConfig.issuer, clientId).toResultOr {
                ClientAuthenticationError.InvalidCredentials
            }.bind()

            if (clientConfig.secret == clientSecret) {
                clientConfig
            } else {
                Err(ClientAuthenticationError.InvalidCredentials).bind()
            }
        }
    }

    private fun convertClientId(parameters: Map<String, List<String>>): Result<String, ClientAuthenticationError> {
        return parameters["client_id"]
            .required {
                ClientAuthenticationError.InvalidRequest("'client_id' is required.")
            }.single {
                ClientAuthenticationError.InvalidRequest("'client_id' is duplicated.")
            }
    }

    private fun convertClientSecret(parameters: Map<String, List<String>>): Result<String, ClientAuthenticationError> {
        return parameters["client_secret"]
            .required {
                ClientAuthenticationError.InvalidRequest("'client_secret' is required.")
            }.single {
                ClientAuthenticationError.InvalidRequest("'client_secret' is duplicated.")
            }
    }
}
