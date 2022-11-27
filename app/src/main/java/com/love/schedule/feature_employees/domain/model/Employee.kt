package com.love.schedule.feature_employees.domain.model

import androidx.lifecycle.ViewModel
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

@Entity
data class Employee (
    @PrimaryKey val id: Int? = null,
    val name: String = "New Employee",
    val employeeId: String = "",
)

class InvalidEmployeeException(message: String): Exception(message)