package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Shortcut
import com.example.ui.screens.components.*
import com.example.ui.viewmodel.ShortcutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortcutsScreen(
    viewModel: ShortcutViewModel,
    onCreateClick: () -> Unit,
    shortcuts: List<Shortcut>
) {
    // Tab actual del Dashboard
    var selectedTab by remember { mutableIntStateOf(0) }
    var isEditMode by remember { mutableStateOf(false) }

    // Estados para control de diálogos interactivos
    var activeOptionsShortcut by remember { mutableStateOf<Shortcut?>(null) }
    var activeDeleteConfirmShortcut by remember { mutableStateOf<Shortcut?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    if (showSettingsDialog) {
        SettingsDialog(
            onDismiss = { showSettingsDialog = false }
        )
    }

    Scaffold(
        bottomBar = {
            ShortcutsBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = onCreateClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar Atajo",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Cabecera Superior estilo iOS
            ShortcutsHeader(
                selectedTab = selectedTab,
                isEditMode = isEditMode,
                onEditModeToggle = { isEditMode = !isEditMode },
                onSettingsClick = { showSettingsDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // TAB 0: Mis Atajos (Grid de Atajos Guardados)
                    if (shortcuts.isEmpty()) {
                        EmptyShortcutsState(onCreateClick)
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(shortcuts, key = { it.id }) { shortcut ->
                                ShortcutCard(
                                    shortcut = shortcut,
                                    isEditMode = isEditMode,
                                    isPinned = viewModel.qsTileShortcutId == shortcut.id,
                                    onRunClick = { viewModel.runShortcut(shortcut) },
                                    onEditClick = { viewModel.openCreateShortcut(shortcut.id) },
                                    onDeleteClick = { activeDeleteConfirmShortcut = shortcut },
                                    onLongClick = { activeOptionsShortcut = shortcut }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // TAB 1: Presets
                    GalleryScreen(viewModel = viewModel)
                }
                2 -> {
                    // TAB 2: Automatización
                    AutomationIdeasScreen()
                }
            }
        }
    }

    // Diálogos de Gestión de Atajos
    activeOptionsShortcut?.let { shortcut ->
        ShortcutOptionsDialog(
            shortcut = shortcut,
            onDismiss = { activeOptionsShortcut = null },
            onRun = { viewModel.runShortcut(shortcut) },
            onEdit = { viewModel.openCreateShortcut(shortcut.id) },
            onDelete = {
                activeOptionsShortcut = null
                activeDeleteConfirmShortcut = shortcut
            },
            onPinToTile = {
                viewModel.pinShortcutToTile(shortcut.id)
            }
        )
    }

    activeDeleteConfirmShortcut?.let { shortcut ->
        DeleteConfirmationDialog(
            shortcutName = shortcut.name,
            onDismiss = { activeDeleteConfirmShortcut = null },
            onConfirm = {
                viewModel.deleteShortcut(shortcut)
                activeDeleteConfirmShortcut = null
                Toast.makeText(context, "Atajo eliminado con éxito", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
