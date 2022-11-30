package com.love.schedule.model.employee

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavController
import com.love.schedule.PreviewData
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.nav_graph.Screen
import javax.inject.Inject

interface IEmployeesState {
    val employees: SnapshotStateList<Employee>
    var addEmployee: (employee: Employee) -> Unit
    var deleteEmployee: (employee: Employee) -> Unit
    var restoreEmployee: () -> Unit
    val employeeToDelete: MutableState<Employee?>

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

    override var addEmployee: (employee: Employee) -> Unit = {}
    override var deleteEmployee: (employee: Employee) -> Unit = {}
    override var restoreEmployee: () -> Unit = {}
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
        val previewActions = object : IEmployeesState {
            override val employees: SnapshotStateList<Employee>
                get() = PreviewData.employees.toMutableStateList()
            override var addEmployee: (employee: Employee) -> Unit = {}
            override var deleteEmployee: (employee: Employee) -> Unit = {}
            override var restoreEmployee: () -> Unit = {}
            override val employeeToDelete: MutableState<Employee?>
                get() = mutableStateOf(null)

            override fun setEmployees(employees: List<Employee>) {}
            override fun onSelectEmployee(employee: Employee, navController: NavController) {}
            override fun onLongPressEmployee(employee: Employee) {}
            override fun cancelDelete() {}

        }
    }

}