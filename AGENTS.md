# Manual Contextual para IA (System Instruction Context)
Este archivo sirve como manual y referencia vital de arquitectura para cualquier modelo de Inteligencia Artificial que continúe, mantenga o agregue funciones a esta aplicación.

---

## 🛠️ Reglas Básicas de Continuación de Código

1. **Desarrollo Ultra Modular Obligatorio**: Nunca concentres nuevas lógicas o pantallas en archivos existentes. Si agregas un nuevo tipo de interacción o sensor, crea su respectivo modelo o utilitario en módulos independientes dentro de `service/` u otro paquete específico.
2. **Preservar el Tema Visual "Clean Minimalism"**: No añadas degradados agresivos ni colores planos saturados de marca. Sigue usando la paleta pastel definida en `Color.kt` y mantén el alto contraste dinámico con su respectivo color de texto.
3. **Manejo Seguro de Hilos en la Ejecución**: La ejecución de los atajos (`runShortcut` en `ShortcutViewModel.kt`) ocurre dentro de un hilo de Corrutinas controlado (`viewModelScope.launch`). Cada bloque que requiera tiempo (como `WAIT` o `SPEECH`) debe apoyarse estrictamente en llamadas seguras de suspensión, retardos secuenciales (`delay`) y contextos seguros (`Dispatchers.Main`).
4. **Cumplimiento de Privacidad (Zero-SDKs) para F-Droid**: Esta app se mantiene totalmente libre de SDKs de telemetría o servicios cloud externos. Para cualquier lógica de Inteligencia Artificial futura, se prefiere la integración de modelos locales en el dispositivo (como *Gemini Nano* local u offline) o el consumo de APIs totalmente configurables por el usuario final bajo llaves privadas guardadas localmente, protegiendo la filosofía de código libre.
5. **Comprobación de Accesibilidad**: Al programar simulaciones físicas de botones, comprueba siempre si `MyAccessibilityService.isActive` es verdadero. En caso negativo, guía al usuario de manera amigable hacia la pantalla de configuración del dispositivo.
6. **Políticas de Logs y Almacenamiento Estricto**: El almacenamiento en directorios de logs (`warnings` y `crashes`) ubicados dentro del entorno de la app en `Android/data/com.example/` debe limitarse rigurosamente a un máximo de 20MB por carpeta. Al escribir cualquier registro, se debe llamar al servicio de poda para buscar y purgar el archivo más antiguo. La visualización de logs se realiza únicamente a través del **Panel Secreto de Depuración** en Ajustes.
7. **Gatillos de Botones Físicos (Volumen +- x5)**: Interceptar teclas físicas (`onKeyEvent` de `MyAccessibilityService` alimentando a `VolumeButtonsTriggerHelper`) requiere verificar si el servicio está activo para guiar amigablemente al usuario. No agregues intercepciones invasivas ajenas al volumen.
8. **Condicionales y Bloques de Espera Sin Bloquear Hilos**: En bloques de lógica reactiva (como `IfBatteryHandler` o `WaitNotificationHandler`), implementa siempre un tiempo de espera de seguridad (`withTimeoutOrNull`) cuando uses Deferred o suspensión. Esto evita colgar o agotar el servicio de ejecución foreground `ShortcutExecutorService` si las condiciones lógicas o las notificaciones filtradas nunca ocurren.
9. **Soporte de Locución Offline y Selección de Voz**: El modulo de `SpeechHandler` debe preservar la compatibilidad con motores de síntesis de voz locales e independientes (como *Piper* o *Sherpa-Onnx*). Nunca fuerces APIs en la nube para locución; siempre consume y detecta dinámicamente el conjunto de voces provistos por los motores instalados en el sistema Android usando `TextToSpeech.voices`.
10. **Alineación Visual de los Ladrillos Neón**: Al agregar o modificar bloques de acción, respeta la paleta neón brillante establecida en `ActionBlockItem.kt` para conservar la categorización visual por color neón (Voz, Sistema, Sensores, etc.) brindando una interfaz atractiva y con alto contraste.
11. **Flujo de Onboarding Inmersivo**: Si se requiere alterar los estados de bienvenida, recuerda que el flujo se rige en `OnboardingScreen.kt` mediante diapositivas nativas y limpias, guardando el estado mediante preferencias para salvaguardar la privacidad offline. No utilices cargadores de imágenes pesados que pongan en jaque la compilación en dispositivos limitados.

---

## 📦 Proceso de Compilación del APK de Release

Para generar y empaquetar correctamente la aplicación lista para producción (Release) sin errores de caché ni de visualización del editor de AI Studio, sigue estas directrices estrictas:

### 1. Comando de Compilación Obligatorio
Dado que las tareas personalizadas (como `generateReleaseKeystore`) inyectan parámetros dinámicos que entran en conflicto con la serialización estática de Gradle, **SIEMPRE** se debe desactivar el caché de configuración durante la compilación de Release:
```bash
gradle :app:assembleRelease --no-configuration-cache
```

### 2. Gestión del Keystore y Firma
- La generación de la llave JKS (`actionstack-key.jks`) se ejecuta automáticamente de forma perezosa antes del empaquetado de Release. No es necesario regenerarla manualmente a menos que se borre.
- Los datos de firma ya están configurados en el archivo `build.gradle.kts`.

### 3. Evitar Bloqueos de Descarga del Editor (Estrategia del APK)
- Si el APK generado se encuentra dentro de carpetas ocultas o del sistema (como `.build-outputs/`), el entorno de AI Studio suele marcarlo en rojo, arrojar errores de descompresión o descargarse con un peso de `0 bytes`.
- **Solución Efectiva**: Inmediatamente después de compilar, mueve el APK desde la ruta de salida de Gradle hacia la raíz del proyecto con la herramienta de mover archivos:
  - Origen: `/app/build/outputs/apk/release/app-release.apk`
  - Destino: `/app-release.apk`
- Al colocarlo en la raíz como `/app-release.apk`, el editor lo indexará con su peso real (es normal que pese entre **11.5MB y 12.5MB** debido a las bibliotecas nativas e interfaces de Jetpack Compose y KSP) y permitirá descargarlo de manera limpia o exportarlo en el ZIP completo sin corrupción.

---

## 🚀 Ideas de Futuras Integraciones y Optimización del APK

Para elevar el rendimiento técnico y la filosofía de la aplicación, se proponen las siguientes integraciones y optimizaciones:

1. **Optimización con ProGuard & R8 (Reducción de Peso)**:
   - Configurar reglas de descarte más agresivas en `proguard-rules.pro` para optimizar y limpiar bibliotecas de Compose y Kotlin Coroutines no utilizadas.
   - Habilitar la optimización completa (`isMinifyEnabled = true` junto con Shrinking de recursos `isShrinkResources = true`) en la firma Release para reducir el tamaño final del APK por debajo de los 9MB.
2. **Modularización de Sensores y Acciones**:
   - Continuar extrayendo los módulos de procesamiento dentro de `service/` en submódulos independientes si la lógica de geolocalización o de reconocimiento de voz se vuelve compleja.
3. **Motores de Síntesis de Voz (offline) Personalizados**:
   - Facilitar un menú de depuración para cargar modelos ONNX externos en local que ofrezcan locuciones hiper-realistas offline sin depender de las voces predefinidas de Google Android TTS.
4. **Resalvaguarda para F-Droid**:
   - Asegurarse de que no se use ninguna dependencia que involucre Google Play Services de forma fuerte; si se usan mapas o GPS, prever proveedores libres y abiertos como OpenStreetMap o la API nativa de localización de Android de bajo nivel.

