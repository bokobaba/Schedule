package com.love.schedule.navigation

import android.util.Log

//const val EMPLOYEEINFO_ARGUMENT = "employeeId"
const val EMPLOYEEINFO_ARGUMENT = "employeeId"
const val ROOT_GRAPH_ROUTE = "root"
const val EMPLOYEES_GRAPH_ROUTE = "employees_root"
const val EMPLOYEES_ROUTE = "EmployeeList"
const val EMPLOYEE_INFO_ROUTE = "EmployeeInfo?employeeId={$EMPLOYEEINFO_ARGUMENT}"

const val SCHEDULE_GRAPH_ROUTE = "schedule_root"
const val SCHEDULE_ROUTE = "Schedule"

const val RULES_GRAPH_ROUTE = "rules_root"
const val RULES_ROUTE = "Rules"


sealed class Screen(val route: String) {
    object EmployeeList: Screen(route = EMPLOYEES_ROUTE)
//    object EmployeeInfo: Screen(route = "$EMPLOYEE_INFO_ROUTE/{$EMPLOYEEINFO_ARGUMENT}") {
    object EmployeeInfo: Screen(route = EMPLOYEE_INFO_ROUTE) {
        fun passEmployee(employeeId: String): String {
            Log.d("EmployeeScreen", "employeeId = $employeeId")
            Log.d("EmployeeScreen", this.route.replace(oldValue = "{$EMPLOYEEINFO_ARGUMENT}", newValue = employeeId))
            return this.route.replace(oldValue = "{$EMPLOYEEINFO_ARGUMENT}", newValue = employeeId)
//            return "${this.route}$EMPLOYEEINFO_ARGUMENT{$employeeId}"
        }
    }

    object Schedule: Screen(route = SCHEDULE_ROUTE)

    object Rules: Screen(route = RULES_ROUTE)
}
