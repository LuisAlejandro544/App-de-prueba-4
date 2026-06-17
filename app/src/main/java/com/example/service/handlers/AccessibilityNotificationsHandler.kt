package com.example.service.handlers

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.data.model.ShortcutAction
import com.example.service.MyAccessibilityService
import kotlinx.coroutines.delay

object AccessibilityNotificationsHandler : ActionHandler {
    override suspend fun execute(
        action: ShortcutAction,
        context: Context,
        textToSpeech: TextToSpeech?,
        isTtsReady: Boolean,
        onStatusUpdate: (String) -> Unit
    ) {
        if (MyAccessibilityService.isActive) {
            MyAccessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
        } else {
            Toast.makeText(context, "Activa el Servicio de Accesibilidad para usar esta acción", Toast.LENGTH_LONG).show()
            AccessibilityHelper.openAccessibilitySettings(context)
        }
        delay(800)
    }
}
