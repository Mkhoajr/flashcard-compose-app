package com.example.flashcard_compose_app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.flashcard_compose_app.ui.theme.White

@Composable
fun SocialAuthButton(
    icon: ImageVector? = null,
    iconRes: Int? = null,
    contentDesc: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier.size(50.dp),
        contentPadding = PaddingValues(0.dp),
        border = ButtonDefaults.outlinedButtonBorder,
        colors = ButtonDefaults.outlinedButtonColors(containerColor = White)
    ) {
        if (iconRes != null) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = contentDesc,
                modifier = Modifier.size(24.dp)
            )
        } else if (icon != null) {
            Icon(
                icon,
                contentDescription = contentDesc,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}