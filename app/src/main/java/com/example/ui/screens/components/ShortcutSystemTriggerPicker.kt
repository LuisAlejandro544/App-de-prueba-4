package com.example.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.ShortcutViewModel

@Composable
fun ShortcutSystemTriggerPicker(
    viewModel: ShortcutViewModel
) {
    val currentTrigger = viewModel.shortcutFormSystemTrigger
    val formColor = Color(android.graphics.Color.parseColor(viewModel.shortcutFormColor))
    val isAnySystemTriggerActive = currentTrigger != null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAnySystemTriggerActive) {
                formColor.copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isAnySystemTriggerActive) formColor.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🔌", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Automatización de Hardware",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = when (currentTrigger) {
                            "BATTERY_LOW" -> "Gatillo: Batería por debajo del 20%"
                            "POWER_CONNECTED" -> "Gatillo: Cargador conectado"
                            "POWER_DISCONNECTED" -> "Gatillo: Cargador desconectado"
                            "SHAKE" -> "Gatillo: Detectar agitación (Sacudir)"
                            "VOLUME_UP_5" -> "Gatillo: Presionar Vol+ 5 veces"
                            "VOLUME_DOWN_5" -> "Gatillo: Presionar Vol- 5 veces"
                            else -> "Inicia el atajo ante eventos físicos"
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Seleccionar Gatillo Físico:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Rejilla / Grid de botones de selección
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    // Opción: Ninguno
                    TriggerOptionCard(
                        emoji = "🚫",
                        title = "Ninguno",
                        desc = "Solo manual o reloj",
                        isSelected = currentTrigger == null,
                        formColor = formColor,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.shortcutFormSystemTrigger = null
                        }
                    )

                    // Opción: Batería baja
                    TriggerOptionCard(
                        emoji = "🔋",
                        title = "Batería Baja",
                        desc = "Bateria < 20%",
                        isSelected = currentTrigger == "BATTERY_LOW",
                        formColor = formColor,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.shortcutFormSystemTrigger = "BATTERY_LOW"
                            viewModel.shortcutFormTriggerEnabled = true // Auto habilitar el switch al elegir
                        }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    // Opción: Cargador Conectado
                    TriggerOptionCard(
                        emoji = "🔌",
                        title = "Conectado",
                        desc = "Cargador enchufado",
                        isSelected = currentTrigger == "POWER_CONNECTED",
                        formColor = formColor,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.shortcutFormSystemTrigger = "POWER_CONNECTED"
                            viewModel.shortcutFormTriggerEnabled = true
                        }
                    )

                    // Opción: Cargador Desconectado
                    TriggerOptionCard(
                        emoji = "🪫",
                        title = "Desconectado",
                        desc = "Cargador removido",
                        isSelected = currentTrigger == "POWER_DISCONNECTED",
                        formColor = formColor,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.shortcutFormSystemTrigger = "POWER_DISCONNECTED"
                            viewModel.shortcutFormTriggerEnabled = true
                        }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    // Opción: Agitación
                    TriggerOptionCard(
                        emoji = "👋",
                        title = "Sacudir / Agitar",
                        desc = "Agitar el celular",
                        isSelected = currentTrigger == "SHAKE",
                        formColor = formColor,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.shortcutFormSystemTrigger = "SHAKE"
                            viewModel.shortcutFormTriggerEnabled = true
                        }
                    )

                    // Spacer para balancear
                    Spacer(modifier = Modifier.weight(1f))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    // Opción: Subir volumen 5x
                    TriggerOptionCard(
                        emoji = "🔊",
                        title = "Volumen+ x5",
                        desc = "Presionar Vol+ 5 veces",
                        isSelected = currentTrigger == "VOLUME_UP_5",
                        formColor = formColor,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.shortcutFormSystemTrigger = "VOLUME_UP_5"
                            viewModel.shortcutFormTriggerEnabled = true
                        }
                    )

                    // Opción: Bajar volumen 5x
                    TriggerOptionCard(
                        emoji = "🔉",
                        title = "Volumen- x5",
                        desc = "Presionar Vol- 5 veces",
                        isSelected = currentTrigger == "VOLUME_DOWN_5",
                        formColor = formColor,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.shortcutFormSystemTrigger = "VOLUME_DOWN_5"
                            viewModel.shortcutFormTriggerEnabled = true
                        }
                    )
                }
            }

            if (isAnySystemTriggerActive) {
                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(14.dp))
                
                Text(
                    text = "⚙️ Nota: Al asociar un Gatillo Físico, asegúrate de activar el Switch de arriba de 'Programación de Alarma / Triggers' para que esté habilitado. En modo Auto-Lanzar se disparará al instante.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun TriggerOptionCard(
    emoji: String,
    title: String,
    desc: String,
    isSelected: Boolean,
    formColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                formColor.copy(alpha = 0.22f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) formColor
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
