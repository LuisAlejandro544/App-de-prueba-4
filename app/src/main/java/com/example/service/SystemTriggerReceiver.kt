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
import com.example.data.model.Shortcut
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SystemTriggerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.d("SystemTriggerReceiver", "Broadcast detectado: $action")

        val triggerType = when (action) {
            Intent.ACTION_BATTERY_LOW -> "BATTERY_LOW"
            Intent.ACTION_POWER_CONNECTED -> "POWER_CONNECTED"
            Intent.ACTION_POWER_DISCONNECTED -> "POWER_DISCONNECTED"
            else -> return
        }

        val triggerName = when (triggerType) {
            "BATTERY_LOW" -> "Batería baja"
            "POWER_CONNECTED" -> "Cargador conectado"
            "POWER_DISCONNECTED" -> "Cargador desconectado"
            else -> "Estado de sistema"
        }

        val database = ShortcutDatabase.getDatabase(context)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = database.shortcutDao()
                val shortcuts = dao.getAllShortcutsSync()

                // Filtrar los que estén habilitados y coincidan con el trigger actual
                val matchedShortcuts = shortcuts.filter { 
                    it.isTriggerEnabled && it.systemTrigger == triggerType 
                }

                Log.d("SystemTriggerReceiver", "Se encontraron ${matchedShortcuts.size} atajos para el trigger $triggerType")

                for (shortcut in matchedShortcuts) {
                    if (shortcut.isAutoTrigger) {
                        Log.d("SystemTriggerReceiver", "Ejecutando automáticamente: ${shortcut.name}")
                        // Iniciar ejecutor robusto en segundo plano mediante Foreground Service
                        ShortcutExecutorService.start(context, shortcut.id)
                        sendSystemTriggerNotification(context, shortcut.name, shortcut.id, isAutoMode = true, triggerName)
                    } else {
                        Log.d("SystemTriggerReceiver", "Ejecutando manualmente (esperando click): ${shortcut.name}")
                        sendSystemTriggerNotification(context, shortcut.name, shortcut.id, isAutoMode = false, triggerName)
                    }
                }
            } catch (e: Exception) {
                Log.e("SystemTriggerReceiver", "Error procesando triggers de sistema", e)
            }
        }
    }

    private fun sendSystemTriggerNotification(
        context: Context, 
        shortcutName: String, 
        shortcutId: Int, 
        isAutoMode: Boolean, 
        triggerName: String
    ) {
        val channelId = "actionstack_system_triggers"
        val channelName = "Gatillos de Sistema ActionStack"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones para atajos gatillados por el estado físico del teléfono (ej. batería/energía)"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val clickIntent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("RUN_SHORTCUT_ID", shortcutId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            shortcutId + 20000,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val titleText = "🔋 Evento: $shortcutName"
        val contentText = if (isAutoMode) {
            "Gatillo '$triggerName' detectado: Ejecutando pila automáticamente."
        } else {
            "Gatillo '$triggerName' detectado: Toca aquí para ver y ejecutar."
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titleText)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            notificationManager.notify(shortcutId + 20000, builder.build())
        } catch (e: Exception) {
            Log.e("SystemTriggerReceiver", "Error enviando notificación de sistema", e)
        }
    }
}
