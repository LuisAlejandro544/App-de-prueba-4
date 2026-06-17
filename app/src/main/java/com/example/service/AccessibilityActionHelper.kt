package com.example.service

import android.accessibilityservice.AccessibilityService

/**
 * Helper para administrar y mapear las acciones globales de Accesibilidad de Android de forma modular.
 */
object AccessibilityActionHelper {

    /**
     * Mapea un identificador de acción amigable a las constantes nativas de AccessibilityService de Android.
     */
    fun performAction(actionName: String): Boolean {
        val actionId = when (actionName.uppercase().trim()) {
            "BACK" -> AccessibilityService.GLOBAL_ACTION_BACK
            "HOME" -> AccessibilityService.GLOBAL_ACTION_HOME
            "RECENTS" -> AccessibilityService.GLOBAL_ACTION_RECENTS
            "NOTIFICATIONS" -> AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS
            "QUICK_SETTINGS" -> AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS
            "POWER_DIALOG" -> AccessibilityService.GLOBAL_ACTION_POWER_DIALOG
            "LOCK_SCREEN" -> AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN
            "TAKE_SCREENSHOT" -> AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT
            else -> return false
        }
        return MyAccessibilityService.performGlobalAction(actionId)
    }
}
