package com.danielesergio.zextrastest.client

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

/***
 * Interceptor which return the cached value when there is an error
 */
class CacheInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // Try network first
        val response = chain.proceed(request)
        return  if(response.code <200 || response.code > 299){
            getCachedResponse(request, chain)
        } else  {
            cacheResponse(response)
        }
    }

    private fun getCachedResponse(request: Request, chain: Interceptor.Chain):Response {
        val cacheControl = CacheControl.Builder()
            .maxStale(Int.MAX_VALUE, TimeUnit.DAYS)
            .build()

        val offlineRequest = request.newBuilder()
            .header(CACHE_HEADER, cacheControl.toString()) // 1 day stale
            .build()

        return chain.proceed(offlineRequest)
    }

    private fun cacheResponse(response: Response): Response {
        val cacheControl = CacheControl.Builder()
            .maxAge(Int.MAX_VALUE, TimeUnit.DAYS)
            .build()
        return response.newBuilder()
            .header(CACHE_HEADER, cacheControl.toString()) // 1 minute
            .build()
    }

    companion object{
        private const val CACHE_HEADER = "Cache-Control"
    }
}