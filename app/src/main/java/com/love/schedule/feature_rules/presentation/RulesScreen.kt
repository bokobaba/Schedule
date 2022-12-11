package com.love.schedule.feature_rules.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow
import com.love.schedule.core.component.CancelDeleteButton
import com.love.schedule.core.component.DeleteButton
import com.love.schedule.core.component.drawBorderWithText
import com.love.schedule.feature_rules.domain.model.Condition
import com.love.schedule.feature_rules.domain.model.ConditionType
import com.love.schedule.feature_rules.domain.model.Rule
import com.love.schedule.feature_rules.presentation.view_model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun RulesScreen(navController: NavController, vm: RulesViewModel = hiltViewModel()) {
    RulesScreenContent(
        state = vm.state,
        cState = vm.conditionsState,
        onRulesEvent = vm::onRulesEvent,
        onConditionsEvent = vm::onConditionsEvent,
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RulesScreenContent(
    onRulesEvent: (RulesEvent) -> Unit,
    state: IRulesViewState,
    cState: IConditionsViewState,
    onConditionsEvent: (ConditionsEvent) -> Unit,
) {
    Log.d("RulesScreenContent", "init")
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    CancelDeleteButton(
                        visible = state.selected.value != null,
//                        onClick = state::onCancelDelete
                        onClick = { onRulesEvent(RulesEvent.CancelDelete) }
                    )
                },
                title = { Text("Rules") },
                actions = {
                    DeleteButton(
                        visible = state.selected.value != null,
//                        onDelete = state::onDelete
                        onDelete = { onRulesEvent(RulesEvent.DeleteRule) }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
//                onClick = state::onAddRule,
                onClick = { onRulesEvent(RulesEvent.AddRule) },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add employee")
            }
        },
    ) {
        RulesList(
            state = state,
            cState = cState,
            onRulesEvent = onRulesEvent,
            onConditionsEvent = onConditionsEvent,
        )
    }
}

@Composable
fun RulesList(
    state: IRulesViewState,
    cState: IConditionsViewState,
    onRulesEvent: (RulesEvent) -> Unit,
    onConditionsEvent: (ConditionsEvent) -> Unit,
) {
    Log.d("RulesList", "Init")
    val lazylistState = rememberLazyListState()
    LazyColumn(
        state = lazylistState,
        modifier = Modifier.padding(5.dp),
        userScrollEnabled = state.scrollEnabled,
    ) {
        itemsIndexed(state.rules) { i, rule ->
            RuleItem(
                index = i,
                listState = lazylistState,
                rule = rule,
                state = state,
                cState = cState,
                onRulesEvent = onRulesEvent,
                onConditionsEvent = onConditionsEvent,
            )
            Divider()
        }
    }
}

@Composable
fun RuleItem(
    index: Int,
    listState: LazyListState,
    rule: Rule,
    state: IRulesViewState,
    cState: IConditionsViewState,
    onRulesEvent: (RulesEvent) -> Unit,
    onConditionsEvent: (ConditionsEvent) -> Unit,
) {
//    Log.d("RuleItem", "init")
    val selected = state.selected.value?.id == rule.id
    val coroutineScope = rememberCoroutineScope()
    var job: Job? by remember { mutableStateOf(null) }
    val height by animateDpAsState(
        targetValue = if (selected) 800.dp else 5.dp,
        animationSpec = tween(500),
        finishedListener = {
            if (selected) {
                job?.cancel()
                job = coroutineScope.launch {
                    listState.animateScrollToItem(index = index)
                }
            }
        }
    )
    Column(
        modifier = Modifier.padding(bottom = height),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 5.dp,
                    bottom = 5.dp,
//                    end = 50.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val rotationState by animateFloatAsState(
                targetValue = if (selected) 180f else 0f
            )
            RuleTextItem(
                modifier = Modifier.weight(0.1f),
                text = "$index",
                status = rule.status,
            )
//            Spacer(modifier = Modifier.width(10.dp))

            RuleTextItem(
                modifier = Modifier
                    .weight(0.2f)
                    .clickable {
//                        state.onEditStatus(index)
                        onRulesEvent(RulesEvent.EditStatus(index))
//                        onStatusChange(index)
                    },
                text = if (rule.status) "ON" else "OFF",
                status = rule.status
            )
//            Spacer(modifier = Modifier.width(20.dp))

            Row(
                modifier = Modifier
                    .weight(0.7f)
                    .clickable {
//                        state.onExpandRule(rule)
                        onRulesEvent(RulesEvent.ExpandRule(rule))
//                        onExpand(rule)
                    }
            ) {
                RuleTextItem(
                    modifier = Modifier.weight(0.7f),
                    text = rule.name,
                    status = rule.status
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "expand",
                    modifier = Modifier
                        .rotate(rotationState)
                        .alpha(0.7f)
                )
            }
        }
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn() + slideInVertically(animationSpec = tween(200)),
            exit = fadeOut() + slideOutVertically(animationSpec = tween(200))
        ) {
            RuleItemSecondary(
                index = index,
                rule = rule,
//                state = state,
                cState = cState,
                onRulesEvent = onRulesEvent,
                onConditionsEvent = onConditionsEvent,
            )
        }
    }
}

@Composable
fun RowScope.RuleTextItem(
    modifier: Modifier = Modifier,
    text: String,
    status: Boolean,
) {
    val textModifier = modifier
        .alignByBaseline()
        .alpha(alpha = if (status) 1f else 0.4f)
    val style: TextStyle = MaterialTheme.typography.h4
    Text(
        text = text,
        modifier = textModifier,
        maxLines = 1,
        style = style,
    )
}

@Composable
fun RuleItemSecondary(
    index: Int,
    rule: Rule,
    cState: IConditionsViewState,
    onRulesEvent: (RulesEvent) -> Unit,
    onConditionsEvent: (ConditionsEvent) -> Unit,
) {
    Log.d("RuleItemSecondary", "init")
    Column {
        Divider()
        RuleInfoName(
            index = index,
            name = rule.name,
            onRulesEvent = onRulesEvent,
        )
        RuleConditions(
            onConditionsEvent = onConditionsEvent,
            rIndex = index,
            getConditions = cState::getConditions,
            searchResults = cState.searchResults,
        )
        Divider()
    }
}

@Composable
fun RuleInfoName(
    index: Int,
    name: String,
    onRulesEvent: (RulesEvent) -> Unit,
) {
    Log.d("RuleInfoName", "init")
    var str by remember { mutableStateOf(name) }
    var dirty by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = str,
        label = { Text("Name") },
        onValueChange = {
            dirty = true
            str = it
        },
        modifier = Modifier
            .padding(bottom = 20.dp)
            .onFocusChanged { focusState ->
                if (dirty && !focusState.isFocused) {
                    onRulesEvent(RulesEvent.EditName(index, str))
                    dirty = false
                }
            },
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done,
        ),
    )
}

@Composable
fun RuleConditions(
    rIndex: Int,
    searchResults: SnapshotStateList<Condition>,
    getConditions: (Int) -> SnapshotStateList<Condition>,
    onConditionsEvent: (ConditionsEvent) -> Unit,
) {
    Log.d("RuleConditions", "init")
    Column() {
        val hours: MutableState<String?> = remember { mutableStateOf(null) }
        ConditionChips(
            modifier = Modifier.fillMaxWidth(),
            rIndex = rIndex,
            onConditionsEvent = onConditionsEvent,
            getConditions = getConditions,
            hours = hours,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ConditionSearchBar(
                rIndex = rIndex,
                onConditionsEvent = onConditionsEvent,
                searchResults = searchResults,
            )
            ConditionHours(
                rIndex = rIndex,
                hours = hours,
                onConditionsEvent = onConditionsEvent,
            )
        }
    }
}

@Composable
fun ConditionHours(
    rIndex: Int,
    hours: MutableState<String?>,
    onConditionsEvent: (ConditionsEvent) -> Unit,
) {
    var dirty by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    hours.value?.let { h ->
        OutlinedTextField(
            modifier = Modifier
                .padding(top = 10.dp)
                .width(75.dp)
                .onFocusChanged { focusState ->
                    if (dirty && !focusState.isFocused) {
                        onConditionsEvent(ConditionsEvent.EditHours(rIndex, h))
                        dirty = false
                    }
                },
            label = { Text("Hours") },
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            value = h,
            onValueChange = { str ->
                dirty = true
                hours.value = str.take(2).filter { c -> c != '\n' }
            },
        )
    }
}

@Composable
fun ConditionSearchBar(
    rIndex: Int,
    searchResults: SnapshotStateList<Condition>,
    onConditionsEvent: (ConditionsEvent) -> Unit,
) {
    Log.d("ConditionSearchBar", "init")
    var query by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var job: Job? by remember { mutableStateOf(null) }
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(0.6f),
        maxLines = 1,
        value = query,
        label = { Text("search conditions") },
        onValueChange = { str ->
            query = str.filter { it != '\n' }
            job?.cancel()
            job = coroutineScope.launch {
                onConditionsEvent(ConditionsEvent.SearchConditions(rIndex, query))
            }
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                tint = MaterialTheme.colors.onBackground,
                contentDescription = "Search Icon"
            )
        },
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done,
        ),
    )
    DropdownMenu(
        modifier = Modifier.requiredSizeIn(maxHeight = 200.dp),
        expanded = searchResults.isNotEmpty(),
        onDismissRequest = { onConditionsEvent(ConditionsEvent.DismissSearch) },
        properties = PopupProperties(focusable = false)
    ) {
        searchResults.forEach { result ->
            DropdownMenuItem(
                onClick = {
                    onConditionsEvent(ConditionsEvent.AddCondition(rIndex, result))
                    query = ""
                }
            ) {
                Text(result.name)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun ConditionChips(
    modifier: Modifier = Modifier,
    rIndex: Int,
    getConditions: (Int) -> SnapshotStateList<Condition>,
    onConditionsEvent: (ConditionsEvent) -> Unit,
    hours: MutableState<String?>,
) {
    Log.d("ConditionChips", "size ")
    val textMeasurer = rememberTextMeasurer()
    val borderColor = MaterialTheme.colors.primary
    val scrollState = rememberScrollState()
    val conditions: SnapshotStateList<Condition> = getConditions(rIndex)
    hours.value = conditions.find { it.type.value == ConditionType.HOURS.value }?.value
    FlowRow(
        mainAxisSpacing = 5.dp,
        modifier = modifier
//            .padding(10.dp)
            .heightIn(max = 200.dp)
            .drawBehind {
                apply { drawBorderWithText(textMeasurer, borderColor) }
            }
            .padding(5.dp)
            .verticalScroll(scrollState)
    ) {
        conditions.forEachIndexed { cIndex, condition ->
            val color = when (condition.type) {
                ConditionType.DAY -> Color.Red
                ConditionType.EMPLOYEE -> MaterialTheme.colors.primary
                ConditionType.SHIFT -> MaterialTheme.colors.secondary
                ConditionType.HOURS -> Color(0xFF6200EE)
            }
            val text = when (condition.type) {
                ConditionType.HOURS -> {
                    condition.name + " " +
                            (if (condition.operator != null)
                                "${condition.operator}"
                            else
                                "") +
                            " ${condition.value}"
                }
                ConditionType.DAY, ConditionType.EMPLOYEE -> {
                    if (condition.operator != null)
                        "NOT:${condition.name}"
                    else
                        condition.name
                }
                else -> condition.name
            }
            Log.d("ConditionChips", "value = ${condition.value}")
            Log.d("ConditionChips", "text = $text")
            InputChip(
                selected = false,
                onClick = { onConditionsEvent(ConditionsEvent.ConditionClick(rIndex, cIndex)) },
                label = {
                    Text(
                        text = text,
                        maxLines = 1,
                        fontSize = 12.sp
                    )
                },
                colors = InputChipDefaults.inputChipColors(
                    containerColor = color,
                    selectedContainerColor = color,
                ),
                trailingIcon = {
                    Icon(
                        modifier = Modifier.clickable {
                            onConditionsEvent(ConditionsEvent.RemoveCondition(rIndex, cIndex))
                        },
                        tint = MaterialTheme.colors.onSurface,
                        imageVector = Icons.Default.Close,
                        contentDescription = "delete condition"
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewRulesScreen() {
    RulesScreenContent(
        state = RulesState.previewRulesState,
        cState = ConditionsState.previewConditionsState,
        onRulesEvent = {},
        onConditionsEvent = {},
    )
}