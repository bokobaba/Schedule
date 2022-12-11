package com.love.schedule.core.component

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

@Composable
fun CancelDeleteButton(
    visible: Boolean,
    onClick: () -> Unit
) {
    Log.d("CancelDeleteButton", "init")
    if (visible) {
        Icon(
            modifier = Modifier.clickable {
                onClick()
            },
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "delete item"
        )
    }
}

@Composable
fun DeleteButton(visible: Boolean, onDelete: () -> Unit) {
    Log.d("DeleteButton", "init")
    if (visible) {
        Button(
            onClick = onDelete
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "delete employee")
        }
    }
}