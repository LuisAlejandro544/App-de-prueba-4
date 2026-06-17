package com.example.service.handlers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.data.model.ShortcutAction
import kotlinx.coroutines.delay

object OpenUrlHandler : ActionHandler {
    override suspend fun execute(
        action: ShortcutAction,
        context: Context,
        textToSpeech: TextToSpeech?,
        isTtsReady: Boolean,
        onStatusUpdate: (String) -> Unit
    ) {
        var url = action.parameters["url"] ?: "https://www.google.com"
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://$url"
        }
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir la URL", Toast.LENGTH_SHORT).show()
        }
        delay(1200)
    }
}
