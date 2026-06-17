package com.example.service.handlers

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.data.model.ShortcutAction
import kotlinx.coroutines.delay

object WaitHandler : ActionHandler {
    override suspend fun execute(
        action: ShortcutAction,
        context: Context,
        textToSpeech: TextToSpeech?,
        isTtsReady: Boolean,
        onStatusUpdate: (String) -> Unit
    ) {
        val secString = action.parameters["seconds"] ?: "5"
        val secs = secString.toLongOrNull() ?: 5L
        Toast.makeText(context, "Esperando $secs segundos...", Toast.LENGTH_SHORT).show()
        delay(secs * 1000L)
    }
}
