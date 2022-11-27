package com.love.schedule

import com.love.schedule.feature_employees.domain.model.Availability
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_employees.domain.model.EmployeeRequest
import com.love.schedule.model.schedule.Schedule
import com.love.schedule.model.schedule.Shift
import com.love.schedule.model.schedule.TimeRange
import java.util.*

enum class Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

object Data {
    val days: List<String>
        get() = listOf(
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
            "Sunday",
        )

    val employees = listOf<Employee>(
        Employee(0, "Josh", "1234567"),
        Employee(1, "John", "7654321"),
        Employee(2,"Parker", "6439174"),
    )

    val shifts = listOf<Shift>(
        Shift(UUID.randomUUID(), "open", "4:00", "12:00"),
        Shift(UUID.randomUUID(), "close", "12:00", "8:00"),
    )

    val schedules = listOf<Schedule>(
        Schedule(
            UUID.randomUUID(),
            2022,
            1,
            "6439174",
            "Parker",
            shifts = (1..7).map { TimeRange("4:00", "12:00") }.toMutableList()
        ),
        Schedule(
            UUID.randomUUID(),
            2022,
            1,
            "1234567",
            "Josh",
            shifts = (1..7).map { TimeRange("04:00", "12:00") }.toMutableList()
        ),
        Schedule(
            UUID.randomUUID(),
            2022,
            1,
            "7654321",
            "John",
            shifts = (1..7).map { TimeRange(null, null) }.toMutableList()
        ),
        Schedule(
            UUID.randomUUID(),
            2022,
            1,
            "7654321",
            "John",
            shifts = (1..7).map { TimeRange(null, null) }.toMutableList()
        ),
        Schedule(
            UUID.randomUUID(),
            2022,
            1,
            "7654321",
            "John",
            shifts = (1..7).map { TimeRange(null, null) }.toMutableList()
        ),
        Schedule(
            UUID.randomUUID(),
            2022,
            1,
            "7654321",
            "John",
            shifts = (1..7).map { TimeRange(null, null) }.toMutableList()
        ),
        Schedule(
            UUID.randomUUID(),
            2022,
            1,
            "7654321",
            "John",
            shifts = (1..7).map { TimeRange(null, null) }.toMutableList()
        ),
    )

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