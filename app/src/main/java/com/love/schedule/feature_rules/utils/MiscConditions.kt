package com.love.schedule.feature_rules.utils

object MiscConditions {
    val dayConditions: List<String> = listOf("All")
    val employeeConditions: List<String> = listOf("All")
    val hoursConditions: List<String> = listOf("Hours")
    val hoursOperators: Map<String, String> = mapOf(">" to "<", "<" to ">")
}