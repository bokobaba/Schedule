package com.love.schedule.core.component

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.*

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawBorderWithText(
    textMeasurer: TextMeasurer,
    borderColor: Color,
    strokeWidth: Float = 2f,
) {
    val x = size.width - strokeWidth
    val y = size.height - strokeWidth

    val textLayoutResult: TextLayoutResult =
        textMeasurer.measure(text = AnnotatedString("Conditions"))
    val textSize = textLayoutResult.size

    val textStart = (size.width * 0.2f - textSize.width / 2f) - 5f
    val textEnd = textStart + textSize.width + 5f

    //top line
    drawLine(
        color = borderColor,
        start = Offset(0f, 0f), //(0,0) at top-left point of the box
        end = Offset(textStart - 5f, 0f), //top-right point of the box
        strokeWidth = strokeWidth
    )

    drawText(
        textMeasurer = textMeasurer,
        text = "Conditions",
        topLeft = Offset(textStart, -textSize.height / 2f),
        style = TextStyle.Default.copy(color = borderColor)

    )

    //top line
    drawLine(
        color = borderColor,
        start = Offset(textEnd, 0f), //(0,0) at top-left point of the box
        end = Offset(x, 0f), //top-right point of the box
        strokeWidth = strokeWidth
    )

    //left line
    drawLine(
        color = borderColor,
        start = Offset(0f, 0f), //(0,0) at top-left point of the box
        end = Offset(0f, y),//bottom-left point of the box
        strokeWidth = strokeWidth
    )

    //right line
    drawLine(
        color = borderColor,
        start = Offset(x, 0f),// top-right point of the box
        end = Offset(x, y),// bottom-right point of the box
        strokeWidth = strokeWidth
    )

    //bottom line
    drawLine(
        color = borderColor,
        start = Offset(0f, y),// bottom-left point of the box
        end = Offset(x, y),// bottom-right point of the box
        strokeWidth = strokeWidth
    )
}