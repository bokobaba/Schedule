package com.love.schedule.core.data.remote.responses

data class AddScheduleDto(
    val day: Int,
    val employeeId: Int,
    val end: String,
    val start: String,
    val week: Int,
    val year: Int
)