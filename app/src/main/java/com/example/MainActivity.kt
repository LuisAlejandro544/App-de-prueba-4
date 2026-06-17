package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CreateShortcutScreen
import com.example.ui.screens.RunShortcutDialog
import com.example.ui.screens.ShortcutsScreen
import com.example.ui.viewmodel.Screen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ShortcutViewModel
import com.example.service.ShakeSensorHelper
import com.example.service.MyAccessibilityService

import com.example.ui.screens.OnboardingScreen

class MainActivity : ComponentActivity() {
  private var shakeSensorHelper: ShakeSensorHelper? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Inicializar helper de almacenamiento ultra-modular y capturador de crasheos
    com.example.service.FileStorageHelper.initializeDirs(this)
    com.example.service.FileStorageHelper.setupCrashHandler()

    val startShortcutId = intent?.getIntExtra("RUN_SHORTCUT_ID", -1) ?: -1
    setContent {
      MyApplicationTheme {
        ShortcutsAppEntry(shortcutIdToRunOnStart = startShortcutId)
      }
    }
  }

  override fun onStart() {
    super.onStart()
    if (!MyAccessibilityService.isActive) {
      shakeSensorHelper = ShakeSensorHelper(this).apply {
        startListening()
      }
    }
  }

  override fun onStop() {
    shakeSensorHelper?.stopListening()
    shakeSensorHelper = null
    super.onStop()
  }

  override fun onNewIntent(intent: android.content.Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    val shortcutId = intent.getIntExtra("RUN_SHORTCUT_ID", -1)
    if (shortcutId != -1) {
      val viewModel = androidx.lifecycle.ViewModelProvider(this)[com.example.ui.viewmodel.ShortcutViewModel::class.java]
      viewModel.runShortcutById(shortcutId)
    }
  }
}

@Composable
fun ShortcutsAppEntry(shortcutIdToRunOnStart: Int = -1) {
  val viewModel: ShortcutViewModel = viewModel()
  val shortcuts by viewModel.allShortcuts.collectAsStateWithLifecycle()

  androidx.compose.runtime.LaunchedEffect(shortcutIdToRunOnStart) {
    if (shortcutIdToRunOnStart != -1) {
      viewModel.runShortcutById(shortcutIdToRunOnStart)
    }
  }

  Box(
    modifier = Modifier.fillMaxSize()
  ) {
    if (viewModel.showOnboarding) {
      OnboardingScreen(viewModel = viewModel)
    } else {
      AnimatedContent(
        targetState = viewModel.currentScreen,
        transitionSpec = {
          fadeIn() togetherWith fadeOut()
        },
        label = "screen_trans"
      ) { screen ->
        when (screen) {
          Screen.Dashboard -> {
            ShortcutsScreen(
              viewModel = viewModel,
              onCreateClick = { viewModel.openCreateShortcut() },
              shortcuts = shortcuts
            )
          }
          Screen.CreateShortcut -> {
            CreateShortcutScreen(
              viewModel = viewModel,
              onBack = { viewModel.backToDashboard() }
            )
          }
        }
      }
    }

    // Dialog de ejecución a nivel global de navegación
    val activeShortcut = viewModel.runningShortcut
    if (activeShortcut != null) {
      RunShortcutDialog(
        shortcut = activeShortcut,
        currentActionIndex = viewModel.runningActionIndex,
        statusText = viewModel.currentStatusText,
        onDismissRequest = {
          viewModel.stopRunningShortcut()
        }
      )
    }
  }
}
