/*
 * FILE: EncounterSetupDifficulty.kt
 *
 * TABLE OF CONTENTS:
 * 1. Difficulty summary card
 * 2. Difficulty color support
 */

package io.github.velyene.loreweaver.ui.screens.tracker.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.DifficultyRating
import io.github.velyene.loreweaver.domain.util.EncounterDifficulty
import io.github.velyene.loreweaver.domain.util.EncounterDifficultyResult

@Composable
internal fun EncounterDifficultyCard(encounterDifficulty: EncounterDifficultyResult) {
	val ratingLabel = EncounterDifficulty.formatDifficultyRating(encounterDifficulty.rating).first
	val difficultySummary = stringResource(
		R.string.encounter_difficulty_accessibility_desc,
		ratingLabel,
		encounterDifficulty.adjustedXp
	)

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.semantics {
				contentDescription = difficultySummary
			},
		colors = CardDefaults.cardColors(
			containerColor = difficultyContainerColor(encounterDifficulty.rating)
		)
	) {
		Column(modifier = Modifier.padding(12.dp)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					stringResource(R.string.encounter_difficulty_label),
					style = MaterialTheme.typography.labelMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.semantics { heading() }
				)
				Text(
					ratingLabel,
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold,
					color = difficultyTextColor(encounterDifficulty.rating)
				)
			}
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				EncounterDifficulty.getDifficultyDescription(encounterDifficulty.rating),
				style = MaterialTheme.typography.bodySmall,
				fontSize = 11.sp,
				lineHeight = 14.sp
			)
			Spacer(modifier = Modifier.height(8.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Column {
					Text(
						stringResource(
							R.string.party_level_label,
							encounterDifficulty.averagePartyLevel
						),
						fontSize = 10.sp
					)
					Text(
						stringResource(R.string.party_size_label, encounterDifficulty.partySize),
						fontSize = 10.sp
					)
				}
				Column(horizontalAlignment = Alignment.End) {
					Text(
						stringResource(
							R.string.monsters_count_label,
							encounterDifficulty.monsterCount
						),
						fontSize = 10.sp
					)
					Text(
						stringResource(R.string.adjusted_xp_label, encounterDifficulty.adjustedXp),
						fontSize = 10.sp
					)
				}
			}
		}
	}
}

@Composable
private fun difficultyContainerColor(rating: DifficultyRating): Color {
	return when (rating) {
		DifficultyRating.TRIVIAL -> MaterialTheme.colorScheme.surfaceVariant
		DifficultyRating.EASY -> MaterialTheme.colorScheme.primaryContainer
		DifficultyRating.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer
		DifficultyRating.HARD -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
		DifficultyRating.DEADLY -> MaterialTheme.colorScheme.errorContainer
		DifficultyRating.BEYOND_DEADLY -> MaterialTheme.colorScheme.error
	}
}

@Composable
private fun difficultyTextColor(rating: DifficultyRating): Color {
	return when (rating) {
		DifficultyRating.TRIVIAL, DifficultyRating.EASY -> MaterialTheme.colorScheme.onPrimaryContainer
		DifficultyRating.MEDIUM -> MaterialTheme.colorScheme.onTertiaryContainer
		DifficultyRating.HARD, DifficultyRating.DEADLY -> MaterialTheme.colorScheme.onErrorContainer
		DifficultyRating.BEYOND_DEADLY -> MaterialTheme.colorScheme.onError
	}
}

