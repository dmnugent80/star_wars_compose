package com.example.starwarscompose.feature.search.composables

import androidx.compose.ui.graphics.Color

enum class AnimationType(val primaryColor: Color, val secondaryColor: Color) {
    PULSE_RED(Color(0xFFE53935), Color(0xFFFF8A65)),      // Sith - yellow eyes
    ROTATE_BLUE(Color(0xFF42A5F5), Color(0xFF90CAF9)),   // Jedi - blue eyes
    SCALE_GREEN(Color(0xFF66BB6A), Color(0xFFA5D6A7)),   // Large characters - height > 200
    ORBIT_PURPLE(Color(0xFFAB47BC), Color(0xFFCE93D8)),  // Droids/aliens - no hair
    FADE_GRAY(Color(0xFF78909C), Color(0xFFB0BEC5))      // Default
}
