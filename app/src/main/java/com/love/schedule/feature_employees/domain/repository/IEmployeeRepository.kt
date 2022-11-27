package com.love.schedule.feature_employees.domain.repository

import com.love.schedule.feature_employees.domain.model.Availability
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_employees.domain.model.EmployeeInfo
import com.love.schedule.feature_employees.domain.model.EmployeeRequest
import kotlinx.coroutines.flow.Flow

interface IEmployeeRepository {

    fun getEmployees(): Flow<List<Employee>>

    suspend fun getEmployeeById(id: Int): Employee?

    suspend fun insertEmployee(employee: Employee)

    suspend fun deleteEmployee(employee: Employee)

    suspend fun deleteEmployeeInfo(employee: Employee)

    suspend fun getEmployeeInfo(id: Int): EmployeeInfo?

    suspend fun getAvailabilityByEmployeeId(id: String): Flow<List<Availability>>

    suspend fun insertAvailability(vararg availability: Availability)

    suspend fun deleteAvailability(id: String)

    suspend fun getRequestsByEmployeeId(id: String): Flow<List<EmployeeRequest>>

    suspend fun insertRequest(vararg request: EmployeeRequest)

    suspend fun deleteRequests(id: String)
}