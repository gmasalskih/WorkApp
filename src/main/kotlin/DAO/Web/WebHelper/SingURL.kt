package DAO.Web.WebHelper

import Util.Const.*
import okhttp3.HttpUrl
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object SingURL {
    fun signingUrl(url: HttpUrl): HttpUrl {
        val sortedUrl = sortedQueryParameter(url).build()



        val unsignedURL = sortedUrl.newBuilder()
                .addQueryParameter("appid", APP_ID)
                .addQueryParameter("secretkey", SECRET_KEY)
                .build()

        var sign = getSign(unsignedURL)

        return sortedUrl.newBuilder()
                .addQueryParameter("appid", APP_ID)
                .addQueryParameter("sign", sign)
                .build()
    }

    private fun sortedQueryParameter(url: HttpUrl): HttpUrl.Builder {
        val listQueryPair = url.queryParameterNames()
                .sorted()
                .distinct()
                .map {
                    var param = url.queryParameterValues(it)
                            .sorted()
                            .distinct()
                            .reduce { acc, s -> "$acc,$s" }
                    Pair<String, String>(it, param)
                }

        val urlBuilder = cleanUrlFromQueryParameter(url, listQueryPair)
        listQueryPair.forEach {
            urlBuilder.addQueryParameter(it.first, it.second)
        }
        return urlBuilder
    }

    private fun cleanUrlFromQueryParameter(url: HttpUrl, listQueryPair: List<Pair<String, String>>): HttpUrl.Builder {
        val cleanBuilder = url.newBuilder()
        listQueryPair.forEach {
            cleanBuilder.removeAllQueryParameters(it.first)
        }
        return cleanBuilder
    }

    private fun getSign(url: HttpUrl) = MessageDigest
            .getInstance("MD5")
            .digest(URLDecoder.decode(url.toString(), StandardCharsets.UTF_8.toString()).toByteArray())
            .map { "%02x".format(it).toUpperCase() }
            .reduce { s1, s2 -> s1 + s2 }
}