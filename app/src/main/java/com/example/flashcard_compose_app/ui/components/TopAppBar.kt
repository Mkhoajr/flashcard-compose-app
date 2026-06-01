package com.example.flashcard_compose_app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    drawerState: DrawerState,
    onSyncClick: () -> Unit,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit
) {
    val scope = rememberCoroutineScope()

    TopAppBar(
        title = {
            Text(
                text = "Flashcard App",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onSyncClick) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Sync",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            KebabMenu(onImportClick = onImportClick, onExportClick = onExportClick)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun KebabMenu(onImportClick: () -> Unit, onExportClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            Icons.Default.MoreVert,
            contentDescription = "More",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Import", style = MaterialTheme.typography.bodyMedium) },
            onClick = {
                onImportClick()
                expanded = false
            },
            leadingIcon = null
        )
        DropdownMenuItem(
            text = { Text("Export", style = MaterialTheme.typography.bodyMedium) },
            onClick = {
                onExportClick()
                expanded = false
            },
            leadingIcon = null
        )
    }
}

@Preview
@Composable
fun TopAppBarPreview() {
    TopAppBar(
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
        onSyncClick = {},
        onImportClick = {},
        onExportClick = {}
    )
}