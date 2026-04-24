package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test

class NoteContentValidationUseCaseTest {

	@Test
	fun addNoteUseCase_rejectsBlankContent_whenContentIsBlank() = runBlocking {
		val repository = FakeNotesRepository()
		val useCase = AddNoteUseCase(repository)

		try {
			useCase(Note.General(campaignId = "campaign-1", content = "\t"))
			fail("Expected IllegalArgumentException for blank note content")
		} catch (exception: IllegalArgumentException) {
			assertEquals(ValidationMessages.NOTE_CONTENT_EMPTY_MESSAGE, exception.message)
			assertNull(repository.insertedNote)
		}
	}

	@Test
	fun updateNoteUseCase_rejectsBlankContent_whenContentIsBlank() = runBlocking {
		val repository = FakeNotesRepository()
		val useCase = UpdateNoteUseCase(repository)

		try {
			useCase(Note.General(campaignId = "campaign-1", content = "   "))
			fail("Expected IllegalArgumentException for blank note content")
		} catch (exception: IllegalArgumentException) {
			assertEquals(ValidationMessages.NOTE_CONTENT_EMPTY_MESSAGE, exception.message)
			assertNull(repository.updatedNote)
		}
	}

	@Test
	fun addNoteUseCase_rejectsLoreWithoutHistoricalEra_whenHistoricalEraIsBlank() = runBlocking {
		val repository = FakeNotesRepository()
		val useCase = AddNoteUseCase(repository)

		try {
			useCase(Note.Lore(campaignId = "campaign-1", content = "Fallen dynasty", historicalEra = " "))
			fail("Expected IllegalArgumentException for blank lore era")
		} catch (exception: IllegalArgumentException) {
			assertEquals(ValidationMessages.NOTE_LORE_HISTORICAL_ERA_EMPTY_MESSAGE, exception.message)
			assertNull(repository.insertedNote)
		}
	}

	@Test
	fun addNoteUseCase_rejectsNpcWithoutFaction_whenFactionIsBlank() = runBlocking {
		val repository = FakeNotesRepository()
		val useCase = AddNoteUseCase(repository)

		try {
			useCase(Note.NPC(campaignId = "campaign-1", content = "Quartermaster", faction = "", attitude = "Neutral"))
			fail("Expected IllegalArgumentException for blank NPC faction")
		} catch (exception: IllegalArgumentException) {
			assertEquals(ValidationMessages.NOTE_NPC_FACTION_EMPTY_MESSAGE, exception.message)
			assertNull(repository.insertedNote)
		}
	}

	@Test
	fun addNoteUseCase_rejectsLocationWithoutRegion_whenRegionIsBlank() = runBlocking {
		val repository = FakeNotesRepository()
		val useCase = AddNoteUseCase(repository)

		try {
			useCase(Note.Location(campaignId = "campaign-1", content = "Sunken archive", region = "\t"))
			fail("Expected IllegalArgumentException for blank location region")
		} catch (exception: IllegalArgumentException) {
			assertEquals(ValidationMessages.NOTE_LOCATION_REGION_EMPTY_MESSAGE, exception.message)
			assertNull(repository.insertedNote)
		}
	}

	@Test
	fun updateNoteUseCase_rejectsLoreWithoutHistoricalEra_whenHistoricalEraIsBlank() = runBlocking {
		val repository = FakeNotesRepository()
		val useCase = UpdateNoteUseCase(repository)

		try {
			useCase(Note.Lore(campaignId = "campaign-1", content = "Fallen dynasty", historicalEra = ""))
			fail("Expected IllegalArgumentException for blank lore era")
		} catch (exception: IllegalArgumentException) {
			assertEquals(ValidationMessages.NOTE_LORE_HISTORICAL_ERA_EMPTY_MESSAGE, exception.message)
			assertNull(repository.updatedNote)
		}
	}

	@Test
	fun updateNoteUseCase_rejectsNpcWithoutFaction_whenFactionIsBlank() = runBlocking {
		val repository = FakeNotesRepository()
		val useCase = UpdateNoteUseCase(repository)

		try {
			useCase(Note.NPC(campaignId = "campaign-1", content = "Quartermaster", faction = " ", attitude = "Neutral"))
			fail("Expected IllegalArgumentException for blank NPC faction")
		} catch (exception: IllegalArgumentException) {
			assertEquals(ValidationMessages.NOTE_NPC_FACTION_EMPTY_MESSAGE, exception.message)
			assertNull(repository.updatedNote)
		}
	}

	@Test
	fun updateNoteUseCase_rejectsLocationWithoutRegion_whenRegionIsBlank() = runBlocking {
		val repository = FakeNotesRepository()
		val useCase = UpdateNoteUseCase(repository)

		try {
			useCase(Note.Location(campaignId = "campaign-1", content = "Sunken archive", region = " "))
			fail("Expected IllegalArgumentException for blank location region")
		} catch (exception: IllegalArgumentException) {
			assertEquals(ValidationMessages.NOTE_LOCATION_REGION_EMPTY_MESSAGE, exception.message)
			assertNull(repository.updatedNote)
		}
	}

	@Test
	fun updateNoteUseCase_updatesNote_whenContentIsNotBlank() = runBlocking {
		val repository = FakeNotesRepository()
		val useCase = UpdateNoteUseCase(repository)
		val note = Note.Lore(campaignId = "campaign-1", content = "A fallen dynasty", historicalEra = "Age of Glass")

		useCase(note)

		assertNotNull(repository.updatedNote)
		assertEquals(note, repository.updatedNote)
	}
}

private class FakeNotesRepository : NotesRepository {
	var insertedNote: Note? = null
	var updatedNote: Note? = null

	override fun getNotesForCampaign(campaignId: String): Flow<List<Note>> = emptyFlow()

	override suspend fun insertNote(note: Note) {
		insertedNote = note
	}

	override suspend fun updateNote(note: Note) {
		updatedNote = note
	}

	override suspend fun deleteNote(note: Note) = Unit
}
