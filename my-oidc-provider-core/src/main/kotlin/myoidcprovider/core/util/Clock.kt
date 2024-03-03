package myoidcprovider.core.util

import java.time.Instant

/**
 * 現在時刻を提供するクロック。
 */
interface Clock {
    /**
     * 現在時刻を取得する。
     *
     * @return 現在時刻
     */
    fun getEpochSecond(): Long

    /**
     * システム時刻を提供するクロック。
     */
    companion object SystemClock : Clock {
        override fun getEpochSecond(): Long = Instant.now().epochSecond
    }
}
