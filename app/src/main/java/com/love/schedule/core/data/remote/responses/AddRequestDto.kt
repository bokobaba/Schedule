package com.love.schedule.core.data.remote.responses

data class AddRequestDto(
    val description: String,
    val employeeId: Int,
    val end: String,
    val start: String
)