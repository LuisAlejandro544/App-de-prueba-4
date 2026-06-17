package com.example.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActionType
import com.example.data.model.ShortcutAction
import com.example.ui.icons.ShortcutIconHelper
import com.example.ui.viewmodel.ShortcutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionBlockItem(
    index: Int,
    action: ShortcutAction,
    viewModel: ShortcutViewModel,
    onParamChange: (String, String) -> Unit,
    onRemove: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showAppSelector by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    if (showHelpDialog) {
        ActionHelpDialog(
            actionType = action.type,
            onDismiss = { showHelpDialog = false }
        )
    }

    val neonColor = when (action.type) {
        ActionType.SHOW_MESSAGE, ActionType.SHARE, ActionType.OPEN_URL, ActionType.NOTIFICATION -> Color(0xFF00B0FF)
        ActionType.VIBRATE, ActionType.FLASHLIGHT -> Color(0xFFFF9100)
        ActionType.SPEECH -> Color(0xFFFF2D55)
        ActionType.ACCESSIBILITY_BACK, ActionType.ACCESSIBILITY_HOME, ActionType.ACCESSIBILITY_NOTIFICATIONS -> Color(0xFF9D4EDD)
        ActionType.PROXIMITY_SENSOR, ActionType.BATTERY_LEVEL, ActionType.IF_BATTERY -> Color(0xFF39FF14)
        ActionType.WAIT, ActionType.LAUNCH_APP, ActionType.CUSTOM_JS, ActionType.WAIT_NOTIFICATION -> Color(0xFF00F5FF)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.8.dp,
            color = neonColor.copy(alpha = 0.8f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Cabecera del bloque de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(neonColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = neonColor
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = ShortcutIconHelper.getIcon(action.type.iconName),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = neonColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = action.type.displayName,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón de Ayuda con ícono informativo "!"
                    IconButton(
                        onClick = { showHelpDialog = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Ayuda sobre esta acción",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remover",
                            tint = Color.Red.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Formulario específico para parámetros según el tipo de acción
            when (action.type) {
                ActionType.SHOW_MESSAGE -> {
                    val currentText = action.parameters["message"] ?: ""
                    Column {
                        Text(
                            text = "Aviso / Toast a mostrar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextField(
                            value = currentText,
                            onValueChange = { onParamChange("message", it) },
                            singleLine = true,
                            colors = transparentTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                ActionType.SPEECH -> {
                    val currentSpeech = action.parameters["text"] ?: ""
                    val currentRateStr = action.parameters["rate"] ?: "1.0"
                    val currentRate = currentRateStr.toFloatOrNull() ?: 1.0f
                    val selectedVoice = action.parameters["voice"] ?: "Predeterminada"

                    var expandedVoiceMenu by remember { mutableStateOf(false) }
                    val availableVoices = viewModel.getAvailableVoices()

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column {
                            Text(
                                text = "Texto del locutor de voz (TTS)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextField(
                                value = currentSpeech,
                                onValueChange = { onParamChange("text", it) },
                                placeholder = { Text("Escribe lo que el sintetizador dirá...") },
                                colors = transparentTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Selector de voz personalizada
                        Column {
                            Text(
                                text = "Selector de Voz (Detecta motores del sistema)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedCard(
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedVoiceMenu = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = formatVoiceName(selectedVoice),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text("▾", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                    }
                                }
                                DropdownMenu(
                                    expanded = expandedVoiceMenu,
                                    onDismissRequest = { expandedVoiceMenu = false },
                                    modifier = Modifier.heightIn(max = 280.dp)
                                ) {
                                    availableVoices.forEach { voice ->
                                        DropdownMenuItem(
                                            text = { Text(formatVoiceName(voice), fontSize = 13.sp) },
                                            onClick = {
                                                onParamChange("voice", voice)
                                                expandedVoiceMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "💬 Puedes instalar motores de voz de terceros altamente realistas y offline como Piper o Sherpa-Onnx en los ajustes de Accesibilidad e Idioma de Android, los cuales se integrarán de forma automática aquí.",
                                fontSize = 11.sp,
                                lineHeight = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                            )
                        }

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Velocidad de la voz: ${String.format(java.util.Locale.US, "%.1f", currentRate)}x",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = when {
                                        currentRate <= 0.6f -> "Tortuga 🐢"
                                        currentRate <= 0.9f -> "Lento 🚶"
                                        currentRate <= 1.2f -> "Normal 👤"
                                        currentRate <= 1.6f -> "Rápido ⚡"
                                        else -> "Sónico 🚀"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Slider(
                                value = currentRate,
                                onValueChange = { 
                                    onParamChange("rate", String.format(java.util.Locale.US, "%.1f", it)) 
                                },
                                valueRange = 0.4f..2.2f,
                                steps = 8,
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    }
                }
                ActionType.SHARE -> {
                    val currentText = action.parameters["text"] ?: ""
                    Column {
                        Text(
                            text = "Contenido de texto a compartir",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextField(
                            value = currentText,
                            onValueChange = { onParamChange("text", it) },
                            colors = transparentTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                ActionType.OPEN_URL -> {
                    val currentUrl = action.parameters["url"] ?: ""
                    Column {
                        Text(
                            text = "Dirección Web completa (URL)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextField(
                            value = currentUrl,
                            onValueChange = { onParamChange("url", it) },
                            placeholder = { Text("https://example.com") },
                            singleLine = true,
                            colors = transparentTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                ActionType.VIBRATE -> {
                    Text(
                        text = "Vibración corta estándar al ejecutarse el bloque.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                ActionType.WAIT -> {
                    val currentSecs = action.parameters["seconds"] ?: "5"
                    Column {
                        Text(
                            text = "Segundos a esperar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextField(
                            value = currentSecs,
                            onValueChange = { onParamChange("seconds", it) },
                            placeholder = { Text("Ej. 5") },
                            singleLine = true,
                            colors = transparentTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                ActionType.LAUNCH_APP -> {
                    val currentPkg = action.parameters["packageName"] ?: ""
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Paquete del App a abrir",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            TextButton(
                                onClick = {
                                    viewModel.loadInstalledApps(context)
                                    showAppSelector = true
                                }
                            ) {
                                Text("🔍 Buscar App", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        TextField(
                            value = currentPkg,
                            onValueChange = { onParamChange("packageName", it) },
                            placeholder = { Text("Ej: com.android.chrome") },
                            singleLine = true,
                            colors = transparentTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (showAppSelector) {
                        val apps by viewModel.installedApps.collectAsState()
                        AppSelectorDialog(
                            installedApps = apps,
                            onDismiss = { showAppSelector = false },
                            onAppSelected = { pkg ->
                                onParamChange("packageName", pkg)
                            }
                        )
                    }
                }
                ActionType.ACCESSIBILITY_BACK -> {
                    Text(
                        text = "Simulará pulsar retroceder en Android con el Servicio de Accesibilidad.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                ActionType.ACCESSIBILITY_HOME -> {
                    Text(
                        text = "Simulará pulsar inicio en Android para cerrar todo y volver al escritorio.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                ActionType.ACCESSIBILITY_NOTIFICATIONS -> {
                    Text(
                        text = "Simulará deslizar el dedo hacia abajo para abrir el panel de notificaciones del sistema.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                ActionType.NOTIFICATION -> {
                    val currentTitle = action.parameters["title"] ?: "ActionStack"
                    val currentMsg = action.parameters["message"] ?: ""
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column {
                            Text(
                                text = "Título de Notificación",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextField(
                                value = currentTitle,
                                onValueChange = { onParamChange("title", it) },
                                placeholder = { Text("Ej: Alerta de ActionStack") },
                                singleLine = true,
                                colors = transparentTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Column {
                            Text(
                                text = "Mensaje de Notificación",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextField(
                                value = currentMsg,
                                onValueChange = { onParamChange("message", it) },
                                placeholder = { Text("Mensaje de recordatorio...") },
                                singleLine = true,
                                colors = transparentTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                ActionType.PROXIMITY_SENSOR -> {
                    val currentCondition = action.parameters["condition"] ?: "TAPADO"
                    Column {
                        Text(
                            text = "Condición a esperar en el sensor de proximidad",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { onParamChange("condition", "TAPADO") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentCondition == "TAPADO") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (currentCondition == "TAPADO") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Sensar Tapado 👋", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Button(
                                onClick = { onParamChange("condition", "DESPEJADO") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentCondition == "DESPEJADO") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (currentCondition == "DESPEJADO") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Sensar Despejado 💨", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
                ActionType.BATTERY_LEVEL -> {
                    val currentPct = action.parameters["percentage"] ?: "50"
                    val currentComp = action.parameters["comparison"] ?: "MENOR"
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column {
                            Text(
                                text = "Nivel de batería límite (%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextField(
                                value = currentPct,
                                onValueChange = { onParamChange("percentage", it) },
                                placeholder = { Text("Ej: 20") },
                                singleLine = true,
                                colors = transparentTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Column {
                            Text(
                                text = "Disparar si la carga actual es",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { onParamChange("comparison", "MENOR") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentComp == "MENOR") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (currentComp == "MENOR") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Menor que (<)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { onParamChange("comparison", "MAYOR") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentComp == "MAYOR") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (currentComp == "MAYOR") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Mayor que (>)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                ActionType.CUSTOM_JS -> {
                    val currentScript = action.parameters["script"] ?: ""
                    Column {
                        Text(
                            text = "Código JavaScript a evaluar (Offline)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextField(
                            value = currentScript,
                            onValueChange = { onParamChange("script", it) },
                            placeholder = { Text("// Escribe tu código JS aquí...\nreturn 'Mensaje';") },
                            maxLines = 10,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1E1E1E),
                                unfocusedContainerColor = Color(0xFF252526),
                                focusedTextColor = Color(0xFF9CDCFE),
                                unfocusedTextColor = Color(0xFFD4D4D4),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp, max = 220.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "💡 Puedes usar sentencias de JS (Math, condicionales) y devolver un string usando 'return'.",
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                        )
                    }
                }
                ActionType.FLASHLIGHT -> {
                    val currentState = action.parameters["state"] ?: "TOGGLE"
                    Column {
                        Text(
                            text = "Acción de la Linterna",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val states = listOf("TOGGLE" to "Alternar 🔄", "ON" to "Encender 💡", "OFF" to "Apagar 🔌")
                            states.forEach { (stateVal, label) ->
                                Button(
                                    onClick = { onParamChange("state", stateVal) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentState == stateVal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (currentState == stateVal) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                                ) {
                                    Text(label, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                }
                            }
                        }
                    }
                }
                ActionType.IF_BATTERY -> {
                    val currentPct = action.parameters["percentage"] ?: "50"
                    val currentComp = action.parameters["comparison"] ?: "MENOR"
                    val thenActionType = action.parameters["then_action_type"] ?: ""
                    val elseActionType = action.parameters["else_action_type"] ?: ""

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "1. Condición de la Batería",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(
                                value = currentPct,
                                onValueChange = { onParamChange("percentage", it) },
                                label = { Text("Límite %") },
                                singleLine = true,
                                colors = transparentTextFieldColors(),
                                modifier = Modifier.weight(1f)
                            )
                            Row(
                                modifier = Modifier.weight(2f),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Button(
                                    onClick = { onParamChange("comparison", "MENOR") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentComp == "MENOR") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (currentComp == "MENOR") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Menor (<)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { onParamChange("comparison", "MAYOR") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentComp == "MAYOR") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (currentComp == "MAYOR") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Mayor (>)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        Text(
                            text = "2. SI se cumple (ENTONCES)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50)
                        )

                        val options = listOf(
                            "" to "Ninguna",
                            "SPEECH" to "Hablar Texto (TTS)",
                            "SHOW_MESSAGE" to "Mostrar Mensaje",
                            "NOTIFICATION" to "Enviar Notificación",
                            "LAUNCH_APP" to "Abrir Aplicación",
                            "FLASHLIGHT" to "Alternar Linterna",
                            "VIBRATE" to "Vibrar de Alerta"
                        )

                        var expandedThenMenu by remember { mutableStateOf(false) }
                        val currentThenLabel = options.find { it.first == thenActionType }?.second ?: "Seleccionar acción..."

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCard(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().clickable { expandedThenMenu = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = currentThenLabel, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text("▾", fontSize = 14.sp)
                                }
                            }
                            DropdownMenu(
                                expanded = expandedThenMenu,
                                onDismissRequest = { expandedThenMenu = false }
                            ) {
                                options.forEach { (typeVal, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label, fontSize = 13.sp) },
                                        onClick = {
                                            onParamChange("then_action_type", typeVal)
                                            expandedThenMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        if (thenActionType.isNotBlank()) {
                            when (thenActionType) {
                                "SPEECH" -> {
                                    val valText = action.parameters["then_text"] ?: ""
                                    TextField(
                                        value = valText,
                                        onValueChange = { onParamChange("then_text", it) },
                                        label = { Text("Texto que leerá") },
                                        colors = transparentTextFieldColors(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                "SHOW_MESSAGE" -> {
                                    val valMsg = action.parameters["then_message"] ?: ""
                                    TextField(
                                        value = valMsg,
                                        onValueChange = { onParamChange("then_message", it) },
                                        label = { Text("Mensaje en pantalla (Toast)") },
                                        colors = transparentTextFieldColors(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                "NOTIFICATION" -> {
                                    val valTitle = action.parameters["then_title"] ?: "ActionStack"
                                    val valMsg = action.parameters["then_message"] ?: ""
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        TextField(
                                            value = valTitle,
                                            onValueChange = { onParamChange("then_title", it) },
                                            label = { Text("Título Notificación") },
                                            colors = transparentTextFieldColors(),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        TextField(
                                            value = valMsg,
                                            onValueChange = { onParamChange("then_message", it) },
                                            label = { Text("Cuerpo de Notificación") },
                                            colors = transparentTextFieldColors(),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                                "LAUNCH_APP" -> {
                                    val valPkg = action.parameters["then_packageName"] ?: ""
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        TextField(
                                            value = valPkg,
                                            onValueChange = { onParamChange("then_packageName", it) },
                                            label = { Text("ID de paquete") },
                                            colors = transparentTextFieldColors(),
                                            modifier = Modifier.weight(1f)
                                        )
                                        TextButton(
                                            onClick = {
                                                viewModel.loadInstalledApps(context)
                                                showAppSelector = true
                                            }
                                        ) {
                                            Text("🔍 Buscar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                "FLASHLIGHT" -> {
                                    val currentS = action.parameters["then_action"] ?: "TOGGLE"
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                                        listOf("TOGGLE" to "Alternar", "ON" to "Encender", "OFF" to "Apagar").forEach { (v, lbl) ->
                                            Button(
                                                onClick = { onParamChange("then_action", v) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (currentS == v) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                                    contentColor = if (currentS == v) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                                ),
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(lbl, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        Text(
                            text = "3. SI NO se cumple (SINO) - Opcional",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFF44336)
                        )

                        var expandedElseMenu by remember { mutableStateOf(false) }
                        val currentElseLabel = options.find { it.first == elseActionType }?.second ?: "Ninguna"

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCard(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().clickable { expandedElseMenu = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = currentElseLabel, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text("▾", fontSize = 14.sp)
                                }
                            }
                            DropdownMenu(
                                expanded = expandedElseMenu,
                                onDismissRequest = { expandedElseMenu = false }
                            ) {
                                options.forEach { (typeVal, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label, fontSize = 13.sp) },
                                        onClick = {
                                            onParamChange("else_action_type", typeVal)
                                            expandedElseMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        if (elseActionType.isNotBlank()) {
                            when (elseActionType) {
                                "SPEECH" -> {
                                    val valText = action.parameters["else_text"] ?: ""
                                    TextField(
                                        value = valText,
                                        onValueChange = { onParamChange("else_text", it) },
                                        label = { Text("Texto que leerá") },
                                        colors = transparentTextFieldColors(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                "SHOW_MESSAGE" -> {
                                    val valMsg = action.parameters["else_message"] ?: ""
                                    TextField(
                                        value = valMsg,
                                        onValueChange = { onParamChange("else_message", it) },
                                        label = { Text("Mensaje en pantalla (Toast)") },
                                        colors = transparentTextFieldColors(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                "NOTIFICATION" -> {
                                    val valTitle = action.parameters["else_title"] ?: "ActionStack"
                                    val valMsg = action.parameters["else_message"] ?: ""
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        TextField(
                                            value = valTitle,
                                            onValueChange = { onParamChange("else_title", it) },
                                            label = { Text("Título Notificación") },
                                            colors = transparentTextFieldColors(),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        TextField(
                                            value = valMsg,
                                            onValueChange = { onParamChange("else_message", it) },
                                            label = { Text("Cuerpo de Notificación") },
                                            colors = transparentTextFieldColors(),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                                "LAUNCH_APP" -> {
                                    val valPkg = action.parameters["else_packageName"] ?: ""
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        TextField(
                                            value = valPkg,
                                            onValueChange = { onParamChange("else_packageName", it) },
                                            label = { Text("ID de paquete") },
                                            colors = transparentTextFieldColors(),
                                            modifier = Modifier.weight(1f)
                                        )
                                        TextButton(
                                            onClick = {
                                                viewModel.loadInstalledApps(context)
                                                showAppSelector = true
                                            }
                                        ) {
                                            Text("🔍 Buscar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                "FLASHLIGHT" -> {
                                    val currentS = action.parameters["else_action"] ?: "TOGGLE"
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                                        listOf("TOGGLE" to "Alternar", "ON" to "Encender", "OFF" to "Apagar").forEach { (v, lbl) ->
                                            Button(
                                                onClick = { onParamChange("else_action", v) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (currentS == v) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                                    contentColor = if (currentS == v) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                                ),
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(lbl, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (showAppSelector) {
                        val apps by viewModel.installedApps.collectAsState()
                        AppSelectorDialog(
                            installedApps = apps,
                            onDismiss = { showAppSelector = false },
                            onAppSelected = { pkg ->
                                if (thenActionType == "LAUNCH_APP") {
                                    onParamChange("then_packageName", pkg)
                                } else if (elseActionType == "LAUNCH_APP") {
                                    onParamChange("else_packageName", pkg)
                                }
                            }
                        )
                    }
                }
                ActionType.WAIT_NOTIFICATION -> {
                    val currentPkg = action.parameters["packageName"] ?: "Cualquiera"
                    val currentSender = action.parameters["senderText"] ?: ""
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Configuración del Filtro de Notificación",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "Paquete de la App a esperar",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                TextField(
                                    value = currentPkg,
                                    onValueChange = { onParamChange("packageName", it) },
                                    singleLine = true,
                                    colors = transparentTextFieldColors(),
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = {
                                        viewModel.loadInstalledApps(context)
                                        showAppSelector = true
                                    }
                                ) {
                                    Text("🔍 Buscar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Column {
                            Text(
                                text = "Filtro de remitente o texto (Ej: Mamá o WhatsApp)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextField(
                                value = currentSender,
                                onValueChange = { onParamChange("senderText", it) },
                                placeholder = { Text("Escribe el nombre o texto a filtrar...") },
                                singleLine = true,
                                colors = transparentTextFieldColors(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "💡 El atajo se pausará hasta que llegue la notificación esperada. Asegúrate de tener el Servicio de Accesibilidad activado.",
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                        )
                    }

                    if (showAppSelector) {
                        val apps by viewModel.installedApps.collectAsState()
                        AppSelectorDialog(
                            installedApps = apps,
                            onDismiss = { showAppSelector = false },
                            onAppSelected = { pkg ->
                                onParamChange("packageName", pkg)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun transparentTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
    disabledContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent
)

fun formatVoiceName(voiceId: String): String {
    if (voiceId == "Predeterminada" || voiceId.equals("Predeterminada", ignoreCase = true)) {
        return "Predeterminada (Voz del sistema)"
    }

    try {
        val parts = voiceId.split("-", "_")
        if (parts.size >= 2) {
            val langCode = parts[0].lowercase()
            val countryCode = parts[1].uppercase()

            val langName = when (langCode) {
                "es" -> "Español"
                "en" -> "Inglés"
                "fr" -> "Francés"
                "de" -> "Alemán"
                "it" -> "Italiano"
                "pt" -> "Portugués"
                "ja" -> "Japonés"
                "ko" -> "Coreano"
                "zh" -> "Chino"
                "ru" -> "Ruso"
                "da" -> "Danés"
                "nl" -> "Holandés"
                "sv" -> "Sueco"
                "no" -> "Noruego"
                "fi" -> "Finés"
                "pl" -> "Polaco"
                "tr" -> "Turco"
                "ar" -> "Árabe"
                "hi" -> "Hindi"
                "bn" -> "Bengalí"
                "ml" -> "Malayalam"
                "ta" -> "Tamil"
                "te" -> "Telugu"
                "th" -> "Tailandés"
                "vi" -> "Vietnamita"
                "id" -> "Indonesio"
                "ms" -> "Malayo"
                "uk" -> "Ucraniano"
                "el" -> "Griego"
                "he" -> "Hebreo"
                "ro" -> "Rumano"
                "sk" -> "Eslovaco"
                "cs" -> "Checo"
                "hu" -> "Húngaro"
                "ca" -> "Catalán"
                "gl" -> "Gallego"
                "eu" -> "Vasco"
                else -> langCode.uppercase()
            }

            val isLocal = voiceId.contains("local", ignoreCase = true)
            val isNetwork = voiceId.contains("network", ignoreCase = true)
            val locationType = when {
                isLocal -> "Local 💾"
                isNetwork -> "Red ☁️ "
                else -> ""
            }

            val partsFiltered = parts.drop(2).filter { 
                it.length <= 4 && it != "x" && !it.contains("local", true) && !it.contains("network", true) 
            }
            val extraInfo = if (partsFiltered.isNotEmpty()) " " + partsFiltered.joinToString(" ").uppercase() else ""

            val suffix = if (locationType.isNotEmpty()) " - $locationType" else ""
            return "$langName ($countryCode)$extraInfo$suffix"
        }
    } catch (e: Exception) {
        // fallback
    }
    return voiceId
}
