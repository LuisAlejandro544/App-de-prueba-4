package com.example.service.handlers

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.data.model.ShortcutAction
import kotlinx.coroutines.delay

object ShowMessageHandler : ActionHandler {
    override suspend fun execute(
        action: ShortcutAction,
        context: Context,
        textToSpeech: TextToSpeech?,
        isTtsReady: Boolean,
        onStatusUpdate: (String) -> Unit
    ) {
        val msg = action.parameters["message"] ?: "Hola"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        delay(1200)
    }
}
