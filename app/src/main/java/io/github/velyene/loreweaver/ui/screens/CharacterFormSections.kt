@file:Suppress("kotlin:S107")

/*
 * FILE: CharacterFormSections.kt
 *
 * TABLE OF CONTENTS:
 * 1. Main form entry point (CharacterFormSections)
 * 2. Identity and party sections
 * 3. Class, level, and stats sections
 * 4. Ability scores, skills, and proficiencies
 * 5. Actions, resources, and inventory
 * 6. Spell slots and spellcasting
 * 7. Notes and journal
 * 8. Reusable form field components
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterAction
import io.github.velyene.loreweaver.domain.model.CharacterResource
import io.github.velyene.loreweaver.domain.model.ClassInfo
import io.github.velyene.loreweaver.domain.util.CharacterCreationReference
import io.github.velyene.loreweaver.ui.theme.AntiqueGold
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.MutedText
import io.github.velyene.loreweaver.ui.theme.PanelSurface

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun CharacterFormContent(
	screenTitle: String,
	classes: List<String>,
	speciesOptions: List<String>,
	backgroundOptions: List<String>,
	formState: CharacterFormState,
	classInfo: ClassInfo?,
	callbacks: CharacterFormCallbacks,
	currentSection: CharacterBuilderSection,
	onSectionSelected: (CharacterBuilderSection) -> Unit,
	onStepBack: () -> Unit,
	onStepNext: () -> Unit
) {
	val scrollState = rememberScrollState()
	val sections = CharacterBuilderSection.entries
	val currentStep = currentSection.ordinal + 1

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(screenTitle) },
				navigationIcon = {
					IconButton(onClick = onStepBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							stringResource(R.string.back_button)
						)
					}
				}
			)
		}
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.padding(16.dp)
				.verticalScroll(scrollState)
				.visibleVerticalScrollbar(scrollState),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			BuilderProgressHeader(
				sections = sections,
				currentSection = currentSection,
				currentStep = currentStep,
				onSectionSelected = onSectionSelected
			)

			when (currentSection) {
				CharacterBuilderSection.IDENTITY -> CharacterBasicsSection(
					classes = classes,
					speciesOptions = speciesOptions,
					backgroundOptions = backgroundOptions,
					formState = formState,
					onFormStateChange = callbacks.onFormStateChange,
					onClassSelected = callbacks.onClassSelected,
					classInfo = classInfo,
					onRandomName = callbacks.onRandomName,
					onQuickBuild = callbacks.onQuickBuild
				)

				CharacterBuilderSection.CORE_STATS -> CoreStatsBuilderSection(
					formState = formState,
					classInfo = classInfo,
					onFormStateChange = callbacks.onFormStateChange,
					onRandomizeAttributes = callbacks.onRandomizeAttributes,
					onRecalcHp = callbacks.onRecalcHp,
					onRecalcMana = callbacks.onRecalcMana,
					onRecalcStamina = callbacks.onRecalcStamina,
					onToggleSkill = callbacks.onToggleSkill,
					onHitDieChange = callbacks.onHitDieChange
				)

				CharacterBuilderSection.BUILD_SUGGESTIONS -> BuildSuggestionsSection(
					formState = formState,
					classInfo = classInfo,
					onFormStateChange = callbacks.onFormStateChange,
					onSpellsChange = { callbacks.onFormStateChange(formState.copy(spellsText = it)) },
					onResourceNameChange = callbacks.onResourceNameChange,
					onResourceMaxChange = callbacks.onResourceMaxChange,
					onRemoveResource = callbacks.onRemoveResource,
					onAddResource = callbacks.onAddResource,
					onInventoryChange = callbacks.onInventoryChange,
					onActionNameChange = callbacks.onActionNameChange,
					onActionAttackBonusChange = callbacks.onActionAttackBonusChange,
					onActionDamageChange = callbacks.onActionDamageChange,
					onRemoveAction = callbacks.onRemoveAction,
					onAddAction = callbacks.onAddAction
				)

				CharacterBuilderSection.STATUS_AND_NOTES -> StatusNotesSection(
					formState = formState,
					onFormStateChange = callbacks.onFormStateChange
				)

				CharacterBuilderSection.REVIEW_AND_SAVE -> ReviewAndSaveSection(
					formState = formState,
					classInfo = classInfo
				)
			}

			Spacer(modifier = Modifier.height(16.dp))

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp)
			) {
				OutlinedButton(onClick = onStepBack, modifier = Modifier.weight(1f)) {
					Text(
						stringResource(
							if (currentSection == CharacterBuilderSection.IDENTITY) {
								R.string.character_builder_exit_button
							} else {
								R.string.character_builder_previous_button
							}
						)
					)
				}
				Button(
					onClick = if (currentSection == CharacterBuilderSection.REVIEW_AND_SAVE) {
						callbacks.onSaveCharacter
					} else {
						onStepNext
					},
					modifier = Modifier.weight(1f)
				) {
					Text(
						stringResource(
							if (currentSection == CharacterBuilderSection.REVIEW_AND_SAVE) {
								R.string.save_character_button
							} else {
								R.string.character_builder_continue_button
							}
						)
					)
				}
			}

			Spacer(modifier = Modifier.height(32.dp))
		}
	}
}

@Composable
private fun CharacterFormSectionTitle(textResId: Int) {
	Text(stringResource(textResId), style = MaterialTheme.typography.titleMedium)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BuilderProgressHeader(
	sections: List<CharacterBuilderSection>,
	currentSection: CharacterBuilderSection,
	currentStep: Int,
	onSectionSelected: (CharacterBuilderSection) -> Unit
) {
	Card(
		colors = CardDefaults.cardColors(containerColor = PanelSurface),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp)
		) {
			Text(
				text = stringResource(R.string.character_builder_step_counter, currentStep, sections.size),
				style = MaterialTheme.typography.labelLarge,
				color = AntiqueGold,
				fontWeight = FontWeight.Bold
			)
			Text(
				text = stringResource(currentSection.titleResId),
				style = MaterialTheme.typography.headlineSmall
			)
			Text(
				text = stringResource(currentSection.descriptionResId),
				style = MaterialTheme.typography.bodyMedium,
				color = MutedText
			)
			LinearProgressIndicator(
				progress = { currentStep / sections.size.toFloat() },
				modifier = Modifier.fillMaxWidth()
			)
			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				sections.forEach { section ->
					FilterChip(
						selected = section == currentSection,
						onClick = { onSectionSelected(section) },
						label = { Text(stringResource(section.titleResId)) }
					)
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterBasicsSection(
	classes: List<String>,
	speciesOptions: List<String>,
	backgroundOptions: List<String>,
	formState: CharacterFormState,
	onFormStateChange: (CharacterFormState) -> Unit,
	onClassSelected: (String) -> Unit,
	classInfo: ClassInfo?,
	onRandomName: () -> Unit,
	onQuickBuild: () -> Unit
) {
	val nameRequiredError = stringResource(R.string.name_required_error)
	val suggestions = remember(classInfo, formState.background) {
		buildCharacterSuggestions(classInfo = classInfo, backgroundName = formState.background)
	}
	val speciesReference = remember(formState.species) {
		CharacterCreationReference.RACES.firstOrNull { race ->
			race.name.equals(formState.species.trim(), ignoreCase = true)
		}
	}
	val backgroundReference = remember(formState.background) {
		CharacterCreationReference.BACKGROUNDS.firstOrNull { background ->
			background.name.equals(formState.background.trim(), ignoreCase = true)
		}
	}

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		OutlinedButton(onClick = onQuickBuild, modifier = Modifier.weight(1f)) {
			Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
			Spacer(modifier = Modifier.width(8.dp))
			Text(stringResource(R.string.character_form_quick_build))
		}
		OutlinedButton(onClick = onRandomName, modifier = Modifier.weight(1f)) {
			Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
			Spacer(modifier = Modifier.width(8.dp))
			Text(stringResource(R.string.character_form_random_name))
		}
	}

	OutlinedTextField(
		value = formState.name,
		onValueChange = {
			onFormStateChange(formState.copy(name = it, nameError = false))
		},
		label = { Text(stringResource(R.string.name_label)) },
		modifier = Modifier
			.fillMaxWidth()
			.semantics { if (formState.nameError) error(nameRequiredError) },
		isError = formState.nameError,
		singleLine = true
	)

	SelectionDropdownField(
		labelResId = R.string.class_type_label,
		value = formState.type,
		options = classes,
		onOptionSelected = onClassSelected
	)

	SelectionDropdownField(
		labelResId = R.string.species_label,
		value = formState.species,
		options = speciesOptions,
		onOptionSelected = { selectedSpecies ->
			onFormStateChange(formState.copy(species = selectedSpecies))
		}
	)

	SelectionDropdownField(
		labelResId = R.string.background_label,
		value = formState.background,
		options = backgroundOptions,
		onOptionSelected = { selectedBackground ->
			onFormStateChange(formState.copy(background = selectedBackground))
		}
	)

	OutlinedTextField(
		value = formState.party,
		onValueChange = { onFormStateChange(formState.copy(party = it)) },
		label = { Text(stringResource(R.string.party_label)) },
		modifier = Modifier.fillMaxWidth(),
		singleLine = true
	)

	if (classInfo != null) {
		ClassInfoCard(classInfo = classInfo)
		SuggestionHighlightsSection(suggestions = suggestions)
	}

	speciesReference?.let { selectedSpecies ->
		OriginReferenceCard(
			title = selectedSpecies.name,
			lines = listOf(
				"${stringResource(R.string.ability_increase_label)} ${selectedSpecies.abilityScoreIncrease}",
				"${stringResource(R.string.speed_label)} ${selectedSpecies.speed}",
				"${stringResource(R.string.languages_label)} ${selectedSpecies.languages}",
				selectedSpecies.traits.take(2).joinToString(prefix = "${stringResource(R.string.species_traits_label)} ") { it.name }
			)
		)
	}

	backgroundReference?.let { selectedBackground ->
		OriginReferenceCard(
			title = selectedBackground.name,
			lines = listOf(
				"${stringResource(R.string.background_feat_label)} ${selectedBackground.feat}",
				"${stringResource(R.string.background_skills_label)} ${selectedBackground.skillProficiencies.joinToString()}",
				"${stringResource(R.string.tool_proficiency_label)} ${selectedBackground.toolProficiency}",
				"${stringResource(R.string.equipment_options_label)} ${selectedBackground.equipmentOptions.joinToString(" / ")}"
			)
		)
	}
}

@Composable
private fun SuggestionHighlightsSection(suggestions: CharacterBuildSuggestions) {
	val suggestedItems = remember(suggestions) {
		(suggestions.suggestedEquipment + suggestions.backgroundEquipment).distinct()
	}

	SuggestionCard(
		title = stringResource(R.string.character_build_recommended_abilities_title),
		lines = suggestions.recommendedAbilities.ifEmpty {
			listOf(stringResource(R.string.character_build_generalist_abilities))
		}
	)
	SuggestionCard(
		title = stringResource(R.string.character_build_suggested_spells_title),
		lines = suggestions.suggestedSpells.ifEmpty {
			listOf(stringResource(R.string.character_build_no_spells_suggestion))
		}
	)
	SuggestionCard(
		title = stringResource(R.string.character_build_suggested_equipment_title),
		lines = suggestedItems.ifEmpty {
			listOf(stringResource(R.string.character_build_no_equipment_suggestion))
		}
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectionDropdownField(
	labelResId: Int,
	value: String,
	options: List<String>,
	onOptionSelected: (String) -> Unit
) {
	var expanded by remember { mutableStateOf(false) }

	ExposedDropdownMenuBox(
		expanded = expanded,
		onExpandedChange = { expanded = !expanded },
		modifier = Modifier.fillMaxWidth()
	) {
		OutlinedTextField(
			value = value,
			onValueChange = {},
			readOnly = true,
			label = { Text(stringResource(labelResId)) },
			trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
			modifier = Modifier
				.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
				.fillMaxWidth()
		)
		ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
			options.forEach { option ->
				DropdownMenuItem(
					text = { Text(option) },
					onClick = {
						expanded = false
						onOptionSelected(option)
					}
				)
			}
		}
	}
}

@Composable
private fun OriginReferenceCard(title: String, lines: List<String>) {
	Card(
		colors = CardDefaults.cardColors(containerColor = PanelSurface),
		shape = RoundedCornerShape(8.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			Text(title, style = MaterialTheme.typography.titleSmall, color = AntiqueGold)
			lines.filter { it.isNotBlank() }.forEach { line ->
				Text(line, style = MaterialTheme.typography.bodySmall, color = MutedText)
			}
		}
	}
}

@Composable
private fun ClassInfoCard(classInfo: ClassInfo) {
	Card(
		colors = CardDefaults.cardColors(containerColor = PanelSurface),
		shape = RoundedCornerShape(8.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(12.dp, 8.dp)) {
			Text(
				stringResource(
					R.string.class_info_hit_die_summary,
					classInfo.displayName,
					classInfo.hitDie
				),
				color = AntiqueGold,
				style = MaterialTheme.typography.labelMedium,
				fontWeight = FontWeight.Bold
			)
			Text(
				stringResource(
					R.string.class_info_recommended_stats,
					classInfo.primaryStats.joinToString(" › ")
				),
				color = MutedText,
				style = MaterialTheme.typography.bodySmall
			)
			if (classInfo.defaultSpellSlotsL1.isNotEmpty()) {
				Text(
					stringResource(R.string.class_info_spellcaster_summary),
					color = ArcaneTeal,
					style = MaterialTheme.typography.bodySmall
				)
			}
		}
	}
}

@Composable
private fun CoreStatsBuilderSection(
	formState: CharacterFormState,
	classInfo: ClassInfo?,
	onFormStateChange: (CharacterFormState) -> Unit,
	onRandomizeAttributes: () -> Unit,
	onRecalcHp: () -> Unit,
	onRecalcMana: () -> Unit,
	onRecalcStamina: () -> Unit,
	onToggleSkill: (String) -> Unit,
	onHitDieChange: (String) -> Unit
) {
	OutlinedTextField(
		value = formState.level,
		onValueChange = {
			if (it.isDigitsOnlyInput()) {
				onFormStateChange(formState.copy(level = it))
			}
		},
		label = { Text(stringResource(R.string.level_label)) },
		modifier = Modifier.fillMaxWidth(),
		keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
		singleLine = true
	)

	if (formState.party != CharacterFormConstants.ADVENTURERS_PARTY) {
		OutlinedTextField(
			value = formState.challengeRating,
			onValueChange = { onFormStateChange(formState.copy(challengeRating = it)) },
			label = { Text(stringResource(R.string.challenge_rating_input_label)) },
			modifier = Modifier.fillMaxWidth(),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
			singleLine = true,
			supportingText = {
				Text(
					stringResource(R.string.challenge_rating_supporting_text),
					style = MaterialTheme.typography.labelSmall,
					color = MutedText
				)
			}
		)
	}

	HorizontalDivider()

	VitalSection(
		formState = formState,
		onFormStateChange = onFormStateChange,
		hitDieLabel = classInfo?.hitDie?.toString() ?: formState.hitDieType,
		onRecalcHp = onRecalcHp,
		showManaSection = classInfo == null || classInfo.defaultSpellSlotsL1.isNotEmpty(),
		onRecalcMana = onRecalcMana,
		onRecalcStamina = onRecalcStamina
	)

	HorizontalDivider()

	CombatStatsSection(
		formState = formState,
		onFormStateChange = onFormStateChange
	)

	HorizontalDivider()

	AttributeSection(
		formState = formState,
		onFormStateChange = onFormStateChange,
		onRandomizeAttributes = onRandomizeAttributes
	)

	HorizontalDivider()

	SkillProficienciesSection(
		skills = CharacterFormConstants.SKILL_LIST,
		selectedProficiencies = formState.selectedProficiencies,
		onToggleSkill = onToggleSkill
	)

	HorizontalDivider()

	HitDieSection(
		hitDieType = formState.hitDieType,
		onHitDieChange = onHitDieChange
	)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AttributeSection(
	formState: CharacterFormState,
	onFormStateChange: (CharacterFormState) -> Unit,
	onRandomizeAttributes: () -> Unit
) {
	val attributes = CharacterFormConstants.ABILITY_SCORE_ABBREVIATIONS.zip(
		listOf(
			formState.str,
			formState.dex,
			formState.con,
			formState.intell,
			formState.wis,
			formState.cha
		)
	)

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		CharacterFormSectionTitle(R.string.attributes_title)
		OutlinedButton(
			onClick = onRandomizeAttributes,
			border = BorderStroke(1.dp, ArcaneTeal)
		) {
			Icon(
				Icons.Default.Casino,
				null,
				tint = ArcaneTeal,
				modifier = Modifier.size(16.dp)
			)
			Spacer(Modifier.width(4.dp))
			Text(stringResource(R.string.randomize_button), color = ArcaneTeal, fontSize = 12.sp)
		}
	}

	attributes.chunked(3).forEach { rowAttributes ->
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			rowAttributes.forEach { (key, value) ->
				Column(modifier = Modifier.weight(1f)) {
					OutlinedTextField(
						value = value,
						onValueChange = {
							val updatedValue =
								it.toAttributeValueOrNull() ?: return@OutlinedTextField
							onFormStateChange(formState.withAttribute(key, updatedValue))
						},
						label = { Text(key) },
						modifier = Modifier.fillMaxWidth(),
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
						singleLine = true,
						supportingText = {
							Text(
								"${CharacterFormConstants.MIN_ATTRIBUTE}â€“${CharacterFormConstants.MAX_ATTRIBUTE}",
								style = MaterialTheme.typography.labelSmall,
								color = MutedText
							)
						}
					)
					FilterChip(
						selected = formState.selectedSaveProficiencies.contains(key),
						onClick = {
							onFormStateChange(
								formState.copy(
									selectedSaveProficiencies = toggleSelection(
										formState.selectedSaveProficiencies,
										key
									)
								)
							)
						},
						label = {
							Text(
								stringResource(R.string.save_chip_label),
								style = MaterialTheme.typography.labelSmall
							)
						},
						modifier = Modifier.padding(top = 2.dp)
					)
				}
			}
		}
	}
}

@Composable
private fun VitalSection(
	formState: CharacterFormState,
	onFormStateChange: (CharacterFormState) -> Unit,
	hitDieLabel: String,
	onRecalcHp: () -> Unit,
	showManaSection: Boolean,
	onRecalcMana: () -> Unit,
	onRecalcStamina: () -> Unit
) {
	val hpRequiredError = stringResource(R.string.hp_required_error)

	CharacterFormSectionTitle(R.string.hit_points_section_title)
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		OutlinedTextField(
			value = formState.hp,
			onValueChange = {
				if (it.isDigitsOnlyInput()) {
					onFormStateChange(formState.copy(hp = it, hpError = false))
				}
			},
			label = { Text(stringResource(R.string.hp_label)) },
			modifier = Modifier
				.weight(1f)
				.semantics { if (formState.hpError) error(hpRequiredError) },
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			isError = formState.hpError,
			singleLine = true
		)
		OutlinedTextField(
			value = formState.maxHp,
			onValueChange = {
				if (it.isDigitsOnlyInput()) {
					onFormStateChange(formState.copy(maxHp = it))
				}
			},
			label = { Text(stringResource(R.string.max_hp_label)) },
			modifier = Modifier.weight(1f),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			singleLine = true
		)
	}
	TextButton(onClick = onRecalcHp) {
		Text(
			stringResource(R.string.auto_calc_hp_button, hitDieLabel),
			color = ArcaneTeal,
			fontSize = 12.sp
		)
	}

	if (showManaSection) {
		CharacterFormSectionTitle(R.string.mana_spell_energy_title)
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			OutlinedTextField(
				value = formState.mana,
				onValueChange = {
					if (it.isDigitsOnlyInput()) {
						onFormStateChange(formState.copy(mana = it))
					}
				},
				label = { Text(stringResource(R.string.mana_label)) },
				modifier = Modifier.weight(1f),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				singleLine = true
			)
			OutlinedTextField(
				value = formState.maxMana,
				onValueChange = {
					if (it.isDigitsOnlyInput()) {
						onFormStateChange(formState.copy(maxMana = it))
					}
				},
				label = { Text(stringResource(R.string.max_mana_label)) },
				modifier = Modifier.weight(1f),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				singleLine = true
			)
		}
		TextButton(onClick = onRecalcMana) {
			Text(
				stringResource(R.string.auto_calc_mana_button),
				color = ArcaneTeal,
				fontSize = 12.sp
			)
		}
	}

	CharacterFormSectionTitle(R.string.stamina_label)
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		OutlinedTextField(
			value = formState.stamina,
			onValueChange = {
				if (it.isDigitsOnlyInput()) {
					onFormStateChange(formState.copy(stamina = it))
				}
			},
			label = { Text(stringResource(R.string.stamina_label)) },
			modifier = Modifier.weight(1f),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			singleLine = true
		)
		OutlinedTextField(
			value = formState.maxStamina,
			onValueChange = {
				if (it.isDigitsOnlyInput()) {
					onFormStateChange(formState.copy(maxStamina = it))
				}
			},
			label = { Text(stringResource(R.string.max_stamina_label)) },
			modifier = Modifier.weight(1f),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			singleLine = true
		)
	}
	TextButton(onClick = onRecalcStamina) {
		Text(
			stringResource(R.string.auto_calc_stamina_button, formState.con),
			color = ArcaneTeal,
			fontSize = 12.sp
		)
	}
}

@Composable
private fun CombatStatsSection(
	formState: CharacterFormState,
	onFormStateChange: (CharacterFormState) -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		OutlinedTextField(
			value = formState.ac,
			onValueChange = {
				if (it.isDigitsOnlyInput()) {
					onFormStateChange(formState.copy(ac = it))
				}
			},
			label = { Text(stringResource(R.string.ac_label)) },
			modifier = Modifier.weight(1f),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			singleLine = true
		)
		OutlinedTextField(
			value = formState.initiative,
			onValueChange = {
				if (it.isSignedIntegerInput()) {
					onFormStateChange(formState.copy(initiative = it))
				}
			},
			label = { Text(stringResource(R.string.initiative_short_label)) },
			modifier = Modifier.weight(1f),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			singleLine = true
		)
		OutlinedTextField(
			value = formState.speed,
			onValueChange = {
				if (it.isDigitsOnlyInput()) {
					onFormStateChange(formState.copy(speed = it))
				}
			},
			label = { Text(stringResource(R.string.speed_label)) },
			modifier = Modifier.weight(1f),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			singleLine = true
		)
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillProficienciesSection(
	skills: List<String>,
	selectedProficiencies: Set<String>,
	onToggleSkill: (String) -> Unit
) {
	CharacterFormSectionTitle(R.string.skill_prof_title)
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {
		skills.forEach { skill ->
			FilterChip(
				selected = selectedProficiencies.contains(skill),
				onClick = { onToggleSkill(skill) },
				label = { Text(skill) }
			)
		}
	}
}

@Composable
private fun ResourcesSection(
	resources: List<CharacterResource>,
	onResourceNameChange: (Int, String) -> Unit,
	onResourceMaxChange: (Int, String) -> Unit,
	onRemoveResource: (Int) -> Unit,
	onAddResource: () -> Unit
) {
	CharacterFormSectionTitle(R.string.resources_section_title)
	resources.forEachIndexed { index, resource ->
		Row(
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically
		) {
			OutlinedTextField(
				value = resource.name,
				onValueChange = { onResourceNameChange(index, it) },
				label = { Text(stringResource(R.string.name_label)) },
				modifier = Modifier.weight(1.5f),
				singleLine = true
			)
			Spacer(Modifier.width(8.dp))
			OutlinedTextField(
				value = resource.max.toString(),
				onValueChange = { onResourceMaxChange(index, it) },
				label = { Text(stringResource(R.string.max_label)) },
				modifier = Modifier.weight(1f),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				singleLine = true
			)
			IconButton(onClick = { onRemoveResource(index) }) {
				Icon(Icons.Default.Remove, stringResource(R.string.remove_resource_desc))
			}
		}
	}
	Button(onClick = onAddResource) {
		Text(stringResource(R.string.add_resource_button))
	}
}

@Composable
private fun InventorySection(
	inventoryText: String,
	onInventoryChange: (String) -> Unit
) {
	CharacterFormSectionTitle(R.string.inventory_section_title)
	OutlinedTextField(
		value = inventoryText,
		onValueChange = onInventoryChange,
		modifier = Modifier.fillMaxWidth(),
		minLines = 4,
		placeholder = {
			Text(stringResource(R.string.inventory_placeholder_one_per_line), color = MutedText)
		}
	)
}

@Composable
private fun HitDieSection(
	hitDieType: String,
	onHitDieChange: (String) -> Unit
) {
	CharacterFormSectionTitle(R.string.hit_die_section_title)
	OutlinedTextField(
		value = hitDieType,
		onValueChange = onHitDieChange,
		label = { Text(stringResource(R.string.hit_die_input_label)) },
		modifier = Modifier.fillMaxWidth(),
		keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
		singleLine = true
	)
}

@Composable
private fun ActionsSection(
	actions: List<CharacterAction>,
	onActionNameChange: (Int, String) -> Unit,
	onActionAttackBonusChange: (Int, String) -> Unit,
	onActionDamageChange: (Int, String) -> Unit,
	onRemoveAction: (Int) -> Unit,
	onAddAction: () -> Unit
) {
	CharacterFormSectionTitle(R.string.custom_actions_section_title)
	actions.forEachIndexed { index, action ->
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 4.dp)
		) {
			Column(
				modifier = Modifier.padding(8.dp),
				verticalArrangement = Arrangement.spacedBy(4.dp)
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					OutlinedTextField(
						value = action.name,
						onValueChange = { onActionNameChange(index, it) },
						label = { Text(stringResource(R.string.action_name_label)) },
						modifier = Modifier.weight(1f),
						singleLine = true
					)
					IconButton(onClick = { onRemoveAction(index) }) {
						Icon(Icons.Default.Remove, stringResource(R.string.remove_action_desc))
					}
				}
				Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
					OutlinedTextField(
						value = action.attackBonus.toString(),
						onValueChange = { onActionAttackBonusChange(index, it) },
						label = { Text(stringResource(R.string.atk_bonus_label)) },
						modifier = Modifier.weight(1f),
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
						singleLine = true
					)
					OutlinedTextField(
						value = action.damageDice,
						onValueChange = { onActionDamageChange(index, it) },
						label = { Text(stringResource(R.string.damage_label_short)) },
						modifier = Modifier.weight(1f),
						singleLine = true
					)
				}
			}
		}
	}
	Button(onClick = onAddAction) {
		Text(stringResource(R.string.add_action_button))
	}
}

@Composable
private fun BuildSuggestionsSection(
	formState: CharacterFormState,
	classInfo: ClassInfo?,
	onFormStateChange: (CharacterFormState) -> Unit,
	onSpellsChange: (String) -> Unit,
	onResourceNameChange: (Int, String) -> Unit,
	onResourceMaxChange: (Int, String) -> Unit,
	onRemoveResource: (Int) -> Unit,
	onAddResource: () -> Unit,
	onInventoryChange: (String) -> Unit,
	onActionNameChange: (Int, String) -> Unit,
	onActionAttackBonusChange: (Int, String) -> Unit,
	onActionDamageChange: (Int, String) -> Unit,
	onRemoveAction: (Int) -> Unit,
	onAddAction: () -> Unit
) {
	val suggestions = remember(classInfo, formState.background) {
		buildCharacterSuggestions(classInfo = classInfo, backgroundName = formState.background)
	}
	val starterEquipment = remember(suggestions) {
		(suggestions.suggestedEquipment + suggestions.backgroundEquipment).distinct()
	}

	SuggestionCard(
		title = stringResource(R.string.character_build_recommended_abilities_title),
		lines = suggestions.recommendedAbilities.ifEmpty {
			listOf(stringResource(R.string.character_build_generalist_abilities))
		}
	)

	SuggestionCard(
		title = stringResource(R.string.character_build_suggested_spells_title),
		lines = suggestions.suggestedSpells.ifEmpty {
			listOf(stringResource(R.string.character_build_no_spells_suggestion))
		}
	)

	SuggestionCard(
		title = stringResource(R.string.character_build_suggested_equipment_title),
		lines = starterEquipment.ifEmpty {
			listOf(stringResource(R.string.character_build_no_equipment_suggestion))
		}
	)

	if (starterEquipment.isNotEmpty()) {
		OutlinedButton(
			onClick = {
				onFormStateChange(formState.copy(inventoryText = starterEquipment.joinToString("\n")))
			},
			modifier = Modifier.fillMaxWidth()
		) {
			Text(stringResource(R.string.character_build_apply_equipment_button))
		}
	}

	if (suggestions.guidance.isNotEmpty()) {
		SuggestionCard(
			title = stringResource(R.string.character_build_guidance_title),
			lines = suggestions.guidance
		)
	}

	CharacterFormSectionTitle(R.string.character_build_selected_spells_title)
	OutlinedTextField(
		value = formState.spellsText,
		onValueChange = onSpellsChange,
		modifier = Modifier.fillMaxWidth(),
		minLines = 4,
		placeholder = {
			Text(
				stringResource(R.string.character_build_selected_spells_placeholder),
				color = MutedText
			)
		}
	)
	if (suggestions.suggestedSpells.isNotEmpty()) {
		OutlinedButton(
			onClick = {
				onSpellsChange(suggestions.suggestedSpells.joinToString("\n"))
			},
			modifier = Modifier.fillMaxWidth()
		) {
			Text(stringResource(R.string.character_build_apply_spells_button))
		}
	}

	HorizontalDivider()

	ResourcesSection(
		resources = formState.resources,
		onResourceNameChange = onResourceNameChange,
		onResourceMaxChange = onResourceMaxChange,
		onRemoveResource = onRemoveResource,
		onAddResource = onAddResource
	)

	HorizontalDivider()

	InventorySection(
		inventoryText = formState.inventoryText,
		onInventoryChange = onInventoryChange
	)

	HorizontalDivider()

	ActionsSection(
		actions = formState.actions,
		onActionNameChange = onActionNameChange,
		onActionAttackBonusChange = onActionAttackBonusChange,
		onActionDamageChange = onActionDamageChange,
		onRemoveAction = onRemoveAction,
		onAddAction = onAddAction
	)
}

@Composable
private fun SuggestionCard(title: String, lines: List<String>) {
	Card(
		colors = CardDefaults.cardColors(containerColor = PanelSurface),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			Text(title, style = MaterialTheme.typography.titleSmall, color = AntiqueGold)
			lines.filter { it.isNotBlank() }.forEach { line ->
				Text("• $line", style = MaterialTheme.typography.bodyMedium)
			}
		}
	}
}

@Composable
private fun StatusNotesSection(
	formState: CharacterFormState,
	onFormStateChange: (CharacterFormState) -> Unit
) {
	ConditionSelectionSection(
		title = stringResource(R.string.encounter_conditions_title),
		supportingText = stringResource(R.string.encounter_conditions_supporting_text),
		emptyText = stringResource(R.string.encounter_conditions_empty),
		selectedConditions = formState.encounterConditions,
		isPersistent = false,
		onConditionsChange = { updatedConditions ->
			onFormStateChange(formState.copy(encounterConditions = updatedConditions))
		}
	)

	ConditionSelectionSection(
		title = stringResource(R.string.persistent_conditions_title),
		supportingText = stringResource(R.string.persistent_conditions_supporting_text),
		emptyText = stringResource(R.string.persistent_conditions_empty),
		selectedConditions = formState.persistentConditions,
		isPersistent = true,
		onConditionsChange = { updatedConditions ->
			onFormStateChange(formState.copy(persistentConditions = updatedConditions))
		}
	)

	CharacterFormSectionTitle(R.string.encounter_status_notes_label)
	OutlinedTextField(
		value = formState.status,
		onValueChange = {
			onFormStateChange(formState.copy(status = it))
		},
		label = { Text(stringResource(R.string.encounter_status_notes_label)) },
		modifier = Modifier.fillMaxWidth(),
		supportingText = {
			Text(
				stringResource(R.string.encounter_status_notes_supporting_text),
				style = MaterialTheme.typography.labelSmall,
				color = MutedText
			)
		}
	)
	OutlinedTextField(
		value = formState.notes,
		onValueChange = {
			onFormStateChange(formState.copy(notes = it))
		},
		label = { Text(stringResource(R.string.notes_label)) },
		modifier = Modifier.fillMaxWidth(),
		minLines = 3
	)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ConditionSelectionSection(
	title: String,
	supportingText: String,
	emptyText: String,
	selectedConditions: Set<String>,
	isPersistent: Boolean,
	onConditionsChange: (Set<String>) -> Unit,
) {
	var customCondition by remember(title) { mutableStateOf("") }
	val normalizedSelection = remember(selectedConditions, isPersistent) {
		buildStatusChipModels(selectedConditions, isPersistent)
	}
	val selectedLabels = remember(selectedConditions) { normalizedStatusLabels(selectedConditions).toSet() }

	CharacterFormSectionTitle(title = title)
	Text(
		text = supportingText,
		style = MaterialTheme.typography.bodySmall,
		color = MutedText
	)
	if (normalizedSelection.isEmpty()) {
		Text(
			text = emptyText,
			style = MaterialTheme.typography.bodySmall,
			color = MutedText
		)
	} else {
		StatusChipFlowRow(
			statuses = normalizedSelection,
			onStatusRemove = { status ->
				onConditionsChange(selectedLabels - status.name)
			}
		)
	}

	Text(
		text = stringResource(R.string.srd_conditions_title),
		style = MaterialTheme.typography.labelLarge,
		color = AntiqueGold,
		fontWeight = FontWeight.Bold
	)
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		ConditionConstants.STANDARD_CONDITIONS.forEach { condition ->
			val metadata = ConditionConstants.metadataFor(condition)
			val isSelected = condition in selectedLabels
			FilterChip(
				selected = isSelected,
				onClick = {
					onConditionsChange(selectedLabels.withToggledCondition(condition))
				},
				label = { Text(condition) },
				border = BorderStroke(1.dp, metadata.borderColor),
				colors = FilterChipDefaults.filterChipColors(
					selectedContainerColor = metadata.color.copy(alpha = 0.24f),
					selectedLabelColor = MaterialTheme.colorScheme.onSurface,
				)
			)
		}
	}

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		OutlinedTextField(
			value = customCondition,
			onValueChange = { customCondition = it },
			label = { Text(stringResource(R.string.custom_condition_label)) },
			modifier = Modifier.weight(1f),
			singleLine = true
		)
		Button(
			onClick = {
				onConditionsChange(selectedLabels.withAddedCondition(customCondition))
				customCondition = ""
			},
			enabled = customCondition.isNotBlank()
		) {
			Text(stringResource(R.string.add_button))
		}
	}
}

@Composable
private fun ReviewAndSaveSection(
	formState: CharacterFormState,
	classInfo: ClassInfo?
) {
	val validation = remember(formState.name, formState.hp) {
		validateCharacterForm(name = formState.name, hp = formState.hp)
	}
	val buildSuggestions = remember(classInfo, formState.background) {
		buildCharacterSuggestions(classInfo = classInfo, backgroundName = formState.background)
	}

	Text(
		text = stringResource(R.string.character_builder_review_intro),
		style = MaterialTheme.typography.bodyMedium,
		color = MutedText
	)

	ReviewCard(title = stringResource(R.string.character_builder_identity_title)) {
		ReviewRow(stringResource(R.string.name_label), formState.name)
		ReviewRow(stringResource(R.string.class_type_label), formState.type)
		ReviewRow(stringResource(R.string.species_label), formState.species)
		ReviewRow(stringResource(R.string.background_label), formState.background)
		ReviewRow(stringResource(R.string.party_label), formState.party)
	}

	ReviewCard(title = stringResource(R.string.character_builder_core_stats_title)) {
		ReviewRow(stringResource(R.string.level_label), formState.level)
		ReviewRow(stringResource(R.string.hit_points_section_title), "${formState.hp} / ${formState.maxHp}")
		ReviewRow(stringResource(R.string.ac_label), formState.ac)
		ReviewRow(stringResource(R.string.initiative_short_label), formState.initiative)
		ReviewRow(stringResource(R.string.speed_label), formState.speed)
		ReviewRow(
			stringResource(R.string.attributes_title),
			listOf(
				"STR ${formState.str}",
				"DEX ${formState.dex}",
				"CON ${formState.con}",
				"INT ${formState.intell}",
				"WIS ${formState.wis}",
				"CHA ${formState.cha}"
			).joinToString(" • ")
		)
	}

	ReviewCard(title = stringResource(R.string.character_builder_build_suggestions_title)) {
		ReviewRow(
			stringResource(R.string.character_build_recommended_abilities_title),
			buildSuggestions.recommendedAbilities.joinToString().ifBlank {
				stringResource(R.string.character_build_generalist_abilities)
			}
		)
		ReviewRow(
			stringResource(R.string.character_build_selected_spells_title),
			formState.spellsText.lineSequence().filter { it.isNotBlank() }.joinToString().ifBlank {
				stringResource(R.string.none_label)
			}
		)
		ReviewRow(
			stringResource(R.string.inventory_section_title),
			formState.inventoryText.lineSequence().filter { it.isNotBlank() }.count().toString()
		)
		ReviewRow(
			stringResource(R.string.resources_section_title),
			formState.resources.size.toString()
		)
		ReviewRow(
			stringResource(R.string.custom_actions_section_title),
			formState.actions.size.toString()
		)
	}

	ReviewCard(title = stringResource(R.string.character_builder_status_notes_title)) {
		ReviewRow(
			stringResource(R.string.encounter_conditions_title),
			formState.encounterConditions.joinToString().ifBlank { stringResource(R.string.none_label) }
		)
		ReviewRow(
			stringResource(R.string.persistent_conditions_title),
			formState.persistentConditions.joinToString().ifBlank { stringResource(R.string.none_label) }
		)
		ReviewRow(stringResource(R.string.encounter_status_notes_label), formState.status)
		ReviewRow(stringResource(R.string.notes_label), formState.notes)
	}

	ReviewCard(title = stringResource(R.string.character_builder_validation_title)) {
		ValidationRow(
			label = stringResource(R.string.character_builder_validation_name),
			isValid = !validation.nameError
		)
		ValidationRow(
			label = stringResource(R.string.character_builder_validation_hp),
			isValid = !validation.hpError
		)
		ValidationRow(
			label = stringResource(R.string.character_builder_validation_species),
			isValid = formState.species.isNotBlank()
		)
		ValidationRow(
			label = stringResource(R.string.character_builder_validation_background),
			isValid = formState.background.isNotBlank()
		)
	}
}

@Composable
private fun ReviewCard(title: String, content: @Composable ColumnScope.() -> Unit) {
	Card(
		colors = CardDefaults.cardColors(containerColor = PanelSurface),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(title, style = MaterialTheme.typography.titleSmall, color = AntiqueGold)
			content()
		}
	}
}

@Composable
private fun ReviewRow(label: String, value: String) {
	Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
		Text(label, style = MaterialTheme.typography.labelMedium, color = AntiqueGold)
		Text(value.ifBlank { stringResource(R.string.none_label) }, style = MaterialTheme.typography.bodyMedium)
	}
}

@Composable
private fun CharacterFormSectionTitle(title: String) {
	Text(title, style = MaterialTheme.typography.titleMedium)
}

private fun Set<String>.withToggledCondition(condition: String): Set<String> {
	val canonicalCondition = canonicalStatusLabel(condition)
	return normalizeConditionSet(toggleSelection(this, canonicalCondition))
}

private fun Set<String>.withAddedCondition(condition: String): Set<String> {
	return normalizeConditionSet(this + condition)
}

private fun normalizeConditionSet(conditions: Iterable<String>): Set<String> {
	return normalizedStatusLabels(conditions).toSet()
}

@Composable
private fun ValidationRow(label: String, isValid: Boolean) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(label, style = MaterialTheme.typography.bodyMedium)
		Text(
			text = stringResource(
				if (isValid) R.string.character_builder_validation_ready else R.string.character_builder_validation_attention
			),
			style = MaterialTheme.typography.labelLarge,
			color = if (isValid) ArcaneTeal else AntiqueGold
		)
	}
}

