package myoidcprovider.core.client.authentication

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.request.authorization.required
import myoidcprovider.core.request.authorization.single
import myoidcprovider.core.storage.ClientConfigStorage
import java.util.Base64

/**
 * クライアント Basic 認証
 */
class ClientSecretBasicAuthenticator(
    private val clientConfigStorage: ClientConfigStorage,
) : ClientAuthenticator {
    override fun match(issuerConfig: IssuerConfig, httpRequest: HttpRequest): Boolean {
        return httpRequest.headers.any { header ->
            header.key == "Authorization" && header.value.any { it.startsWith("Basic") }
        }
    }

    override fun authenticate(
        issuerConfig: IssuerConfig,
        httpRequest: HttpRequest,
    ): Result<ClientConfig, ClientAuthenticationError> = binding {
        val (clientId, password) = convertCredentials(httpRequest.headers).bind()

        val clientConfig = clientConfigStorage.findById(issuerConfig.issuer, clientId).toResultOr {
            ClientAuthenticationError.InvalidCredentials
        }.bind()

        if (clientConfig.secret != password) {
            Err(ClientAuthenticationError.InvalidCredentials).bind<Unit>()
        }

        clientConfig
    }

    private fun convertCredentials(
        headers: Map<String, List<String>>,
    ): Result<Pair<String, String>, ClientAuthenticationError> {
        return headers["Authorization"]
            .required {
                ClientAuthenticationError.InvalidRequest()
            }.single {
                ClientAuthenticationError.InvalidRequest()
            }.andThen { value ->
                value.split(" ").let {
                    if (it.size == 2) {
                        Ok(it[0] to it[1])
                    } else {
                        Err(ClientAuthenticationError.InvalidRequest())
                    }
                }
            }.andThen {
                val (type, credentials) = it

                if (type == "Basic") {
                    Ok(credentials)
                } else {
                    Err(ClientAuthenticationError.InvalidRequest())
                }
            }.andThen {
                try {
                    Ok(String(Base64.getDecoder().decode(it)))
                } catch (_: IllegalArgumentException) {
                    Err(ClientAuthenticationError.InvalidRequest())
                }
            }.andThen { credentials ->
                credentials.split(":").let {
                    if (it.size == 2) {
                        Ok(it[0] to it[1])
                    } else {
                        Err(ClientAuthenticationError.InvalidRequest())
                    }
                }
            }
    }
}
