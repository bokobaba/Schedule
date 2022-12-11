package com.love.schedule.core.auth

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.result.UserProfile
import com.love.schedule.BuildConfig
import com.love.schedule.core.data.repository.Auth0TestRepository
import com.love.schedule.core.data.repository.IDataStoreRepository
import com.love.schedule.core.util.Constants.ACCESS_TOKEN
import com.love.schedule.feature_account.domain.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

interface IAuth {
    val user: MutableState<User?>
    val accessToken: String?
    val account: Auth0

    fun setAccessToken(token: String)
    fun logoutUser()
}

class Auth @Inject constructor(
    private val dataStoreRepository: IDataStoreRepository,
    private val _apiTestToken: Auth0TestRepository,
    ) : IAuth {
    private var _account: Auth0 = Auth0(
        BuildConfig.AUTH0_CLIENTID,
        BuildConfig.AUTH0_DOMAIN
    )
    private val _user: MutableState<User?> = mutableStateOf(null)
    private var _accessToken: String? = null

    override val account: Auth0
        get() = _account
    override val user: MutableState<User?>
        get() = _user
    override val accessToken: String?
        get() = _accessToken

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _accessToken = dataStoreRepository.getString(ACCESS_TOKEN)
            if (_accessToken == null) {
                Log.d("Auth", "getting new test token")
                val resource = _apiTestToken.getTestToken()
                if (resource.data != null) {
                    setAccessToken(resource.data.access_token)
                    Log.d("Auth", "response: ${resource.data}")
                }
            }
            assignUserProfile()
        }
    }

    override fun setAccessToken(token: String) {
        runBlocking {
            dataStoreRepository.putString(ACCESS_TOKEN, token)
            _accessToken = token
            assignUserProfile()
        }
    }

    override fun logoutUser() {
        runBlocking {
            _user.value = null
            dataStoreRepository.clearPreferences(ACCESS_TOKEN)
        }

    }

    private fun assignUserProfile() {
        Log.d("Auth", "assignUserProfile token = $_accessToken")
        _accessToken?.let { token ->
            var client = AuthenticationAPIClient(_account)

            // With the access token, call `userInfo` and get the profile from Auth0.
            client.userInfo(token)
                .start(object : Callback<UserProfile, AuthenticationException> {
                    override fun onFailure(exception: AuthenticationException) {
                        // Something went wrong!
                        Log.e("Auth", "unable to get user profile")
                    }

                    override fun onSuccess(profile: UserProfile) {
                        Log.d(
                            "Auth", "name = ${profile.name}, " +
                                    "email = ${profile.email}"
                        )
                        if (profile.email != null && profile.name != null) {
                            _user.value = User(email = profile.email!!, name = profile.name!!)
                        }
                    }
                })
        }
    }
}