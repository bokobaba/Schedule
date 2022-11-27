package com.love.schedule.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup

data class DateRangePopupData(
    val title: String,
    val start: String,
    val end: String,
    val onSaveData: (String, String) -> Unit
)

@Composable
fun DateRangePopup(data: DateRangePopupData?) {
    if (data == null) return

    Popup() {

    }

}
