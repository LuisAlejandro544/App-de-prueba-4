package com.example.service.handlers

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.data.model.ShortcutAction
import kotlinx.coroutines.delay

object LaunchAppHandler : ActionHandler {
    override suspend fun execute(
        action: ShortcutAction,
        context: Context,
        textToSpeech: TextToSpeech?,
        isTtsReady: Boolean,
        onStatusUpdate: (String) -> Unit
    ) {
        val pack = action.parameters["packageName"] ?: ""
        if (pack.isNotBlank()) {
            try {
                val launchIntent = context.packageManager.getLaunchIntentForPackage(pack)
                if (launchIntent != null) {
                    launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(launchIntent)
                } else {
                    Toast.makeText(context, "Aplicación no encontrada: $pack", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error lanzando app: $pack", Toast.LENGTH_SHORT).show()
            }
        }
        delay(1000)
    }
}
