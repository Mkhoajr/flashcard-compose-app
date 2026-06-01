package com.example.flashcard_compose_app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.flashcard_compose_app.data.AuthManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppLayout(
    navController: NavController,
    authManager: AuthManager,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        modifier = Modifier.fillMaxSize(),
        drawerState = drawerState,
        drawerContent = {
            MenuDrawerContent(
                navController = navController,
                authManager = authManager,
                onClose = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    drawerState = drawerState,
                    onSyncClick = { },
                    onImportClick = { },
                    onExportClick = { }
                )
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppLayoutPreview() {
    MainAppLayout(
        navController = rememberNavController(),
        authManager = AuthManager() // Provide a default AuthManager for preview
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Text("Main App Layout Content")
        }
    }
}