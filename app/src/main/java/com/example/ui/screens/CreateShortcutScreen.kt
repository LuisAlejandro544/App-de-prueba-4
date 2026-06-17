package com.example.ui.screens

import com.example.ui.screens.components.AppSelectorDialog
import com.example.ui.screens.components.InteractiveShortcutGuide
import com.example.ui.screens.components.ActionBlockItem
import com.example.ui.screens.components.ShortcutColorPicker
import com.example.ui.screens.components.ShortcutIconPicker
import com.example.ui.screens.components.ShortcutClockScheduler
import com.example.ui.screens.components.ShortcutSystemTriggerPicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActionType
import com.example.data.model.ShortcutAction
import com.example.ui.icons.ShortcutIconHelper
import com.example.ui.viewmodel.ShortcutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateShortcutScreen(
    viewModel: ShortcutViewModel,
    onBack: () -> Unit
) {
    // Paleta de colores minimalista pastel y limpia
    val colorPalette = listOf(
        "#EADDFF", // Violet
        "#D0E4FF", // Sky Blue
        "#D1E8D1", // Sage Green
        "#FFDBCB", // Peach
        "#F1DBFA", // Lavender
        "#FFD1DC", // Rose Quartz
        "#FFF0B5", // Soft Butter
        "#C2F0C2"  // Mint Pastel
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                    Text(
                        text = if (viewModel.editingShortcutId != null) "Editar Atajo" else "Nuevo Atajo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Button(
                    onClick = {
                        if (viewModel.shortcutFormName.isNotBlank()) {
                            viewModel.saveShortcut()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.shortcutFormName.isNotBlank()) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (viewModel.shortcutFormName.isNotBlank()) Color.White
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Text("Listo", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        var selectedTab by remember { mutableStateOf(0) }
        var helpActionType by remember { mutableStateOf<ActionType?>(null) }
        val tabs = listOf("⚙️ Ajustes", "⚡ Gatillos", "🧩 Ladrillos")

        if (helpActionType != null) {
            com.example.ui.screens.components.ActionHelpDialog(
                actionType = helpActionType!!,
                onDismiss = { helpActionType = null }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Elegant M3-style Tab Switcher Row with soft background and rounded corners
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                tabs.forEachIndexed { index, label ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { selectedTab = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // PESTAÑA 0: CONFIGURACIÓN GENERAL (Nombre, Color y Icono)
                if (selectedTab == 0) {
                    item {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = "Nombre del Atajo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = viewModel.shortcutFormName,
                                onValueChange = { viewModel.shortcutFormName = it },
                                placeholder = { Text("Ej. Mi Flujo Matutino") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Selección de Color
                    item {
                        ShortcutColorPicker(viewModel = viewModel, colorPalette = colorPalette)
                    }

                    // Selección de Icono
                    item {
                        ShortcutIconPicker(viewModel = viewModel)
                    }

                    // Guía Visual al final de la primera pestaña para educar sin estorbar
                    item {
                        InteractiveShortcutGuide()
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // PESTAÑA 1: GATILLOS Y AUTOMATIZACIONES (Reloj y triggers de hardware)
                if (selectedTab == 1) {
                    // Programación por Reloj (Trigger de alarma)
                    item {
                        Box(modifier = Modifier.padding(top = 8.dp)) {
                            ShortcutClockScheduler(viewModel = viewModel)
                        }
                    }

                    // Gatillos del sistema/hardware
                    item {
                        ShortcutSystemTriggerPicker(viewModel = viewModel)
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // PESTAÑA 2: LADRILLOS / ACCIONES (Configuración del Flujo)
                if (selectedTab == 2) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ladrillos de Acción",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${viewModel.shortcutFormActions.value.size} en total",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }

                    if (viewModel.shortcutFormActions.value.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Este atajo no tiene pasos todavía.\nToca los bloques de abajo para ir construyendo tu flujo.",
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 18.sp,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    } else {
                        itemsIndexed(viewModel.shortcutFormActions.value) { index, action ->
                            ActionBlockItem(
                                index = index,
                                action = action,
                                viewModel = viewModel,
                                onParamChange = { key, newValue ->
                                    viewModel.updateActionParameter(index, key, newValue)
                                },
                                onRemove = {
                                    viewModel.removeActionFromForm(index)
                                }
                            )
                        }
                    }

                    // Paleta de acciones a añadir en una grilla compacta de dos columnas por categoría
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Añadir Bloque de Acción",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Categorías para agrupar y colapsar la altura visual de las acciones
                    val actionCategories = listOf(
                        Pair("📢 Mensajes y Voz", listOf(ActionType.SHOW_MESSAGE, ActionType.SPEECH, ActionType.NOTIFICATION)),
                        Pair("🌐 Enlaces y Aplicaciones", listOf(ActionType.LAUNCH_APP, ActionType.OPEN_URL, ActionType.SHARE)),
                        Pair("🧠 Lógica y Automatización", listOf(ActionType.IF_BATTERY, ActionType.WAIT_NOTIFICATION)),
                        Pair("🔧 Control de Dispositivo", listOf(ActionType.FLASHLIGHT, ActionType.VIBRATE, ActionType.ACCESSIBILITY_BACK, ActionType.ACCESSIBILITY_HOME, ActionType.ACCESSIBILITY_NOTIFICATIONS)),
                        Pair("⏳ Flujos de Espera y Sensores", listOf(ActionType.WAIT, ActionType.PROXIMITY_SENSOR, ActionType.BATTERY_LEVEL, ActionType.CUSTOM_JS))
                    )

                    actionCategories.forEach { (catName, actions) ->
                        item {
                            Text(
                                text = catName,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }

                        val chunkedActions = actions.chunked(2)
                        chunkedActions.forEach { rowActions ->
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowActions.forEach { actionType ->
                                        Card(
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                            onClick = { viewModel.addActionToForm(actionType) },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(10.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(26.dp)
                                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = ShortcutIconHelper.getIcon(actionType.iconName),
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        IconButton(
                                                            onClick = { helpActionType = actionType },
                                                            modifier = Modifier.size(20.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Info,
                                                                contentDescription = "Ayuda sobre esta de acción",
                                                                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                                                                modifier = Modifier.size(14.dp)
                                                            )
                                                        }
                                                        Icon(
                                                            imageVector = Icons.Default.Add,
                                                            contentDescription = "Añadir",
                                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = actionType.displayName,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    fontSize = 11.sp,
                                                    maxLines = 1,
                                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = actionType.description,
                                                    fontSize = 9.sp,
                                                    lineHeight = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                                    maxLines = 2,
                                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                    if (rowActions.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}
