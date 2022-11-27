package com.love.schedule.di

import com.love.schedule.model.schedule.IScheduleActions
import com.love.schedule.model.schedule.ScheduleActions
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
    abstract fun bindScheduleActions(scheduleActions: ScheduleActions): IScheduleActions
}