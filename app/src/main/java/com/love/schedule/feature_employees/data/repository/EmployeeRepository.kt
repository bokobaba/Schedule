package com.love.schedule.feature_employees.data.repository

import com.love.schedule.feature_employees.data.data_source.EmployeeDao
import com.love.schedule.feature_employees.domain.model.Availability
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_employees.domain.model.EmployeeInfo
import com.love.schedule.feature_employees.domain.model.EmployeeRequest
import com.love.schedule.feature_employees.domain.repository.IEmployeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EmployeeRepository(
    private val employeeDao: EmployeeDao,
): IEmployeeRepository {
    override fun getEmployees(): Flow<List<Employee>> {
        return employeeDao.getEmployees().map { employees ->
            employees.sortedBy { it.name }
        }
    }

    override suspend fun getEmployeeById(id: Int): Employee? {
        return employeeDao.getEmployeeById(id)
    }

    override suspend fun insertEmployee(employee: Employee) {
        employeeDao.insertEmployee(employee)
    }

    override suspend fun deleteEmployee(employee: Employee) {
        employeeDao.deleteEmployee(employee)
    }

    override suspend fun deleteEmployeeInfo(employee: Employee) {
        employeeDao.deleteEmployeeInfo(employee)
    }

    override suspend fun getEmployeeInfo(id: Int): EmployeeInfo? {
        return employeeDao.getEmployeeInfo(id)
    }

    override suspend fun getAvailabilityByEmployeeId(id: String): Flow<List<Availability>> {
        return employeeDao.getAvailabilityByEmployeeId(id).map { availability ->
            availability.sortedBy { it.day }
        }
    }

    override suspend fun insertAvailability(vararg availability: Availability) {
        return employeeDao.insertAvailability(availability = availability)
    }

    override suspend fun deleteAvailability(id: String) {
        return employeeDao.deleteAvailability(id = id)
    }

    override suspend fun getRequestsByEmployeeId(id: String): Flow<List<EmployeeRequest>> {
        return employeeDao.getRequestsByEmployeeId(id).map { requests ->
            requests.sortedBy { it.start }
        }
    }

    override suspend fun insertRequest(vararg request: EmployeeRequest) {
        employeeDao.insertRequest(request = request)
    }

    override suspend fun deleteRequests(id: String) {
        employeeDao.deleteRequests(id)
    }
}