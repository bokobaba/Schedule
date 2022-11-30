package com.love.schedule

import androidx.compose.runtime.toMutableStateList
import com.love.schedule.feature_employees.domain.model.Availability
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_employees.domain.model.EmployeeRequest
import com.love.schedule.feature_schedule.domain.model.Shift
import com.love.schedule.feature_schedule.presentation.view_model.EmployeeShift

object PreviewData {
    val employees = listOf<Employee>(
        Employee(0, "Josh", "1234567"),
        Employee(1, "John", "7654321"),
        Employee(2, "Parker", "6439174"),
    )

    val shifts = listOf<Shift>(
        Shift(0, "open", "4:00", "12:00"),
        Shift(1, "close", "12:00", "8:00"),
    )

    val schedules = (1..6).map { day ->
        employees.map { e ->
            EmployeeShift(
                day = day,
                employeeName = e.name,
                employeeId = e.employeeId,
                id = null,
                start = "09:00",
                end = "05:00",
            )
        }.toMutableStateList()
    }
//        listOf<ScheduleColumn>(
//        ScheduleColumn(
//            shifts = (0..6).associate { "6439174" to
//                    EmployeeShift(
//                        employeeId = "6439174",
//                        employeeName = "Parker",
//                        id = 0
//                    )
//            }
//        ),
//        ScheduleColumn(
//            2022,
//            1,
//            "1234567",
//            "Josh",
//            shifts = (0..6).map { EmployeeShift(null,null, null) }.toMutableStateList()
//        ),
//        ScheduleColumn(
//            2022,
//            1,
//            "7654321",
//            "John",
//            shifts = (0..6).map { EmployeeShift(null,null, null) }.toMutableStateList()
//        ),
//        ScheduleRow(
//            2022,
//            1,
//            "7654321",
//            "John",
//            shifts = (0..6).map { TimeRange(null, null) }.toMutableStateList()
//        ),
//        ScheduleRow(
//            2022,
//            1,
//            "7654321",
//            "John",
//            shifts = (0..6).map { TimeRange(null, null) }.toMutableStateList()
//        ),
//        ScheduleRow(
//            2022,
//            1,
//            "7654321",
//            "John",
//            shifts = (0..6).map { TimeRange(null, null) }.toMutableStateList()
//        ),
//        ScheduleRow(
//            2022,
//            1,
//            "7654321",
//            "John",
//            shifts = (0..6).map { TimeRange(null, null) }.toMutableStateList()
//        ),
//    )

    val requests = listOf<EmployeeRequest>(
        EmployeeRequest(
            id = 0,
            employeeId = employees[0].employeeId,
            description = "Test Request",
            start = "2022-07-20",
            end = "2022-07-22",
        ),
        EmployeeRequest(
            id = 1,
            employeeId = employees[0].employeeId,
            description = "Test Request",
            start = "2022-07-20",
            end = "2022-07-22",
        ),
        EmployeeRequest(
            id = 1,
            employeeId = employees[0].employeeId,
            description = "Test Request",
            start = "",
            end = "",
        )
    )

    val availability = (0..6).map {
        Availability(
            employeeId = employees[0].employeeId,
            day = it,
            enabled = true,
            allDay = false,
            start = "09:00",
            end = "05:00",
        )
    }
}