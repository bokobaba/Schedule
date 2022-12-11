package com.love.schedule.feature_employees.presentation.employees

import com.love.schedule.feature_employees.domain.model.Employee

sealed class EmployeeListEvent {
    data class SelectEmployee(val employee: Employee): EmployeeListEvent()
    object AddEmployee: EmployeeListEvent()
    data class LongPressEmployee(val employee: Employee): EmployeeListEvent()
    object CancelDelete: EmployeeListEvent()
    object DeleteEmployee: EmployeeListEvent()
}