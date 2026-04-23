/*
 * FILE: HomeScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Main Screen (HomeScreen)
 * 2. Screen Content (HomeScreenContent)
 * 3. Error Snackbar Handler (ErrorSnackbarHandler)
 * 4. Hero Header (LoreweaverHeroHeader)
 * 5. Action Card (HomeActionCard)
 * 6. Section Header (HomeSectionHeader)
 * 7. Menu Item (HomeItem)
 * 8. Recent Campaigns Section (RecentCampaignsSection)
 * 9. Encounter Action Button (EncounterActionButton)
 */

package com.example.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fort
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loreweaver.R
import com.example.loreweaver.domain.model.Campaign
import com.example.loreweaver.ui.theme.AntiqueGold
import com.example.loreweaver.ui.theme.ArcaneTeal
import com.example.loreweaver.ui.theme.DeepSurface
import com.example.loreweaver.ui.theme.MutedText
import com.example.loreweaver.ui.theme.ObsidianBlack
import com.example.loreweaver.ui.theme.PanelSurface
import com.example.loreweaver.ui.viewmodels.CampaignUiState
import com.example.loreweaver.ui.viewmodels.CampaignViewModel

// -----------------------------------------------------------------------------
// 1. Main Screen
// -----------------------------------------------------------------------------

@Composable
fun HomeScreen(
	onNewEncounter: () -> Unit,
	onCampaigns: () -> Unit,
	onResumeEncounter: () -> Unit,
	onCampaignClick: (String) -> Unit,
	onRulesReference: () -> Unit
) {
	val viewModel: CampaignViewModel = hiltViewModel()
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()
	val snackbarHostState = remember { SnackbarHostState() }
	val retryActionLabel = stringResource(R.string.retry_action)

	ErrorSnackbarHandler(
		error = uiState.error,
		onRetry = uiState.onRetry,
		snackbarHostState = snackbarHostState,
		onClear = viewModel::clearError,
		retryActionLabel = retryActionLabel
	)

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) },
		containerColor = MaterialTheme.colorScheme.background
	) { padding ->
		HomeScreenContent(
			uiState = uiState,
			padding = padding,
			onNewEncounter = onNewEncounter,
			onResumeEncounter = onResumeEncounter,
			onCampaigns = onCampaigns,
			onCampaignClick = onCampaignClick,
			onRulesReference = onRulesReference
		)
	}
}

// -----------------------------------------------------------------------------
// 2. Screen Content
// -----------------------------------------------------------------------------

@Composable
private fun HomeScreenContent(
	uiState: CampaignUiState,
	padding: PaddingValues,
	onNewEncounter: () -> Unit,
	onResumeEncounter: () -> Unit,
	onCampaigns: () -> Unit,
	onCampaignClick: (String) -> Unit,
	onRulesReference: () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
			.verticalScroll(rememberScrollState()),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		if (uiState.isLoading) {
			LinearProgressIndicator(
				modifier = Modifier.fillMaxWidth(),
				color = ArcaneTeal,
				trackColor = PanelSurface
			)
		}

		// Hero header
		LoreweaverHeroHeader()

		// Quick action cards
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			HomeSectionHeader(stringResource(R.string.quick_actions))

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp)
			) {
				HomeActionCard(
					title = stringResource(R.string.start_new_session),
					icon = Icons.Default.Add,
					isPrimary = true,
					modifier = Modifier.weight(1f),
					onClick = onNewEncounter
				)
				HomeActionCard(
					title = stringResource(R.string.browse_campaigns),
					icon = Icons.Default.Fort,
					isPrimary = false,
					modifier = Modifier.weight(1f),
					onClick = onCampaigns
				)
			}

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp)
			) {
				HomeActionCard(
					title = stringResource(R.string.home_rules_reference),
					icon = Icons.Default.Info,
					isPrimary = false,
					modifier = Modifier.weight(1f),
					onClick = onRulesReference
				)
			}

			EncounterActionButton(
				hasActiveEncounter = false,
				onResumeEncounter = onResumeEncounter,
				onCampaigns = onCampaigns
			)
		}

		Spacer(Modifier.height(24.dp))
		HorizontalDivider(
			modifier = Modifier.padding(horizontal = 16.dp),
			color = AntiqueGold.copy(alpha = 0.25f)
		)
		Spacer(Modifier.height(16.dp))

		// Recent campaigns
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			RecentCampaignsSection(
				campaigns = uiState.campaigns,
				onCampaignClick = onCampaignClick
			)
		}

		Spacer(modifier = Modifier.height(24.dp))

		Text(
			stringResource(R.string.home_footer),
			color = MutedText,
			style = MaterialTheme.typography.bodySmall,
			modifier = Modifier.padding(bottom = 32.dp)
		)
	}
}

// -----------------------------------------------------------------------------
// 3. Error Snackbar Handler
// -----------------------------------------------------------------------------

@Composable
private fun ErrorSnackbarHandler(
	error: String?,
	onRetry: (() -> Unit)?,
	snackbarHostState: SnackbarHostState,
	onClear: () -> Unit,
	retryActionLabel: String
) {
	LaunchedEffect(error) {
		error ?: return@LaunchedEffect
		val result = snackbarHostState.showSnackbar(
			message = error,
			actionLabel = if (onRetry != null) retryActionLabel else null,
			duration = SnackbarDuration.Long
		)
		if (result == SnackbarResult.ActionPerformed) onRetry?.invoke()
		onClear()
	}
}

// -----------------------------------------------------------------------------
// 4. Hero Header
// -----------------------------------------------------------------------------

@Composable
private fun LoreweaverHeroHeader() {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				Brush.verticalGradient(
					colors = listOf(
						DeepSurface,
						ObsidianBlack
					)
				)
			)
			.padding(top = 40.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
		contentAlignment = Alignment.Center
	) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			// Gem accent
			Icon(
				imageVector = Icons.Default.Shield,
				contentDescription = null,
				tint = ArcaneTeal,
				modifier = Modifier.size(40.dp)
			)
			Spacer(Modifier.height(12.dp))
			// App title
			Text(
				text = stringResource(R.string.home_brand_title),
				style = MaterialTheme.typography.headlineLarge.copy(
					fontWeight = FontWeight.Bold,
					letterSpacing = 4.sp
				),
				color = AntiqueGold
			)
			Spacer(Modifier.height(4.dp))
			// Gold divider line
			Box(
				modifier = Modifier
					.width(80.dp)
					.height(2.dp)
					.background(
						Brush.horizontalGradient(
							listOf(Color.Transparent, AntiqueGold, Color.Transparent)
						)
					)
			)
			Spacer(Modifier.height(8.dp))
			Text(
				text = stringResource(R.string.home_brand_subtitle),
				style = MaterialTheme.typography.bodyMedium,
				color = MutedText,
				textAlign = TextAlign.Center
			)
		}
	}
}

// -----------------------------------------------------------------------------
// 5. Action Card
// -----------------------------------------------------------------------------

@Composable
private fun HomeActionCard(
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
			Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
			Spacer(Modifier.height(8.dp))
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

// -----------------------------------------------------------------------------
// 6. Section Header
// -----------------------------------------------------------------------------

@Composable
fun HomeSectionHeader(title: String) {
	Text(
		title.uppercase(),
		style = MaterialTheme.typography.labelMedium,
		color = AntiqueGold,
		fontWeight = FontWeight.Bold,
		letterSpacing = 1.5.sp,
		modifier = Modifier
			.fillMaxWidth()
			.padding(bottom = 4.dp)
	)
}

// -----------------------------------------------------------------------------
// 7. Menu Item
// -----------------------------------------------------------------------------

@Composable
fun HomeItem(
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
				icon,
				null,
				tint = ArcaneTeal,
				modifier = Modifier.size(20.dp)
			)
			Spacer(Modifier.width(12.dp))
			Text(
				title,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurface
			)
		}
	}
}

// -----------------------------------------------------------------------------
// 8. Recent Campaigns Section
// -----------------------------------------------------------------------------

@Composable
private fun RecentCampaignsSection(
	campaigns: List<Campaign>,
	onCampaignClick: (String) -> Unit
) {
	HomeSectionHeader(stringResource(R.string.recent_campaigns))
	if (campaigns.isEmpty()) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.clip(MaterialTheme.shapes.medium)
				.background(PanelSurface)
				.padding(20.dp),
			contentAlignment = Alignment.Center
		) {
			Text(
				stringResource(R.string.no_campaigns),
				style = MaterialTheme.typography.bodyMedium,
				color = MutedText,
				textAlign = TextAlign.Center
			)
		}
	} else {
		Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
			campaigns.take(3).forEach { campaign ->
				HomeItem(
					title = campaign.title,
					icon = Icons.Default.Fort,
					onClick = { onCampaignClick(campaign.id) }
				)
			}
		}
	}
}

// -----------------------------------------------------------------------------
// 9. Encounter Action Button
// -----------------------------------------------------------------------------

@Suppress("SameParameterValue")
@Composable
private fun EncounterActionButton(
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
			Icon(Icons.Default.PlayArrow, null, tint = ObsidianBlack)
			Spacer(Modifier.width(8.dp))
			Text(
				stringResource(R.string.resume_encounter),
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
			Icon(Icons.Default.Search, null, tint = ArcaneTeal, modifier = Modifier.size(18.dp))
			Spacer(Modifier.width(8.dp))
			Text(
				stringResource(R.string.browse_campaigns),
				color = MaterialTheme.colorScheme.onBackground
			)
		}
	}
}
