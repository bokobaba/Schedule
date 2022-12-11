package com.love.schedule.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.love.schedule.feature_schedule.presentation.view_model.ScheduleViewModel

@Composable
fun BottomNavGraph(navController: NavHostController) {
    val scheduleVm = hiltViewModel<ScheduleViewModel>()
    NavHost(
        navController = navController,
        startDestination = SCHEDULE_GRAPH_ROUTE,
        route = ROOT_GRAPH_ROUTE
    ) {
        scheduleNavGraph(navController = navController, scheduleVm)
        employeesNavGraph(navController = navController)
        rulesNavGraph(navController = navController)
        accountNavGraph(navController = navController)
    }
}