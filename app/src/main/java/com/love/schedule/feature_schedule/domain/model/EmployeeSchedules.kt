package com.love.schedule.feature_schedule.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import com.love.schedule.feature_employees.domain.model.Employee


data class EmployeeSchedules(
    @Embedded
    val employee: Employee,

    @Relation(
        parentColumn = "employeeId",
        entityColumn = "employeeId",
    )
    val schedules: List<Schedule>,
)
