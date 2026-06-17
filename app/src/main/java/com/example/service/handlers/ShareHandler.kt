package com.example.service.handlers

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import com.example.data.model.ShortcutAction
import kotlinx.coroutines.delay

object ShareHandler : ActionHandler {
    override suspend fun execute(
        action: ShortcutAction,
        context: Context,
        textToSpeech: TextToSpeech?,
        isTtsReady: Boolean,
        onStatusUpdate: (String) -> Unit
    ) {
        val text = action.parameters["text"] ?: ""
        val shareIntent = Intent().apply {
            this.action = Intent.ACTION_SEND
            this.putExtra(Intent.EXTRA_TEXT, text)
            this.type = "text/plain"
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val chooserIntent = Intent.createChooser(shareIntent, "Compartir desde Atajos").apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(chooserIntent)
        delay(1500)
    }
}
