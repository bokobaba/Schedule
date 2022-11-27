package com.love.schedule.feature_employees.presentation.employee_info.view_model

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.Gson
import com.love.schedule.Data
import com.love.schedule.feature_employees.domain.model.Availability
import com.love.schedule.shared.component.ShiftPopupData
import javax.inject.Inject

interface IAvailabilityState {
    val availability: List<Availability>
    val popupData: MutableState<ShiftPopupData?>
    fun setAvailability(list: List<Availability>)
    fun editAllDay(day: Int, checked: Boolean)
    fun editEnabled(day: Int, checked: Boolean)
    fun editStartEnd(day: Int, start: String, end: String)
    fun setPopupData(day: Int, start: String, end: String)
    fun onDismissPopup()
}

class AvailabilityState @Inject constructor() : IAvailabilityState {
    private val _availability = mutableStateListOf<Availability>()
    private var _popupData = mutableStateOf<ShiftPopupData?>(null)

    override val availability: List<Availability>
        get() = _availability

    override val popupData: MutableState<ShiftPopupData?>
        get() = _popupData

    override fun setAvailability(list: List<Availability>) {
        _availability.clear()
        Log.d("AvailabilityState", "setAvailability")
        if (list.isEmpty()) {
            Log.d("setAvailability", "list empty")
            for (i in 0..6) {
                _availability.add(
                    Availability(
                        employeeId = "",
                        day = i,
                        enabled = true,
                        allDay = true,
                        start = "00:00",
                        end = "23:59",
                    )
                )
            }
        } else {
            _availability.addAll(list)
        }
        Log.d("AvailabilityState", "_availability = ${Gson().toJson(_availability)}")
    }

    override fun editAllDay(day: Int, checked: Boolean) {
        Log.d("AvailabilityState", "editAllDay: id = $day, checked = $checked")
        _availability[day] = _availability[day]?.copy(allDay = checked) ?: return
    }

    override fun editEnabled(day: Int, checked: Boolean) {
        Log.d("AvailabilityState", "editEnabled: id = $day, checked = $checked")
        _availability[day] = _availability[day]?.copy(enabled = checked) ?: return
    }

    override fun setPopupData(day: Int, start: String, end: String) {
        _popupData.value = ShiftPopupData(
            name = Data.days[day],
            start = start,
            end = end,
            onSaveData = { _, start, end ->
                editStartEnd(day, start, end)
                onDismissPopup()
            }
        )
    }

    override fun onDismissPopup() {
        _popupData.value = null
    }

    override fun editStartEnd(day: Int, start: String, end: String) {
        Log.d("AvailabilityState", "editStartEnd: day = $day, start = $start, end = $end")
        _availability[day] = _availability[day].copy(
            start = start,
            end = end
        )
    }

    companion object {
        val previewState = object : IAvailabilityState {
            override val availability: List<Availability>
                get() = Data.availability
            override val popupData: MutableState<ShiftPopupData?>
                get() = mutableStateOf(null)

            override fun setAvailability(list: List<Availability>) {}
            override fun editAllDay(id: Int, checked: Boolean) {}
            override fun editEnabled(day: Int, checked: Boolean) {}
            override fun editStartEnd(day: Int, start: String, end: String) {}
            override fun setPopupData(day: Int, start: String, end: String) {}
            override fun onDismissPopup() {}
        }
    }
}