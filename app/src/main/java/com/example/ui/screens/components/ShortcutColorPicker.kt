package com.example.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.ShortcutViewModel

@Composable
fun ShortcutColorPicker(
    viewModel: ShortcutViewModel,
    colorPalette: List<String>
) {
    Column {
        Text(
            text = "Color del Banner",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(colorPalette) { hex ->
                val color = Color(android.graphics.Color.parseColor(hex))
                val isSelected = viewModel.shortcutFormColor.equals(hex, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { viewModel.shortcutFormColor = hex },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        val checkTint = if (
                            hex.startsWith("#D", ignoreCase = true) ||
                            hex.startsWith("#F", ignoreCase = true) ||
                            hex.startsWith("#E", ignoreCase = true) ||
                            hex.startsWith("#C", ignoreCase = true)
                        ) Color(0xFF1D1B20) else Color.White
                        
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = checkTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
