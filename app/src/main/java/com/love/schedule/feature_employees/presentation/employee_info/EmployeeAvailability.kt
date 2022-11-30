package com.love.schedule.feature_employees.presentation.employee_info

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.love.schedule.feature_employees.presentation.employee_info.view_model.AvailabilityState
import com.love.schedule.feature_employees.presentation.employee_info.view_model.IAvailabilityState
import com.love.schedule.core.component.ShiftPopup
import com.love.schedule.core.util.Days

val HEADER_HEIGHT = 50.dp

@Composable
fun EmployeeAvailability(state: IAvailabilityState) {
    Log.d("EmployeeAvailability", "init")
    LazyColumn() {
        items(state.availability) { availability ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { },
            ) {
                InfoPrimary(
                    day = availability.day,
                    allDay = availability.allDay,
                    enabled = availability.enabled,
                    onAllDayChange = state::editAllDay,
                    onEnabledChange = state::editEnabled
                )
                AnimatedVisibility(
                    visible = availability.enabled && !availability.allDay,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    InfoSecondary(
                        day = availability.day,
                        start = availability.start,
                        end = availability.end,
                        onClick = state::setPopupData
                    )
                }
            }
        }
    }
    ShiftPopup(popupData = state.popupData, state::onDismissPopup)
}

@Composable
fun InfoPrimary(
    day: Int,
    allDay: Boolean,
    enabled: Boolean,
    onEnabledChange: (Int, Boolean) -> Unit,
    onAllDayChange: (Int, Boolean) -> Unit,
) {
    Log.d("InfoPrimary", "day = $day")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(HEADER_HEIGHT),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LabelledCheckbox(
            day = day,
            modifier = Modifier.weight(1f),
            enabled = enabled,
            text = "All Day",
            checked = allDay,
            onCheckChange = onAllDayChange
        )
        Text(
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            text = Days.get(day),
            fontSize = MaterialTheme.typography.h5.fontSize,
        )
        LabelledCheckbox(
            day = day,
            modifier = Modifier.weight(1f),
            text = "Available",
            checked = enabled,
            onCheckChange = onEnabledChange
        )
    }
}

@Composable
fun InfoSecondary(
    day: Int,
    start: String,
    end: String,
    onClick: (Int, String, String) -> Unit
) {
    Log.d("InfoSecondary", "init")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(day, start, end)
            }
    ) {
        Text(
            textAlign = TextAlign.Right,
            modifier = Modifier
                .weight(1f)
                .padding(end = 50.dp),
            text = "start: $start"
        )
        Text(
            textAlign = TextAlign.Left,
            modifier = Modifier
                .weight(1f)
                .padding(start = 50.dp),
            text = "end: $end"
        )
    }
}

@Composable
fun LabelledCheckbox(
    day: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    text: String,
    checked: Boolean,
    onCheckChange: (Int, Boolean) -> Unit
) {
    Log.d("Labelled Checkbox", "$text: $checked")
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text)
        Checkbox(checked = checked, onCheckedChange = { onCheckChange(day, it) }, enabled = enabled)
    }
}

@Preview
@Composable
fun EmployeeAvailabilityPreview() {
    EmployeeAvailability(AvailabilityState.previewState)
}