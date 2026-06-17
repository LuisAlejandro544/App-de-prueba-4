package com.example.data.model

import com.squareup.moshi.JsonClass

enum class ActionType(val displayName: String, val iconName: String, val description: String) {
    SHOW_MESSAGE("Mostrar Mensaje", "chat", "Muestra un mensaje de advertencia o información."),
    SPEECH("Hablar Texto (TTS)", "volume_up", "Lee en voz alta el texto que indiques mediante sintetizador."),
    VIBRATE("Vibración de Alerta", "ring_volume", "Hace vibrar el dispositivo para llamar tu atención."),
    SHARE("Compartir Texto", "share", "Envía un texto a través del menú nativo de Android."),
    OPEN_URL("Abrir URL", "language", "Abre un enlace del navegador web automáticamente."),
    WAIT("Esperar Tiempo", "schedule", "Pausa la ejecución por unos segundos personalizables."),
    LAUNCH_APP("Abrir Aplicación", "apps", "Inicia una aplicación instalada buscando por paquete."),
    ACCESSIBILITY_BACK("Ir Atrás (Sistema)", "arrow_back", "Usa accesibilidad para simular pulsar Atrás."),
    ACCESSIBILITY_HOME("Ir a Inicio (Sistema)", "home", "Usa accesibilidad para simular volver a Inicio."),
    ACCESSIBILITY_NOTIFICATIONS("Abrir Panel de Notificaciones", "warning", "Usa accesibilidad para desplegar el panel de notificaciones del sistema."),
    NOTIFICATION("Enviar Notificación", "notifications", "Muestra una notificación en la barra de estado con un título y mensaje personalizables."),
    PROXIMITY_SENSOR("Sensor de Proximidad", "warning", "Pausa el flujo hasta tapar o destapar el sensor de proximidad del dispositivo."),
    BATTERY_LEVEL("Verificar Batería", "settings", "Frena la ejecución si la carga de batería no cumple con la restricción indicada."),
    CUSTOM_JS("Código JavaScript (Offline)", "settings", "Ejecuta JavaScript local y offline regresando el resultado como Toast o aviso."),
    FLASHLIGHT("Control de Linterna", "flashlight", "Enciende, apaga o alterna la linterna física de tu teléfono."),
    IF_BATTERY("Batería con Lógica (IF/ELSE)", "settings", "Evalúa si el nivel de batería es mayor o menor a un valor para ejecutar diferentes acciones."),
    WAIT_NOTIFICATION("Esperar Mensaje/Notificación", "notifications", "Pausa la ejecución hasta recibir una notificación de una app específica.")
}

@JsonClass(generateAdapter = true)
data class ShortcutAction(
    val type: ActionType,
    val parameters: Map<String, String> = emptyMap()
)
