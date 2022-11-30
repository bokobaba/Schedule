package com.love.schedule.di

import com.love.schedule.feature_schedule.presentation.view_model.IScheduleState
import com.love.schedule.feature_schedule.presentation.view_model.ScheduleState
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ScheduleScreenModule {

    @Binds
    @Singleton
    abstract fun bindScheduleState(scheduleState: ScheduleState): IScheduleState

}