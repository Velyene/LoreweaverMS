package com.example.encountertimer.domain.use_case

import com.example.encountertimer.domain.model.Note
import com.example.encountertimer.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesForCampaignUseCase @Inject constructor(
	private val repository: NotesRepository
) {
	operator fun invoke(campaignId: String): Flow<List<Note>> {
		return repository.getNotesForCampaign(campaignId)
	}
}
