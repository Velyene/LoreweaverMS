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

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
	onClickLabel: String = title,
	onClick: () -> Unit = {}
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(role = Role.Button, onClickLabel = onClickLabel) { onClick() },
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

@Composable
internal fun HomeDetailItem(
	title: String,
	subtitle: String,
	icon: ImageVector,
	modifier: Modifier = Modifier,
	onClickLabel: String = title,
	onClick: () -> Unit = {},
) {
	Card(
		modifier = modifier
			.fillMaxWidth()
			.clickable(role = Role.Button, onClickLabel = onClickLabel) { onClick() },
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
			Column {
				Text(
					text = title,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurface,
					fontWeight = FontWeight.Medium,
				)
				Spacer(modifier = Modifier.height(2.dp))
				Text(
					text = subtitle,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
	}
}

@Composable
internal fun HomeFirstRunHintCard(modifier: Modifier = Modifier) {
	Card(
		modifier = modifier.fillMaxWidth(),
		shape = MaterialTheme.shapes.medium,
		colors = CardDefaults.cardColors(containerColor = PanelSurface),
		border = BorderStroke(width = 1.dp, color = AntiqueGold.copy(alpha = 0.4f)),
		elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
	) {
		Row(
			modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Icon(
				imageVector = Icons.Default.AutoStories,
				contentDescription = null,
				tint = AntiqueGold,
				modifier = Modifier.size(20.dp),
			)
			Spacer(modifier = Modifier.width(12.dp))
			Column {
				Text(
					text = stringResource(R.string.home_first_run_title),
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.SemiBold,
					color = MaterialTheme.colorScheme.onSurface,
				)
				Spacer(modifier = Modifier.height(2.dp))
				Text(
					text = stringResource(R.string.home_first_run_message),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
	}
}

@Suppress("SameParameterValue")
@Composable
internal fun EncounterActionButton(
	hasActiveEncounter: Boolean,
	activeEncounterName: String?,
	activeEncounterRound: Int?,
	onResumeEncounter: () -> Unit,
	@Suppress("UNUSED_PARAMETER")
	onCampaigns: () -> Unit = {},
) {
	if (!hasActiveEncounter) {
		return
	}

	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = PanelSurface),
		border = BorderStroke(width = 1.dp, color = ArcaneTeal),
		shape = MaterialTheme.shapes.medium,
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
	) {
		Column(modifier = Modifier.padding(14.dp)) {
			Text(
				text = stringResource(R.string.home_active_encounter_title),
				style = MaterialTheme.typography.labelMedium,
				fontWeight = FontWeight.Bold,
				color = ArcaneTeal
			)
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				text = activeEncounterName?.takeIf(String::isNotBlank)
					?: stringResource(R.string.home_active_encounter_name_fallback),
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.SemiBold,
				color = MaterialTheme.colorScheme.onSurface
			)
			Spacer(modifier = Modifier.height(2.dp))
			Text(
				text = activeEncounterRound?.let {
					stringResource(R.string.home_active_encounter_round_ready, it)
				} ?: stringResource(R.string.home_active_encounter_ready),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Spacer(modifier = Modifier.height(12.dp))
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
		}
	}
}
