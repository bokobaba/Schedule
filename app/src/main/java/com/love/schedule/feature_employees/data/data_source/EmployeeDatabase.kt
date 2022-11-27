package com.love.schedule.feature_employees.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.love.schedule.feature_employees.domain.model.Availability
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_employees.domain.model.EmployeeRequest

@Database(
    entities = [Employee::class, EmployeeRequest::class, Availability::class],
    version = 2
)
abstract class EmployeeDatabase: RoomDatabase() {

    abstract val employeeDao: EmployeeDao

    companion object {
        const val DATABASE_NAME = "employees_db"
    }
}