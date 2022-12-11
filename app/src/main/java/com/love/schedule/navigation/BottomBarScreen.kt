package com.love.schedule.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import com.love.schedule.R

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: Int
) {
    object Employees : BottomBarScreen(
        route = EMPLOYEES_ROUTE,
        icon = R.drawable.ic_employees,
        title = "Employees"
    )

    object Rules : BottomBarScreen(
        route = RULES_ROUTE,
        icon = R.drawable.ic_rules,
        title = "Rules"
    )

    object Schedule : BottomBarScreen(
        route = SCHEDULE_ROUTE,
        icon = R.drawable.ic_schedule,
        title = "Schedule"
    )

    object Account: BottomBarScreen(
        route = ACCOUNT_ROUTE,
        icon = R.drawable.ic_account,
        title = "Account"
    )
}


