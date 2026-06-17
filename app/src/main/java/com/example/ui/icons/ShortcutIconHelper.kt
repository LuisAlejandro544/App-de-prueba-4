package com.example.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

object ShortcutIconHelper {
    fun getIcon(name: String): ImageVector {
        return when (name.lowercase()) {
            "add" -> Icons.Default.Add
            "play" -> Icons.Default.PlayArrow
            "delete" -> Icons.Default.Delete
            "back" -> Icons.Default.ArrowBack
            "check" -> Icons.Default.Check
            "share" -> Icons.Default.Share
            "star" -> Icons.Default.Star
            "home" -> Icons.Default.Home
            "settings" -> Icons.Default.Settings
            "warning" -> Icons.Default.Warning
            "info" -> Icons.Default.Info
            "phone" -> Icons.Default.Call
            else -> Icons.Default.PlayArrow
        }
    }

    val availableIcons = listOf(
        "play" to "Rayo/Ejecutar",
        "star" to "Favorito/Símbolo",
        "share" to "Enlace/Compartir",
        "home" to "Hogar/Control",
        "settings" to "Automatización",
        "warning" to "Notificación",
        "info" to "Ayuda",
        "phone" to "Llamada"
    )
}
