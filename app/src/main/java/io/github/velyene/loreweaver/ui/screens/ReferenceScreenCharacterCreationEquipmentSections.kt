/*
 * FILE: ReferenceScreenCharacterCreationEquipmentSections.kt
 *
 * TABLE OF CONTENTS:
 * 1. Equipment Section Entry Points
 * 2. Equipment Filter and List Sections
 * 3. Equipment Detail Routing Helpers
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.stringResource
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver

internal fun LazyListScope.renderEquipmentChapterItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.equipmentChapterSections.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_equipment_chapter_title)) }
	items(sectionData.equipmentChapterSections, key = { it.title }) { section ->
		CharacterCreationTextSectionCard(section)
	}
}

internal fun LazyListScope.renderWeaponItems(sectionData: CharacterCreationEquipmentSectionData) {
	if (sectionData.visibleWeapons.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_equipment_weapons)) }
	items(sectionData.visibleWeapons, key = { it.name }) { weapon ->
		WeaponReferenceCard(
			weapon = weapon,
			onClick = {
				openCharacterCreationEquipmentDetail(
					sectionData.onOpenDetail,
					ReferenceDetailResolver.CATEGORY_WEAPONS,
					weapon.name
				)
			}
		)
	}
}

internal fun LazyListScope.renderArmorItems(sectionData: CharacterCreationEquipmentSectionData) {
	if (sectionData.visibleArmor.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_equipment_armor)) }
	items(sectionData.visibleArmor, key = { it.name }) { armor ->
		ArmorReferenceCard(
			armor = armor,
			onClick = {
				openCharacterCreationEquipmentDetail(
					sectionData.onOpenDetail,
					ReferenceDetailResolver.CATEGORY_ARMOR,
					armor.name
				)
			}
		)
	}
}

internal fun LazyListScope.renderAdventuringGearItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.visibleGear.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_equipment_adventuring_gear)) }
	items(sectionData.visibleGear, key = { it.name }) { gear ->
		AdventuringGearReferenceCard(
			gear = gear,
			onClick = {
				openCharacterCreationEquipmentDetail(
					sectionData.onOpenDetail,
					ReferenceDetailResolver.CATEGORY_ADVENTURING_GEAR,
					gear.name
				)
			}
		)
	}
}

internal fun LazyListScope.renderMagicItemItems(sectionData: CharacterCreationEquipmentSectionData) {
	if (sectionData.visibleMagicItems.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_equipment_magic_items)) }
	items(sectionData.visibleMagicItems, key = { it.name }) { item ->
		MagicItemReferenceCard(
			item = item,
			onClick = {
				openCharacterCreationEquipmentDetail(
					sectionData.onOpenDetail,
					ReferenceDetailResolver.CATEGORY_MAGIC_ITEMS,
					item.name
				)
			}
		)
	}
}

internal fun LazyListScope.renderAmmunitionItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.visibleAmmunition.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_equipment_ammunition)) }
	items(sectionData.visibleAmmunition, key = { it.type }) { ammunition ->
		AmmunitionReferenceCard(
			ammunition = ammunition,
			onClick = {
				openCharacterCreationEquipmentDetail(
					sectionData.onOpenDetail,
					ReferenceDetailResolver.CATEGORY_AMMUNITION,
					ammunition.type
				)
			}
		)
	}
}

internal fun LazyListScope.renderFocusItems(sectionData: CharacterCreationEquipmentSectionData) {
	if (sectionData.visibleFocuses.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_equipment_focuses)) }
	items(sectionData.visibleFocuses, key = { "${it.group.name}-${it.name}" }) { focus ->
		FocusReferenceCard(
			focus = focus,
			onClick = {
				openCharacterCreationEquipmentDetail(
					sectionData.onOpenDetail,
					ReferenceDetailResolver.CATEGORY_SPELLCASTING_FOCUSES,
					focus.name
				)
			}
		)
	}
}

internal fun LazyListScope.renderMountItems(sectionData: CharacterCreationEquipmentSectionData) {
	if (sectionData.visibleMounts.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_equipment_mounts)) }
	items(sectionData.visibleMounts, key = { it.item }) { mount ->
		MountReferenceCard(
			mount = mount,
			onClick = {
				openCharacterCreationEquipmentDetail(
					sectionData.onOpenDetail,
					ReferenceDetailResolver.CATEGORY_MOUNTS,
					mount.item
				)
			}
		)
	}
}

internal fun LazyListScope.renderTackAndDrawnItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.visibleTackAndDrawn.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_equipment_tack_and_drawn)) }
	items(sectionData.visibleTackAndDrawn, key = { it.item }) { item ->
		TackDrawnReferenceCard(
			item = item,
			onClick = {
				openCharacterCreationEquipmentDetail(
					sectionData.onOpenDetail,
					ReferenceDetailResolver.CATEGORY_TACK_AND_VEHICLES,
					item.item
				)
			}
		)
	}
}

internal fun LazyListScope.renderLargeVehicleItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.visibleLargeVehicles.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_equipment_large_vehicles)) }
	items(sectionData.visibleLargeVehicles, key = { it.ship }) { vehicle ->
		LargeVehicleReferenceCard(
			vehicle = vehicle,
			onClick = {
				openCharacterCreationEquipmentDetail(
					sectionData.onOpenDetail,
					ReferenceDetailResolver.CATEGORY_LARGE_VEHICLES,
					vehicle.ship
				)
			}
		)
	}
}

internal fun LazyListScope.renderWeaponPropertyItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.weaponPropertySections.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_equipment_weapon_properties)) }
	items(sectionData.weaponPropertySections, key = { it.title }) { section ->
		CharacterCreationTextSectionCard(section)
	}
}

internal fun LazyListScope.renderWeaponMasteryItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.weaponMasterySections.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_equipment_weapon_masteries)) }
	items(sectionData.weaponMasterySections, key = { it.title }) { section ->
		CharacterCreationTextSectionCard(section)
	}
}

internal fun LazyListScope.renderToolItems(sectionData: CharacterCreationEquipmentSectionData) {
	if (sectionData.visibleTools.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_equipment_tools)) }
	items(sectionData.visibleTools, key = { "${it.category.name}-${it.name}" }) { tool ->
		ToolReferenceCard(
			tool = tool,
			onClick = {
				openCharacterCreationEquipmentDetail(
					sectionData.onOpenDetail,
					ReferenceDetailResolver.CATEGORY_TOOLS,
					tool.name
				)
			}
		)
	}
}

internal fun LazyListScope.renderEquipmentTableItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.equipmentTables.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_equipment_tables)) }
	items(sectionData.equipmentTables, key = { it.title }) { table ->
		ReferenceTableCard(table)
	}
}

internal fun LazyListScope.renderAdventuringGearDetailItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.adventuringGearDetailSections.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader(stringResource(R.string.reference_character_creation_equipment_gear_details)) }
	items(sectionData.adventuringGearDetailSections, key = { it.title }) { section ->
		CharacterCreationTextSectionCard(section)
	}
}
