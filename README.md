# ActionStack (Automatización Modular para Android)

¡Bienvenido al proyecto de automatización modular de código abierto definitivo para Android! Esta aplicación está fuertemente inspirada en la famosa aplicación **Shortcuts (Atajos) de iOS**, diseñada y adaptada desde cero para el ecosistema Android con un enfoque local, offline por privacidad, minimalismo visual y desarrollo ultra modular.

---

## 🚀 Concepto Principal
La aplicación permite a los usuarios encadenar acciones secuenciales en forma de **ladrillos de construcción (bloques visuales)**. Al ejecutar una pila (Stack) de acciones, el motor de ActionStack procesa secuencialmente cada instrucción:
- **Alertas Personalizadas**: Notificaciones en la barra de estado del sistema con título y contenido personalizable.
- **Visualización de Apps**: Selección y buscador de aplicaciones instaladas en el sistema mediante selector visual.
- **Navegación Web**: Apertura inmediata de URLs en navegadores preferidos.
- **Relojes de Retardo**: Esperar retardos en segundos programables lógicamente.
- **Sintetizador y Tacto**: Lectura de textos por síntesis de voz (TTS) y vibraciones de retroalimentación.
- **Gestión de Almacenamiento Local**: Estructura limpia en `Android/data/com.example/` con respaldos automáticos de tus atajos y caché temporal para acelerar procesos.
- **Depuración Seguro (20MB Max)**: Un Crash Handler integrado para fallos graves del sistema y logs de advertencias autocontrolados. Límite estricto de espacio de 20MB para preservar el espacio local.
- **Integración de Accesibilidad**: Acciones de hardware del dispositivo como simular volver a Inicio (Home) o Ir Atrás (Back).
- **Control Lógico Bifurcado (IF/ELSE)**: Bloques condicionales que evalúan propiedades del dispositivo (como el nivel de batería actual) y bifurcan el curso del atajo en ramas independientes ("Entonces" / "Sino") en una sola ejecución.
- **Ladrillos Neón de Acción (Diseño de Brillo Dinámico)**: Cada ladrillo o bloque en el editor cuenta con un tema de iluminación neón brillante único según su categoría de utilidad (Voz/TTS, Sistema, Sensores, Alertas, Lógica). Esto permite una rápida identificación visual y un aspecto futurista de alto impacto estético.
- **Pantalla de Bienvenida Inmersiva (Onboarding de Varias Páginas)**: Flujo introductorio dinámico que se muestra la primera vez antes de entrar directamente a la app. Consta de diapositivas interactivas (slides) para guiar al usuario en el concepto modular de ActionStack, explicar su estado de desarrollo y configurar de forma directa y amigable los permisos requeridos (Accesibilidad y Sensores).
- **Filtro de Espera de Mensajería**: Bloques de espera que detienen el flujo del atajo en segundo plano de manera segura hasta recibir una notificación proveniente de una app o contacto de interés (por ejemplo, esperar un mensaje de WhatsApp de un remitente específico).
- **Gatillos de Botones Físicos (Vol+/Vol- x5)**: Desencadenadores invisibles y silenciosos en segundo plano para iniciar atajos rápidamente pulsando de forma consecutiva 5 veces algún botón físico de volumen desde el Servicio de Accesibilidad.
- **Síntesis de Voz Personalizada (Multi-Motor local)**: Soporte completo en la acción de locución (`SPEECH`) para elegir entre múltiples voces locales detectadas en el sistema Android. Compatible de forma nativa offline con motores ultra realistas de alta fidelidad como *Piper* o *Sherpa-Onnx*.

---

## 🎨 Guía de Diseño: "Clean Minimalism"
La interfaz de ActionStack implementa el tema de diseño **Clean Minimalism** con una paleta pastel suave de alto contraste y esquinas redondeadas generosas (32dp para tarjetas, 24dp para diálogos):
- **Lavanda (#F1DBFA)**: Texto contrastante púrpura oscuro (`#2B1237`).
- **Melocotón (#FFDBCB)**: Texto contrastante café oscuro/rojo (`#3B0900`).
- **Verde Menta (#D1E8D1)**: Texto contrastante verde profundo (`#00210E`).
- **Cielo (#D0E4FF)**: Texto contrastante azul marino (`#001D36`).
- **Fondo de la App (#FEF7FF)**: Un lienzo suave y descansado libre de la saturación azul tradicional.

---

## 📂 Recursos y Referencias Adicionales

Para mantener el proyecto limpio y accesible a todos los contribuidores y asistentes de Inteligencia Artificial, hemos documentado cada sección importante en archivos especializados:

- 🏛️ **[Estructura y Arquitectura (STRUCTURE.md)](STRUCTURE.md)**: Conoce el desglose del desarrollo ultra modular del proyecto, capas de datos (Room Database), vistas (Jetpack Compose) y lógica (ViewModels).
- 🗺️ **[Plan de Desarrollo (ROADMAP.md)](ROADMAP.md)**: Descubre las metas logradas y nuestra hoja de ruta detallada para lanzar ActionStack oficialmente en tiendas de software libre como **F-Droid**.
- 🤖 **[Contexto de Inteligencia Artificial (AGENTS.md)](AGENTS.md)**: El manual de reglas esenciales para cualquier modelo de lenguaje o agente IA que venga a dar mantenimiento o agregar bloques, protegiendo las corrutinas, los hilos de ejecución seguros y la privacidad de datos.
