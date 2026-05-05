/*
 * FILE: ReferenceScreenCharacterCreationEquipmentSupport.kt
 *
 * TABLE OF CONTENTS:
 * 1. Equipment Section State Models
 * 2. Equipment Filtering Helpers
 * 3. Equipment Navigation Helpers
 */

package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.AdventuringGearEntry
import io.github.velyene.loreweaver.domain.util.AmmunitionReferenceEntry
import io.github.velyene.loreweaver.domain.util.ArmorReferenceEntry
import io.github.velyene.loreweaver.domain.util.EquipmentReference
import io.github.velyene.loreweaver.domain.util.FocusReferenceEntry
import io.github.velyene.loreweaver.domain.util.LargeVehicleReferenceEntry
import io.github.velyene.loreweaver.domain.util.MagicItemReferenceEntry
import io.github.velyene.loreweaver.domain.util.MountReferenceEntry
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.domain.util.ReferenceTable
import io.github.velyene.loreweaver.domain.util.TackDrawnReferenceEntry
import io.github.velyene.loreweaver.domain.util.ToolReferenceEntry
import io.github.velyene.loreweaver.domain.util.WeaponReferenceEntry

internal fun openCharacterCreationEquipmentDetail(
	onOpenDetail: (String, String) -> Unit,
	category: String,
	name: String
) {
	onOpenDetail(category, ReferenceDetailResolver.slugFor(name))
}

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

internal fun ToolReferenceEntry.matchesSearchQuery(query: String): Boolean {
	return matchesQuery(
		query,
		name,
		category.canonicalLabel,
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
	return matchesQuery(query, group.canonicalLabel, name, weight, cost, usage, *notes.toTypedArray())
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
