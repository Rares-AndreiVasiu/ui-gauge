package com.example.gitgauge.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun AnimatedGradientText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    fontFamily: FontFamily = FontFamily.Default,
    colors: List<Color> = listOf(Color(0xFFf06bc7), Color(0xFFc67aff)),
    letterSpacing: TextUnit = TextUnit.Unspecified,
    showUnderlineAnimation: Boolean = true
) {
    val underlineProgress = remember { Animatable(0f) }
    val isFromLeft = remember { Random.nextBoolean() }
    val textWidth = remember { androidx.compose.runtime.mutableStateOf(0) }

    LaunchedEffect(showUnderlineAnimation) {
        if (showUnderlineAnimation) {
            underlineProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1500)
            )
        }
    }

    val gradientBrush = Brush.linearGradient(
        colors = colors
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicText(
            text = text,
            style = TextStyle(
                fontSize = fontSize,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                brush = gradientBrush
            ),
            modifier = Modifier.onSizeChanged { size ->
                textWidth.value = size.width
            }
        )

        // Underline animation from left or right
        if (showUnderlineAnimation && textWidth.value > 0) {
            Box(
                modifier = Modifier
                    .height(3.dp)
                    .fillMaxWidth(underlineProgress.value)
                    .offset(
                        x = if (isFromLeft) {
                            0.dp
                        } else {
                            // When animating from right, offset the box to the right as it grows
                            val progressDp = (textWidth.value * (1 - underlineProgress.value)).let {
                                androidx.compose.ui.unit.Dp(it / androidx.compose.ui.platform.LocalDensity.current.density)
                            }
                            progressDp
                        }
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = colors
                        )
                    )
            )
        }
    }
}
