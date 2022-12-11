package com.love.schedule.feature_rules.presentation.view_model

import com.love.schedule.feature_rules.domain.model.Rule

sealed class RulesEvent {
    object AddRule : RulesEvent()
    object DeleteRule : RulesEvent()
    object CancelDelete: RulesEvent()
    data class ExpandRule(val rule: Rule): RulesEvent()
    data class EditStatus(val index: Int): RulesEvent()
    data class EditName(val index: Int, val name: String): RulesEvent()
}