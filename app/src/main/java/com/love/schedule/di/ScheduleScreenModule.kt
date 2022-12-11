package com.love.schedule.di

import com.love.schedule.feature_rules.presentation.view_model.ConditionsState
import com.love.schedule.feature_rules.presentation.view_model.IConditionsState
import com.love.schedule.feature_rules.presentation.view_model.IRulesStateData
import com.love.schedule.feature_rules.presentation.view_model.RulesStateData
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

    @Binds
    @Singleton
    abstract fun bindConditionsState(conditionsState: ConditionsState): IConditionsState

    @Binds
    @Singleton
    abstract fun bindRulesStateData(stateData: RulesStateData): IRulesStateData

}