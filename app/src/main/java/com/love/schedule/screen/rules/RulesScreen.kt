package com.love.schedule.screen.rules

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RulesScreen(navController: NavController) {
    //    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(text = "Rules")
//    }
    HelloScreen()
}

@Composable
fun HelloScreen() {
    var name by rememberSaveable { mutableStateOf("") }
    Column {
        Test(name = name, onNameChange = { name = it })
        HelloContent(name = name, onNameChange = { name = it })
    }
}

@Composable
fun HelloContent(
    name: String,
    onNameChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") }
        )
    }
}

@Composable
fun Test(
    name: String,
    onNameChange: (String) -> Unit,
) {
    Row {
        OutlinedTextField(
            value = name,
            onValueChange =  onNameChange ,
            label = { Text("Enter Name") })

    }
}