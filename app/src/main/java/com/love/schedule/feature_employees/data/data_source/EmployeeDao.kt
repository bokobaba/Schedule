package com.love.schedule.feature_employees.data.data_source

import androidx.room.*
import com.love.schedule.feature_employees.domain.model.Availability
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_employees.domain.model.EmployeeInfo
import com.love.schedule.feature_employees.domain.model.EmployeeRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {

    @Query("SELECT * FROM employee")
    fun getEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM employee WHERE id = :id")
    suspend fun getEmployeeById(id: Int): Employee?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee)

    @Delete
    suspend fun deleteEmployee(employee: Employee)

    // Availability
    @Query("SELECT * FROM availability WHERE employeeId == :id")
    fun getAvailabilityByEmployeeId(id: String): Flow<List<Availability>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvailability(vararg availability: Availability)

    @Query("DELETE FROM availability WHERE employeeId == :id")
    suspend fun deleteAvailability(id: String)

    // Requests
    @Query("SELECT * FROM employeeRequest WHERE employeeId == :id")
    fun getRequestsByEmployeeId(id: String): Flow<List<EmployeeRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(vararg request: EmployeeRequest)

    @Query("DELETE FROM employeerequest WHERE employeeId == :id")
    suspend fun deleteRequests(id: String)

    @Transaction
    @Query("SELECT * FROM employee WHERE id = :id")
    suspend fun getEmployeeInfo(id: Int): EmployeeInfo?

    @Transaction
    suspend fun deleteEmployeeInfo(employee: Employee) {
        deleteEmployee(employee)
        deleteAvailability(employee.employeeId)
        deleteRequests(employee.employeeId)
    }
}