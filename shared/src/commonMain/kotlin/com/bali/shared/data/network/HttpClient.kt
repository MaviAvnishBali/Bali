package com.bali.shared.data.network

import com.bali.shared.data.local.SessionManager
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun createHttpClient(sessionManager: SessionManager) = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL
    }

    install(DefaultRequest) {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }
}.also { client ->
    // Add interceptor for dynamic tokens
    client.plugin(HttpSend).intercept { request ->
        sessionManager.getToken()?.let { token ->
            request.header(HttpHeaders.Authorization, "Bearer $token")
        }
        execute(request)
    }
}
