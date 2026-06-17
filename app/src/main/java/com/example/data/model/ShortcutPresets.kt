package com.example.data.model

/**
 * Proveedor de presets para la Galería de Atajos de ActionStack.
 */
object ShortcutPresets {
    val presets = listOf(
        Shortcut(
            name = "Lectura de Bienvenida",
            colorHex = "#F1DBFA", // Lavender
            iconName = "star",
            actions = listOf(
                ShortcutAction(ActionType.SPEECH, mapOf("text" to "Hola. Te doy la bienvenida a ActionStack. Todo funciona correctamente.")),
                ShortcutAction(ActionType.VIBRATE)
            )
        ),
        Shortcut(
            name = "Detector de Cercanía",
            colorHex = "#D1E8D1", // Mint Green
            iconName = "warning",
            actions = listOf(
                ShortcutAction(ActionType.SHOW_MESSAGE, mapOf("message" to "Pasa tu mano por el sensor superior para continuar.")),
                ShortcutAction(ActionType.PROXIMITY_SENSOR, mapOf("condition" to "TAPADO")),
                ShortcutAction(ActionType.SPEECH, mapOf("text" to "¡Sensor detectado! Has tapado el sensor de proximidad.")),
                ShortcutAction(ActionType.VIBRATE)
            )
        ),
        Shortcut(
            name = "Verificador de Batería",
            colorHex = "#FFF0B5", // Yellow Butter
            iconName = "settings",
            actions = listOf(
                ShortcutAction(ActionType.BATTERY_LEVEL, mapOf("percentage" to "20", "comparison" to "MAYOR")),
                ShortcutAction(ActionType.SPEECH, mapOf("text" to "Control de energía correcto. Continuamos.")),
                ShortcutAction(ActionType.SHOW_MESSAGE, mapOf("message" to "Batería por encima del 20%."))
            )
        ),
        Shortcut(
            name = "Fórmula JS Offline",
            colorHex = "#FFDBCB", // Peach
            iconName = "settings",
            actions = listOf(
                ShortcutAction(ActionType.CUSTOM_JS, mapOf("script" to "// Ejemplo de script matemático offline\nconst residuo = 120 % 7;\nreturn 'El residuo JS calculado localmente es: ' + residuo;"))
            )
        )
    )
}
