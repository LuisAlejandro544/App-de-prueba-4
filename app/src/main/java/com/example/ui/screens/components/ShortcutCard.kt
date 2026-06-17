package com.example.ui.screens.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Shortcut
import com.example.ui.icons.ShortcutIconHelper

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShortcutCard(
    shortcut: Shortcut,
    isEditMode: Boolean,
    isPinned: Boolean = false,
    onRunClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val cardColor = Color(android.graphics.Color.parseColor(shortcut.colorHex))
    
    // Calcular contraste idóneo para Clean Minimalism
    val contentColor = when (shortcut.colorHex.uppercase()) {
        "#F1DBFA" -> Color(0xFF2B1237) // Lavender
        "#FFDBCB" -> Color(0xFF3B0900) // Peach
        "#D1E8D1" -> Color(0xFF00210E) // Sage Green
        "#D0E4FF" -> Color(0xFF001D36) // Sky Blue
        "#EADDFF" -> Color(0xFF21005D) // Violet
        else -> {
            val parsedColor = android.graphics.Color.parseColor(shortcut.colorHex)
            val l = 0.2126 * android.graphics.Color.red(parsedColor) + 
                      0.7152 * android.graphics.Color.green(parsedColor) + 
                      0.0722 * android.graphics.Color.blue(parsedColor)
            if (l > 140) Color(0xFF1D1B20) else Color.White
        }
    }

    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier
            .height(130.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isEditMode) {
                        onEditClick()
                    } else {
                        onRunClick()
                    }
                },
                onLongClick = onLongClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Icono en la esquina superior izquierda sobre fondo translúcido blanco o negro suave
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(contentColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ShortcutIconHelper.getIcon(shortcut.iconName),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Botón de borrar o editar en modo edición
            if (isEditMode) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(32.dp)
                        .background(contentColor.copy(alpha = 0.2f), CircleShape)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Borrar",
                        tint = contentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                // Botón Play rápido en la esquina superior derecha
                IconButton(
                    onClick = onRunClick,
                    modifier = Modifier
                        .size(32.dp)
                        .background(contentColor.copy(alpha = 0.15f), CircleShape)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Ejecutar",
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Textos en la esquina inferior izquierda
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
            ) {
                Text(
                    text = shortcut.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = contentColor,
                    lineHeight = 18.sp,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${shortcut.actions.size} ${if (shortcut.actions.size == 1) "acción" else "acciones"}",
                        fontSize = 11.sp,
                        color = contentColor.copy(alpha = 0.7f),
                        fontWeight = FontWeight.SemiBold
                    )
                    if (shortcut.isTriggerEnabled && shortcut.triggerTime != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(contentColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (shortcut.isAutoTrigger) "⚡" else "🔔",
                                    fontSize = 9.sp
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = shortcut.triggerTime,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor
                                )
                            }
                        }
                    }
                    if (shortcut.isTriggerEnabled && shortcut.systemTrigger != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(contentColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (shortcut.isAutoTrigger) "⚡" else "🔌",
                                    fontSize = 9.sp
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = when (shortcut.systemTrigger) {
                                        "BATTERY_LOW" -> "Batería"
                                        "POWER_CONNECTED" -> "Cargado"
                                        "POWER_DISCONNECTED" -> "Desench"
                                        else -> "Físico"
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor
                                )
                            }
                        }
                    }
                    if (isPinned) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(contentColor.copy(alpha = 0.20f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📌 Fijado",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = contentColor
                            )
                        }
                    }
                }
            }
        }
    }
}
