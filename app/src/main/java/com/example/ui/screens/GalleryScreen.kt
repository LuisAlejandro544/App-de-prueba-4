package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ShortcutPresets
import com.example.ui.screens.components.PresetCard
import com.example.ui.viewmodel.ShortcutViewModel
import kotlinx.coroutines.launch

@Composable
fun GalleryScreen(viewModel: ShortcutViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val presets = ShortcutPresets.presets

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp)
    ) {
        Text(
            text = "Presets listos para usar",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Presiona cualquier preset para agregarlo instantáneamente a tus atajos activos.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        presets.forEach { preset ->
            PresetCard(
                preset = preset,
                onImportClick = {
                    coroutineScope.launch {
                        viewModel.saveShortcutFromPreset(preset)
                        Toast.makeText(context, "Preset importado: ${preset.name}", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

