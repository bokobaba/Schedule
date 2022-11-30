package com.love.schedule.feature_schedule.domain.repository

import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_schedule.domain.model.EmployeeSchedules
import com.love.schedule.feature_schedule.domain.model.Schedule
import com.love.schedule.feature_schedule.domain.model.Shift
import kotlinx.coroutines.flow.Flow

interface IScheduleRepository {
    fun getEmployeeSchedules(): Flow<List<EmployeeSchedules>>
    suspend fun insertSchedule(vararg schedule: Schedule): List<Int>
    suspend fun deleteSchedule(id: Int)
    fun getShifts(): Flow<List<Shift>>
    suspend fun insertShift(shift: Shift): Int
    suspend fun deleteShift(shift: Shift)
    suspend fun getSchedules(week: Int, year: Int): List<Schedule>
    fun getEmployees(): Flow<List<Employee>>

}