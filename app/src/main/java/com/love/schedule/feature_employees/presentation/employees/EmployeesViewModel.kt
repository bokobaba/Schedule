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
import com.love.schedule.model.employee.IEmployeesViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeesViewModel @Inject constructor(
    private val _repository: IEmployeeRepository,
    private val _state: IEmployeesState,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private var getEmployeesJob: Job? = null
    private var recentlyDeletedEmployee: Employee? = null
    private var _loading = mutableStateOf(false)
    private val _eventFlow = MutableSharedFlow<UiEvent>()

    val state: IEmployeesViewState
        get() = _state

    val loading: MutableState<Boolean>
        get() = _loading

    val eventFlow: MutableSharedFlow<UiEvent>
        get() = _eventFlow

    init {
        getEmployees()
//        _state.addEmployee = { addEmployee(it) }
//        _state.deleteEmployee = { deleteEmployee(it) }
//        _state.restoreEmployee = { restoreEmployee() }
    }

    fun onEvent(event: EmployeeListEvent) {
        when (event) {
            is EmployeeListEvent.DeleteEmployee -> {
                val toDelete: Employee? = _state.employeeToDelete.value
                if (toDelete != null)
                    deleteEmployee(toDelete)
                _state.cancelDelete()

            }
            is EmployeeListEvent.CancelDelete -> _state.cancelDelete()
            is EmployeeListEvent.LongPressEmployee -> _state.onLongPressEmployee(event.employee)
            is EmployeeListEvent.SelectEmployee -> viewModelScope.launch {
                _eventFlow.emit(UiEvent.SelectEmployee(event.employee))
            }
            is EmployeeListEvent.AddEmployee -> viewModelScope.launch {
                _eventFlow.emit(UiEvent.AddEmployee)
            }

        }
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
            _state.cancelDelete()
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
                _state.setEmployees(employees)
                _loading.value = false
            }
            .launchIn(viewModelScope)
    }

    sealed class UiEvent {
        data class SelectEmployee(val employee: Employee) : UiEvent()
        object AddEmployee: UiEvent()
    }
}