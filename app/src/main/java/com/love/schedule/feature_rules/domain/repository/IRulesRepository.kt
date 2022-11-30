package com.love.schedule.feature_rules.domain.repository

import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_rules.domain.model.Rule
import com.love.schedule.feature_schedule.domain.model.Shift
import kotlinx.coroutines.flow.Flow

interface IRulesRepository {
    fun getEmployees(): Flow<List<Employee>>

    fun getShifts(): Flow<List<Shift>>

    fun getRules(): Flow<List<Rule>>

    suspend fun insertRule(vararg rule: Rule): List<Long>

    suspend fun deleteRule(id: Int)
}