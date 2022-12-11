package com.love.schedule.core.data.remote.responses

data class GetAvailabilityDto(
    val allDay: Boolean,
    val day: Int,
    val employeeId: Int,
    val enabled: Boolean,
    val end: String,
    val start: String
)