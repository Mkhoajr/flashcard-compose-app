package com.example.flashcard_compose_app.ui.components

import com.example.flashcard_compose_app.ui.theme.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizScreenContent(
    currentQuestionIndex: Int,
    totalQuestions: Int,
    questionText: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    onNotKnowClick: () -> Unit,
    onCloseClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Indigo)
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        // --- TOP BAR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCloseClick) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = QuizletTextPrimary)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${currentQuestionIndex + 1} / $totalQuestions",
                    color = QuizletTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Quiz Mode",
                    color = QuizletTextSecondary,
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = QuizletTextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- MAIN CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Small title in Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Definition",
                        color = IndigoDark,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${currentQuestionIndex + 1} of $totalQuestions",
                        color = IndigoDark,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Main Question
                Text(
                    text = questionText,
                    color = IndigoDark,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Choose an answer",
                    color = IndigoLight,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuizOptionButton(text = options.getOrElse(0) { "" }, modifier = Modifier.weight(1f)) { onOptionSelected(options[0]) }
                        QuizOptionButton(text = options.getOrElse(1) { "" }, modifier = Modifier.weight(1f)) { onOptionSelected(options[1]) }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuizOptionButton(text = options.getOrElse(2) { "" }, modifier = Modifier.weight(1f)) { onOptionSelected(options[2]) }
                        QuizOptionButton(text = options.getOrElse(3) { "" }, modifier = Modifier.weight(1f)) { onOptionSelected(options[3]) }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Don't Know Button
                TextButton(
                    onClick = onNotKnowClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Don't know?",
                        color = IndigoDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun QuizOptionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 60.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, QuizletOutline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = IndigoDark
        ),
        contentPadding = PaddingValues(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
fun QuizScreenPreview() {
    QuizScreenContent(
        currentQuestionIndex = 0,
        totalQuestions = 20,
        questionText = "たいふう bão",
        options = listOf("勉強", "台風", "天気", "学校"),
        onOptionSelected = {},
        onNotKnowClick = {},
        onCloseClick = {},
        onSettingsClick = {}
    )
}