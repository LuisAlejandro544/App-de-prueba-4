# 🗺️ Roadmap de ActionStack (Hacia F-Droid)

ActionStack aspira a ser el equivalente de código abierto definitivo a "Shortcuts" (Atajos) de iOS para Android. El proyecto está enfocado en la privacidad total, operando al 100% en local y sin telemetría, lo que lo hace perfecto para la tienda de F-Droid.

---

## 🎯 Logros Alcanzados (Completados)

- [x] **Motor Secuencial de Cascada**: Capacidad de ejecutar atajos en secuencia pasando de un bloque a otro de forma fluida.
- [x] **Guía Didáctica de Flujo de Bloques**: Panel expandible de aprendizaje interactivo dentro de la creación con paletas de color pastel.
- [x] **Servicios de Accesibilidad Integrados**: Simulación de hardware del dispositivo con un solo toque (Atrás, Inicio).
- [x] **Lanzador de Apps y Esperas**: Bloque de pausa regulable en segundos y lanzamiento instantáneo de aplicaciones externas de terceros a través de intents de Android.
- [x] **Buscador y Selector de Apps del Sistema**: Diálogo interactivo que escanea el `PackageManager` en tiempo real y permite buscar y seleccionar visualmente cualquier aplicación instalada (ej: Chrome, WhatsApp, Mapas).
- [x] **Servicio de Notificaciones con Marca**: Bloque personalizado que lanza notificaciones en la barra de estado de Android con títulos y descripciones configurables.
- [x] **Estructura Organizada de Almacenamiento (Poda 20MB)**: Respaldos automáticos en JSON, caché optimizada y un manejador de crasheos global (`Crash Handler`) con una auto-poda de 20MB máximos para evitar saturación del disco.
- [x] **Panel Secreto de Depuración / Ajustes**: Un menú secreto de administración desbloqueable presionando 5 veces el número de versión de la app, permitiendo visualizar en tiempo real, filtrar, recargar y copiar logs al portapapeles.
- [x] **Gatillos de Botones Físicos (Gestos de Volumen)**: Inicio silencioso y ultrarápido de atajos presionando consecutivamente cinco (5) veces las teclas físicas de subir (+) o bajar (-) volumen utilizando el Servicio de Accesibilidad.
- [x] **Ajustes Rápidos de Android (Quick Settings Tiles)**: Soporte completo integrado de un botón de ajustes rápidos del dispositivo configurado modularmente en la barra superior para activar instantáneamente tus atajos.
- [x] **Lógica Condicional Dinámica (IF/ELSE de Batería)**: Ejecución condicional inteligente que evalúa el estado del sistema en una sola pila de atajos, ejecutando una subacción si se cumple (ENTONCES) o una alternativa si falla (SINO).
- [x] **Manejador de Espera de Mensajería / Notificaciones**: Bloque reactivo súper modular que pausa de forma asíncrona la secuencia de atajos hasta recibir una notificación proveniente de una app específica (ej: WhatsApp/Telegram) y/o con remitentes y textos filtrados.
- [x] **Selector Multivoz y Soporte Multi-Motor Local (TTS)**: Soporte completo en la acción `SPEECH` para detectar dinámicamente voces instaladas localmente en Android. Soporte certificado e ilustrado para motores offline de última generación como *Piper* u *Sherpa-Onnx*.
- [x] **Pantalla Inmersiva de Onboarding**: Flujo guiado de bienvenida para primeros usuarios antes del ingreso a la aplicación. Diapositivas dinámicas para comprender la naturaleza modular de ActionStack, educar sobre accesibilidad, y dar transparencia sobre el desarrollo BETA.
- [x] **Estética Ladrillos Neón**: Rediseño visual del editor de atajos implementando bordes coloreados y brillos neón según la categoría de cada bloque para optimizar la jerarquía de información.

---

## 🚀 Próximas Funcionalidades (Roadmap Técnico)

### Fase 1: Desencadenadores Físicos y Automatización Activa
- **Detección de Cambios de Estado**:
  - Ejecutar atajos de forma automática cuando el cargador se conecta/desconecta (`Intent.ACTION_POWER_CONNECTED` o `ACTION_POWER_DISCONNECTED`).
- **Integración NFC**:
  - Asociar la lectura de una tarjeta física NFC registrada localmente para iniciar una lista o pila de acciones.

### Fase 2: Localización y Ajustes de Acceso Rápido
- **Módulo de Geocercas (GPS)**:
  - Definir coordenadas geográficas para encender o alertar al usuario cuando entra o sale de un radio definido por él (por ejemplo, al llegar a casa o salir de la oficina).
- [x] **Ajustes Rápidos de Android (Quick Settings Tiles)** (Completado):
  - Integrar ActionStack con los paneles del centro de notificaciones, permitiendo al usuario asignar sus atajos preferidos a botones rápidos del sistema para ejecutarlos instantáneamente con un jalón de pantalla.

### Fase 3: Variables Dinámicas e Inteligencia Local
- **Paso de Argumentos (Variables de Entrada/Salida)**:
  - Permitir que las acciones hereden resultados o datos de la acción anterior (por ejemplo, capturar un texto copiado del portapapeles y mandarlo al lector TTS o enviarlo como notificación).
- **Control IoT Local**:
  - Bloques HTTP totalmente locales (Ktor/Retrofit) para controlar dispositivos del hogar inteligente compatibles con integraciones locales offline.
