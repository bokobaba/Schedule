package com.love.schedule.model.schedule

import com.love.schedule.Day
import java.util.*

data class Schedule(
    val id: UUID,
    val year: Int,
    val week: Int,
    val employeeId: String,
    val name: String,
//    val monday: TimeRange,
//    val tuesday: TimeRange,
//    val wednesday: TimeRange,
//    val thursday: TimeRange,
//    val friday: TimeRange,
//    val saturday: TimeRange,
//    val sunday: TimeRange,
    val shifts: MutableList<TimeRange> = (1..7).map { TimeRange(null, null) }.toMutableList()
) {
//    fun shift(day: Day) : TimeRange {
//        return when(day) {
//            Day.MONDAY -> {
//                monday
//            }
//            Day.TUESDAY -> {
//                tuesday
//            }
//            Day.WEDNESDAY -> {
//                wednesday
//            }
//            Day.THURSDAY -> {
//                thursday
//            }
//            Day.FRIDAY -> {
//                friday
//            }
//            Day.SATURDAY -> {
//                saturday
//            }
//            Day.SUNDAY -> {
//                sunday
//            }
//        }
//    }
}

data class TimeRange(
    var start: String?,
    var end: String?,
)
