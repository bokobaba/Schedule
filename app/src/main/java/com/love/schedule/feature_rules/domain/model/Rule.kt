package com.love.schedule.feature_rules.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Rule(
    @PrimaryKey val id: Int?,
    val name: String,
    val priority: Int,
    val status: Boolean,
    val rules: String,
)
