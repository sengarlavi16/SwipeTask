package com.community.swipetask.api

import android.content.Context
import android.util.Log
import com.community.swipetask.utils.NetworkCheck
import okhttp3.Interceptor
import okhttp3.Headers
import okhttp3.Response
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.util.concurrent.TimeUnit
import okhttp3.Protocol
import okio.Buffer
import okio.BufferedSource

class CustomInterceptor(private val context: Context, private val lang: String, private val version: String) :
    Interceptor {

    private val logger: Logger = DefaultLogger()
    private var headers: Headers? = null
    private var level: Level = Level.NONE

    private companion object {
        val UTF8: Charset = Charset.forName("UTF-8")
    }

    init {
        setLevel(Level.BODY)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val level = this.level
        val original = chain.request()

        val requestBuilder = original.newBuilder()
            .addHeader("os", "android")
            .addHeader("version", version)
            .addHeader("language", lang)

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        val logBody = level == Level.BODY
        val logHeaders = logBody || level == Level.HEADERS

        val requestBody = request.body
        val hasRequestBody = requestBody != null

        val connection = chain.connection()
        val protocol = connection?.protocol() ?: Protocol.HTTP_1_1

        var requestStartMessage =
            "--> ${request.method} ${request.url} $protocol"
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (${requestBody!!.contentLength()}-byte body)"
        }
        logger.log(requestStartMessage)

        if (logHeaders) {
            if (hasRequestBody) {
                if (requestBody!!.contentType() != null) {
                    logger.log("Content-Type: ${requestBody.contentType()}")
                }
                if (requestBody.contentLength() != -1L) {
                    logger.log("Content-Length: ${requestBody.contentLength()}")
                }
            }

            val headers = request.headers
            for (i in 0 until headers.size) {
                val name = headers.name(i)
                if (!"Content-Type".equals(name, ignoreCase = true) &&
                    !"Content-Length".equals(name, ignoreCase = true)
                ) {
                    logger.log("${name}: ${headers.value(i)}")
                }
            }

            if (!logBody || !hasRequestBody) {
                logger.log("--> END ${request.method}")
            } else if (bodyEncoded(request.headers)) {
                logger.log("--> END ${request.method} (encoded body omitted)")
            } else {
                val buffer = Buffer()
                requestBody!!.writeTo(buffer)

                var charset: Charset = UTF8
                val contentType = requestBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)!!
                }

                logger.log("")
                logger.log(buffer.readString(charset))

                logger.log(
                    "--> END ${request.method} (${requestBody.contentLength()}-byte body)"
                )
            }
        }

        val startNs = System.nanoTime()
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body
        val contentLength = responseBody?.contentLength() ?: -1L
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        logger.log(
            "<-- ${response.code} ${response.message} " +
                    "${response.request.url} ($tookMs ms${if (!logHeaders) ", $bodySize body" else ""})"
        )

        if (logHeaders) {
            val headers = response.headers
            for (i in 0 until headers.size) {
                logger.log("${headers.name(i)}: ${headers.value(i)}")
            }

            if (!logBody) {
                logger.log("<-- END HTTP")
            } else if (bodyEncoded(response.headers)) {
                logger.log("<-- END HTTP (encoded body omitted)")
            } else {
                val source: BufferedSource = responseBody!!.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source.buffer()

                var charset: Charset = UTF8
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8)!!
                    } catch (e: UnsupportedCharsetException) {
                        logger.log("")
                        logger.log("Couldn't decode the response body; charset is likely malformed.")
                        logger.log("<-- END HTTP")

                        return response
                    }
                }

                if (contentLength != 0L) {
                    logger.log("")
                    logger.log(buffer.clone().readString(charset))
                }

                logger.log("<-- END HTTP (${buffer.size}-byte body)")
            }
        }

        return if (NetworkCheck.isConnected(context)) {
            val maxAge = 60 // Read from cache for 1 minute
            response.newBuilder()
                .header("Cache-Control", "public, max-age=$maxAge")
                .build()
        } else {
            val maxStale = 60 * 60 * 24 * 28 // Tolerate 4-weeks stale
            response.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .build()
        }
    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }

    fun setHeaders(headers: Headers) {
        this.headers = headers
    }

    fun setLevel(level: Level) {
        this.level = level
    }

    private interface Logger {
        fun log(message: String)
    }

    private class DefaultLogger : Logger {
        override fun log(message: String) {
            Log.e("Response", message)
        }
    }

    enum class Level {
        NONE, BASIC, HEADERS, BODY
    }
}
