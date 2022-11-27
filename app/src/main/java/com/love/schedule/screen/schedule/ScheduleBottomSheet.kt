package com.love.schedule.screen.schedule

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.love.schedule.R
import com.love.schedule.model.schedule.IScheduleViewModel
import com.love.schedule.model.schedule.ScheduleViewModel
import com.love.schedule.model.schedule.Shift
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object ScheduleSheetConstants {
    val PEEK_HEIGHT = 50.dp
    val ACTION_HEIGHT = 40.dp
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleBottomSheet(
    bsState: BottomSheetScaffoldState,
    vm: IScheduleViewModel,
    onAddShift: () -> Unit,
    onEditShift: (Shift) -> Unit,
) {
    LaunchedEffect(Unit, block = {
        Log.d("ScheduleScreen", "fetch employees")
        vm.fetchShifts()
    })
    ScheduleBottomSheetContent(
        bsState = bsState,
        vm = vm,
        onAddShift = onAddShift,
        onEditShift = onEditShift,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleBottomSheetContent(
    bsState: BottomSheetScaffoldState,
    vm: IScheduleViewModel,
    onAddShift: () -> Unit,
    onEditShift: (Shift) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedShift: Shift? = vm.selectedShift
    val onClickRow = { shift: Shift -> vm.selectShift(shift) }
    val onCancel = { vm.deselectShift() }
    val maxHeight = LocalConfiguration.current.screenHeightDp.dp / 2
    val listHeight = maxHeight - ScheduleSheetConstants.ACTION_HEIGHT - ScheduleSheetConstants.PEEK_HEIGHT
    Column(
        modifier = Modifier.heightIn(min = 0.dp, max = maxHeight)
    ) {
        SheetHandle(
            bsState = bsState,
            coroutineScope = coroutineScope,
            selectedShift = selectedShift,
            onCancel = onCancel
        )
        ColumnHeaders()
        Divider()
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(5f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(vm.shifts) { shift ->
                ShiftRow(shift, onClick = onClickRow)
                Divider()
            }
        }
        if (selectedShift == null) {
            ShiftActionButton("Add New Shift", onAddShift)
        } else {
            ShiftActionButton(text = "Edit Shift", onClick = { onEditShift(selectedShift) })
        }
    }
}

@Composable
fun ShiftRow(
    shift: Shift,
    onClick: (Shift) -> Unit,
) {
    Row(
        modifier = Modifier.clickable {
            Log.d("ShiftRow", "selected: ${Gson().toJson(shift)}")
            onClick.invoke(shift)
        }
    ) {
        val fontSize = MaterialTheme.typography.h5.fontSize
        val fontStyle = MaterialTheme.typography.h5.fontStyle
        Cell(shift.name, fontSize, fontStyle)
        Cell(shift.start, fontSize, fontStyle)
        Cell(shift.end, fontSize, fontStyle)
    }
}

@Composable
fun ColumnHeaders() {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val fontSize = MaterialTheme.typography.h6.fontSize
        val fontStyle = MaterialTheme.typography.h6.fontStyle
        Cell("Name", fontSize, fontStyle)
        Cell("Start", fontSize, fontStyle)
        Cell("End", fontSize, fontStyle)
    }
}

@Composable
fun RowScope.Cell(text: String, fontSize: TextUnit, fontStyle: FontStyle?) {
    Text(
        modifier = Modifier.weight(1f),
        text = text,
        textAlign = TextAlign.Center,
        fontSize = fontSize,
        fontStyle = fontStyle
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SheetHandle(
    bsState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope,
    selectedShift: Shift?,
    onCancel: () -> Unit,
    ) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ScheduleSheetConstants.PEEK_HEIGHT),
        contentAlignment = Alignment.BottomEnd
    ) {
        ExpandButton(
            bsState = bsState,
            coroutineScope = coroutineScope,
            selectedShift = selectedShift
        )
        CancelSelectButton(selectedShift = selectedShift, onCancel)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExpandButton(
    bsState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope,
    selectedShift: Shift?,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        onClick = {
            coroutineScope.launch {
                bsState.bottomSheetState.apply {
                    if (isCollapsed) expand() else collapse()
                }
            }
        }) {
        Text(
            text = selectedShift?.name ?: "Shifts",
            fontSize = MaterialTheme.typography.h5.fontSize
        )
    }
}

@Composable
fun CancelSelectButton(selectedShift: Shift?, onCancel: () -> Unit) {
    if (selectedShift == null) return
    Button(
        modifier = Modifier.fillMaxHeight(),
        onClick = onCancel
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_shift_select_cancel),
            contentDescription = "Cancel Shift Selection"
        )
    }
}

@Composable
fun ColumnScope.ShiftActionButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth().weight(1f),
        onClick = onClick
    ) {
        Text(text = text)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun ScheduleBottomSheetPreview() {
    ScheduleBottomSheetContent(
        bsState = rememberBottomSheetScaffoldState(),
        vm = ScheduleViewModel.previewViewModel,
        onAddShift = {},
        onEditShift = {},
    )
}