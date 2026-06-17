package com.example.ui.screens.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.icons.ShortcutIconHelper

@Composable
fun ShortcutPulseIcon(
    colorHex: String,
    iconName: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val activeColor = Color(android.graphics.Color.parseColor(colorHex))

    Box(
        modifier = Modifier
            .size(90.dp)
            .scale(pulseScale)
            .background(
                color = activeColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ShortcutIconHelper.getIcon(iconName),
            contentDescription = "Ejecución",
            tint = activeColor,
            modifier = Modifier.size(44.dp)
        )
    }
}
