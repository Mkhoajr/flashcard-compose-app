package com.example.flashcard_compose_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*

val IndigoPrimary = Color(0xFF3F51B5)
val BackgroundGray = Color(0xFFF8F9FA)
val TextDark = Color(0xFF111827)

@Composable
fun AnimatedGreetingSection(userName: String) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(com.example.flashcard_compose_app.R.raw.handshake_loop))
    val progress by animateLottieCompositionAsState(
        composition = composition,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundGray)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Greeting text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Welcome back,",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$userName!",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark,
                lineHeight = 32.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ready to smash today's goals?",
                fontSize = 14.sp,
                color = IndigoPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(120.dp)
                .padding(start = 16.dp)
        )
    }
}

@Preview
@Composable
fun AnimatedGreetingSectionPreview() {
    AnimatedGreetingSection(userName = "Alice")
}