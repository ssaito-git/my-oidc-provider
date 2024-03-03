package myoidcprovider.core.http

/**
 * HTTP リクエスト。
 */
data class HttpRequest(
    /**
     * メソッド。
     */
    val method: HttpMethod,
    /**
     * ヘッダー。
     */
    val headers: Map<String, List<String>>,
    /**
     * フォームパラメーター。
     */
    val formParameters: Map<String, List<String>>,
    /**
     * クエリパラメーター。
     */
    val queryParameters: Map<String, List<String>>,
)
