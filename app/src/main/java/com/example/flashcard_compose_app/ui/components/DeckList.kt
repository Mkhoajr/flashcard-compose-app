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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import com.example.flashcard_compose_app.domain.model.Deck

@Composable
fun DeckList(
    decks: List<Deck>,
    expandedIds: Set<Int>,
    onDeckClick: (Deck) -> Unit,
    onEditClick: (Deck) -> Unit,
    onDeleteClick: (Deck) -> Unit,
    onAddUnitClick: (Deck) -> Unit,
    onManageVocabClick: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        deckTree(
            decks = decks,
            expandedIds = expandedIds,
            onDeckClick = onDeckClick,
            onEditClick = onEditClick,         // Nối cáp
            onDeleteClick = onDeleteClick,     // Nối cáp
            onAddUnitClick = onAddUnitClick,   // Nối cáp
            onManageVocabClick = onManageVocabClick,
            depth = 0,
            parentKey = "root"
        )
    }
}

fun LazyListScope.deckTree(
    decks: List<Deck>,
    expandedIds: Set<Int>,
    onDeckClick: (Deck) -> Unit,
    onEditClick: (Deck) -> Unit,       // Nối cáp
    onDeleteClick: (Deck) -> Unit,     // Nối cáp
    onAddUnitClick: (Deck) -> Unit,    // Nối cáp
    onManageVocabClick: (Deck) -> Unit,
    depth: Int,
    parentKey: String = ""
) {
    decks.forEach { deck ->
        val itemKey = "$parentKey/${deck.id}"
        item(key = itemKey) {
            val isExpanded = expandedIds.contains(deck.id)
            DeckItem(
                deck = deck,
                isExpanded = isExpanded,
                depth = depth,
                onDeckClick = onDeckClick,
                onEditClick = onEditClick, // Pass to DeckItem
                onDeleteClick = onDeleteClick,
                onAddUnitClick = onAddUnitClick,
                onManageVocabClick = onManageVocabClick
            )
        }

        if (expandedIds.contains(deck.id) && deck.subDecks.isNotEmpty()) {
            deckTree(
                decks = deck.subDecks,
                expandedIds = expandedIds,
                onDeckClick = onDeckClick,
                onEditClick = onEditClick,      // Recursive
                onDeleteClick = onDeleteClick,
                onAddUnitClick = onAddUnitClick,
                onManageVocabClick = onManageVocabClick,
                depth = depth + 1,
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
    onDeckClick: (Deck) -> Unit,
    // 🟢 THÊM 3 CALLBACK MỚI ĐỂ ĐẨY SỰ KIỆN LÊN MÀN HÌNH CHÍNH
    onEditClick: (Deck) -> Unit,
    onDeleteClick: (Deck) -> Unit,
    onAddUnitClick: (Deck) -> Unit,
    onManageVocabClick: (Deck) -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        label = "ArrowRotation"
    )

    // Menu state
    var expandedMenu by remember { mutableStateOf(false) }

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
            .padding(start = (depth * 16).dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {

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
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        imageVector = if (deck.isUnit) Icons.Default.Menu else Icons.Default.Folder,
                        contentDescription = "Loại thẻ",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

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
                                modifier = Modifier.rotate(rotation)
                            )
                        }

                        // Menu icon display on the rightmost side
                        Box {
                            IconButton(
                                onClick = { expandedMenu = true },
                                modifier = Modifier.size(32.dp).padding(start = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            DropdownMenu(
                                expanded = expandedMenu,
                                onDismissRequest = { expandedMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Modify Deck") },
                                    onClick = { expandedMenu = false; onEditClick(deck) },
                                    leadingIcon = { Icon(Icons.Default.Edit, "Edit") }
                                )

                                // Just show Manage Vocabulary option if this is a Unit (not a Deck)
                                if (deck.isUnit) {
                                    DropdownMenuItem(
                                        text = { Text("Manage Flashcards") },
                                        onClick = {
                                            expandedMenu = false
                                            onManageVocabClick(deck)
                                        },
                                        leadingIcon = { Icon(Icons.Default.List, "Manage") }
                                    )
                                }

                                // Just show Add Deck option if this is a Deck (not a Unit), since Units can't have sub-decks
                                if (!deck.isUnit) {
                                    DropdownMenuItem(
                                        text = { Text("Add Deck") },
                                        onClick = { expandedMenu = false; onAddUnitClick(deck) },
                                        leadingIcon = { Icon(Icons.Default.Add, "Insert") }
                                    )
                                }

                                HorizontalDivider()

                                DropdownMenuItem(
                                    text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                                    onClick = { expandedMenu = false; onDeleteClick(deck) },
                                    leadingIcon = { Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                                )
                            }
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
    DeckList(
        decks = sampleDecks,
        expandedIds = setOf(0),
        onDeckClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onAddUnitClick = {},
        onManageVocabClick = {}
    )
}
