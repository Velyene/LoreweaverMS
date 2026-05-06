/*
 * FILE: LiveTrackerTurnOrder.kt
 *
 * TABLE OF CONTENTS:
 * 1. Turn Order Helpers
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

internal enum class TurnOrderEmphasis {
	CURRENT,
	NEXT,
	ON_DECK,
	LATER
}

internal fun upcomingTurnWindow(
	participants: List<LiveParticipantUiModel>,
	turnIndex: Int,
	windowSize: Int = 6
): List<LiveParticipantUiModel> {
	if (participants.isEmpty()) return emptyList()
	return List(minOf(windowSize, participants.size)) { offset ->
		participants[(turnIndex + offset).floorMod(participants.size)]
	}
}

internal fun Int.floorMod(divisor: Int): Int = ((this % divisor) + divisor) % divisor
