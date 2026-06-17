package com.example.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActionType
import com.example.ui.icons.ShortcutIconHelper

@Composable
fun ActionHelpDialog(
    actionType: ActionType,
    onDismiss: () -> Unit
) {
    val detailedInfo = when (actionType) {
        ActionType.SHOW_MESSAGE -> ActionHelpInfo(
            title = "Mostrar Mensaje (Toast)",
            concept = "Muestra una pequeña burbuja informativa flotante en la pantalla del celular que desaparece al cabo de unos segundos.",
            howItWorks = "El flujo tomará el texto que declares y gatillará un aviso instantáneo a nivel de sistema. No detiene la ejecución, el siguiente paso se ejecuta de inmediato.",
            useCase = "Ejemplo: Mostrar un mensaje confirmando que tu atajo matutino inició correctamente antes de lanzar la música.",
            requiredPermission = "Ninguno. Funciona nativamente en cualquier versión de Android."
        )
        ActionType.SPEECH -> ActionHelpInfo(
            title = "Hablar Texto (TTS)",
            concept = "Convierte cualquier texto escrito en lenguaje hablado natural a través de las bocinas o audífonos del celular.",
            howItWorks = "Utiliza el motor de síntesis de voz (Text-to-Speech) nativo de Android. Te permite regular el tono y velocidad para hacerlo más pausado o dinámico.",
            useCase = "Ejemplo: Hacer que el celular te diga en voz alta '¡Buenos días usuario, despertador apagado!' al desconectar el cargador.",
            requiredPermission = "Servicio de Text-to-Speech de Google o del sistema (suele estar preinstalado)."
        )
        ActionType.NOTIFICATION -> ActionHelpInfo(
            title = "Enviar Notificación",
            concept = "Genera una notificación en la barra superior de Android con el título y mensaje que configures, con un sonido o vibración discretos.",
            howItWorks = "Coloca alertas interactivas estables en la bandeja de notificaciones. Es el método más seguro cuando deseas ejecutar un flujo en segundo plano.",
            useCase = "Ejemplo: Notificar discretamente 'Atajo de Batería Baja iniciado' cuando la batería baje del 20%.",
            requiredPermission = "Permiso de Notificaciones (Android 13+ lo solicita automáticamente al iniciar la app)."
        )
        ActionType.LAUNCH_APP -> ActionHelpInfo(
            title = "Lanzar Aplicación",
            concept = "Abre de forma automática y en primer plano cualquier aplicación que tengas instalada en tu teléfono.",
            howItWorks = "Busca el nombre de paquete único (Package Name) de la aplicación y solicita un Intent de lanzamiento nativo al sistema operativo Android.",
            useCase = "Ejemplo: Lanzar automáticamente Spotify o YouTube cuando el celular esté cargándose.",
            requiredPermission = "Consulta de paquetes instalados (QUERY_ALL_PACKAGES). Ya está incluido y gestionado por la aplicación."
        )
        ActionType.OPEN_URL -> ActionHelpInfo(
            title = "Abrir Enlace Web",
            concept = "Abre un enlace de internet (URL) directamente en el navegador de tu preferencia.",
            howItWorks = "Envía una solicitud nativa de visualización (ACTION_VIEW). Android buscará tu navegador predeterminado (ej: Chrome, Firefox) y cargará la página.",
            useCase = "Ejemplo: Abrir tu panel de noticias favorito, un reporte financiero o una página de recetas matutinas automáticamente.",
            requiredPermission = "Ninguno."
        )
        ActionType.SHARE -> ActionHelpInfo(
            title = "Compartir Texto",
            concept = "Despliega la hoja nativa para compartir contenido de Android con el texto predefinido por ti.",
            howItWorks = "Envía un Intent de envío con formato de texto plano para que puedas enviarlo directamente al chat de tu preferencia o guardarlo en las notas.",
            useCase = "Ejemplo: Copiar un reporte o plantilla rápida de texto y enviarlo a tus contactos con un solo toque.",
            requiredPermission = "Ninguno."
        )
        ActionType.FLASHLIGHT -> ActionHelpInfo(
            title = "Control de Linterna",
            concept = "Enciende, apaga o alterna el estado de la linterna (flash LED físico) de la cámara trasera de tu dispositivo móvil.",
            howItWorks = "Accede a la interfaz de la cámara trasera y de manera segura activa o desactiva la antorcha sin interrumpir tus otras aplicaciones.",
            useCase = "Ejemplo: Crear un atajo para agitar el celular y encender la linterna al instante sin oprimir botones (estilo Moto Actions).",
            requiredPermission = "Permiso de Cámara (para uso del hardware del flash)."
        )
        ActionType.VIBRATE -> ActionHelpInfo(
            title = "Vibración de Alerta",
            concept = "Hace vibrar el dispositivo con un pulso corto, largo o con patrones específicos de confirmación táctil.",
            howItWorks = "Invoca el servicio del motor háptico (Vibrator) del sistema operativo con patrones de tiempo programables para dar feedback táctil silencioso.",
            useCase = "Ejemplo: Generar una vibración corta de 300 milisegundos para avisarte de forma física que un comando se ejecutó en tu bolsillo.",
            requiredPermission = "Permiso VIBRATE (Pre-aprobado automáticamente por Android)."
        )
        ActionType.ACCESSIBILITY_BACK -> ActionHelpInfo(
            title = "Volver Atrás (Accesibilidad)",
            concept = "Simula que has pulsado físicamente el botón 'Atrás' en la barra de navegación de tu celular.",
            howItWorks = "Envía un comando de acción de retroceso directo al gestor de accesibilidad global de Android, lo que permite retroceder pantallas en cualquier app.",
            useCase = "Ejemplo: Volver atrás automáticamente tras lanzar una app para limpiar la pantalla de forma autónoma.",
            requiredPermission = "Servicio de Accesibilidad activo. Debes activar 'ActionStack' en Accesibilidad."
        )
        ActionType.ACCESSIBILITY_HOME -> ActionHelpInfo(
            title = "Ir al Inicio (Accesibilidad)",
            concept = "Simula pulsar el botón central de 'Inicio' (Home) para minimizar todo y volver a la pantalla principal del launcher.",
            howItWorks = "Aprovecha el canal de accesibilidad para invocar la acción GLOBAL_ACTION_HOME del sistema de manera instantánea y segura.",
            useCase = "Ejemplo: Minimizar aplicaciones complejas después de ejecutar una automatización de apagado.",
            requiredPermission = "Servicio de Accesibilidad activo en las configuraciones de tu dispositivo móvil."
        )
        ActionType.ACCESSIBILITY_NOTIFICATIONS -> ActionHelpInfo(
            title = "Desplegar Notificaciones",
            concept = "Desplaza hacia abajo el panel de notificaciones y la barra de estado de Android de forma automática.",
            howItWorks = "Gatilla de forma segura la llamada del panel de estado a través de la accesibilidad del sistema para evitar que tengas que deslizar manualmente.",
            useCase = "Ejemplo: Desplegar el panel de notificaciones cuando conectas tu cargador para ver tus correos o mensajes nuevos.",
            requiredPermission = "Servicio de Accesibilidad activo del dispositivo."
        )
        ActionType.WAIT -> ActionHelpInfo(
            title = "Esperar Tiempo",
            concept = "Pausa la ejecución de la secuencia de acciones por una cantidad fija de segundos personalizados por ti.",
            howItWorks = "Aplica un retraso de suspensión secuencial no bloqueante (delay) para permitir que los procesos anteriores (como abrir una URL o app) carguen por completo.",
            useCase = "Ejemplo: Abrir la URL de tu música, esperar 5 segundos para que cargue la app, y luego modular el volumen o mandar mensaje de voz.",
            requiredPermission = "Ninguno."
        )
        ActionType.PROXIMITY_SENSOR -> ActionHelpInfo(
            title = "Gatillo: Sensor de Proximidad",
            concept = "Pausa la ejecución del atajo hasta que pases la mano cerca del sensor frontal del teléfono, reanudándola de inmediato al detectarlo.",
            howItWorks = "Registra temporalmente un detector que vigila el sensor de proximidad (ubicado junto a la bocina del oído) hasta que los valores cambien.",
            useCase = "Ejemplo: Crear un atajo de emergencia que solo se complete cuando cubras el teléfono completamente o lo metas a tu bolsillo.",
            requiredPermission = "Hardware de sensor de proximidad incorporado en el celular."
        )
        ActionType.BATTERY_LEVEL -> ActionHelpInfo(
            title = "Gatillo: Nivel de Batería",
            concept = "Realiza un chequeo instantáneo del porcentaje exacto de batería y pausa o continúa el flujo según un límite numérico establecido.",
            howItWorks = "Registra un detector de batería a nivel de sistema para saber exactamente cuándo el porcentaje cruza el umbral definido por el usuario.",
            useCase = "Ejemplo: Detener el flujo de reproducción o encendido de la linterna si tu batería actual está por debajo del 25% para evitar descargar el móvil.",
            requiredPermission = "Ninguno."
        )
        ActionType.CUSTOM_JS -> ActionHelpInfo(
            title = "Código JavaScript Pers.",
            concept = "Ejecuta fragmentos dinámicos en formato de programación JavaScript, devolviendo el resultado del bloque de forma directa.",
            howItWorks = "Inicializa un evaluador de JS básico seguro para manipular variables lógicas y numéricas avanzadas de manera local en el teléfono.",
            useCase = "Ejemplo: Efectuar cálculos matemáticos o procesar cadenas de texto especiales en tus atajos más robustos.",
            requiredPermission = "Ninguno."
        )
        ActionType.IF_BATTERY -> ActionHelpInfo(
            title = "Condicional: Batería",
            concept = "Permite evaluar la batería actual y correr un bloque si se cumple una regla (ENTONCES) u otro bloque de lo contrario (SINO).",
            howItWorks = "Verifica el estatus energético en tiempo real con Android y compara el porcentaje para ejecutar dinámicamente un bloque secundario configurado.",
            useCase = "Ejemplo: SI la batería es menor a 30% activar linterna y avisar por voz, SINO hacer sonar una notificación simple.",
            requiredPermission = "Ninguno."
        )
        ActionType.WAIT_NOTIFICATION -> ActionHelpInfo(
            title = "Esperar Notificación",
            concept = "Detiene momentáneamente la secuencia del atajo hasta detectar que entra una notificación con textos específicos.",
            howItWorks = "Se conecta al Listener de notificaciones del Servicio de Accesibilidad, filtra el remitente y reanuda el atajo con la siguiente acción cuando coincide.",
            useCase = "Ejemplo: Iniciar un flujo automático de respuesta o alerta en cuanto recibas un WhatsApp de un contacto concreto.",
            requiredPermission = "Servicio de Accesibilidad activado."
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ShortcutIconHelper.getIcon(actionType.iconName),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = detailedInfo.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Concepto general
                Column {
                    Text(
                        text = "¿Qué hace?",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = detailedInfo.concept,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                // Cómo funciona técnicamente
                Column {
                    Text(
                        text = "¿Cómo funciona?",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = detailedInfo.howItWorks,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                // Ejemplo / Caso de uso
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "💡 Ejemplo práctico",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = detailedInfo.useCase,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Requerimientos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Requisitos: ",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = detailedInfo.requiredPermission,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Entendido",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

data class ActionHelpInfo(
    val title: String,
    val concept: String,
    val howItWorks: String,
    val useCase: String,
    val requiredPermission: String
)
