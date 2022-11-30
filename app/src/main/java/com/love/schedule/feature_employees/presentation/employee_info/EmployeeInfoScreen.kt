package com.love.schedule.feature_employees.presentation.employee_info

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.love.schedule.feature_employees.presentation.employee_info.view_model.*
import com.love.schedule.core.component.LoadingAnimation
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EmployeeInfoScreen(
    navController: NavController,
    vm: EmployeeInfoViewModel = hiltViewModel()
) {
    Log.d("EmployeeInfoScreen", "")

    val scaffoldState: ScaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        vm.eventFlow.collectLatest { event ->
            when (event) {
                is EmployeeInfoViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is EmployeeInfoViewModel.UiEvent.SaveNote -> {
                    navController.navigateUp()
                }
                is EmployeeInfoViewModel.UiEvent.Back -> {
                    navController.navigateUp()
                }
            }
        }
    }

    EmployeeInfo(
        loading = vm.loading,
        state = vm.state,
        availabilityState = vm.availabilityState,
        requestsState = vm.requestsState,
        newEmployee = vm.newEmployee,
        scaffoldState = scaffoldState
    )
}

@Composable
fun EmployeeInfo(
    loading: MutableState<Boolean> = mutableStateOf(false),
    state: IEmployeeInfoState,
    availabilityState: IAvailabilityState,
    requestsState: IRequestsState,
    newEmployee: Boolean = false,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    Log.d("EmployeeInfo", "init")
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (newEmployee) "New Employee" else "Employee Info") },
                navigationIcon = { BackButton(state.navigateUp) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    state.saveInfo()
                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Done, contentDescription = "Save data")
            }
        },
//        modifier = Modifier.background(MaterialTheme.colors.background),
        scaffoldState = scaffoldState
    ) {
        if (loading.value) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimation()
            }
        } else {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (primary, tabs, button) = createRefs()
                EmployeeInfoPrimary(
                    name = state.employeeName,
                    employeeId = state.employeeId,
                    modifier = Modifier.constrainAs(primary) {
                        top.linkTo(parent.top)
                    },
                    actions = state,
                    newEmployee = newEmployee
                )
                Tabs(
                    availabilityState = availabilityState,
                    requestsState = requestsState,
                    modifier = Modifier.constrainAs(tabs) {
                        top.linkTo(primary.bottom)
                        bottom.linkTo(button.top)
                        height = Dimension.fillToConstraints
                    }
                )
            }
        }
    }
}

@Composable
fun BackButton(onClick: () -> Unit) {
    Icon(
        modifier = Modifier.clickable {
            onClick()
        },
        imageVector = Icons.Default.ArrowBack,
        contentDescription = "back"
    )
}

@Composable
fun EmployeeInfoPrimary(
//    name: String,
//    employeeId: String,
    name: MutableState<String>,
    employeeId: MutableState<String>,
    modifier: Modifier = Modifier,
    actions: IEmployeeInfoState,
    newEmployee: Boolean = false,
) {
    Log.d("EmployeeInfoPrimary", "name: $name, id: $employeeId")
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        EmployeeInfoTextField(
            text = name,
            label = "Name",
            onValueChange = { actions.editEmployeeName(it) })
        if (newEmployee) {
            EmployeeInfoTextField(
                text = employeeId,
                label = "EmployeeId",
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.None
                ),
                onValueChange = { actions.editEmployeeId(it) })
        } else {
            EmployeeInfoText(text = employeeId)
        }
    }
}

@Composable
fun RowScope.EmployeeInfoText(text: MutableState<String>) {
    Log.d("EmployeeInfoText", text.value)
    Text(
        modifier = Modifier.weight(1f),
        text = text.value,
        style = MaterialTheme.typography.h4,
        textAlign = TextAlign.Center,
        maxLines = 1,
    )
}

@Composable
fun RowScope.EmployeeInfoTextField(
    text: MutableState<String>,
    label: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.None
    )
) {
    Log.d("EmployeeInfoText", text.value)
    OutlinedTextField(
        modifier = Modifier.weight(1f),
        keyboardOptions = keyboardOptions,
        value = text.value,
        maxLines = 1,
        singleLine = true,
        onValueChange = { string ->
            onValueChange(string.filter { it != '\n' })
        },
        label = { Text(label) }
    )

}

@Composable
fun Tabs(
    availabilityState: IAvailabilityState,
    requestsState: IRequestsState,
    modifier: Modifier = Modifier
) {
    Log.d("Tabs", "tabs")
    Column(modifier = modifier) {
        var selectedIndex by remember { mutableStateOf(0) }
        val list = listOf("Availability", "Requests")
        TabRow(selectedTabIndex = selectedIndex,
            backgroundColor = MaterialTheme.colors.secondary,
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .clip(RoundedCornerShape(50))
                .padding(1.dp),
            indicator = { tabPositions: List<TabPosition> ->
                Box {}
            }
        ) {
            list.forEachIndexed { index, text ->
                val selected = selectedIndex == index
                Tab(
                    modifier =
                    if (selected) Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colors.primary)
                    else Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colors.secondary),
                    selected = selected,
                    onClick = { selectedIndex = index },
                    text = { Text(text = text, color = Color(0xff6FAAEE)) }
                )
            }
        }
        when (selectedIndex) {
            0 -> EmployeeAvailability(state = availabilityState)
            1 -> EmployeeRequests(requestsState)
        }
    }
}

@Composable
fun SaveButton(
    name: String,
    employeeId: String,
    navController: NavController,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        onClick = {
            Log.d("SaveButton", "edit employee: $name, $employeeId")
            onSave()
        }) {
        Text("Save")
    }
}

@Preview(showBackground = false)
@Composable
fun EmployeeInfoPreview() {
    EmployeeInfo(
        state = EmployeeInfoState.previewState,
        availabilityState = AvailabilityState.previewState,
        requestsState = RequestsState.previewRequestsState,
        newEmployee = false
    )
}