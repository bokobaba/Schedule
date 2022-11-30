package com.love.schedule.core.component

import android.content.res.Configuration
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.love.schedule.feature_schedule.presentation.ScheduleScreenConstants

@Composable
fun AnimatedShimmer() {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            repeatMode = RepeatMode.Reverse,
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing,
            )
        )
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Column() {
        repeat(7) {
            ScheduleShimmer(brush = brush)
        }
    }
}

@Composable
fun ScheduleShimmer(brush: Brush) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = ScheduleScreenConstants.rowPad,
                bottom = ScheduleScreenConstants.rowPad,
                start = ScheduleScreenConstants.sidePad,
                end = ScheduleScreenConstants.sidePad,
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {

//        Column(verticalArrangement = Arrangement.Center) {
            Spacer(
                modifier = Modifier
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .weight(1f)
                    .background(brush = brush)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Box(
                modifier = Modifier
//                    .height(80.dp)
                    .height(80.dp)
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(brush = brush)
            )
//            Spacer(modifier = Modifier
//                .width(5.dp)
//                .height(5.dp))
//            Spacer(
//                modifier = Modifier
//                    .height(20.dp)
//                    .clip(RoundedCornerShape(10.dp))
//                    .fillMaxWidth(0.7f)
//                    .background(brush = brush)
//            )
//        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleShimmerPreview() {
    ScheduleShimmer(
        brush = Brush.linearGradient(
            listOf(
                Color.LightGray.copy(alpha = 0.6f),
                Color.LightGray.copy(alpha = 0.2f),
                Color.LightGray.copy(alpha = 0.6f),
            )
        )
    )
}

//@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//fun AnimatedShimmerDarkPreview() {
//    MaterialTheme() {
//        Surface {
//            ShimmerDisplay(
//                brush = Brush.linearGradient(
//                    listOf(
//                        Color.LightGray.copy(alpha = 0.6f),
//                        Color.LightGray.copy(alpha = 0.2f),
//                        Color.LightGray.copy(alpha = 0.6f),
//                    )
//                )
//            )
//        }
//    }
//}