package com.love.schedule.feature_rules.presentation.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.feature_rules.domain.model.Rule
import com.love.schedule.feature_rules.domain.repository.IRulesRepository
import com.love.schedule.feature_schedule.domain.model.Shift
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RulesViewModel @Inject constructor(
    private val _stateData: IRulesStateData,
    private val _repository: IRulesRepository,
    private val _state: IRulesState,
    private val _conditionsState: IConditionsState,
) : ViewModel() {
    private var getShiftsJob: Job? = null
    private var getEmployeesJob: Job? = null
    private var getDataJob: Job? = null

    val state: IRulesViewState
        get() = _state
    val conditionsState: IConditionsViewState
        get() = _conditionsState

    init {
//        getEmployees()
//        getShifts()
//        getRules()

        getData()
    }

    fun onRulesEvent(event: RulesEvent) {
        when (event) {
            is RulesEvent.AddRule -> {
                insertRule(
                    Rule(
                        id = null,
                        name = "New Rule",
                        status = true,
                        priority = _stateData.rules.size,
                        rules = "",
                    )
                )
            }
            is RulesEvent.DeleteRule -> deleteRule()
            is RulesEvent.ExpandRule -> {
                _state.onExpandRule(event.rule)
            }
            is RulesEvent.CancelDelete -> _state.onCancelDelete()
            is RulesEvent.EditName -> {
                val rule: Rule? = _state.onEditName(event.index, event.name)
                if (rule != null)
                    insertRule(rule)
            }
            is RulesEvent.EditStatus -> {
                val rule: Rule? = _state.onEditStatus(event.index)
                if (rule != null)
                    insertRule(rule)
            }
        }
    }

    fun onConditionsEvent(event: ConditionsEvent) {
        when (event) {
            is ConditionsEvent.GetConditions -> {}
            is ConditionsEvent.AddCondition -> {
                val rule: Rule? = _conditionsState.onAddCondition(event.index, event.condition)
                if (rule != null)
                    insertRule(rule)
            }
            is ConditionsEvent.ConditionClick -> {
                val rule: Rule? = _conditionsState.onConditionClick(event.rIndex, event.cIndex)
                if (rule != null)
                    insertRule(rule)
            }
            is ConditionsEvent.SearchConditions ->
                _conditionsState.onSearchCondition(event.index, event.query)
            is ConditionsEvent.DismissSearch -> _conditionsState.onDismissSearch()
            is ConditionsEvent.RemoveCondition -> {
                val rule: Rule? = _conditionsState.onRemoveCondition(event.rIndex, event.cIndex)
                if (rule != null)
                    insertRule(rule)
            }
            is ConditionsEvent.EditHours -> {
                val rule: Rule? = _conditionsState.onEditHours(event.rIndex, event.hours)
                if (rule != null)
                    insertRule(rule)
            }
        }
    }

    private fun getEmployees() {
        getEmployeesJob?.cancel()
        getEmployeesJob = _repository.getEmployees().onEach { employees ->
            _state.setEmployees(employees)
        }.launchIn(viewModelScope)
    }

    private fun getShifts() {
        getShiftsJob?.cancel()
        getShiftsJob = _repository.getShifts().onEach { shifts ->
            _state.setShifts(shifts)
        }.launchIn(viewModelScope)
    }

    private fun getRules() {
        viewModelScope.launch {
//            delay(1000)
            val rules: List<Rule> = _repository.getRules().sortedBy { it.priority }
            Log.d("RulesViewModel", "rules: ${Gson().toJson(rules)}")
            _state.setRules(rules)
            _state.rules.forEach {
                Log.d("RulesState", "rules: ${it.rules}")
                _conditionsState.setConditions(it.id!!, it.rules)
            }
        }
    }

    private fun getData() {
        getDataJob?.cancel()
        getDataJob = viewModelScope.launch {
            val employeesFlow: Flow<List<Employee>> = _repository.getEmployees()
            val shiftsFlow: Flow<List<Shift>> = _repository.getShifts()
            employeesFlow.combine(shiftsFlow) { employees, shifts ->
                _state.setEmployees(employees)
                _state.setShifts(shifts)
                getRules()
            }.collect()
        }

    }

    private fun insertRule(rule: Rule) {
        Log.d("RulesViewModel", "insert rule")
        viewModelScope.launch {
            val id: Int = _repository.insertRule(rule)[0].toInt()
            if (rule.id == null)
                _state.addRule(rule.copy(id = id))
        }
    }

    private fun deleteRule() {
        Log.d("RulesViewModel", "delete rule")
        viewModelScope.launch {
            val id: Int? = _state.onDelete()
            if (id != null)
                _repository.deleteRule(id)
        }
    }
}