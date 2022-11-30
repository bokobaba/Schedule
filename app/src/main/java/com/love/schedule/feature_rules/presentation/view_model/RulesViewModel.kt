package com.love.schedule.feature_rules.presentation.view_model

import androidx.lifecycle.ViewModel
import com.love.schedule.feature_rules.domain.repository.IRulesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RulesViewModel @Inject constructor(
    private val _repository: IRulesRepository,
    private val _state: IRulesState,
): ViewModel() {

}