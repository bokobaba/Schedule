package com.love.schedule.feature_account.presentation

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.result.UserProfile
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.love.schedule.BuildConfig
import com.love.schedule.core.auth.IAuth
import com.love.schedule.core.data.remote.responses.GetEmployeeInfoDto
import com.love.schedule.core.data.repository.Auth0TestRepository
import com.love.schedule.core.data.repository.DataStoreRepository
import com.love.schedule.core.data.repository.IDataStoreRepository
import com.love.schedule.core.data.repository.ScheduleApiRepository
import com.love.schedule.core.util.Constants.ACCESS_TOKEN
import com.love.schedule.core.util.Resource
import com.love.schedule.feature_account.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val _apiRepository: ScheduleApiRepository,
    private val _auth: IAuth,
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<UiEvent>()


    val eventFlow: MutableSharedFlow<UiEvent>
        get() = _eventFlow

    val account: Auth0
        get() = _auth.account

    val user: User?
        get() = _auth.user.value

    val accessToken: String?
        get() = _auth.accessToken

    fun loginWithBrowser() {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.Login)
        }
    }

    fun logoutWithBrowser() {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.Logout)
        }
    }

    fun setAccessToken(token: String) {
        _auth.setAccessToken(token)
    }

    fun logoutUser() {
        _auth.logoutUser()
    }

    fun fetchData() {
        viewModelScope.launch {
            Log.d("AccountViewModel", "fetching data")
            _apiRepository.importData()
//            Log.d("AccountViewModel", "employees: ${Gson().toJson(resource.data)}")
        }
    }

    fun exportData() {
        viewModelScope.launch {
            Log.d("AccountViewModel", "exporting data")
            _apiRepository.exportData()
        }
    }
}

sealed class UiEvent {
    object Login : UiEvent()
    object Logout : UiEvent()
}