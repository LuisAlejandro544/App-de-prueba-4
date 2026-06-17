package com.example.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
fun ShortcutClockScheduler(
    viewModel: ShortcutViewModel
) {
    val isEnabled = viewModel.shortcutFormTriggerEnabled
    val currentTime = viewModel.shortcutFormTriggerTime ?: "08:00"
    val timeParts = currentTime.split(":")
    val hour = timeParts.getOrNull(0) ?: "08"
    val minute = timeParts.getOrNull(1) ?: "00"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) {
                Color(android.graphics.Color.parseColor(viewModel.shortcutFormColor)).copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isEnabled) Color(android.graphics.Color.parseColor(viewModel.shortcutFormColor)).copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "⏰", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Programación por Reloj",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isEnabled) "Comenzará a las $currentTime" else "Desactivado",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = { checked ->
                        viewModel.shortcutFormTriggerEnabled = checked
                        if (checked && viewModel.shortcutFormTriggerTime == null) {
                            viewModel.shortcutFormTriggerTime = "08:00"
                        }
                    }
                )
            }

            if (isEnabled) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Ajustar Hora de Activación:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hora Spinner/Input block
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                var h = hour.toIntOrNull() ?: 8
                                h = (h - 1 + 24) % 24
                                val newTime = String.format("%02d:%s", h, minute)
                                viewModel.shortcutFormTriggerTime = newTime
                            }) {
                                Text("▼", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            }

                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = hour,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            IconButton(onClick = {
                                var h = hour.toIntOrNull() ?: 8
                                h = (h + 1) % 24
                                val newTime = String.format("%02d:%s", h, minute)
                                viewModel.shortcutFormTriggerTime = newTime
                            }) {
                                Text("▲", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Text("Horas (24h)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = ":", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.width(8.dp))

                    // Minuto Spinner/Input block
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                var m = minute.toIntOrNull() ?: 0
                                m = (m - 5 + 60) % 60
                                val newTime = String.format("%s:%02d", hour, m)
                                viewModel.shortcutFormTriggerTime = newTime
                            }) {
                                Text("▼", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            }

                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = minute,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            IconButton(onClick = {
                                var m = minute.toIntOrNull() ?: 0
                                m = (m + 5) % 60
                                val newTime = String.format("%s:%02d", hour, m)
                                viewModel.shortcutFormTriggerTime = newTime
                            }) {
                                Text("▲", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Text("Minutos", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Modo de Ejecución:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val isAuto = viewModel.shortcutFormIsAutoTrigger
                    // Automático block
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.shortcutFormIsAutoTrigger = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAuto) {
                                Color(android.graphics.Color.parseColor(viewModel.shortcutFormColor)).copy(alpha = 0.2f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                            }
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isAuto) Color(android.graphics.Color.parseColor(viewModel.shortcutFormColor))
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("⚡", fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Auto-Lanzar",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Ejecuta directo al sonar",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Manual block
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.shortcutFormIsAutoTrigger = false },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!isAuto) {
                                Color(android.graphics.Color.parseColor(viewModel.shortcutFormColor)).copy(alpha = 0.2f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                            }
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (!isAuto) Color(android.graphics.Color.parseColor(viewModel.shortcutFormColor))
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔔", fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Manual (Click)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Lanza con notificación",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "💡 Nota: En modo Automatizado, ActionStack se abrirá y ejecutará esta pila directamente. En modo Manual se te enviará una notificación interactiva.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
