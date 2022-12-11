package com.love.schedule.feature_rules.data.repository

import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_rules.data.data_source.RulesDao
import com.love.schedule.feature_rules.domain.model.Rule
import com.love.schedule.feature_rules.domain.repository.IRulesRepository
import com.love.schedule.feature_schedule.domain.model.Shift
import kotlinx.coroutines.flow.Flow

class RulesRepository(
    private val dao: RulesDao
) : IRulesRepository {
    override fun getEmployees(): Flow<List<Employee>> {
        return dao.getEmployees()
    }

    override fun getShifts(): Flow<List<Shift>> {
        return dao.getShifts()
    }

    override suspend fun getRules(): List<Rule> {
        return dao.getRules()
    }

    override suspend fun insertRule(vararg rule: Rule): List<Long> {
        return dao.insertRule(*rule)
    }

    override suspend fun deleteRule(id: Int) {
        dao.deleteRule(id)
    }
}