package io.github.velyene.loreweaver.ui.screens

import androidx.annotation.StringRes
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.ClassInfo
import io.github.velyene.loreweaver.domain.util.CharacterCreationReference

internal enum class CharacterBuilderSection(
	@param:StringRes val titleResId: Int,
	@param:StringRes val descriptionResId: Int,
) {
	IDENTITY(
		R.string.character_builder_identity_title,
		R.string.character_builder_identity_description,
	),
	CORE_STATS(
		R.string.character_builder_core_stats_title,
		R.string.character_builder_core_stats_description,
	),
	BUILD_SUGGESTIONS(
		R.string.character_builder_build_suggestions_title,
		R.string.character_builder_build_suggestions_description,
	),
	STATUS_AND_NOTES(
		R.string.character_builder_status_notes_title,
		R.string.character_builder_status_notes_description,
	),
	REVIEW_AND_SAVE(
		R.string.character_builder_review_save_title,
		R.string.character_builder_review_save_description,
	),
}

private val characterBuilderSections = CharacterBuilderSection::class.java.enumConstants.orEmpty().toList()

internal fun CharacterBuilderSection.previous(): CharacterBuilderSection? {
	val previousIndex = ordinal - 1
	return characterBuilderSections.getOrNull(previousIndex)
}

internal fun CharacterBuilderSection.next(): CharacterBuilderSection? {
	return characterBuilderSections.getOrNull(ordinal + 1)
}

internal data class CharacterBuildSuggestions(
	val recommendedAbilities: List<String>,
	val suggestedSpells: List<String>,
	val suggestedEquipment: List<String>,
	val backgroundEquipment: List<String>,
	val guidance: List<String>,
)

private const val EXPLORERS_PACK = "Explorer's Pack"
private const val LEATHER_ARMOR = "Leather Armor"

private val SPELL_SUGGESTIONS_BY_CLASS = mapOf(
	"Bard" to listOf("Healing Word", "Dissonant Whispers", "Faerie Fire"),
	"Cleric" to listOf("Bless", "Cure Wounds", "Guiding Bolt"),
	"Druid" to listOf("Entangle", "Faerie Fire", "Healing Word"),
	"Paladin" to listOf("Bless", "Shield of Faith", "Wrathful Smite"),
	"Ranger" to listOf("Hunter's Mark", "Cure Wounds", "Ensnaring Strike"),
	"Sorcerer" to listOf("Magic Missile", "Shield", "Chromatic Orb"),
	"Warlock" to listOf("Hex", "Armor of Agathys", "Hellish Rebuke"),
	"Wizard" to listOf("Magic Missile", "Shield", "Sleep"),
)

private val EQUIPMENT_SUGGESTIONS_BY_CLASS = mapOf(
	"Barbarian" to listOf("Greataxe", "Handaxes", EXPLORERS_PACK),
	"Bard" to listOf("Rapier", "Lute", LEATHER_ARMOR),
	"Cleric" to listOf("Mace", "Shield", "Holy Symbol"),
	"Druid" to listOf("Quarterstaff", LEATHER_ARMOR, "Druidic Focus"),
	"Fighter" to listOf("Chain Mail", "Longsword", "Shield"),
	"Monk" to listOf("Quarterstaff", "Darts", EXPLORERS_PACK),
	"Paladin" to listOf("Chain Mail", "Shield", "Holy Symbol"),
	"Ranger" to listOf("Longbow", "Shortswords", EXPLORERS_PACK),
	"Rogue" to listOf("Rapier", "Shortbow", "Thieves' Tools"),
	"Sorcerer" to listOf("Light Crossbow", "Arcane Focus", "Dagger"),
	"Warlock" to listOf("Light Crossbow", "Arcane Focus", LEATHER_ARMOR),
	"Wizard" to listOf("Quarterstaff", "Spellbook", "Component Pouch"),
)

internal fun buildCharacterSuggestions(
	classInfo: ClassInfo?,
	backgroundName: String,
): CharacterBuildSuggestions {
	val normalizedClassName = classInfo?.displayName.orEmpty()
	val backgroundReference = CharacterCreationReference.BACKGROUNDS.firstOrNull {
		it.name.equals(backgroundName.trim(), ignoreCase = true)
	}
	val recommendedAbilities = classInfo?.primaryStats.orEmpty()
	val suggestedSpells = SPELL_SUGGESTIONS_BY_CLASS[normalizedClassName].orEmpty()
	val suggestedEquipment = EQUIPMENT_SUGGESTIONS_BY_CLASS[normalizedClassName]
		?: listOf(EXPLORERS_PACK, "Potion of Healing", "Backup weapon")
	val guidance = buildList {
		if (classInfo != null) {
			add("d${classInfo.hitDie} hit die")
			if (classInfo.defaultSaveProficiencies.isNotEmpty()) {
				add(classInfo.defaultSaveProficiencies.joinToString(prefix = "Save proficiencies: "))
			}
			if (classInfo.defaultSpellSlotsL1.isNotEmpty()) {
				add(classInfo.defaultSpellSlotsL1.entries.joinToString(prefix = "Starting spell slots: ") {
					"L${it.key} x${it.value}"
				})
			}
		}
		backgroundReference?.let {
			add("Background feat: ${it.feat}")
			add(it.skillProficiencies.joinToString(prefix = "Background skills: "))
		}
	}
	return CharacterBuildSuggestions(
		recommendedAbilities = recommendedAbilities,
		suggestedSpells = suggestedSpells,
		suggestedEquipment = suggestedEquipment,
		backgroundEquipment = backgroundReference?.equipmentOptions.orEmpty(),
		guidance = guidance,
	)
}

