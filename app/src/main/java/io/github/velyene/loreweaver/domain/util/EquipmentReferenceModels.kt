package io.github.velyene.loreweaver.domain.util

import androidx.annotation.StringRes
import io.github.velyene.loreweaver.R

enum class ToolCategory(val canonicalLabel: String, @param:StringRes val labelResId: Int) {
	ARTISANS("Artisan's Tools", R.string.reference_character_creation_tool_category_artisans),
	OTHER("Other Tools", R.string.reference_character_creation_tool_category_other)
}

data class ToolReferenceEntry(
	val name: String,
	val category: ToolCategory,
	val ability: String,
	val weight: String,
	val cost: String,
	val utilize: List<String>,
	val craft: List<String> = emptyList(),
	val variants: List<String> = emptyList(),
	val notes: List<String> = emptyList()
)

data class WeaponReferenceEntry(
	val name: String,
	val category: String,
	val damage: String,
	val properties: List<String>,
	val mastery: String,
	val weight: String,
	val cost: String
)

data class ArmorReferenceEntry(
	val name: String,
	val categoryDonDoff: String,
	val armorClass: String,
	val strength: String,
	val stealth: String,
	val weight: String,
	val cost: String
)

data class AdventuringGearEntry(
	val name: String,
	val weight: String,
	val cost: String,
	val body: String? = null
)

data class MagicItemReferenceEntry(
	val name: String,
	val subtitle: String,
	val body: String,
	val tables: List<ReferenceTable> = emptyList()
)

data class AmmunitionReferenceEntry(
	val type: String,
	val amount: String,
	val storage: String,
	val weight: String,
	val cost: String
)

enum class FocusGroup(val canonicalLabel: String, @param:StringRes val labelResId: Int) {
	ARCANE("Arcane Focus", R.string.reference_character_creation_focus_group_arcane),
	DRUIDIC("Druidic Focus", R.string.reference_character_creation_focus_group_druidic),
	HOLY_SYMBOL("Holy Symbol", R.string.reference_character_creation_focus_group_holy_symbol)
}

data class FocusReferenceEntry(
	val group: FocusGroup,
	val name: String,
	val weight: String,
	val cost: String,
	val usage: String,
	val notes: List<String> = emptyList()
)

data class MountReferenceEntry(
	val item: String,
	val carryingCapacity: String,
	val cost: String
)

data class TackDrawnReferenceEntry(
	val item: String,
	val weight: String,
	val cost: String
)

data class LargeVehicleReferenceEntry(
	val ship: String,
	val speed: String,
	val crew: String,
	val passengers: String,
	val cargoTons: String,
	val ac: String,
	val hp: String,
	val damageThreshold: String,
	val cost: String
)
