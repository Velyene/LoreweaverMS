package io.github.velyene.loreweaver.domain.use_case

import io.github.velyene.loreweaver.domain.model.Note
import io.github.velyene.loreweaver.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesForCampaignUseCase @Inject constructor(
	private val repository: NotesRepository
) {
	operator fun invoke(campaignId: String): Flow<List<Note>> {
		return repository.getNotesForCampaign(campaignId)
	}
}
