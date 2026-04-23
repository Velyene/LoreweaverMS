/*
 * FILE: ReferenceScreenMadness.kt
 *
 * TABLE OF CONTENTS:
 * 1. Madness content entry point and remembered section state
 * 2. Header, roll, result, and table sections
 * 3. Roll-preview and table-entry mappers
 * 4. Share-text helpers
 */

package com.example.loreweaver.ui.screens

import android.content.Context
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loreweaver.R
import com.example.loreweaver.domain.util.MadnessDuration
import com.example.loreweaver.domain.util.MadnessReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun MadnessContent(
	selectedDuration: MadnessDuration,
	lastRoll: Int?,
	lastResult: String?,
	listState: LazyListState,
	onDurationSelected: (MadnessDuration) -> Unit,
	onRoll: () -> Unit
) {
	val madnessSectionState = rememberMadnessSectionState(
		listState = listState,
		selectedDuration = selectedDuration,
		lastRoll = lastRoll,
		onRoll = onRoll
	)
	val context = LocalContext.current
	val shareChooserTitle = stringResource(R.string.reference_share_chooser_title)
	val resultBringIntoViewRequester = remember { BringIntoViewRequester() }

	LaunchedEffect(madnessSectionState.resultScrollToken, lastRoll, lastResult) {
		if (madnessSectionState.resultScrollToken == 0 || lastRoll == null || lastResult.isNullOrBlank()) return@LaunchedEffect

		delay(150.milliseconds)
		resultBringIntoViewRequester.bringIntoView()
		madnessSectionState.onResultShown()
	}

	MadnessContentLayout(
		selectedDuration = selectedDuration,
		lastRoll = lastRoll,
		lastResult = lastResult,
		shareChooserTitle = shareChooserTitle,
		context = context,
		onDurationSelected = onDurationSelected,
		state = madnessSectionState,
		resultBringIntoViewRequester = resultBringIntoViewRequester
	)
}

@Composable
private fun MadnessContentLayout(
	selectedDuration: MadnessDuration,
	lastRoll: Int?,
	lastResult: String?,
	shareChooserTitle: String,
	context: Context,
	onDurationSelected: (MadnessDuration) -> Unit,
	state: MadnessSectionState,
	resultBringIntoViewRequester: BringIntoViewRequester
) {
	Column(modifier = Modifier.fillMaxSize()) {
		MadnessDurationTabs(
			selectedDuration = selectedDuration,
			onDurationSelected = onDurationSelected
		)

		LazyColumn(
			state = state.listState,
			modifier = Modifier.fillMaxSize(),
			contentPadding = PaddingValues(16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			item {
				MadnessHeaderSection(
					selectedDuration = selectedDuration,
					lastRoll = lastRoll,
					lastResult = lastResult,
					shareChooserTitle = shareChooserTitle,
					context = context
				)
			}

			item {
				MadnessRollSection(
					isRolling = state.isRolling,
					animatedRoll = state.animatedRoll,
					lastRoll = lastRoll,
					lastResult = lastResult,
					onRollRequested = state.onRollRequested,
					resultBringIntoViewRequester = resultBringIntoViewRequester
				)
			}

			item {
				MadnessTableSection(
					tableEntries = state.tableEntries,
					highlightedRoll = state.highlightedRoll
				)
			}
		}
	}
}

private data class MadnessSectionState(
	val listState: LazyListState,
	val tableEntries: List<MadnessTableDisplayEntry>,
	val highlightedRoll: Int?,
	val animatedRoll: Int?,
	val isRolling: Boolean,
	val resultScrollToken: Int,
	val onRollRequested: () -> Unit,
	val onResultShown: () -> Unit
)

@Composable
private fun rememberMadnessSectionState(
	listState: LazyListState,
	selectedDuration: MadnessDuration,
	lastRoll: Int?,
	onRoll: () -> Unit
): MadnessSectionState {
	val coroutineScope = rememberCoroutineScope()
	val tableEntries = remember(selectedDuration) { createMadnessTableEntries(selectedDuration) }
	var animatedRoll by remember(selectedDuration) { mutableStateOf<Int?>(null) }
	var isRolling by remember(selectedDuration) { mutableStateOf(false) }
	var resultScrollToken by remember(selectedDuration) { mutableIntStateOf(0) }
	val highlightedRoll = animatedRoll ?: lastRoll

	return MadnessSectionState(
		listState = listState,
		tableEntries = tableEntries,
		highlightedRoll = highlightedRoll,
		animatedRoll = animatedRoll,
		isRolling = isRolling,
		resultScrollToken = resultScrollToken,
		onRollRequested = {
			startMadnessRollPreview(
				coroutineScope = coroutineScope,
				isRolling = isRolling,
				onRollingChange = {
					@Suppress("UNUSED_VALUE")
					isRolling = it
				},
				onAnimatedRollChange = {
					@Suppress("UNUSED_VALUE")
					animatedRoll = it
				},
				onBeforeRoll = {
					@Suppress("UNUSED_VALUE")
					resultScrollToken += 1
				},
				onRoll = onRoll
			)
		},
		onResultShown = {
			@Suppress("UNUSED_VALUE")
			isRolling = false
		}
	)
}

@Composable
private fun MadnessTableEntryCard(
	entry: MadnessTableDisplayEntry,
	isHighlighted: Boolean = false
) {
	Card(
		colors = CardDefaults.cardColors(
			containerColor = if (isHighlighted) {
				MaterialTheme.colorScheme.tertiaryContainer
			} else {
				MaterialTheme.colorScheme.surfaceVariant
			}
		)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Text(
				"d100: ${entry.range.first}-${entry.range.last}",
				fontSize = 10.sp,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Spacer(modifier = Modifier.height(4.dp))
			Text(entry.text, style = MaterialTheme.typography.bodyMedium)
		}
	}
}

private fun startMadnessRollPreview(
	coroutineScope: CoroutineScope,
	isRolling: Boolean,
	onRollingChange: (Boolean) -> Unit,
	onAnimatedRollChange: (Int?) -> Unit,
	onBeforeRoll: () -> Unit,
	onRoll: () -> Unit
) {
	if (isRolling) return

	coroutineScope.launch {
		onRollingChange(true)
		repeat(12) { index ->
			onAnimatedRollChange(Random.nextInt(1, 101))
			delay(if (index < 6) 45.milliseconds else 75.milliseconds)
		}
		onAnimatedRollChange(null)
		onBeforeRoll()
		onRoll()
	}
}

@Composable
private fun MadnessDurationTabs(
	selectedDuration: MadnessDuration,
	onDurationSelected: (MadnessDuration) -> Unit
) {
	PrimaryScrollableTabRow(
		selectedTabIndex = selectedDuration.ordinal,
		containerColor = MaterialTheme.colorScheme.surface
	) {
		MadnessDuration.entries.forEach { duration ->
			Tab(
				selected = selectedDuration == duration,
				onClick = { onDurationSelected(duration) },
				text = {
					Text(
						text = duration.toDisplayLabel(),
						fontSize = 12.sp
					)
				}
			)
		}
	}
}

@Composable
private fun MadnessDurationInfoCard(selectedDuration: MadnessDuration) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
		Column(modifier = Modifier.padding(12.dp)) {
			Text(
				text = "Duration: ${MadnessReference.getDurationDescription(selectedDuration)}",
				fontWeight = FontWeight.Bold
			)
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				text = MadnessReference.getCureInfo()[selectedDuration].orEmpty(),
				fontSize = 12.sp
			)
		}
	}
}

@Composable
private fun MadnessHeaderSection(
	selectedDuration: MadnessDuration,
	lastRoll: Int?,
	lastResult: String?,
	shareChooserTitle: String,
	context: Context
) {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		ReferenceTitleWithShare(
			title = stringResource(R.string.reference_tab_madness),
			onShare = {
				shareReferenceText(
					context = context,
					chooserTitle = shareChooserTitle,
					text = buildMadnessReferenceShareText(selectedDuration, lastRoll, lastResult)
				)
			}
		)

		MadnessDurationInfoCard(selectedDuration)
	}
}

@Composable
private fun MadnessRollSection(
	isRolling: Boolean,
	animatedRoll: Int?,
	lastRoll: Int?,
	lastResult: String?,
	onRollRequested: () -> Unit,
	resultBringIntoViewRequester: BringIntoViewRequester
) {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		MadnessRollButton(
			isRolling = isRolling,
			onRoll = onRollRequested
		)

		animatedRoll?.let { previewRoll ->
			InfoCard(
				title = stringResource(R.string.madness_rolling_preview_title, previewRoll),
				body = stringResource(R.string.madness_rolling_preview_body)
			)
		}

		if (lastRoll != null && !lastResult.isNullOrBlank()) {
			MadnessResultCard(
				roll = lastRoll,
				result = lastResult,
				modifier = Modifier.bringIntoViewRequester(resultBringIntoViewRequester)
			)
		}
	}
}

@Composable
private fun MadnessResultCard(
	roll: Int,
	result: String,
	modifier: Modifier = Modifier
) {
	Card(
		modifier = modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Text(
				text = stringResource(R.string.madness_roll_result_title, roll),
				style = MaterialTheme.typography.titleSmall,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onTertiaryContainer
			)
			Text(
				text = result,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onTertiaryContainer
			)
		}
	}
}

@Composable
private fun MadnessTableSection(
	tableEntries: List<MadnessTableDisplayEntry>,
	highlightedRoll: Int?
) {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		tableEntries.forEach { entry ->
			MadnessTableEntryCard(
				entry = entry,
				isHighlighted = highlightedRoll != null && highlightedRoll in entry.range
			)
		}
	}
}

@Composable
private fun MadnessRollButton(
	isRolling: Boolean,
	onRoll: () -> Unit
) {
	Button(
		onClick = onRoll,
		enabled = !isRolling,
		modifier = Modifier.fillMaxWidth()
	) {
		Text(
			text = if (isRolling) stringResource(R.string.reference_rolling_d100) else stringResource(
				R.string.reference_roll_d100
			)
		)
	}
}

private data class MadnessTableDisplayEntry(
	val key: String,
	val range: IntRange,
	val text: String
)

private fun createMadnessTableEntries(duration: MadnessDuration): List<MadnessTableDisplayEntry> =
	when (duration) {
		MadnessDuration.SHORT_TERM -> MadnessReference.SHORT_TERM_EFFECTS.map { effect ->
			MadnessTableDisplayEntry(
				key = "${duration.name}-${effect.diceRange.first}",
				range = effect.diceRange,
				text = effect.effect
			)
		}

		MadnessDuration.LONG_TERM -> MadnessReference.LONG_TERM_EFFECTS.map { effect ->
			MadnessTableDisplayEntry(
				key = "${duration.name}-${effect.diceRange.first}",
				range = effect.diceRange,
				text = effect.effect
			)
		}

		MadnessDuration.INDEFINITE -> MadnessReference.INDEFINITE_FLAWS.map { (flaw, range) ->
			MadnessTableDisplayEntry(
				key = "${duration.name}-${range.first}",
				range = range,
				text = flaw
			)
		}
	}

private fun buildMadnessReferenceShareText(
	duration: MadnessDuration,
	lastRoll: Int?,
	lastResult: String?
): String = buildString {
	appendLine("Madness Reference")
	appendLine("Duration: ${MadnessReference.getDurationDescription(duration)}")
	appendLine("Cure: ${MadnessReference.getCureInfo()[duration].orEmpty()}")
	appendLine()
	if (lastRoll != null && !lastResult.isNullOrBlank()) {
		appendLine("Latest Roll: $lastRoll")
		appendLine(lastResult)
	} else {
		appendLine("No roll yet. Use the d100 roller to generate a madness result.")
	}
}.trim()
