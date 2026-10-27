package com.downloader.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.downloader.app.data.AppTheme

@Composable
fun DownbeatTheme(
    theme: AppTheme = AppTheme.GRAPHITE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme(theme, darkTheme),
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
