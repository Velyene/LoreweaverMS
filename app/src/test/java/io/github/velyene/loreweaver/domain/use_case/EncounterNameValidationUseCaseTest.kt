package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.CombatantState
import io.github.velyene.loreweaver.domain.model.Encounter
import io.github.velyene.loreweaver.domain.repository.EncountersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test

class EncounterNameValidationUseCaseTest {

	@Test
	fun addEncounterUseCase_rejectsBlankName_whenNameIsBlank() = runBlocking {
		val repository = FakeEncountersRepository()
		val useCase = AddEncounterUseCase(repository)

		try {
			useCase(campaignId = "campaign-1", name = "   ")
			fail("Expected IllegalArgumentException for blank encounter name")
		} catch (exception: IllegalArgumentException) {
			assertEquals(ValidationMessages.ENCOUNTER_NAME_EMPTY_MESSAGE, exception.message)
			assertNull(repository.insertedEncounter)
		}
	}

	@Test
	fun addEncounterUseCase_insertsEncounter_whenNameIsNotBlank() = runBlocking {
		val repository = FakeEncountersRepository()
		val useCase = AddEncounterUseCase(repository)

		val encounterId = useCase(campaignId = "campaign-1", name = "Whispers in the Catacombs")

		val insertedEncounter = repository.insertedEncounter
		assertNotNull(insertedEncounter)
		assertEquals(encounterId, insertedEncounter?.id)
		assertEquals("campaign-1", insertedEncounter?.campaignId)
		assertEquals("Whispers in the Catacombs", insertedEncounter?.name)
	}

	@Test
	fun updateEncounterUseCase_rejectsBlankName_whenNameIsBlank() = runBlocking {
		val repository = FakeEncountersRepository()
		val useCase = UpdateEncounterUseCase(repository)

		try {
			useCase(Encounter(id = "encounter-1", campaignId = "campaign-1", name = "   "))
			fail("Expected IllegalArgumentException for blank encounter name")
		} catch (exception: IllegalArgumentException) {
			assertEquals(ValidationMessages.ENCOUNTER_NAME_EMPTY_MESSAGE, exception.message)
			assertNull(repository.updatedEncounter)
		}
	}

	@Test
	fun updateEncounterUseCase_updatesEncounter_whenNameIsNotBlank() = runBlocking {
		val repository = FakeEncountersRepository()
		val useCase = UpdateEncounterUseCase(repository)
		val encounter = Encounter(id = "encounter-1", campaignId = "campaign-1", name = "Whispers in the Catacombs")

		useCase(encounter)

		assertEquals(encounter, repository.updatedEncounter)
	}
}

private class FakeEncountersRepository : EncountersRepository {
	var insertedEncounter: Encounter? = null
	var updatedEncounter: Encounter? = null

	override fun getAllEncounters(): Flow<List<Encounter>> = emptyFlow()

	override fun getEncountersForCampaign(campaignId: String): Flow<List<Encounter>> = emptyFlow()

	override suspend fun getEncounterById(encounterId: String): Encounter? = null

	override suspend fun insertEncounter(encounter: Encounter) {
		insertedEncounter = encounter
	}

	override suspend fun updateEncounter(encounter: Encounter) {
		updatedEncounter = encounter
	}

	override suspend fun deleteEncounter(encounter: Encounter) = Unit

	override suspend fun addCombatantsToEncounter(
		encounterId: String,
		combatants: List<CombatantState>
	) = Unit

	override suspend fun getActiveEncounter(): Encounter? = null

	override suspend fun setActiveEncounter(encounterId: String) = Unit
}
