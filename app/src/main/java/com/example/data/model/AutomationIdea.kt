package com.example.data.model

/**
 * Modelo de datos modular para una sugerencia o idea de integración de automatización en ActionStack.
 */
data class AutomationIdea(
    val title: String,
    val description: String,
    val iconKey: String
) {
    companion object {
        val ideas = listOf(
            AutomationIdea(
                title = "Enchufe de cargador",
                description = "Decir 'Cargador Conectado' mediante TTS y emitir una vibración breve al conectar el cable.",
                iconKey = "volume_up"
            ),
            AutomationIdea(
                title = "Ubicación por GPS",
                description = "Integrar geocercas locales en Android para avisar a un contacto de confianza al ingresar a un área.",
                iconKey = "home"
            ),
            AutomationIdea(
                title = "Liberación a F-Droid",
                description = "Esta app está diseñada libre de SDKs corporativos (Totalmente Offline). ¡Lista para empaquetarse con licencia GPLv3 de código abierto!",
                iconKey = "star"
            )
        )
    }
}
