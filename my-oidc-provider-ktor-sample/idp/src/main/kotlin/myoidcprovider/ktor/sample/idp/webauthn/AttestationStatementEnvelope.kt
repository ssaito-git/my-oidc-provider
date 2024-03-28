package myoidcprovider.ktor.sample.idp.webauthn

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.webauthn4j.data.attestation.statement.AndroidKeyAttestationStatement
import com.webauthn4j.data.attestation.statement.AndroidSafetyNetAttestationStatement
import com.webauthn4j.data.attestation.statement.AppleAnonymousAttestationStatement
import com.webauthn4j.data.attestation.statement.AttestationStatement
import com.webauthn4j.data.attestation.statement.FIDOU2FAttestationStatement
import com.webauthn4j.data.attestation.statement.NoneAttestationStatement
import com.webauthn4j.data.attestation.statement.PackedAttestationStatement
import com.webauthn4j.data.attestation.statement.TPMAttestationStatement

class AttestationStatementEnvelope @JsonCreator constructor(
    @field:JsonProperty("attStmt")
    @field:JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "fmt",
    )
    @field:JsonSubTypes(
        JsonSubTypes.Type(value = NoneAttestationStatement::class, name = NoneAttestationStatement.FORMAT),
        JsonSubTypes.Type(value = AndroidKeyAttestationStatement::class, name = AndroidKeyAttestationStatement.FORMAT),
        JsonSubTypes.Type(
            value = AndroidSafetyNetAttestationStatement::class,
            name = AndroidSafetyNetAttestationStatement.FORMAT,
        ),
        JsonSubTypes.Type(
            value = AppleAnonymousAttestationStatement::class,
            name = AppleAnonymousAttestationStatement.FORMAT,
        ),
        JsonSubTypes.Type(value = FIDOU2FAttestationStatement::class, name = FIDOU2FAttestationStatement.FORMAT),
        JsonSubTypes.Type(value = TPMAttestationStatement::class, name = TPMAttestationStatement.FORMAT),
        JsonSubTypes.Type(value = PackedAttestationStatement::class, name = PackedAttestationStatement.FORMAT),
    )
    @JsonProperty(
        "attStmt",
    ) val attestationStatement: AttestationStatement,
) {

    @get:JsonProperty("fmt")
    val format: String
        get() = attestationStatement.format
}
