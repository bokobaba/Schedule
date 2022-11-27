package com.love.schedule.shared.component

import android.util.Log
import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.google.gson.Gson
import java.time.LocalDateTime

data class ShiftPopupData(
    val editName: Boolean = false,
    val name: String,
    val start: String?,
    val end: String?,
    val onSaveData: (name: String, start: String, end: String) -> Unit
)

fun timeString(strings: List<String>): String {
    return "${strings[0]}:${strings[1]}"
}

internal const val nameMaxLength = 20

@Composable
fun ShiftPopup(data: ShiftPopupData?, onDismiss: () -> Unit) {
    Log.d("ShiftPopup", "init")
    if (data == null) return

    Log.d("ShiftPopup", "data: ${data.start}, ${data.end}")
    val dataStart = if (data.start == null) listOf("0", "0") else data.start!!.split(":")
    val dataEnd = if (data.end == null) listOf("0", "0") else data.end!!.split(":")

    if (dataStart.size < 2 || dataEnd.size < 2) {
        Log.e("ShiftPopup", "invalid time string")
        return
    }

    var name by remember { mutableStateOf(data.name) }
    val start: MutableList<String> by remember { mutableStateOf(dataStart.toMutableList()) }
    val end: MutableList<String> by remember { mutableStateOf(dataEnd.toMutableList()) }

    val onNameChange = { str: String -> name = str.take(nameMaxLength) }

    Popup(
        alignment = Alignment.Center,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
        onDismissRequest = onDismiss
    ) {
        PopupContent(
            editName = data.editName,
            name = name,
            start = start, end = end,
            onNameChange = onNameChange,
            onDismiss = onDismiss,
            onSaveData = { data.onSaveData(name, timeString(start), timeString(end)) }
        )
    }
}

@Composable
fun PopupContent(
    editName: Boolean,
    name: String,
    start: MutableList<String>,
    end: MutableList<String>,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSaveData: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .background(Color.DarkGray, RoundedCornerShape(16.dp))
            .padding(10.dp),
    ) {
        NameField(edit = editName, name = name, onNameChange = onNameChange)
        StartEndSelector(start = start, end = end)
        ActionButtons(onDismiss = onDismiss, onSaveData = onSaveData)
    }
}

@Composable
fun NameField(edit: Boolean, name: String, onNameChange: (String) -> Unit) {
    if (edit)
        TextField(
            label = { Text("name") },
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = onNameChange
        )
    else
        Text(
            text = name,
            fontSize = MaterialTheme.typography.h5.fontSize,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
}

@Composable
fun StartEndSelector(start: MutableList<String>, end: MutableList<String>) {
    Row(
        modifier = Modifier.height(150.dp).padding(top = 5.dp, bottom = 5.dp)
    ) {
        TimePicker(
            title = "Start",
            time = start,
            onHourChange = { start[0] = it },
            onMinuteChange = { start[1] = it }

        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
        )
        TimePicker(
            title = "End",
            time = end,
            onHourChange = { end[0] = it },
            onMinuteChange = { end[1] = it }
        )
    }
}

@Composable
fun ActionButtons(
    onDismiss: () -> Unit,
    onSaveData: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = onDismiss) {
            Text(text = "Cancel")
        }
        Button(onClick = onSaveData) {
            Text(text = "OK")
        }
    }
}

@Composable
fun RowScope.TimePicker(
    title: String,
    time: List<String>,
    onHourChange: (String) -> Unit,
    onMinuteChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().background(Color.DarkGray),
            textAlign = TextAlign.Center,
            text = title,
            color = Color.Black)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            NumberPicker(time[0].toIntOrNull() ?: 0, 23, onHourChange)
            Text(text = ":", fontSize = 40.sp, color = Color.Black)
            NumberPicker(time[1].toIntOrNull() ?: 0, 59, onMinuteChange)
        }
    }
}

@Composable
fun RowScope.NumberPicker(default: Int, max: Int, onValueChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .weight(1f)
    ) {
        AndroidView(
            modifier = Modifier.width(50.dp),
            factory = { context ->
                NumberPicker(context).apply {
                    setOnValueChangedListener { numberPicker, previous, current ->
                        onValueChange(String.format("%02d", current))
                    }
                    setFormatter {
                        String.format("%02d", it)
                    }
                    minValue = 0
                    maxValue = max
                    value = default
                }
            }
        )
    }
}

@Preview(showBackground = false)
@Composable
fun ShiftPopupPreview() {
    PopupContent(
        editName = true,
        name = "shift name",
        start = mutableListOf("12", "30"),
        end = mutableListOf("09", "00"),
        onNameChange = {},
        onDismiss = {},
        onSaveData = {},
    )
}