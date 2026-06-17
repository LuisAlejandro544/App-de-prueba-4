package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

object ShortcutScheduler {

    fun scheduleAlarm(context: Context, shortcutId: Int, timeStr: String) {
        try {
            val parts = timeStr.split(":")
            if (parts.size != 2) return
            val hour = parts[0].toIntOrNull() ?: return
            val minute = parts[1].toIntOrNull() ?: return

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = AlarmIntentFactory.createPendingIntent(context, shortcutId)
            val triggerTime = AlarmIntentFactory.calculateTriggerTime(hour, minute)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                    Log.d("ShortcutScheduler", "Alarma EXACTA programada a las $timeStr para atajo ID $shortcutId")
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                    Log.d("ShortcutScheduler", "Alarma APROXIMADA (fallback) programada a las $timeStr para atajo ID $shortcutId")
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                Log.d("ShortcutScheduler", "Alarma EXACTA programada a las $timeStr para atajo ID $shortcutId")
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                Log.d("ShortcutScheduler", "Alarma EXACTA programada a las $timeStr para atajo ID $shortcutId")
            }
        } catch (e: Exception) {
            Log.e("ShortcutScheduler", "Error al programar alarma para atajo ID $shortcutId", e)
        }
    }

    fun cancelAlarm(context: Context, shortcutId: Int) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = AlarmIntentFactory.createPendingIntent(context, shortcutId)
            alarmManager.cancel(pendingIntent)
            Log.d("ShortcutScheduler", "Alarma cancelada para el atajo: $shortcutId")
        } catch (e: Exception) {
            Log.e("ShortcutScheduler", "Error al cancelar la alarma para atajo ID $shortcutId", e)
        }
    }
}
