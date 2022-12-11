package com.love.schedule.feature_schedule.presentation

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.love.schedule.core.component.ShiftPopup
import com.love.schedule.core.util.Days
import com.love.schedule.feature_schedule.presentation.view_model.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.pager.rememberPagerState
import com.love.schedule.core.component.AnimatedShimmer
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

object ScheduleScreenConstants {
    val sidePad: Dp = 30.dp
    val rowPad: Dp = 10.dp
}

@Composable
fun ScheduleScreen(navController: NavController, vm: ScheduleViewModel) {
    ScheduleList(
        onEvent = vm::onEvent,
        state = vm.state,
        schedulesLoading = vm.schedulesLoading,
        shiftsLoading = vm.shiftsLoading,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleList(
    onEvent: (ScheduleEvent) -> Unit,
    state: IScheduleViewState,
    schedulesLoading: MutableState<Boolean> = mutableStateOf(false),
    shiftsLoading: MutableState<Boolean> = mutableStateOf(false),
) {
    state.setBottomSheetScaffoldState(
        rememberBottomSheetScaffoldState(
            bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
        )
    )

    ShiftPopup(
        popupData = state.popupData,
        onDismiss = { onEvent(ScheduleEvent.DismissPopup) }
    )

    BottomSheetScaffold(
        sheetContent = {
            ScheduleBottomSheet(
                shiftsLoading = shiftsLoading,
                state = state,
                onEvent = onEvent,
            )
        },
        scaffoldState = state.bottomSheetScaffoldState,
        sheetPeekHeight = ScheduleSheetConstants.PEEK_HEIGHT,
        sheetElevation = 200.dp,
        sheetGesturesEnabled = false,
        drawerGesturesEnabled = false,
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            WeekButton(week = state.week, year = state.year, onEvent = onEvent)
            Divider()
            SchedulePager(
                loading = schedulesLoading,
                schedulesForDay = state::scheduleForDay,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
fun WeekButton(
    year: MutableState<Int>,
    week: MutableState<Int>,
    onEvent: (ScheduleEvent) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val context = LocalContext.current
    val weekFields: WeekFields = WeekFields.of(Locale.getDefault())
    val date: LocalDate = if (year.value > 0 && week.value > 0) {
        LocalDate.now()
            .with(weekFields.weekBasedYear(),  year.value.toLong())
            .with(weekFields.weekOfYear(), week.value.toLong())
            .with(weekFields.dayOfWeek(), 2)
        // day of week starts on sunday
    } else
        LocalDate.now()
            .with(weekFields.dayOfWeek(), 2)

    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, y: Int, m: Int, d: Int ->
            onEvent(ScheduleEvent.SelectWeek(y, m, d))
        },
        date.year,
        date.monthValue - 1,
        date.dayOfMonth,
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date.format(formatter)
        )
        Button(
            onClick = { datePicker.show() }
        ) {
            Text(text = "Select Week")
        }
        Text(
            text = date.plusDays(6).format(formatter)
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SchedulePager(
    onEvent: (ScheduleEvent) -> Unit,
    loading: MutableState<Boolean>,
    schedulesForDay: (Int) -> SnapshotStateList<EmployeeShift>,
) {
    val pagerState = rememberPagerState()
    Log.d("SchedulePager", "init")
    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = ScheduleSheetConstants.PEEK_HEIGHT),
            verticalAlignment = Alignment.Top,
            count = Days.values().size,
            state = pagerState,
        ) { day ->
//            val day by remember { mutableStateOf(page) }
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                DayTitle(day)
                Divider()
                if (loading.value) {
                    AnimatedShimmer()
                } else {
                    EmployeeSchedules(
                        schedulesForDay = schedulesForDay,
                        day = day,
                        onEvent = onEvent,
                    )
                }
            }
        }
    }
}

@Composable
fun DayTitle(day: Int) {
    Log.d("DayTitle", "day = $day")
    Row(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = Days.get(day),
            fontSize = MaterialTheme.typography.h4.fontSize,
            fontStyle = MaterialTheme.typography.h4.fontStyle
        )
    }
}

@Composable
fun EmployeeSchedules(
    schedulesForDay: (Int) -> SnapshotStateList<EmployeeShift>,
    day: Int,
    onEvent: (ScheduleEvent) -> Unit,
) {
    val list = schedulesForDay(day)
    Log.d("EmployeeSchedules", "init")
    LazyColumn(
        modifier = Modifier.fillMaxHeight()
    ) {
        itemsIndexed(list) { i, s ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ScheduleScreenConstants.rowPad),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EmployeeName(s.employeeName)
                Spacer(modifier = Modifier.width(3.dp))
                ScheduleShift(
                    start = s.start,
                    end = s.end,
                    day = day,
                    index = i,
                    onEvent = onEvent,
                )
            }
            Divider()
        }
    }
}

@Composable
fun RowScope.EmployeeName(
    name: String,
    style: TextStyle = MaterialTheme.typography.h6,
) {
    Log.d("EmployeeName", name)
    Text(
        modifier = Modifier
            .weight(1f)
            .padding(start = ScheduleScreenConstants.sidePad),
        text = name,
        style = style,
        maxLines = 1,
    )
}

@Composable
fun RowScope.ScheduleShift(
    start: String?,
    end: String?,
    day: Int = 0,
    index: Int = 0,
    style: TextStyle = MaterialTheme.typography.h6,
    onEvent: (ScheduleEvent) -> Unit,
) {
    Log.d("ScheduleShift", "init")
    Row(
        modifier = Modifier
            .weight(1f)
            .padding(end = ScheduleScreenConstants.sidePad),
        horizontalArrangement = Arrangement.End,
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(1f),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
            onClick = {
                onEvent(ScheduleEvent.ScheduleClick(day = day, index = index))
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (start != null && end != null) {
                    Text(text = start, style = style)
                    Text(text = end, style = style)
                } else {
                    Text(text = "None", style = style)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = false)
fun ScheduleScreenPreview() {
    ScheduleList(
        onEvent = {},
        state = ScheduleState.previewScheduleState,
    )
}