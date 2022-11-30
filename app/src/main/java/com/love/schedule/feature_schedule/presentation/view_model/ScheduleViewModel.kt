package com.love.schedule.feature_schedule.presentation.view_model

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
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

    val state: IScheduleViewState
        get() = _state
    val schedulesLoading: MutableState<Boolean>
        get() = _schedulesLoading
    val shiftsLoading: MutableState<Boolean>
        get() = _shiftsLoading

//    fun saveSchedules(id: UUID, updateShifts: List<TimeRange>) {
//        _schedules[id]?.copy(shifts = updateShifts)
//    }

    init {
        Log.d("ScheduleViewModel", "init")
        getSchedules()
        getShifts()

        _state.saveSchedule = { saveSchedule(it) }
        _state.insertSchedule = { day, index, schedule ->
            insertSchedule(day, index, schedule)
        }
        _state.deleteSchedule = { id ->
            deleteSchedule(id)
        }
        _state.insertShift = { insertShift(it) }
        _state.deleteShift = { deleteShift(it) }
    }

    private fun saveSchedule(schedule: Schedule) {
        Log.d("ScheduleViewModel", "saving schedule...")
    }

    private fun getSchedules() {
        _schedulesLoading.value = true
        _getSchedulesJob?.cancel()
        _getSchedulesJob = _repository.getEmployees().onEach { employees ->
            viewModelScope.launch {
                delay(1000)
                Log.d("ScheduleViewModel", "getschedules")
                val schedules: List<Schedule> = _repository.getSchedules(_state.week, _state.year)
                _state.setSchedules(employees.map { e ->
                    EmployeeSchedules(
                        employee = e,
                        schedules = schedules.filter { it.employeeId == e.employeeId }
                    )
                })
                _schedulesLoading.value = false
            }
        }.launchIn(viewModelScope)
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
            _state.addSchedule(
                day = day,
                index = index,
                schedule = schedule.copy(id = id)
            )
        }
    }

    private fun insertShift(shift: Shift) {
        Log.d("ScheduleViewModel", "insertShift")
        viewModelScope.launch {
            val id = _repository.insertShift(shift)
            _state.addShift(shift.copy(id = id))
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