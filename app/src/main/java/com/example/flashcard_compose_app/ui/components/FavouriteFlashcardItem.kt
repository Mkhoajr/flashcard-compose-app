package com.example.flashcard_compose_app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcard_compose_app.domain.model.Flashcard

@Composable
fun FavoriteFlashcardItem(
    flashcard: Flashcard,
    onRemoveFavorite: (Flashcard) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = flashcard.word,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                if (flashcard.reading.isNotEmpty() && flashcard.reading != flashcard.word) {
                    Text(
                        text = flashcard.reading,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = flashcard.meaning,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // STAR ICON - Always filled for favorites, with a click action to remove from favorites
            IconButton(onClick = { onRemoveFavorite(flashcard) }) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Remove from Favorites",
                    tint = Color(0xFFFFC107), // Amber color for filled star
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}