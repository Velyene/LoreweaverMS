package io.github.velyene.loreweaver.ui.screens.tracker.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun TrackerModeBadge(
	label: String,
	containerColor: Color,
	contentColor: Color
) {
	Badge(containerColor = containerColor) {
		Text(
			label,
			color = contentColor,
			modifier = Modifier.padding(4.dp)
		)
	}
}

