package com.love.schedule.feature_employees.presentation.employee_info.view_model

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.love.schedule.feature_employees.domain.model.*
import com.love.schedule.feature_employees.domain.repository.IEmployeeRepository
import com.love.schedule.nav_graph.EMPLOYEEINFO_ARGUMENT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.DateFormat
import javax.inject.Inject

@HiltViewModel
class EmployeeInfoViewModel @Inject constructor(
    private val _employeeRepository: IEmployeeRepository,
    private val _repository: IEmployeeRepository,
    private val _state: IEmployeeInfoState,
    private val _availabilityState: IAvailabilityState,
    private val _requestsState: IRequestsState,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private var _currentEmployee: Employee? = null
    private var _id: Int = -1
    private var _getAvailabilityJob: Job? = null
    private val _loading = mutableStateOf(false)

    val newEmployee: Boolean
        get() = _id == -1
    val state: IEmployeeInfoState
        get() = _state
    val availabilityState: IAvailabilityState
        get() = _availabilityState
    val requestsState: IRequestsState
        get() = _requestsState
    val loading: MutableState<Boolean>
        get() = _loading

    init {
        Log.d("EmployeeInfoViewModel", "init")

        _state.saveInfo = { saveInfo() }

        savedStateHandle.get<Int>(EMPLOYEEINFO_ARGUMENT)?.let { id ->
            Log.d("EmployeeInfoViewModel", "id: $id")
            if (id != -1) {
                _id = id
                viewModelScope.launch {
                    _loading.value = true
                    delay(2000)
                    getEmployeeInfo(id)?.also { employeeInfo ->
                        val employee: Employee = employeeInfo.employee
                        Log.d("EmployeeInfoViewModel", "employee: ${Gson().toJson(employee)}")
                        _currentEmployee = employee
                        _state.editEmployeeName(employee.name)
                        _state.editEmployeeId(employee.employeeId)

                        Log.d("EmployeeInfoViewModel", "getAvailability")
//                        getAvailability(employee.employeeId)
                        _availabilityState.setAvailability(employeeInfo.availability)
                        _loading.value = false
                    }
                }
            } else {
                _state.editEmployeeName("")
                _state.editEmployeeId("")
                _availabilityState.setAvailability(emptyList())
            }
        }
    }

    @Throws(InvalidEmployeeException::class)
    private fun saveInfo() {
        val name = _state.employeeName.value
        val employeeId = _state.employeeId.value
        if (name.isBlank()) {
            throw InvalidEmployeeException("the employee name cannot be empty")
        }
        if (employeeId.isBlank()) {
            throw InvalidEmployeeException("the employee id cannot be empty")
        }
        viewModelScope.launch {
            Log.d("EmployeeInfoViewModel", "inserting employees...")
            _employeeRepository.insertEmployee(Employee(
                id = if (_id != -1) _id else null,
                name = name,
                employeeId = employeeId
            ))

            var availability: List<Availability> = _availabilityState.availability.map {
                it.copy(
                    employeeId = _state.employeeId.value
                )
            }

            Log.d("EmployeeInfoViewModel", "availability: ${Gson().toJson(availability)}")

            _repository.insertAvailability(availability = availability.toTypedArray())
        }
    }

    private suspend fun getEmployeeInfo(id: Int): EmployeeInfo? {
        return _employeeRepository.getEmployeeInfo(id)
    }
}

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    object SaveNote : UiEvent()
}