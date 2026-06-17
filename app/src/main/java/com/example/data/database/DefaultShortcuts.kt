package com.example.data.database

import com.example.data.model.ActionType
import com.example.data.model.Shortcut
import com.example.data.model.ShortcutAction

object DefaultShortcuts {
    val list = listOf(
        Shortcut(
            name = "Bienvenida a ActionStack",
            colorHex = "#F1DBFA", // Lavender
            iconName = "star",
            actions = listOf(
                ShortcutAction(ActionType.SHOW_MESSAGE, mapOf("message" to "¡Bienvenido a ActionStack!")),
                ShortcutAction(ActionType.SPEECH, mapOf("text" to "Hola. Te doy la bienvenida a ActionStack. Crea automatizaciones increíbles usando bloques secuenciales de construcción.")),
                ShortcutAction(ActionType.VIBRATE)
            )
        ),
        Shortcut(
            name = "Voz y Vibración",
            colorHex = "#D1E8D1", // Sage Green
            iconName = "volume_up",
            actions = listOf(
                ShortcutAction(ActionType.VIBRATE),
                ShortcutAction(ActionType.SPEECH, mapOf("text" to "Vibración completada. Ahora leyendo este texto de tu pila de acciones.")),
                ShortcutAction(ActionType.SHOW_MESSAGE, mapOf("message" to "Sintetizador de texto finalizado."))
            )
        ),
        Shortcut(
            name = "Copiar y Compartir",
            colorHex = "#D0E4FF", // Sky Blue
            iconName = "share",
            actions = listOf(
                ShortcutAction(ActionType.SHOW_MESSAGE, mapOf("message" to "Preparando texto para compartir...")),
                ShortcutAction(ActionType.SHARE, mapOf("text" to "Hola, estoy enviando este texto automatizado mediante ActionStack."))
            )
        ),
        Shortcut(
            name = "Visitar Google",
            colorHex = "#FFDBCB", // Peach
            iconName = "language",
            actions = listOf(
                ShortcutAction(ActionType.SHOW_MESSAGE, mapOf("message" to "Abriendo navegador web")),
                ShortcutAction(ActionType.OPEN_URL, mapOf("url" to "https://www.google.com"))
            )
        )
    )
}
