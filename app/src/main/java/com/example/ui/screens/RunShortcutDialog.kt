package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Shortcut
import com.example.ui.screens.components.ShortcutProgressIndicator
import com.example.ui.screens.components.ShortcutPulseIcon

@Composable
fun RunShortcutDialog(
    shortcut: Shortcut,
    currentActionIndex: Int,
    statusText: String,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Cabecera Atajo ejecutándose
                Text(
                    text = "Ejecutando Atajo",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = shortcut.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(android.graphics.Color.parseColor(shortcut.colorHex)),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Emblema de acción con pulso estético (Modularizado)
                ShortcutPulseIcon(colorHex = shortcut.colorHex, iconName = shortcut.iconName)

                Spacer(modifier = Modifier.height(24.dp))

                // Estado detallado (Modularizado)
                ShortcutProgressIndicator(colorHex = shortcut.colorHex, statusText = statusText)

                Spacer(modifier = Modifier.height(8.dp))

                // Contador de progreso
                if (shortcut.actions.isNotEmpty() && currentActionIndex in shortcut.actions.indices) {
                    Text(
                        text = "Acción ${currentActionIndex + 1} de ${shortcut.actions.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón cancelar ejecución
                Button(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Detener Atajo", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

