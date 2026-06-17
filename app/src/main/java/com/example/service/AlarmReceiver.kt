package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.database.ShortcutDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val shortcutId = intent.getIntExtra("shortcut_id", -1)
        if (shortcutId == -1) return

        Log.d("AlarmReceiver", "Activando alarma para atajo ID: $shortcutId")

        val database = ShortcutDatabase.getDatabase(context)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = database.shortcutDao()
                val shortcut = dao.getShortcutById(shortcutId) ?: return@launch

                if (!shortcut.isTriggerEnabled) {
                    Log.d("AlarmReceiver", "El trigger de la alarma no está habilitado para: ${shortcut.name}")
                    return@launch
                }

                // Decidir según si es de activación automática o manual
                if (shortcut.isAutoTrigger) {
                    Log.d("AlarmReceiver", "Activación automática habilitada para: ${shortcut.name}")
                    // Iniciar el servicio en primer plano para ejecutar la secuencia de manera robusta
                    ShortcutExecutorService.start(context, shortcutId)
                    // Enviar notificación de estado pasivo informando del lanzamiento automático
                    sendClockNotification(context, shortcut.name, shortcutId, isAutoMode = true)
                } else {
                    Log.d("AlarmReceiver", "Activación manual pausada (esperando click en la notificación) para: ${shortcut.name}")
                    // Enviar notificación interactiva que requiere toque para activarse
                    sendClockNotification(context, shortcut.name, shortcutId, isAutoMode = false)
                }

            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Error en onReceive de la alarma", e)
            }
        }
    }

    private fun sendClockNotification(context: Context, shortcutName: String, shortcutId: Int, isAutoMode: Boolean) {
        val channelId = "actionstack_clock_triggers"
        val channelName = "Programación Reloj ActionStack"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones para atajos programados con reloj"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Si es automático, al hacer click en la notificación ya cargada solo abrimos la app de forma segura,
        // si es manual, le pasamos el intent extra "RUN_SHORTCUT_ID" para que empiece al pulsar.
        val clickIntent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (!isAutoMode) {
                putExtra("RUN_SHORTCUT_ID", shortcutId)
            }
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            shortcutId,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val titleText = if (isAutoMode) "⏰ Automatización: $shortcutName" else "⏰ Reloj Programado: $shortcutName"
        val contentText = if (isAutoMode) {
            "La pila de acciones se está ejecutando de forma totalmente automática."
        } else {
            "Tu pila de acciones se ha despertado. ¡Toca para ejecutar!"
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(titleText)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            notificationManager.notify(shortcutId + 10000, builder.build())
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Error enviando notificación del reloj", e)
        }
    }
}
