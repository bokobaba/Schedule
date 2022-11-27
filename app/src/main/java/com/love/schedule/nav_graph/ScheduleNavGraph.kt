package com.love.schedule.nav_graph

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.love.schedule.model.schedule.ScheduleViewModel
import com.love.schedule.screen.schedule.ScheduleScreen

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