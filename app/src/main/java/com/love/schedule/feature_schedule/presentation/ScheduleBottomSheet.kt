package com.love.schedule.feature_schedule.presentation

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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.love.schedule.R
import com.love.schedule.core.component.LoadingAnimation
import com.love.schedule.feature_schedule.domain.model.Shift
import com.love.schedule.feature_schedule.presentation.view_model.IScheduleViewState
import com.love.schedule.feature_schedule.presentation.view_model.ScheduleEvent
import com.love.schedule.feature_schedule.presentation.view_model.ScheduleState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object ScheduleSheetConstants {
    val PEEK_HEIGHT = 50.dp
    val ACTION_HEIGHT = 40.dp
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleBottomSheet(
    state: IScheduleViewState,
    shiftsLoading: MutableState<Boolean> = mutableStateOf(false),
    onEvent: (ScheduleEvent) -> Unit,
) {
    val maxHeight = LocalConfiguration.current.screenHeightDp.dp / 2

    Column(
        modifier = Modifier.heightIn(min = 0.dp, max = maxHeight)
    ) {
        SheetHandle(
            bsState = state.bottomSheetScaffoldState,
            selectedShift = state.selectedShift,
            onEvent = onEvent,
        )
        if (shiftsLoading.value) {
            LoadingAnimation(modifier = Modifier.fillMaxSize())
        } else {
            ColumnHeaders()
            Divider()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(5f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(state.shifts) { shift ->
                    ShiftRow(shift = shift, onEvent = onEvent)
                    Divider()
                }
            }
            if (state.selectedShift.value == null) {
                ShiftActionButton(
                    text = "Add New Shift",
                    onClick = { onEvent(ScheduleEvent.AddShift) }
                )
            } else {
                ShiftActionButton(
                    text = "Edit Shift",
                    onClick = { onEvent(ScheduleEvent.EditShift) }
                )
            }
        }
    }
}

@Composable
fun ShiftRow(
    shift: Shift,
    onEvent: (ScheduleEvent) -> Unit,
) {
    Log.d("ShiftRow", shift.name)
    Row(
        modifier = Modifier.clickable {
            onEvent(ScheduleEvent.ShiftClick(shift))
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
    selectedShift: MutableState<Shift?>,
    onEvent: (ScheduleEvent) -> Unit,
    ) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ScheduleSheetConstants.PEEK_HEIGHT),
        contentAlignment = Alignment.BottomEnd
    ) {
        ExpandButton(
            bsState = bsState,
            selectedShift = selectedShift
        )
        CancelSelectButton(selectedShift = selectedShift, onEvent = onEvent)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExpandButton(
    bsState: BottomSheetScaffoldState,
    selectedShift: MutableState<Shift?>
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
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
            text = selectedShift.value?.name ?: "Shifts",
            fontSize = MaterialTheme.typography.h5.fontSize
        )
    }
}

@Composable
fun CancelSelectButton(selectedShift: MutableState<Shift?>, onEvent: (ScheduleEvent) -> Unit) {
    if (selectedShift.value == null) return
    Button(
        modifier = Modifier.fillMaxHeight(),
        onClick = { onEvent(ScheduleEvent.CancelSelect) }
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
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        onClick = onClick
    ) {
        Text(text = text)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun ScheduleBottomSheetPreview() {
    ScheduleState.previewScheduleState.setBottomSheetScaffoldState(
        rememberBottomSheetScaffoldState())
    ScheduleBottomSheet(
        state = ScheduleState.previewScheduleState,
        onEvent = {},
    )
}