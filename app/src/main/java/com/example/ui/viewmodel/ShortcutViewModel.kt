package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.ShortcutDatabase
import com.example.data.model.ActionType
import com.example.data.model.Shortcut
import com.example.data.model.ShortcutAction
import com.example.data.repository.ShortcutRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import com.example.service.ShortcutScheduler
import com.example.data.database.DefaultShortcuts
import com.example.service.ActionExecutor

class ShortcutViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val repository: ShortcutRepository
    val allShortcuts: StateFlow<List<Shortcut>>

    // TextToSpeech
    private var textToSpeech: TextToSpeech? = null
    var isTtsReady by mutableStateOf(false)
        private set

    // Navigation State
    var currentScreen by mutableStateOf(Screen.Dashboard)
    var editingShortcutId by mutableStateOf<Int?>(null)

    // Run execution states
    var runningShortcut by mutableStateOf<Shortcut?>(null)
    var runningActionIndex by mutableStateOf(-1)
    var currentStatusText by mutableStateOf("")
    private var runningJob: kotlinx.coroutines.Job? = null

    // Form states for creating/editing shortcuts
    var shortcutFormName by mutableStateOf("")
    var shortcutFormColor by mutableStateOf("#EADDFF") // Clean Violet by default
    var shortcutFormIcon by mutableStateOf("bolt")
    val shortcutFormActions = mutableStateOf<List<ShortcutAction>>(emptyList())
    var shortcutFormTriggerTime by mutableStateOf<String?>(null)
    var shortcutFormTriggerEnabled by mutableStateOf(false)
    var shortcutFormIsAutoTrigger by mutableStateOf(false)
    var shortcutFormSystemTrigger by mutableStateOf<String?>(null)

    // Onboarding SharedPreferences
    private val sharedPrefs = application.getSharedPreferences("actionstack_prefs", Context.MODE_PRIVATE)
    var showOnboarding by mutableStateOf(sharedPrefs.getBoolean("first_run_v1", true))
        private set

    fun completeOnboarding() {
        sharedPrefs.edit().putBoolean("first_run_v1", false).apply()
        showOnboarding = false
    }

    var qsTileShortcutId by mutableStateOf(sharedPrefs.getInt("qs_tile_shortcut_id", -1))
        private set

    fun pinShortcutToTile(shortcutId: Int) {
        sharedPrefs.edit().putInt("qs_tile_shortcut_id", shortcutId).apply()
        qsTileShortcutId = shortcutId
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                android.service.quicksettings.TileService.requestListeningState(
                    getApplication(),
                    android.content.ComponentName(getApplication(), com.example.service.ShortcutTileService::class.java)
                )
            } catch (e: Exception) {
                Log.e("ShortcutViewModel", "Error actualizando tile de ajustes rápidos", e)
            }
        }
        Toast.makeText(getApplication(), "📌 Atajo listo en tus Ajustes Rápidos", Toast.LENGTH_SHORT).show()
    }

    // Alarm / Reloj Scheduler Methods
    fun toggleAlarm(context: Context, shortcut: Shortcut, enabled: Boolean) {
        viewModelScope.launch {
            val updated = shortcut.copy(isTriggerEnabled = enabled)
            repository.update(updated)
            if (enabled) {
                shortcut.triggerTime?.let { time ->
                    ShortcutScheduler.scheduleAlarm(context, shortcut.id, time)
                }
            } else {
                ShortcutScheduler.cancelAlarm(context, shortcut.id)
            }
        }
    }

    fun updateAlarmTime(context: Context, shortcut: Shortcut, time: String) {
        viewModelScope.launch {
            val updated = shortcut.copy(triggerTime = time, isTriggerEnabled = true)
            repository.update(updated)
            ShortcutScheduler.scheduleAlarm(context, shortcut.id, time)
        }
    }

    fun runShortcutById(shortcutId: Int) {
        viewModelScope.launch {
            val shortcut = repository.getShortcutById(shortcutId)
            if (shortcut != null) {
                runShortcut(shortcut)
            }
        }
    }

    // Listado de aplicaciones instaladas para selector visual
    private val _installedApps = kotlinx.coroutines.flow.MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: kotlinx.coroutines.flow.StateFlow<List<AppInfo>> = _installedApps

    fun loadInstalledApps(context: Context) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val pm = context.packageManager
                val intent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                val list = pm.queryIntentActivities(intent, 0).map { resolveInfo ->
                    AppInfo(
                        label = resolveInfo.loadLabel(pm).toString(),
                        packageName = resolveInfo.activityInfo.packageName
                    )
                }.distinctBy { it.packageName }.sortedBy { it.label.lowercase() }
                _installedApps.value = list
            } catch (e: Exception) {
                Log.e("ShortcutViewModel", "Error cargando aplicaciones", e)
            }
        }
    }

    init {
        val database = ShortcutDatabase.getDatabase(application)
        repository = ShortcutRepository(database.shortcutDao())
        
        allShortcuts = repository.allShortcuts
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        // Pre-poplar por defecto si vacío con verificación síncrona/segura en el hilo IO
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val existing = database.shortcutDao().getAllShortcutsSync()
            if (existing.isEmpty()) {
                seedDefaultShortcuts()
            }
        }

        // Initialize TTS
        try {
            textToSpeech = TextToSpeech(application, this)
        } catch (e: Exception) {
            Log.e("ShortcutViewModel", "Error al inicializar TTS: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale("es", "ES"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Forzar fallback de locale predeterminado si es/ES no está instalado
                textToSpeech?.setLanguage(Locale.getDefault())
            }
            isTtsReady = true
        } else {
            Log.e("ShortcutViewModel", "Error al iniciar TextToSpeech de Android.")
        }
    }

    fun getAvailableVoices(): List<String> {
        val list = mutableListOf<String>()
        list.add("Predeterminada")
        if (isTtsReady && textToSpeech != null) {
            try {
                val voices = textToSpeech?.voices
                if (!voices.isNullOrEmpty()) {
                    for (v in voices) {
                        list.add(v.name)
                    }
                }
            } catch (e: Exception) {
                Log.e("ShortcutViewModel", "Error al obtener lista de voces: ${e.message}")
            }
        }
        return list
    }

    private suspend fun seedDefaultShortcuts() {
        for (shortcut in DefaultShortcuts.list) {
            repository.insert(shortcut)
        }
    }

    // Navigation and Action Composers
    fun openCreateShortcut(shortcutId: Int? = null) {
        if (shortcutId != null) {
            viewModelScope.launch {
                val s = repository.getShortcutById(shortcutId)
                if (s != null) {
                    editingShortcutId = s.id
                    shortcutFormName = s.name
                    shortcutFormColor = s.colorHex
                    shortcutFormIcon = s.iconName
                    shortcutFormActions.value = s.actions
                    shortcutFormTriggerTime = s.triggerTime
                    shortcutFormTriggerEnabled = s.isTriggerEnabled
                    shortcutFormIsAutoTrigger = s.isAutoTrigger
                    shortcutFormSystemTrigger = s.systemTrigger
                    currentScreen = Screen.CreateShortcut
                }
            }
        } else {
            editingShortcutId = null
            shortcutFormName = ""
            shortcutFormColor = "#FF9500" // Orange default
            shortcutFormIcon = "bolt"
            shortcutFormActions.value = emptyList()
            shortcutFormTriggerTime = null
            shortcutFormTriggerEnabled = false
            shortcutFormIsAutoTrigger = false
            shortcutFormSystemTrigger = null
            currentScreen = Screen.CreateShortcut
        }
    }

    fun backToDashboard() {
        currentScreen = Screen.Dashboard
        editingShortcutId = null
    }

    fun addActionToForm(type: ActionType) {
        val defaultParams = when (type) {
            ActionType.SHOW_MESSAGE -> mapOf("message" to "¡Mensaje de mi atajo!")
            ActionType.SPEECH -> mapOf("text" to "Texto de ejemplo para locución.", "rate" to "1.0", "voice" to "Predeterminada")
            ActionType.SHARE -> mapOf("text" to "Texto para compartir.")
            ActionType.OPEN_URL -> mapOf("url" to "https://")
            ActionType.VIBRATE -> emptyMap()
            ActionType.WAIT -> mapOf("seconds" to "5")
            ActionType.LAUNCH_APP -> mapOf("packageName" to "com.android.chrome")
            ActionType.ACCESSIBILITY_BACK -> emptyMap()
            ActionType.ACCESSIBILITY_HOME -> emptyMap()
            ActionType.ACCESSIBILITY_NOTIFICATIONS -> emptyMap()
            ActionType.NOTIFICATION -> mapOf("title" to "ActionStack", "message" to "Notificación de ejemplo")
            ActionType.PROXIMITY_SENSOR -> mapOf("condition" to "TAPADO")
            ActionType.BATTERY_LEVEL -> mapOf("percentage" to "50", "comparison" to "MENOR")
            ActionType.CUSTOM_JS -> mapOf("script" to "// Escribe tu script JS aquí (Ej IIFE)\nreturn 'Batería: ' + (75) + '%';")
            ActionType.FLASHLIGHT -> mapOf("state" to "TOGGLE")
            ActionType.IF_BATTERY -> mapOf("percentage" to "50", "comparison" to "MENOR", "then_action_type" to "SPEECH", "then_text" to "Batería baja detectada", "else_action_type" to "VIBRATE")
            ActionType.WAIT_NOTIFICATION -> mapOf("packageName" to "Cualquiera", "senderText" to "")
        }
        val newAction = ShortcutAction(type, defaultParams)
        shortcutFormActions.value = shortcutFormActions.value + newAction
    }

    fun removeActionFromForm(index: Int) {
        val list = shortcutFormActions.value.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            shortcutFormActions.value = list
        }
    }

    fun updateActionParameter(actionIndex: Int, key: String, value: String) {
        val list = shortcutFormActions.value.toMutableList()
        if (actionIndex in list.indices) {
            val action = list[actionIndex]
            val params = action.parameters.toMutableMap()
            params[key] = value
            list[actionIndex] = action.copy(parameters = params)
            shortcutFormActions.value = list
        }
    }

    fun saveShortcut() {
        if (shortcutFormName.isBlank()) return
        
        viewModelScope.launch {
            val shortcut = Shortcut(
                id = editingShortcutId ?: 0,
                name = shortcutFormName,
                colorHex = shortcutFormColor,
                iconName = shortcutFormIcon,
                actions = shortcutFormActions.value,
                triggerTime = shortcutFormTriggerTime,
                isTriggerEnabled = shortcutFormTriggerEnabled,
                isAutoTrigger = shortcutFormIsAutoTrigger,
                systemTrigger = shortcutFormSystemTrigger
            )
            
            val finalId = if (editingShortcutId != null) {
                repository.update(shortcut)
                editingShortcutId!!
            } else {
                repository.insert(shortcut).toInt()
            }

            // Respaldar atajo automáticamente en Android/data/com.example/files/shortcuts/
            val backupShortcut = shortcut.copy(id = finalId)
            com.example.service.FileStorageHelper.saveShortcutBackup(backupShortcut)

            val context = getApplication<Application>().applicationContext
            if (shortcutFormTriggerEnabled && shortcutFormTriggerTime != null) {
                ShortcutScheduler.scheduleAlarm(context, finalId, shortcutFormTriggerTime!!)
            } else {
                ShortcutScheduler.cancelAlarm(context, finalId)
            }

            backToDashboard()
        }
    }

    fun deleteShortcut(shortcut: Shortcut) {
        viewModelScope.launch {
            repository.delete(shortcut)
        }
    }

    fun saveShortcutFromPreset(shortcut: Shortcut) {
        viewModelScope.launch {
            repository.insert(shortcut.copy(id = 0))
        }
    }

    // Runner Engine
    fun runShortcut(shortcut: Shortcut) {
        val context = getApplication<Application>().applicationContext
        if (runningShortcut != null) {
            Toast.makeText(context, "Un atajo ya se está ejecutando", Toast.LENGTH_SHORT).show()
            return
        }

        runningShortcut = shortcut
        runningActionIndex = 0

        runningJob = viewModelScope.launch {
            try {
                for (index in shortcut.actions.indices) {
                    runningActionIndex = index
                    val action = shortcut.actions[index]
                    currentStatusText = "Ejecutando ${action.type.displayName}..."

                    try {
                        com.example.service.ActionExecutor.execute(
                            action = action,
                            context = context,
                            textToSpeech = textToSpeech,
                            isTtsReady = isTtsReady,
                            onStatusUpdate = { currentStatusText = it }
                        )
                    } catch (e: Exception) {
                        Log.e("ShortcutViewModel", "Error ejecutando acción: ${e.message}")
                        currentStatusText = "Error en ${action.type.displayName}"
                        delay(1000)
                    }
                    delay(400) // Delay estético entre acciones
                }
            } finally {
                runningShortcut = null
                runningActionIndex = -1
                currentStatusText = ""
                runningJob = null
            }
        }
    }

    fun stopRunningShortcut() {
        textToSpeech?.stop()
        runningJob?.cancel()
        runningShortcut = null
        runningActionIndex = -1
        currentStatusText = ""
        runningJob = null
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}
