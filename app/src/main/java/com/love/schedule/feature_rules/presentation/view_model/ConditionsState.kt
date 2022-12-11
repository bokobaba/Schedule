package com.love.schedule.feature_rules.presentation.view_model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.gson.Gson
import com.love.schedule.core.util.Days
import com.love.schedule.feature_rules.domain.model.Condition
import com.love.schedule.feature_rules.domain.model.ConditionType
import com.love.schedule.feature_rules.domain.model.Rule
import com.love.schedule.feature_rules.utils.MiscConditions
import java.util.*
import javax.inject.Inject

interface IConditionsViewState {
    val searchResults: SnapshotStateList<Condition>
    fun getConditions(rIndex: Int): SnapshotStateList<Condition>
}

interface IConditionsState : IConditionsViewState {
    fun setConditions(id: Int, rules: String)
    fun onAddCondition(index: Int, condition: Condition): Rule?
    fun onRemoveCondition(rIndex: Int, cIndex: Int): Rule?
    fun onConditionClick(rIndex: Int, cIndex: Int): Rule?
    fun onSearchCondition(index: Int, query: String)
    fun onDismissSearch()
    fun onEditHours(rIndex: Int, hours: String): Rule?
}

class ConditionsState @Inject constructor(
    private val _data: IRulesStateData,
) : IConditionsState {


    override val searchResults: SnapshotStateList<Condition>
        get() = _data.searchResults

    //    override fun setConditions(rules: String) {
//        _data.conditions.add(parseRules(rules).toMutableStateList())
//    }
    override fun setConditions(id: Int, rules: String) {
        _data.conditions[id] = parseRules(rules).toMutableStateList()
    }

    override fun onSearchCondition(index: Int, str: String) {
        _data.searchResults.clear()
        val id: Int? = _data.rules[index].id
//        val conditions: List<Condition> = _rules[index].conditions
        val conditions = _data.conditions[id] ?: return
        val query = str.lowercase()
        _data.searchResults.addAll(
            listOf(
                MiscConditions.dayConditions.filter { it.startsWith(query) }
                    .map { c ->
                        Condition(
                            type = ConditionType.DAY,
                            name = "Day:$c"
                        )
                    },

                MiscConditions.employeeConditions.filter { it.startsWith(query) }
                    .map { c ->
                        Condition(
                            type = ConditionType.EMPLOYEE,
                            name = "Employee:$c"
                        )
                    },

                if (conditions.any { it.type.value == ConditionType.EMPLOYEE.value })
                    MiscConditions.hoursConditions.filter {
                        it.lowercase().startsWith(query)
                    }
                        .map {
                            Condition(
                                type = ConditionType.HOURS,
                                name = it,
                                operator = "<",
                                value = "40",
                            )
                        }
                else
                    emptyList<Condition>(),

                // search employees
                _data.employees.filter { it.name.lowercase().startsWith(query) }
                    .map { employee ->
                        Condition(
                            type = ConditionType.EMPLOYEE,
                            name = employee.name,
                            value = employee.employeeId,
                        )
                    },

                //search shifts: only allow one shift if an employee is present
                if (conditions.any { it.type.value == ConditionType.EMPLOYEE.value } &&
                    conditions.any { it.type.value == ConditionType.SHIFT.value })
                    emptyList()
                else
                    _data.shifts.filter { it.name.lowercase().startsWith(query) }
                        .map { shift ->
                            Condition(
                                type = ConditionType.SHIFT,
                                name = shift.name,
                                value = "${shift.id}"
                            )
                        },

                // search days
                Days.values().filter {
                    it.value.lowercase().startsWith(query)
                }.map { day ->
                    Condition(
                        type = ConditionType.DAY,
                        name = day.name,
                    )
                }

                // flatten results and remove conditions already present
            ).flatten()
                .filter { m ->
                    !(conditions.any { c ->
                        c.type.value != ConditionType.SHIFT.value &&
                                c.name == m.name &&
                                c.value == m.value
                    })
                }
        )
    }

    override fun onDismissSearch() {
        _data.searchResults.clear()
    }

    override fun getConditions(rIndex: Int): SnapshotStateList<Condition> {
        val id: Int? = _data.rules[rIndex].id
        return if (id != null)
            _data.conditions[id] ?: mutableStateListOf()
        else
            mutableStateListOf()
    }

    override fun onAddCondition(rIndex: Int, condition: Condition): Rule? {
        Log.d("ConditionsState", "onAddCondition: $condition")
        _data.searchResults.clear()
        try {
            val rule: Rule = _data.rules[rIndex]
            _data.conditions[rule.id]?.add(condition) ?: return null

//            val rule = _data.rules[rIndex]
            return rule.copy(
                priority = rIndex,
                rules = stringifyRules(_data.conditions[rIndex] ?: mutableStateListOf())
            )
        } catch (e: IndexOutOfBoundsException) {
            Log.e(
                "ConditionsState", "onAddCondition: invalid index: $rIndex," +
                        "size = ${_data.conditions.size}"
            )
            return null
        }
    }

    override fun onRemoveCondition(rIndex: Int, cIndex: Int): Rule? {
        try {
//            val id = _data.rules[rIndex].id
            val rule: Rule = _data.rules[rIndex]
            val conditions: SnapshotStateList<Condition> = _data.conditions[rule.id] ?: return null
            conditions.removeAt(cIndex)
            return rule.copy(
                priority = rIndex,
                rules = stringifyRules(conditions)
            )
        } catch (e: IndexOutOfBoundsException) {
            Log.e(
                "ConditionsState", "onRemoveCondition: invalid Index: index = $rIndex, " +
                        "cIndex = $cIndex"
            )
            return null
        }
    }

    override fun onEditHours(rIndex: Int, hours: String): Rule? {
        Log.d("ConditionsState", "onEditHours: hours = $hours")
        try {
            val rule: Rule = _data.rules[rIndex]
            val conditions = _data.conditions[rule.id] ?: return null
            val index: Int = conditions.indexOfFirst {
                it.type.value == ConditionType.HOURS.value
            }
            if (index == -1) return null
            conditions[index] = conditions[index].copy(value = hours)
            return rule.copy(
                priority = rIndex,
                rules = stringifyRules(conditions)
            )
        } catch (e: IndexOutOfBoundsException) {
            Log.d("ConditionsState", "onEditHours: invalid Index: index = $rIndex")
            return null
        }
    }

    override fun onConditionClick(rIndex: Int, cIndex: Int): Rule? {
        Log.d("RulesState", "onConditionClick")
        try {
            val rule: Rule = _data.rules[rIndex]
            val conditions: SnapshotStateList<Condition> =
                _data.conditions[rule.id] ?: mutableStateListOf()
            val condition: Condition = conditions[cIndex]
            when (condition.type.value) {
                ConditionType.EMPLOYEE.value, ConditionType.DAY.value -> {
                    if (condition.operator == null) {
                        conditions[cIndex] = condition.copy(operator = "NOT")
                    } else {
                        conditions[cIndex] = condition.copy(operator = null)
                    }

                    return rule.copy(
                        priority = rIndex,
                        rules = stringifyRules(conditions)
                    )
                }
                ConditionType.HOURS.value -> {
                    conditions[cIndex] = condition.copy(
                        operator = MiscConditions.hoursOperators[condition.operator]
                    )
                    return rule.copy(
                        priority = rIndex,
                        rules = stringifyRules(conditions)
                    )
                }
                else -> return null
            }
        } catch (e: IndexOutOfBoundsException) {
            Log.e(
                "RulesState", "onConditionClick: invalid index: index: id = $rIndex," +
                        "cIndex = $cIndex"
            )
            return null
        }
    }

    private fun stringifyRules(conditions: List<Condition>): String {
        Log.d("RulesState", "stringify rules")
        var rules: String = ""
        conditions.forEach { c ->
            var name: String = c.name
            if (name.contains(":"))
                name = name.split(":")[1]

            rules += c.type.value + ":"
            rules += if (c.operator != null) c.operator + "," else ""
            rules += c.value ?: name
            rules += ";"
        }

        Log.d("RulesState", "stringify rules: $rules")

        return rules
    }

    private fun parseRules(rules: String): List<Condition> {
        // rules are separated by a ; operators separated by , conditions separated by :
        val conditions = mutableListOf<Condition>()
        rules.split(';').filter { it.isNotBlank() }.forEach { ruleStr ->
            val properties = ruleStr.split(':')

            if (properties.size < 2) {
                Log.e("parseRules", "invalid properties: size = ${properties.size}")
                return@forEach
            }

            val type: String = properties[0].lowercase(Locale.ROOT)
            when (type) {
                ConditionType.HOURS.value -> conditions.add(parseHours(properties))
                ConditionType.EMPLOYEE.value -> {
                    val condition: Condition? = parseEmployee(properties)
                    if (condition != null)
                        conditions.add(condition)
                }
                ConditionType.SHIFT.value -> {
                    val condition: Condition? = parseShift(properties)
                    if (condition != null)
                        conditions.add(condition)
                }
                ConditionType.DAY.value -> conditions.add(parseDay(properties))
            }
        }
        return conditions
    }

    private fun parseHours(properties: List<String>): Condition {
        val type = ConditionType.HOURS
        val description: List<String> = properties[1].split(',')
        val value: String = if (description.size > 1) description[1] else description[0]
        val operator: String? = if (description.size > 1) description[0] else null

        return Condition(
            type = type,
            name = "Hours",
            operator = operator,
            value = value,
        )
    }

    private fun parseEmployee(properties: List<String>): Condition? {
        val type = ConditionType.EMPLOYEE
        val description: List<String> = properties[1].split(',')
        val value: String = if (description.size > 1) description[1] else description[0]
        val operator: String? =
            if (description.size > 1) description[0].uppercase(Locale.ROOT) else null

        val name: String = _data.employees.find { it.employeeId == value }?.name ?: return null

        return Condition(
            type = type,
            operator = operator,
            value = value,
            name = name,
        )
    }

    private fun parseShift(properties: List<String>): Condition? {
        val type = ConditionType.SHIFT
        val description: List<String> = properties[1].split(',')
        val value: String = if (description.size > 1) description[1] else description[0]

        val name: String = _data.shifts.find { it.id == value.toInt() }?.name ?: return null
        return Condition(
            type = type,
            value = value,
            name = name,
            operator = null,
        )
    }

    private fun parseDay(properties: List<String>): Condition {
        val type = ConditionType.DAY
        val description: List<String> = properties[1].split(',')
        val name = if (description.size > 1) description[1].trim() else description[0].trim()
        val operator = if (description.size > 1) description[0].lowercase(Locale.ROOT) else null

        return Condition(
            type = type,
            name = name,
            operator = operator,
            value = null
        )
    }

    companion object {
        val previewConditionsState = object : IConditionsViewState {
            override val searchResults = mutableStateListOf<Condition>()
            override fun getConditions(rIndex: Int): SnapshotStateList<Condition> {
                return mutableStateListOf()
            }
        }
    }
}