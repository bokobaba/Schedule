package com.love.schedule.feature_schedule.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_schedule.domain.model.EmployeeSchedules
import com.love.schedule.feature_schedule.domain.model.Schedule
import com.love.schedule.feature_schedule.domain.model.Shift
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    // Schedule
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(vararg schedule: Schedule): List<Long>

    @Query("DELETE FROM schedule WHERE id = :id")
    suspend fun deleteSchedule(id: Int)

    // Shift
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: Shift): Long

    @Delete
    suspend fun deleteShift(shift: Shift)

    // Join
    @Transaction
    @Query("SELECT * FROM employee")
    fun getEmployeeSchedules(): Flow<List<EmployeeSchedules>>

    @Query("SELECT * FROM shift")
    fun getShifts(): Flow<List<Shift>>

    @Query("SELECT * FROM employee")
    fun getEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM schedule WHERE week == :week AND year == :year")
    suspend fun getSchedules(week: Int, year: Int): List<Schedule>
}