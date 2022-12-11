package com.love.schedule.feature_rules.presentation.view_model

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.love.schedule.PreviewData
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_rules.domain.model.Rule
import com.love.schedule.feature_schedule.domain.model.Shift
import javax.inject.Inject

interface IRulesViewState {
    val rules: SnapshotStateList<Rule>
    val selected: MutableState<Rule?>
    val scrollEnabled: Boolean
}

interface IRulesState : IRulesViewState {
    fun setEmployees(employees: List<Employee>)
    fun setShifts(shifts: List<Shift>)
    fun setRules(rules: List<Rule>)
    fun addRule(rule: Rule)

    fun onDelete(): Int?
    fun onCancelDelete()
    fun onExpandRule(rule: Rule)
    fun onEditStatus(index: Int): Rule?
    fun onEditName(index: Int, name: String): Rule?
}

class RulesState @Inject constructor(
    private val _data: IRulesStateData,
) : IRulesState {
    override var scrollEnabled by mutableStateOf(true)

    private val _selected = mutableStateOf<Rule?>(null)

    override val rules: SnapshotStateList<Rule>
        get() = _data.rules

    override val selected: MutableState<Rule?>
        get() = _selected

    override fun setEmployees(employees: List<Employee>) {
        _data.employees.clear()
        _data.employees.addAll(employees)
    }

    override fun setShifts(shifts: List<Shift>) {
        _data.shifts.clear()
        _data.shifts.addAll(shifts)
    }

    override fun setRules(rules: List<Rule>) {
        _data.rules.clear()
        _data.rules.addAll(rules)
    }

    override fun addRule(rule: Rule) {
        _data.rules.add(rule)
//        _data.conditions.add(mutableStateListOf())
        if (rule.id != null)
            _data.conditions[rule.id] = mutableStateListOf()
    }

    override fun onDelete(): Int? {
        val id = _selected.value?.id
        if (id != null) {
            val index: Int = _data.rules.indexOfFirst { it.id == id }
            _data.rules.removeAt(index)
//            _data.conditions.removeAt(index)
            _data.conditions.remove(id)
            onCancelDelete()
        }
        return id
    }

    override fun onCancelDelete() {
        _selected.value = null
    }

    override fun onExpandRule(rule: Rule) {
        val selected: Rule? = _selected.value
        if (selected?.id == rule.id) {
            _selected.value = null
            scrollEnabled = true
        } else {
            _selected.value = rule
            scrollEnabled = false
        }
    }

    override fun onEditStatus(index: Int): Rule? {
        Log.d("RulesState", "onEditStatus")
        try {
            val rule: Rule = _data.rules[index]
            _data.rules[index] = _data.rules[index].copy(status = !rule.status)
//            _data.insertRule(rule.copy(priority = index, status = !rule.status))
            return rule.copy(priority = index, status = !rule.status)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("RulesState", "onEditStatus: invalid index: $index, size: ${_data.rules.size} ")
            return null
        }
    }

    override fun onEditName(index: Int, name: String): Rule? {
        Log.d("RulesState", "onEditName: name = $name")
        try {
            val rule: Rule = _data.rules[index]
            if (name == rule.name) return null
            Log.d("RulesState", "editing name")
            _data.rules[index] = _data.rules[index].copy(name = name)
//            _data.insertRule(rule.copy(priority = index, name = name))
            return rule.copy(priority = index, name = name)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("RulesState", "onEditStatus: invalid index: $index, size: ${_data.rules.size} ")
            return null
        }
    }

    companion object {
        val previewRulesState = object : IRulesViewState {
            override val rules: SnapshotStateList<Rule>
                get() = PreviewData.rules.toMutableStateList()
            override val selected: MutableState<Rule?>
                get() = mutableStateOf(PreviewData.rules[0])
            override val scrollEnabled: Boolean = true
        }
    }
}