package com.example.biztrack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = OnForestGreen,
    primaryContainer = ForestGreenContainer,
    onPrimaryContainer = DarkSlateText,
    secondary = MintGreenSecondary,
    secondaryContainer = MintGreenContainer,
    onSecondaryContainer = DarkSlateText,
    tertiary = MintGreenSecondary,
    tertiaryContainer = MintGreenContainer,
    background = OffWhiteBackground,
    onBackground = DarkSlateText,
    surface = PureWhiteSurface,
    onSurface = DarkSlateText,
    surfaceVariant = CardSurfaceVariant,
    onSurfaceVariant = MediumSlateText,
    error = BizTrackRedError,
    errorContainer = BizTrackErrorContainer
)

@Composable
fun BIZTRACKTheme(
    darkTheme: Boolean = false, // Enforce Light Mode
    dynamicColor: Boolean = false, // Preserve brand palette
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
