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
import com.love.schedule.core.util.Days
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
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
    private val _eventFlow = MutableSharedFlow<UiEvent>()

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
    val eventFlow: MutableSharedFlow<UiEvent>
        get() = _eventFlow

    init {
        Log.d("EmployeeInfoViewModel", "init")

        _state.saveInfo = { saveInfo() }
        _state.navigateUp = { navigateUp() }

        savedStateHandle.get<Int>(EMPLOYEEINFO_ARGUMENT)?.let { id ->
            Log.d("EmployeeInfoViewModel", "id: $id")
            if (id != -1) {
                _id = id
                viewModelScope.launch {
                    _loading.value = true
                    getEmployeeInfo(id)?.also { employeeInfo ->
                        val employee: Employee = employeeInfo.employee
                        Log.d("EmployeeInfoViewModel", "employee: ${Gson().toJson(employee)}")
                        _currentEmployee = employee
                        _state.editEmployeeName(employee.name)
                        _state.editEmployeeId(employee.employeeId)

                        Log.d("EmployeeInfoViewModel", "getAvailability")
//                        getAvailability(employee.employeeId)
                        _availabilityState.setAvailability(employeeInfo.availability)

                        Log.d("EmployeeInfoViewModel", "getRequests")
                        _requestsState.setRequests(employeeInfo.requests)

                        _loading.value = false

                    }
                }
            } else {
                _state.editEmployeeName("")
                _state.editEmployeeId("")
                _availabilityState.setAvailability(emptyList())
                _requestsState.setRequests(emptyList())
            }
        }
    }

    @Throws(InvalidEmployeeException::class)
    private fun saveInfo() {
        val name = _state.employeeName.value
        val employeeId = _state.employeeId.value
        viewModelScope.launch {
            try {
                saveEmployee(name, employeeId)
                saveAvailability()
                saveRequests()
                _eventFlow.emit(UiEvent.SaveNote)
            } catch(e: InvalidEmployeeException) {
                Log.e("EmployeeInfoViewModel", e.message ?: "Couldn't save employee")
                _eventFlow.emit(UiEvent.ShowSnackbar(
                    message = e.message ?: "Couldn't save employee"
                ))
                return@launch
            } catch(e: InvalidRequestException) {
                Log.e("EmployeeInfoViewModel", e.message ?: "Couldn't save employee")
                _eventFlow.emit(UiEvent.ShowSnackbar(
                    message = e.message ?: "Couldn't save employee"
                ))
                return@launch
            } catch(e: InvalidAvailabilityException) {
                Log.e("EmployeeInfoViewModel", e.message ?: "Couldn't save employee")
                _eventFlow.emit(UiEvent.ShowSnackbar(
                    message = e.message ?: "Couldn't save employee"
                ))
                return@launch
            } catch (e: Exception) {
                Log.e("EmployeeInfoViewModel", e.message ?: "Couldn't save employee")
                _eventFlow.emit(UiEvent.ShowSnackbar(
                    message = "Couldn't save employee"
                ))
                return@launch
            }
        }
    }

    private suspend fun saveEmployee(name: String, employeeId: String) {
        if (name.isBlank()) {
            throw InvalidEmployeeException("the employee name cannot be empty")
        }
        if (employeeId.isBlank()) {
            throw InvalidEmployeeException("the employee id cannot be empty")
        }

        Log.d("EmployeeInfoViewModel", "inserting employees...")
        _employeeRepository.insertEmployee(Employee(
            id = if (_id != -1) _id else null,
            name = name,
            employeeId = employeeId
        ))
    }

    private suspend fun saveAvailability() {
        val availability: List<Availability> = _availabilityState.availability.map {
            if (it.day > Days.values().size || it.day < 0) {
                throw InvalidAvailabilityException("Invalid day for Availability found")
            }
            if (it.enabled && it.start.isBlank()) {
                throw InvalidAvailabilityException("Availability for ${Days.get(it.day)} has no start time")
            }
            if (it.enabled && it.end.isBlank()) {
                throw InvalidAvailabilityException("Availability for ${Days.get(it.day)} has no end time")
            }

            it.copy(
                employeeId = _state.employeeId.value
            )
        }
        Log.d("EmployeeInfoViewModel", "Inserting availability")
        _repository.insertAvailability(availability = availability.toTypedArray())
    }

    private suspend fun saveRequests() {
        val requests: List<EmployeeRequest> = _requestsState.requests.map {
            if (it.description.isBlank()) {
                throw InvalidRequestException("Description for Request cannot be empty")
            }
            if (it.start.isBlank()) {
                throw InvalidRequestException("Request:${it.description} must have start date")
            }
            if (it.end.isBlank()) {
                throw InvalidRequestException("Request:${it.description} must have end date")
            }

            it.copy(
                employeeId = _state.employeeId.value
            )
        }
        Log.d("EmployeeInfoViewModel", "Inserting requests")
        _repository.insertRequest(request = requests.toTypedArray())
    }

    private suspend fun getEmployeeInfo(id: Int): EmployeeInfo? {
        return _employeeRepository.getEmployeeInfo(id)
    }

    private fun navigateUp() {
        viewModelScope.launch {
            eventFlow.emit(UiEvent.Back)
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
        object Back: UiEvent()
    }
}