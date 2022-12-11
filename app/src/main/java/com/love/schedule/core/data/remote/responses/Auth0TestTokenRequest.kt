package com.love.schedule.core.data.remote.responses

data class Auth0TestTokenRequest(
    val client_id: String,
    val client_secret: String,
    val audience: String,
    val grant_type: String = "client_credentials"
)