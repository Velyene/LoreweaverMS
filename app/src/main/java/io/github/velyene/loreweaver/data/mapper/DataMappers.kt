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
import com.google.gson.JsonArray
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import io.github.velyene.loreweaver.data.entities.CampaignEntity
import io.github.velyene.loreweaver.data.entities.CharacterEntity
import io.github.velyene.loreweaver.data.entities.EncounterEntity
import io.github.velyene.loreweaver.data.entities.LogEntryEntity
import io.github.velyene.loreweaver.data.entities.SessionEntity
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.CharacterAction
import io.github.velyene.loreweaver.domain.model.CharacterEntry
import io.github.velyene.loreweaver.domain.model.CharacterInventoryState
import io.github.velyene.loreweaver.domain.model.CharacterResource
import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.CampaignInventoryState
import io.github.velyene.loreweaver.domain.model.DurationType
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.model.EncounterDifficultyTarget
import io.github.velyene.loreweaver.domain.model.EncounterGenerationDetails
import io.github.velyene.loreweaver.domain.model.EncounterGenerationSettings
import io.github.velyene.loreweaver.domain.model.EncounterGenerationSourceFilter
import io.github.velyene.loreweaver.domain.model.EncounterRewardSummary
import io.github.velyene.loreweaver.domain.model.EncounterRewardTemplate
import io.github.velyene.loreweaver.domain.model.EncounterSnapshot
import io.github.velyene.loreweaver.domain.model.EncounterStatus
import io.github.velyene.loreweaver.domain.model.LogEntry
import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.model.ParticipantRewardShare
import io.github.velyene.loreweaver.domain.model.RewardReviewState
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.domain.model.normalizeClassName
import io.github.velyene.loreweaver.domain.util.formatCurrencyCp
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
private val combatantListType = object : TypeToken<List<CombatantState>>() {}.type
private val stringListType = object : TypeToken<List<String>>() {}.type
private val participantRewardShareListType = object : TypeToken<List<ParticipantRewardShare>>() {}.type

private fun parseEncounterSnapshot(snapshotJson: String): EncounterSnapshot? {
	return runCatching {
		val snapshotObject = JsonParser.parseString(snapshotJson).asJsonObject
		// Session snapshots have evolved over time. Normalize older payloads before Gson reads them
		// so saved encounters from previous builds still restore into the current domain model.
		normalizeLegacyCombatantSnapshots(snapshotObject)
		gson.fromJson(snapshotObject, EncounterSnapshot::class.java)
	}.getOrNull()
}

private fun parseCombatantsJson(participantsJson: String): List<CombatantState> {
	return runCatching {
		val snapshotObject = JsonObject().apply {
			add("combatants", JsonParser.parseString(participantsJson).asJsonArray)
		}
		normalizeLegacyCombatantSnapshots(snapshotObject)
		gson.fromJson<List<CombatantState>>(snapshotObject.getAsJsonArray("combatants"), combatantListType)
	}.getOrDefault(emptyList())
}

private fun normalizeLegacyCombatantSnapshots(snapshotObject: JsonObject) {
	val combatants = snapshotObject.getAsJsonArray("combatants") ?: return
	combatants.forEach { combatantElement ->
		if (!combatantElement.isJsonObject) return@forEach
		val combatant = combatantElement.asJsonObject
		if (!combatant.has("tempHp")) {
			combatant.addProperty("tempHp", 0)
		}
		normalizeLegacyConditionArray(combatant)
	}
}

private fun normalizeLegacyConditionArray(combatant: JsonObject) {
	val conditionsElement = combatant["conditions"] ?: return
	if (!conditionsElement.isJsonArray) return

	val normalizedConditions = JsonArray()
	conditionsElement.asJsonArray.forEach { conditionElement ->
		when {
			conditionElement.isJsonObject -> normalizedConditions.add(conditionElement)
			conditionElement.isJsonPrimitive -> {
				// Older builds stored conditions as bare names. Rebuild the missing fields with safe
				// defaults so duration handling can treat restored and newly created conditions the same.
				val legacyCondition = JsonObject().apply {
					addProperty("name", conditionElement.asString)
					add("duration", JsonNull.INSTANCE)
					addProperty("durationType", DurationType.ROUNDS.name)
					addProperty("addedOnRound", 0)
				}
				normalizedConditions.add(legacyCondition)
			}
		}
	}
	combatant.add("conditions", normalizedConditions)
}

private fun parseEncounterRewardTemplate(rewardTemplateJson: String?): EncounterRewardTemplate {
	if (rewardTemplateJson.isNullOrBlank()) return EncounterRewardTemplate()
	return runCatching {
		val rewardObject = JsonParser.parseString(rewardTemplateJson).asJsonObject
		EncounterRewardTemplate(
			difficultyTarget = rewardObject["difficultyTarget"]
				?.takeUnless { it.isJsonNull }
				?.asString
				?.let { runCatching { EncounterDifficultyTarget.valueOf(it) }.getOrNull() }
				?: EncounterDifficultyTarget.MODERATE,
			customTargetBudgetXp = rewardObject["customTargetBudgetXp"]
				?.takeUnless { it.isJsonNull }
				?.asInt,
			preloadedCurrencyCp = rewardObject["preloadedCurrencyCp"]
				?.takeUnless { it.isJsonNull }
				?.asInt
				?: 0,
			preloadedLoot = rewardObject["preloadedLoot"]
				?.takeUnless { it.isJsonNull }
				?.let { gson.fromJson<List<String>>(it, stringListType) }
				?: emptyList(),
			specialItemRewards = rewardObject["specialItemRewards"]
				?.takeUnless { it.isJsonNull }
				?.let { gson.fromJson<List<String>>(it, stringListType) }
				?: emptyList(),
			currencyRateCpPerXp = rewardObject["currencyRateCpPerXp"]
				?.takeUnless { it.isJsonNull }
				?.asDouble
				?: 5.0,
			economyMultiplier = rewardObject["economyMultiplier"]
				?.takeUnless { it.isJsonNull }
				?.asDouble
				?: 1.0
		)
	}.getOrDefault(EncounterRewardTemplate())
}

private fun parseEncounterGenerationSettings(generationSettingsJson: String?): EncounterGenerationSettings {
	if (generationSettingsJson.isNullOrBlank()) return EncounterGenerationSettings()
	return runCatching {
		val settingsObject = JsonParser.parseString(generationSettingsJson).asJsonObject
		EncounterGenerationSettings(
			difficultyTarget = settingsObject["difficultyTarget"]
				?.takeUnless { it.isJsonNull }
				?.asString
				?.let { runCatching { EncounterDifficultyTarget.valueOf(it) }.getOrNull() }
				?: EncounterDifficultyTarget.MODERATE,
			customTargetXp = settingsObject["customTargetXp"]?.takeUnless { it.isJsonNull }?.asInt,
			minimumTargetPercent = settingsObject["minimumTargetPercent"]?.takeUnless { it.isJsonNull }?.asInt ?: 100,
			maximumTargetPercent = settingsObject["maximumTargetPercent"]?.takeUnless { it.isJsonNull }?.asInt ?: 105,
			allowSingleHighCrEnemy = settingsObject["allowSingleHighCrEnemy"]?.takeUnless { it.isJsonNull }?.asBoolean ?: false,
			maximumEnemyCr = settingsObject["maximumEnemyCr"]?.takeUnless { it.isJsonNull }?.asDouble,
			allowDuplicateEnemies = settingsObject["allowDuplicateEnemies"]?.takeUnless { it.isJsonNull }?.asBoolean ?: true,
			maximumEnemyQuantity = settingsObject["maximumEnemyQuantity"]?.takeUnless { it.isJsonNull }?.asInt,
			creatureTypeFilter = settingsObject["creatureTypeFilter"]?.takeUnless { it.isJsonNull }?.asString,
			groupFilter = settingsObject["groupFilter"]?.takeUnless { it.isJsonNull }?.asString,
			sourceFilter = settingsObject["sourceFilter"]
				?.takeUnless { it.isJsonNull }
				?.asString
				?.let { runCatching { EncounterGenerationSourceFilter.valueOf(it) }.getOrNull() }
				?: EncounterGenerationSourceFilter.SRD_ONLY
		)
	}.getOrDefault(EncounterGenerationSettings())
}

private fun parseEncounterGenerationDetails(generationDetailsJson: String?): EncounterGenerationDetails? {
	if (generationDetailsJson.isNullOrBlank()) return null
	return runCatching {
		gson.fromJson(generationDetailsJson, EncounterGenerationDetails::class.java)
	}.getOrNull()
}

private fun parseEncounterRewardSummary(rewardsJson: String?): EncounterRewardSummary? {
	if (rewardsJson.isNullOrBlank()) return null
	return runCatching {
		val rewardObject = JsonParser.parseString(rewardsJson).asJsonObject
		val totalCurrencyCp = rewardObject["totalCurrencyCp"]
			?.takeUnless { it.isJsonNull }
			?.asInt
			?: 0
		val currencyPerParticipantCp = rewardObject["currencyPerParticipantCp"]
			?.takeUnless { it.isJsonNull }
			?.asInt
			?: 0
		EncounterRewardSummary(
			experiencePoints = rewardObject["experiencePoints"]
				?.takeUnless { it.isJsonNull }
				?.asInt
				?: 0,
			experiencePerParticipant = rewardObject["experiencePerParticipant"]
				?.takeUnless { it.isJsonNull }
				?.asInt
				?: 0,
			experienceRoundingSurplus = rewardObject["experienceRoundingSurplus"]
				?.takeUnless { it.isJsonNull }
				?.asInt
				?: 0,
			participantCount = rewardObject["participantCount"]
				?.takeUnless { it.isJsonNull }
				?.asInt
				?: 0,
			participantRewards = rewardObject["participantRewards"]
				?.takeUnless { it.isJsonNull }
				?.let { gson.fromJson<List<ParticipantRewardShare>>(it, participantRewardShareListType) }
				?: emptyList(),
			currencyReward = rewardObject["currencyReward"]
				?.takeUnless { it.isJsonNull }
				?.asString
				?: totalCurrencyCp.takeIf { it > 0 }?.let(::formatCurrencyCp),
			currencyPerParticipant = rewardObject["currencyPerParticipant"]
				?.takeUnless { it.isJsonNull }
				?.asString
				?: currencyPerParticipantCp.takeIf { it > 0 }?.let(::formatCurrencyCp),
			totalCurrencyCp = totalCurrencyCp,
			currencyPerParticipantCp = currencyPerParticipantCp,
			currencyRoundingSurplusCp = rewardObject["currencyRoundingSurplusCp"]
				?.takeUnless { it.isJsonNull }
				?.asInt
				?: 0,
			itemRewards = rewardObject["itemRewards"]
				?.takeUnless { it.isJsonNull }
				?.let { gson.fromJson<List<String>>(it, stringListType) }
				?: emptyList(),
			equipmentRewards = rewardObject["equipmentRewards"]
				?.takeUnless { it.isJsonNull }
				?.let { gson.fromJson<List<String>>(it, stringListType) }
				?: emptyList(),
			skillPoints = rewardObject["skillPoints"]
				?.takeUnless { it.isJsonNull }
				?.asInt
				?: 0,
			storyRewards = rewardObject["storyRewards"]
				?.takeUnless { it.isJsonNull }
				?.let { gson.fromJson<List<String>>(it, stringListType) }
				?: emptyList(),
			rewardLog = rewardObject["rewardLog"]
				?.takeUnless { it.isJsonNull }
				?.let { gson.fromJson<List<String>>(it, stringListType) }
				?: emptyList()
		)
	}.getOrNull()
}

private fun parseCharacterInventoryState(
	inventoryStateJson: String?,
	legacyInventory: List<String>
): CharacterInventoryState {
	if (inventoryStateJson.isNullOrBlank()) {
		return CharacterInventoryState(
			personalInventory = legacyInventory.map { io.github.velyene.loreweaver.domain.model.InventoryItem(name = it) }
		)
	}
	return runCatching {
		gson.fromJson(inventoryStateJson, CharacterInventoryState::class.java)
	}.getOrDefault(
		CharacterInventoryState(
			personalInventory = legacyInventory.map { io.github.velyene.loreweaver.domain.model.InventoryItem(name = it) }
		)
	)
}

private fun parseCampaignInventoryState(inventoryStateJson: String?): CampaignInventoryState {
	if (inventoryStateJson.isNullOrBlank()) return CampaignInventoryState()
	return runCatching {
		gson.fromJson(inventoryStateJson, CampaignInventoryState::class.java)
	}.getOrDefault(CampaignInventoryState())
}

private fun parseRewardReviewState(rewardReviewJson: String?): RewardReviewState? {
	if (rewardReviewJson.isNullOrBlank()) return null
	return runCatching {
		gson.fromJson(rewardReviewJson, RewardReviewState::class.java)
	}.getOrNull()
}

private fun normalizedCharacterTypeForDomain(
	type: String,
	party: String,
	isPlayerCharacter: Boolean
): String {
	val trimmedType = type.trim()
	return if (party == CharacterParty.ADVENTURERS || isPlayerCharacter) {
		normalizeClassName(trimmedType)
	} else {
		trimmedType.ifBlank { "Monster" }
	}
}

private fun normalizedCharacterTypeForStorage(type: String, party: String): String {
	val trimmedType = type.trim()
	return if (party == CharacterParty.ADVENTURERS) {
		normalizeClassName(trimmedType)
	} else {
		trimmedType.ifBlank { "Monster" }
	}
}

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
		description = description,
		inventoryState = parseCampaignInventoryState(inventoryStateJson)
	)
}

fun Campaign.toEntity(): CampaignEntity {
	return CampaignEntity(
		id = id,
		name = title,
		description = description,
		inventoryStateJson = gson.toJson(inventoryState)
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
		status = if (isActive) EncounterStatus.ACTIVE else EncounterStatus.PENDING,
		participants = parseCombatantsJson(participantsJson),
		activeLog = gson.fromJson(activeLogJson, stringListType) ?: emptyList(),
		rewardTemplate = parseEncounterRewardTemplate(rewardTemplateJson),
		generationSettings = parseEncounterGenerationSettings(generationSettingsJson),
		generationDetails = parseEncounterGenerationDetails(generationDetailsJson)
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
		currentTurnIndex = currentTurnIndex,
		participantsJson = gson.toJson(participants),
		activeLogJson = gson.toJson(activeLog),
		rewardTemplateJson = gson.toJson(rewardTemplate),
		generationSettingsJson = gson.toJson(generationSettings),
		generationDetailsJson = generationDetails?.let(gson::toJson)
	)
}

fun CharacterEntity.toDomain(): CharacterEntry {
	return CharacterEntry(
		id = id,
		name = name,
		type = normalizedCharacterTypeForDomain(type = type, party = party, isPlayerCharacter = isPlayerCharacter),
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
		experiencePoints = experiencePoints,
		saveProficiencies = saveProficiencies,
		resources = gson.fromJson(resourcesJson, resourceListType),
		hitDieType = hitDieType,
		hitDiceCurrent = hitDiceCurrent,
			persistentConditions = persistentConditions,
		activeConditions = activeConditions,
		actions = gson.fromJson(actionsJson, actionListType),
		proficiencies = proficiencies,
		inventory = inventory,
		inventoryState = parseCharacterInventoryState(inventoryStateJson, inventory),
		hasInspiration = hasInspiration,
		// Spell slots are persisted as simple integer lists for stable JSON round-tripping, then
		// reconstructed into the domain-friendly Pair<current, max> shape on read.
		spellSlots = gson.fromJson<Map<Int, List<Int>>>(
			spellSlotsJson,
			spellSlotsMapType
		)?.mapValues { (it.value.getOrElse(0) { 0 }) to (it.value.getOrElse(1) { 0 }) }
			?: emptyMap(),
		species = species,
		background = background,
		spells = spells
	)
}

fun CharacterEntry.toEntity(): CharacterEntity {
	return CharacterEntity(
		id = id,
		name = name,
		type = normalizedCharacterTypeForStorage(type = type, party = party),
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
		experiencePoints = experiencePoints,
		saveProficiencies = saveProficiencies,
		resourcesJson = gson.toJson(resources),
		hitDieType = hitDieType,
		hitDiceCurrent = hitDiceCurrent,
			persistentConditions = persistentConditions,
		activeConditions = activeConditions,
		actionsJson = gson.toJson(actions),
		proficiencies = proficiencies,
		inventory = inventory,
		inventoryStateJson = gson.toJson(inventoryState),
		isPlayerCharacter = party == CharacterParty.ADVENTURERS,
		hasInspiration = hasInspiration,
		// Persist spell slots as raw lists instead of Kotlin Pair JSON so older snapshots and Room
		// rows keep a stable, library-agnostic wire shape.
		spellSlotsJson = gson.toJson(spellSlots.mapValues {
			listOf(
				it.value.first,
				it.value.second
			)
		}),
		species = species,
		background = background,
		spells = spells
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
		snapshot = snapshotJson?.let(::parseEncounterSnapshot),
		reuseFlag = reuseFlag,
		isCompleted = isCompleted,
		encounterResult = encounterResult,
		rewards = parseEncounterRewardSummary(rewardsJson),
		rewardReview = parseRewardReviewState(rewardReviewJson)
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
		reuseFlag = reuseFlag,
		isCompleted = isCompleted,
		encounterResult = encounterResult,
		rewardsJson = rewards?.let(gson::toJson),
		rewardReviewJson = rewardReview?.let(gson::toJson)
	)
}
