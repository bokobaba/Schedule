package com.love.schedule.feature_schedule.presentation

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.love.schedule.core.component.LoadingAnimation
import com.love.schedule.core.component.ShiftPopup
import com.love.schedule.core.util.Days
import com.love.schedule.feature_schedule.presentation.view_model.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import com.love.schedule.core.component.AnimatedShimmer

object ScheduleScreenConstants {
    val sidePad: Dp = 30.dp
    val rowPad: Dp = 10.dp
}

@Composable
fun ScheduleScreen(navController: NavController, vm: ScheduleViewModel) {
    ScheduleList(vm.state, vm.schedulesLoading, vm.shiftsLoading)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleList(
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
        onDismiss = state::dismissPopup
    )

    BottomSheetScaffold(
        sheetContent = {
            ScheduleBottomSheet(
                shiftsLoading = shiftsLoading,
                state = state
            )
        },
        scaffoldState = state.bottomSheetScaffoldState,
        sheetPeekHeight = ScheduleSheetConstants.PEEK_HEIGHT,
        sheetElevation = 200.dp,
        sheetGesturesEnabled = false,
        drawerGesturesEnabled = false,
    ) {
        SchedulePager(
            loading = schedulesLoading,
            schedulesForDay = state::scheduleForDay,
            onScheduleClick = state::onScheduleClick
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SchedulePager(
    loading: MutableState<Boolean>,
    schedulesForDay: (Int) -> SnapshotStateList<EmployeeShift>,
    onScheduleClick: (Int, Int) -> Unit,
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
                        onScheduleShiftClick = onScheduleClick
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
    onScheduleShiftClick: (Int, Int) -> Unit
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
                    onClick = onScheduleShiftClick
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
        style = style
    )
}

@Composable
fun RowScope.ScheduleShift(
    start: String?,
    end: String?,
    day: Int = 0,
    index: Int = 0,
    style: TextStyle = MaterialTheme.typography.h6,
    onClick: (Int, Int) -> Unit
) {
    Log.d("ScheduleShift", "init index = $index")
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
                onClick(day, index)
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
fun SaveButton(
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Text("Save")
    }
}

@Composable
@Preview(showBackground = false)
fun ScheduleScreenPreview() {
    ScheduleList(
        state = ScheduleState.previewScheduleState,
    )
}