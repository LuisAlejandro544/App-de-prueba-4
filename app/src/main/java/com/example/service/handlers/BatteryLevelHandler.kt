package com.example.service.handlers

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.data.model.ShortcutAction
import com.example.service.BatteryHelper
import kotlinx.coroutines.delay

object BatteryLevelHandler : ActionHandler {
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
        if (!conditionMet) {
            val compSign = if (comparison == "MENOR") "<" else ">"
            val descText = "Batería ($currentPct%) no cumple con $compSign $targetPct%"
            Toast.makeText(context, descText, Toast.LENGTH_LONG).show()
            throw Exception("Condición de batería no cumplida: $descText")
        } else {
            Toast.makeText(context, "Batería ($currentPct%): Condición cumplida", Toast.LENGTH_SHORT).show()
            delay(800)
        }
    }
}
