package myoidcprovider.core

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.gen.ECKeyGenerator
import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.client.ClientType
import myoidcprovider.core.client.authentication.ClientAuthenticationManager
import myoidcprovider.core.client.authentication.ClientSecretBasicAuthenticator
import myoidcprovider.core.client.authentication.ClientSecretPostAuthenticator
import myoidcprovider.core.config.Config
import myoidcprovider.core.config.Endpoint
import myoidcprovider.core.handler.DefaultSecurityTokenGenerator
import myoidcprovider.core.http.HttpMethod
import myoidcprovider.core.http.HttpRequest
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.jwk.JWKConfig
import myoidcprovider.core.metadata.GrantType
import myoidcprovider.core.metadata.PKCECodeChallengeMethod
import myoidcprovider.core.metadata.ResponseType
import myoidcprovider.core.request.authorization.AuthorizationRequestParameter
import myoidcprovider.core.request.introspection.IntrospectionRequestParameter
import myoidcprovider.core.request.token.TokenRequestParameter
import myoidcprovider.core.storage.AccessTokenStorageMemory
import myoidcprovider.core.storage.AuthorizationCodeStorageMemory
import myoidcprovider.core.storage.AuthorizationRequestDataStorageMemory
import myoidcprovider.core.storage.ClientConfigStorageMemory
import myoidcprovider.core.storage.IssuerConfigStorageMemory
import myoidcprovider.core.storage.JWKConfigStorageMemory
import myoidcprovider.core.storage.RefreshTokenStorageMemory
import myoidcprovider.core.storage.UserClaimSetStorageMemory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Base64
import java.util.UUID
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class ProviderTest {
    private lateinit var issuerConfig: IssuerConfig
    private lateinit var provider: Provider

    @BeforeEach
    @Suppress("LongMethod")
    fun setUp() {
        issuerConfig = IssuerConfig(
            "http://localhost",
            listOf("read", "write", "offline_access"),
            listOf(ResponseType.CODE, ResponseType.ID_TOKEN),
            listOf(GrantType.AUTHORIZATION_CODE, GrantType.CLIENT_CREDENTIALS),
            listOf(PKCECodeChallengeMethod.S256),
            true,
            10.minutes.inWholeSeconds,
            5.minutes.inWholeSeconds,
            5.minutes.inWholeSeconds,
            30.days.inWholeSeconds,
            5.minutes.inWholeSeconds,
        )

        val issuerConfigStorage = IssuerConfigStorageMemory(
            mapOf(
                "http://localhost" to issuerConfig,
            ),
        )

        val jwkConfigStorageMemory = JWKConfigStorageMemory(
            mapOf(
                "http://localhost" to mapOf(
                    ("foo-" + UUID.randomUUID().toString()).let {
                        it to JWKConfig(
                            true,
                            JWSAlgorithm.ES256,
                            ECKeyGenerator(Curve.P_256)
                                .keyUse(KeyUse.SIGNATURE)
                                .keyID(it)
                                .generate(),
                        )
                    },
                    ("bar-" + UUID.randomUUID().toString()).let {
                        it to JWKConfig(
                            true,
                            JWSAlgorithm.ES256,
                            ECKeyGenerator(Curve.P_256)
                                .keyUse(KeyUse.SIGNATURE)
                                .keyID(it)
                                .generate(),
                        )
                    },
                ),
            ),
        )

        val clientConfigStorage = ClientConfigStorageMemory(
            mapOf(
                "http://localhost" to mapOf(
                    "foo" to ClientConfig(
                        "foo",
                        "foo client",
                        "secret",
                        ClientType.CONFIDENTIAL,
                        listOf("read", "write", "offline_access"),
                        listOf(GrantType.AUTHORIZATION_CODE, GrantType.CLIENT_CREDENTIALS),
                        listOf("http://localhost/cb"),
                        null,
                        null,
                        null,
                        null,
                        null,
                    ),
                ),
            ),
        )

        val userClaimSetStorageMemory = UserClaimSetStorageMemory(
            mapOf(
                "http://localhost" to mapOf(),
            ),
        )

        val clientAuthenticationManager = ClientAuthenticationManager(
            listOf(
                ClientSecretPostAuthenticator(clientConfigStorage),
                ClientSecretBasicAuthenticator(clientConfigStorage),
            ),
        )

        val config = Config(
            issuerConfigStorage,
            jwkConfigStorageMemory,
            clientConfigStorage,
            AuthorizationRequestDataStorageMemory(),
            AuthorizationCodeStorageMemory(),
            AccessTokenStorageMemory(),
            RefreshTokenStorageMemory(),
            userClaimSetStorageMemory,
            clientAuthenticationManager,
            DefaultSecurityTokenGenerator(),
            Endpoint(),
        )

        provider = Provider(config)
    }

    @Test
    @Suppress("LongMethod")
    fun test() {
//        val codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(
//            MessageDigest.getInstance("SHA-256").digest("12345".toByteArray()),
//        )

        val clientCredentials = Base64.getUrlEncoder().withoutPadding().encodeToString("foo:secret".toByteArray())

        val authorizationRequestPreProcessResult = Ok(Unit).andThen {
            val request = HttpRequest(
                method = HttpMethod.GET,
                headers = mapOf(),
                formParameters = mapOf(),
                queryParameters = mapOf(
                    AuthorizationRequestParameter.CLIENT_ID to listOf("foo"),
                    AuthorizationRequestParameter.RESPONSE_TYPE to listOf(ResponseType.CODE.value),
                    AuthorizationRequestParameter.REDIRECT_URI to listOf("http://localhost/cb"),
                    AuthorizationRequestParameter.CODE_CHALLENGE to listOf("12345"),
                    AuthorizationRequestParameter.SCOPE to listOf("read write offline_access"),
                ),
            )

            provider.handleAuthorizationRequestPreProcessHandler(issuerConfig.issuer, request)
                .onSuccess { println(it) }
                .onFailure { System.err.println(it) }
        }

        val authorizationRequestPostProcessResult =
            authorizationRequestPreProcessResult.andThen { authorizationRequestData ->
                provider.handleAuthorizationRequestPostProcessHandler(
                    issuerConfig.issuer,
                    "alice",
                    authorizationRequestData.key,
                    true,
                )
                    .onSuccess { println(it) }
                    .onFailure { System.err.println(it) }
            }

        val tokenRequestResult = authorizationRequestPostProcessResult.andThen { authorizationResponse ->
            val request = HttpRequest(
                method = HttpMethod.POST,
                headers = mapOf(
                    "Authorization" to listOf("Basic $clientCredentials"),
                ),
                formParameters = mapOf(
                    TokenRequestParameter.GRANT_TYPE to listOf(GrantType.AUTHORIZATION_CODE.value),
                    TokenRequestParameter.CODE to listOf("${authorizationResponse.code?.code}"),
                    TokenRequestParameter.REDIRECT_URI to listOf("http://localhost/cb"),
                    TokenRequestParameter.CODE_VERIFIER to listOf("12345"),
                ),
                queryParameters = mapOf(),
            )

            provider.handleTokenRequest(issuerConfig.issuer, request)
                .onSuccess { println(it) }
                .onFailure { System.err.println(it) }
        }

        tokenRequestResult.andThen { tokenResponse ->
            val request = HttpRequest(
                method = HttpMethod.POST,
                headers = mapOf(
                    "Authorization" to listOf("Basic $clientCredentials"),
                ),
                formParameters = mapOf(
                    IntrospectionRequestParameter.TOKEN to listOf(tokenResponse.accessToken),
                ),
                queryParameters = mapOf(),
            )

            provider.handleIntrospectionRequest(issuerConfig.issuer, request)
                .onSuccess { println(it) }
                .onFailure { System.err.println(it) }
        }

        tokenRequestResult.andThen { tokenResponse ->
            val request = HttpRequest(
                method = HttpMethod.POST,
                headers = mapOf(
                    "Authorization" to listOf("Basic $clientCredentials"),
                ),
                formParameters = mapOf(
                    IntrospectionRequestParameter.TOKEN to listOf(tokenResponse.refreshToken ?: ""),
                ),
                queryParameters = mapOf(),
            )

            provider.handleRevocationRequest(issuerConfig.issuer, request)
                .onSuccess { println(it) }
                .onFailure { System.err.println(it) }
        }

        tokenRequestResult.andThen { tokenResponse ->
            val request = HttpRequest(
                method = HttpMethod.POST,
                headers = mapOf(
                    "Authorization" to listOf("Basic $clientCredentials"),
                ),
                formParameters = mapOf(
                    TokenRequestParameter.GRANT_TYPE to listOf(GrantType.REFRESH_TOKEN.value),
                    TokenRequestParameter.REFRESH_TOKEN to listOf(tokenResponse.refreshToken ?: ""),
                ),
                queryParameters = mapOf(),
            )

            provider.handleTokenRequest(issuerConfig.issuer, request)
                .onSuccess { println(it) }
                .onFailure { System.err.println(it) }
        }

        tokenRequestResult.andThen { tokenResponse ->
            val request = HttpRequest(
                method = HttpMethod.POST,
                headers = mapOf(
                    "Authorization" to listOf("Basic $clientCredentials"),
                ),
                formParameters = mapOf(
                    IntrospectionRequestParameter.TOKEN to listOf(tokenResponse.accessToken),
                ),
                queryParameters = mapOf(),
            )

            provider.handleIntrospectionRequest(issuerConfig.issuer, request)
                .onSuccess { println(it) }
                .onFailure { System.err.println(it) }
        }
    }

    @Test
    fun clientCredentialGrantTest() {
        val httpRequest = HttpRequest(
            method = HttpMethod.POST,
            headers = mapOf(),
            formParameters = mapOf(
                TokenRequestParameter.CLIENT_ID to listOf("foo"),
                TokenRequestParameter.CLIENT_SECRET to listOf("secret"),
                TokenRequestParameter.GRANT_TYPE to listOf(GrantType.CLIENT_CREDENTIALS.value),
                TokenRequestParameter.SCOPE to listOf("read write"),
            ),
            queryParameters = mapOf(),
        )

        provider.handleTokenRequest(issuerConfig.issuer, httpRequest)
            .mapBoth(
                { println(it) },
                { System.err.println(it) },
            )

        val basicAuthenticationHttpRequest = HttpRequest(
            method = HttpMethod.POST,
            headers = mapOf(
                "Authorization" to listOf("Basic " + Base64.getUrlEncoder().encodeToString("foo:secret".toByteArray())),
            ),
            formParameters = mapOf(
                TokenRequestParameter.GRANT_TYPE to listOf(GrantType.CLIENT_CREDENTIALS.value),
                TokenRequestParameter.SCOPE to listOf("read write"),
            ),
            queryParameters = mapOf(),
        )

        provider.handleTokenRequest(issuerConfig.issuer, basicAuthenticationHttpRequest)
            .mapBoth(
                { println(it) },
                { System.err.println(it) },
            )
    }
}
