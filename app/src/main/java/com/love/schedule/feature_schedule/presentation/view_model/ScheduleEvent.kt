package com.love.schedule.feature_schedule.presentation.view_model

import com.love.schedule.feature_schedule.domain.model.Shift

sealed class ScheduleEvent {
    object DismissPopup: ScheduleEvent()
    data class ScheduleClick(val day: Int, val index: Int): ScheduleEvent()
    data class ShiftClick(val shift: Shift): ScheduleEvent()
    object CancelSelect: ScheduleEvent()
    object AddShift: ScheduleEvent()
    object EditShift: ScheduleEvent()
    data class SelectWeek(val year: Int, val month: Int, val day: Int): ScheduleEvent()
}