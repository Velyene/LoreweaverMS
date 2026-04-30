/*
 * FILE: ReferenceScreenObjects.kt
 *
 * TABLE OF CONTENTS:
 * 1. Objects reference screen entry point
 * 2. Armor Class and hit point reference cards
 * 3. Common object cards and stat rows
 * 4. Share-text support for the objects reference section
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.ObjectStats
import io.github.velyene.loreweaver.domain.util.ObjectStatsReference
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal

@Composable
internal fun ObjectsContent(listState: LazyListState) {
	val context = LocalContext.current
	val shareChooserTitle = stringResource(R.string.reference_share_chooser_title)
	LazyColumn(
		state = listState,
		modifier = Modifier
			.fillMaxSize()
			.visibleVerticalScrollbar(listState),
		contentPadding = PaddingValues(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		item {
			ReferenceTitleWithShare(
				title = stringResource(R.string.reference_objects_ac_by_substance_title),
				onShare = {
					shareReferenceText(
						context = context,
						chooserTitle = shareChooserTitle,
						text = buildObjectsReferenceShareText()
					)
				}
			)
		}

		item { ObjectsAcTableCard() }

		item {
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				stringResource(R.string.reference_objects_hp_by_size_title),
				style = MaterialTheme.typography.titleLarge,
				fontWeight = FontWeight.Bold
			)
		}

		item { ObjectsHpTableCard() }

		item {
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				stringResource(R.string.reference_objects_common_objects_title),
				style = MaterialTheme.typography.titleLarge,
				fontWeight = FontWeight.Bold
			)
		}

		items(
			ObjectStatsReference.COMMON_OBJECTS,
			key = { "${it.substance}-${it.ac}-${it.hitPoints}" }
		) { obj ->
			ObjectsCommonObjectCard(obj)
		}

		item {
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				stringResource(R.string.reference_objects_damage_type_guidance_title),
				style = MaterialTheme.typography.titleLarge,
				fontWeight = FontWeight.Bold
			)
		}

		item { ObjectsDamageTypeCard() }

		item {
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				stringResource(R.string.reference_objects_massive_objects_title),
				style = MaterialTheme.typography.titleLarge,
				fontWeight = FontWeight.Bold
			)
		}

		item { ObjectsGuidanceCard(ObjectStatsReference.getLargeObjectGuidance()) }

		item {
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				stringResource(R.string.reference_objects_damage_thresholds_title),
				style = MaterialTheme.typography.titleLarge,
				fontWeight = FontWeight.Bold
			)
		}

		item { ObjectsGuidanceCard(ObjectStatsReference.getDamageThresholdInfo()) }
	}
}

@Composable
private fun ObjectsAcTableCard() {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			DetailRow("Cloth, Paper, Rope", "AC 11")
			DetailRow("Crystal, Glass, Ice", "AC 13")
			DetailRow("Wood, Bone", "AC 15")
			DetailRow("Stone", "AC 17")
			DetailRow("Iron, Steel", "AC 19")
			DetailRow("Mithral", "AC 21")
			DetailRow("Adamantine", "AC 23")
		}
	}
}

@Composable
private fun ObjectsHpTableCard() {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			Text(
				stringResource(R.string.reference_objects_fragile_resilient),
				fontWeight = FontWeight.Bold,
				fontSize = 12.sp
			)
			HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
			DetailRow("Tiny (bottle, lock)", "2 (1d4) / 5 (2d4)")
			DetailRow("Small (chest, lute)", "3 (1d6) / 10 (3d6)")
			DetailRow("Medium (barrel)", "4 (1d8) / 18 (4d8)")
			DetailRow("Large (cart, window)", "5 (1d10) / 27 (5d10)")
		}
	}
}

@Composable
private fun ObjectsCommonObjectCard(obj: ObjectStats) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(modifier = Modifier.padding(12.dp)) {
			Text(obj.substance, fontWeight = FontWeight.Bold)
			Spacer(modifier = Modifier.height(4.dp))
			Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
				Text(stringResource(R.string.reference_objects_ac_value, obj.ac), fontSize = 12.sp)
				Text(stringResource(R.string.reference_objects_hp_value, obj.hitPoints), fontSize = 12.sp)
				ObjectsThresholdText(obj.damageThreshold)
			}
		}
	}
}

@Composable
private fun ObjectsThresholdText(damageThreshold: Int) {
	if (damageThreshold <= 0) return

	Text(
		stringResource(R.string.reference_objects_threshold_value, damageThreshold),
		fontSize = 12.sp,
		color = ArcaneTeal
	)
}

@Composable
private fun ObjectsDamageTypeCard() {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			ObjectStatsReference.getDamageTypeGuidance().forEach { (type, description) ->
				Text(type, fontWeight = FontWeight.Bold, fontSize = 12.sp)
				Text(description, style = MaterialTheme.typography.bodySmall)
			}
		}
	}
}

@Composable
private fun ObjectsGuidanceCard(text: String) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Text(
			text = text,
			modifier = Modifier.padding(12.dp),
			style = MaterialTheme.typography.bodySmall
		)
	}
}

private fun buildObjectsReferenceShareText(): String = buildString {
	appendLine("Objects Reference")
	appendLine("AC by Substance")
	appendLine("Cloth, Paper, Rope: AC 11")
	appendLine("Crystal, Glass, Ice: AC 13")
	appendLine("Wood, Bone: AC 15")
	appendLine("Stone: AC 17")
	appendLine("Iron, Steel: AC 19")
	appendLine("Mithral: AC 21")
	appendLine("Adamantine: AC 23")
	appendLine()
	appendLine("Common Objects")
	ObjectStatsReference.COMMON_OBJECTS.forEach { obj ->
		val thresholdSuffix = if (obj.damageThreshold > 0) {
			", Threshold ${obj.damageThreshold}"
		} else {
			""
		}
		appendLine("${obj.substance}: AC ${obj.ac}, HP ${obj.hitPoints}$thresholdSuffix")
	}
}.trim()
