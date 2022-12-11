package com.love.schedule.feature_rules.domain.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Rule(
    @PrimaryKey val id: Int? = null,
    val name: String,
    val priority: Int,
    val status: Boolean,
    val rules: String,
)
//    @Ignore
//    var conditions: SnapshotStateList<Condition> = mutableStateListOf()
//) {
//    constructor(id: Int?, name: String, priority: Int, status: Boolean, rules:String):
//            this(id = id, name = name, priority = priority,
//                status = status, rules = rules, conditions = mutableStateListOf<Condition>()
//            )
//}

enum class ConditionType(val value: String) {
    SHIFT("shift"),
    DAY("day"),
    EMPLOYEE("employee"),
    HOURS("hours")
}

data class Condition(
    val name: String,
    val type: ConditionType,
    val operator: String? = null,
    val value: String? = null,
)
