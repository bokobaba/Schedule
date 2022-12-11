package com.love.schedule.feature_rules.presentation.view_model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_rules.domain.model.Condition
import com.love.schedule.feature_rules.domain.model.Rule
import com.love.schedule.feature_schedule.domain.model.Shift
import javax.inject.Inject

interface IRulesStateData {
    val rules: SnapshotStateList<Rule>
//    val conditions: MutableList<SnapshotStateList<Condition>>
    val conditions: MutableMap<Int, SnapshotStateList<Condition>>
    val employees: SnapshotStateList<Employee>
    val shifts: SnapshotStateList<Shift>
    val searchResults: SnapshotStateList<Condition>
}

class RulesStateData @Inject constructor(): IRulesStateData {
    override val searchResults = mutableStateListOf<Condition>()
    override val rules = mutableStateListOf<Rule>()
    override val employees = mutableStateListOf<Employee>()
    override val shifts = mutableStateListOf<Shift>()
//    override val conditions = mutableListOf(mutableStateListOf<Condition>())
    override val conditions = mutableMapOf<Int, SnapshotStateList<Condition>>()

}