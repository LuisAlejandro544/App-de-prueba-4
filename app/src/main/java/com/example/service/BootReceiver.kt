package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.database.ShortcutDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Dispositivo encendido. Re-programando atajos automáticos...")
            
            val appCtx = context.applicationContext
            val database = ShortcutDatabase.getDatabase(appCtx)
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val dao = database.shortcutDao()
                    val shortcuts = dao.getAllShortcuts().first()
                    
                    for (shortcut in shortcuts) {
                        if (shortcut.isTriggerEnabled && !shortcut.triggerTime.isNullOrEmpty()) {
                            ShortcutScheduler.scheduleAlarm(appCtx, shortcut.id, shortcut.triggerTime!!)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error re-programando alarmas al iniciar", e)
                }
            }
        }
    }
}

