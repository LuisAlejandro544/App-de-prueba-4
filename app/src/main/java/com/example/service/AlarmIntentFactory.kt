package com.example.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

/**
 * Fábrica modular para crear PendingIntents de Alarma y parsear/calcular tiempos de Calendar.
 */
object AlarmIntentFactory {

    /**
     * Crea un PendingIntent para el BroadcastReceiver de Alarmas para un atajo id concreto.
     */
    fun createPendingIntent(context: Context, shortcutId: Int): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("shortcut_id", shortcutId)
        }
        return PendingIntent.getBroadcast(
            context,
            shortcutId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Calcula la fecha en milisegundos para una hora y minutos dados.
     * Si la hora ya pasó en el día actual, se programa para el día siguiente de forma automática.
     */
    fun calculateTriggerTime(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return calendar.timeInMillis
    }
}
