package com.example.ui.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.icons.ShortcutIconHelper

@Composable
fun InteractiveShortcutGuide() {
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "💡 Guía Visual: Flujo de Bloques",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = if (isExpanded) "Ocultar ⬆️" else "Ver Guía ⬇️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Los Atajos ejecutan acciones en secuencia como filas de dominoes. Aquí tienes el ejemplo perfecto de automatización:",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    // Bloques del Flujo Guía
                    val guideSteps = listOf(
                        GuideStep(
                            number = "1",
                            title = "Abrir URL / App",
                            desc = "Abre un navegador o app (ej: Chrome o WhatsApp) en primer plano instantáneamente.",
                            colorHex = "#FFDBCB", // Melocotón Pastel
                            textColor = Color(0xFF3B0900),
                            iconName = "language"
                        ),
                        GuideStep(
                            number = "2",
                            title = "Esperar X Segundos",
                            desc = "Realiza una pausa lógica (ej. 5s) para dar tiempo a que la pantalla cargue antes del siguiente paso.",
                            colorHex = "#FFF0B5", // Mantequilla Pastel
                            textColor = Color(0xFF3E2723),
                            iconName = "schedule"
                        ),
                        GuideStep(
                            number = "3",
                            title = "Hablar Texto (TTS)",
                            desc = "El sintetizador de voz lee un mensaje en voz alta (\"Tu reporte está listo\").",
                            colorHex = "#F1DBFA", // Lavanda Pastel
                            textColor = Color(0xFF2B1237),
                            iconName = "volume_up"
                        ),
                        GuideStep(
                            number = "4",
                            title = "Volver al Inicio (Accesibilidad)",
                            desc = "¡Acción del dispositivo! El servicio de accesibilidad simula pulsar el botón de Inicio automáticamente.",
                            colorHex = "#D1E8D1", // Verde Menta Pastel
                            textColor = Color(0xFF00210E),
                            iconName = "home"
                        )
                    )

                    guideSteps.forEachIndexed { i, step ->
                        GuideStepItem(step = step)
                        if (i < guideSteps.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "⬇️",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class GuideStep(
    val number: String,
    val title: String,
    val desc: String,
    val colorHex: String,
    val textColor: Color,
    val iconName: String
)

@Composable
fun GuideStepItem(step: GuideStep) {
    val bg = Color(android.graphics.Color.parseColor(step.colorHex))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(step.textColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step.number,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = step.textColor
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = step.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = step.textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = step.desc,
                fontSize = 11.sp,
                color = step.textColor.copy(alpha = 0.8f),
                lineHeight = 14.sp
            )
        }

        Icon(
            imageVector = ShortcutIconHelper.getIcon(step.iconName),
            contentDescription = null,
            tint = step.textColor.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}
