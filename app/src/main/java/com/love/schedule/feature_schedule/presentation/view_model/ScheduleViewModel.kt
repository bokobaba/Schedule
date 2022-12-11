package com.love.schedule.feature_schedule.presentation.view_model

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_schedule.domain.model.EmployeeSchedules
import com.love.schedule.feature_schedule.domain.model.Schedule
import com.love.schedule.feature_schedule.domain.model.Shift
import com.love.schedule.feature_schedule.domain.repository.IScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.single
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val _repository: IScheduleRepository,
    private val _state: IScheduleState
) : ViewModel() {
    private var _schedulesLoading = mutableStateOf(false)
    private var _shiftsLoading = mutableStateOf(false)
    private var _getSchedulesJob: Job? = null
    private var _getShiftsJob: Job? = null
    private lateinit var _employees: List<Employee>

    val state: IScheduleViewState
        get() = _state
    val schedulesLoading: MutableState<Boolean>
        get() = _schedulesLoading
    val shiftsLoading: MutableState<Boolean>
        get() = _shiftsLoading

    init {
        Log.d("ScheduleViewModel", "init")
        getSchedules()
        getShifts()
    }

    fun onEvent(event: ScheduleEvent) {
        when (event) {
            ScheduleEvent.AddShift -> {
                _state.onAddShift(onSaveData = { insertShift(it) })
            }
            ScheduleEvent.CancelSelect -> _state.onCancelSelect()
            ScheduleEvent.DismissPopup -> _state.dismissPopup()
            ScheduleEvent.EditShift -> {
                _state.onEditShift(
                    onSaveData = { insertShift(it) },
                    onDelete = { deleteShift(it) }
                )
            }
            is ScheduleEvent.ScheduleClick -> {
                _state.onScheduleClick(day = event.day, index = event.index,
                    onSaveData = { insertSchedule(event.day, event.index, it) },
                    onDelete = { deleteSchedule(it) }
                )
            }
            is ScheduleEvent.ShiftClick -> _state.onShiftClick(event.shift)
            is ScheduleEvent.SelectWeek -> {
                _state.selectWeek(event.year, event.month, event.day)
                getSchedulesForWeek()
            }
        }
    }

    private fun getSchedules() {
        _schedulesLoading.value = true
        _getSchedulesJob?.cancel()
        _getSchedulesJob = _repository.getEmployees().onEach { employees ->
            _employees = employees
            getSchedulesForWeek()
        }.launchIn(viewModelScope)
    }

    private fun getSchedulesForWeek() {
        _schedulesLoading.value = true
        viewModelScope.launch {
            Log.d("ScheduleViewModel", "getschedules")
            val schedules: List<Schedule> =
                _repository.getSchedules(_state.week.value, _state.year.value)
            _state.setSchedules(_employees.map { e ->
                EmployeeSchedules(
                    employee = e,
                    schedules = schedules.filter { it.employeeId == e.employeeId }
                )
            })
            _schedulesLoading.value = false
        }
    }

    private fun getShifts() {
        _shiftsLoading.value = true
        _getShiftsJob?.cancel()
        _getShiftsJob = _repository.getShifts()
            .onEach { shifts ->
                _state.setShifts(shifts)
                _shiftsLoading.value = false
            }
            .launchIn(viewModelScope)
    }

    private fun insertSchedule(day: Int, index: Int, schedule: Schedule) {
        Log.d("ScheduleViewModel", "insertShift: ${Gson().toJson(schedule)}")
        viewModelScope.launch {
            val id = _repository.insertSchedule(schedule)[0]
            if (schedule.id == null) {
                _state.addSchedule(
                    day = day,
                    index = index,
                    schedule = schedule.copy(id = id)
                )
            }
        }
    }

    private fun insertShift(shift: Shift) {
        Log.d("ScheduleViewModel", "insertShift")
        viewModelScope.launch {
            val id = _repository.insertShift(shift)
            if (shift.id == null) {
                _state.addShift(shift.copy(id = id))
            }
        }
    }

    private fun deleteSchedule(id: Int) {
        viewModelScope.launch {
            _repository.deleteSchedule(id)
        }
    }

    private fun deleteShift(shift: Shift) {
        viewModelScope.launch {
            _repository.deleteShift(shift)
            _state.deleteShift(shift)
        }
    }
}

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    object SaveNote : UiEvent()
}