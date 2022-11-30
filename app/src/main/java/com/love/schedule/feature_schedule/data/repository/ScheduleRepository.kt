package com.love.schedule.feature_schedule.data.repository

import android.util.Log
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_schedule.data.data_source.ScheduleDao
import com.love.schedule.feature_schedule.domain.model.EmployeeSchedules
import com.love.schedule.feature_schedule.domain.model.Schedule
import com.love.schedule.feature_schedule.domain.model.Shift
import com.love.schedule.feature_schedule.domain.repository.IScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScheduleRepository(
    private val scheduleDao: ScheduleDao
): IScheduleRepository {
    override fun getEmployeeSchedules(): Flow<List<EmployeeSchedules>> {
        return scheduleDao.getEmployeeSchedules().map {
            it.sortedBy { employeeSchedule ->
                employeeSchedule.employee.name
            }
        }
    }

    override suspend fun insertSchedule(vararg schedule: Schedule): List<Int> {
        return scheduleDao.insertSchedule(*schedule).map { it.toInt() }
    }

    override suspend fun deleteSchedule(id: Int) {
        scheduleDao.deleteSchedule(id)
    }


    override fun getShifts(): Flow<List<Shift>> {
        return scheduleDao.getShifts().map { shifts ->
            shifts.sortedBy { it.name }
        }
    }

    override suspend fun insertShift(shift: Shift): Int {
        return scheduleDao.insertShift(shift).toInt()
    }

    override suspend fun deleteShift(shift: Shift) {
        scheduleDao.deleteShift(shift)
    }

    override fun getEmployees(): Flow<List<Employee>> {
        return scheduleDao.getEmployees()
    }

    override suspend fun getSchedules(week: Int, year: Int): List<Schedule> {
        return scheduleDao.getSchedules(week, year)
    }
}