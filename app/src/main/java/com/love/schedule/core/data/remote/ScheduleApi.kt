package com.love.schedule.core.data.remote

import com.love.schedule.BuildConfig
import com.love.schedule.core.data.remote.responses.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ScheduleApi {

    @GET("Employees/EmployeeInfo")
    suspend fun getEmployees(): List<GetEmployeeInfoDto>

    @GET("Shifts")
    suspend fun getShifts(): List<GetShiftDto>

    @GET("Schedules")
    suspend fun getSchedules(): List<GetScheduleDto>

    @GET("RuleGroups")
    suspend fun getRules(): List<GetRuleDto>

    @DELETE("Employees/DeleteAll")
    suspend fun deleteAllEmployees(): Response<Unit>

    @DELETE("Schedules/DeleteAll")
    suspend fun deleteAllSchedules(): Response<Unit>

    @DELETE("Shifts/DeleteAll")
    suspend fun deleteAllShifts(): Response<Unit>

    @POST("Employees/Import")
    suspend fun exportEmployees(@Body dto: List<AddEmployeeDto>): Response<Unit>

    @POST("Requests/Import")
    suspend fun exportRequests(@Body dto: List<AddRequestDto>): Response<Unit>

    @POST("Availability/Import")
    suspend fun exportAvailability(@Body dto: List<AddAvailabilityDto>): Response<Unit>

    @POST("Schedules/Import")
    suspend fun exportSchedules(@Body dto: List<AddScheduleDto>): Response<Unit>

    @POST("Shifts/Import")
    suspend fun exportShifts(@Body dto: List<AddShiftDto>): Response<Unit>

    @POST("RuleGroups")
    suspend fun exportRules(@Body dto: AddRuleGroupsDto): Response<Unit>
}