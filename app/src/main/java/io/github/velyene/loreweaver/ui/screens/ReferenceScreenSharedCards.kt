package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.domain.util.ReferenceTable
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal

@Composable
internal fun DetailTextSection(title: String, body: String) {
	Text(
		title,
		style = MaterialTheme.typography.titleMedium,
		fontWeight = FontWeight.Bold,
		modifier = Modifier.semantics { heading() }
	)
	Text(body, style = MaterialTheme.typography.bodyMedium)
}

@Composable
internal fun InfoCard(title: String, body: String) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			Text(
				title,
				style = MaterialTheme.typography.titleSmall,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.semantics { heading() }
			)
			Text(body, style = MaterialTheme.typography.bodyMedium)
		}
	}
}

@Composable
internal fun BulletListCard(items: List<String>) {
	Card(
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant
		)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			items.forEach { item ->
				ReferenceBulletRow(text = item)
			}
		}
	}
}

@Composable
internal fun DetailRow(label: String, value: String) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
		Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
	}
}

@Composable
internal fun StackedDetailRow(label: String, value: String) {
	Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
		Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
		Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
	}
}

@Composable
internal fun ReferenceBulletRow(text: String) {
	Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
		Text("â€¢", fontWeight = FontWeight.Bold, color = ArcaneTeal)
		Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
	}
}

@Composable
internal fun ReferenceTableCard(table: ReferenceTable) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = table.title,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.semantics { heading() }
			)
			Text(
				text = table.columns.joinToString(" â€¢ "),
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
			)

			table.rows.forEach { row ->
				val cells = table.columns.zip(row).filter { (_, value) ->
					value.isNotBlank() && value != "â€”"
				}

				if (cells.isNotEmpty()) {
					Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
						Column(
							modifier = Modifier.padding(12.dp),
							verticalArrangement = Arrangement.spacedBy(4.dp)
						) {
							Text(
								text = "${cells.first().first}: ${cells.first().second}",
								style = MaterialTheme.typography.titleSmall,
								fontWeight = FontWeight.Bold
							)
							cells.drop(1).forEach { (label, value) ->
								StackedDetailRow(label, value)
							}
						}
					}
				}
			}
		}
	}
}

