package com.love.schedule.feature_employees.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class EmployeeInfo(
    @Embedded
    val employee: Employee,

    @Relation(
        parentColumn = "employeeId",
        entityColumn = "employeeId",
    )
    val availability: List<Availability>,

    @Relation(
        parentColumn = "employeeId",
        entityColumn = "employeeId"
    )
    val requests: List<EmployeeRequest>,
)
