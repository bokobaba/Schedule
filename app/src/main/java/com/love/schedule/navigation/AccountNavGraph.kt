package com.love.schedule.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.love.schedule.feature_account.presentation.AccountScreen

fun NavGraphBuilder.accountNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.Account.route,
        route = ACCOUNT_GRAPH_ROUTE
    ) {
        composable(
            route = Screen.Account.route
        ) {
            AccountScreen(navController = navController)
        }
    }
}