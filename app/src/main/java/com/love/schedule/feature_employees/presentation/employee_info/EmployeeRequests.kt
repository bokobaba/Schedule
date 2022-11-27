package com.love.schedule.feature_employees.presentation.employee_info

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.love.schedule.feature_employees.domain.model.EmployeeRequest
import com.love.schedule.feature_employees.presentation.employee_info.view_model.IRequestsState
import com.love.schedule.feature_employees.presentation.employee_info.view_model.RequestsState
import java.time.LocalDate
import java.time.format.DateTimeParseException

@Composable
fun EmployeeRequests(state: IRequestsState) {
    Log.d("EmployeeRequests", "init")
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AddRequestButton(onClick = state::addRequest)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
//                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(state.requests) { index, request ->
                RequestItem(
                    modifier = Modifier
                        .fillMaxWidth(),
//                        .padding(10.dp),
                    index = index,
                    request = request,
                    onDescriptionChange = state::editDescription,
                    onStartChange = state::editStart,
                    onEndChange = state::editEnd,
                )
                Divider()
            }
        }
    }
}

@Composable
fun RequestItem(
    modifier: Modifier = Modifier,
    index: Int,
    request: EmployeeRequest,
    onDescriptionChange: (Int, String) -> Unit,
    onStartChange: (Int, String) -> Unit,
    onEndChange: (Int, String) -> Unit,
) {
    Log.d("RequestItem", "init")
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        OutlinedTextField(
            value = request.description,
            label = { Text("description") },
            onValueChange = { onDescriptionChange(index, it) }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            RequestDate(
                modifier = Modifier.weight(1f),
                index = index,
                label = "Start:",
                value = request.start,
                onClick = onStartChange
            )
            RequestDate(
                modifier = Modifier.weight(1f),
                index = index,
                label = "End:",
                value = request.end,
                onClick = onEndChange
            )
        }
    }
}

@Composable
fun RequestDate(
    modifier: Modifier = Modifier,
    index: Int,
    label: String,
    value: String,
    onClick: (Int, String) -> Unit,
) {
    Log.d("RequestDateRange", label)
    var validDate: Boolean = true
    var date: LocalDate = try {
        LocalDate.parse(value)
    } catch (ex: DateTimeParseException) {
        validDate = false
        LocalDate.now()
    }
    Log.d(
        "RequestDateRange",
        "year = ${date.year}, month = ${date.monthValue}, day = ${date.dayOfMonth}"
    )
    val context = LocalContext.current
    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, y: Int, m: Int, d: Int ->
            onClick(index, "$y-$m-$d")
        },
        date.year, date.monthValue - 1, date.dayOfMonth
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable {
            datePicker.show()
        }
    ) {
        Text(text = label, style = MaterialTheme.typography.h6)
        if (validDate) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = date.toString(),
                style = MaterialTheme.typography.h5
            )
        } else {
            Icon(
                modifier = Modifier.fillMaxWidth(),
                imageVector = Icons.Default.DateRange,
                contentDescription = "date picker"
            )
        }
    }
}

@Composable
fun AddRequestButton(onClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Text("Add New Request")
    }
}

@Composable
@Preview
fun EmployeeRequestsPreview() {
    EmployeeRequests(RequestsState.previewRequestsState)
}