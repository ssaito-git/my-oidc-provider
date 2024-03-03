package myoidcprovider.core.util

import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.Base64

/**
 * トークンジェネレーター。
 */
object TokenGenerator {
    private val secureRandom = SecureRandom()

    /**
     * トークンを生成する。
     *
     * @param size トークンのサイズ（byte）
     * @return Base64 (URL safe) エンコードされたトークン。
     */
    fun generate(size: Int): String {
        return ByteBuffer.allocate(size).let {
            secureRandom.nextBytes(it.array())
            Base64.getUrlEncoder().withoutPadding().encodeToString(it.array())
        }
    }
}
