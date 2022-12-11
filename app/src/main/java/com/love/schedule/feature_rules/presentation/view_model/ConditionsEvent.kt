package com.love.schedule.feature_rules.presentation.view_model

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.love.schedule.feature_rules.domain.model.Condition

sealed class ConditionsEvent {
    data class GetConditions(val index: Int): ConditionsEvent()
    data class AddCondition(val index: Int, val condition: Condition): ConditionsEvent()
    data class RemoveCondition(val rIndex: Int, val cIndex: Int): ConditionsEvent()
    data class ConditionClick(val rIndex: Int, val cIndex: Int): ConditionsEvent()
    data class SearchConditions(val index: Int, val query: String): ConditionsEvent()
    data class EditHours(val rIndex: Int, val hours: String): ConditionsEvent()
    object DismissSearch: ConditionsEvent()
}