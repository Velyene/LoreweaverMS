/*
 * FILE: LiveTrackerFormatting.kt
 *
 * TABLE OF CONTENTS:
 * 1. Participant mapping and labels
 * 2. Resource formatting
 * 3. Turn-window formatting
 */

package io.github.velyene.loreweaver.ui.screens.tracker.live

internal data class LiveTrackerFormattingLabels(
	val enemyTypeLabel: String,
	val defaultActionLabels: List<String>,
	val tempHpFormat: String,
	val manaFormat: String,
	val staminaFormat: String,
	val namedResourceFormat: String
)
