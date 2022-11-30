package com.love.schedule.feature_employees.presentation.employees

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.love.schedule.feature_employees.domain.model.Employee
import com.love.schedule.model.employee.EmployeesState
import com.love.schedule.model.employee.IEmployeesState
import com.love.schedule.nav_graph.Screen
import com.love.schedule.core.component.FlipAnimation
import com.love.schedule.core.component.FlipState
import com.love.schedule.core.component.LoadingAnimation
import com.love.schedule.ui.theme.ScheduleTheme

internal val spacing: Dp = 10.dp

@Composable
fun EmployeeListScreen(
    navController: NavController,
    vm: EmployeesViewModel = hiltViewModel()
) {
    EmployeeList(vm.loading, navController, vm.state)
}

@Composable
fun EmployeeList(
    loading: MutableState<Boolean> = mutableStateOf(false),
    navController: NavController,
    state: IEmployeesState
) {
    Log.d("EmployeeList", "init")
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    CancelDeleteButton(
                        toDelete = state.employeeToDelete,
                        onClick = state::cancelDelete
                    )
                },
                title = { Text("Employees") },
                actions = {
                    DeleteButton(
                        toDelete = state.employeeToDelete,
                        onDelete = state.deleteEmployee
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(route = Screen.EmployeeInfo.route)
                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add employee")
            }
        },
        scaffoldState = scaffoldState
    ) {
        if (loading.value) {
            LoadingAnimation()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(spacing),
            ) {
                items(state.employees) { employee ->
                    EmployeeItem(
                        employee = employee,
                        navController = navController,
                        onTap = state::onSelectEmployee,
                        onLongPress = state::onLongPressEmployee,
                        employeeToDelete = state.employeeToDelete
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun CancelDeleteButton(
    toDelete: MutableState<Employee?>,
    onClick: () -> Unit
) {
    Log.d("CancelDeleteButton", "init")
    val employee: Employee = toDelete.value ?: return
    if (toDelete.value != null) {
        Icon(
            modifier = Modifier.clickable {
                onClick()
            },
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "delete employee"
        )
    }
}

@Composable
fun DeleteButton(toDelete: MutableState<Employee?>, onDelete: (Employee) -> Unit) {
    Log.d("DeleteButton", "init")
    val employee: Employee = toDelete.value ?: return
    if (toDelete.value != null) {
        Button(
            onClick = { onDelete(employee) }
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "delete employee")
        }
    }
}

@Composable
fun EmployeeItem(
    employee: Employee,
    employeeToDelete: MutableState<Employee?>,
    navController: NavController,
    onTap: (Employee, NavController) -> Unit,
    onLongPress: (Employee) -> Unit,
) {
    Log.d("EmployeeItem", "name = ${employee.name}")
    val markedForDelete = employeeToDelete.value?.employeeId == employee.employeeId
    val modifier: Modifier = if (markedForDelete) Modifier.background(Color.Red) else Modifier
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
//            .padding(spacing)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap(employee, navController) },
                    onLongPress = { onLongPress(employee) }
                )
            }
    ) {
        Box(modifier = Modifier.weight(1f)) {
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
            modifier = Modifier.weight(1f),
            text = employee.name,
            style = MaterialTheme.typography.h4
        )
        Box(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = false)
@Composable
fun DefaultPreview() {
    ScheduleTheme {
        EmployeeList(
            navController = rememberNavController(),
            state = EmployeesState.previewActions
        )
    }
}