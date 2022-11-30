package com.love.schedule.nav_graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.love.schedule.feature_schedule.presentation.view_model.ScheduleViewModel
import com.love.schedule.feature_schedule.presentation.ScheduleScreen

fun NavGraphBuilder.scheduleNavGraph(navController: NavHostController, vm: ScheduleViewModel) {
    navigation(
        startDestination = Screen.Schedule.route,
        route = SCHEDULE_GRAPH_ROUTE
    ) {
        composable(
            route = Screen.Schedule.route
        ) {
            ScheduleScreen(navController = navController, vm)
        }
    }
}