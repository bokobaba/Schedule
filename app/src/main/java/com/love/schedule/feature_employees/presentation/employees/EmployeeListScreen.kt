package com.love.schedule.feature_employees.presentation.employees

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.love.schedule.core.component.*
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.model.employee.EmployeesState
import com.love.schedule.model.employee.IEmployeesViewState
import com.love.schedule.navigation.Screen
import com.love.schedule.ui.theme.ScheduleTheme
import kotlinx.coroutines.flow.collectLatest

internal val spacing: Dp = 10.dp

@Composable
fun EmployeeListScreen(
    navController: NavController,
    vm: EmployeesViewModel = hiltViewModel()
) {
    EmployeeList(
        loading = vm.loading,
        state = vm.state,
        onEvent = vm::onEvent

    )

    LaunchedEffect(key1 = true) {
        vm.eventFlow.collectLatest { event ->
            when (event) {
                is EmployeesViewModel.UiEvent.SelectEmployee -> {
                    navController.navigate(
                        route = Screen.EmployeeInfo.passEmployee("${event.employee.id}")
                    )
                }
                is EmployeesViewModel.UiEvent.AddEmployee -> {
                    navController.navigate(route = Screen.EmployeeInfo.route)
                }
            }
        }
    }
}

@Composable
fun EmployeeList(
    loading: MutableState<Boolean> = mutableStateOf(false),
    state: IEmployeesViewState,
    onEvent: (EmployeeListEvent) -> Unit,
) {
    Log.d("EmployeeList", "init")
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    CancelDeleteButton(
                        visible = state.employeeToDelete.value != null,
                        onClick = { onEvent(EmployeeListEvent.CancelDelete) }
                    )
                },
                title = { Text("Employees") },
                actions = {
                    DeleteButton(
                        visible = state.employeeToDelete.value != null,
                        onDelete = { onEvent(EmployeeListEvent.DeleteEmployee) }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(EmployeeListEvent.AddEmployee) },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add employee")
            }
        },
        scaffoldState = scaffoldState
    ) { padding ->
        if (loading.value) {
            LoadingAnimation()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(spacing),
            ) {
                items(state.employees) { employee ->
                    EmployeeItem(
                        employee = employee,
                        onEvent = onEvent,
                        employeeToDelete = state.employeeToDelete
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun EmployeeItem(
    employee: Employee,
    employeeToDelete: MutableState<Employee?>,
    onEvent: (EmployeeListEvent) -> Unit,
) {
    Log.d("EmployeeItem", "name = ${employee.name}")
    val markedForDelete = employeeToDelete.value?.employeeId == employee.employeeId
    val modifier: Modifier = if (markedForDelete)
        Modifier.background(MaterialTheme.colors.secondary)
    else
        Modifier
    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onEvent(EmployeeListEvent.SelectEmployee(employee)) },
                    onLongPress = { onEvent(EmployeeListEvent.LongPressEmployee(employee)) }
                )
            }
    ) {
        Box(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            FlipAnimation(
                state = if (markedForDelete) FlipState.BACK else FlipState.FRONT,
                backModifier = Modifier.background(color = MaterialTheme.colors.primary),
                front = {},
                back = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "marked for delete"
                    )
                }
            )
        }
        Text(
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.8f),
            text = employee.name,
            style = MaterialTheme.typography.h4,
            maxLines = 1
        )
    }
}

@Preview(showBackground = false)
@Composable
fun DefaultPreview() {
    ScheduleTheme {
        EmployeeList(
            state = EmployeesState.previewState,
            onEvent = {},
        )
    }
}