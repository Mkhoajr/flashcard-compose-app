package com.example.flashcard_compose_app.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flashcard_compose_app.domain.model.Flashcard
import com.example.flashcard_compose_app.ui.theme.Amber
import com.example.flashcard_compose_app.ui.theme.ButtonKnew
import com.example.flashcard_compose_app.ui.theme.ButtonNotKnow
import com.example.flashcard_compose_app.ui.theme.Indigo
import com.example.flashcard_compose_app.ui.viewmodels.AppViewModelProvider
import com.example.flashcard_compose_app.ui.viewmodels.FlashcardViewModel

// STATEFUL COMPONENT (Contain state and logic)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    deckId: Int,
    unitId: String? = null,
    deckName: String = "",
    unitTitle: String = ""
) {
    val viewModel: FlashcardViewModel = viewModel(factory = AppViewModelProvider.FlashcardFactory)

    // Collect all state from ViewModel
    val flashcards by viewModel.flashcards.collectAsState()
    val learnedCount by viewModel.learnedCount.collectAsState()
    val stillLearningCount by viewModel.stillLearningCount.collectAsState()
    val currentCardIndex by viewModel.currentCardIndex.collectAsState()
    val isFlipped by viewModel.isFlipped.collectAsState()
    val favourites by viewModel.favorites.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFlashcards(deckId, unitId)
        viewModel.setDeckName(deckName)
        viewModel.setUnitTitle(unitTitle)
    }

    // Check if we have finished the deck
    val isFinished = flashcards.isNotEmpty() && currentCardIndex >= flashcards.size

    if (isFinished) {
        DeckCompletedScreen(
            knewCount = learnedCount,
            stillLearningCount = stillLearningCount,
            onPracticeClick = { },
            onRestartAll = { viewModel.restartAll() },
            onFocusStillLearning = { viewModel.focusOnStillLearning() },
            onBack = { navController.popBackStack() }
        )
        return // Prevents the rest of the screen from loading
    }

    val currentCard = flashcards.getOrNull(currentCardIndex)

    // Pass state to stateless component
    FlashcardScreenContent(
        modifier = modifier,
        isLoading = isLoading,
        flashcardsCount = flashcards.size,
        currentCard = currentCard,
        deckName = deckName,
        unitTitle = unitTitle,
        currentIndex = if (flashcards.isEmpty()) 0 else currentCardIndex + 1,
        isFlipped = isFlipped,
        isFavourite = currentCard?.let { favourites.contains(it.id) } ?: false,
        onClose = { navController.popBackStack() },
        onFlip = { viewModel.toggleFlip() },
        onFavouriteToggle = { currentCard?.let { viewModel.toggleFavorite(it.id) } },
        onNotLearned = {
            viewModel.markCardAsNotLearned()
        },
        onLearned = {
            viewModel.markCardAsLearned()
        },
        onUndo = {
            viewModel.undoLastAction()
        },
    )
}

// STATELESS COMPONENT (Just contain UI)
@Composable
fun FlashcardScreenContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    flashcardsCount: Int,
    currentCard: Flashcard?,
    deckName: String,
    unitTitle: String,
    currentIndex: Int,
    isFlipped: Boolean,
    onClose: () -> Unit,
    onFlip: () -> Unit,
    isFavourite: Boolean,
    onFavouriteToggle: () -> Unit,
    onNotLearned: () -> Unit,
    onLearned: () -> Unit,
    onUndo: () -> Unit,
) {
    if (isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (flashcardsCount == 0 || currentCard == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No flashcards available")
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Indigo)
        ) {
            // Header
            FlashcardHeaderContent(
                deckName = deckName,
                unitTitle = unitTitle,
                onClose = onClose
            )

            // Progress Section
            ProgressSection(
                currentIndex = currentIndex,
                totalCount = flashcardsCount,
                onUndo = onUndo,
                canUndo = currentIndex > 0
            )

            // Main Content Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                FlashcardContent(
                    card = currentCard,
                    isFlipped = isFlipped,
                    onFlip = onFlip,
                    isFavourite = isFavourite,
                    onFavouriteToggle = onFavouriteToggle
                )
            }

            // Bottom Buttons
            ActionButtonsSection(
                onStillLearning = onNotLearned,
                onKnew = onLearned
            )
        }
    }
}

@Composable
private fun FlashcardHeaderContent(
    deckName: String,
    unitTitle: String,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = deckName,
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            )
            if (unitTitle.isNotEmpty()) {
                Text(
                    text = unitTitle,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 15.sp
                )
            }
        }
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ProgressSection(
    currentIndex: Int,totalCount: Int,
    onUndo: () -> Unit,
    canUndo: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onUndo,
                enabled = canUndo,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Undo,
                    contentDescription = "Undo",
                    tint = if (canUndo) Color.White else Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(25.dp)
                )
            }

            Text(
                text = "$currentIndex/$totalCount",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress Bar
        LinearProgressIndicator(
            progress = { if (totalCount > 0) currentIndex.toFloat() / totalCount else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun ActionButtonsSection(
    onStillLearning: () -> Unit,
    onKnew: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ElevatedButton(
            onClick = onStillLearning,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = ButtonNotKnow,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                "Still learning",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        ElevatedButton(
            onClick = onKnew,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = ButtonKnew,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                "I knew",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun FlashcardContent(
    card: Flashcard,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    isFavourite: Boolean,
    onFavouriteToggle: () -> Unit
) {
    // Optimize Animation: Reduce the time when flipped (400ms) and using Easing to flip more naturally
    val rotationY by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "FlipAnimation"
    )

    Box(
        modifier = Modifier
            .aspectRatio(0.75f)
            .shadow(8.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onFlip() }
            .graphicsLayer {
                this.rotationY = rotationY
                // Increase cameraDistance to flip 3D flashcard without distortion (default is 8, we can increase it to 12 or more)
                cameraDistance = 12 * density
            }
    ) {
        // ==========================================
        // FRONT-SIDE (Vocab + Icon)
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .graphicsLayer {
                    // Just display front side when rotationY <= 90, otherwise hide it to prevent overlap with back side
                    alpha = if (rotationY <= 90f) 1f else 0f
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Audio Icon & Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                IconButton(
                    onClick = onFavouriteToggle,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isFavourite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favorite",
                        tint = if (isFavourite) Amber else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = { /* TODO: Play audio */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Play Audio",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Push content to center
            Spacer(modifier = Modifier.weight(1f))

            if (card.reading.isNotEmpty() && !card.reading.equals(card.word, ignoreCase = true)) {
                Text(
                    text = card.reading,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
            }

            val wordLength = card.word.length
            val wordFontSize = when {
                wordLength > 15 -> 20.sp
                wordLength > 10 -> 24.sp
                else -> 32.sp
            }
            val wordLineHeight = when {
                wordLength > 15 -> 28.sp
                wordLength > 10 -> 32.sp
                else -> 40.sp
            }

            Text(
                text = card.word,
                fontSize = wordFontSize,
                lineHeight = wordLineHeight,
                fontWeight = FontWeight.Bold,
                color = Indigo,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        // ==========================================
        // BEHIND SIDE (Display meaning)
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .graphicsLayer {
                    // Just display behind side when rotationY > 90, otherwise hide it to prevent overlap with front side
                    alpha = if (rotationY > 90f) 1f else 0f
                    // Avoid mirror effect by flipping back when showing the behind side
                    this.rotationY = 180f
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val meaningLength = card.meaning.length
            val meaningFontSize = if (meaningLength > 20) 22.sp else 28.sp
            val meaningLineHeight = if (meaningLength > 20) 30.sp else 36.sp

            Text(
                text = card.meaning,
                fontSize = meaningFontSize,
                lineHeight = meaningLineHeight,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
fun FlashcardScreenPreview() {
    FlashcardScreenPreviewContent()
}

@Composable
fun FlashcardScreenPreviewContent() {
    FlashcardScreenContent(
        isLoading = false,
        flashcardsCount = 10,
        currentCard = Flashcard(
            id = 1,
            unit = "Unit 1",
            word = "はじめまして、どうぞよろしくおねがいします",
            reading = "はじめまして、どうぞよろしくおねがいします",
            meaning = "Hello",
            imagePath = null,
            audioPath = null,
            status = "not-learned"
        ),
        deckName = "NEJ-N5-Vocab",
        unitTitle = "Unit 1",
        currentIndex = 3,
        isFlipped = false,
        isFavourite = false,
        onClose = {},
        onFlip = {},
        onFavouriteToggle = {},
        onNotLearned = {},
        onLearned = {},
        onUndo = {},
    )
}