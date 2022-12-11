package com.love.schedule.core.data.data_source

import androidx.room.*
import com.love.schedule.core.data.remote.ScheduleApi
import com.love.schedule.feature_employees.domain.model.Availability
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_employees.domain.model.EmployeeInfo
import com.love.schedule.feature_employees.domain.model.EmployeeRequest
import com.love.schedule.feature_rules.domain.model.Rule
import com.love.schedule.feature_schedule.domain.model.Schedule
import com.love.schedule.feature_schedule.domain.model.Shift

@Dao
interface ScheduleApiDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(vararg employee: Employee)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvailability(vararg availability: Availability)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(vararg requests: EmployeeRequest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(vararg schedule: Schedule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(vararg rule: Rule)

    @Query("DELETE FROM employee")
    suspend fun deleteEmployees()

    @Query("DELETE FROM availability")
    suspend fun deleteAvailability()

    @Query("DELETE FROM employeerequest")
    suspend fun deleteRequests()

    @Transaction
    suspend fun deleteAllInfo() {
        deleteEmployees()
        deleteRequests()
        deleteAvailability()
        deleteEmployeeSchedules()
        deleteRules()
    }

    @Query("DELETE FROM schedule")
    suspend fun deleteEmployeeSchedules()

    @Query("DELETE FROM rule")
    suspend fun deleteRules()

    @Transaction
    @Query("SELECT * FROM employee")
    suspend fun getEmployeeInfo(): List<EmployeeInfo>

    @Query("SELECT * FROM shift")
    suspend fun getShifts(): List<Shift>

    @Query("SELECT * FROM rule")
    suspend fun getRules(): List<Rule>

    @Query("SELECT * FROM schedule")
    suspend fun getSchedules(): List<Schedule>
}