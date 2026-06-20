package com.example.flashcard_compose_app.ui.screens

import android.net.Uri
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.flashcard_compose_app.domain.model.Deck
import com.example.flashcard_compose_app.domain.model.RecentDeck
import com.example.flashcard_compose_app.ui.components.*
import com.example.flashcard_compose_app.ui.navigation.Screen
import com.example.flashcard_compose_app.ui.viewmodels.AppViewModelProvider
import com.example.flashcard_compose_app.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    userId: Int,
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
    var parentDeckForNewSub by remember { mutableStateOf<Deck?>(null) }
    var showAddDeckDialog by remember { mutableStateOf(false) }
    var deckToEdit by remember { mutableStateOf<Deck?>(null) }
    var deckToDelete by remember { mutableStateOf<Deck?>(null) }

    var searchQuery by remember { mutableStateOf("") }

    // DECK LIST filtered
    val displayedDecks = remember(decks, searchQuery) {
        filterDecks(decks, searchQuery)
    }

    val focusManager = LocalFocusManager.current

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
                onCreateDeckClick = {
                    parentDeckForNewSub = null
                    showAddDeckDialog = true
                },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { homePadding -> // Get padding from Scaffold

    Column(
        modifier = Modifier
            .padding(homePadding)
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus() // Clear focus when tapping outside of TextField
                })
            }
    ) {
            // Welcome Section
            AnimatedGreetingSection(userName = userName)

            // Recent Learning Section
            RecentLearningDeck(recentDecks = recentDecks, onDeckClick = { /* TODO */ })

            Spacer(modifier = Modifier.height(16.dp))

            // SEARCH BAR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Deck, Lesson...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    // Just display clear button when there's text to clear
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // All Decks Section
            Text(
                text = "All Decks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Body
            DeckList(
                decks = displayedDecks,
                //decks = decks,
                expandedIds = expandedIds,
                onDeckClick = { clickedDeck ->
                    if (clickedDeck.isUnit) {

                        // 1. Chuẩn bị Tên hiển thị chung cho cả 2 luồng
                        val displayUnitTitle = clickedDeck.title
                        val safeUnitTitle = Uri.encode(displayUnitTitle)

                        val parentDeck = decks.find { it.id == clickedDeck.parentId }
                        val deckName = parentDeck?.title ?: "Default Deck"
                        val safeDeckName = Uri.encode(deckName)

                        // 2. BỘ LỌC THÔNG MINH (Phân luồng Cũ - Mới)
                        // ID sinh ảo từ JSON thường rất to (> 1 triệu) hoặc bằng 0
                        val isLegacyVirtualDeck = clickedDeck.id == 0 || clickedDeck.id > 1000000

                        if (isLegacyVirtualDeck) {
                            // ---> LUỒNG 1: DÀNH CHO NEJ (Dữ liệu cũ) <---
                            val formattedUnitId = clickedDeck.title
                                .replace(" ", "")
                                .replace("Unit0", "Unit") // Tạo chữ "Unit1"

                            val parentId = clickedDeck.parentId // Lấy ID cha (VD: 16)

                            // Truyền parentId và có kèm theo unitId
                            navController.navigate("flashcard_screen/$parentId?unitId=$formattedUnitId&deckName=$safeDeckName&unitTitle=$safeUnitTitle")

                            println("DEBUG: Đi luồng CŨ (NEJ) - API Unit: $formattedUnitId, Cha ID: $parentId")

                        } else {
                            // ---> LUỒNG 2: DÀNH CHO TEST UNIT (Kiến trúc mới) <---
                            val targetUnitId = clickedDeck.id // Lấy thẳng ID của chính nó (VD: 18)

                            // Chỉ truyền targetUnitId, KHÔNG TRUYỀN unitId (để Backend tự bắt được null)
                            navController.navigate("flashcard_screen/$targetUnitId?deckName=$safeDeckName&unitTitle=$safeUnitTitle")

                            println("DEBUG: Đi luồng MỚI (Test Unit) - Unit ID: $targetUnitId")
                        }

                    } else {
                        viewModel.onDeckClicked(clickedDeck)
                    }
                },

                onEditClick = { clickedDeck ->
                    deckToEdit = clickedDeck
                },

                onDeleteClick = { clickedDeck ->
                    deckToDelete = clickedDeck
                },

                onAddUnitClick = { clickedDeck ->
                    parentDeckForNewSub = clickedDeck // Save the parent deck for the new sub-deck/unit
                    showAddDeckDialog = true          // Open Dialog
                },

                onManageVocabClick = { clickedUnit ->
                    // Tái sử dụng lại logic check ID ảo của bạn
                    val isLegacyVirtualDeck = clickedUnit.id == 0 || clickedUnit.id > 1000000

                    if (isLegacyVirtualDeck) {
                        // ---> LUỒNG 1: DỮ LIỆU CŨ (NEJ) <---
                        // Truyền ID cha (parentId) và báo isUnit = true để API lấy tên Unit đi lọc
                        navController.navigate(
                            Screen.UnitManager.createRoute(
                                deckId = clickedUnit.parentId,
                                unitTitle = clickedUnit.title,
                                isUnit = true
                            )
                        )
                    } else {
                        // ---> LUỒNG 2: DỮ LIỆU MỚI TẠO <---
                        // Truyền ID của chính nó và báo isUnit = false để API load toàn bộ thẻ
                        navController.navigate(
                            Screen.UnitManager.createRoute(
                                deckId = clickedUnit.id,
                                unitTitle = clickedUnit.title,
                                isUnit = false
                            )
                        )
                    }
                },

                onQuizClick = { selectedUnit ->
                    // 1. Tái sử dụng logic kiểm tra Deck Ảo (Dữ liệu NEJ cũ)
                    val isLegacyVirtualDeck = selectedUnit.id == 0 || selectedUnit.id > 1000000

                    if (isLegacyVirtualDeck) {
                        // ---> LUỒNG 1: DỮ LIỆU CŨ (NEJ) <---
                        // PHẢI truyền ID của Deck Cha (parentId) thì Server mới tìm được
                        val safeParentId = selectedUnit.parentId ?: 0
                        navController.navigate(
                            Screen.Quiz.createRoute(deckId = safeParentId, unitTitle = selectedUnit.title)
                        )
                        println("DEBUG: QUIZ - Luồng CŨ: Gửi parentId = $safeParentId, unitTitle = ${selectedUnit.title}")
                    } else {
                        // ---> SECOND THREAD: NEW DATA (Kiến trúc chuẩn) <---
                        // Truyền thẳng ID của chính cái Unit đó
                        navController.navigate(
                            Screen.Quiz.createRoute(deckId = selectedUnit.id, unitTitle = selectedUnit.title)
                        )
                        println("DEBUG: QUIZ - Luồng MỚI: Gửi Unit ID = ${selectedUnit.id}, unitTitle = ${selectedUnit.title}")
                    }
                }
            )

            if (showAddDeckDialog) {
                AddDeckDialog(
                    parentDeckName = parentDeckForNewSub?.title, // Change title for Dialog
                    onDismiss = {
                        showAddDeckDialog = false
                        parentDeckForNewSub = null
                    },
                    onConfirm = { title, isUnit ->
                        // Nếu parentDeckForNewSub == null thì ID = 0 (Root Deck)
                        val parentId = parentDeckForNewSub?.id ?: 0

                        // Nếu là Root Deck thì mặc định isUnit = false
                        val finalIsUnit = if (parentId == 0) false else isUnit

                        viewModel.createDeck(
                            title = title,
                            authorId = userId,
                            parentId = parentId,
                            isUnit = finalIsUnit
                        )

                        showAddDeckDialog = false
                        parentDeckForNewSub = null
                    }
                )
            }

            if (deckToEdit != null) {
                EditDeckDialog(
                    deckToEdit = deckToEdit!!,
                    onDismiss = {
                        deckToEdit = null
                    },
                    onConfirm = { newTitle, newIsUnit ->
                        viewModel.updateDeck(
                            deckId = deckToEdit!!.id,
                            title = newTitle,
                            authorId = userId,
                            parentId = deckToEdit!!.parentId ?: 0,
                            isUnit = newIsUnit
                        )
                        deckToEdit = null
                    }
                )
            }

            if (deckToDelete != null) {
                DeleteConfirmationDialog(
                    title = "Delete Deck / Unit",
                    message = "Are you sure to Delete '${deckToDelete?.title}'?\n\nWarning: All Flashcards and Children inside will be Deleted!",
                    onConfirm = {
                        deckToDelete?.let { deck ->
                            viewModel.deleteDeck(deck.id)
                        }
                        deckToDelete = null // Close Dialog
                    },
                    onDismiss = {
                        deckToDelete = null // Cancel Deletion and Close Dialog
                    }
                )
            }
        }
    }
}

// Function to recursively filter decks based on search query
fun filterDecks(decks: List<Deck>, query: String): List<Deck> {
    if (query.isBlank()) return decks

    return decks.mapNotNull { deck ->
        // Kiểm tra xem tên thư mục hiện tại có khớp không
        val matches = deck.title.contains(query, ignoreCase = true)

        if (matches) {
            // Nếu cha đã khớp -> Giữ lại nguyên vẹn cha và toàn bộ con cháu
            deck
        } else {
            // Nếu cha không khớp -> Đi tìm xem có đứa con nào khớp không
            val filteredSubDecks = filterDecks(deck.subDecks, query)
            if (filteredSubDecks.isNotEmpty()) {
                // Nếu có con khớp -> Giữ lại cha, nhưng chỉ hiển thị những đứa con khớp
                deck.copy(subDecks = filteredSubDecks)
            } else {
                // Cả cha và con đều không khớp -> Loại bỏ hoàn toàn
                null
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        navController = rememberNavController(),
        userId = 1,
        userName = "Alice"
    )
}