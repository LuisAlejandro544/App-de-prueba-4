package com.example.service.handlers

import android.content.Context
import android.speech.tts.TextToSpeech
import com.example.data.model.ShortcutAction

interface ActionHandler {
    suspend fun execute(
        action: ShortcutAction,
        context: Context,
        textToSpeech: TextToSpeech?,
        isTtsReady: Boolean,
        onStatusUpdate: (String) -> Unit
    )
}
