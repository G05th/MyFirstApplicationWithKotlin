package com.example.myfirstapplication.ui.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Surface

@Composable
fun MyLoginTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = CustomPrimary,
        onPrimary = CustomOnPrimary,
        background = CustomBackground,
        onBackground = CustomOnBackground,
        primaryContainer = CustomPrimaryContainer
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}