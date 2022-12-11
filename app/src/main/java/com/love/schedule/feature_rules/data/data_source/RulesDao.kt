package com.love.schedule.feature_rules.data.data_source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_rules.domain.model.Rule
import com.love.schedule.feature_schedule.domain.model.Shift
import kotlinx.coroutines.flow.Flow

@Dao
interface RulesDao {

    @Query("SELECT * FROM employee")
    fun getEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM shift")
    fun getShifts(): Flow<List<Shift>>

    @Query("SELECT * FROM rule")
    suspend fun getRules(): List<Rule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(vararg rule: Rule): List<Long>

    @Query("DELETE FROM rule WHERE id = :id")
    suspend fun deleteRule(id: Int)
}