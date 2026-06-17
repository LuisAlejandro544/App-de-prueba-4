package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.ShortcutViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: ShortcutViewModel
) {
    val context = LocalContext.current
    var currentPage by remember { mutableIntStateOf(0) }
    
    // Hermosa paleta Neon minimalista
    val neonBlue = Color(0xFF00B0FF)
    val neonMagenta = Color(0xFFFF2D55)
    val neonPurple = Color(0xFF9D4EDD)
    val neonGreen = Color(0xFF39FF14)

    val currentThemeBrush = remember(currentPage) {
        when (currentPage) {
            0 -> Brush.linearGradient(listOf(neonPurple, neonMagenta))
            1 -> Brush.linearGradient(listOf(neonBlue, neonPurple))
            2 -> Brush.linearGradient(listOf(neonGreen, neonBlue))
            else -> Brush.linearGradient(listOf(neonMagenta, neonBlue))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Fondo decorativo con brillo sutil superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            when (currentPage) {
                                0 -> neonPurple.copy(alpha = 0.08f)
                                1 -> neonBlue.copy(alpha = 0.08f)
                                2 -> neonGreen.copy(alpha = 0.08f)
                                else -> neonMagenta.copy(alpha = 0.08f)
                            },
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header con versión de la app o aviso de desarrollo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ActionStack v1.2",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    letterSpacing = 1.sp
                )
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = neonMagenta.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, neonMagenta.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "BETA / EN DESARROLLO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = neonMagenta,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Área de contenido animada según la página
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "onboarding_page_transition"
                ) { page ->
                    when (page) {
                        0 -> OnboardingPageOne(currentThemeBrush)
                        1 -> OnboardingPageTwo()
                        2 -> OnboardingPageThree()
                        3 -> OnboardingPageFour()
                    }
                }
            }

            // Pie de página con indicador de puntos y botones de acción
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Indicadores de puntos (Dots)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(4) { idx ->
                        val isSelected = idx == currentPage
                        val widthAnim = if (isSelected) 24.dp else 8.dp
                        val alphaAnim = if (isSelected) 1f else 0.3f
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(widthAnim)
                                .background(
                                    color = if (isSelected) {
                                        when (currentPage) {
                                            0 -> neonPurple
                                            1 -> neonBlue
                                            2 -> neonGreen
                                            else -> neonMagenta
                                        }
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = alphaAnim)
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }

                // Botones principales de navegación
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentPage > 0) {
                        TextButton(
                            onClick = { currentPage-- },
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                "Atrás",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(60.dp))
                    }

                    if (currentPage < 3) {
                        Button(
                            onClick = { currentPage++ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(52.dp)
                                .width(150.dp)
                                .background(
                                    brush = currentThemeBrush,
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Siguiente",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    } else {
                        // Última página: finalizar e iniciar la app
                        Button(
                            onClick = {
                                viewModel.completeOnboarding()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(52.dp)
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                                .background(
                                    brush = Brush.linearGradient(listOf(neonPurple, neonMagenta)),
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "¡Comenzar Experiencia!",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageOne(brush: Brush) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        // Logo de la App inspirado en Atajos de Apple, estilizado con Neón
        Box(
            modifier = Modifier
                .size(110.dp)
                .background(
                    brush = brush,
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF141218)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Capas de Ladrillos en perspectiva neón
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .width(56.dp)
                                .height(16.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF9D4EDD).copy(alpha = 0.9f)
                        ) {}
                        Surface(
                            modifier = Modifier
                                .width(64.dp)
                                .height(16.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF00B0FF).copy(alpha = 0.9f)
                        ) {}
                        Surface(
                            modifier = Modifier
                                .width(52.dp)
                                .height(16.dp),
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFF2D55).copy(alpha = 0.9f)
                        ) {}
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Bienvenido a ActionStack",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Inspirado en los atajos de iOS, ActionStack te permite automatizar tareas locales arrastrando y encadenando bloques lógicos de forma ultra modular.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun OnboardingPageTwo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFF00B0FF).copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = Color(0xFF00B0FF),
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ladrillos Neón de Acción",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Cada acción es un 'Ladrillo' que puedes arrastrar, configurar y ordenar a voluntad. Desde reproducir mensajes en voz alta o alternar la linterna, hasta desplegar notificaciones y evaluar flujos lógicos según tu batería.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF2D55).copy(alpha = 0.5f))
            ) {
                Text(
                    text = "Voz (TTS) 🗣️",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF2D55),
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF39FF14).copy(alpha = 0.5f))
            ) {
                Text(
                    text = "Sensores 🔋",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF39FF14),
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F5FF).copy(alpha = 0.5f))
            ) {
                Text(
                    text = "Espera ⏳",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00F5FF),
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun OnboardingPageThree() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFF39FF14).copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color(0xFF39FF14),
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Gatillos Inteligentes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Ejecuta tus atajos al instante con gatillos innovadores: agitando el dispositivo, usando el sensor de proximidad, agendando por reloj (cronograma) o presionando las teclas físicas de volumen (Volumen ±) de forma rápida.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun OnboardingPageFour() {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFFFF2D55).copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFFF2D55),
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Estado del Desarrollo y FOSS",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Esta app es código de fuente abierto nativa (diseñada para integrarse a la perfección con F-Droid sin trackers ni telemetría). Se encuentra en estadoexperimental de desarrollo activo. ¡Damos la bienvenida a tus ideas!",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Caja de accesibilidad interactiva
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "⚠️ Permiso de Accesibilidad",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Súper útil para simular volver atrás, ir al inicio o activar gatillos de volumen. Se activa de forma segura y totalmente local sin envíos de datos.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error al abrir Ajustes", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Configurar Accesibilidad", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
