package com.love.schedule.model.schedule

import java.util.*

data class Shift(
    val id: UUID,
    val name: String,
    val start: String,
    val end: String,
)
