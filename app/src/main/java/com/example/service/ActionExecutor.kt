package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import com.example.data.model.ActionType
import com.example.data.model.ShortcutAction
import com.example.service.handlers.*

object ActionExecutor {
    private val handlers: Map<ActionType, ActionHandler> = mapOf(
        ActionType.SHOW_MESSAGE to ShowMessageHandler,
        ActionType.SPEECH to SpeechHandler,
        ActionType.VIBRATE to VibrateHandler,
        ActionType.SHARE to ShareHandler,
        ActionType.OPEN_URL to OpenUrlHandler,
        ActionType.WAIT to WaitHandler,
        ActionType.LAUNCH_APP to LaunchAppHandler,
        ActionType.ACCESSIBILITY_BACK to AccessibilityBackHandler,
        ActionType.ACCESSIBILITY_HOME to AccessibilityHomeHandler,
        ActionType.ACCESSIBILITY_NOTIFICATIONS to AccessibilityNotificationsHandler,
        ActionType.NOTIFICATION to NotificationHandler,
        ActionType.PROXIMITY_SENSOR to ProximitySensorHandler,
        ActionType.BATTERY_LEVEL to BatteryLevelHandler,
        ActionType.CUSTOM_JS to CustomJsHandler,
        ActionType.FLASHLIGHT to FlashlightHandler,
        ActionType.IF_BATTERY to IfBatteryHandler,
        ActionType.WAIT_NOTIFICATION to WaitNotificationHandler
    )

    suspend fun execute(
        action: ShortcutAction,
        context: Context,
        textToSpeech: TextToSpeech?,
        isTtsReady: Boolean,
        onStatusUpdate: (String) -> Unit
    ) {
        val handler = handlers[action.type]
        if (handler != null) {
            try {
                handler.execute(action, context, textToSpeech, isTtsReady, onStatusUpdate)
            } catch (e: Exception) {
                com.example.service.FileStorageHelper.logWarning(
                    "ActionExecutor", 
                    "Error ejecutando acción ${action.type} en flujo: ${e.localizedMessage ?: e.message}"
                )
                throw e
            }
        } else {
            val errMsg = "No se encontró manejador registrado para: ${action.type}"
            android.util.Log.w("ActionExecutor", errMsg)
            com.example.service.FileStorageHelper.logWarning("ActionExecutor", errMsg)
        }
    }
}
