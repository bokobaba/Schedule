package com.love.schedule.di

import com.love.schedule.feature_employees.presentation.employee_info.view_model.*
import com.love.schedule.model.employee.EmployeesState
import com.love.schedule.model.employee.IEmployeesState
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EmployeesScreenModule {

    @Binds
    @Singleton
    abstract fun bindEmployeesState(employeesState: EmployeesState): IEmployeesState

    @Binds
    @Singleton
    abstract fun bindEmployeeInfoState(employeeInfoState: EmployeeInfoState): IEmployeeInfoState

    @Binds
    @Singleton
    abstract fun bindAvailabilityState(availabilityState: AvailabilityState): IAvailabilityState

    @Binds
    @Singleton
    abstract fun bindRequestsState(requestsState: RequestsState): IRequestsState
}