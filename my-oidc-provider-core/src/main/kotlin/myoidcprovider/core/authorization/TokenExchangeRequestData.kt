package myoidcprovider.core.authorization

import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.handler.ActorToken
import myoidcprovider.core.handler.SubjectToken
import myoidcprovider.core.request.token.TokenExchangeGrantRequest

data class TokenExchangeRequestData(
    val client: ClientConfig,
    val request: TokenExchangeGrantRequest,
    val subjectToken: SubjectToken?,
    val actorToken: ActorToken?,
)