package com.example.flashcard_compose_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard_compose_app.data.AuthManager
import com.example.flashcard_compose_app.data.network.dto.request.DeckRequest
import com.example.flashcard_compose_app.data.repository.DeckRepository
import com.example.flashcard_compose_app.domain.model.Deck
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: DeckRepository, private val authManager: AuthManager) : ViewModel() {

    private val currentUserId = authManager.getUserId()?.toIntOrNull() ?: 0

    // Manage the List of root Decks (Tree folder)
    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks: StateFlow<List<Deck>> = _decks.asStateFlow()

    // Manage which Decks are currently expanded
    private val _expandedDeckIds = MutableStateFlow<Set<Int>>(emptySet())
    val expandedDeckIds: StateFlow<Set<Int>> = _expandedDeckIds.asStateFlow()

    // (Opt) State of Recent Decks
    // private val _recentDecks = MutableStateFlow<List<RecentDeck>>(emptyList())
    // val recentDecks = _recentDecks.asStateFlow()

    init {
        loadRootDecks()
    }

    fun loadRootDecks() {

        _expandedDeckIds.value = emptySet()

        viewModelScope.launch {
            val result = repository.getRootDecks(currentUserId)
            result.onSuccess { rootDecks ->
                println("DEBUG: Loaded ${rootDecks.size} root decks: ${rootDecks.map { it.id to it.title }}")
                _decks.value = rootDecks
            }
            result.onFailure { error ->
                println("error to load Deck: ${error.message}")
            }
        }
    }

    // Function to handle when user clicks on a Deck in the UI
    fun onDeckClicked(clickedDeck: Deck) {
        println("DEBUG: onDeckClicked - id=${clickedDeck.id}, title=${clickedDeck.title}, isUnit=${clickedDeck.isUnit}, subDecks.size=${clickedDeck.subDecks.size}")
        
        val currentExpanded = _expandedDeckIds.value.toMutableSet()

        // If the clicked deck is already expanded, we want to collapse it
        if (currentExpanded.contains(clickedDeck.id)) {
            currentExpanded.remove(clickedDeck.id)
            _expandedDeckIds.value = currentExpanded
            println("DEBUG: Collapsed deck ${clickedDeck.id}")
            return
        }

        // If folder is not yet opened, add ID to list so UI expands
        currentExpanded.add(clickedDeck.id)
        _expandedDeckIds.value = currentExpanded
        println("DEBUG: Expanded deck ${clickedDeck.id}")

        // IMPORTANT CHECK: If this folder has no child data yet, fetch from API
        if (clickedDeck.subDecks.isEmpty() && !clickedDeck.isUnit) {
            println("DEBUG: Fetching children for deck ${clickedDeck.id}")
            fetchSubDecks(clickedDeck.id)
        } else {
            println("DEBUG: No fetch needed - subDecks.isEmpty=${clickedDeck.subDecks.isEmpty()}, isUnit=${clickedDeck.isUnit}")
        }
    }

    // Fetch sub-decks and insert them into the correct position in the deck tree
    private fun fetchSubDecks(parentDeckId: Int) {
        viewModelScope.launch {

            val subDecksResult = repository.getSubDecks(parentDeckId, currentUserId)

            subDecksResult.onSuccess { subDecks ->
                if (subDecks.isNotEmpty()) {
                    // If has sub-decks, attach them to the tree
                    _decks.value = insertChildrenToTree(_decks.value, parentDeckId, subDecks)
                } else {
                    val unitsResult = repository.getUnitsByDeckId(parentDeckId, currentUserId)
                    unitsResult.onSuccess { units ->
                        _decks.value = insertChildrenToTree(_decks.value, parentDeckId, units)
                    }
                    unitsResult.onFailure { error ->
                        println("Error fetching units: ${error.message}")
                    }
                }
            }
            subDecksResult.onFailure { error ->
                println("Error fetching sub-decks: ${error.message}")
                val unitsResult = repository.getUnitsByDeckId(parentDeckId, currentUserId)
                unitsResult.onSuccess { units ->
                    _decks.value = insertChildrenToTree(_decks.value, parentDeckId, units)
                }
                unitsResult.onFailure { unitError ->
                    println("Error fetching units: ${unitError.message}")
                }
            }
        }
    }

    private fun insertChildrenToTree(
        currentList: List<Deck>,
        parentId: Int,
        newChildren: List<Deck>
    ): List<Deck> {
        println("DEBUG: insertChildrenToTree - parentId=$parentId, newChildren.size=${newChildren.size}")
        return currentList.map { deck ->
            if (deck.id == parentId) {
                // Found parent -> attach children
                println("DEBUG: Found parent deck $parentId, attaching ${newChildren.size} children")
                deck.copy(subDecks = newChildren)
            } else if (deck.subDecks.isNotEmpty()) {
                // Haven't found parent but this deck has children -> search deeper
                println("DEBUG: Searching deeper in deck ${deck.id} (${deck.subDecks.size} children)")
                deck.copy(subDecks = insertChildrenToTree(deck.subDecks, parentId, newChildren))
            } else {
                // Not parent and no children -> keep as is
                deck
            }
        }
    }

    // CRUD functions for Decks and Units (Create, Read, Update, Delete) - to be implemented
    // CREATE DECK / UNIT
    fun createDeck(title: String, authorId: Int, parentId: Int, isUnit: Boolean) {
        viewModelScope.launch {
            try {
                val request = DeckRequest(
                    title = title,
                    description = "",
                    authorId = authorId,
                    parentDeckId = parentId, // Receive ID (0 if it's Root, > 0 if it's sub-deck/unit)
                    isUnit = isUnit,
                    isPublic = false
                )

                val result = repository.createDeck(request)

                if (result.isSuccess) {
                    loadRootDecks()
                } else {
                    println("error to Create Deck: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // UPDATE DECK / UNIT
    fun updateDeck(deckId: Int, title: String, authorId: Int, parentId: Int, isUnit: Boolean) {
        viewModelScope.launch {
            try {
                val request = DeckRequest(
                    title = title,
                    description = "",
                    authorId = authorId,
                    parentDeckId = parentId,
                    isUnit = isUnit,
                    isPublic = false
                )

                val result = repository.updateDeck(deckId, request)

                if (result.isSuccess) {
                    loadRootDecks()
                } else {
                    println("error to Update Deck: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // DELETE DECK / UNIT
    fun deleteDeck(deckId: Int) {
        viewModelScope.launch {
            try {
                val result = repository.deleteDeck(deckId)
                if (result.isSuccess) {
                    // Reload Deck Tree when delete successfully (to reflect changes in UI)
                    loadRootDecks()
                } else {
                    println("error to Delete Deck: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}