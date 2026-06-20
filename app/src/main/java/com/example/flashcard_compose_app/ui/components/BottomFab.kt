package com.example.flashcard_compose_app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

//@Composable
//fun BottomFab(
//    onCreateDeckClick: () -> Unit,
//    onCreateFlashcardClick: () -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//
//    Column(
//        horizontalAlignment = Alignment.End
//    ) {
//        // Animated visibility for the additional FABs
//        AnimatedVisibility(
//            visible = expanded,
//            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
//            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
//        ) {
//            Column(
//                horizontalAlignment = Alignment.End,
//                modifier = Modifier.padding(bottom = 16.dp)
//            ) {
//                // Create Flashcard FAB
//                FloatingActionButton(
//                    onClick = {
//                        onCreateFlashcardClick()
//                        expanded = false
//                    },
//                    modifier = Modifier
//                        .padding(bottom = 12.dp)
//                        .size(56.dp),
//                    containerColor = MaterialTheme.colorScheme.secondary
//                ) {
//                    Icon(
//                        Icons.Outlined.Edit,
//                        contentDescription = "Create Flashcard",
//                        tint = MaterialTheme.colorScheme.onSecondary
//                    )
//                }
//
//                // Create Deck FAB
//                FloatingActionButton(
//                    onClick = {
//                        onCreateDeckClick()
//                        expanded = false
//                    },
//                    modifier = Modifier.size(56.dp),
//                    containerColor = MaterialTheme.colorScheme.tertiary
//                ) {
//                    Icon(
//                        Icons.Default.Add,
//                        contentDescription = "Create Deck",
//                        tint = MaterialTheme.colorScheme.onTertiary
//                    )
//                }
//            }
//        }
//
//        // Main FAB
//        FloatingActionButton(
//            onClick = { expanded = !expanded },
//            containerColor = MaterialTheme.colorScheme.primary,
//            modifier = Modifier.size(56.dp)
//        ) {
//            Icon(
//                Icons.Default.Add,
//                contentDescription = "Add",
//                tint = MaterialTheme.colorScheme.onPrimary
//            )
//        }
//    }
//}

@Composable
fun BottomFab(
    onCreateDeckClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onCreateDeckClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Create Deck"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomFabPreview() {
    BottomFab(
        onCreateDeckClick = { /* TODO */ },
        //onCreateFlashcardClick = { /* TODO */ }
    )
}