/*
 * FILE: CampaignDetailContent.kt
 *
 * TABLE OF CONTENTS:
 * 1. Content State Models (CampaignDetailContentState, CampaignDetailActions)
 * 2. Campaign Detail Content (CampaignDetailContent)
 * 3. Empty and Badge States (CampaignNotFoundState, CampaignBadgeHeader)
 * 4. Tab Navigation (CampaignDetailTabs, CampaignDetailTabContent)
 * 5. Shared Section Header (SectionHeader)
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.model.RemoteItem
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.ui.theme.AntiqueGold

internal data class CampaignDetailContentState(
	val campaign: Campaign?,
	val selectedTab: Int,
	val isLoading: Boolean,
	val notes: List<Note>,
	val sessions: List<SessionRecord>,
	val encounters: List<Encounter>
)

internal data class CampaignDetailActions(
	val onEncounterClick: (String) -> Unit,
	val onAddNote: (String, String, String) -> Unit,
	val onDeleteNote: (Note) -> Unit,
	val onUpdateNote: (Note) -> Unit,
	val onAddEncounter: (String) -> Unit,
	val onAddEncounterWithMonsters: (String, List<RemoteItem>) -> Unit
)

@Composable
internal fun CampaignDetailContent(
	state: CampaignDetailContentState,
	actions: CampaignDetailActions,
	onSelectedTabChange: (Int) -> Unit,
	modifier: Modifier = Modifier
) {
	Column(modifier = modifier) {
		if (state.isLoading) {
			LinearProgressIndicator(
				modifier = Modifier.fillMaxWidth(),
				color = MaterialTheme.colorScheme.secondary,
				trackColor = MaterialTheme.colorScheme.surfaceVariant
			)
		}

		// The early return keeps the tab content logic focused on the fully loaded case
		// while still showing an explicit empty state when the campaign no longer exists.
		if (state.campaign == null) {
			if (state.isLoading) {
				CenteredLoadingState()
			} else {
				CampaignNotFoundState()
			}
			return@Column
		}

		CampaignBadgeHeader()
		CampaignDetailTabs(
			selectedTab = state.selectedTab,
			onSelectedTabChange = onSelectedTabChange
		)
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
		) {
			CampaignDetailTabContent(
				state = state,
				actions = actions
			)
		}
	}
}

@Composable
private fun CampaignNotFoundState() {
	CenteredEmptyState(message = stringResource(R.string.campaign_not_found_message))
}

@Composable
private fun CampaignBadgeHeader() {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp),
		contentAlignment = Alignment.Center
	) {
		Badge(containerColor = MaterialTheme.colorScheme.secondary) {
			Text(
				text = stringResource(R.string.campaign_badge_label),
				color = MaterialTheme.colorScheme.onSecondary,
				modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
				fontWeight = FontWeight.Bold
			)
		}
	}
}

@Composable
private fun CampaignDetailTabs(
	selectedTab: Int,
	onSelectedTabChange: (Int) -> Unit
) {
	val campaignDetailTabs = listOf(
		stringResource(R.string.campaign_tab_lore_notes),
		stringResource(R.string.campaign_tab_sessions),
		stringResource(R.string.campaign_tab_encounters)
	)

	@Suppress("DEPRECATION")
	TabRow(
		selectedTabIndex = selectedTab,
		containerColor = MaterialTheme.colorScheme.background,
		contentColor = MaterialTheme.colorScheme.secondary,
		indicator = { tabPositions ->
			@Suppress("DEPRECATION")
			TabRowDefaults.SecondaryIndicator(
				modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
				color = MaterialTheme.colorScheme.secondary
			)
		}
	) {
		campaignDetailTabs.forEachIndexed { index, title ->
			Tab(
				selected = selectedTab == index,
				onClick = { onSelectedTabChange(index) },
				text = { Text(title) }
			)
		}
	}
}

@Composable
private fun CampaignDetailTabContent(
	state: CampaignDetailContentState,
	actions: CampaignDetailActions
) {
	// Tab content stays centralized so the route layer only coordinates data loading
	// and navigation while this file owns the campaign-detail presentation split.
	when (state.selectedTab) {
		0 -> LoreAndNotesSection(
			notes = state.notes,
			onAddNote = actions.onAddNote,
			onDeleteNote = actions.onDeleteNote,
			onUpdateNote = actions.onUpdateNote
		)

		1 -> SessionHistoryList(state.sessions)
		2 -> LinkedEncounterList(
			encounters = state.encounters,
			onEncounterClick = actions.onEncounterClick,
			onAddEncounter = actions.onAddEncounter,
			onAddEncounterWithMonsters = actions.onAddEncounterWithMonsters
		)
	}
}

@Composable
internal fun SectionHeader(title: String) {
	Text(
		text = title.uppercase(),
		color = AntiqueGold,
		style = MaterialTheme.typography.labelMedium,
		fontWeight = FontWeight.Bold,
		letterSpacing = 1.5.sp,
		modifier = Modifier
			.semantics { heading() }
			.padding(bottom = 8.dp)
	)
}
