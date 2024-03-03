package myoidcprovider.ktor.sample.idp.config

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.gen.ECKeyGenerator
import kotlinx.serialization.Serializable
import myoidcprovider.core.Provider
import myoidcprovider.core.authentication.AddressClaim
import myoidcprovider.core.authentication.StandardClaim
import myoidcprovider.core.authentication.UserClaimSet
import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.client.ClientType
import myoidcprovider.core.client.authentication.ClientAuthenticationManager
import myoidcprovider.core.client.authentication.ClientSecretBasicAuthenticator
import myoidcprovider.core.client.authentication.ClientSecretPostAuthenticator
import myoidcprovider.core.config.Config
import myoidcprovider.core.config.Endpoint
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.jwk.JWKConfig
import myoidcprovider.core.metadata.GrantType
import myoidcprovider.core.metadata.PKCECodeChallengeMethod
import myoidcprovider.core.metadata.ResponseType
import myoidcprovider.core.storage.AccessTokenStorageMemory
import myoidcprovider.core.storage.AuthorizationCodeStorageMemory
import myoidcprovider.core.storage.AuthorizationRequestDataStorageMemory
import myoidcprovider.core.storage.ClientConfigStorageMemory
import myoidcprovider.core.storage.IssuerConfigStorageMemory
import myoidcprovider.core.storage.JWKConfigStorageMemory
import myoidcprovider.core.storage.RefreshTokenStorageMemory
import myoidcprovider.core.storage.UserClaimSetStorageMemory
import myoidcprovider.core.util.Clock
import java.util.*

/**
 * OpenID Provider の設定
 */
@Serializable
data class ProviderConfig(
    val issuerConfigs: List<IssuerConfigJson>,
) {
    fun createProvider(): Provider {
        val issuerConfigStorage = IssuerConfigStorageMemory(toIssuerConfigs().associateBy { it.issuer })
        val clientConfigStorage = ClientConfigStorageMemory(toClientConfigs())
        val jwkConfigStorage = JWKConfigStorageMemory(
            issuerConfigs.associate {
                it.issuer to mapOf(
                    UUID.randomUUID().toString() to JWKConfig(
                        true,
                        JWSAlgorithm.ES256,
                        ECKeyGenerator(Curve.P_256)
                            .keyUse(KeyUse.SIGNATURE)
                            .keyID(UUID.randomUUID().toString())
                            .generate(),
                    ),
                    UUID.randomUUID().toString() to JWKConfig(
                        false,
                        JWSAlgorithm.ES256,
                        ECKeyGenerator(Curve.P_256)
                            .keyUse(KeyUse.SIGNATURE)
                            .keyID(UUID.randomUUID().toString())
                            .generate(),
                    ),
                )
            },
        )
        val userClaimSetStorage = UserClaimSetStorageMemory(toUserClaimsMap())
        val config = Config(
            issuerConfigStorage = issuerConfigStorage,
            jwkConfigStorage = jwkConfigStorage,
            clientConfigStorage = clientConfigStorage,
            authorizationCodeStorage = AuthorizationCodeStorageMemory(),
            authorizationRequestDataStorage = AuthorizationRequestDataStorageMemory(),
            accessTokenStorage = AccessTokenStorageMemory(),
            refreshTokenStorage = RefreshTokenStorageMemory(),
            userClaimSetStorage = userClaimSetStorage,
            clientAuthenticationManager = ClientAuthenticationManager(
                listOf(
                    ClientSecretBasicAuthenticator(clientConfigStorage),
                    ClientSecretPostAuthenticator(clientConfigStorage),
                ),
            ),
            endpoint = Endpoint(),
            clock = Clock.SystemClock,
        )

        return Provider(config)
    }

    private fun toIssuerConfigs() = issuerConfigs.map {
        IssuerConfig(
            issuer = it.issuer,
            scopes = it.scopes,
            supportedResponseTypes = it.supportedResponseTypes.map { responseType ->
                when (responseType) {
                    ResponseType.CODE.value -> ResponseType.CODE
                    ResponseType.TOKEN.value -> ResponseType.TOKEN
                    ResponseType.ID_TOKEN.value -> ResponseType.ID_TOKEN
                    else -> error("Unknown value. ResponseType[$responseType]")
                }
            },
            supportedGrantTypes = it.supportedGrantTypes.map { grantType ->
                when (grantType) {
                    GrantType.AUTHORIZATION_CODE.value -> GrantType.AUTHORIZATION_CODE
                    GrantType.IMPLICIT.value -> GrantType.IMPLICIT
                    GrantType.PASSWORD.value -> GrantType.PASSWORD
                    GrantType.CLIENT_CREDENTIALS.value -> GrantType.CLIENT_CREDENTIALS
                    GrantType.REFRESH_TOKEN.value -> GrantType.REFRESH_TOKEN
                    GrantType.JWT_BEARER.value -> GrantType.JWT_BEARER
                    GrantType.SAML2_BEARER.value -> GrantType.SAML2_BEARER
                    else -> error("Unknown value. GrantType[$grantType]")
                }
            },
            supportedCodeChallengeMethods = it.supportedCodeChallengeMethods.map { codeChallengeMethod ->
                when (codeChallengeMethod) {
                    PKCECodeChallengeMethod.PLAIN.value -> PKCECodeChallengeMethod.PLAIN
                    PKCECodeChallengeMethod.S256.value -> PKCECodeChallengeMethod.S256
                    else -> error("Unknown value. PKCECodeChallengeMethod[$codeChallengeMethod]")
                }
            },
            requiredPKCE = it.requiredPKCE,
            authorizationRequestDataDuration = it.authorizationRequestDataDuration,
            authorizationCodeDuration = it.authorizationCodeDuration,
            accessTokenDuration = it.accessTokenDuration,
            refreshTokenDuration = it.refreshTokenDuration,
            idTokenDuration = it.idTokenDuration,
        )
    }

    private fun toClientConfigs() = issuerConfigs.associate {
        it.issuer to it.clients.associateBy({ clientConfigJson -> clientConfigJson.id }, { clientConfigJson ->
            ClientConfig(
                id = clientConfigJson.id,
                name = clientConfigJson.name,
                secret = clientConfigJson.secret,
                type = when (clientConfigJson.type) {
                    ClientType.PUBLIC.value -> ClientType.PUBLIC
                    ClientType.CONFIDENTIAL.value -> ClientType.CONFIDENTIAL
                    else -> error("Unknown value. ClientType[${clientConfigJson.secret}]")
                },
                scopes = clientConfigJson.scopes,
                supportedGrantTypes = clientConfigJson.supportedGrantTypes.map { grantType ->
                    when (grantType) {
                        GrantType.AUTHORIZATION_CODE.value -> GrantType.AUTHORIZATION_CODE
                        GrantType.IMPLICIT.value -> GrantType.IMPLICIT
                        GrantType.PASSWORD.value -> GrantType.PASSWORD
                        GrantType.CLIENT_CREDENTIALS.value -> GrantType.CLIENT_CREDENTIALS
                        GrantType.REFRESH_TOKEN.value -> GrantType.REFRESH_TOKEN
                        GrantType.JWT_BEARER.value -> GrantType.JWT_BEARER
                        GrantType.SAML2_BEARER.value -> GrantType.SAML2_BEARER
                        else -> error("Unknown value. GrantType[$grantType]")
                    }
                },
                redirectUris = clientConfigJson.redirectUris,
                authorizationRequestDataDuration = clientConfigJson.authorizationRequestDataDuration,
                authorizationCodeDuration = clientConfigJson.authorizationCodeDuration,
                accessTokenDuration = clientConfigJson.accessTokenDuration,
                refreshTokenDuration = clientConfigJson.refreshTokenDuration,
                idTokenDuration = clientConfigJson.idTokenDuration,
            )
        })
    }

    private fun toUserClaimsMap(): Map<String, Map<String, UserClaimSet>> =
        issuerConfigs.associate {
            it.issuer to it.users.associate { user ->
                user.subject to UserClaimSet(
                    standardClaim = StandardClaim(
                        name = user.standardClaimSet?.name,
                        givenName = user.standardClaimSet?.givenName,
                        familyName = user.standardClaimSet?.familyName,
                        middleName = user.standardClaimSet?.middleName,
                        nickname = user.standardClaimSet?.nickname,
                        preferredUsername = user.standardClaimSet?.preferredUsername,
                        profile = user.standardClaimSet?.profile,
                        picture = user.standardClaimSet?.picture,
                        website = user.standardClaimSet?.website,
                        email = user.standardClaimSet?.email,
                        emailVerified = user.standardClaimSet?.emailVerified,
                        gender = user.standardClaimSet?.gender,
                        birthdate = user.standardClaimSet?.birthdate,
                        zoneInfo = user.standardClaimSet?.zoneInfo,
                        locale = user.standardClaimSet?.locale,
                        phoneNumber = user.standardClaimSet?.phoneNumber,
                        phoneNumberVerified = user.standardClaimSet?.phoneNumberVerified,
                        address = user.standardClaimSet?.address?.let { address ->
                            AddressClaim(
                                formatted = address.formatted,
                                streetAddress = address.streetAddress,
                                locality = address.locality,
                                region = address.region,
                                postalCode = address.postalCode,
                                country = address.country,
                            )
                        },
                        updatedAt = user.standardClaimSet?.updatedAt,
                    ),
                    customClaim = user.customClaims ?: emptyMap(),
                )
            }
        }
}

@Serializable
data class IssuerConfigJson(
    val issuer: String,
    val scopes: List<String>,
    val supportedResponseTypes: List<String>,
    val supportedGrantTypes: List<String>,
    val supportedCodeChallengeMethods: List<String>,
    val requiredPKCE: Boolean,
    val authorizationRequestDataDuration: Long,
    val authorizationCodeDuration: Long,
    val accessTokenDuration: Long,
    val refreshTokenDuration: Long,
    val idTokenDuration: Long,
    val clients: List<ClientConfigJson>,
    val users: List<UserJson>,
)

@Serializable
data class ClientConfigJson(
    val id: String,
    val name: String,
    val secret: String,
    val type: String,
    val scopes: List<String>,
    val supportedGrantTypes: List<String>,
    val redirectUris: List<String>,
    val authorizationRequestDataDuration: Long?,
    val authorizationCodeDuration: Long?,
    val accessTokenDuration: Long?,
    val refreshTokenDuration: Long?,
    val idTokenDuration: Long?,
)

@Serializable
data class UserJson(
    val username: String,
    val subject: String,
    val password: String,
    val standardClaimSet: StandardClaimSetJson?,
    val customClaims: Map<String, String>?,
)

@Serializable
data class StandardClaimSetJson(
    val name: String? = null,
    val givenName: String? = null,
    val familyName: String? = null,
    val middleName: String? = null,
    val nickname: String? = null,
    val preferredUsername: String? = null,
    val profile: String? = null,
    val picture: String? = null,
    val website: String? = null,
    val email: String? = null,
    val emailVerified: Boolean? = null,
    val gender: String? = null,
    val birthdate: String? = null,
    val zoneInfo: String? = null,
    val locale: String? = null,
    val phoneNumber: String? = null,
    val phoneNumberVerified: Boolean? = null,
    val address: AddressClaimJson? = null,
    val updatedAt: Long? = null,
)

@Serializable
data class AddressClaimJson(
    val formatted: String?,
    val streetAddress: String?,
    val locality: String?,
    val region: String?,
    val postalCode: String?,
    val country: String?,
)
