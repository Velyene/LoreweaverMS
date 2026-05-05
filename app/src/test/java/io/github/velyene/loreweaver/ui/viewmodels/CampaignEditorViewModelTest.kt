/*
 * FILE: CampaignEditorViewModelTest.kt
 *
 * TABLE OF CONTENTS:
 * 1. Class: CampaignEditorViewModelTest
 * 2. Value: CAMPAIGN_ID
 * 3. Value: mainDispatcherRule
 * 4. Function: addCampaign_setsValidationMessage_whenNameIsBlank
 * 5. Value: repository
 * 6. Value: viewModel
 * 7. Function: addEncounter_setsValidationMessage_whenNameIsBlank
 * 8. Function: updateCampaign_setsValidationMessage_whenNameIsBlank
 */

package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.MainDispatcherRule
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.model.Encounter
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
import io.github.velyene.loreweaver.ui.util.UiText
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

		fun expectedValidationMessage(prefix: UiText, detail: String): UiText {
			return UiText.StringResource(
				R.string.error_with_detail,
				listOf(prefix, UiText.DynamicString(detail))
			)
		}
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
				expectedValidationMessage(CAMPAIGN_EDITOR_ADD_CAMPAIGN_ERROR_PREFIX, CAMPAIGN_NAME_EMPTY_MESSAGE),
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
				expectedValidationMessage(CAMPAIGN_EDITOR_ADD_ENCOUNTER_ERROR_PREFIX, ENCOUNTER_NAME_EMPTY_MESSAGE),
				viewModel.uiState.value.message
			)
		}
	}

	@Test
	fun updateCampaign_setsValidationMessage_whenNameIsBlank() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)
			val campaign = Campaign(id = CAMPAIGN_ID, title = "Harbor Watch", description = "Before")

			viewModel.updateCampaign(campaign, name = "   ", description = "After")
			advanceUntilIdle()

			assertEquals(
				expectedValidationMessage(CAMPAIGN_EDITOR_UPDATE_CAMPAIGN_ERROR_PREFIX, CAMPAIGN_NAME_EMPTY_MESSAGE),
				viewModel.uiState.value.message
			)
		}
	}

	@Test
	fun updateCampaign_updatesRepositoryAndPublishesUpdatedId_whenNameIsValid() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)
			val originalCampaign = Campaign(id = CAMPAIGN_ID, title = "Harbor Watch", description = "Before")
			repository.setCampaigns(listOf(originalCampaign))

			viewModel.updateCampaign(originalCampaign, name = "Harbor Watch Revised", description = "After")
			advanceUntilIdle()

			assertEquals(
				"Harbor Watch Revised",
				repository.getCampaignById(CAMPAIGN_ID)?.title
			)
			assertEquals("After", repository.getCampaignById(CAMPAIGN_ID)?.description)
			assertEquals(CAMPAIGN_ID, viewModel.uiState.value.updatedCampaignId)
		}
	}

	@Test
	fun deleteCampaign_removesCampaignAndPublishesDeletedId() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)
			val campaign = Campaign(id = CAMPAIGN_ID, title = "Harbor Watch", description = "Before")
			repository.setCampaigns(listOf(campaign))

			viewModel.deleteCampaign(campaign)
			advanceUntilIdle()

			assertEquals(null, repository.getCampaignById(CAMPAIGN_ID))
			assertEquals(CAMPAIGN_ID, viewModel.uiState.value.deletedCampaignId)
		}
	}

	@Test
	fun updateEncounter_setsValidationMessage_whenNameIsBlank() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)
			val encounter = Encounter(id = "encounter-1", campaignId = CAMPAIGN_ID, name = "Bridge Ambush")

			viewModel.updateEncounter(encounter, name = "   ")
			advanceUntilIdle()

			assertEquals(
				expectedValidationMessage(CAMPAIGN_EDITOR_UPDATE_ENCOUNTER_ERROR_PREFIX, ENCOUNTER_NAME_EMPTY_MESSAGE),
				viewModel.uiState.value.message
			)
		}
	}

	@Test
	fun updateEncounter_updatesRepositoryAndPublishesUpdatedEncounterId() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)
			val encounter = Encounter(id = "encounter-1", campaignId = CAMPAIGN_ID, name = "Bridge Ambush")
			repository.insertEncounter(encounter)

			viewModel.updateEncounter(encounter, name = "Bridge Ambush Revised")
			advanceUntilIdle()

			assertEquals(
				"Bridge Ambush Revised",
				repository.getEncounterById(encounter.id)?.name
			)
			assertEquals(encounter.id, viewModel.uiState.value.updatedEncounterId)
		}
	}

	@Test
	fun deleteEncounter_removesEncounterAndPublishesDeletedEncounterId() {
		runTest {
			val repository = SplitFakeCampaignRepository()
			val viewModel = createCampaignEditorViewModel(repository)
			val encounter = Encounter(id = "encounter-1", campaignId = CAMPAIGN_ID, name = "Bridge Ambush")
			repository.insertEncounter(encounter)

			viewModel.deleteEncounter(encounter)
			advanceUntilIdle()

			assertEquals(null, repository.getEncounterById(encounter.id))
			assertEquals(encounter.id, viewModel.uiState.value.deletedEncounterId)
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
				expectedValidationMessage(CAMPAIGN_EDITOR_ADD_ENCOUNTER_ERROR_PREFIX, ENCOUNTER_NAME_EMPTY_MESSAGE),
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
			assertTrue(combatants.all { it.initiative in 13..32 })
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
				expectedValidationMessage(CAMPAIGN_EDITOR_ADD_NOTE_ERROR_PREFIX, NOTE_CONTENT_EMPTY_MESSAGE),
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
				expectedValidationMessage(CAMPAIGN_EDITOR_ADD_NOTE_ERROR_PREFIX, NOTE_LORE_HISTORICAL_ERA_EMPTY_MESSAGE),
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
				expectedValidationMessage(CAMPAIGN_EDITOR_ADD_NOTE_ERROR_PREFIX, NOTE_NPC_FACTION_EMPTY_MESSAGE),
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
				expectedValidationMessage(CAMPAIGN_EDITOR_ADD_NOTE_ERROR_PREFIX, NOTE_LOCATION_REGION_EMPTY_MESSAGE),
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
				expectedValidationMessage(CAMPAIGN_EDITOR_UPDATE_NOTE_ERROR_PREFIX, NOTE_CONTENT_EMPTY_MESSAGE),
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
				expectedValidationMessage(CAMPAIGN_EDITOR_UPDATE_NOTE_ERROR_PREFIX, NOTE_LORE_HISTORICAL_ERA_EMPTY_MESSAGE),
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
				expectedValidationMessage(CAMPAIGN_EDITOR_UPDATE_NOTE_ERROR_PREFIX, NOTE_NPC_FACTION_EMPTY_MESSAGE),
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
				expectedValidationMessage(CAMPAIGN_EDITOR_UPDATE_NOTE_ERROR_PREFIX, NOTE_LOCATION_REGION_EMPTY_MESSAGE),
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
