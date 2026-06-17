package com.example.service.handlers

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.example.data.model.ShortcutAction
import com.example.service.MyAccessibilityService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

object WaitNotificationHandler : ActionHandler {
    override suspend fun execute(
        action: ShortcutAction,
        context: Context,
        textToSpeech: TextToSpeech?,
        isTtsReady: Boolean,
        onStatusUpdate: (String) -> Unit
    ) {
        val targetPackage = action.parameters["packageName"] ?: "Cualquiera"
        val targetSender = action.parameters["senderText"] ?: ""

        val statusMsg = if (targetSender.isNotBlank()) {
            "Esperando mensaje de '$targetSender'..."
        } else {
            "Esperando notificación..."
        }
        
        onStatusUpdate(statusMsg)
        
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "ActionStack: $statusMsg", Toast.LENGTH_LONG).show()
        }

        if (!MyAccessibilityService.isActive) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context, 
                    "Por favor, activa el Servicio de Accesibilidad para detectar notificaciones automáticamente.", 
                    Toast.LENGTH_LONG
                ).show()
            }
            Log.e("WaitNotificationHandler", "Servicio de Accesibilidad inactivo.")
            return
        }

        val deferredNotification = CompletableDeferred<Unit>()

        val listener = object : MyAccessibilityService.NotificationListener {
            override fun onNotificationReceived(packageName: String, title: String, text: String) {
                Log.d("WaitNotificationHandler", "Notificación recibida en handler: $packageName | Title: $title | Text: $text")
                
                // Comparación de Package Name
                val packageMatches = targetPackage.isBlank() || 
                        targetPackage.lowercase() == "cualquiera" || 
                        packageName.lowercase().contains(targetPackage.lowercase())

                // Comparación del Emisor o Contenido
                val senderMatches = targetSender.isBlank() || 
                        title.lowercase().contains(targetSender.lowercase()) || 
                        text.lowercase().contains(targetSender.lowercase())

                if (packageMatches && senderMatches) {
                    Log.d("WaitNotificationHandler", "¡Condición de notificación CUMPLIDA! Continuando flujo.")
                    deferredNotification.complete(Unit)
                }
            }
        }

        try {
            MyAccessibilityService.registerNotificationListener(listener)
            
            // Timeout de seguridad de 5 minutos (300,000 ms) para evitar bloquear el hilo de ejecución por siempre si nunca llega
            val result = withTimeoutOrNull(300_000L) {
                deferredNotification.await()
            }

            if (result == null) {
                Log.w("WaitNotificationHandler", "Timeout de 5 minutos esperando la notificación.")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Tiempo de espera agotado buscando la notificación.", Toast.LENGTH_SHORT).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Notificación detectada. Continuando atajo...", Toast.LENGTH_SHORT).show()
                }
            }
        } finally {
            MyAccessibilityService.unregisterNotificationListener(listener)
        }
    }
}
