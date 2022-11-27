package com.love.schedule.screen.schedule

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.love.schedule.Data
import com.love.schedule.model.schedule.*
import com.love.schedule.shared.component.ShiftPopup

internal val rowPad: Dp = 50.dp

@Composable
fun ScheduleScreen(navController: NavController, vm: ScheduleViewModel) {
    LaunchedEffect(Unit, block = {
        Log.d("ScheduleScreen", "fetch employees")
        vm.fetchSchedules()
    })
    ScheduleList(schedules = vm.schedules, navController = navController, vm)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleList(schedules: List<Schedule>, navController: NavController, vm: IScheduleViewModel) {
    vm.actions.setBottomSheetScaffoldState(
        rememberBottomSheetScaffoldState(
            bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
        )
    )
    var selectedShift = vm.selectedShift

    ShiftPopup(
        data = vm.actions.popupData,
        onDismiss = { vm.actions.onDismissPopup() }
    )

    BottomSheetScaffold(
        sheetContent = {
            ScheduleBottomSheet(
                bsState = vm.actions.bottomSheetScaffoldState,
                vm = vm,
                onAddShift = { vm.actions.addShift { vm.addShift(it) } },
                onEditShift = { shift: Shift -> vm.actions.editShift(shift) { vm.editShift(it) } }
            )
        },
        scaffoldState = vm.actions.bottomSheetScaffoldState,
        sheetPeekHeight = ScheduleSheetConstants.PEEK_HEIGHT,
        sheetElevation = 200.dp,
        sheetGesturesEnabled = false,
        drawerGesturesEnabled = false,
    ) {
        SchedulePager(
            schedules = schedules,
            onShiftClick = { schedule, day ->
                vm.actions.onShiftClick(selectedShift, schedule, day,
                    onEditShift = { id, day, shift -> vm.editSchedule(id, day, shift) }
                )
            }
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SchedulePager(
    schedules: List<Schedule>,
    onShiftClick: (Schedule, Int) -> Unit,
) {
    Column {
        HorizontalPager(
            modifier = Modifier.padding(bottom = ScheduleSheetConstants.PEEK_HEIGHT),
            count = Data.days.size
        ) { day ->
            Column(
                modifier = Modifier.weight(1f)
            ) {
                DayTitle(day)
                Divider()
//                Column(
//                    modifier = Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.spacedBy(10.dp)
//                ) {
                EmployeeSchedules(schedules, day, onShiftClick = onShiftClick)
//                }
            }
        }
    }
}

@Composable
fun DayTitle(day: Int) {
    Row(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = Data.days[day],
            fontSize = MaterialTheme.typography.h4.fontSize,
            fontStyle = MaterialTheme.typography.h4.fontStyle
        )
    }
}

@Composable
fun EmployeeSchedules(schedules: List<Schedule>, day: Int, onShiftClick: (Schedule, Int) -> Unit) {
    LazyColumn(
//        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        items(schedules) { s ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val fontSize = MaterialTheme.typography.h6.fontSize
                val fontStyle = MaterialTheme.typography.h6.fontStyle
                EmployeeName(s.name, fontSize, fontStyle)
                Shift(s, day, fontSize, fontStyle, onShiftClick)
            }
            Divider()
        }
    }
}

@Composable
fun RowScope.EmployeeName(name: String, fontSize: TextUnit, fontStyle: FontStyle?) {
    Text(
        modifier = Modifier
            .weight(1f)
            .padding(start = rowPad),
        text = name,
        fontSize = fontSize,
        fontStyle = fontStyle,
    )
}

@Composable
fun RowScope.Shift(
    schedule: Schedule,
    day: Int,
    fontSize: TextUnit,
    fontStyle: FontStyle?,
    onClick: (Schedule, Int) -> Unit,
//    showPopup: (Schedule, Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .weight(1f)
            .padding(end = rowPad),
        horizontalArrangement = Arrangement.End,
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(1f),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
            onClick = {
                Log.d("Shift", "onClick")
                onClick(schedule, day)
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var start = schedule.shifts[day].start
                var end = schedule.shifts[day].end
                if (start != null && end != null) {
                    Text(text = start, fontSize = fontSize, fontStyle = fontStyle)
                    Text(text = end, fontSize = fontSize, fontStyle = fontStyle)
                } else {
                    Text(text = "None", fontSize = fontSize, fontStyle = fontStyle)
                }
            }
        }
    }
}

@Composable
fun SaveButton(
    navController: NavController,
    vm: IScheduleViewModel
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            Log.d("ScheduleSaveButton", "save")
        }) {
        Text("Save")
    }
}

@Composable
@Preview(showBackground = false)
fun ScheduleScreenPreview() {
    ScheduleList(
        schedules = Data.schedules,
        navController = rememberNavController(),
        vm = ScheduleViewModel.previewViewModel
    )
}