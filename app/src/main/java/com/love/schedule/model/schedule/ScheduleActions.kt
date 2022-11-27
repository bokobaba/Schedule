package com.love.schedule.model.schedule

import android.util.Log
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.love.schedule.shared.component.ShiftPopupData
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
interface IScheduleActions {
    val popupData: ShiftPopupData?
    val bottomSheetScaffoldState: BottomSheetScaffoldState

    fun setBottomSheetScaffoldState(bsState: BottomSheetScaffoldState)
    fun setPopupData(popupData: ShiftPopupData)
    fun onDismissPopup()
    fun addShift(onAddShift: (Shift) -> Unit)
    fun editShift(shift: Shift, onEditShift: (Shift) -> Unit)
    fun onShiftClick(
        selectedShift: Shift?,
        schedule: Schedule,
        day: Int,
        onEditShift: (UUID, Int, TimeRange) -> Unit,
    )
}

@OptIn(ExperimentalMaterialApi::class)
class ScheduleActions @Inject constructor(): IScheduleActions {
    private var _popupData: ShiftPopupData? by mutableStateOf(null)
    private lateinit var _bottomSheetScaffoldState: BottomSheetScaffoldState

    override val popupData: ShiftPopupData?
        get() = _popupData

    override val bottomSheetScaffoldState: BottomSheetScaffoldState
        get() = _bottomSheetScaffoldState

    override fun setPopupData(popupData: ShiftPopupData) {
        if (_popupData == null)
            _popupData = popupData
    }

    override fun setBottomSheetScaffoldState(bsState: BottomSheetScaffoldState) {
        _bottomSheetScaffoldState = bsState
    }

    override fun onDismissPopup() {
        _popupData = null
    }

    override fun addShift(onAddShift: (Shift) -> Unit) {
        Log.d("ScheduleActions", "addShift")
        setPopupData(ShiftPopupData(
            editName = true,
            name = "New Shift",
            start = "0:00",
            end = "0:00",
            onSaveData = { name, start, end ->
                Log.d("shiftONSaveData", "name: $name, start: $start, end: $end")
                if (name != "")
                    onAddShift(
                        Shift(
                            id = UUID.randomUUID(),
                            name = name,
                            start = start,
                            end = end
                        )
                    )
                onDismissPopup()
            }
        ))
    }

    override fun editShift(shift: Shift, onEditShift: (Shift) -> Unit) {
        setPopupData(ShiftPopupData(
            editName = true,
            name = shift.name,
            start = shift.start,
            end = shift.end,
            onSaveData = { name, start, end ->
                if (name != "")
                    onEditShift(Shift(shift.id, name, start, end))
                onDismissPopup()
            }
        ))
    }

    override fun onShiftClick(
        selectedShift: Shift?,
        schedule: Schedule,
        day: Int,
        onEditShift: (UUID, Int, TimeRange) -> Unit,
    ) {
        if (selectedShift == null) {
            val shift = schedule.shifts[day]
            setPopupData(ShiftPopupData(
                name = schedule.name,
                start = shift.start,
                end = shift.end,
                onSaveData = { _, start, end ->
                    Log.d("onShiftClick", "saveData")
                    onEditShift(schedule.id, day, TimeRange(start, end))
                    onDismissPopup()
                }
            ))
        } else {
            onEditShift(schedule.id, day, TimeRange(selectedShift.start, selectedShift.end))
        }
    }
}