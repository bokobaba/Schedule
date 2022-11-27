package com.love.schedule.feature_employees.data.data_source

import androidx.room.*
import com.love.schedule.feature_employees.domain.model.EmployeeRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestDao {

    @Query("SELECT * FROM employeeRequest WHERE employeeId == :id")
    fun getRequestsByEmployeeId(id: String): Flow<List<EmployeeRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(vararg request: EmployeeRequest)

    @Delete
    suspend fun deleteRequest(vararg request: EmployeeRequest)
}