/*
 * FILE: ReferenceScreenSpellcasting.kt
 *
 * TABLE OF CONTENTS:
 * 1. Spellcasting content entry point and derived state
 * 2. Overview, rules, glossary, and progression sections
 * 3. Spellcasting calculators and presentation helpers
 * 4. Search, filtering, and share-text helpers
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.domain.util.SpellcasterType
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.ReferenceUiState

@Composable
internal fun SpellcastingContent(
	uiState: ReferenceUiState,
	listState: LazyListState,
	onSpellcasterTypeChange: (SpellcasterType) -> Unit,
	onAbilityModifierChange: (String) -> Unit,
	onProficiencyBonusChange: (String) -> Unit,
	onSpellBonusChange: (String) -> Unit,
	onCasterLevelChange: (String) -> Unit,
	onOpenSpellDetail: (String) -> Unit
) {
	val context = LocalContext.current
	val shareChooserTitle = stringResource(R.string.reference_share_chooser_title)
	val spellcastingState = rememberSpellcastingContentState(uiState)

	if (!spellcastingState.hasResults) {
		ReferenceNoResultsState()
		return
	}

	LazyColumn(
		state = listState,
		modifier = Modifier
			.fillMaxSize()
			.visibleVerticalScrollbar(listState),
		contentPadding = PaddingValues(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		item {
			SpellcastingOverviewSection(
				uiState = uiState,
				state = spellcastingState,
				shareChooserTitle = shareChooserTitle,
				context = context,
				onSpellcasterTypeChange = onSpellcasterTypeChange,
				onAbilityModifierChange = onAbilityModifierChange,
				onProficiencyBonusChange = onProficiencyBonusChange,
				onSpellBonusChange = onSpellBonusChange,
				onCasterLevelChange = onCasterLevelChange
			)
		}

		item { SpellcastingRulesSection(state = spellcastingState) }
		item { SpellcastingGlossarySection(state = spellcastingState) }
		item {
			SpellcastingProgressionSection(
				state = spellcastingState,
				onOpenSpellDetail = onOpenSpellDetail
			)
		}
	}
}

