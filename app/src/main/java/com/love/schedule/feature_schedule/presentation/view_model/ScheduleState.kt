package com.love.schedule.feature_schedule.presentation.view_model

import android.util.Log
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.Gson
import com.love.schedule.PreviewData
import com.love.schedule.core.component.ShiftPopupData
import com.love.schedule.core.util.Days
import com.love.schedule.feature_schedule.domain.model.EmployeeSchedules
import com.love.schedule.feature_schedule.domain.model.Schedule
import com.love.schedule.feature_schedule.domain.model.Shift
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
interface IScheduleViewState {
    val shifts: SnapshotStateList<Shift>
    val bottomSheetScaffoldState: BottomSheetScaffoldState
    val popupData: MutableState<ShiftPopupData?>
    val selectedShift: MutableState<Shift?>

    fun dismissPopup()
    fun onScheduleClick(day: Int, index: Int)
    fun onShiftClick(shift: Shift)
    fun onCancelSelect()
    fun onAddShift()
    fun onEditShift()
    fun setBottomSheetScaffoldState(bsState: BottomSheetScaffoldState)
    fun scheduleForDay(day: Int): SnapshotStateList<EmployeeShift>
}

interface IScheduleState : IScheduleViewState {
    var saveSchedule: (schedule: Schedule) -> Unit
    var insertShift: (Shift) -> Unit
    var insertSchedule: (Int, Int, Schedule) -> Unit
    var deleteSchedule: (Int) -> Unit
    var deleteShift: (Shift) -> Unit
    val week: Int
    val year: Int

    fun setShifts(list: List<Shift>)
    fun setSchedules(list: List<EmployeeSchedules>)
    fun addShift(shift: Shift)
    fun addSchedule(day: Int, index: Int, schedule: Schedule)
    fun deleteShift(shift: Shift)
}

@OptIn(ExperimentalMaterialApi::class)
class ScheduleState @Inject constructor() : IScheduleState {
    private val _schedules = listOf(
        *(0 until Days.values().size).map {
            mutableStateListOf<EmployeeShift>()
        }.toTypedArray()
    )
    private val _shifts = mutableStateMapOf<Int, Shift>()
    private var _popupData: MutableState<ShiftPopupData?> = mutableStateOf(null)
    private lateinit var _bottomSheetScaffoldState: BottomSheetScaffoldState

    private val _selectedShift = mutableStateOf<Shift?>(null)
    private var _year = -1
    private var _week = -1

    override lateinit var saveSchedule: (schedule: Schedule) -> Unit
    override lateinit var insertSchedule: (Int, Int, Schedule) -> Unit
    override lateinit var insertShift: (Shift) -> Unit
    override lateinit var deleteShift: (Shift) -> Unit
    override lateinit var deleteSchedule: (Int) -> Unit

    override val week: Int
        get() = _week
    override val year: Int
        get() = _year

    private val monday = mutableStateMapOf<String, EmployeeShift>()
    private val tuedsday = mutableStateMapOf<String, EmployeeShift>()

    override val shifts: SnapshotStateList<Shift>
        get() = _shifts.values.toMutableStateList()

    override val popupData: MutableState<ShiftPopupData?>
        get() = _popupData

    override val bottomSheetScaffoldState: BottomSheetScaffoldState
        get() = _bottomSheetScaffoldState

    override val selectedShift: MutableState<Shift?>
        get() = _selectedShift

    override fun setSchedules(list: List<EmployeeSchedules>) {
        Log.d("setSchedules", Gson().toJson(list))
        for (day in 0 until Days.values().size) {
            _schedules[day].clear()
            list.forEach { employeeSchedule ->
                val employee = employeeSchedule.employee
                _schedules[day].add(
                    employeeSchedule.schedules.find { s ->
                        s.day == day
                    }.let { schedule ->
                        EmployeeShift(
                            day = day,
                            employeeName = employee.name,
                            employeeId = employee.employeeId,
                            id = schedule?.id,
                            start = schedule?.start,
                            end = schedule?.end
                        )
                    }
                )
            }
            _schedules[day].sortBy { it.employeeName }
        }
    }

    override fun scheduleForDay(day: Int): SnapshotStateList<EmployeeShift> {
        return _schedules[day]
    }

    override fun setShifts(list: List<Shift>) {
        _shifts.clear()
        list.forEach { shift ->
            if (shift.id != null)
                _shifts[shift.id] = shift
        }
    }

    override fun setBottomSheetScaffoldState(bsState: BottomSheetScaffoldState) {
        _bottomSheetScaffoldState = bsState
    }

    private fun setPopupData(popupData: ShiftPopupData) {
        if (_popupData.value == null)
            _popupData.value = popupData
    }

    override fun dismissPopup() {
        _popupData.value = null
    }

    private fun showShiftPopup(shift: Shift, onEditShift: (Shift) -> Unit) {
        setPopupData(ShiftPopupData(
            editName = true,
            name = shift.name,
            start = shift.start,
            end = shift.end,
            onSaveData = { name, start, end ->
                if (name != "")
                    onEditShift(Shift(shift.id, name, start, end))
                dismissPopup()
            }
        ))
    }

    override fun onScheduleClick(day: Int, index: Int) {
        Log.d("ScheduleState", "onScheduleClick index = $index, day = $day")
        if (index > _schedules[day].size) return
        val employeeShift: EmployeeShift = _schedules[day][index]
        val assignShift = _selectedShift.value
        if (assignShift != null) {
            insertSchedule(
                day,
                index,
                Schedule(
                    id = employeeShift.id,
                    employeeId = employeeShift.employeeId,
                    employeeName = employeeShift.employeeName,
                    year = _year,
                    week = _week,
                    day = day,
                    start = assignShift.start,
                    end = assignShift.end
                )
            )
        } else {
            setPopupData(ShiftPopupData(
                name = employeeShift.employeeName,
                start = employeeShift.start,
                end = employeeShift.end,
                onSaveData = { _, start, end ->
                    Log.d("onShiftClick", "saveData: day = $day, start = $start, end = $end")
                    insertSchedule(
                        day,
                        index,
                        Schedule(
                            id = employeeShift.id,
                            employeeId = employeeShift.employeeId,
                            employeeName = employeeShift.employeeName,
                            year = _year,
                            week = _week,
                            day = day,
                            start = start,
                            end = end
                        )
                    )
                    dismissPopup()
                },
                onDelete = if (employeeShift.id != null) {
                    {
                        onDeleteSchedule(day = day, index = index, id = employeeShift.id)
                        dismissPopup()
                    }
                } else
                    null
            ))
        }
    }

    override fun onShiftClick(shift: Shift) {
        _selectedShift.value = shift
    }

    override fun onCancelSelect() {
        _selectedShift.value = null
    }

    override fun onEditShift() {
        val selected = _selectedShift.value
        if (selected != null) {
            setPopupData(ShiftPopupData(
                editName = true,
                name = selected.name,
                start = selected.start,
                end = selected.end,
                onSaveData = { name, start, end ->
                    insertShift(Shift(name = name, start = start, end = end))
                },
                onDelete = {
                    onDeleteShift(selected)
                    dismissPopup()
                }
            ))
        }
    }

    override fun onAddShift() {
        Log.d("ScheduleState", "onAddShift")
        setPopupData(ShiftPopupData(
            editName = true,
            name = "New Shift",
            start = "09:00",
            end = "05:00",
            onSaveData = { name, start, end ->
                Log.d("ScheduleState", "onSaveData")
                insertShift(Shift(name = name, start = start, end = end))
                dismissPopup()
            }
        ))
    }

    fun onDeleteSchedule(day: Int, index: Int, id: Int) {
        if (index > _schedules[day].size) return
        deleteSchedule(id)
        _schedules[day][index] = _schedules[day][index].copy(
            id = null,
            start = null,
            end = null,
        )
    }

    fun onDeleteShift(shift: Shift) {
        deleteShift(shift)
    }

    override fun addSchedule(day: Int, index: Int, schedule: Schedule) {
        Log.d("ScheduleState", "updating ${schedule.employeeName}")
        _schedules[day][index] = _schedules[day][index].copy(
            employeeName = schedule.employeeName,
            start = schedule.start,
            end = schedule.end,
        )
    }

    override fun addShift(shift: Shift) {
        Log.d("editShift", Gson().toJson(shift))

        if (shift.id != null)
            _shifts[shift.id] = shift
    }

    override fun deleteShift(shift: Shift) {
        Log.d("deleteShift", Gson().toJson(shift))
        if (shift.id != null)
            _shifts.remove(shift.id)
    }

    companion object {
        val previewScheduleState = object : IScheduleViewState {
            private lateinit var _bottomSheetScaffoldState: BottomSheetScaffoldState
            override val shifts: SnapshotStateList<Shift>
                get() = PreviewData.shifts.toMutableStateList()
            override val bottomSheetScaffoldState: BottomSheetScaffoldState
                get() = _bottomSheetScaffoldState
            override val popupData: MutableState<ShiftPopupData?>
                get() = mutableStateOf(null)
            override val selectedShift: MutableState<Shift?>
                get() = mutableStateOf(null)

            override fun dismissPopup() {}
            override fun onScheduleClick(day: Int, index: Int) {}
            override fun onShiftClick(shift: Shift) {}
            override fun onCancelSelect() {}
            override fun onAddShift() {}
            override fun onEditShift() {}
            override fun setBottomSheetScaffoldState(bsState: BottomSheetScaffoldState) {
                _bottomSheetScaffoldState = bsState
            }

            override fun scheduleForDay(day: Int): SnapshotStateList<EmployeeShift> {
                return PreviewData.schedules[day]
            }

        }
    }
}

data class EmployeeShift(
    val employeeId: String,
    val employeeName: String,
    val day: Int,
    val id: Int?,
    val start: String?,
    val end: String?,
)