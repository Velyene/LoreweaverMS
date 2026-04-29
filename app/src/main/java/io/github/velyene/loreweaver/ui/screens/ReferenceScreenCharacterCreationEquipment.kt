/*
 * FILE: ReferenceScreenCharacterCreationEquipment.kt
 *
 * TABLE OF CONTENTS:
 * 1. Section state model and list entry point
 * 2. Lazy list subsection renderers
 * 3. Equipment reference cards
 * 4. Detail-navigation helpers
 * 5. Search visibility and dataset filters
 * 6. Search matching extensions by equipment type
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.domain.util.AdventuringGearEntry
import io.github.velyene.loreweaver.domain.util.AmmunitionReferenceEntry
import io.github.velyene.loreweaver.domain.util.ArmorReferenceEntry
import io.github.velyene.loreweaver.domain.util.CharacterCreationTextSection
import io.github.velyene.loreweaver.domain.util.EquipmentReference
import io.github.velyene.loreweaver.domain.util.FocusReferenceEntry
import io.github.velyene.loreweaver.domain.util.LargeVehicleReferenceEntry
import io.github.velyene.loreweaver.domain.util.MagicItemReferenceEntry
import io.github.velyene.loreweaver.domain.util.MountReferenceEntry
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver.STAT_LABEL_COST
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver.STAT_LABEL_WEIGHT
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

// Keep the render order aligned with the equipment subsection layout so search results and
// default subsection browsing feel consistent regardless of which content buckets are visible.
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

// region Lazy list subsection renderers

private fun LazyListScope.renderEquipmentChapterItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.equipmentChapterSections.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader("Chapter 6: Equipment") }
	items(sectionData.equipmentChapterSections, key = { it.title }) { section ->
		CharacterCreationTextSectionCard(section)
	}
}

private fun LazyListScope.renderWeaponItems(sectionData: CharacterCreationEquipmentSectionData) {
	if (sectionData.visibleWeapons.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader("Weapons") }
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

private fun LazyListScope.renderArmorItems(sectionData: CharacterCreationEquipmentSectionData) {
	if (sectionData.visibleArmor.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader("Armor") }
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

private fun LazyListScope.renderAdventuringGearItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.visibleGear.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader("Adventuring Gear") }
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

private fun LazyListScope.renderMagicItemItems(sectionData: CharacterCreationEquipmentSectionData) {
	if (sectionData.visibleMagicItems.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader("Magic Items A–Z") }
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

private fun LazyListScope.renderAmmunitionItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.visibleAmmunition.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader("Ammunition") }
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

private fun LazyListScope.renderFocusItems(sectionData: CharacterCreationEquipmentSectionData) {
	if (sectionData.visibleFocuses.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader("Spellcasting Focuses") }
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

private fun LazyListScope.renderMountItems(sectionData: CharacterCreationEquipmentSectionData) {
	if (sectionData.visibleMounts.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader("Mounts and Other Animals") }
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

private fun LazyListScope.renderTackAndDrawnItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.visibleTackAndDrawn.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader("Tack and Drawn Vehicles") }
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

private fun LazyListScope.renderLargeVehicleItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.visibleLargeVehicles.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader("Airborne and Waterborne Vehicles") }
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

private fun LazyListScope.renderWeaponPropertyItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.weaponPropertySections.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader("Weapon Properties") }
	items(sectionData.weaponPropertySections, key = { it.title }) { section ->
		CharacterCreationTextSectionCard(section)
	}
}

private fun LazyListScope.renderWeaponMasteryItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.weaponMasterySections.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader("Weapon Mastery Properties") }
	items(sectionData.weaponMasterySections, key = { it.title }) { section ->
		CharacterCreationTextSectionCard(section)
	}
}

private fun LazyListScope.renderToolItems(sectionData: CharacterCreationEquipmentSectionData) {
	if (sectionData.visibleTools.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader("Tool Reference") }
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

private fun LazyListScope.renderEquipmentTableItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.equipmentTables.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader("Equipment Tables") }
	items(sectionData.equipmentTables, key = { it.title }) { table ->
		ReferenceTableCard(table)
	}
}

private fun LazyListScope.renderAdventuringGearDetailItems(
	sectionData: CharacterCreationEquipmentSectionData
) {
	if (sectionData.adventuringGearDetailSections.isEmpty() || !sectionData.showEquipment) return

	item { ReferenceSectionHeader("Adventuring Gear Details") }
	items(sectionData.adventuringGearDetailSections, key = { it.title }) { section ->
		CharacterCreationTextSectionCard(section)
	}
}

// endregion

// region Equipment reference cards

@Composable
private fun ToolReferenceCard(tool: ToolReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = tool.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow("Category", tool.category.label)
			StackedDetailRow("Ability", tool.ability)
			StackedDetailRow(STAT_LABEL_WEIGHT, tool.weight)
			StackedDetailRow(STAT_LABEL_COST, tool.cost)
			CharacterCreationCardSectionHeader("Utilize")
			BulletListCard(items = tool.utilize)
			if (tool.craft.isNotEmpty()) {
				CharacterCreationCardSectionHeader("Craft")
				BulletListCard(items = tool.craft)
			}
			if (tool.variants.isNotEmpty()) {
				CharacterCreationCardSectionHeader("Variants")
				BulletListCard(items = tool.variants)
			}
			if (tool.notes.isNotEmpty()) {
				CharacterCreationCardSectionHeader("Notes")
				BulletListCard(items = tool.notes)
			}
		}
	}
}

@Composable
private fun WeaponReferenceCard(weapon: WeaponReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = weapon.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow("Category", weapon.category)
			StackedDetailRow("Damage", weapon.damage)
			StackedDetailRow("Mastery", weapon.mastery)
			StackedDetailRow(STAT_LABEL_WEIGHT, weapon.weight)
			StackedDetailRow(STAT_LABEL_COST, weapon.cost)
			if (weapon.properties.isNotEmpty()) {
				CharacterCreationCardSectionHeader("Properties")
				BulletListCard(items = weapon.properties)
			}
		}
	}
}

@Composable
private fun ArmorReferenceCard(armor: ArmorReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = armor.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow("Category / Don-Doff", armor.categoryDonDoff)
			StackedDetailRow("Armor Class", armor.armorClass)
			StackedDetailRow("Strength", armor.strength)
			StackedDetailRow("Stealth", armor.stealth)
			StackedDetailRow(STAT_LABEL_WEIGHT, armor.weight)
			StackedDetailRow(STAT_LABEL_COST, armor.cost)
		}
	}
}

@Composable
private fun AdventuringGearReferenceCard(gear: AdventuringGearEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = gear.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow(STAT_LABEL_WEIGHT, gear.weight)
			StackedDetailRow(STAT_LABEL_COST, gear.cost)
			gear.body?.let { body ->
				Text(text = body, style = MaterialTheme.typography.bodyMedium)
			}
		}
	}
}

@Composable
private fun MagicItemReferenceCard(item: MagicItemReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = item.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			Text(
				text = item.subtitle,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Text(text = item.body, style = MaterialTheme.typography.bodyMedium)
			item.tables.forEach { table ->
				ReferenceTableCard(table)
			}
		}
	}
}

@Composable
private fun AmmunitionReferenceCard(ammunition: AmmunitionReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = ammunition.type,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow("Amount", ammunition.amount)
			StackedDetailRow("Storage", ammunition.storage)
			StackedDetailRow(STAT_LABEL_WEIGHT, ammunition.weight)
			StackedDetailRow(STAT_LABEL_COST, ammunition.cost)
		}
	}
}

@Composable
private fun FocusReferenceCard(focus: FocusReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = focus.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow("Group", focus.group.label)
			StackedDetailRow(STAT_LABEL_WEIGHT, focus.weight)
			StackedDetailRow(STAT_LABEL_COST, focus.cost)
			Text(text = focus.usage, style = MaterialTheme.typography.bodyMedium)
			if (focus.notes.isNotEmpty()) {
				CharacterCreationCardSectionHeader("Notes")
				BulletListCard(items = focus.notes)
			}
		}
	}
}

@Composable
private fun MountReferenceCard(mount: MountReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = mount.item,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow("Carrying Capacity", mount.carryingCapacity)
			StackedDetailRow(STAT_LABEL_COST, mount.cost)
		}
	}
}

@Composable
private fun TackDrawnReferenceCard(item: TackDrawnReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = item.item,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow(STAT_LABEL_WEIGHT, item.weight)
			StackedDetailRow(STAT_LABEL_COST, item.cost)
		}
	}
}

@Composable
private fun LargeVehicleReferenceCard(vehicle: LargeVehicleReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = vehicle.ship,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow("Speed", vehicle.speed)
			StackedDetailRow("Crew", vehicle.crew)
			StackedDetailRow("Passengers", vehicle.passengers)
			StackedDetailRow("Cargo (Tons)", vehicle.cargoTons)
			StackedDetailRow("AC", vehicle.ac)
			StackedDetailRow("HP", vehicle.hp)
			StackedDetailRow("Damage Threshold", vehicle.damageThreshold)
			StackedDetailRow(STAT_LABEL_COST, vehicle.cost)
		}
	}
}

// endregion

private fun openCharacterCreationEquipmentDetail(
	onOpenDetail: (String, String) -> Unit,
	category: String,
	name: String
) {
	onOpenDetail(category, ReferenceDetailResolver.slugFor(name))
}

// region Search visibility and dataset filters

internal fun filterEquipmentTables(query: String): List<ReferenceTable> {
	return EquipmentReference.ALL_TABLES.filter { it.matchesQuery(query) }
}

internal fun filterEquipmentTools(query: String): List<ToolReferenceEntry> {
	return EquipmentReference.ALL_TOOLS.filter { it.matchesSearchQuery(query) }
}

internal fun filterEquipmentWeapons(query: String): List<WeaponReferenceEntry> {
	return EquipmentReference.WEAPONS.filter { it.matchesSearchQuery(query) }
}

internal fun filterEquipmentArmor(query: String): List<ArmorReferenceEntry> {
	return EquipmentReference.ARMOR.filter { it.matchesSearchQuery(query) }
}

internal fun filterAdventuringGear(query: String): List<AdventuringGearEntry> {
	return EquipmentReference.ADVENTURING_GEAR.filter { it.matchesSearchQuery(query) }
}

internal fun filterEquipmentMagicItems(query: String): List<MagicItemReferenceEntry> {
	return EquipmentReference.MAGIC_ITEMS_A_TO_Z.filter { it.matchesSearchQuery(query) }
}

internal fun filterEquipmentAmmunition(query: String): List<AmmunitionReferenceEntry> {
	return EquipmentReference.AMMUNITION.filter { it.matchesSearchQuery(query) }
}

internal fun filterEquipmentFocuses(query: String): List<FocusReferenceEntry> {
	return EquipmentReference.FOCUSES.filter { it.matchesSearchQuery(query) }
}

internal fun filterEquipmentMounts(query: String): List<MountReferenceEntry> {
	return EquipmentReference.MOUNTS.filter { it.matchesSearchQuery(query) }
}

internal fun filterEquipmentTackAndDrawn(query: String): List<TackDrawnReferenceEntry> {
	return EquipmentReference.TACK_AND_DRAWN_ITEMS.filter { it.matchesSearchQuery(query) }
}

internal fun filterEquipmentLargeVehicles(query: String): List<LargeVehicleReferenceEntry> {
	return EquipmentReference.LARGE_VEHICLES.filter { it.matchesSearchQuery(query) }
}

internal fun visibleEquipmentTools(
	searchQuery: String,
	selectedSubsection: CharacterCreationSubsection
): List<ToolReferenceEntry> {
	return visibleEquipmentEntries(searchQuery, selectedSubsection, ::filterEquipmentTools)
}

internal fun visibleEquipmentWeapons(
	searchQuery: String,
	selectedSubsection: CharacterCreationSubsection
): List<WeaponReferenceEntry> {
	return visibleEquipmentEntries(searchQuery, selectedSubsection, ::filterEquipmentWeapons)
}

internal fun visibleEquipmentArmor(
	searchQuery: String,
	selectedSubsection: CharacterCreationSubsection
): List<ArmorReferenceEntry> {
	return visibleEquipmentEntries(searchQuery, selectedSubsection, ::filterEquipmentArmor)
}

internal fun visibleAdventuringGear(searchQuery: String): List<AdventuringGearEntry> {
	return visibleSearchOnlyEntries(searchQuery, ::filterAdventuringGear)
}

internal fun visibleEquipmentMagicItems(searchQuery: String): List<MagicItemReferenceEntry> {
	return visibleSearchOnlyEntries(searchQuery, ::filterEquipmentMagicItems)
}

internal fun visibleEquipmentAmmunition(
	searchQuery: String,
	selectedSubsection: CharacterCreationSubsection
): List<AmmunitionReferenceEntry> {
	return visibleEquipmentEntries(searchQuery, selectedSubsection, ::filterEquipmentAmmunition)
}

internal fun visibleEquipmentFocuses(
	searchQuery: String,
	selectedSubsection: CharacterCreationSubsection
): List<FocusReferenceEntry> {
	return visibleEquipmentEntries(searchQuery, selectedSubsection, ::filterEquipmentFocuses)
}

internal fun visibleEquipmentMounts(
	searchQuery: String,
	selectedSubsection: CharacterCreationSubsection
): List<MountReferenceEntry> {
	return visibleEquipmentEntries(searchQuery, selectedSubsection, ::filterEquipmentMounts)
}

internal fun visibleEquipmentTackAndDrawn(
	searchQuery: String,
	selectedSubsection: CharacterCreationSubsection
): List<TackDrawnReferenceEntry> {
	return visibleEquipmentEntries(searchQuery, selectedSubsection, ::filterEquipmentTackAndDrawn)
}

internal fun visibleEquipmentLargeVehicles(
	searchQuery: String,
	selectedSubsection: CharacterCreationSubsection
): List<LargeVehicleReferenceEntry> {
	return visibleEquipmentEntries(searchQuery, selectedSubsection, ::filterEquipmentLargeVehicles)
}

private inline fun <T> visibleEquipmentEntries(
	searchQuery: String,
	selectedSubsection: CharacterCreationSubsection,
	filter: (String) -> List<T>
): List<T> {
	val normalizedQuery = searchQuery.trim()
	// Equipment subsections stay visible during normal browsing when the equipment tab is selected,
	// but hidden when another subsection is active and the user has not started a search.
	if (normalizedQuery.isBlank() && !selectedSubsection.showsEquipment()) {
		return emptyList()
	}
	return filter(normalizedQuery)
}

private inline fun <T> visibleSearchOnlyEntries(
	searchQuery: String,
	filter: (String) -> List<T>
): List<T> {
	val normalizedQuery = searchQuery.trim()
	if (normalizedQuery.isBlank()) {
		return emptyList()
	}
	return filter(normalizedQuery)
}

// endregion

// region Search matching extensions

internal fun ToolReferenceEntry.matchesSearchQuery(query: String): Boolean {
	return matchesQuery(
		query,
		name,
		category.label,
		ability,
		weight,
		cost,
		*utilize.toTypedArray(),
		*craft.toTypedArray(),
		*variants.toTypedArray(),
		*notes.toTypedArray()
	)
}

internal fun WeaponReferenceEntry.matchesSearchQuery(query: String): Boolean {
	return matchesQuery(
		query,
		name,
		category,
		damage,
		mastery,
		weight,
		cost,
		*properties.toTypedArray()
	)
}

internal fun ArmorReferenceEntry.matchesSearchQuery(query: String): Boolean {
	return matchesQuery(
		query,
		name,
		categoryDonDoff,
		armorClass,
		strength,
		stealth,
		weight,
		cost
	)
}

// endregion

internal fun AdventuringGearEntry.matchesSearchQuery(query: String): Boolean {
	return matchesQuery(query, name, weight, cost, body.orEmpty())
}

internal fun MagicItemReferenceEntry.matchesSearchQuery(query: String): Boolean {
	val tableFields = tables.flatMap { table ->
		listOf(table.title) + table.columns + table.rows.flatten()
	}
	return matchesQuery(query, name, subtitle, body, *tableFields.toTypedArray())
}

internal fun AmmunitionReferenceEntry.matchesSearchQuery(query: String): Boolean {
	return matchesQuery(query, type, amount, storage, weight, cost)
}

internal fun FocusReferenceEntry.matchesSearchQuery(query: String): Boolean {
	return matchesQuery(query, group.label, name, weight, cost, usage, *notes.toTypedArray())
}

internal fun MountReferenceEntry.matchesSearchQuery(query: String): Boolean {
	return matchesQuery(query, item, carryingCapacity, cost)
}

internal fun TackDrawnReferenceEntry.matchesSearchQuery(query: String): Boolean {
	return matchesQuery(query, item, weight, cost)
}

internal fun LargeVehicleReferenceEntry.matchesSearchQuery(query: String): Boolean {
	return matchesQuery(
		query,
		ship,
		speed,
		crew,
		passengers,
		cargoTons,
		ac,
		hp,
		damageThreshold,
		cost
	)
}
