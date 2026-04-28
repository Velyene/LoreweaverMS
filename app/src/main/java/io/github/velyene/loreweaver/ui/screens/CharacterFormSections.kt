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
import io.github.velyene.loreweaver.ui.theme.AntiqueGold
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.MutedText
import io.github.velyene.loreweaver.ui.theme.PanelSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CharacterFormContent(
	screenTitle: String,
	classes: List<String>,
	formState: CharacterFormState,
	classInfo: ClassInfo?,
	callbacks: CharacterFormCallbacks,
	onBack: () -> Unit
) {
	val scrollState = rememberScrollState()

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(screenTitle) },
				navigationIcon = {
					IconButton(onClick = onBack) {
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
			CharacterBasicsSection(
				classes = classes,
				formState = formState,
				onFormStateChange = callbacks.onFormStateChange,
				onClassSelected = callbacks.onClassSelected,
				classInfo = classInfo,
				onRandomName = callbacks.onRandomName,
				onQuickBuild = callbacks.onQuickBuild
			)

			HorizontalDivider()

			AttributeSection(
				formState = formState,
				onFormStateChange = callbacks.onFormStateChange,
				onRandomizeAttributes = callbacks.onRandomizeAttributes
			)

			HorizontalDivider()

			VitalSection(
				formState = formState,
				onFormStateChange = callbacks.onFormStateChange,
				hitDieLabel = classInfo?.hitDie?.toString() ?: formState.hitDieType,
				onRecalcHp = callbacks.onRecalcHp,
				showManaSection = classInfo == null || classInfo.defaultSpellSlotsL1.isNotEmpty(),
				onRecalcMana = callbacks.onRecalcMana,
				onRecalcStamina = callbacks.onRecalcStamina
			)

			HorizontalDivider()

			CombatStatsSection(
				formState = formState,
				onFormStateChange = callbacks.onFormStateChange
			)

			HorizontalDivider()

			SkillProficienciesSection(
				skills = CharacterFormConstants.SKILL_LIST,
				selectedProficiencies = formState.selectedProficiencies,
				onToggleSkill = callbacks.onToggleSkill
			)

			HorizontalDivider()

			ResourcesSection(
				resources = formState.resources,
				onResourceNameChange = callbacks.onResourceNameChange,
				onResourceMaxChange = callbacks.onResourceMaxChange,
				onRemoveResource = callbacks.onRemoveResource,
				onAddResource = callbacks.onAddResource
			)

			HorizontalDivider()

			InventorySection(
				inventoryText = formState.inventoryText,
				onInventoryChange = callbacks.onInventoryChange
			)

			HorizontalDivider()

			HitDieSection(
				hitDieType = formState.hitDieType,
				onHitDieChange = callbacks.onHitDieChange
			)

			HorizontalDivider()

			ActionsSection(
				actions = formState.actions,
				onActionNameChange = callbacks.onActionNameChange,
				onActionAttackBonusChange = callbacks.onActionAttackBonusChange,
				onActionDamageChange = callbacks.onActionDamageChange,
				onRemoveAction = callbacks.onRemoveAction,
				onAddAction = callbacks.onAddAction
			)

			HorizontalDivider()

			StatusNotesSection(
				formState = formState,
				onFormStateChange = callbacks.onFormStateChange
			)

			Spacer(modifier = Modifier.height(16.dp))

			Button(onClick = callbacks.onSaveCharacter, modifier = Modifier.fillMaxWidth()) {
				Text(stringResource(R.string.save_character_button))
			}

			Spacer(modifier = Modifier.height(32.dp))
		}
	}
}

@Composable
private fun CharacterFormSectionTitle(textResId: Int) {
	Text(stringResource(textResId), style = MaterialTheme.typography.titleMedium)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterBasicsSection(
	classes: List<String>,
	formState: CharacterFormState,
	onFormStateChange: (CharacterFormState) -> Unit,
	onClassSelected: (String) -> Unit,
	classInfo: ClassInfo?,
	onRandomName: () -> Unit,
	onQuickBuild: () -> Unit
) {
	var classExpanded by remember { mutableStateOf(false) }
	val nameRequiredError = stringResource(R.string.name_required_error)

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

	ExposedDropdownMenuBox(
		expanded = classExpanded,
		onExpandedChange = { classExpanded = !classExpanded },
		modifier = Modifier.fillMaxWidth()
	) {
		OutlinedTextField(
			value = formState.type,
			onValueChange = {},
			readOnly = true,
			label = { Text(stringResource(R.string.class_type_label)) },
			trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
			modifier = Modifier
				.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
				.fillMaxWidth()
		)
		ExposedDropdownMenu(
			expanded = classExpanded,
			onDismissRequest = { classExpanded = false }
		) {
			classes.forEach { className ->
				DropdownMenuItem(
					text = { Text(className) },
					onClick = {
						classExpanded = false
						onClassSelected(className)
					}
				)
			}
		}
	}

	if (classInfo != null) {
		ClassInfoCard(classInfo = classInfo)
	}

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
			onValueChange = {
				onFormStateChange(formState.copy(challengeRating = it))
			},
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
					classInfo.primaryStats.joinToString(" â€º ")
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

	OutlinedTextField(
		value = formState.party,
		onValueChange = {
			onFormStateChange(formState.copy(party = it))
		},
		label = { Text(stringResource(R.string.party_label)) },
		modifier = Modifier.fillMaxWidth(),
		singleLine = true
	)
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
private fun StatusNotesSection(
	formState: CharacterFormState,
	onFormStateChange: (CharacterFormState) -> Unit
) {
	OutlinedTextField(
		value = formState.status,
		onValueChange = {
			onFormStateChange(formState.copy(status = it))
		},
		label = { Text(stringResource(R.string.status_label)) },
		modifier = Modifier.fillMaxWidth(),
		singleLine = true
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
