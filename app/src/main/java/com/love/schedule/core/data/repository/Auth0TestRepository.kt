package com.love.schedule.core.data.repository

import android.util.Log
import com.google.gson.Gson
import com.love.schedule.BuildConfig
import com.love.schedule.core.data.remote.Auth0Test
import com.love.schedule.core.data.remote.responses.Auth0TestTokenRequest
import com.love.schedule.core.data.remote.responses.Auth0TestTokenResponse
import com.love.schedule.core.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class Auth0TestRepository @Inject constructor(
    private val api: Auth0Test
) {
    suspend fun getTestToken(): Resource<Auth0TestTokenResponse> {
        Log.d(
            "getTestToken", Gson().toJson(
                Auth0TestTokenRequest(
                    client_id = BuildConfig.AUTH0_TEST_CLIENTID,
                    client_secret = BuildConfig.AUTH0_TEST_CLIENT_SECRET,
                    audience = BuildConfig.AUTH0_TEST_AUDIENCE
                )
            )
        )
        val response = try {
            api.getTestToken(
                Auth0TestTokenRequest(
                    client_id = BuildConfig.AUTH0_TEST_CLIENTID,
                    client_secret = BuildConfig.AUTH0_TEST_CLIENT_SECRET,
                    audience = BuildConfig.AUTH0_TEST_AUDIENCE
                )
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return Resource.Error("An error occurred")
        }
        return Resource.Success(response)
    }
}