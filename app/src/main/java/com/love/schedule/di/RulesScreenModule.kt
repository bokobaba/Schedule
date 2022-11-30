package com.love.schedule.di

import com.love.schedule.feature_rules.presentation.view_model.IRulesState
import com.love.schedule.feature_rules.presentation.view_model.RulesState
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RulesScreenModule {

    @Binds
    @Singleton
    abstract fun bindRulesState(rulesState: RulesState): IRulesState
}