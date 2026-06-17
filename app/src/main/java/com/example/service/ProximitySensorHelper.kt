package com.example.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object ProximitySensorHelper {
    suspend fun waitForCondition(context: Context, condition: String): Boolean {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager ?: return false
        val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) ?: return false

        return suspendCancellableCoroutine { continuation ->
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event != null) {
                        val distance = event.values[0]
                        val maxRange = proximitySensor.maximumRange
                        // Comprobación común para determinar si está cubierto
                        val isCovered = (distance < maxRange) && (distance < 5.0f || maxRange <= 5.0f)

                        val shouldResume = if (condition == "TAPADO") isCovered else !isCovered
                        if (shouldResume) {
                            sensorManager.unregisterListener(this)
                            if (continuation.isActive) {
                                continuation.resume(true)
                            }
                        }
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            sensorManager.registerListener(listener, proximitySensor, SensorManager.SENSOR_DELAY_UI)

            continuation.invokeOnCancellation {
                sensorManager.unregisterListener(listener)
            }
        }
    }
}
