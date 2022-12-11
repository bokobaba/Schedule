package com.love.schedule.feature_account.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.love.schedule.feature_account.domain.User
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AccountScreen(navController: NavController, vm: AccountViewModel = hiltViewModel()) {
    val context = LocalContext.current
    AccountScreenContent(
        onLogin = vm::loginWithBrowser,
        onLogout = vm::logoutWithBrowser,
        onFetchData = vm::fetchData,
        user = vm.user,
        onExportData = vm::exportData,
    )

    LaunchedEffect(key1 = true) {
        vm.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.Login -> {
                    // Setup the WebAuthProvider, using the custom scheme and scope.
                    WebAuthProvider.login(vm.account)
                        .withScheme("demo")
                        .withScope("openid profile email")
                        // Launch the authentication passing the callback where the results will be received
                        .start(context, object : Callback<Credentials, AuthenticationException> {
                            // Called when there is an authentication failure
                            override fun onFailure(exception: AuthenticationException) {
                                // Something went wrong!
                            }

                            // Called when authentication completed successfully
                            override fun onSuccess(credentials: Credentials) {
                                // Get the access token from the credentials object.
                                // This can be used to call APIs
                                vm.setAccessToken(credentials.accessToken)
//                                vm.assignUserProfile()
                            }
                        })
                }
                is UiEvent.Logout -> {
                    WebAuthProvider.logout(vm.account)
                        .withScheme("demo")
                        .start(context, object: Callback<Void?, AuthenticationException> {
                            override fun onSuccess(payload: Void?) {
                                // The user has been logged out!
                                vm.logoutUser()
                            }

                            override fun onFailure(error: AuthenticationException) {
                                // Something went wrong!
                            }
                        })
                }
            }
        }
    }
}

@Composable
fun AccountScreenContent(
    user: User?,
    onLogin: () -> Unit,
    onLogout: () -> Unit,
    onFetchData: () -> Unit,
    onExportData: () -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Account") },
                navigationIcon = {}
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (user != null) {
                Text(
                    text = "Welcome",
                    style = MaterialTheme.typography.h3
                )
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.h4
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (user == null) {
                Button(
                    modifier = Modifier.width(100.dp),
                    onClick = onLogin
                ) {
                    Text("Log In")
                }
            } else {
                Button(
                    modifier = Modifier.width(100.dp),
                    onClick = onLogout
                ) {
                    Text("Log Out")
                }
            }

            Button(
                onClick = onFetchData,
            ) {
                Text("Import Data")
            }

            Button(
                onClick = onExportData,
            ) {
                Text("Export Data")
            }
        }
    }
}

@Composable
@Preview
fun AccountScreenPreview() {
    AccountScreenContent(
        onLogin = {},
        onLogout = {},
        onFetchData = {},
        onExportData = {},
        user = User(name = "John", email = "test@test.com")
    )
}