package com.example.gitgauge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
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

@Composable
fun GradientUnderlineText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    fontFamily: FontFamily = FontFamily.Default,
    colors: List<Color> = listOf(Color(0xFFf06bc7), Color(0xFFc67aff)),
    letterSpacing: TextUnit = TextUnit.Unspecified
) {
    val textWidth = remember { androidx.compose.runtime.mutableStateOf(0) }

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

        // Static gradient underline that matches text width
        if (textWidth.value > 0) {
            Box(
                modifier = Modifier
                    .height(3.dp)
                    .fillMaxWidth(1f)  // Full width of the text
                    .background(
                        brush = Brush.linearGradient(
                            colors = colors
                        )
                    )
            )
        }
    }
}
