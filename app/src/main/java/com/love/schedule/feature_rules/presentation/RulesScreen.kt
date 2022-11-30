package com.love.schedule.feature_rules.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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