package com.example.service.handlers

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.data.model.ShortcutAction
import com.example.service.JsEvaluator
import kotlinx.coroutines.delay

object CustomJsHandler : ActionHandler {
    override suspend fun execute(
        action: ShortcutAction,
        context: Context,
        textToSpeech: TextToSpeech?,
        isTtsReady: Boolean,
        onStatusUpdate: (String) -> Unit
    ) {
        val script = action.parameters["script"] ?: "return 'Hola';"
        onStatusUpdate("Evaluando JS...")
        val result = JsEvaluator.evaluate(context, script)
        onStatusUpdate("Resultado JS!")
        Toast.makeText(context, "JS retornado: $result", Toast.LENGTH_LONG).show()
        delay(1200)
    }
}
