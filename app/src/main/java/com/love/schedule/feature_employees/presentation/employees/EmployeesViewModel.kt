package com.love.schedule.feature_employees.presentation.employees

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_employees.domain.model.InvalidEmployeeException
import com.love.schedule.feature_employees.domain.repository.IEmployeeRepository
import com.love.schedule.model.employee.IEmployeesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeesViewModel @Inject constructor(
    private val _repository: IEmployeeRepository,
    private val _employeeState: IEmployeesState,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private var getEmployeesJob: Job? = null
    private var recentlyDeletedEmployee: Employee? = null
    private var _loading = mutableStateOf(false)

    val state: IEmployeesState
        get() = _employeeState

    val loading: MutableState<Boolean>
        get() = _loading

    init {
        getEmployees()
        state.addEmployee = { addEmployee(it) }
        state.deleteEmployee = { deleteEmployee(it) }
        state.restoreEmployee = { restoreEmployee() }
    }

    @Throws(InvalidEmployeeException::class)
    private fun addEmployee(employee: Employee) {
        if (employee.name.isBlank()) {
            throw InvalidEmployeeException("the employee name cannot be empty")
        }
        if (employee.employeeId.isBlank()) {
            throw InvalidEmployeeException("the employee id cannot be empty")
        }
        viewModelScope.launch {
            _repository.insertEmployee(employee)
        }
    }

    private fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            _repository.deleteEmployeeInfo(employee)
            recentlyDeletedEmployee = employee
            _employeeState.cancelDelete()
        }
    }

    private fun restoreEmployee() {
        viewModelScope.launch {
            _repository.insertEmployee(recentlyDeletedEmployee ?: return@launch)
            recentlyDeletedEmployee = null
        }
    }

    private fun getEmployees() {
        getEmployeesJob?.cancel()
        getEmployeesJob = _repository.getEmployees()
            .onEach { employees ->
                _loading.value = true
                Log.d("EmployeesViewModel", "employees: ${Gson().toJson(employees)}")
                _employeeState.setEmployees(employees)
                _loading.value = false
            }
            .launchIn(viewModelScope)
    }
}