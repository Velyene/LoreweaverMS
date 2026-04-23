package com.example.loreweaver.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val LoreweaverDarkColorScheme = darkColorScheme(
	// Primary – Arcane Teal
	primary = ArcaneTeal,
	onPrimary = ObsidianBlack,
	primaryContainer = MutedAqua.copy(alpha = 0.25f),
	onPrimaryContainer = SoftCyanGlow,

	// Secondary – Gold
	secondary = AntiqueGold,
	onSecondary = ObsidianBlack,
	secondaryContainer = SoftBrass.copy(alpha = 0.25f),
	onSecondaryContainer = WarmGold,

	// Tertiary – Soft Cyan glow (magic emphasis)
	tertiary = SoftCyanGlow,
	onTertiary = ObsidianBlack,
	tertiaryContainer = ArcaneTeal.copy(alpha = 0.15f),
	onTertiaryContainer = SoftCyanGlow,

	// Backgrounds / Surfaces
	background = ObsidianBlack,
	onBackground = PrimaryText,
	surface = DeepSurface,
	onSurface = PrimaryText,
	surfaceVariant = PanelSurface,
	onSurfaceVariant = SecondaryText,
	surfaceContainerLow = DeepSurface,
	surfaceContainer = PanelSurface,
	surfaceContainerHigh = ElevatedSurface,

	// Error / Danger
	error = DangerRed,
	onError = PrimaryText,
	errorContainer = DangerRed.copy(alpha = 0.20f),
	onErrorContainer = Color(0xFFFFB4AB),

	outline = MutedText.copy(alpha = 0.4f),
	outlineVariant = MutedText.copy(alpha = 0.2f),
	scrim = ObsidianBlack.copy(alpha = 0.8f),
)

// Light scheme is never used (app is force-dark) but must exist to compile
private val LightColorScheme = lightColorScheme(
	primary = ArcaneTeal,
	secondary = AntiqueGold,
	tertiary = SoftCyanGlow,
)

val LoreweaverShapes = Shapes(
	extraSmall = RoundedCornerShape(4.dp),
	small = RoundedCornerShape(8.dp),
	medium = RoundedCornerShape(12.dp),
	large = RoundedCornerShape(16.dp),
	extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun LoreweaverTheme(
	darkTheme: Boolean = true,          // force dark per AGENTS.md
	dynamicColor: Boolean = false,      // disabled – preserve fantasy palette
	content: @Composable () -> Unit
) {
	val colorScheme = when {
		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			val context = LocalContext.current
			if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
		}

		darkTheme -> LoreweaverDarkColorScheme
		else -> LightColorScheme
	}

	MaterialTheme(
		colorScheme = colorScheme,
		typography = Typography,
		shapes = LoreweaverShapes,
	) {
		val view = LocalView.current
		if (!view.isInEditMode) {
			SideEffect {
				val window = (view.context as Activity).window
				@Suppress("DEPRECATION")
				window.statusBarColor = ObsidianBlack.toArgb()
				WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
			}
		}
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = colorScheme.background,
			content = content
		)
	}
}
