package com.example.gitgauge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        val lines = text.split("\n")
        var i = 0

        while (i < lines.size) {
            val line = lines[i]

            when {
                // Headers
                line.startsWith("# ") -> {
                    Text(
                        text = line.removePrefix("# "),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFf06bc7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 8.dp)
                    )
                }
                line.startsWith("## ") -> {
                    Text(
                        text = line.removePrefix("## "),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFc67aff),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 6.dp)
                    )
                }
                line.startsWith("### ") -> {
                    Text(
                        text = line.removePrefix("### "),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp)
                    )
                }
                // Bold lists
                line.startsWith("- ") -> {
                    Text(
                        text = "• ${line.removePrefix("- ")}",
                        fontSize = 14.sp,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, bottom = 4.dp)
                    )
                }
                // Code blocks
                line.startsWith("```") -> {
                    i++
                    val codeLines = mutableListOf<String>()
                    while (i < lines.size && !lines[i].startsWith("```")) {
                        codeLines.add(lines[i])
                        i++
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF0f1621),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = codeLines.joinToString("\n"),
                            fontSize = 12.sp,
                            color = Color(0xFF00ff41),
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 1.6.em,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                // Empty lines
                line.isBlank() -> {
                    Box(modifier = Modifier.padding(4.dp))
                }
                // Regular text with potential formatting
                else -> {
                    if (line.isNotEmpty()) {
                        Text(
                            text = parseInlineMarkdown(line),
                            fontSize = 14.sp,
                            color = Color.White,
                            lineHeight = 1.5.em,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        )
                    }
                }
            }
            i++
        }
    }
}

private fun parseInlineMarkdown(text: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        var currentText = text
        var index = 0

        while (index < currentText.length) {
            when {
                // Bold text **text**
                currentText.substring(index).startsWith("**") -> {
                    val endIndex = currentText.indexOf("**", index + 2)
                    if (endIndex != -1) {
                        val boldText = currentText.substring(index + 2, endIndex)
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFf06bc7)
                            )
                        ) {
                            append(boldText)
                        }
                        index = endIndex + 2
                    } else {
                        append(currentText[index])
                        index++
                    }
                }
                // Italic text *text*
                currentText.substring(index).startsWith("*") &&
                !currentText.substring(index).startsWith("**") -> {
                    val endIndex = currentText.indexOf("*", index + 1)
                    if (endIndex != -1) {
                        val italicText = currentText.substring(index + 1, endIndex)
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFc67aff)
                            )
                        ) {
                            append(italicText)
                        }
                        index = endIndex + 1
                    } else {
                        append(currentText[index])
                        index++
                    }
                }
                // Code text `text`
                currentText[index] == '`' -> {
                    val endIndex = currentText.indexOf("`", index + 1)
                    if (endIndex != -1) {
                        val codeText = currentText.substring(index + 1, endIndex)
                        withStyle(
                            style = SpanStyle(
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF00ff41),
                                background = Color(0xFF0f1621)
                            )
                        ) {
                            append(codeText)
                        }
                        index = endIndex + 1
                    } else {
                        append(currentText[index])
                        index++
                    }
                }
                else -> {
                    append(currentText[index])
                    index++
                }
            }
        }
    }
}
