package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.MONSTER_ABILITY_SCORE_PREFIX
import io.github.velyene.loreweaver.domain.util.MONSTER_CARD_PREVIEW_SECTION_TITLES
import io.github.velyene.loreweaver.domain.util.MONSTER_CARD_SUMMARY_STAT_LABELS
import io.github.velyene.loreweaver.domain.util.MONSTER_STAT_CR
import io.github.velyene.loreweaver.domain.util.MonsterReferenceEntry

private val monsterFilterSectionSpacing = 6.dp
private val monsterFilterChipSpacing = 8.dp
internal const val MONSTER_CARD_VALUE_SEPARATOR = " • "
internal const val MONSTER_FILTERS_ITEM_KEY = "monster-filters"
internal const val MONSTER_NO_RESULTS_ITEM_KEY = "monster-no-results"
internal const val MONSTER_SHORTCUT_KEY_PREFIX = "shortcut-"
internal const val MONSTER_FILTER_CR_KEY_PREFIX = "monster-filter-cr"
internal const val MONSTER_FILTER_TYPE_KEY_PREFIX = "monster-filter-type"
internal const val ENCOUNTER_FILTER_CR_KEY_PREFIX = "encounter-filter-cr"
internal const val ENCOUNTER_FILTER_TYPE_KEY_PREFIX = "encounter-filter-type"
internal val MONSTER_CARD_SUMMARY_STAT_LABELS_NO_CR =
	MONSTER_CARD_SUMMARY_STAT_LABELS.filterNot { it == MONSTER_STAT_CR }

@Composable
internal fun MonsterFilterSectionBlock(
	label: String,
	content: @Composable () -> Unit
) {
	Column(verticalArrangement = Arrangement.spacedBy(monsterFilterSectionSpacing)) {
		Text(
			text = label,
			style = MaterialTheme.typography.labelLarge,
			color = MaterialTheme.colorScheme.secondary
		)
		content()
	}
}

@Composable
internal fun MonsterSingleSelectFilterRow(
	keyPrefix: String,
	label: String,
	selectedOption: String?,
	options: List<String>,
	onOptionSelected: (String?) -> Unit
) {
	MonsterFilterSectionBlock(label = label) {
		LazyRow(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(monsterFilterChipSpacing)
		) {
			item(key = "$keyPrefix-all") {
				FilterChip(
					selected = selectedOption == null,
					onClick = { onOptionSelected(null) },
					label = { Text(stringResource(R.string.reference_monster_filter_all)) }
				)
			}
			items(options, key = { "$keyPrefix-$it" }) { option ->
				FilterChip(
					selected = selectedOption == option,
					onClick = { onOptionSelected(option) },
					label = { Text(option) }
				)
			}
		}
	}
}

internal fun formatMonsterChallengeRatingLabel(challengeRating: String): String? =
	challengeRating.takeIf { it.isNotBlank() }?.let { "$MONSTER_STAT_CR $it" }

internal fun MonsterReferenceEntry.monsterCardSubtitleChallengeRatingLine(): String =
	listOfNotNull(
		subtitle.takeIf { it.isNotBlank() },
		formatMonsterChallengeRatingLabel(challengeRating)
	).joinToString(MONSTER_CARD_VALUE_SEPARATOR)

internal fun MonsterReferenceEntry.monsterCardSummaryLine(
	statLabels: List<String>
): String {
	return statLabels
		.mapNotNull { label ->
			statRows.firstOrNull { it.first == label }
				?.let { "${it.first} ${it.second}" }
		}
		.joinToString(MONSTER_CARD_VALUE_SEPARATOR)
}

internal fun MonsterReferenceEntry.monsterCardFilterSummaryLine(): String =
	listOfNotNull(
		creatureType.takeIf { it.isNotBlank() },
		formatMonsterChallengeRatingLabel(challengeRating)
	).joinToString(MONSTER_CARD_VALUE_SEPARATOR)

internal fun MonsterReferenceEntry.monsterCardPreviewLine(): String? {
	val firstSectionBodyLine = sections
		.firstOrNull { it.title in MONSTER_CARD_PREVIEW_SECTION_TITLES }
		?.body
		?.lineSequence()
		?.firstOrNull(String::isNotBlank)
	if (firstSectionBodyLine != null) return firstSectionBodyLine

	return body.lineSequence()
		.firstOrNull { it.isNotBlank() && !it.startsWith(MONSTER_ABILITY_SCORE_PREFIX) }
}

