package io.github.velyene.loreweaver.ui.viewmodels

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.velyene.loreweaver.R
import javax.inject.Inject

interface CombatTextProvider {
	fun roundBeginsStatus(round: Int): String
	fun quickEncounterName(timestampMillis: Long): String
	fun encounterSessionTitle(timestampMillis: Long): String
}

class AndroidCombatTextProvider @Inject constructor(
	@param:ApplicationContext private val context: Context
) : CombatTextProvider {
	override fun roundBeginsStatus(round: Int): String {
		return context.getString(R.string.combat_round_begins_status, round)
	}

	override fun quickEncounterName(timestampMillis: Long): String {
		return context.getString(R.string.quick_encounter_fallback_name, timestampMillis)
	}

	override fun encounterSessionTitle(timestampMillis: Long): String {
		return context.getString(R.string.encounter_session_fallback_title, timestampMillis)
	}
}


