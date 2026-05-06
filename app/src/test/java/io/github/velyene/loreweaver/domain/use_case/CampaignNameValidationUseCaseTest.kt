package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.domain.repository.CampaignsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test

class CampaignNameValidationUseCaseTest {

	@Test
	fun addCampaignUseCase_rejectsBlankName_whenNameIsBlank() = runBlocking {
		val repository = FakeCampaignsRepository()
		val useCase = AddCampaignUseCase(repository)

		try {
			useCase(name = "   ", description = "A frontier chronicle")
			fail("Expected IllegalArgumentException for blank campaign name")
		} catch (exception: IllegalArgumentException) {
			assertEquals(ValidationMessages.CAMPAIGN_NAME_EMPTY_MESSAGE, exception.message)
			assertNull(repository.insertedCampaign)
		}
	}

	@Test
	fun addCampaignUseCase_insertsCampaign_whenNameIsNotBlank() = runBlocking {
		val repository = FakeCampaignsRepository()
		val useCase = AddCampaignUseCase(repository)

		useCase(name = "Shadows of Emberfall", description = "A frontier chronicle")

		val insertedCampaign = repository.insertedCampaign
		assertNotNull(insertedCampaign)
		assertEquals("Shadows of Emberfall", insertedCampaign?.title)
		assertEquals("A frontier chronicle", insertedCampaign?.description)
	}

	@Test
	fun updateCampaignUseCase_rejectsBlankName_whenNameIsBlank() = runBlocking {
		val repository = FakeCampaignsRepository()
		val useCase = UpdateCampaignUseCase(repository)

		try {
			useCase(Campaign(id = "campaign-1", title = "   ", description = "Updated"))
			fail("Expected IllegalArgumentException for blank campaign name")
		} catch (exception: IllegalArgumentException) {
			assertEquals(ValidationMessages.CAMPAIGN_NAME_EMPTY_MESSAGE, exception.message)
			assertNull(repository.updatedCampaign)
		}
	}

	@Test
	fun updateCampaignUseCase_updatesCampaign_whenNameIsNotBlank() = runBlocking {
		val repository = FakeCampaignsRepository()
		val useCase = UpdateCampaignUseCase(repository)
		val campaign = Campaign(id = "campaign-1", title = "Shadows of Emberfall", description = "Updated")

		useCase(campaign)

		assertEquals(campaign, repository.updatedCampaign)
	}
}

private class FakeCampaignsRepository : CampaignsRepository {
	var insertedCampaign: Campaign? = null
	var updatedCampaign: Campaign? = null

	override fun getAllCampaigns(): Flow<List<Campaign>> = emptyFlow()

	override suspend fun getCampaignById(id: String): Campaign? = null

	override suspend fun insertCampaign(campaign: Campaign) {
		insertedCampaign = campaign
	}

	override suspend fun updateCampaign(campaign: Campaign) {
		updatedCampaign = campaign
	}

	override suspend fun deleteCampaign(campaign: Campaign) = Unit
}
