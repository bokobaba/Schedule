package com.love.schedule.data.remote

import retrofit2.http.GET

interface TestApi {
    @GET("test")
    suspend fun doNetworkCall()
}