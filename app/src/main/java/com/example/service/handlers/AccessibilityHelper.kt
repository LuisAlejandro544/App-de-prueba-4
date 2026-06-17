package com.example.service.handlers

import android.content.Context
import android.content.Intent
import android.widget.Toast

object AccessibilityHelper {
    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudieron abrir los ajustes de accesibilidad", Toast.LENGTH_SHORT).show()
        }
    }
}
