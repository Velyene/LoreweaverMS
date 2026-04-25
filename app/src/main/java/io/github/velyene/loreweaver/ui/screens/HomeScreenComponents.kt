/*
 * FILE: HomeScreenComponents.kt
 *
 * TABLE OF CONTENTS:
 * 1. Action Card (HomeActionCard)
 * 2. Section Header (HomeSectionHeader)
 * 3. Home List Item (HomeItem)
 * 4. Encounter Action Button (EncounterActionButton)
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.theme.AntiqueGold
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.ObsidianBlack
import io.github.velyene.loreweaver.ui.theme.PanelSurface

@Composable
internal fun HomeActionCard(
	title: String,
	icon: ImageVector,
	isPrimary: Boolean,
	modifier: Modifier = Modifier,
	onClick: () -> Unit
) {
	val borderColor = if (isPrimary) ArcaneTeal else AntiqueGold.copy(alpha = 0.5f)
	val iconTint = if (isPrimary) ArcaneTeal else AntiqueGold

	Card(
		modifier = modifier
			.height(100.dp)
			.border(
				width = 1.dp,
				color = borderColor,
				shape = MaterialTheme.shapes.medium
			)
			.clickable(role = Role.Button, onClickLabel = title) { onClick() },
		shape = MaterialTheme.shapes.medium,
		colors = CardDefaults.cardColors(containerColor = PanelSurface),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(12.dp),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				tint = iconTint,
				modifier = Modifier.size(28.dp)
			)
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				text = title,
				style = MaterialTheme.typography.labelMedium,
				fontWeight = FontWeight.SemiBold,
				color = MaterialTheme.colorScheme.onSurface,
				textAlign = TextAlign.Center
			)
		}
	}
}

@Composable
internal fun HomeSectionHeader(title: String) {
	Text(
		text = title.uppercase(),
		style = MaterialTheme.typography.labelMedium,
		color = AntiqueGold,
		fontWeight = FontWeight.Bold,
		letterSpacing = 1.5.sp,
		modifier = Modifier
			.semantics { heading() }
			.fillMaxWidth()
			.padding(bottom = 4.dp)
	)
}

@Composable
internal fun HomeItem(
	title: String,
	icon: ImageVector,
	onClick: () -> Unit = {}
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(role = Role.Button, onClickLabel = title) { onClick() },
		shape = MaterialTheme.shapes.small,
		colors = CardDefaults.cardColors(containerColor = PanelSurface),
		elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
	) {
		Row(
			modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				tint = ArcaneTeal,
				modifier = Modifier.size(20.dp)
			)
			Spacer(modifier = Modifier.width(12.dp))
			Text(
				text = title,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
	}
}

@Suppress("SameParameterValue")
@Composable
internal fun EncounterActionButton(
	hasActiveEncounter: Boolean,
	onResumeEncounter: () -> Unit,
	onCampaigns: () -> Unit
) {
	if (hasActiveEncounter) {
		Button(
			onClick = onResumeEncounter,
			modifier = Modifier
				.fillMaxWidth()
				.height(52.dp),
			colors = ButtonDefaults.buttonColors(containerColor = ArcaneTeal),
			shape = MaterialTheme.shapes.small
		) {
			Icon(
				imageVector = Icons.Default.PlayArrow,
				contentDescription = null,
				tint = ObsidianBlack
			)
			Spacer(modifier = Modifier.width(8.dp))
			Text(
				text = stringResource(R.string.resume_encounter),
				color = ObsidianBlack,
				fontWeight = FontWeight.Bold
			)
		}
	} else {
		OutlinedButton(
			onClick = onCampaigns,
			modifier = Modifier
				.fillMaxWidth()
				.height(52.dp),
			shape = MaterialTheme.shapes.small,
			border = ButtonDefaults.outlinedButtonBorder(true).copy(width = 1.dp)
		) {
			Icon(
				imageVector = Icons.Default.Search,
				contentDescription = null,
				tint = ArcaneTeal,
				modifier = Modifier.size(18.dp)
			)
			Spacer(modifier = Modifier.width(8.dp))
			Text(
				text = stringResource(R.string.browse_campaigns),
				color = MaterialTheme.colorScheme.onBackground
			)
		}
	}
}
