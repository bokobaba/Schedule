package com.love.schedule.model.employee

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavController
import com.love.schedule.PreviewData
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.navigation.Screen
import javax.inject.Inject

interface IEmployeesViewState {
    val employees: SnapshotStateList<Employee>
    val employeeToDelete: MutableState<Employee?>
}

interface IEmployeesState: IEmployeesViewState {
    fun setEmployees(employees: List<Employee>)
    fun onSelectEmployee(employee: Employee, navController: NavController)
    fun onLongPressEmployee(employee: Employee)
    fun cancelDelete()
}

class EmployeesState @Inject constructor() : IEmployeesState {
    private val _employees = mutableStateListOf<Employee>()
    private val _employeeToDelete = mutableStateOf<Employee?>(null)

    override val employees: SnapshotStateList<Employee>
        get() = _employees

    override val employeeToDelete: MutableState<Employee?>
        get() = _employeeToDelete

    override fun setEmployees(employees: List<Employee>) {
        _employees.clear()
        _employees.addAll(employees)
    }

    override fun onSelectEmployee(employee: Employee, navController: NavController) {
        navController.navigate(route = Screen.EmployeeInfo.passEmployee("${employee.id}"))
    }

    override fun onLongPressEmployee(employee: Employee) {
        _employeeToDelete.value = employee
    }

    override fun cancelDelete() {
        _employeeToDelete.value = null
    }

    companion object {
        val previewState = object : IEmployeesViewState {
            override val employees: SnapshotStateList<Employee>
                get() = PreviewData.employees.toMutableStateList()
            override val employeeToDelete: MutableState<Employee?>
                get() = mutableStateOf(null)
        }
    }

}