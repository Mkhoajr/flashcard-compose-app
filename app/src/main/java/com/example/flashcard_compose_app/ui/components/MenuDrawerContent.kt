package com.example.flashcard_compose_app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcard_compose_app.data.AuthManager
import com.example.flashcard_compose_app.ui.navigation.Screen

@Composable
fun MenuDrawerContent(
    navController: NavController,
    authManager: AuthManager,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .width(340.dp)
    ) {
        Column(modifier = Modifier.statusBarsPadding()) {
            // User Profile Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            authManager.getUserName() ?: "User Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            authManager.getUserEmail() ?: "user@example.com",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            // Modes Section
            Text(
                "Modes",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )

            NavigationDrawerItem(
                label = { Text("Quiz History") },
                icon = { Icon(Icons.Default.History, contentDescription = "Quiz History") },
                selected = false,
                onClick = {
                    navController.navigate(Screen.QuizHistory.route) {

                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true

                        onClose()
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            NavigationDrawerItem(
                label = { Text("Favourite Cards") },
                icon = { Icon(Icons.Default.Favorite, contentDescription = "Favourite") },
                selected = false,
                onClick = {
                    navController.navigate(Screen.Favorites.route)
                    onClose()
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            NavigationDrawerItem(
                label = { Text("Statistics") },
                icon = { Icon(Icons.Default.Analytics, contentDescription = "Statistics") },
                selected = false,
                onClick = {
                    navController.navigate(Screen.Statistics.route)
                    onClose()
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            // Utility & Support Section
            Text(
                "Utility & Support",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )
            NavigationDrawerItem(
                label = { Text("Settings") },
                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                selected = false,
                onClick = {
                    navController.navigate(Screen.Settings.route)
                    onClose()
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            NavigationDrawerItem(
                label = { Text("About") },
                icon = { Icon(Icons.Default.Info, contentDescription = "About") },
                selected = false,
                onClick = {
                    navController.navigate(Screen.About.route)
                    onClose()
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Logout Section
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            NavigationDrawerItem(
                label = { Text("Logout") },
                icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout") },
                selected = false,
                onClick = {
                    authManager.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) // Clear back stack
                    }
                    onClose()
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuDrawerContentPreview() {
    val navController = NavController(LocalContext.current)
    val authManager = AuthManager(LocalContext.current)
    MenuDrawerContent(navController, authManager, onClose = {})
}