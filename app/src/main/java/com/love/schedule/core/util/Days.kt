package com.love.schedule.core.util

enum class Days(val value: String) {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY( "Thursday"),
    FRIDAY( "Friday"),
    SATURDAY( "Saturday"),
    SUNDAY( "Sunday");

    companion object {
        private val types = values().associate { it.ordinal to it.value }

        fun get(value: Int): String = types[value] ?: error("invalid index")
    }
}