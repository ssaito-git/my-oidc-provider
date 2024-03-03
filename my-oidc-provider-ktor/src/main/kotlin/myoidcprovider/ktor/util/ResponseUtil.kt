package myoidcprovider.ktor.util

import io.ktor.http.URLBuilder
import io.ktor.http.Url
import myoidcprovider.core.metadata.ResponseMode
import myoidcprovider.core.metadata.ResponseType
import myoidcprovider.core.request.authorization.AuthenticationErrorResponse
import myoidcprovider.core.request.authorization.AuthorizationErrorResponse
import myoidcprovider.core.request.authorization.AuthorizationResponse
import myoidcprovider.core.request.authorization.AuthorizationResponseError

/**
 * 認可リクエストのエラーレスポンスからリダイレクト URL を作成する、
 *
 * @return リダイレクト URL
 */
fun AuthorizationErrorResponse.toRedirectUrl(): Url {
    return buildErrorRedirectUrl(redirectUri, error.value, errorDescription, errorUri, state)
}

/**
 * 認証リクエストのエラーレスポンスからリダイレクト URL を作成する。
 *
 * @return リダイレクト URL
 */
fun AuthenticationErrorResponse.toRedirectUrl(): Url {
    return buildErrorRedirectUrl(redirectUri, error.value, errorDescription, errorUri, state)
}

/**
 * 認可レスポンスのエラーレスポンスからリダイレクト URL を作成する。
 *
 * @return リダイレクト URL
 */
fun AuthorizationResponseError.ErrorResponse.toRedirectUrl(): Url {
    return buildErrorRedirectUrl(redirectUri, error.value, errorDescription, errorUri, state)
}

/**
 * エラーリダイレクト URL を構築する。
 *
 * @param redirectUri リダイレクト URI
 * @param error エラー
 * @param errorDescription エラーの追加情報
 * @param errorUri エラーに関する web ページの URI
 * @param state State
 * @return リダイレクト URL
 */
private fun buildErrorRedirectUrl(
    redirectUri: String,
    error: String,
    errorDescription: String?,
    errorUri: String?,
    state: String?,
): Url {
    return URLBuilder(redirectUri).apply {
        parameters.append("error", error)
        errorDescription?.let {
            parameters.append("error_description", it)
        }
        errorUri?.let {
            parameters.append("error_uri", it)
        }
        state?.let {
            parameters.append("state", it)
        }
    }.build()
}

/**
 * 認可レスポンスからリダイレクト URL を作成する。
 *
 * @return リダイレクト URL
 */
fun AuthorizationResponse.toRedirectUrl(): Url {
    val responseParameters = mutableMapOf<String, String>()

    token?.let {
        responseParameters["access_token"] = it.accessToken
        responseParameters["token_type"] = it.tokenType.value
        it.expiresIn?.let { expiresIn ->
            responseParameters["expires_in"] = expiresIn.toString()
        }
        it.scope?.let { scope ->
            responseParameters["scope"] = scope
        }
    }

    code?.let {
        responseParameters["code"] = it.code
    }

    idToken?.let {
        responseParameters["id_token"] = it.idToken
    }

    state?.let {
        responseParameters["state"] = it
    }

    return URLBuilder(redirectUri).apply {
        if (responseMode != ResponseMode.FRAGMENT &&
            !responseType.contains(ResponseType.TOKEN) &&
            !responseType.contains(ResponseType.ID_TOKEN)
        ) {
            responseParameters.forEach { parameters.append(it.key, it.value) }
        } else {
            fragment = responseParameters.map { "${it.key}=${it.value}" }.joinToString("&")
        }
    }.build()
}

/**
 * 認可レスポンスからフォームポスト HTML を作成する。
 *
 * @return フォームポスト HTML
 */
fun AuthorizationResponse.toFormPostHtml(): String {
    val responseParameters = mutableListOf<String>()

    token?.let {
        responseParameters.add("""<input type="hidden" name="access_token" value="${it.accessToken}" />""")
        responseParameters.add("""<input type="hidden" name="token_type" value="${it.tokenType.value}" />""")
        it.expiresIn?.let { expiresIn ->
            responseParameters.add("""<input type="hidden" name="expires_in" value="$expiresIn" />""")
        }
        it.scope?.let { scope ->
            responseParameters.add("""<input type="hidden" name="scope" value="$scope" />""")
        }
    }

    code?.let {
        responseParameters.add("""<input type="hidden" name="code" value="${it.code}" />""")
    }

    idToken?.let {
        responseParameters.add("""<input type="hidden" name="id_token" value="${it.idToken}" />""")
    }

    state?.let {
        responseParameters.add("""<input type="hidden" name="state" value="$it" />""")
    }

    return """
        <html>
          <head>
            <title>Submit This Form</title>
          </head>
          <body onload="javascript:document.forms[0].submit()">
            <form method="post" action="$redirectUri">
            ${responseParameters.joinToString("")}
            </form>
          </body>
        </html>
    """.trimIndent()
}
