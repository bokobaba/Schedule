package com.love.schedule.feature_schedule.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Shift(
    @PrimaryKey val id: Int? = null,
    val name: String,
    val start: String,
    val end: String,
)
