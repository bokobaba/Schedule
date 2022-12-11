package com.love.schedule.core.data.remote

import com.love.schedule.core.auth.IAuth
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val _auth: IAuth,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = _auth.accessToken
        val r: Request = chain.request()
            .newBuilder()
            .header("Accept", "application/json")
            .header("Authorization", "Bearer $token")
            .build();

        return chain.proceed(r)
    }
}