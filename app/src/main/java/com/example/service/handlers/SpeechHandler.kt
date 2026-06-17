package com.example.service.handlers

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.data.model.ShortcutAction
import kotlinx.coroutines.delay

object SpeechHandler : ActionHandler {
    override suspend fun execute(
        action: ShortcutAction,
        context: Context,
        textToSpeech: TextToSpeech?,
        isTtsReady: Boolean,
        onStatusUpdate: (String) -> Unit
    ) {
        if (isTtsReady && textToSpeech != null) {
            val text = action.parameters["text"] ?: "Silencio"
            val rateString = action.parameters["rate"] ?: "1.0"
            val rate = rateString.toFloatOrNull() ?: 1.0f
            val selectedVoiceName = action.parameters["voice"] ?: "Predeterminada"

            // Guardar voz de fábrica para poder restaurarla después
            val defaultVoice = try { textToSpeech.voice } catch (e: Exception) { null }

            try {
                textToSpeech.setSpeechRate(rate)
            } catch (e: Exception) {
                android.util.Log.e("SpeechHandler", "No se pudo cambiar la tasa de voz", e)
            }

            // Aplicar la voz seleccionada si corresponde
            if (selectedVoiceName != "Predeterminada" && selectedVoiceName.isNotEmpty()) {
                try {
                    val targetVoice = textToSpeech.voices?.find { it.name == selectedVoiceName }
                    if (targetVoice != null) {
                        textToSpeech.voice = targetVoice
                    }
                } catch (e: Exception) {
                    android.util.Log.e("SpeechHandler", "Error al aplicar voz personalizada: ${e.message}")
                }
            }

            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "shortcut")
            
            val baseDuration = (text.length * 80L).coerceAtLeast(1500L).coerceAtMost(5500L)
            // Escalar la espera de forma segura según la velocidad para que no corte palabras ni espere de más
            val expectedDuration = if (rate > 0.1f) (baseDuration / rate).toLong() else baseDuration
            delay(expectedDuration)
            
            // Reestablecer a velocidad y voz normal por buenas prácticas
            try {
                textToSpeech.setSpeechRate(1.0f)
                if (defaultVoice != null) {
                    textToSpeech.voice = defaultVoice
                }
            } catch (e: Exception) { /* ignore */ }
        } else {
            Toast.makeText(context, "Sintetizador no disponible", Toast.LENGTH_SHORT).show()
            delay(1000)
        }
    }
}
