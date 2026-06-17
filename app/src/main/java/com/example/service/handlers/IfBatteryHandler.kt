package com.example.service.handlers

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.example.data.model.ActionType
import com.example.data.model.ShortcutAction
import com.example.service.ActionExecutor
import com.example.service.BatteryHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object IfBatteryHandler : ActionHandler {
    override suspend fun execute(
        action: ShortcutAction,
        context: Context,
        textToSpeech: TextToSpeech?,
        isTtsReady: Boolean,
        onStatusUpdate: (String) -> Unit
    ) {
        val comparison = action.parameters["comparison"] ?: "MENOR"
        val targetPct = action.parameters["percentage"]?.toIntOrNull() ?: 50
        val currentPct = BatteryHelper.getBatteryPercentage(context)

        val conditionMet = if (comparison == "MENOR") {
            currentPct < targetPct
        } else {
            currentPct > targetPct
        }

        Log.d("IfBatteryHandler", "Batería: $currentPct% vs objetivo: $targetPct% (condición met: $conditionMet)")

        val prefix = if (conditionMet) "then_" else "else_"
        val actionTypeStr = action.parameters["${prefix}action_type"] ?: ""

        if (actionTypeStr.isNotBlank()) {
            try {
                val actionType = ActionType.valueOf(actionTypeStr)
                
                // Reconstruir los parámetros mapeándolos desde then_param_... o else_param_... o then_...
                val innerParams = mutableMapOf<String, String>()
                action.parameters.forEach { (key, value) ->
                    if (key.startsWith(prefix)) {
                        val innerKey = key.removePrefix(prefix)
                            .removePrefix("param_") // Por si se guardó con prefijo param_
                        if (innerKey != "action_type") {
                            innerParams[innerKey] = value
                        }
                    }
                }

                // Fallbacks si las claves no tienen el prefijo para simplificar
                if (innerParams.isEmpty()) {
                    // Mapeo directo por comodidad
                    when (actionType) {
                        ActionType.SHOW_MESSAGE -> innerParams["message"] = action.parameters["${prefix}message"] ?: ""
                        ActionType.SPEECH -> {
                            innerParams["text"] = action.parameters["${prefix}text"] ?: ""
                            innerParams["rate"] = action.parameters["${prefix}rate"] ?: "1.0"
                        }
                        ActionType.LAUNCH_APP -> innerParams["packageName"] = action.parameters["${prefix}packageName"] ?: ""
                        ActionType.NOTIFICATION -> {
                            innerParams["title"] = action.parameters["${prefix}title"] ?: ""
                            innerParams["message"] = action.parameters["${prefix}message"] ?: ""
                        }
                        ActionType.OPEN_URL -> innerParams["url"] = action.parameters["${prefix}url"] ?: ""
                        ActionType.SHARE -> innerParams["text"] = action.parameters["${prefix}text"] ?: ""
                        ActionType.VIBRATE -> innerParams["duration"] = action.parameters["${prefix}duration"] ?: ""
                        ActionType.FLASHLIGHT -> innerParams["action"] = action.parameters["${prefix}action"] ?: ""
                        else -> { /* no secondary keys */ }
                    }
                }

                val nestedAction = ShortcutAction(type = actionType, parameters = innerParams)
                Log.d("IfBatteryHandler", "Ejecutando acción rama ${prefix.uppercase()}: $nestedAction")
                onStatusUpdate("Ejecutando rama lógica...")
                
                ActionExecutor.execute(
                    action = nestedAction,
                    context = context,
                    textToSpeech = textToSpeech,
                    isTtsReady = isTtsReady,
                    onStatusUpdate = onStatusUpdate
                )
            } catch (e: Exception) {
                Log.e("IfBatteryHandler", "Error ejecutando acción nested de la rama $prefix", e)
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context, 
                    "Condición batería (${comparison} ${targetPct}%): ${if (conditionMet) "Cumplida" else "No cumplida"}. Rama vacía.", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
