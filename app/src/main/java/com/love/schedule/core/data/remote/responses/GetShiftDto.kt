package com.love.schedule.core.data.remote.responses

data class GetShiftDto(
    val end: String,
    val id: Int,
    val name: String,
    val start: String
)