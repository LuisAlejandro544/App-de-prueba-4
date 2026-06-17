package com.example.service

import android.content.Context
import android.webkit.WebView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

object JsEvaluator {
    suspend fun evaluate(context: Context, script: String): String {
        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                try {
                    val webView = WebView(context)
                    // Envolvemos el script en una función auto-ejecutada (IIFE) para soportar la palabra 'return'
                    val wrappedScript = "(function(){\n$script\n})()"
                    webView.evaluateJavascript(wrappedScript) { result ->
                        // Limpiar las comillas dobles que JSON-Stringify agrega al resultado
                        val cleanResult = if (result != null && result.length >= 2 && result.startsWith("\"") && result.endsWith("\"")) {
                            result.substring(1, result.length - 1)
                        } else {
                            result ?: "null"
                        }
                        if (continuation.isActive) {
                            continuation.resume(cleanResult)
                        }
                    }
                } catch (e: Exception) {
                    if (continuation.isActive) {
                        continuation.resume("Error de ejecución: ${e.message}")
                    }
                }
            }
        }
    }
}
