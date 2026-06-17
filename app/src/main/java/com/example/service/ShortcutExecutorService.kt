package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.database.ShortcutDatabase
import kotlinx.coroutines.*
import java.util.Locale

class ShortcutExecutorService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    companion object {
        private const val CHANNEL_ID = "shortcut_executor_channel"
        private const val NOTIFICATION_ID = 54321

        fun start(context: Context, shortcutId: Int) {
            val intent = Intent(context, ShortcutExecutorService::class.java).apply {
                putExtra("SHORTCUT_ID", shortcutId)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initTextToSpeech()
    }

    private fun initTextToSpeech() {
        try {
            textToSpeech = TextToSpeech(this) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech?.language = Locale.getDefault()
                    isTtsReady = true
                    Log.d("ShortcutExecutorService", "TextToSpeech inicializado en el servicio Foreground")
                } else {
                    Log.e("ShortcutExecutorService", "Error al inicializar TextToSpeech en el servicio Foreground")
                }
            }
        } catch (e: Exception) {
            Log.e("ShortcutExecutorService", "Excepción inicializando TTS en servicio", e)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val shortcutId = intent?.getIntExtra("SHORTCUT_ID", -1) ?: -1
        if (shortcutId == -1) {
            stopSelf()
            return START_NOT_STICKY
        }

        val initialNotification = buildNotification("Iniciando atajo automático...", "")
        startForeground(NOTIFICATION_ID, initialNotification)

        serviceScope.launch(Dispatchers.IO) {
            try {
                val db = ShortcutDatabase.getDatabase(applicationContext)
                val shortcut = db.shortcutDao().getShortcutById(shortcutId)

                if (shortcut == null) {
                    Log.e("ShortcutExecutorService", "Atajo ID $shortcutId no encontrado.")
                    updateNotification("Error", "No se encontró el atajo")
                    delay(1000)
                    stopSelf()
                    return@launch
                }

                // Esperar un momento corto a que el TTS se inicialice si el atajo contiene alguna acción de voz
                val hasSpeechAction = shortcut.actions.any { 
                    it.type.displayName.lowercase().contains("voz") || it.type.name == "SPEECH" 
                }
                if (hasSpeechAction) {
                    var waitAttempts = 0
                    while (!isTtsReady && waitAttempts < 15) {
                        delay(200)
                        waitAttempts++
                    }
                }

                updateNotification(shortcut.name, "Ejecutando acciones en segundo plano...")

                for (index in shortcut.actions.indices) {
                    val action = shortcut.actions[index]
                    val stepText = "Pasos: ${index + 1}/${shortcut.actions.size}"
                    val actionLabel = "Ejecutando ${action.type.displayName}..."
                    
                    updateNotification(shortcut.name, "$stepText - $actionLabel")

                    try {
                        ActionExecutor.execute(
                            action = action,
                            context = applicationContext,
                            textToSpeech = textToSpeech,
                            isTtsReady = isTtsReady,
                            onStatusUpdate = { status ->
                                updateNotification(shortcut.name, "$stepText - $status")
                            }
                        )
                    } catch (e: Exception) {
                        Log.e("ShortcutExecutorService", "Error ejecutando acción $index en background service", e)
                    }
                    
                    delay(400) // Delay estético entre acciones
                }

                updateNotification(shortcut.name, "¡Finalizado con éxito!")
                delay(1200)

            } catch (e: Exception) {
                Log.e("ShortcutExecutorService", "Fallo general en la ejecución del servicio Foreground", e)
            } finally {
                withContext(Dispatchers.Main) {
                    stopSelf()
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun updateNotification(title: String, content: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, buildNotification(title, content))
    }

    private fun buildNotification(title: String, content: String): android.app.Notification {
        val clickIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            999,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("⚡ ActionStack: $title")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Ejecutor de Atajos"
            val descriptionText = "Muestra el progreso de los atajos ejecutándose en segundo plano de forma persistente."
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        Log.d("ShortcutExecutorService", "Deteniendo servicio de atajos.")
        serviceJob.cancel()
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (e: Exception) {
            Log.e("ShortcutExecutorService", "Error liberando TTS en onDestroy", e)
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
