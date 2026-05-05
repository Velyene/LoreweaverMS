/*
 * FILE: HomeScreenContent.kt
 *
 * TABLE OF CONTENTS:
 * 1. Home Screen Content (HomeScreenContent)
 * 2. Quick Action Row (QuickActionRow)
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fort
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.theme.AntiqueGold
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.MutedText
import io.github.velyene.loreweaver.ui.theme.PanelSurface
import io.github.velyene.loreweaver.ui.viewmodels.CampaignListUiState
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
internal fun HomeScreenContent(
	uiState: CampaignListUiState,
	padding: PaddingValues,
	onNewEncounter: () -> Unit,
	onResumeEncounter: () -> Unit,
	onLatestSessionClick: (String) -> Unit,
	onCampaigns: () -> Unit,
	onCampaignClick: (String) -> Unit,
	onRulesReference: () -> Unit
) {
	val scrollState = rememberScrollState()
	val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }
	val showFirstRunHint = !uiState.hasActiveEncounter &&
		uiState.latestCompletedSession == null &&
		uiState.campaigns.isEmpty()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(padding)
			.verticalScroll(scrollState)
			.visibleVerticalScrollbar(scrollState),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		if (uiState.isLoading) {
			LinearProgressIndicator(
				modifier = Modifier.fillMaxWidth(),
				color = ArcaneTeal,
				trackColor = PanelSurface
			)
		}

		LoreweaverHeroHeader()

		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			// Quick actions intentionally group the most common home flows so they remain
			// discoverable even when the recent-campaign list is empty.
			HomeSectionHeader(stringResource(R.string.quick_actions))

			QuickActionRow(
				primaryTitle = stringResource(R.string.open_session_hub),
				primaryIcon = Icons.Default.Add,
				onPrimaryClick = onNewEncounter,
				secondaryTitle = stringResource(R.string.browse_campaigns),
				secondaryIcon = Icons.Default.Fort,
				onSecondaryClick = onCampaigns,
				secondaryIsPrimary = false
			)

			QuickActionRow(
				primaryTitle = stringResource(R.string.home_rules_reference),
				primaryIcon = Icons.Default.Info,
				onPrimaryClick = onRulesReference,
				primaryIsPrimary = false
			)

			EncounterActionButton(
				uiState.hasActiveEncounter,
				uiState.activeEncounterName,
				uiState.activeEncounterRound,
				onResumeEncounter,
				onCampaigns,
			)

			if (showFirstRunHint) {
				HomeFirstRunHintCard()
			}
		}

		Spacer(modifier = Modifier.height(24.dp))
		HorizontalDivider(
			modifier = Modifier.padding(horizontal = 16.dp),
			color = AntiqueGold.copy(alpha = 0.25f)
		)
		Spacer(modifier = Modifier.height(16.dp))

		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			LatestCompletedSessionSection(
				session = uiState.latestCompletedSession,
				dateFormat = dateFormat,
				onSessionClick = onLatestSessionClick,
			)

			RecentCampaignsSection(
				campaigns = uiState.campaigns,
				onCampaignClick = onCampaignClick
			)
		}

		Spacer(modifier = Modifier.height(24.dp))

		Text(
			text = stringResource(R.string.home_footer),
			color = MutedText,
			style = MaterialTheme.typography.bodySmall,
			modifier = Modifier.padding(bottom = 32.dp)
		)
	}
}

@Composable
private fun QuickActionRow(
	primaryTitle: String,
	primaryIcon: ImageVector,
	onPrimaryClick: () -> Unit,
	secondaryTitle: String? = null,
	secondaryIcon: ImageVector? = null,
	onSecondaryClick: (() -> Unit)? = null,
	primaryIsPrimary: Boolean = true,
	secondaryIsPrimary: Boolean = false
) {
	// The optional secondary slot keeps the home layout consistent across one- and
	// two-action rows without forcing the caller to build separate row containers.
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(12.dp)
	) {
		HomeActionCard(
			title = primaryTitle,
			icon = primaryIcon,
			isPrimary = primaryIsPrimary,
			modifier = Modifier.weight(1f),
			onClick = onPrimaryClick
		)
		if (secondaryTitle != null && secondaryIcon != null && onSecondaryClick != null) {
			HomeActionCard(
				title = secondaryTitle,
				icon = secondaryIcon,
				isPrimary = secondaryIsPrimary,
				modifier = Modifier.weight(1f),
				onClick = onSecondaryClick
			)
		}
	}
}
