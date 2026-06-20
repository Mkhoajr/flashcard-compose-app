package com.example.flashcard_compose_app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcard_compose_app.domain.model.QuizDetail
import com.example.flashcard_compose_app.ui.theme.*

@Composable
fun QuizDetailCard(detail: QuizDetail) {

    // Classify color depend on result of answer
    val cardBackgroundColor = if (detail.isCorrect) ButtonKnew.copy(alpha = 0.1f) else ButtonNotKnow.copy(alpha = 0.1f)
    val statusColor = if (detail.isCorrect) ButtonKnew else ButtonNotKnow
    val statusIcon = if (detail.isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = statusIcon,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = detail.word,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IndigoDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Chosen User's answer
                Text(
                    text = "Your Answer: ${if(detail.userAnswer == "SKIPPED") "Don't Know" else detail.userAnswer}",
                    fontSize = 14.sp,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )

                // If the answer is wrong, show the correct answer
                if (!detail.isCorrect) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Correct Answer: ${detail.correctAnswer}",
                        fontSize = 14.sp,
                        color = Grey700,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}