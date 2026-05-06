package io.github.velyene.loreweaver.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.velyene.loreweaver.domain.util.CharacterParty
import java.util.UUID

@Entity(tableName = "characters")
data class CharacterEntity(
	@PrimaryKey val id: String = UUID.randomUUID().toString(),
	val name: String,
	val type: String,
	val hp: Int,
	val maxHp: Int,
	val tempHp: Int = 0,
	val mana: Int = 0,
	val maxMana: Int = 0,
	val stamina: Int = 0,
	val maxStamina: Int = 0,
	val ac: Int,
	val speed: Int = 30,
	val initiative: Int = 0,
	val level: Int = 1,
	val challengeRating: Double = 0.0,
	val strength: Int = 10,
	val dexterity: Int = 10,
	val constitution: Int = 10,
	val intelligence: Int = 10,
	val wisdom: Int = 10,
	val charisma: Int = 10,
	val notes: String = "",
	val party: String = CharacterParty.ADVENTURERS,
	val status: String = "",
	val deathSaveSuccesses: Int = 0,
	val deathSaveFailures: Int = 0,
	val experiencePoints: Int = 0,
	val saveProficiencies: Set<String> = emptySet(),
	val resourcesJson: String = "[]",
	val hitDieType: Int = 8,
	val hitDiceCurrent: Int = 1,
	val persistentConditions: Set<String> = emptySet(),
	val activeConditions: Set<String> = emptySet(),
	val actionsJson: String = "[]",
	val proficiencies: Set<String> = emptySet(),
	val inventory: List<String> = emptyList(),
	val inventoryStateJson: String = "{}",
	val isPlayerCharacter: Boolean = false,
	val hasInspiration: Boolean = false,
	val spellSlotsJson: String = "{}",
	val species: String = "",
	val background: String = "",
	val spells: List<String> = emptyList(),
	val createdAt: Long = System.currentTimeMillis()
)
