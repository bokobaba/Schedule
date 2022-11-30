package com.love.schedule.feature_schedule.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Schedule(
    @PrimaryKey val id: Int? = null,
    val year: Int,
    val week: Int,
    val day: Int,
    val employeeId: String,
    val employeeName: String,
    val start: String,
    val end: String,
//    val monday: TimeRange,
//    val tuesday: TimeRange,
//    val wednesday: TimeRange,
//    val thursday: TimeRange,
//    val friday: TimeRange,
//    val saturday: TimeRange,
//    val sunday: TimeRange,
//    val shifts: MutableList<TimeRange> = (1..7).map { TimeRange(null, null) }.toMutableList()
)
//{
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
//}

data class TimeRange(
    var start: String?,
    var end: String?,
)
