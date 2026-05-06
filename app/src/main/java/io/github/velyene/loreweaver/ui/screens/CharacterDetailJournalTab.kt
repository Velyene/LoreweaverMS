/*
 * FILE: CharacterDetailJournalTab.kt
 *
 * TABLE OF CONTENTS:
 * 1. Status Overview Models
 * 2. Character Status Overview UI
 * 3. Persistent Status Manager Components
 * 4. Journal and Inventory Sections
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.InventoryItem
import io.github.velyene.loreweaver.domain.util.formatCurrencyCp
import io.github.velyene.loreweaver.ui.viewmodels.CharacterViewModel

private data class CharacterStatusSections(
	val encounterConditions: List<StatusChipModel>,
	val ongoingAfflictions: List<StatusChipModel>,
	val persistentEffects: List<StatusChipModel>
)

@Composable
private fun CharacterStatusOverview(
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	onLookupCondition: (String) -> Unit
) {
	var showPersistentStatusPicker by remember { mutableStateOf(false) }
	val statusSections = remember(character.activeConditions) {
		classifyCharacterStatusSections(character.activeConditions)
	}
	val currentPersistentEffects = statusSections.persistentEffects

	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		Text(
			text = stringResource(R.string.character_status_overview_title),
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.Bold
		)
		CharacterStatusSectionCard(
			title = stringResource(R.string.character_status_encounter_conditions_title),
			emptyText = stringResource(R.string.character_status_encounter_conditions_empty),
			statuses = statusSections.encounterConditions,
			onLookupCondition = onLookupCondition,
			onRemoveCondition = { conditionName ->
				viewModel.updateCharacter(character.withRemovedActiveCondition(conditionName))
			}
		)
		CharacterStatusSectionCard(
			title = stringResource(R.string.character_status_ongoing_afflictions_title),
			emptyText = stringResource(R.string.character_status_ongoing_afflictions_empty),
			statuses = statusSections.ongoingAfflictions,
			onLookupCondition = onLookupCondition,
			onRemoveCondition = { conditionName ->
				viewModel.updateCharacter(character.withRemovedActiveCondition(conditionName))
			}
		)
		CharacterStatusSectionCard(
			title = stringResource(R.string.character_status_persistent_effects_title),
			emptyText = stringResource(R.string.character_status_persistent_effects_empty),
			statuses = statusSections.persistentEffects,
			onLookupCondition = onLookupCondition,
			actionContent = {
				TextButton(onClick = { showPersistentStatusPicker = true }) {
					Text(stringResource(R.string.character_status_manage_persistent_button))
				}
			},
			onRemoveCondition = { conditionName ->
				viewModel.updateCharacter(character.withRemovedActiveCondition(conditionName))
			}
		)
	}

	if (showPersistentStatusPicker) {
		AddConditionDialog(
			config = AddConditionDialogConfig(
				titleOverride = stringResource(R.string.character_status_manage_persistent_title),
				supportingTextOverride = stringResource(R.string.character_status_manage_persistent_supporting_text),
				availableConditions = ConditionConstants.persistentConditions().toSet(),
				initialHasDuration = false,
				initialPersistsAcrossEncounters = true,
				showDurationControls = false,
				showPersistenceToggle = false,
				sheetTag = PERSISTENT_STATUS_PICKER_SHEET_TAG,
			),
			summaryContent = {
				PersistentStatusManagerSummary(
					statuses = currentPersistentEffects,
					onRemoveCondition = { conditionName ->
						viewModel.updateCharacter(character.withRemovedActiveCondition(conditionName))
					}
				)
			},
			onDismiss = {
				@Suppress("UNUSED_VALUE")
				showPersistentStatusPicker = false
			},
			onConfirm = { statusName, _, _ ->
				@Suppress("UNUSED_VALUE")
				showPersistentStatusPicker = false
				viewModel.updateCharacter(character.withAddedPersistentCondition(statusName))
			}
		)
	}
}

@Composable
private fun CharacterStatusSectionCard(
	title: String,
	emptyText: String,
	statuses: List<StatusChipModel>,
	onLookupCondition: (String) -> Unit,
	actionContent: @Composable (() -> Unit)? = null,
	onRemoveCondition: (String) -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = title,
				style = MaterialTheme.typography.labelLarge,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onSurface
			)
			if (statuses.isEmpty()) {
				Text(
					text = emptyText,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
				actionContent?.invoke()
			} else {
				StatusChipFlowRow(
					statuses = statuses,
					onStatusClick = { status -> onLookupCondition(status.name) },
					onStatusRemove = { status -> onRemoveCondition(status.name) },
					trailingContent = actionContent
				)
			}
		}
	}
}

internal const val PERSISTENT_STATUS_PICKER_SHEET_TAG = "persistent_status_picker_sheet"
internal const val PERSISTENT_STATUS_MANAGER_CURRENT_EFFECTS_TAG = "persistent_status_manager_current_effects"

internal fun persistentStatusManagerRemoveButtonTag(label: String): String =
	"persistent_status_manager_remove_${canonicalStatusLabel(label).lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')}"

@Composable
private fun PersistentStatusManagerSummary(
	statuses: List<StatusChipModel>,
	onRemoveCondition: (String) -> Unit,
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.testTag(PERSISTENT_STATUS_MANAGER_CURRENT_EFFECTS_TAG),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = stringResource(R.string.character_status_current_persistent_effects_title),
				style = MaterialTheme.typography.labelLarge,
				fontWeight = FontWeight.Bold,
			)
			if (statuses.isEmpty()) {
				Text(
					text = stringResource(R.string.character_status_persistent_effects_empty),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			} else {
				statuses.forEach { status ->
					PersistentStatusManagerRow(
						status = status,
						onRemoveCondition = onRemoveCondition,
					)
				}
			}
		}
	}
}

@Composable
private fun PersistentStatusManagerRow(
	status: StatusChipModel,
	onRemoveCondition: (String) -> Unit,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Text(
			text = statusChipAnnouncement(
				status = status,
				persistentSuffix = stringResource(R.string.condition_persistent_chip_suffix),
				referenceAvailableDescription = stringResource(R.string.status_chip_reference_available),
				statusOnlyDescription = stringResource(R.string.status_chip_status_only),
			).substringBeforeLast(", "),
			modifier = Modifier.weight(1f),
			style = MaterialTheme.typography.bodyMedium,
		)
		PersistentStatusManagerRemoveButton(status.name) {
			onRemoveCondition(status.name)
		}
	}
}

@Composable
private fun PersistentStatusManagerRemoveButton(
	conditionName: String,
	onClick: () -> Unit,
) {
	TextButton(
		onClick = onClick,
		modifier = Modifier.testTag(persistentStatusManagerRemoveButtonTag(conditionName)),
	) {
		Text(stringResource(R.string.remove_button))
	}
}

private fun CharacterEntry.withAddedPersistentCondition(conditionName: String): CharacterEntry {
	return copy(
		activeConditions = normalizedStatusLabels(activeConditions + canonicalStatusLabel(conditionName)).toSet()
	)
}

private fun CharacterEntry.withRemovedActiveCondition(conditionName: String): CharacterEntry {
	val canonicalCondition = canonicalStatusLabel(conditionName)
	return copy(
		activeConditions = normalizedStatusLabels(
			activeConditions.filterNot { existingCondition ->
				canonicalStatusLabel(existingCondition).equals(canonicalCondition, ignoreCase = true)
			}
		).toSet()
	)
}

private fun classifyCharacterStatusSections(activeConditions: Set<String>): CharacterStatusSections {
	val afflictionCategories = setOf(
		ConditionConstants.StatusCategory.DAMAGE_OVER_TIME,
		ConditionConstants.StatusCategory.SENSORY,
		ConditionConstants.StatusCategory.CONTROL,
		ConditionConstants.StatusCategory.MOBILITY,
		ConditionConstants.StatusCategory.DEBUFF
	)
	val encounterConditions = mutableListOf<StatusChipModel>()
	val ongoingAfflictions = mutableListOf<StatusChipModel>()
	val persistentEffects = mutableListOf<StatusChipModel>()

	normalizedStatusLabels(activeConditions).forEach { conditionName ->
		val metadata = ConditionConstants.metadataFor(conditionName)
		val chip = statusChipModel(
			name = conditionName,
			isPersistent = metadata.persistsAcrossEncounters
		)
		when {
			metadata.persistsAcrossEncounters -> persistentEffects += chip
			metadata.category in afflictionCategories -> ongoingAfflictions += chip
			else -> encounterConditions += chip
		}
	}

	return CharacterStatusSections(
		encounterConditions = encounterConditions,
		ongoingAfflictions = ongoingAfflictions,
		persistentEffects = persistentEffects
	)
}

@Composable
fun JournalTab(
	character: CharacterEntry,
	viewModel: CharacterViewModel,
	onLookupCondition: (String) -> Unit
) {
	val personalInventory = character.personalInventoryItems()
	val equippedItems = character.inventoryState.equippedItems
	val consumables = personalInventory.filter { it.consumable }
	val specialItems = personalInventory.filter { it.specialItem }
	val generalItems = personalInventory.filterNot { it.consumable || it.specialItem }

	Text(stringResource(R.string.inventory_label), style = MaterialTheme.typography.titleMedium)
	InventorySummaryCard(
		title = stringResource(R.string.character_wallet_label),
		items = listOfNotNull(character.inventoryState.currencyCp.takeIf { it > 0 }?.let(::formatCurrencyCp)),
		emptyText = stringResource(R.string.character_wallet_empty_label)
	)
	InventorySummaryCard(
		title = stringResource(R.string.equipped_items_label),
		items = equippedItems.map(InventoryItem::toDisplayLine),
		emptyText = stringResource(R.string.empty_label)
	)
	InventorySummaryCard(
		title = stringResource(R.string.character_personal_inventory_label),
		items = generalItems.map(InventoryItem::toDisplayLine),
		emptyText = stringResource(R.string.empty_label)
	)
	InventorySummaryCard(
		title = stringResource(R.string.character_consumables_label),
		items = consumables.map(InventoryItem::toDisplayLine),
		emptyText = stringResource(R.string.empty_label)
	)
	InventorySummaryCard(
		title = stringResource(R.string.character_special_items_label),
		items = specialItems.map(InventoryItem::toDisplayLine),
		emptyText = stringResource(R.string.empty_label)
	)
	InventorySummaryCard(
		title = stringResource(R.string.carrying_notes_label),
		items = listOfNotNull(character.inventoryState.carryingNotes.takeIf(String::isNotBlank)),
		emptyText = stringResource(R.string.no_notes_label)
	)

	Spacer(modifier = Modifier.height(16.dp))
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			if (personalInventory.isEmpty() && equippedItems.isEmpty()) {
				Text(stringResource(R.string.empty_label))
			} else {
				personalInventory.forEach { item -> Text(stringResource(R.string.inventory_bullet, item.toDisplayLine())) }
				equippedItems.forEach { item -> Text(stringResource(R.string.inventory_bullet, item.toDisplayLine())) }
			}
		}
	}

	Spacer(modifier = Modifier.height(16.dp))
	CharacterStatusOverview(
		character = character,
		viewModel = viewModel,
		onLookupCondition = onLookupCondition
	)

	Spacer(modifier = Modifier.height(16.dp))
	Text(stringResource(R.string.notes_label), style = MaterialTheme.typography.titleMedium)
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Text(
			character.notes.ifBlank { stringResource(R.string.no_notes_label) },
			modifier = Modifier.padding(16.dp)
		)
	}
}

@Composable
private fun InventorySummaryCard(
	title: String,
	items: List<String>,
	emptyText: String
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
			Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
			if (items.isEmpty()) {
				Text(emptyText)
			} else {
				items.forEach { item -> Text(item) }
			}
		}
	}
}

private fun InventoryItem.toDisplayLine(): String {
	return if (quantity > 1) "$name ×$quantity" else name
}
