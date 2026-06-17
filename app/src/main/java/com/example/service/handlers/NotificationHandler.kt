package com.example.service.handlers

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.data.model.ShortcutAction
import kotlinx.coroutines.delay

object NotificationHandler : ActionHandler {
    override suspend fun execute(
        action: ShortcutAction,
        context: Context,
        textToSpeech: TextToSpeech?,
        isTtsReady: Boolean,
        onStatusUpdate: (String) -> Unit
    ) {
        val title = action.parameters["title"] ?: "ActionStack"
        val message = action.parameters["message"] ?: "¡Notificación de Acción!"
        sendSystemNotification(context, title, message)
        delay(1000)
    }

    private fun sendSystemNotification(context: Context, title: String, message: String) {
        val channelId = "actionstack_notifications_channel"
        val channelName = "Notificaciones de ActionStack"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                channelName,
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones enviadas por las tareas de ActionStack"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        } catch (e: Exception) {
            Log.e("NotificationHandler", "Error al enviar notificación", e)
        }
    }
}
