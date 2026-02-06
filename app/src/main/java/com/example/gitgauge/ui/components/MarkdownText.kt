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
                // Headers - Remove markdown syntax and render as bold text with inline markdown parsing
                line.startsWith("##### ") -> {
                    Text(
                        text = parseInlineMarkdown(line.removePrefix("##### ").trim()),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFf06bc7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 4.dp)
                    )
                }
                line.startsWith("#### ") -> {
                    Text(
                        text = parseInlineMarkdown(line.removePrefix("#### ").trim()),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFf06bc7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 6.dp)
                    )
                }
                line.startsWith("### ") -> {
                    Text(
                        text = parseInlineMarkdown(line.removePrefix("### ").trim()),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFf06bc7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 6.dp)
                    )
                }
                line.startsWith("## ") -> {
                    Text(
                        text = parseInlineMarkdown(line.removePrefix("## ").trim()),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFf06bc7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp, bottom = 8.dp)
                    )
                }
                line.startsWith("# ") -> {
                    Text(
                        text = parseInlineMarkdown(line.removePrefix("# ").trim()),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFf06bc7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 10.dp)
                    )
                }
                // Tables - convert to bullet points, skip separator lines
                line.trim().startsWith("|") -> {
                    if (!line.trim().startsWith("|---|") &&
                        !line.trim().startsWith("|:---|") &&
                        !line.trim().startsWith("|---|") &&
                        !line.trim().matches(Regex("^\\|[\\s\\-:|]*\\|$"))) {
                        // Extract table cells
                        val cells = line.split("|")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }

                        if (cells.isNotEmpty()) {
                            Text(
                                text = "• " + cells.joinToString(" - "),
                                fontSize = 13.sp,
                                color = Color.White,
                                lineHeight = 1.5.em,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, bottom = 4.dp)
                            )
                        }
                    }
                }
                // Numbered lists - remove numbering from line, render with inline markdown
                line.matches(Regex("^\\d+\\.\\s.*")) -> {
                    val content = line.replaceFirst(Regex("^\\d+\\.\\s"), "")
                    Text(
                        text = parseInlineMarkdown(content),
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 1.6.em,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, bottom = 6.dp)
                    )
                }
                // Bullet lists with dash
                line.startsWith("- ") -> {
                    val content = line.removePrefix("- ")
                    Text(
                        text = parseInlineMarkdown("• $content"),
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 1.6.em,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, bottom = 6.dp)
                    )
                }
                // Bullet lists with asterisk
                line.startsWith("* ") -> {
                    val content = line.removePrefix("* ")
                    Text(
                        text = parseInlineMarkdown("• $content"),
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 1.6.em,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, bottom = 6.dp)
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
                    if (codeLines.isNotEmpty()) {
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
                }
                // Empty lines
                line.isBlank() -> {
                    Box(modifier = Modifier.padding(2.dp))
                }
                // Regular text with potential inline formatting
                else -> {
                    if (line.isNotEmpty() && !line.trim().isEmpty()) {
                        Text(
                            text = parseInlineMarkdown(line),
                            fontSize = 14.sp,
                            color = Color.White,
                            lineHeight = 1.6.em,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
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
        val currentText = text
        var index = 0

        while (index < currentText.length) {
            when {
                // Bold text **text** and __text__
                (currentText.substring(index).startsWith("**") ||
                 currentText.substring(index).startsWith("__")) -> {
                    val delimiter = if (currentText[index] == '*') "**" else "__"
                    val endIndex = currentText.indexOf(delimiter, index + 2)
                    if (endIndex != -1) {
                        val boldText = currentText.substring(index + 2, endIndex)
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                background = Color(0xFFf06bc7)
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
                // Italic text *text* and _text_
                (currentText.substring(index).startsWith("*") ||
                 currentText.substring(index).startsWith("_")) &&
                !currentText.substring(index).startsWith("**") &&
                !currentText.substring(index).startsWith("__") -> {
                    val delimiter = currentText[index].toString()
                    val endIndex = currentText.indexOf(delimiter, index + 1)
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
                                background = Color(0xFF0f1621),
                                fontSize = 12.sp
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
