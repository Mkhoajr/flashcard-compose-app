package com.example.flashcard_compose_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flashcard_compose_app.R
import com.example.flashcard_compose_app.data.AuthManager
import com.example.flashcard_compose_app.data.network.RetrofitClient
import com.example.flashcard_compose_app.data.repository.AuthRepository
import com.example.flashcard_compose_app.ui.components.SocialAuthButton
import com.example.flashcard_compose_app.ui.navigation.Screen
import com.example.flashcard_compose_app.ui.theme.Indigo
import com.example.flashcard_compose_app.ui.theme.White
import com.example.flashcard_compose_app.ui.viewmodels.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController? = null,
    onSignUp: () -> Unit = {},
    onLogin: () -> Unit = {},
    onSocialLogin: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val authRepository = remember { AuthRepository(RetrofitClient.authApiService) }
    val viewModel = remember { RegisterViewModel(authRepository, authManager) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Indigo
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Create an account so you can explore all the existing decks and start learning!",
            fontSize = 15.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("Email") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading // Block to not enter when loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.username,
            onValueChange = { viewModel.username = it },
            label = { Text("Username") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading // Block to not enter when loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text("Password") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.confirmPassword,
            onValueChange = { viewModel.confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        viewModel.errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = {
                // Handle registration logic in ViewModel and navigate on success
                viewModel.onSignUpClick(
                    onSuccess = {
                        navController?.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        } ?: onSignUp()
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(6.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo),
        ) {
            // Change UI when loading
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp))
            } else {
                Text("Sign up", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                navController?.popBackStack() ?: onLogin()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Already have an account", color = Color.Black, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Or continue with", color = Color.Gray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            SocialAuthButton(
                iconRes = R.drawable.ic_google,
                contentDesc = "Google"
            ) { onSocialLogin("google") }
            SocialAuthButton(
                icon = Icons.Filled.Facebook,
                contentDesc = "Facebook"
            ) { onSocialLogin("facebook") }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}
