package myoidcprovider.ktor.sample.idp.webauthn

import java.util.UUID

data class WebAuthnCredential(
    val userId: String,
    val handle: UUID,
    val credentialId: ByteArray,
    val attestedCredentialData: ByteArray,
    val attestationStatement: ByteArray,
    val signCount: Long,
    val transports: String,
) {
    fun updateCounter(count: Long): WebAuthnCredential {
        return this.copy(signCount = count)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WebAuthnCredential

        if (userId != other.userId) return false
        if (handle != other.handle) return false
        if (!credentialId.contentEquals(other.credentialId)) return false
        if (!attestedCredentialData.contentEquals(other.attestedCredentialData)) return false
        if (!attestationStatement.contentEquals(other.attestationStatement)) return false
        if (signCount != other.signCount) return false
        if (transports != other.transports) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + handle.hashCode()
        result = 31 * result + credentialId.contentHashCode()
        result = 31 * result + attestedCredentialData.contentHashCode()
        result = 31 * result + attestationStatement.contentHashCode()
        result = 31 * result + signCount.hashCode()
        result = 31 * result + transports.hashCode()
        return result
    }
}
