package com.love.schedule.nav_graph

import androidx.navigation.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.love.schedule.feature_employees.presentation.employee_info.EmployeeInfoScreen
import com.love.schedule.feature_employees.presentation.employees.EmployeeListScreen

fun NavGraphBuilder.employeesNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.EmployeeList.route,
        route = EMPLOYEES_GRAPH_ROUTE
    ) {
        composable(
            route = Screen.EmployeeList.route
        ) {
            EmployeeListScreen(navController = navController)
        }
        composable(
            route = Screen.EmployeeInfo.route,
            arguments = listOf(navArgument(EMPLOYEEINFO_ARGUMENT) {
                type = NavType.IntType
                defaultValue = -1
            })
        ) {
            it.arguments?.getInt(EMPLOYEEINFO_ARGUMENT)?.let {
                EmployeeInfoScreen(navController)
            }
        }
    }
}