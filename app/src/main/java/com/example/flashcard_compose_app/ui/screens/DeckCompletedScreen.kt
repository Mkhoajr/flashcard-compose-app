package com.example.flashcard_compose_app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.flashcard_compose_app.ui.theme.*

@Composable
fun DeckCompletedScreen(
    knewCount: Int,
    stillLearningCount: Int,
    onPracticeClick: () -> Unit,
    onRestartAll: () -> Unit,
    onFocusStillLearning: () -> Unit,
    onBack: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(com.example.flashcard_compose_app.R.raw.firework_lottie))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuizletBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        // ==========================================
        // HEADER and LOTTIE ANIMATION
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "You're doing great! Keep it up to build confidence.",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                lineHeight = 32.sp
            )
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ==========================================
        // HOW YOU'RE DOING (Donut Chart + Stats)
        // ==========================================
        Text("How you're doing", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Donut Chart (Left side)
            MasteryDonutChart(
                knewCount = knewCount,
                stillLearningCount = stillLearningCount,
                modifier = Modifier.padding(end = 24.dp)
            )

            // Static (Right side)
            Column(modifier = Modifier.weight(1f)) {
                SummaryRow(label = "Know", count = knewCount, color = QuizletGreen)
                Spacer(modifier = Modifier.height(8.dp))
                SummaryRow(label = "Still learning", count = stillLearningCount, color = QuizletOrange)
                Spacer(modifier = Modifier.height(8.dp))
                SummaryRow(label = "Terms left", count = 0, color = QuizletGrey)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // ==========================================
        // NEXT STEPS and ACTION BUTTONS
        // ==========================================
        Text("Next steps", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Practice with questions Button
        Button(
            onClick = onPracticeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QuizletPrimaryBtn),
            shape = RoundedCornerShape(50)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Practice",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Practice with questions", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Focus on Still Learning Button
        if (stillLearningCount > 0) {
            Button(
                onClick = onFocusStillLearning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                // Đổi thành QuizletSecondaryBtn để giống nền tối trong ảnh
                colors = ButtonDefaults.buttonColors(containerColor = QuizletSecondaryBtn),
                shape = RoundedCornerShape(50)
            ) {
                Text("Focus on $stillLearningCount Still learning cards", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Restart All Flashcard Button
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TextButton(onClick = onRestartAll) {
                Text(
                    text = "Restart Flashcards",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // FOOTER (BACK TO UNITS BUTTON)
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(
                onClick = onBack,
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Back to units", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// Component drawing each summary row (Know, Still learning, Terms left)
@Composable
fun SummaryRow(label: String, count: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = color.copy(alpha = 0.2f), // Blur the background color for better contrast like background
                shape = RoundedCornerShape(50) // Pill shape
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(text = count.toString(), color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MasteryDonutChart(
    knewCount: Int,
    stillLearningCount: Int,
    modifier: Modifier = Modifier
) {
    val total = knewCount + stillLearningCount
    val progressValue = if (total > 0) knewCount.toFloat() / total else 0f
    val percentage = (progressValue * 100).toInt()

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(100.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            val strokeWidth = 10.dp.toPx()

            drawArc(
                color = QuizletOrange,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )

            if (progressValue > 0f) {
                drawArc(
                    color = QuizletGreen,
                    startAngle = -90f,
                    sweepAngle = progressValue * 360f,
                    useCenter = false,
                    // Use StrokeCap.Butt to avoid rounded ends and create a clean donut look
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
            }
        }

        // Display percentage text in the center of the donut
        Text(
            text = "$percentage%",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun DeckCompletedScreenPreview() {
    DeckCompletedScreen(
        knewCount = 16,
        stillLearningCount = 24,
        onPracticeClick = { },
        onRestartAll = { },
        onFocusStillLearning = { },
        onBack = { }
    )
}