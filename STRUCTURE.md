# 📂 Estructura de ActionStack

ActionStack adopta una arquitectura **ultra modular** y limpia de MVVM (Model-View-ViewModel) con Room Database para una persistencia rápida de datos locales sin APIs de la nube, cumpliendo los principios de privacidad para lanzamientos listos en **F-Droid**.

---

## 🏗️ Directorios y Capas Clave

```text
/app/src/main/java/com/example/
│
├── MainActivity.kt                  # Punto de entrada / Enrutamiento animado global
│
├── data/                            # Capa de Datos (Modelos, Base de datos y Repositorios)
│   ├── model/
│   │   ├── Shortcut.kt              # Entidad principal del Atajo (Definición Room)
│   │   ├── ShortcutAction.kt        # Modelo de Acción: define cada bloque, sus parámetros y la enumeración ActionType
│   │   ├── ShortcutPresets.kt       # Proveedor de presets para la Galería de Atajos de ActionStack
│   │   └── AutomationIdea.kt        # Modelo y proveedor de sugerencias/ideas de integración opensource
│   │
│   ├── database/
│   │   ├── Converters.kt            # Convertidor de tipos para serializar listas de acciones en SQLite
│   │   ├── ShortcutDao.kt           # Consultas Room rápidas para inserciones, eliminaciones e histórico
│   │   └── ShortcutDatabase.kt      # Inicialización de Room Database
│   │
│   └── repository/
│       └── ShortcutRepository.kt    # Capa limpia de acceso unificado que aísla de la base de datos
│
├── service/                         # Servicios en segundo plano e integraciones del sistema Android
│   ├── AlarmReceiver.kt             # Receptor de eventos temporales para la ejecución automatizada de atajos
│   ├── BootReceiver.kt              # Receptor del encendido del dispositivo que re-programa las alarmas tras reiniciar
│   ├── SystemTriggerReceiver.kt     # Receptor de eventos físicos del sistema (batería, cargador conectado)
│   ├── ShakeSensorHelper.kt         # Detector de agitación del teléfono mediante acelerómetro de bajo consumo
│   ├── VolumeButtonsTriggerHelper.kt# Detector de pulsaciones múltiples en botones físicos de volumen (+/-)
│   ├── ShortcutTileService.kt       # Servicio de Ajustes Rápidos (Quick Settings Tile) para disparar atajos fijados
│   ├── MyAccessibilityService.kt    # Intercepta hardware (Vol +/-) y emula toques físicos (Back, Home, Notificaciones)
│   ├── ShortcutExecutorService.kt   # Servicio Foreground robusto para running persistente cuando la App está cerrada
│   ├── AccessibilityActionHelper.kt # Gestor modular para mapear y accionar comandos del Servicio de Accesibilidad
│   ├── ShortcutScheduler.kt         # Orquestador unificado para programar y cancelar alarmas en el AlarmManager
│   ├── AlarmIntentFactory.kt        # Fábrica modular de PendingIntents y cálculos horarios para AlarmManager
│   ├── ActionExecutor.kt            # Coordinador central de ejecución que delega las tareas en handlers individuales
│   ├── JsEvaluator.kt               # Motor local y offline de evaluación de scripts JavaScript en WebView encapsulado
│   ├── ProximitySensorHelper.kt     # Manejador del sensor de proximidad mediante corrutinas de suspensión reactivas
│   ├── BatteryHelper.kt             # Lector modular de la API de administración de energía y porcentaje de batería
│   ├── FileStorageHelper.kt         # Orquestador ultra-modular de almacenamiento y logs (poda de 20MB max) con capturador de fallos (Crash Handler)
│   └── handlers/                    # Manejadores de acciones individuales desacoplados (Ultra Modular)
│       ├── ActionHandler.kt         # Interfaz base para los ejecutores de acciones
│       ├── ShowMessageHandler.kt    # Ejecutor de la acción MOSTRAR_MENSAJE
│       ├── SpeechHandler.kt         # Ejecutor de la acción HABLAR (TTS) con selector de voces dinámico y soporte multi-motor local (Piper, Sherpa-Onnx, etc.)
│       ├── VibrateHandler.kt        # Ejecutor de la acción VIBRAR
│       ├── ShareHandler.kt          # Ejecutor de la acción COMPARTIR
│       ├── OpenUrlHandler.kt        # Ejecutor de la acción ABRIR_URL
│       ├── WaitHandler.kt           # Ejecutor de la acción ESPERAR
│       ├── LaunchAppHandler.kt      # Ejecutor de la acción LANZAR_APP
│       ├── AccessibilityHelper.kt   # Gestor para redefinir apertura de ajustes de accesibilidad
│       ├── AccessibilityBackHandler.kt # Ejecutor de volver atrás mediante accesibilidad
│       ├── AccessibilityHomeHandler.kt # Ejecutor de volver al inicio mediante accesibilidad
│       ├── AccessibilityNotificationsHandler.kt # Ejecutor de panel de notificaciones
│       ├── NotificationHandler.kt   # Ejecutor de notificaciones del sistema
│       ├── ProximitySensorHandler.kt# Gestor del sensor de proximidad suspendible
│       ├── BatteryLevelHandler.kt   # Analizador y comparador del nivel de carga de batería
│       ├── CustomJsHandler.kt       # Orquestador del evaluador de Scripts en JavaScript
│       ├── FlashlightHandler.kt     # Controlador del hardware de linterna del dispositivo (ON, OFF, TOGGLE)
│       ├── IfBatteryHandler.kt      # Evaluador condicional de batería con sub-rutas dinámicas (IF/ELSE)
│       └── WaitNotificationHandler.kt# Receptor de bloqueo asíncrono para filtros de notificaciones/mensajería
│
└── ui/                              # Interfaces de Usuario fluidas en Jetpack Compose
    ├── icons/
    │   └── ShortcutIconHelper.kt    # Gestor dinámico que asocia strings a Material Symbols nativos
    │
    ├── theme/
    │   ├── Color.kt                 # Paleta pastel de Clean Minimalism y variantes de contraste oscuro
    │   ├── Type.kt                  # Tipografía de títulos robusta y tamaños expresivos
    │   └── Theme.kt                 # Configuración del motor de temas dinámico de Material 3
    │
    ├── viewmodel/
    │   ├── ShortcutViewModel.kt     # Motor lógico secuencial de ejecución con hilos, TTS, vibraciones táctiles y notificaciones
    │   ├── AppInfo.kt               # Representación limpia del identificador y label de apps instaladas
    │   └── Screen.kt                # Enumeración de estados de la pantalla de enrutamiento del Dashboard
    │
    └── screens/
        ├── ShortcutsScreen.kt       # Pantalla principal simplificada (organizada con Bottom Navigation)
        ├── CreateShortcutScreen.kt  # Editor visual de acciones secuenciales paso a paso
        ├── OnboardingScreen.kt      # Pantalla inmersiva de bienvenida con diapositivas neón
        ├── GalleryScreen.kt         # Galería modular de presets preconfigurados listos para importar
        ├── AutomationIdeasScreen.kt # Tarjetas con ideas para automatizaciones locales y publicaciones en F-Droid
        ├── RunShortcutDialog.kt     # Superposición de pulso de ondas que muestra la acción actual en ejecución
        │
        └── components/              # Bloques de UI modulares desacoplados (Evitan romper el código al actualizar)
            ├── ShortcutCard.kt             # Tarjeta animada e interactiva del atajo adaptada al color asignado
            ├── ShortcutOptionsDialog.kt    # Menú extendido al mantener pulsado un Atajo (Ejecutar, Editar, Eliminar)
            ├── DeleteConfirmationDialog.kt # Modal de advertencia seguro antes de purgar un atajo
            ├── EmptyShortcutsState.kt      # Ilustración y llamada a la acción cuando no hay atajos registrados
            ├── AppSelectorDialog.kt        # Buscador y selector modal de aplicaciones instaladas en el sistema
            ├── ShortcutSystemTriggerPicker.kt # Selector visual e interactivo de gatillos físicos de hardware
            ├── InteractiveShortcutGuide.kt # Guía visual plegable tipo 'Dominó' explicando el flujo secuencial
            ├── ActionBlockItem.kt          # Tarjeta modular para editar componentes e inputs de cada tipo de acción
            ├── ShortcutColorPicker.kt      # carrusel de selección rápida de color pastel para el atajo
            ├── ShortcutIconPicker.kt       # Selector optimizado de iconos Material M3
            ├── ShortcutClockScheduler.kt   # Selector detallado de alarma por reloj y modos de lanzamiento
            ├── ShortcutsHeader.kt          # Cabecera dinámica superior con estilos iOS y modos de edición
            ├── ShortcutsBottomBar.kt       # Barra inferior de pestañas con navegación fluida de Material 3
            ├── SettingsDialog.kt           # Menú de Ajustes del Sistema con panel secreto desbloqueable de diagnóstico/logs (Warnings y Crashes)
            ├── PresetCard.kt               # Tarjeta modular para renderizar presets de la Galería
            ├── AutomationIdeaItem.kt       # Tarjeta modular para sugerencias de automatización local
            ├── ShortcutPulseIcon.kt        # Efecto de pulso con ondas animadas para el diálogo de ejecución
            └── ShortcutProgressIndicator.kt # Indicador circular de progreso de la acción actual

```

---

## 🛠️ Principios de Modularidad

1. **Desarrollo Modular Ultra Obligatorio**: Las vistas pesadas fueron desestructuradas en el directorio `components/`. Ningún componente satélite comparte archivo con la pantalla madre, permitiendo aislar errores de depuración en compilación de inmediato.
2. **Independencia en Tipos de Acción**: Las definiciones de bloques se rigen por la enumeración `ActionType` en `ShortcutAction.kt`. Para mantener un desarrollo ultra modular, cada tipo de acción cuenta con su propio manejador desacoplado bajo `service/handlers/` implementando la interfaz base `ActionHandler`. Esto permite agregar, modificar o corregir el comportamiento de cualquier acción de forma aislada sin riesgo de romper el orquestador principal `ActionExecutor`.
3. **Control Centralizado de Alarmas**: Con el nuevo `ShortcutScheduler`, tanto `ShortcutViewModel` como `BootReceiver` consumen de forma unificada el servicio de sincronización de alarmas, evitando lógicas duplicadas e inconsistencias en persistencia.
4. **Cero-SDKs y Privacidad**: Todo permanece local. Esta aplicación es infinitamente mantenible y auditable por repositorios Open-Source.
