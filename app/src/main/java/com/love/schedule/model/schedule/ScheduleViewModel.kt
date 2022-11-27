package com.love.schedule.model.schedule

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.love.schedule.Data
import com.love.schedule.Day
import com.love.schedule.data.domain.repository.IRepository
import com.love.schedule.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

interface IScheduleViewModel {
    val schedules: List<Schedule>
    val shifts: List<Shift>
    val selectedShift: Shift?
    val actions: IScheduleActions

    fun addSchedule(schedule: Schedule)
    fun editSchedule(id: UUID, day: Int, shift: TimeRange)
    fun fetchSchedules()

    fun addShift(shift: Shift)
    fun editShift(shift: Shift)
    fun selectShift(shift: Shift)
    fun deselectShift()
    fun fetchShifts()
}

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val _repository: IRepository,
    private val _scheduleActions: IScheduleActions
) : ViewModel(), IScheduleViewModel {
    private val _schedules = mutableStateMapOf<UUID, Schedule>()
    private val _shifts = mutableStateMapOf<UUID, Shift>()
    private var _schedulesLoaded: Boolean = false
    private var _shiftsLoaded: Boolean = false
    private var _selectedShift by mutableStateOf<Shift?>(null)

    override val actions: IScheduleActions
        get() = _scheduleActions

    override val schedules: List<Schedule>
        get() = _schedules.values.toList()

    override val shifts: List<Shift>
        get() = _shifts.values.toList()

    override val selectedShift: Shift?
        get() = _selectedShift

    override fun selectShift(shift: Shift) {
        _selectedShift = shift
    }

    override fun deselectShift() {
        _selectedShift = null
    }

    fun getScheduleById(id: UUID): Schedule? {
        return _schedules[id]
    }

    fun getShiftById(id: UUID): Shift? {
        return _shifts[id]
    }

    override fun addSchedule(schedule: Schedule) {
        _schedules[schedule.id] = schedule
    }

    override fun addShift(shift: Shift) {
        Log.d("addShift", "adding shift: ${Gson().toJson(shift)}")
        _shifts[shift.id] = shift
    }

    override fun editShift(shift: Shift) {
        Log.d("editShift", "shift: ${Gson().toJson(shift)}")

        _shifts[shift.id] = _shifts[shift.id]?.copy(
            name = shift.name,
            start = shift.start,
            end = shift.end
        ) ?: return
    }

//    fun saveSchedules(id: UUID, updateShifts: List<TimeRange>) {
//        _schedules[id]?.copy(shifts = updateShifts)
//    }

    override fun editSchedule(id: UUID, day: Int, shift: TimeRange) {
        Log.d("editSchedule", "day: $day, shift: $shift")

        val shifts: MutableList<TimeRange> = _schedules[id]?.shifts?.toMutableList() ?: return
        shifts[day] = shift

        Log.d("editSchedule", "shifts: ${Gson().toJson(shifts)}")

        val edit: Schedule? = _schedules[id]?.copy(shifts = shifts)
        Log.d("editSchedule", "edit: ${Gson().toJson(edit)}")

        if (edit != null) {
            _schedules[id] = edit
            Log.d("editSchedule", "final: ${Gson().toJson(_schedules[id])}")
        }
    }

    override fun fetchSchedules() {
        if (_schedulesLoaded) return
        viewModelScope.launch {
            _schedules.clear()
            try {
                Data.schedules.forEach { schedule ->
                    _schedules[schedule.id] = schedule
                }
                _schedulesLoaded = true
            } catch (e: Exception) {
                Log.d("ScheduleViewModel", e.message!!)
            }
        }
    }

    override fun fetchShifts() {
        if (_shiftsLoaded) return
        viewModelScope.launch {
            _shifts.clear()
            try {
//                Data.shifts.forEach { shift ->
                for (shift in (1..10).map { Shift(UUID.randomUUID(), "Test", "0:00", "0:00") }) {
                    _shifts[shift.id] = shift
                }
                _shiftsLoaded = true
            } catch (e: Exception) {
                Log.d("ScheduleViewModel", e.message!!)
            }
        }
    }

    companion object {
        val previewViewModel = object : IScheduleViewModel {
            override val schedules: List<Schedule>
                get() = Data.schedules
            override val shifts: List<Shift>
                get() = (1..10).map { Shift(UUID.randomUUID(), "Test", "0:00", "0:00") }
//                get() = Data.shifts
            override val selectedShift: Shift?
                get() = null
            override val actions: IScheduleActions
                get() = ScheduleActions()

            override fun addSchedule(schedule: Schedule) {}
            override fun editSchedule(id: UUID, day: Int, shift: TimeRange) {}
            override fun fetchSchedules() {}
            override fun addShift(shift: Shift) {}
            override fun editShift(shift: Shift) {}
            override fun selectShift(shift: Shift) {}
            override fun deselectShift() {}
            override fun fetchShifts() {}
        }
    }
}