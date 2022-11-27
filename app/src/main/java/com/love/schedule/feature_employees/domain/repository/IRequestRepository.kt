package com.love.schedule.feature_employees.domain.repository

import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_employees.domain.model.EmployeeRequest
import kotlinx.coroutines.flow.Flow

interface IRequestRepository {

    suspend fun getRequestsByEmployeeId(id: String): Flow<List<EmployeeRequest>>

    suspend fun insertRequest(request: EmployeeRequest)

    suspend fun deleteRequest(request: EmployeeRequest)

}