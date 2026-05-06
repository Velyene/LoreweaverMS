package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.repository.CampaignRepository
import io.github.velyene.loreweaver.domain.use_case.ClearLogsUseCase
import io.github.velyene.loreweaver.domain.use_case.GetAllLogsUseCase

internal fun createAdventureLogViewModel(repository: CampaignRepository): AdventureLogViewModel {
	return AdventureLogViewModel(
		getAllLogsUseCase = GetAllLogsUseCase(repository),
		clearLogsUseCase = ClearLogsUseCase(repository),
		appText = fakeAppText
	)
}

internal fun expectedErrorMessage(prefixResId: Int, detail: String): String {
	val prefix = fakeAppText.getString(prefixResId)
	return fakeAppText.getString(R.string.error_with_detail, prefix, detail)
}

