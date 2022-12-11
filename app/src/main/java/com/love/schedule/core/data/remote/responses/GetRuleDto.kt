package com.love.schedule.core.data.remote.responses

data class GetRuleDto(
    val name: String,
    val priority: Int,
    val rules: String,
    val status: Boolean
)