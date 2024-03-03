package myoidcprovider.core.authentication

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.crypto.Ed25519Signer
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.OctetKeyPair
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import myoidcprovider.core.authorization.AccessToken
import myoidcprovider.core.client.ClientConfig
import myoidcprovider.core.issuer.IssuerConfig
import myoidcprovider.core.request.authorization.AuthenticationRequest
import myoidcprovider.core.storage.JWKConfigStorage
import myoidcprovider.core.storage.UserClaimSetStorage
import myoidcprovider.core.util.Clock
import java.security.MessageDigest
import java.time.Instant
import java.util.*

/**
 * ID トークンジェネレーター。
 */
class IdTokenGenerator(
    private val jwkConfigStorage: JWKConfigStorage,
    private val userClaimSetStorage: UserClaimSetStorage,
    private val clock: Clock,
) {
    /**
     * ID トークンを作成する。
     *
     * @param issuer Issuer
     * @param client Client
     * @param authenticationRequest 認証リクエスト
     * @param accessToken アクセストークン
     * @param authorizationCode 認可コード
     * @param subject 識別子
     * @return ID トークン
     */
    fun generate(
        issuer: IssuerConfig,
        client: ClientConfig,
        authenticationRequest: AuthenticationRequest,
        accessToken: AccessToken?,
        authorizationCode: String?,
        subject: String,
    ): String {
        val jwkConfig = jwkConfigStorage.findByPrimary(issuer.issuer)
            ?: TODO("プライマリ JWK が存在しない場合はエラーを返す")

        val messageDigest = when (jwkConfig.algorithm) {
            JWSAlgorithm.HS256, JWSAlgorithm.RS256, JWSAlgorithm.ES256, JWSAlgorithm.ES256K, JWSAlgorithm.PS256 ->
                MessageDigest.getInstance("SHA-256")

            JWSAlgorithm.HS384, JWSAlgorithm.RS384, JWSAlgorithm.ES384, JWSAlgorithm.PS384 ->
                MessageDigest.getInstance("SHA-384")

            JWSAlgorithm.HS512, JWSAlgorithm.RS512, JWSAlgorithm.ES512, JWSAlgorithm.PS512 ->
                MessageDigest.getInstance("SHA-512")

            else -> TODO("Unknown Hash Algorithm. [${jwkConfig.algorithm.name}]")
        }

        val issueTime = Instant.ofEpochSecond(clock.getEpochSecond())
        val idTokenDuration = client.idTokenDuration ?: issuer.idTokenDuration
        val expirationTime = issueTime.plusSeconds(idTokenDuration)
        val claimsSetBuilder = JWTClaimsSet.Builder()
            .issuer(issuer.issuer)
            .subject(subject)
            .audience(client.id)
            .issueTime(Date.from(issueTime))
            .expirationTime(Date.from(expirationTime))

        authenticationRequest.nonce?.let {
            claimsSetBuilder.claim("nonce", it)
        }

        accessToken?.let {
            val hashValue = messageDigest.digest(it.token.toByteArray(Charsets.US_ASCII))
            val atHash = Base64.getUrlEncoder().withoutPadding().encodeToString(hashValue)
            claimsSetBuilder.claim("at_hash", atHash)
        }

        authorizationCode?.let {
            val hashValue = messageDigest.digest(it.toByteArray(Charsets.US_ASCII))
            val cHash = Base64.getUrlEncoder().withoutPadding().encodeToString(hashValue)
            claimsSetBuilder.claim("c_hash", cHash)
        }

        val userClaimSet = userClaimSetStorage.findBySubject(issuer.issuer, subject)

        userClaimSet?.standardClaim?.name?.let { claimsSetBuilder.claim("name", it) }
        userClaimSet?.standardClaim?.givenName?.let { claimsSetBuilder.claim("given_name", it) }
        userClaimSet?.standardClaim?.familyName?.let { claimsSetBuilder.claim("family_name", it) }
        userClaimSet?.standardClaim?.middleName?.let { claimsSetBuilder.claim("middle_name", it) }
        userClaimSet?.standardClaim?.nickname?.let { claimsSetBuilder.claim("nickname", it) }
        userClaimSet?.standardClaim?.preferredUsername?.let { claimsSetBuilder.claim("preferred_username", it) }
        userClaimSet?.standardClaim?.profile?.let { claimsSetBuilder.claim("profile", it) }
        userClaimSet?.standardClaim?.picture?.let { claimsSetBuilder.claim("picture", it) }
        userClaimSet?.standardClaim?.website?.let { claimsSetBuilder.claim("website", it) }
        userClaimSet?.standardClaim?.email?.let { claimsSetBuilder.claim("email", it) }
        userClaimSet?.standardClaim?.emailVerified?.let { claimsSetBuilder.claim("email_verified", it) }
        userClaimSet?.standardClaim?.gender?.let { claimsSetBuilder.claim("gender", it) }
        userClaimSet?.standardClaim?.birthdate?.let { claimsSetBuilder.claim("birthdate", it) }
        userClaimSet?.standardClaim?.zoneInfo?.let { claimsSetBuilder.claim("zoneinfo", it) }
        userClaimSet?.standardClaim?.locale?.let { claimsSetBuilder.claim("locale", it) }
        userClaimSet?.standardClaim?.phoneNumber?.let { claimsSetBuilder.claim("phone_number", it) }
        userClaimSet?.standardClaim?.phoneNumberVerified?.let {
            claimsSetBuilder.claim(
                "phone_number_verified",
                it,
            )
        }
        userClaimSet?.standardClaim?.address?.let { claimsSetBuilder.claim("address", it) }
        userClaimSet?.standardClaim?.updatedAt?.let { claimsSetBuilder.claim("updated_at", it) }
        userClaimSet?.customClaim?.forEach { (name, value) ->
            claimsSetBuilder.claim(name, value)
        }

        val jwsHeader = JWSHeader.Builder(jwkConfig.algorithm).keyID(jwkConfig.jwk.keyID).build()
        val signedJWT = SignedJWT(jwsHeader, claimsSetBuilder.build())

        when (jwkConfig.jwk) {
            is ECKey -> signedJWT.sign(ECDSASigner(jwkConfig.jwk))
            is RSAKey -> signedJWT.sign(RSASSASigner(jwkConfig.jwk))
            is OctetKeyPair -> signedJWT.sign(Ed25519Signer(jwkConfig.jwk))
            else -> TODO("unknown jwk type. [${jwkConfig.jwk.keyID}]")
        }

        return signedJWT.serialize()
    }
}
