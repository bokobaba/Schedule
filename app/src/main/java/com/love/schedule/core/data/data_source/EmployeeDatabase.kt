package com.love.schedule.core.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.love.schedule.feature_employees.data.data_source.EmployeeDao
import com.love.schedule.feature_employees.domain.model.Availability
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_employees.domain.model.EmployeeRequest
import com.love.schedule.feature_schedule.data.data_source.ScheduleDao
import com.love.schedule.feature_schedule.domain.model.Schedule
import com.love.schedule.feature_schedule.domain.model.Shift

@Database(
    entities = [
        Employee::class,
        EmployeeRequest::class,
        Availability::class,
        Schedule::class,
        Shift::class,
    ],
    version = 2
)
abstract class EmployeeDatabase : RoomDatabase() {

    abstract val employeeDao: EmployeeDao
    abstract val scheduleDao: ScheduleDao

    companion object {
        const val DATABASE_NAME = "employees_db"
    }
}