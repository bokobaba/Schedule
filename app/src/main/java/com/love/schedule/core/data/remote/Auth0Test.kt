package com.love.schedule.core.data.remote

import com.love.schedule.core.data.remote.responses.Auth0TestTokenRequest
import com.love.schedule.core.data.remote.responses.Auth0TestTokenResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface Auth0Test {

    @Headers("Content-Type: application/json")
    @POST("oauth/token")
    suspend fun getTestToken(@Body body: Auth0TestTokenRequest): Auth0TestTokenResponse
}