package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AutomationIdea
import com.example.ui.screens.components.AutomationIdeaItem

@Composable
fun AutomationIdeasScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp)
    ) {
        Text(
            text = "Ideas de Integración Opensource y más",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Usa estas ideas para publicar en F-Droid o crear automatizaciones reales y locales:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        val ideas = AutomationIdea.ideas

        ideas.forEach { idea ->
            AutomationIdeaItem(idea = idea)
        }
    }
}

