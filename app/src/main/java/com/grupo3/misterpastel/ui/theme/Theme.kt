package com.grupo3.misterpastel.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat




// === PALETA DE COLORES CLAROS ===
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFC69C6D),          // Rosa pastel principal
    secondary = Color(0xFFFFB74D),        // Naranja suave
    tertiary = Color(0xFFFFF176),         // Amarillo pastel
    background = Color(0xFFFFF8E1),       // Fondo crema
    surface = Color(0xFFFFFFFF),          // Superficie blanca
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF3E2723),
    onSurface = Color(0xFF3E2723)
)

// === PALETA DE COLORES OSCUROS ===
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFEF9A9A),
    secondary = Color(0xFFFFCC80),
    tertiary = Color(0xFFFFF59D),
    background = Color(0xFF212121),
    surface = Color(0xFF303030),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

// === FUNCIÓN PRINCIPAL DE TEMA ===
@Composable
fun MrPastelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Usa colores dinámicos (Android 12+)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalView.current.context
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
            window.statusBarColor = colorScheme.primary.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


