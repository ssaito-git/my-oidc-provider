package myoidcprovider.ktor.sample.idp.repository

import myoidcprovider.ktor.sample.idp.webauthn.WebAuthnCredential
import java.util.UUID

/**
 * WebAuthn クレデンシャルのリポジトリ
 */
class WebAuthnCredentialRepository {
    /**
     * WebAuthn クレデンシャルのリスト
     */
    private val webAuthnCredentials = mutableListOf<WebAuthnCredential>()

    /**
     * ハンドルとクレデンシャル ID が一致する WebAuthn クレデンシャルを取得する。
     *
     * @param handle ハンドル
     * @param credentialId クレデンシャル ID
     * @return ハンドルとクレデンシャル ID が一致する [WebAuthnCredential]。存在しない場合は null。
     */
    fun findByHandleAndCredentialId(handle: UUID, credentialId: ByteArray): WebAuthnCredential? {
        return webAuthnCredentials.firstOrNull { it.handle == handle && it.credentialId.contentEquals(credentialId) }?.copy()
    }

    /**
     * WebAuthn クレデンシャルを保存する。
     *
     * @param webAuthnCredential WebAuthn クレデンシャル
     */
    fun save(webAuthnCredential: WebAuthnCredential) {
        webAuthnCredentials.add(webAuthnCredential)
    }
}
