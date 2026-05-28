package com.example.flashcard_compose_app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import com.example.flashcard_compose_app.domain.model.Deck

@Composable
fun DeckList(
    decks: List<Deck>,
    expandedIds: Set<Int>, // Opening state of each deck by id
    onDeckClick: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Provide a root parentKey to build unique keys for each item in the recursive tree
        deckTree(
            decks = decks,
            expandedIds = expandedIds,
            onDeckClick = onDeckClick,
            depth = 0,
            parentKey = "root"
        )
    }
}

fun LazyListScope.deckTree(
    decks: List<Deck>,
    expandedIds: Set<Int>,
    onDeckClick: (Deck) -> Unit,
    depth: Int,
    parentKey: String = ""
) {
    decks.forEach { deck ->
        // Draw current deck item
        // Use a path-like parentKey + id to guarantee uniqueness across the whole list
        val itemKey = "$parentKey/${deck.id}"
        item(key = itemKey) {
            val isExpanded = expandedIds.contains(deck.id)
            DeckItem(
                deck = deck,
                isExpanded = isExpanded,
                depth = depth, // Pass depth to DeckItem for indentation
                onDeckClick = onDeckClick
            )
        }

        // If this deck is expanded and has sub-decks, recursively draw them
        if (expandedIds.contains(deck.id) && deck.subDecks.isNotEmpty()) {
            deckTree(
                decks = deck.subDecks,
                expandedIds = expandedIds,
                onDeckClick = onDeckClick,
                depth = depth + 1, // Increase depth for sub-decks
                parentKey = itemKey
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckItem(
    deck: Deck,
    isExpanded: Boolean = false,
    depth: Int = 0,
    onDeckClick: (Deck) -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        label = "ArrowRotation"
    )

    val isChild = depth > 0
    val cardElevation = if (isChild) 0.dp else 4.dp
    val cardColor = if (isChild) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        onClick = { onDeckClick(deck) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (depth * 16).dp), // Indent
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {

            // If this is a child card, show a vertical colored bar on the left to indicate hierarchy
            if (isChild) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = deck.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!isChild && deck.author.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = deck.author,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${deck.learnedCards}/${deck.totalCards}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        // Check if this deck has sub-decks to decide which icon to show
                        if (deck.isUnit) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Learn Now",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Open Sub-Decks",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.rotate(rotation) // Apply rotate
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { if (deck.totalCards > 0) deck.learnedCards / deck.totalCards.toFloat() else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeckListPreview() {
    val sampleDecks = listOf(
        Deck(id = 1, title = "Art", author = "Jane Doe", learnedCards = 4, totalCards = 82),
        Deck(id = 2, title = "Earth Science", author = "John Smith", learnedCards = 0, totalCards = 60),
        Deck(id = 3, title = "Countries on a Map", author = "Geo Expert", learnedCards = 0, totalCards = 39),
        Deck(id = 4, title = "Musicians", author = "Music Guru", learnedCards = 0, totalCards = 76),
        Deck(id = 5, title = "Vocal Workout", author = "Voice Coach", learnedCards = 0, totalCards = 68)
    )
    DeckList(decks = sampleDecks, expandedIds = setOf(0), onDeckClick = {})
}
