package com.bali.shared.data

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import okio.Buffer

class ApolloClientFactory {
    fun create(serverUrl: String, tokenProvider: (() -> String?)? = null): ApolloClient {
        val builder = ApolloClient.Builder()
            .serverUrl(serverUrl)
            
        // Auth Interceptor
        builder.addHttpInterceptor(AuthInterceptor(tokenProvider))

        // Logging Interceptor (Development/Debug)
        builder.addHttpInterceptor(LoggingInterceptor())

        // Cache Configuration
        val memoryCacheFactory = MemoryCacheFactory(maxSizeBytes = 10 * 1024 * 1024)
        val sqlCacheFactory = SqlNormalizedCacheFactory(name = "bali_apollo.db")
        
        builder.normalizedCache(
            memoryCacheFactory.chain(sqlCacheFactory)
        )

        return builder.build()
    }
}

private class AuthInterceptor(private val tokenProvider: (() -> String?)?) : HttpInterceptor {
    override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {
        val token = tokenProvider?.invoke()
        val newRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .build()
        } else {
            request.newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
        }
        return chain.proceed(newRequest)
    }
}

private class LoggingInterceptor : HttpInterceptor {
    override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {
        println("🚀 APOLLO_REQ: ${request.method} ${request.url}")
        
        // Log Headers
        request.headers.forEach { println("   Header: ${it.name}: ${it.value}") }
        
        // Log Body (safely)
        request.body?.let { body ->
            val buffer = Buffer()
            body.writeTo(buffer)
            println("   Request Body: ${buffer.readUtf8()}")
        }
        
        return try {
            val response = chain.proceed(request)
            println("✅ APOLLO_RES: ${response.statusCode} for ${request.url}")
            
            // Log Response Body (safely using peek)
            response.body?.peek()?.let { source ->
                println("   Response Body: ${source.readUtf8()}")
            }

            response
        } catch (e: Exception) {
            println("❌ APOLLO_ERR: ${e.message} for ${request.url}")
            throw e
        }
    }
}
