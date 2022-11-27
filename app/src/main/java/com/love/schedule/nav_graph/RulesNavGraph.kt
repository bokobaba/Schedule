package com.love.schedule.nav_graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.love.schedule.screen.rules.RulesScreen

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