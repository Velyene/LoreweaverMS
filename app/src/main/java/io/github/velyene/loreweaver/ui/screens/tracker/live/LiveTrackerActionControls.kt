/*
 * FILE: LiveTrackerActionControls.kt
 *
 * TABLE OF CONTENTS:
 * 1. Turn-step summary
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.CombatTurnStep

@Composable
internal fun turnStepSummary(turnStep: CombatTurnStep): String {
	return when (turnStep) {
		CombatTurnStep.SELECT_ACTION -> stringResource(R.string.encounter_turn_step_select_action)
		CombatTurnStep.SELECT_TARGET -> stringResource(R.string.encounter_turn_step_select_target)
		CombatTurnStep.APPLY_RESULT -> stringResource(R.string.encounter_turn_step_apply_result)
		CombatTurnStep.READY_TO_END -> stringResource(R.string.encounter_turn_step_ready_to_end)
	}
}


