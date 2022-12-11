package com.love.schedule.core.data.repository

import android.util.Log
import com.google.gson.Gson
import com.love.schedule.core.data.data_source.ScheduleApiDao
import com.love.schedule.core.data.remote.ScheduleApi
import com.love.schedule.core.data.remote.responses.*
import com.love.schedule.core.util.Resource
import com.love.schedule.feature_employees.domain.model.Availability
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_employees.domain.model.EmployeeInfo
import com.love.schedule.feature_employees.domain.model.EmployeeRequest
import com.love.schedule.feature_rules.domain.model.Rule
import com.love.schedule.feature_schedule.domain.model.Schedule
import com.love.schedule.feature_schedule.domain.model.Shift
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class ScheduleApiRepository @Inject constructor(
    private val api: ScheduleApi,
    private val dao: ScheduleApiDao,
) {
    suspend fun getEmployeeInfo(): Resource<List<GetEmployeeInfoDto>> {
        val response = try {
            api.getEmployees()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return Resource.Error("An error occurred")
        }
        return Resource.Success(response)
    }

    suspend fun getSchedules(): Resource<List<GetScheduleDto>> {
        val response = try {
            api.getSchedules()
        } catch (e: java.lang.Exception) {
            return Resource.Error("An error occurred")
        }
        return Resource.Success(response)
    }

    suspend fun getShifts(): Resource<List<GetShiftDto>> {
        val response = try {
            api.getShifts()
        } catch (e: java.lang.Exception) {
            return Resource.Error("An error occurred")
        }
        return Resource.Success(response)
    }

    suspend fun getRules(): Resource<List<GetRuleDto>> {
        val response = try {
            api.getRules()
        } catch (e: java.lang.Exception) {
            return Resource.Error("An error occurred")
        }
        return Resource.Success(response)
    }

    private suspend fun deleteDataFromServer(delete: suspend () -> Unit, identifier: String)
            : Resource<Boolean> {
        try {
            Log.d("ScheduleApiRepository", "deleting $identifier from server")
            delete()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return Resource.Error("error occurred deleting $identifier from server")
        }
        return Resource.Success(true)
    }

    private suspend fun deleteAllDataFromServer(): Resource<Boolean> {
        var resource: Resource<Boolean> =
            deleteDataFromServer(api::deleteAllEmployees, "employees")
        if (resource is Resource.Error) return resource

        resource = deleteDataFromServer(api::deleteAllSchedules, "schedules")
        if (resource is Resource.Error) return resource

        resource = deleteDataFromServer(api::deleteAllShifts, "shifts")
        if (resource is Resource.Error) return resource

        return Resource.Success(true)
    }

    private suspend fun <T> exportData(
        data: T,
        export: suspend (T) -> Unit,
        identifier: String
    ): Resource<Boolean> {
        try {
            Log.d("ScheduleApiRespository", "exporting $identifier")
            export(data)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return Resource.Error("unable to export data to server")
        }

        return Resource.Success(true)
    }

    private suspend fun exportEmployees(employeeInfo: List<EmployeeInfo>): Resource<Boolean> {
        return exportData(
            data = employeeInfo.map { info ->
                AddEmployeeDto(
                    employeeId = info.employee.employeeId.toInt(),
                    name = info.employee.name,
                )
            },
            export = api::exportEmployees,
            identifier = "employees",
        )
    }

    private suspend fun exportShifts(): Resource<Boolean> {
        val shifts: List<Shift> = dao.getShifts()
        return exportData(
            identifier = "shifts",
            data = shifts.map { shift ->
                AddShiftDto(
                    name = shift.name,
                    start = shift.start,
                    end = shift.end,
                )
            },
            export = api::exportShifts,
        )
    }

    private suspend fun exportAvailability(employeeInfo: List<EmployeeInfo>): Resource<Boolean> {
        return exportData(
            identifier = "availability",
            data = employeeInfo.map { info ->
                info.availability.map { a ->
                    AddAvailabilityDto(
                        enabled = a.enabled,
                        allDay = a.allDay,
                        day = a.day,
                        employeeId = a.employeeId.toInt(),
                        start = a.start,
                        end = a.end
                    )
                }
            }.flatten(),
            export = api::exportAvailability
        )
    }

    private suspend fun exportRequests(employeeInfo: List<EmployeeInfo>): Resource<Boolean> {
        return exportData(
            identifier = "requests",
            data = employeeInfo.map { info ->
                info.requests.map { r ->
                    AddRequestDto(
                        description = r.description,
                        employeeId = r.employeeId.toInt(),
                        start = r.start,
                        end = r.end
                    )
                }
            }.flatten(),
            export = api::exportRequests
        )
    }

    private suspend fun exportSchedules(): Resource<Boolean> {
        val schedules = dao.getSchedules()
        return exportData(
            identifier = "schedules",
            data = schedules.map { s ->
                AddScheduleDto(
                    employeeId = s.employeeId.toInt(),
                    year = s.year,
                    week = s.week,
                    day = s.day,
                    start = s.start,
                    end = s.end
                )
            },
            export = api::exportSchedules
        )
    }

    private suspend fun exportRules(): Resource<Boolean> {
        val rules = dao.getRules()
        Log.d("exportRules", Gson().toJson(rules))
        return exportData(
            identifier = "rules",
            data = AddRuleGroupsDto(
                rules = rules.map { r ->
                AddRuleDto(
                    name = r.name,
                    status = r.status,
                    priority = r.priority,
                    rules = r.rules
                )
            }),
            export = api::exportRules
        )
    }

    suspend fun exportData(): Resource<Boolean> {
        var resource: Resource<Boolean> = deleteAllDataFromServer()
        if (resource is Resource.Error) return resource

        val employeeInfo: List<EmployeeInfo> = dao.getEmployeeInfo()
        resource = exportEmployees(employeeInfo)
        if (resource is Resource.Error) return resource

        resource = exportRequests(employeeInfo)
        if (resource is Resource.Error) return resource

        resource = exportAvailability(employeeInfo)
        if (resource is Resource.Error) return resource

        resource = exportRules()
        if (resource is Resource.Error) return resource

        resource = exportShifts()
        if (resource is Resource.Error) return resource

        resource = exportSchedules()
        if (resource is Resource.Error) return resource

        return Resource.Success(true)
    }

    suspend fun importData() {
        dao.deleteAllInfo()
        val employeesResponse = getEmployeeInfo()
        Log.d("ImportData", Gson().toJson(employeesResponse.data))
        Log.d("ImportData", "null = ${employeesResponse.data != null}")
        if (employeesResponse.data != null) {
            Log.d("ImportData", "mapping...")
            val availability = mutableListOf<Availability>()
            val requests = mutableListOf<EmployeeRequest>()
            val employees = mutableListOf<Employee>()

            employeesResponse.data.forEach { dto ->
                employees.add(Employee(name = dto.name, employeeId = "${dto.employeeId}"))
                requests.addAll(dto.requests.map {
                    EmployeeRequest(
                        description = it.description,
                        employeeId = "${it.employeeId}",
                        start = it.start,
                        end = it.end,
                    )
                })
                availability.addAll(dto.availability.map {
                    Availability(
                        employeeId = "${it.employeeId}",
                        day = it.day,
                        enabled = it.enabled,
                        allDay = it.allDay,
                        start = it.start,
                        end = it.end,
                    )
                })


            }

            dao.insertEmployee(*employees.toTypedArray())
            dao.insertAvailability(*availability.toTypedArray())
            dao.insertRequests(*requests.toTypedArray())

            val schedulesResponse = getSchedules()
            if (schedulesResponse.data != null) {
                val schedules: List<Schedule> = schedulesResponse.data.map { dto ->
                    Schedule(
                        employeeId = "${dto.employeeId}",
                        employeeName = employees.find {
                            it.employeeId == "${dto.employeeId}"
                        }?.name ?: "",
                        year = dto.year,
                        week = dto.week,
                        day = dto.day,
                        start = dto.start,
                        end = dto.end,
                    )
                }

                dao.insertSchedules(*schedules.toTypedArray())
            }
        }

        val rulesResponse = getRules()
        if (rulesResponse.data != null) {
            val rules: List<Rule> = rulesResponse.data.map { dto ->
                Rule(
                    status = dto.status,
                    priority = dto.priority,
                    name = dto.name,
                    rules = dto.rules,
                )
            }

            dao.insertRules(*rules.toTypedArray())
        }

    }
}