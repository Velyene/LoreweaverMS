/*
 * FILE: DataMappers.kt
 *
 * TABLE OF CONTENTS:
 * 1. Campaign Mappers
 * 2. Encounter Mappers
 * 3. Character Mappers
 * 4. Note Mappers
 * 5. Session Mappers
 * 6. Log Mappers
 */

package io.github.velyene.loreweaver.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.velyene.loreweaver.data.entities.CampaignEntity
import io.github.velyene.loreweaver.data.entities.CharacterEntity
import io.github.velyene.loreweaver.data.entities.EncounterEntity
import io.github.velyene.loreweaver.data.entities.LogEntryEntity
import io.github.velyene.loreweaver.data.entities.SessionEntity
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterAction
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CharacterResource
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.model.normalizeClassName
import io.github.velyene.loreweaver.domain.util.CharacterParty

/**
 * Shared Gson instance to avoid creating multiple instances.
 * Addresses SonarQube S1118 (utility class) and performance concerns.
 */
private val gson = Gson()

/**
 * Cached TypeToken instances to avoid repeated reflection.
 * Addresses SonarQube S1185 (inefficient type tokens) and performance.
 */
private val resourceListType = object : TypeToken<List<CharacterResource>>() {}.type
private val actionListType = object : TypeToken<List<CharacterAction>>() {}.type
private val spellSlotsMapType = object : TypeToken<Map<Int, List<Int>>>() {}.type

/**
 * Converts a LogEntryEntity from the database to a domain LogEntry model.
 * @return Domain model representation of the log entry
 */
fun LogEntryEntity.toDomain(): LogEntry {
	return LogEntry(
		id = id,
		timestamp = timestamp,
		message = message,
		type = type
	)
}

/**
 * Converts a domain LogEntry model to a database LogEntryEntity.
 * @return Database entity representation of the log entry
 */
fun LogEntry.toEntity(): LogEntryEntity {
	return LogEntryEntity(
		id = id,
		timestamp = timestamp,
		message = message,
		type = type
	)
}

fun CampaignEntity.toDomain(): Campaign {
	return Campaign(
		id = id,
		title = name,
		description = description
	)
}

fun Campaign.toEntity(): CampaignEntity {
	return CampaignEntity(
		id = id,
		name = title,
		description = description
	)
}

fun EncounterEntity.toDomain(): Encounter {
	return Encounter(
		id = id,
		campaignId = campaignId,
		name = name,
		notes = notes,
		currentRound = currentRound,
		currentTurnIndex = currentTurnIndex,
		status = if (isActive) EncounterStatus.ACTIVE else EncounterStatus.PENDING
	)
}

fun Encounter.toEntity(): EncounterEntity {
	return EncounterEntity(
		id = id,
		campaignId = campaignId ?: "",
		name = name,
		notes = notes,
		isActive = status == EncounterStatus.ACTIVE,
		currentRound = currentRound,
		currentTurnIndex = currentTurnIndex
	)
}

fun CharacterEntity.toDomain(): CharacterEntry {
	return CharacterEntry(
		id = id,
		name = name,
		type = normalizeClassName(type),
		hp = hp,
		maxHp = maxHp,
		tempHp = tempHp,
		mana = mana,
		maxMana = maxMana,
		stamina = stamina,
		maxStamina = maxStamina,
		ac = ac,
		speed = speed,
		initiative = initiative,
		level = level,
		challengeRating = challengeRating,
		strength = strength,
		dexterity = dexterity,
		constitution = constitution,
		intelligence = intelligence,
		wisdom = wisdom,
		charisma = charisma,
		notes = notes,
		party = party,
		status = status,
		deathSaveSuccesses = deathSaveSuccesses,
		deathSaveFailures = deathSaveFailures,
		saveProficiencies = saveProficiencies,
		resources = gson.fromJson(resourcesJson, resourceListType),
		hitDieType = hitDieType,
		hitDiceCurrent = hitDiceCurrent,
		activeConditions = activeConditions,
		actions = gson.fromJson(actionsJson, actionListType),
		proficiencies = proficiencies,
		inventory = inventory,
		hasInspiration = hasInspiration,
		spellSlots = gson.fromJson<Map<Int, List<Int>>>(
			spellSlotsJson,
			spellSlotsMapType
		)?.mapValues { (it.value.getOrElse(0) { 0 }) to (it.value.getOrElse(1) { 0 }) }
			?: emptyMap()
	)
}

fun CharacterEntry.toEntity(): CharacterEntity {
	return CharacterEntity(
		id = id,
		name = name,
		type = normalizeClassName(type),
		hp = hp,
		maxHp = maxHp,
		tempHp = tempHp,
		mana = mana,
		maxMana = maxMana,
		stamina = stamina,
		maxStamina = maxStamina,
		ac = ac,
		speed = speed,
		initiative = initiative,
		level = level,
		strength = strength,
		dexterity = dexterity,
		constitution = constitution,
		intelligence = intelligence,
		wisdom = wisdom,
		charisma = charisma,
		notes = notes,
		party = party,
		status = status,
		deathSaveSuccesses = deathSaveSuccesses,
		deathSaveFailures = deathSaveFailures,
		saveProficiencies = saveProficiencies,
		resourcesJson = gson.toJson(resources),
		hitDieType = hitDieType,
		hitDiceCurrent = hitDiceCurrent,
		activeConditions = activeConditions,
		actionsJson = gson.toJson(actions),
		proficiencies = proficiencies,
		inventory = inventory,
		isPlayerCharacter = party == CharacterParty.ADVENTURERS,
		hasInspiration = hasInspiration,
		spellSlotsJson = gson.toJson(spellSlots.mapValues {
			listOf(
				it.value.first,
				it.value.second
			)
		})
	)
}

fun io.github.velyene.loreweaver.data.entities.NoteEntity.toDomain(): Note {
	return when (subtype) {
		"Lore" -> Note.Lore(
			id = id,
			campaignId = campaignId,
			content = content,
			createdAt = createdAt,
			historicalEra = historicalEra ?: ""
		)

		"NPC" -> Note.NPC(
			id = id,
			campaignId = campaignId,
			content = content,
			createdAt = createdAt,
			faction = faction ?: "",
			attitude = attitude ?: "Neutral"
		)

		"Location" -> Note.Location(
			id = id,
			campaignId = campaignId,
			content = content,
			createdAt = createdAt,
			region = region ?: ""
		)

		else -> Note.General(
			id = id,
			campaignId = campaignId,
			content = content,
			createdAt = createdAt
		)
	}
}

fun Note.toEntity(): io.github.velyene.loreweaver.data.entities.NoteEntity {
	return when (this) {
		is Note.General -> io.github.velyene.loreweaver.data.entities.NoteEntity(
			id = id,
			campaignId = campaignId ?: "",
			subtype = "General",
			content = content,
			createdAt = createdAt
		)

		is Note.Lore -> io.github.velyene.loreweaver.data.entities.NoteEntity(
			id = id,
			campaignId = campaignId ?: "",
			subtype = "Lore",
			content = content,
			createdAt = createdAt,
			historicalEra = historicalEra
		)

		is Note.NPC -> io.github.velyene.loreweaver.data.entities.NoteEntity(
			id = id,
			campaignId = campaignId ?: "",
			subtype = "NPC",
			content = content,
			createdAt = createdAt,
			faction = faction,
			attitude = attitude
		)

		is Note.Location -> io.github.velyene.loreweaver.data.entities.NoteEntity(
			id = id,
			campaignId = campaignId ?: "",
			subtype = "Location",
			content = content,
			createdAt = createdAt,
			region = region
		)
	}
}


fun SessionEntity.toDomain(): SessionRecord {
	return SessionRecord(
		id = id,
		encounterId = encounterId,
		title = title,
		date = date,
		log = gson.fromJson(logJson, Array<String>::class.java).toList(),
		snapshot = snapshotJson?.let { gson.fromJson(it, EncounterSnapshot::class.java) },
		reuseFlag = reuseFlag
	)
}

fun SessionRecord.toEntity(): SessionEntity {
	return SessionEntity(
		id = id,
		encounterId = encounterId,
		title = title,
		date = date,
		logJson = gson.toJson(log),
		snapshotJson = snapshot?.let { gson.toJson(it) },
		reuseFlag = reuseFlag
	)
}
