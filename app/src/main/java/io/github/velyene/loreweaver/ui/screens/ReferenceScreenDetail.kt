/*
 * FILE: ReferenceScreenDetail.kt
 *
 * TABLE OF CONTENTS:
 * 1. Generic reference detail composable
 * 2. Detail section rendering helpers
 * 3. Clipboard/share text formatting helpers
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.ReferenceDetailContent
import io.github.velyene.loreweaver.domain.util.ReferenceDetailSection

@Composable
internal fun GenericReferenceDetailView(
	detail: ReferenceDetailContent,
	onBack: () -> Unit
) {
	val detailClipboardText = remember(detail) { buildReferenceDetailClipboardText(detail) }
	val referenceTextActions = rememberReferenceTextActions(detailClipboardText)

	ReferenceDetailLayout(onBack = onBack) {
		item {
			ReferenceDetailHeader(
				title = detail.title,
				isFavorite = false,
				onToggleFavorite = {},
				onCopy = referenceTextActions.onCopy,
				onShare = referenceTextActions.onShare,
				showFavoriteAction = false
			)
		}

		detail.subtitle.takeIf { it.isNotBlank() }?.let { subtitle ->
			item {
				Text(
					text = subtitle,
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.secondary
				)
			}
		}

		detail.overview?.takeIf { it.isNotBlank() }?.let { overview ->
			item { DetailTextSection(title = stringResource(R.string.reference_overview_title), body = overview) }
		}

		if (detail.statRows.isNotEmpty()) {
			item { GenericReferenceDetailStatsCard(detail.statRows) }
		}

		detail.sections.forEach { section ->
			item(key = "${detail.title}-${section.title}") {
				GenericReferenceDetailSectionCard(section)
			}
		}

		detail.tables.forEach { table ->
			item(key = "${detail.title}-${table.title}") {
				ReferenceTableCard(table)
			}
		}

		detail.note?.takeIf { it.isNotBlank() }?.let { note ->
			item {
				Text(
					text = note,
					style = MaterialTheme.typography.labelMedium,
					color = MaterialTheme.colorScheme.secondary
				)
			}
		}
	}
}

@Composable
private fun GenericReferenceDetailStatsCard(statRows: List<Pair<String, String>>) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			statRows.forEach { (label, value) ->
				DetailRow(label, value)
			}
		}
	}
}

@Composable
private fun GenericReferenceDetailSectionCard(section: ReferenceDetailSection) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = section.title,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			section.body?.takeIf { it.isNotBlank() }?.let { body ->
				Text(text = body, style = MaterialTheme.typography.bodyMedium)
			}
			if (section.bullets.isNotEmpty()) {
				section.bullets.forEach { bullet ->
					ReferenceBulletRow(text = bullet)
				}
			}
		}
	}
}


private fun buildReferenceDetailClipboardText(detail: ReferenceDetailContent): String =
	buildString {
		appendLine(detail.title)
		if (detail.subtitle.isNotBlank()) {
			appendLine(detail.subtitle)
		}
		appendLine()
		detail.overview?.takeIf { it.isNotBlank() }?.let { overview ->
			appendLine(overview)
			appendLine()
		}
		if (detail.statRows.isNotEmpty()) {
			detail.statRows.forEach { (label, value) ->
				appendLine("$label: $value")
			}
			appendLine()
		}
		detail.sections.forEach { section ->
			appendLine(section.title)
			section.body?.takeIf { it.isNotBlank() }?.let { body -> appendLine(body) }
			section.bullets.forEach { bullet -> appendLine("• $bullet") }
			appendLine()
		}
		detail.tables.forEach { table ->
			appendLine(table.title)
			appendLine(table.columns.joinToString(" • "))
			table.rows.forEach { row -> appendLine(row.joinToString(" • ")) }
			appendLine()
		}
		detail.note?.takeIf { it.isNotBlank() }?.let { note ->
			appendLine(note)
		}
	}.trim()
