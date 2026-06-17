package com.example.service.handlers

import android.content.Context
import android.hardware.camera2.CameraManager
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.data.model.ShortcutAction
import kotlinx.coroutines.delay

object FlashlightHandler : ActionHandler {
    private var isTorchOn: Boolean = false

    override suspend fun execute(
        action: ShortcutAction,
        context: Context,
        textToSpeech: TextToSpeech?,
        isTtsReady: Boolean,
        onStatusUpdate: (String) -> Unit
    ) {
        val state = action.parameters["state"] ?: "TOGGLE" // "ON", "OFF", "TOGGLE"
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            val cameraId = cameraManager?.cameraIdList?.firstOrNull()
            if (cameraManager != null && cameraId != null) {
                val shouldTurnOn = when (state) {
                    "ON" -> true
                    "OFF" -> false
                    else -> {
                        isTorchOn = !isTorchOn
                        isTorchOn
                    }
                }
                cameraManager.setTorchMode(cameraId, shouldTurnOn)
                isTorchOn = shouldTurnOn
                val statusText = if (shouldTurnOn) "Linterna Encendida" else "Linterna Apagada"
                onStatusUpdate(statusText)
            } else {
                Toast.makeText(context, "Linterna no disponible o inaccesible", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error controlando linterna: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        delay(600)
    }
}
