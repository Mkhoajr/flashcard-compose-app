package com.example.flashcard_compose_app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcard_compose_app.domain.model.QuizHistory
import com.example.flashcard_compose_app.ui.theme.*

@Composable
fun QuizHistoryCard(item: QuizHistory, onClick: () -> Unit) {
    val scorePercentage = if (item.totalQuestions > 0) {
        item.correctAnswer.toFloat() / item.totalQuestions
    } else 0f

    val scoreColor = when {
        scorePercentage >= 0.8f -> ButtonKnew
        scorePercentage >= 0.5f -> AmberDark
        else -> ButtonNotKnow
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side with deck title and date
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.deckTitle,
                    color = IndigoDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Date: ${item.displayDate}",
                    color = Grey600,
                    fontSize = 14.sp
                )
            }

            // Right side with score and icon
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "${item.correctAnswer} / ${item.totalQuestions}",
                    color = scoreColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Score",
                    color = Grey500,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View Details",
                tint = Grey400
            )
        }
    }
}