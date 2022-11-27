package com.love.schedule.feature_employees.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Availability(
    @PrimaryKey val id: Int? = null,
    val employeeId: String,
    val day: Int,
    val enabled: Boolean,
    val allDay: Boolean,
    val start: String,
    val end: String,
)