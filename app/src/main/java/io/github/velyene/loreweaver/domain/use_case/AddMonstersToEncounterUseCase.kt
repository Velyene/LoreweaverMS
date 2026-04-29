package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CombatRules
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Condition
import io.github.velyene.loreweaver.domain.model.RemoteItem
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
import io.github.velyene.loreweaver.domain.repository.CharactersRepository
import io.github.velyene.loreweaver.domain.repository.EncountersRepository
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.domain.util.MonsterReferenceCatalog
import io.github.velyene.loreweaver.domain.util.MonsterReferenceEntry
import io.github.velyene.loreweaver.domain.util.parseMonsterChallengeRatingValue
import javax.inject.Inject

/**
 * Use case for adding selected monsters to an encounter.
 * When a selected item resolves to the local monster corpus, its HP, AC, CR, speed, and creature
 * type are persisted as a monster CharacterEntry so combat difficulty and later encounter sessions
 * can use the same underlying stats.
 */
class AddMonstersToEncounterUseCase @Inject constructor(
	private val encountersRepository: EncountersRepository,
	private val charactersRepository: CharactersRepository
) {
	constructor(repository: CampaignRepository) : this(
		repository as EncountersRepository,
		repository as CharactersRepository
	)

	/**
	 * Adds selected monsters to an encounter.
	 * @param encounterId The ID of the encounter to add monsters to
	 * @param selectedMonsters List of monsters selected from the library
	 */
	suspend operator fun invoke(
		encounterId: String,
		selectedMonsters: List<RemoteItem>
	) {
		if (selectedMonsters.isEmpty()) return

		val combatants = selectedMonsters.map { monster ->
			val character = monster.toMonsterCharacterEntry()
			charactersRepository.insertCharacter(character)
			character.toCombatantState()
		}

		encountersRepository.addCombatantsToEncounter(encounterId, combatants)
	}

	private fun RemoteItem.toMonsterCharacterEntry(): CharacterEntry {
		val referenceEntry = MonsterReferenceCatalog.findEntry(id)
			?: MonsterReferenceCatalog.findEntry(name)

		return referenceEntry?.toCharacterEntry()
			?: CharacterEntry(
				name = name,
				type = detail.ifBlank { DEFAULT_MONSTER_TYPE },
				party = CharacterParty.MONSTERS,
				hp = DEFAULT_MONSTER_HP,
				maxHp = DEFAULT_MONSTER_HP,
				ac = DEFAULT_MONSTER_AC,
				speed = DEFAULT_MONSTER_SPEED,
				initiative = DEFAULT_MONSTER_INITIATIVE_MODIFIER,
				notes = fullDescription.ifBlank { detail }
			)
	}

	private fun MonsterReferenceEntry.toCharacterEntry(): CharacterEntry {
		val hpValue = statValue(STAT_HP).leadingIntOr(defaultValue = DEFAULT_MONSTER_HP)
		return CharacterEntry(
			name = name,
			type = creatureType.ifBlank { subtitle.substringBefore(',').trim().ifBlank { DEFAULT_MONSTER_TYPE } },
			party = CharacterParty.MONSTERS,
			hp = hpValue,
			maxHp = hpValue,
			ac = statValue(STAT_AC).leadingIntOr(defaultValue = DEFAULT_MONSTER_AC),
			speed = statValue(STAT_SPEED).leadingIntOr(defaultValue = DEFAULT_MONSTER_SPEED),
			initiative = statValue(STAT_INITIATIVE).leadingSignedIntOrNull() ?: DEFAULT_MONSTER_INITIATIVE_MODIFIER,
			challengeRating = parseMonsterChallengeRatingValue(challengeRating),
			notes = buildList {
				add(subtitle)
				group?.takeIf { it.isNotBlank() }?.let { add(it) }
			}.joinToString(" • ")
		)
	}

	private fun CharacterEntry.toCombatantState(): CombatantState {
		return CombatantState(
			characterId = id,
			name = name,
			initiative = CombatRules.rollInitiative(initiative),
			currentHp = hp,
			maxHp = maxHp,
			conditions = emptyList<Condition>(),
			tempHp = tempHp
		)
	}

	private fun MonsterReferenceEntry.statValue(label: String): String {
		return statRows.firstOrNull { (statLabel, _) -> statLabel == label }?.second.orEmpty()
	}

	private fun String.leadingIntOr(defaultValue: Int): Int {
		return Regex("\\d+").find(this)?.value?.toIntOrNull() ?: defaultValue
	}

	private fun String.leadingSignedIntOrNull(): Int? {
		return Regex("[+-]?\\d+").find(this)?.value?.toIntOrNull()
	}

	private companion object {
		const val DEFAULT_MONSTER_TYPE = "Monster"
		const val DEFAULT_MONSTER_HP = 50
		const val DEFAULT_MONSTER_AC = 10
		const val DEFAULT_MONSTER_INITIATIVE_MODIFIER = 0
		const val DEFAULT_MONSTER_SPEED = 30
		const val STAT_AC = "AC"
		const val STAT_INITIATIVE = "Initiative"
		const val STAT_HP = "HP"
		const val STAT_SPEED = "Speed"
	}
}
