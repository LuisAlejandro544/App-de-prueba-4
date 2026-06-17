package com.example.service.handlers

import android.content.Context
import android.speech.tts.TextToSpeech
import com.example.data.model.ShortcutAction
import com.example.service.ProximitySensorHelper
import kotlinx.coroutines.delay

object ProximitySensorHandler : ActionHandler {
    override suspend fun execute(
        action: ShortcutAction,
        context: Context,
        textToSpeech: TextToSpeech?,
        isTtsReady: Boolean,
        onStatusUpdate: (String) -> Unit
    ) {
        val condition = action.parameters["condition"] ?: "TAPADO"
        val statusText = if (condition == "TAPADO") "Tapado (Cercanía)" else "Despejado (Lejanía)"
        onStatusUpdate("Sensor Proximidad: Esperando estado $statusText...")
        ProximitySensorHelper.waitForCondition(context, condition)
        onStatusUpdate("Proximidad activada!")
        delay(800)
    }
}
