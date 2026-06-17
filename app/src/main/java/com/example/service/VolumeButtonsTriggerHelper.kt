package com.example.service

import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.example.data.database.ShortcutDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class VolumeButtonsTriggerHelper(private val context: Context) {

    private val helperScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var lastVolumeUpTime: Long = 0
    private var volumeUpCount = 0

    private var lastVolumeDownTime: Long = 0
    private var volumeDownCount = 0

    companion object {
        private const val MAX_DELAY_MS = 1500L // Intervalo máximo entre pulsaciones consecutivas
        private const val TARGET_PRESS_COUNT = 5 // Número de veces a pulsar
    }

    /**
     * Se llama al pulsar el botón de subir volumen.
     * Retorna true si se debe consumir el evento (opcional).
     */
    fun onVolumeUpPressed(): Boolean {
        val now = SystemClock.uptimeMillis()
        if (now - lastVolumeUpTime > MAX_DELAY_MS) {
            volumeUpCount = 1
        } else {
            volumeUpCount++
        }
        lastVolumeUpTime = now

        Log.d("VolumeButtonsTrigger", "Volumen (+) pulsado ($volumeUpCount/$TARGET_PRESS_COUNT)")

        if (volumeUpCount >= TARGET_PRESS_COUNT) {
            volumeUpCount = 0 // Resetear
            triggerShortcut("VOLUME_UP_5")
            return true
        }
        return false
    }

    /**
     * Se llama al pulsar el botón de bajar volumen.
     */
    fun onVolumeDownPressed(): Boolean {
        val now = SystemClock.uptimeMillis()
        if (now - lastVolumeDownTime > MAX_DELAY_MS) {
            volumeDownCount = 1
        } else {
            volumeDownCount++
        }
        lastVolumeDownTime = now

        Log.d("VolumeButtonsTrigger", "Volumen (-) pulsado ($volumeDownCount/$TARGET_PRESS_COUNT)")

        if (volumeDownCount >= TARGET_PRESS_COUNT) {
            volumeDownCount = 0 // Resetear
            triggerShortcut("VOLUME_DOWN_5")
            return true
        }
        return false
    }

    private fun triggerShortcut(triggerType: String) {
        helperScope.launch {
            try {
                val db = ShortcutDatabase.getDatabase(context)
                // Obtener todos los shortcuts y buscar si hay alguno habilitado con este trigger específico
                val shortcuts = db.shortcutDao().getAllShortcutsSync()
                val matched = shortcuts.find { 
                    it.isTriggerEnabled && it.systemTrigger == triggerType 
                }

                if (matched != null) {
                    Log.d("VolumeButtonsTrigger", "¡Atajo detectado para gatillo $triggerType! Iniciando: ${matched.name}")
                    ShortcutExecutorService.start(context, matched.id)
                } else {
                    Log.d("VolumeButtonsTrigger", "Pulsaciones completadas pero ningún atajo está configurado con $triggerType")
                }
            } catch (e: Exception) {
                Log.e("VolumeButtonsTrigger", "Error buscando atajo por volumen", e)
            }
        }
    }
}
