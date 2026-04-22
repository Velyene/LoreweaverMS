/*
 * FILE: ReferenceScreenHazards.kt
 *
 * TABLE OF CONTENTS:
 * 1. Trap, poison, and disease content entry points
 * 2. Shared card, badge, and detail-view components
 * 3. Stats cards for hazard detail layouts
 * 4. Clipboard/share text builders and helper extensions
 */

package com.example.loreweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loreweaver.R
import com.example.loreweaver.domain.util.DiseaseTemplate
import com.example.loreweaver.domain.util.PoisonReference
import com.example.loreweaver.domain.util.PoisonTemplate
import com.example.loreweaver.domain.util.TrapDanger
import com.example.loreweaver.domain.util.TrapReference
import com.example.loreweaver.domain.util.TrapTemplate
import com.example.loreweaver.ui.theme.LoreGold

@Composable
internal fun TrapsContent(
	traps: List<TrapTemplate>,
	selectedTrap: TrapTemplate?,
	listState: LazyListState,
	showFavoritesOnly: Boolean,
	searchQuery: String,
	favoriteTrapNames: Set<String>,
	onTrapSelected: (TrapTemplate?) -> Unit,
	onToggleFavorite: (String) -> Unit
) {
	ReferenceMasterDetailContent(
		items = traps,
		selectedItem = selectedTrap,
		listState = listState,
		showFavoritesOnly = showFavoritesOnly,
		searchQuery = searchQuery,
		favoritesCount = favoriteTrapNames.size,
		emptyFavoritesMessage = stringResource(R.string.reference_no_favorite_traps),
		emptySearchMessage = stringResource(R.string.reference_no_favorite_search_results),
		detailContent = { trap ->
			TrapDetailView(
				trap = trap,
				isFavorite = trap.name in favoriteTrapNames,
				onBack = { onTrapSelected(null) },
				onToggleFavorite = { onToggleFavorite(trap.name) }
			)
		}
	) {
		item { TrapDangerGuideCard() }

		items(traps, key = { it.name }) { trap ->
			TrapCard(
				trap = trap,
				isFavorite = trap.name in favoriteTrapNames,
				onClick = { onTrapSelected(trap) },
				onToggleFavorite = { onToggleFavorite(trap.name) }
			)
		}
	}
}

@Composable
private fun TrapCard(
	trap: TrapTemplate,
	isFavorite: Boolean,
	onClick: () -> Unit,
	onToggleFavorite: () -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant
		)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			TrapCardHeader(
				trap = trap,
				isFavorite = isFavorite,
				onToggleFavorite = onToggleFavorite
			)
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				"${trap.type} • Detection DC ${trap.detectionDC} • Save DC ${trap.saveDC}",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				trap.description,
				style = MaterialTheme.typography.bodyMedium,
				maxLines = 2,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
			)
		}
	}
}

@Composable
private fun TrapCardHeader(
	trap: TrapTemplate,
	isFavorite: Boolean,
	onToggleFavorite: () -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = trap.name,
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.Bold
		)
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(6.dp)
		) {
			TrapDangerBadge(trap.danger)
			ReferenceFavoriteIconButton(
				isFavorite = isFavorite,
				onClick = onToggleFavorite,
				modifier = Modifier.size(28.dp),
				iconSize = 18.dp
			)
		}
	}
}

@Composable
private fun TrapDangerBadge(danger: TrapDanger) {
	Badge(containerColor = trapDangerColor(danger)) {
		Text(danger.name, fontSize = 10.sp)
	}
}

@Composable
private fun trapDangerColor(danger: TrapDanger) = when (danger) {
	TrapDanger.SETBACK -> MaterialTheme.colorScheme.primaryContainer
	TrapDanger.DANGEROUS -> MaterialTheme.colorScheme.tertiaryContainer
	TrapDanger.DEADLY -> MaterialTheme.colorScheme.errorContainer
}

@Composable
private fun TrapDangerGuideCard() {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Text("Trap Stats by Danger Level", fontWeight = FontWeight.Bold)
			HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
			TrapDangerRow(TrapDanger.SETBACK)
			TrapDangerRow(TrapDanger.DANGEROUS)
			TrapDangerRow(TrapDanger.DEADLY)
		}
	}
}

@Composable
private fun TrapDangerRow(danger: TrapDanger) {
	val dcRange = TrapReference.getSaveDC(danger)
	val attackRange = TrapReference.getAttackBonus(danger)
	val damageL1 = TrapReference.getDamage(1, danger)
	val damageL11 = TrapReference.getDamage(11, danger)
	val label = danger.name.lowercase().replaceFirstChar { it.uppercase() }
	Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
		Text(label, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
		Text(
			"Save DC ${dcRange.first}–${dcRange.last}  •  Attack +${attackRange.first}–+${attackRange.last}",
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		Text(
			"Damage (lv 1–4) $damageL1  •  (lv 11–16) $damageL11",
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrapDetailView(
	trap: TrapTemplate,
	isFavorite: Boolean,
	onBack: () -> Unit,
	onToggleFavorite: () -> Unit
) {
	val trapClipboardText = remember(trap) { buildTrapClipboardText(trap) }
	val referenceTextActions = rememberReferenceTextActions(trapClipboardText)

	ReferenceDetailLayout(onBack = onBack) {
		item {
			ReferenceDetailHeader(
				title = trap.name,
				isFavorite = isFavorite,
				onToggleFavorite = onToggleFavorite,
				onCopy = referenceTextActions.onCopy,
				onShare = referenceTextActions.onShare
			)
		}

		item { TrapDetailBadges(trap) }
		item { TrapStatsCard(trap) }
		item { DetailTextSection("Description", trap.description) }
		item { DetailTextSection("Effect", trap.effect) }
		item { DetailTextSection("Disarm Method", trap.disarmMethod) }
	}
}

@Composable
internal fun PoisonsContent(
	poisons: List<PoisonTemplate>,
	selectedPoison: PoisonTemplate?,
	listState: LazyListState,
	showFavoritesOnly: Boolean,
	searchQuery: String,
	favoritePoisonNames: Set<String>,
	onPoisonSelected: (PoisonTemplate?) -> Unit,
	onToggleFavorite: (String) -> Unit
) {
	ReferenceMasterDetailContent(
		items = poisons,
		selectedItem = selectedPoison,
		listState = listState,
		showFavoritesOnly = showFavoritesOnly,
		searchQuery = searchQuery,
		favoritesCount = favoritePoisonNames.size,
		emptyFavoritesMessage = stringResource(R.string.reference_no_favorite_poisons),
		emptySearchMessage = stringResource(R.string.reference_no_favorite_search_results),
		detailContent = { poison ->
			PoisonDetailView(
				poison = poison,
				isFavorite = poison.name in favoritePoisonNames,
				onBack = { onPoisonSelected(null) },
				onToggleFavorite = { onToggleFavorite(poison.name) }
			)
		}
	) {
		items(poisons, key = { it.name }) { poison ->
			PoisonCard(
				poison = poison,
				isFavorite = poison.name in favoritePoisonNames,
				onClick = { onPoisonSelected(poison) },
				onToggleFavorite = { onToggleFavorite(poison.name) }
			)
		}
	}
}

@Composable
private fun PoisonCard(
	poison: PoisonTemplate,
	isFavorite: Boolean,
	onClick: () -> Unit,
	onToggleFavorite: () -> Unit
) {
	val poisonTypeLabel = poison.type.toDisplayLabel()
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					poison.name,
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.weight(1f)
				)
				Row(verticalAlignment = Alignment.CenterVertically) {
					Text(poison.pricePerDose, color = LoreGold, fontWeight = FontWeight.Bold)
					IconButton(onClick = onToggleFavorite, modifier = Modifier.size(28.dp)) {
						Icon(
							imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
							contentDescription = stringResource(R.string.reference_toggle_favorite),
							tint = if (isFavorite) LoreGold else MaterialTheme.colorScheme.onSurfaceVariant,
							modifier = Modifier.size(18.dp)
						)
					}
				}
			}
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				"$poisonTypeLabel • DC ${poison.saveDC}",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			if (poison.damageOnFail != "0") {
				Text(
					"Damage: ${poison.damageOnFail} ${poison.damageType}",
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.error
				)
			}
		}
	}
}

@Composable
private fun PoisonDetailView(
	poison: PoisonTemplate,
	isFavorite: Boolean,
	onBack: () -> Unit,
	onToggleFavorite: () -> Unit
) {
	val poisonClipboardText = remember(poison) { buildPoisonClipboardText(poison) }
	val referenceTextActions = rememberReferenceTextActions(poisonClipboardText)
	val poisonTypeLabel = poison.type.toDisplayLabel()

	ReferenceDetailLayout(onBack = onBack) {
		item {
			ReferenceDetailHeader(
				title = poison.name,
				isFavorite = isFavorite,
				onToggleFavorite = onToggleFavorite,
				onCopy = referenceTextActions.onCopy,
				onShare = referenceTextActions.onShare,
				leadingActions = {
					Text(
						text = poison.pricePerDose,
						color = LoreGold,
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.Bold
					)
				}
			)
		}

		item {
			ReferenceTypeBadge(poisonTypeLabel)
		}

		item {
			Text(
				text = PoisonReference.getTypeDescription(poison.type),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}

		item { PoisonStatsCard(poison) }

		if (poison.additionalEffect.isNotEmpty()) {
			item { DetailTextSection("Effect", poison.additionalEffect) }
		}

		item { DetailTextSection("Description", poison.description) }
	}
}

@Composable
internal fun DiseasesContent(
	diseases: List<DiseaseTemplate>,
	selectedDisease: DiseaseTemplate?,
	listState: LazyListState,
	showFavoritesOnly: Boolean,
	searchQuery: String,
	favoriteDiseaseNames: Set<String>,
	onDiseaseSelected: (DiseaseTemplate?) -> Unit,
	onToggleFavorite: (String) -> Unit
) {
	ReferenceMasterDetailContent(
		items = diseases,
		selectedItem = selectedDisease,
		listState = listState,
		showFavoritesOnly = showFavoritesOnly,
		searchQuery = searchQuery,
		favoritesCount = favoriteDiseaseNames.size,
		emptyFavoritesMessage = stringResource(R.string.reference_no_favorite_diseases),
		emptySearchMessage = stringResource(R.string.reference_no_favorite_search_results),
		detailContent = { disease ->
			DiseaseDetailView(
				disease = disease,
				isFavorite = disease.name in favoriteDiseaseNames,
				onBack = { onDiseaseSelected(null) },
				onToggleFavorite = { onToggleFavorite(disease.name) }
			)
		}
	) {
		items(diseases, key = { it.name }) { disease ->
			DiseaseCard(
				disease = disease,
				isFavorite = disease.name in favoriteDiseaseNames,
				onClick = { onDiseaseSelected(disease) },
				onToggleFavorite = { onToggleFavorite(disease.name) }
			)
		}
	}
}

@Composable
private fun DiseaseCard(
	disease: DiseaseTemplate,
	isFavorite: Boolean,
	onClick: () -> Unit,
	onToggleFavorite: () -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					disease.name,
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.weight(1f)
				)
				IconButton(onClick = onToggleFavorite, modifier = Modifier.size(28.dp)) {
					Icon(
						imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
						contentDescription = stringResource(R.string.reference_toggle_favorite),
						tint = if (isFavorite) LoreGold else MaterialTheme.colorScheme.onSurfaceVariant,
						modifier = Modifier.size(18.dp)
					)
				}
			}
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				"DC ${disease.saveDC} • Incubation: ${disease.incubationTime}",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				disease.symptoms,
				style = MaterialTheme.typography.bodyMedium,
				maxLines = 2,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
			)
		}
	}
}

@Composable
private fun DiseaseDetailView(
	disease: DiseaseTemplate,
	isFavorite: Boolean,
	onBack: () -> Unit,
	onToggleFavorite: () -> Unit
) {
	val diseaseClipboardText = remember(disease) { buildDiseaseClipboardText(disease) }
	val referenceTextActions = rememberReferenceTextActions(diseaseClipboardText)

	ReferenceDetailLayout(onBack = onBack) {
		item {
			ReferenceDetailHeader(
				title = disease.name,
				isFavorite = isFavorite,
				onToggleFavorite = onToggleFavorite,
				onCopy = referenceTextActions.onCopy,
				onShare = referenceTextActions.onShare
			)
		}

		item { DiseaseStatsCard(disease) }
		item { DetailTextSection("Symptoms", disease.symptoms) }
		item { DetailTextSection("Effects", disease.effects) }
		item { DetailTextSection("Progression", disease.progression) }
		item { DetailTextSection("Cure", disease.cure) }
	}
}

@Composable
private fun TrapDetailBadges(trap: TrapTemplate) {
	Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
		ReferenceTypeBadge(trap.type)
		Badge(containerColor = trapDangerColor(trap.danger)) {
			Text(trap.danger.name, modifier = Modifier.padding(4.dp))
		}
	}
}

@Composable
private fun TrapStatsCard(trap: TrapTemplate) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			DetailRow("Detection DC", trap.detectionDC.toString())
			DetailRow("Disarm DC", trap.disarmDC.toString())
			DetailRow(stringResource(R.string.reference_save_dc), trap.saveDC.toString())
			if (trap.attackBonus > 0) {
				DetailRow("Attack Bonus", "+${trap.attackBonus}")
			}
			DetailRow("Damage", "${trap.damage} ${trap.damageType}")
		}
	}
}

@Composable
private fun PoisonStatsCard(poison: PoisonTemplate) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			DetailRow(stringResource(R.string.reference_save_dc), poison.saveDC.toString())
			if (poison.damageOnFail != "0") {
				DetailRow("Damage (Failed Save)", "${poison.damageOnFail} ${poison.damageType}")
				DetailRow("Damage (Success)", poison.damageOnSuccess)
			}
			if (poison.duration.isNotEmpty()) {
				DetailRow("Duration", poison.duration)
			}
		}
	}
}

@Composable
private fun DiseaseStatsCard(disease: DiseaseTemplate) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			DetailRow(stringResource(R.string.reference_save_dc), disease.saveDC.toString())
			DetailRow("Incubation Time", disease.incubationTime)
			DetailRow("Transmission", disease.transmission)
		}
	}
}

private fun buildTrapClipboardText(trap: TrapTemplate): String = buildString {
	appendLine(trap.name)
	appendLine("${trap.type} • ${trap.danger.name}")
	appendLine("Detection DC: ${trap.detectionDC}")
	appendLine("Disarm DC: ${trap.disarmDC}")
	appendLine("Save DC: ${trap.saveDC}")
	if (trap.attackBonus > 0) appendLine("Attack Bonus: +${trap.attackBonus}")
	appendLine("Damage: ${trap.damage} ${trap.damageType}")
	appendLine()
	appendLine("Description: ${trap.description}")
	appendLine("Effect: ${trap.effect}")
	appendLine("Disarm Method: ${trap.disarmMethod}")
}.trim()

private fun buildPoisonClipboardText(poison: PoisonTemplate): String = buildString {
	appendLine(poison.name)
	appendLine("Type: ${poison.type.toDisplayLabel()}")
	appendLine("Price: ${poison.pricePerDose}")
	appendLine("Save DC: ${poison.saveDC}")
	appendPoisonDamageLines(poison)
	appendOptionalPoisonDetail(label = "Duration", value = poison.duration)
	appendOptionalPoisonDetail(label = "Effect", value = poison.additionalEffect)
	appendLine()
	appendLine("Description: ${poison.description}")
}.trim()

private fun StringBuilder.appendPoisonDamageLines(poison: PoisonTemplate) {
	if (poison.damageOnFail == "0") return
	appendLine("Failed Save: ${poison.damageOnFail} ${poison.damageType}")
	appendLine("Successful Save: ${poison.damageOnSuccess}")
}

private fun StringBuilder.appendOptionalPoisonDetail(label: String, value: String) {
	if (value.isNotEmpty()) {
		appendLine("$label: $value")
	}
}

private fun buildDiseaseClipboardText(disease: DiseaseTemplate): String = buildString {
	appendLine(disease.name)
	appendLine("Save DC: ${disease.saveDC}")
	appendLine("Incubation: ${disease.incubationTime}")
	appendLine("Transmission: ${disease.transmission}")
	appendLine()
	appendDiseaseClipboardDetails(disease)
}.trim()

private fun StringBuilder.appendDiseaseClipboardDetails(disease: DiseaseTemplate) {
	listOf(
		"Symptoms: ${disease.symptoms}",
		"Effects: ${disease.effects}",
		"Progression: ${disease.progression}",
		"Cure: ${disease.cure}"
	).forEach(::appendLine)
}
