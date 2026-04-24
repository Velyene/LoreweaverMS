package io.github.velyene.loreweaver.domain.util

enum class ToolCategory(val label: String) {
	ARTISANS("Artisan's Tools"),
	OTHER("Other Tools")
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

enum class FocusGroup(val label: String) {
	ARCANE("Arcane Focus"),
	DRUIDIC("Druidic Focus"),
	HOLY_SYMBOL("Holy Symbol")
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
