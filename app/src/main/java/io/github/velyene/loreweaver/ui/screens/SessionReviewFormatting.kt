package io.github.velyene.loreweaver.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.EncounterResult

internal fun parseEncounterResultOrNull(value: String): EncounterResult? {
	return runCatching { EncounterResult.valueOf(value) }.getOrNull()
}

internal fun encounterResultLabelRes(result: EncounterResult): Int {
	return when (result) {
		EncounterResult.VICTORY -> R.string.session_summary_result_victory
		EncounterResult.DEFEAT -> R.string.session_summary_result_defeat
		EncounterResult.ENDED_EARLY -> R.string.session_summary_result_ended_early
	}
}

@Composable
internal fun encounterResultLabel(result: EncounterResult): String {
	return stringResource(encounterResultLabelRes(result))
}

@Composable
internal fun encounterResultColor(result: EncounterResult): Color {
	return when (result) {
		EncounterResult.VICTORY -> MaterialTheme.colorScheme.primary
		EncounterResult.DEFEAT -> MaterialTheme.colorScheme.error
		EncounterResult.ENDED_EARLY -> MaterialTheme.colorScheme.tertiary
	}
}

