package com.example.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.example.data.database.ShortcutDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class ShakeSensorHelper(private val context: Context) : SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    
    private var lastUpdate: Long = 0
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private val shakeThreshold = 950 // Sensibilidad de agitación balanceada
    private var lastShakeTime: Long = 0

    fun startListening() {
        try {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if (accelerometer != null) {
                sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
                Log.d("ShakeSensorHelper", "Escuchando hardware del sensor de agitación registrado con éxito")
            } else {
                Log.w("ShakeSensorHelper", "Acelerómetro no disponible en este dispositivo")
            }
        } catch (e: Exception) {
            Log.e("ShakeSensorHelper", "Error registrando ShakeSensorHelper", e)
        }
    }

    fun stopListening() {
        try {
            sensorManager?.unregisterListener(this)
            Log.d("ShakeSensorHelper", "Deteniendo sensor de agitación")
        } catch (e: Exception) {
            Log.e("ShakeSensorHelper", "Error deteniendo ShakeSensorHelper", e)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Calculate direct gravity G-Force
        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH

        // Total G-force received (1.0f when stationary)
        val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

        val curTime = System.currentTimeMillis()
        // Threshold around 2.1f of G-Force represents a deliberate shake motion
        if (gForce > 2.1f) {
            // Depuración de intervalo de 2 segundos para evitar repetir disparos continuos
            if (curTime - lastShakeTime > 2000) {
                lastShakeTime = curTime
                Log.d("ShakeSensorHelper", "¡Gatillo de Agitación detectado con fuerza G: $gForce!")
                triggerShakeAction()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun triggerShakeAction() {
        val database = ShortcutDatabase.getDatabase(context)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = database.shortcutDao()
                val shortcuts = dao.getAllShortcutsSync()
                val matched = shortcuts.filter { it.isTriggerEnabled && it.systemTrigger == "SHAKE" }
                
                Log.d("ShakeSensorHelper", "Se encontraron ${matched.size} atajos para ejecutar con agitación")
                
                for (shortcut in matched) {
                    if (shortcut.isAutoTrigger) {
                        launchShortcutAutomatically(shortcut.id)
                    } else {
                        sendNotification(shortcut.id, shortcut.name)
                    }
                }
            } catch (e: Exception) {
                Log.e("ShakeSensorHelper", "Error leyendo base de datos para agitación", e)
            }
        }
    }

    private fun launchShortcutAutomatically(shortcutId: Int) {
        try {
            // Iniciar ejecutor robusto en segundo plano mediante Foreground Service
            ShortcutExecutorService.start(context, shortcutId)
        } catch (e: Exception) {
            Log.e("ShakeSensorHelper", "Fallo al autoejecutar atajo en segundo plano", e)
        }
    }

    private fun sendNotification(shortcutId: Int, name: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val channelId = "actionstack_system_triggers"
        
        val clickIntent = android.content.Intent(context, com.example.MainActivity::class.java).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("RUN_SHORTCUT_ID", shortcutId)
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            shortcutId + 30000,
            clickIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val builder = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("👋 ¡Dispositivo Agitado!")
            .setContentText("Pulsa aquí para ejecutar la pila: '$name'")
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setDefaults(androidx.core.app.NotificationCompat.DEFAULT_VIBRATE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            notificationManager.notify(shortcutId + 30000, builder.build())
        } catch (e: Exception) {
            Log.e("ShakeSensorHelper", "Error lanzando notificación de agitación", e)
        }
    }
}
