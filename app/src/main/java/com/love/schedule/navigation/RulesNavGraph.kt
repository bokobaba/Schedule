package com.love.schedule.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.love.schedule.feature_rules.presentation.RulesScreen

fun NavGraphBuilder.rulesNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.Rules.route,
        route = RULES_GRAPH_ROUTE
    ) {
        composable(
            route = Screen.Rules.route
        ) {
            RulesScreen(navController = navController)
        }
    }
}