package com.love.schedule.feature_employees.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class EmployeeRequest(
    @PrimaryKey val id: Int? = null,
    val description: String,
    val start: String,
    val end: String,
    val employeeId: String,
)