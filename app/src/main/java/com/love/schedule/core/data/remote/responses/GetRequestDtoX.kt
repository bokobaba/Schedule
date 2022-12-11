package com.love.schedule.core.data.remote.responses

data class GetRequestDtoX(
    val description: String,
    val employeeId: Int,
    val end: String,
    val id: Int,
    val start: String
)