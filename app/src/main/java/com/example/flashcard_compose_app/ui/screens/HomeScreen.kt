package com.example.flashcard_compose_app.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flashcard_compose_app.domain.model.Deck
import com.example.flashcard_compose_app.domain.model.RecentDeck
import com.example.flashcard_compose_app.ui.components.*
import com.example.flashcard_compose_app.ui.viewmodels.AppViewModelProvider
import com.example.flashcard_compose_app.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    userName: String,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory), // Inject HomeViewModel
) {

    // Dummy data
    val recentDecks = listOf(
        RecentDeck(Deck(1, "Minna no Nihongo", "admin1", 12, 50), 0L),
        RecentDeck(Deck(2, "Kanji N5", "admin1", 45, 50), 0L),
        RecentDeck(Deck(3, "SE vocab", "admin1", 0, 100), 0L)
    )

    val decks by viewModel.decks.collectAsState()
// val recentDecks by viewModel.recentDecks.collectAsState()
    val expandedIds by viewModel.expandedDeckIds.collectAsState()


    // Refresh decks when the screen is shown
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadRootDecks()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            BottomFab(
                onCreateDeckClick = { },
                onCreateFlashcardClick = { }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { homePadding -> // Get padding from Scaffold

    Column(
        modifier = Modifier
            .padding(homePadding)
            .fillMaxSize()
    ) {
            // Welcome Section
            AnimatedGreetingSection(userName = userName)

            // Recent Learning Section
            RecentLearningDeck(recentDecks = recentDecks, onDeckClick = { /* TODO */ })

            Spacer(modifier = Modifier.height(16.dp))

            // All Decks Section
            Text(
                text = "All Decks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Body
            DeckList(
                decks = decks,
                expandedIds = expandedIds,
                onDeckClick = { clickedDeck ->
                    if (clickedDeck.isUnit) {

                        // Use for API ("Unit1")
                        val formattedUnitId = clickedDeck.title
                            .replace(" ", "")
                            .replace("Unit0", "Unit")

                        // Use for UI ("Unit 01")
                        val displayUnitTitle = clickedDeck.title

                        val parentDeck = decks.find { it.id == clickedDeck.parentId }
                        val deckName = parentDeck?.title ?: "Default Deck"

                        val safeDeckName = Uri.encode(deckName)
                        val safeUnitTitle = Uri.encode(displayUnitTitle)

                        navController.navigate("flashcard_screen/${clickedDeck.parentId}?unitId=$formattedUnitId&deckName=$safeDeckName&unitTitle=$safeUnitTitle")

                        println("DEBUG: Navigating to FlashcardScreen for ${clickedDeck.totalCards} cards - API Unit: $formattedUnitId, UI Unit: $displayUnitTitle, DeckName: $deckName")
                    } else {
                        viewModel.onDeckClicked(clickedDeck)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = NavController(LocalContext.current),
        userName = "Alice Preview"
    )
}