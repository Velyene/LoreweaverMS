/*
 * FILE: ReferenceScreenCharacterCreationEquipment.kt
 *
 * TABLE OF CONTENTS:
 * 1. Section state model
 * 2. Top-level equipment list entry point
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.lazy.LazyListScope
import io.github.velyene.loreweaver.domain.util.AdventuringGearEntry
import io.github.velyene.loreweaver.domain.util.AmmunitionReferenceEntry
import io.github.velyene.loreweaver.domain.util.ArmorReferenceEntry
import io.github.velyene.loreweaver.domain.util.CharacterCreationTextSection
import io.github.velyene.loreweaver.domain.util.FocusReferenceEntry
import io.github.velyene.loreweaver.domain.util.LargeVehicleReferenceEntry
import io.github.velyene.loreweaver.domain.util.MagicItemReferenceEntry
import io.github.velyene.loreweaver.domain.util.MountReferenceEntry
import io.github.velyene.loreweaver.domain.util.ReferenceTable
import io.github.velyene.loreweaver.domain.util.TackDrawnReferenceEntry
import io.github.velyene.loreweaver.domain.util.ToolReferenceEntry
import io.github.velyene.loreweaver.domain.util.WeaponReferenceEntry

internal data class CharacterCreationEquipmentSectionData(
	val showEquipment: Boolean,
	val equipmentChapterSections: List<CharacterCreationTextSection>,
	val visibleWeapons: List<WeaponReferenceEntry>,
	val visibleArmor: List<ArmorReferenceEntry>,
	val visibleGear: List<AdventuringGearEntry>,
	val visibleMagicItems: List<MagicItemReferenceEntry>,
	val visibleAmmunition: List<AmmunitionReferenceEntry>,
	val visibleFocuses: List<FocusReferenceEntry>,
	val visibleMounts: List<MountReferenceEntry>,
	val visibleTackAndDrawn: List<TackDrawnReferenceEntry>,
	val visibleLargeVehicles: List<LargeVehicleReferenceEntry>,
	val weaponPropertySections: List<CharacterCreationTextSection>,
	val weaponMasterySections: List<CharacterCreationTextSection>,
	val visibleTools: List<ToolReferenceEntry>,
	val equipmentTables: List<ReferenceTable>,
	val adventuringGearDetailSections: List<CharacterCreationTextSection>,
	val onOpenDetail: (String, String) -> Unit
)

@Suppress("kotlin:S3776")
internal fun LazyListScope.renderCharacterCreationEquipmentItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	renderEquipmentChapterItems(sectionData)
	renderWeaponItems(sectionData)
	renderArmorItems(sectionData)
	renderAdventuringGearItems(sectionData)
	renderMagicItemItems(sectionData)
	renderAmmunitionItems(sectionData)
	renderFocusItems(sectionData)
	renderMountItems(sectionData)
	renderTackAndDrawnItems(sectionData)
	renderLargeVehicleItems(sectionData)
	renderWeaponPropertyItems(sectionData)
	renderWeaponMasteryItems(sectionData)
	renderToolItems(sectionData)
	renderEquipmentTableItems(sectionData)
	renderAdventuringGearDetailItems(sectionData)
}

