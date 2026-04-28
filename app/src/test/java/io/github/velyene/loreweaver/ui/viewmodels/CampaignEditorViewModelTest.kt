package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.MainDispatcherRule
import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.model.RemoteItem
import io.github.velyene.loreweaver.domain.use_case.ValidationMessages.CAMPAIGN_NAME_EMPTY_MESSAGE
import io.github.velyene.loreweaver.domain.use_case.ValidationMessages.ENCOUNTER_NAME_EMPTY_MESSAGE
import io.github.velyene.loreweaver.domain.use_case.ValidationMessages.NOTE_CONTENT_EMPTY_MESSAGE
import io.github.velyene.loreweaver.domain.use_case.ValidationMessages.NOTE_LOCATION_REGION_EMPTY_MESSAGE
import io.github.velyene.loreweaver.domain.use_case.ValidationMessages.NOTE_LORE_HISTORICAL_ERA_EMPTY_MESSAGE
import io.github.velyene.loreweaver.domain.use_case.ValidationMessages.NOTE_NPC_FACTION_EMPTY_MESSAGE
import io.github.velyene.loreweaver.domain.util.CharacterParty
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.ui.util.NOTE_TYPE_LOCATION
import io.github.velyene.loreweaver.ui.util.NOTE_TYPE_LORE
import io.github.velyene.loreweaver.ui.util.NOTE_TYPE_NPC
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CampaignEditorViewModelTest {

	private companion object {
		const val CAMPAIGN_ID = "campaign-1"
	}

	@get:Rule
	val mainDispatcherRule = MainDispatcherRule()

	@Test
	fun addCampaign_setsValidationMessage_whenNameIsBlank() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)

			viewModel.addCampaign(name = "\t", description = "Harbor intrigue")
			advanceUntilIdle()

			assertEquals(
				"$CAMPAIGN_EDITOR_ADD_CAMPAIGN_ERROR_PREFIX: $CAMPAIGN_NAME_EMPTY_MESSAGE",
				viewModel.uiState.value.message
			)
		}
	}

	@Test
	fun addEncounter_setsValidationMessage_whenNameIsBlank() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)

			viewModel.addEncounter(campaignId = CAMPAIGN_ID, name = "\n")
			advanceUntilIdle()

			assertEquals(
				"$CAMPAIGN_EDITOR_ADD_ENCOUNTER_ERROR_PREFIX: $ENCOUNTER_NAME_EMPTY_MESSAGE",
				viewModel.uiState.value.message
			)
		}
	}

	@Test
	fun addEncounterWithMonsters_setsValidationMessage_whenNameIsBlank() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)

			viewModel.addEncounterWithMonsters(
				campaignId = CAMPAIGN_ID,
				name = "   ",
				selectedMonsters = emptyList()
			)
			advanceUntilIdle()

			assertEquals(
				"$CAMPAIGN_EDITOR_ADD_ENCOUNTER_ERROR_PREFIX: $ENCOUNTER_NAME_EMPTY_MESSAGE",
				viewModel.uiState.value.message
			)
		}
	}

	@Test
	fun addEncounterWithMonsters_setsSuccessMessage_whenMonstersAreAdded() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)
			val selectedMonsters = listOf(
				RemoteItem(
					id = "goblin",
					name = "Goblin",
					category = "monster",
					detail = "Skirmisher"
				),
				RemoteItem(
					id = "wolf",
					name = "Wolf",
					category = "monster",
					detail = "Hunter"
				)
			)

			viewModel.addEncounterWithMonsters(
				campaignId = CAMPAIGN_ID,
				name = "Ambush at Hollow Ford",
				selectedMonsters = selectedMonsters
			)
			advanceUntilIdle()

			assertEquals(
				formatEncounterAddedWithMonstersMessage(selectedMonsters.size),
				viewModel.uiState.value.message
			)
		}
	}

	@Test
	fun addEncounterWithMonsters_persistsLocalMonsterStatsForEncounterCombatants() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)
			val selectedMonsters = listOf(
				RemoteItem(
					id = ReferenceDetailResolver.slugFor("Ancient White Dragon"),
					name = "Ancient White Dragon",
					category = "monster",
					detail = "Dragon"
				),
				RemoteItem(
					id = ReferenceDetailResolver.slugFor("Ancient White Dragon"),
					name = "Ancient White Dragon",
					category = "monster",
					detail = "Dragon"
				)
			)

			viewModel.addEncounterWithMonsters(
				campaignId = CAMPAIGN_ID,
				name = "Dragon's Lair",
				selectedMonsters = selectedMonsters
			)
			advanceUntilIdle()

			assertEquals(2, repository.insertedCharacters.size)
			assertEquals(2, repository.insertedCharacters.map { it.id }.distinct().size)

			repository.insertedCharacters.forEach { monster ->
				assertEquals("Ancient White Dragon", monster.name)
				assertEquals("Dragon", monster.type)
				assertEquals(CharacterParty.MONSTERS, monster.party)
				assertEquals(333, monster.hp)
				assertEquals(333, monster.maxHp)
				assertEquals(20, monster.ac)
				assertEquals(40, monster.speed)
				assertEquals(12, monster.initiative)
				assertEquals(20.0, monster.challengeRating, 0.0)
				assertTrue(monster.notes.contains("Gargantuan Dragon"))
			}

			val encounterId = repository.getEncountersForCampaign(CAMPAIGN_ID).first().single().id
			val combatants = repository.combatantsByEncounterId.getValue(encounterId)
			assertEquals(2, combatants.size)
			assertEquals(repository.insertedCharacters.map { it.id }.toSet(), combatants.map { it.characterId }.toSet())
			assertTrue(combatants.all { it.name == "Ancient White Dragon" && it.currentHp == 333 && it.maxHp == 333 })
		}
	}

	@Test
	fun addNote_setsValidationMessage_whenContentIsBlank() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)

			viewModel.addNote(
				campaignId = CAMPAIGN_ID,
				content = "   ",
				type = NOTE_TYPE_LORE,
				extra = "Age of Ruin"
			)
			advanceUntilIdle()

			assertEquals(
				"$CAMPAIGN_EDITOR_ADD_NOTE_ERROR_PREFIX: $NOTE_CONTENT_EMPTY_MESSAGE",
				viewModel.uiState.value.message
			)
		}
	}

	@Test
	fun addNote_setsLoreHistoricalEraValidationMessage_whenHistoricalEraIsBlank() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)

			viewModel.addNote(
				campaignId = CAMPAIGN_ID,
				content = "The empire fell in ash.",
				type = NOTE_TYPE_LORE,
				extra = "  "
			)
			advanceUntilIdle()

			assertEquals(
				"$CAMPAIGN_EDITOR_ADD_NOTE_ERROR_PREFIX: $NOTE_LORE_HISTORICAL_ERA_EMPTY_MESSAGE",
				viewModel.uiState.value.message
			)
		}
	}

	@Test
	fun addNote_setsNpcFactionValidationMessage_whenFactionIsBlank() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)

			viewModel.addNote(
				campaignId = CAMPAIGN_ID,
				content = "Suspicious quartermaster",
				type = NOTE_TYPE_NPC,
				extra = "|Friendly"
			)
			advanceUntilIdle()

			assertEquals(
				"$CAMPAIGN_EDITOR_ADD_NOTE_ERROR_PREFIX: $NOTE_NPC_FACTION_EMPTY_MESSAGE",
				viewModel.uiState.value.message
			)
		}
	}

	@Test
	fun addNote_setsLocationRegionValidationMessage_whenRegionIsBlank() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)

			viewModel.addNote(
				campaignId = CAMPAIGN_ID,
				content = "Flooded archives",
				type = NOTE_TYPE_LOCATION,
				extra = "\t"
			)
			advanceUntilIdle()

			assertEquals(
				"$CAMPAIGN_EDITOR_ADD_NOTE_ERROR_PREFIX: $NOTE_LOCATION_REGION_EMPTY_MESSAGE",
				viewModel.uiState.value.message
			)
		}
	}

	@Test
	fun updateNote_setsValidationMessage_whenContentIsBlank() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)

			viewModel.updateNote(Note.General(campaignId = CAMPAIGN_ID, content = "\n"))
			advanceUntilIdle()

			assertEquals(
				"$CAMPAIGN_EDITOR_UPDATE_NOTE_ERROR_PREFIX: $NOTE_CONTENT_EMPTY_MESSAGE",
				viewModel.uiState.value.message
			)
		}
	}

	@Test
	fun updateNote_setsLoreHistoricalEraValidationMessage_whenHistoricalEraIsBlank() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)

			viewModel.updateNote(Note.Lore(campaignId = CAMPAIGN_ID, content = "The empire fell in ash.", historicalEra = ""))
			advanceUntilIdle()

			assertEquals(
				"$CAMPAIGN_EDITOR_UPDATE_NOTE_ERROR_PREFIX: $NOTE_LORE_HISTORICAL_ERA_EMPTY_MESSAGE",
				viewModel.uiState.value.message
			)
		}
	}

	@Test
	fun updateNote_setsNpcFactionValidationMessage_whenFactionIsBlank() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)

			viewModel.updateNote(Note.NPC(campaignId = CAMPAIGN_ID, content = "Quartermaster", faction = " ", attitude = "Friendly"))
			advanceUntilIdle()

			assertEquals(
				"$CAMPAIGN_EDITOR_UPDATE_NOTE_ERROR_PREFIX: $NOTE_NPC_FACTION_EMPTY_MESSAGE",
				viewModel.uiState.value.message
			)
		}
	}

	@Test
	fun updateNote_setsLocationRegionValidationMessage_whenRegionIsBlank() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)

			viewModel.updateNote(Note.Location(campaignId = CAMPAIGN_ID, content = "Flooded archives", region = "\n"))
			advanceUntilIdle()

			assertEquals(
				"$CAMPAIGN_EDITOR_UPDATE_NOTE_ERROR_PREFIX: $NOTE_LOCATION_REGION_EMPTY_MESSAGE",
				viewModel.uiState.value.message
			)
		}
	}

	@Test
	fun addNote_createsLoreNote_whenHistoricalEraIsProvided() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)

			viewModel.addNote(
				campaignId = CAMPAIGN_ID,
				content = "The empire fell in ash.",
				type = NOTE_TYPE_LORE,
				extra = "Age of Ruin"
			)
			advanceUntilIdle()

			val insertedNote = repository.insertedNotes.single()
			assertTrue(insertedNote is Note.Lore)
			insertedNote as Note.Lore
			assertEquals(CAMPAIGN_ID, insertedNote.campaignId)
			assertEquals("The empire fell in ash.", insertedNote.content)
			assertEquals("Age of Ruin", insertedNote.historicalEra)
		}
	}

	@Test
	fun addNote_createsNpcNoteAndDefaultsAttitude_whenAttitudeIsMissing() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)

			viewModel.addNote(
				campaignId = CAMPAIGN_ID,
				content = "Suspicious quartermaster",
				type = NOTE_TYPE_NPC,
				extra = "Iron Circle"
			)
			advanceUntilIdle()

			val insertedNote = repository.insertedNotes.single()
			assertTrue(insertedNote is Note.NPC)
			insertedNote as Note.NPC
			assertEquals("Iron Circle", insertedNote.faction)
			assertEquals("Neutral", insertedNote.attitude)
		}
	}

	@Test
	fun updateNote_recordsUpdatedNote_whenContentIsNotBlank() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)
			val note = Note.Location(campaignId = CAMPAIGN_ID, content = "Flooded archives", region = "Old Harbor")

			viewModel.updateNote(note)
			advanceUntilIdle()

			assertEquals(listOf(note), repository.updatedNotes)
		}
	}
}
