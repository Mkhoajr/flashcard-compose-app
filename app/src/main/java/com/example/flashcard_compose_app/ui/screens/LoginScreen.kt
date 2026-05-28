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
import com.example.flashcard_compose_app.ui.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController? = null,
    onSignIn: () -> Unit = {},
    onRegister: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onSocialLogin: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val authRepository = remember { AuthRepository(RetrofitClient.authApiService) }
    val loginViewModel = remember { LoginViewModel(authManager, authRepository) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by loginViewModel.loginState.collectAsState()
    val scope = rememberCoroutineScope()

    // Handle login state changes
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginViewModel.LoginState.Success -> {
                navController?.navigate(Screen.Home.route) ?: onSignIn()
            }
            else -> {} // Handle other states if needed
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login here",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Indigo
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Welcome back you've been missed!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onForgotPassword) {
                Text("Forgot your password?", color = Indigo, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Show error message if any
        if (loginState is LoginViewModel.LoginState.Error) {
            Text(
                text = (loginState as LoginViewModel.LoginState.Error).message,
                color = Color.Red,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                scope.launch {
                    loginViewModel.login(email, password)
                }
            },
            enabled = loginState !is LoginViewModel.LoginState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(6.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo)
        ) {
            if (loginState is LoginViewModel.LoginState.Loading) {
                CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp))
            } else {
                Text("Sign in", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                navController?.navigate(Screen.Register.route) ?: onRegister()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Create new account", color = Color.Black, fontSize = 15.sp)
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
fun LoginScreenPreview() {
    LoginScreen()
}