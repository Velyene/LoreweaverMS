/*
 * FILE: ReferenceScreenCharacterCreation.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character creation content entry point
 * 2. Delegation to extracted state, sections, and card support
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
@Suppress("kotlin:S3776")
internal fun CharacterCreationContent(
	searchQuery: String,
	listState: LazyListState,
	onOpenDetail: (String, String) -> Unit
) {
	var selectedSubsectionName by rememberSaveable { mutableStateOf(CharacterCreationSubsection.ALL.name) }
	val selectedSubsection = remember(selectedSubsectionName) {
		CharacterCreationSubsection.valueOf(selectedSubsectionName)
	}
	val state = rememberCharacterCreationContentState(
		searchQuery = searchQuery,
		selectedSubsection = selectedSubsection
	)

	if (!state.hasResults) {
		ReferenceNoResultsState()
		return
	}

	Column(modifier = Modifier.fillMaxSize()) {
		ReferenceFilterChipRow(
			options = CharacterCreationSubsection.entries,
			selectedOption = state.effectiveSubsection,
			onOptionSelected = { selectedSubsectionName = it.name },
			label = { stringResource(it.labelResId) }
		)

		LazyColumn(
			state = listState,
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.visibleVerticalScrollbar(listState),
			contentPadding = PaddingValues(16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			renderCharacterCreationFoundationItems(state, onOpenDetail)
			renderCharacterCreationGuidanceItems(state)
			renderCharacterCreationEquipmentItems(
				sectionData = CharacterCreationEquipmentSectionData(
					showEquipment = state.effectiveSubsection.showsEquipment(),
					equipmentChapterSections = state.equipmentChapterSections,
					visibleWeapons = state.visibleWeapons,
					visibleArmor = state.visibleArmor,
					visibleGear = state.visibleGear,
					visibleMagicItems = state.visibleMagicItems,
					visibleAmmunition = state.visibleAmmunition,
					visibleFocuses = state.visibleFocuses,
					visibleMounts = state.visibleMounts,
					visibleTackAndDrawn = state.visibleTackAndDrawn,
					visibleLargeVehicles = state.visibleLargeVehicles,
					weaponPropertySections = state.weaponPropertySections,
					weaponMasterySections = state.weaponMasterySections,
					visibleTools = state.visibleTools,
					equipmentTables = state.equipmentTables,
					adventuringGearDetailSections = state.adventuringGearDetailSections,
					onOpenDetail = onOpenDetail
				)
			)
			renderCharacterCreationTailItems(state)
		}
	}
}

