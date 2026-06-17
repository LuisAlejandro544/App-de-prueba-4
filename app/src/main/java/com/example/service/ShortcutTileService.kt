package com.example.service

import android.content.Context
import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import com.example.MainActivity
import com.example.data.database.ShortcutDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShortcutTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        val tile = qsTile ?: return
        val sharedPrefs = getSharedPreferences("actionstack_prefs", Context.MODE_PRIVATE)
        val shortcutId = sharedPrefs.getInt("qs_tile_shortcut_id", -1)

        if (shortcutId == -1) {
            tile.state = Tile.STATE_INACTIVE
            tile.label = "Ejecutar Atajo"
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                tile.subtitle = "Configurar"
            }
        } else {
            tile.state = Tile.STATE_ACTIVE
            val database = ShortcutDatabase.getDatabase(this)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val shortcut = database.shortcutDao().getShortcutById(shortcutId)
                    launch(Dispatchers.Main) {
                        if (shortcut != null) {
                            tile.label = shortcut.name
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                tile.subtitle = "Listo para lanzar"
                            }
                        } else {
                            tile.label = "Atajo inexistente"
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                tile.subtitle = "Pin removido"
                            }
                        }
                        tile.updateTile()
                    }
                } catch (e: Exception) {
                    Log.e("ShortcutTileService", "Error cargando datos de atajo pinned", e)
                }
            }
        }
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        val sharedPrefs = getSharedPreferences("actionstack_prefs", Context.MODE_PRIVATE)
        val shortcutId = sharedPrefs.getInt("qs_tile_shortcut_id", -1)

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (shortcutId != -1) {
                putExtra("RUN_SHORTCUT_ID", shortcutId)
            }
        }

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // Android 14+ requires PendingIntent for startActivityAndCollapse on newer devices
                val pendingIntent = android.app.PendingIntent.getActivity(
                    this,
                    999,
                    intent,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                )
                startActivityAndCollapse(pendingIntent)
            } else {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(intent)
            }
        } catch (e: Exception) {
            Log.e("ShortcutTileService", "Error iniciando actividad desde Tile", e)
            // Fallback con flag estándar de actividad
            try {
                startActivity(intent)
            } catch (ex: Exception) {
                Log.e("ShortcutTileService", "Re-error de inicio de actividad", ex)
            }
        }

        if (shortcutId == -1) {
            Toast.makeText(this, "Fija un Atajo dentro de la app para activarlo aquí", Toast.LENGTH_LONG).show()
        }
    }
}
