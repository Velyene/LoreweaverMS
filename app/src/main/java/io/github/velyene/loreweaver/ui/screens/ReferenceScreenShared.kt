/*
 * FILE: ReferenceScreenShared.kt
 *
 * TABLE OF CONTENTS:
 * 1. Shared master-detail scaffolding
 * 2. Reference headers, actions, and detail chrome
 * 3. Shared cards, bullet rows, and detail rows
 * 4. Query, share-text, and table helpers
 * 5. Empty-state presentation helpers
 */

package io.github.velyene.loreweaver.ui.screens

import android.content.ClipData
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.ReferenceTable
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.LoreGold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun <T> ReferenceMasterDetailContent(
	items: List<T>,
	selectedItem: T?,
	listState: LazyListState,
	showFavoritesOnly: Boolean,
	searchQuery: String,
	favoritesCount: Int,
	emptyFavoritesMessage: String,
	emptySearchMessage: String,
	detailContent: @Composable (T) -> Unit,
	listContent: LazyListScope.() -> Unit
) {
	if (selectedItem != null) {
		detailContent(selectedItem)
		return
	}

	if (items.isEmpty()) {
		ReferenceEmptyState(
			showFavoritesOnly = showFavoritesOnly,
			searchQuery = searchQuery,
			favoritesCount = favoritesCount,
			emptyFavoritesMessage = emptyFavoritesMessage,
			emptySearchMessage = emptySearchMessage
		)
		return
	}

	LazyColumn(
		state = listState,
		modifier = Modifier.fillMaxSize(),
		contentPadding = PaddingValues(16.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
		content = listContent
	)
}

@Composable
internal fun ReferenceSectionHeader(title: String) {
	Text(
		text = title,
		style = MaterialTheme.typography.titleMedium,
		fontWeight = FontWeight.Bold,
		modifier = Modifier.semantics { heading() }
	)
}

@Composable
internal fun ReferenceNoResultsState() {
	ReferenceCenteredMessage(message = stringResource(R.string.reference_no_results))
}

@Composable
internal fun ReferenceTitleWithShare(
	title: String,
	onShare: () -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = title,
			style = MaterialTheme.typography.titleLarge,
			fontWeight = FontWeight.Bold,
			modifier = Modifier
				.weight(1f)
				.semantics { heading() }
		)
		IconButton(onClick = onShare) {
			Icon(
				imageVector = Icons.Default.Share,
				contentDescription = stringResource(R.string.reference_share)
			)
		}
	}
}

@Composable
internal fun ReferenceFavoriteIconButton(
	isFavorite: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	iconSize: Dp = 24.dp
) {
	IconButton(onClick = onClick, modifier = modifier) {
		Icon(
			imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
			contentDescription = stringResource(R.string.reference_toggle_favorite),
			tint = if (isFavorite) LoreGold else MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.size(iconSize)
		)
	}
}

@Composable
internal fun ReferenceDetailLayout(
	onBack: () -> Unit,
	content: LazyListScope.() -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp)
	) {
		BackToReferenceListButton(onBack)
		Spacer(modifier = Modifier.height(8.dp))
		LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
	}
}

internal data class ReferenceTextActions(
	val onCopy: () -> Unit,
	val onShare: () -> Unit
)

@Composable
internal fun rememberReferenceTextActions(text: String): ReferenceTextActions {
	val clipboard = LocalClipboard.current
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()
	val shareChooserTitle = stringResource(R.string.reference_share_chooser_title)
	return ReferenceTextActions(
		onCopy = { copyReferenceText(clipboard, coroutineScope, text) },
		onShare = {
			shareReferenceText(
				context = context,
				chooserTitle = shareChooserTitle,
				text = text
			)
		}
	)
}

@Composable
internal fun ReferenceDetailHeader(
	title: String,
	isFavorite: Boolean,
	onToggleFavorite: () -> Unit,
	onCopy: () -> Unit,
	onShare: () -> Unit,
	showFavoriteAction: Boolean = true,
	leadingActions: @Composable RowScope.() -> Unit = {}
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = title,
			style = MaterialTheme.typography.headlineMedium,
			fontWeight = FontWeight.Bold,
			modifier = Modifier
				.weight(1f)
				.semantics { heading() }
		)
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(4.dp)
		) {
			leadingActions()
			if (showFavoriteAction) {
				ReferenceFavoriteIconButton(isFavorite = isFavorite, onClick = onToggleFavorite)
			}
			IconButton(onClick = onCopy) {
				Icon(
					imageVector = Icons.Default.ContentCopy,
					contentDescription = stringResource(R.string.reference_copy_to_clipboard)
				)
			}
			IconButton(onClick = onShare) {
				Icon(
					imageVector = Icons.Default.Share,
					contentDescription = stringResource(R.string.reference_share)
				)
			}
		}
	}
}

@Composable
internal fun ReferenceTypeBadge(label: String) {
	Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
		Text(label.uppercase(), modifier = Modifier.padding(4.dp))
	}
}

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

internal fun matchesQuery(query: String, vararg values: String): Boolean {
	return query.isBlank() || values.any { it.contains(query, ignoreCase = true) }
}

internal fun Enum<*>.toDisplayLabel(): String = name
	.lowercase()
	.replace('_', ' ')
	.split(' ')
	.joinToString(" ") { token -> token.replaceFirstChar { it.uppercase() } }

internal fun shareReferenceText(
	context: android.content.Context,
	chooserTitle: String,
	text: String
) {
	val shareIntent = Intent(Intent.ACTION_SEND).apply {
		type = "text/plain"
		putExtra(Intent.EXTRA_TEXT, text)
	}
	context.startActivity(Intent.createChooser(shareIntent, chooserTitle))
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

internal fun ReferenceTable.matchesQuery(query: String): Boolean {
	return matchesQuery(query, title, *columns.toTypedArray()) ||
		rows.any { row -> row.any { it.contains(query, ignoreCase = true) } }
}

@Composable
private fun ReferenceEmptyState(
	showFavoritesOnly: Boolean,
	searchQuery: String,
	favoritesCount: Int,
	emptyFavoritesMessage: String,
	emptySearchMessage: String
) {
	ReferenceCenteredMessage(
		message = when {
			showFavoritesOnly && favoritesCount == 0 -> emptyFavoritesMessage
			showFavoritesOnly && searchQuery.isNotBlank() -> emptySearchMessage
			else -> stringResource(R.string.reference_no_results)
		}
	)
}

@Composable
internal fun ReferenceCenteredMessage(message: String) {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		contentAlignment = Alignment.Center
	) {
		Text(
			text = message,
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
	}
}

@Composable
private fun BackToReferenceListButton(onBack: () -> Unit) {
	TextButton(onClick = onBack) {
		Icon(
			Icons.AutoMirrored.Filled.ArrowBack,
			contentDescription = stringResource(R.string.back_button),
			modifier = Modifier.size(16.dp)
		)
		Spacer(modifier = Modifier.width(4.dp))
		Text(stringResource(R.string.reference_back_to_list))
	}
}

private fun copyReferenceText(
	clipboard: Clipboard,
	coroutineScope: CoroutineScope,
	text: String
) {
	coroutineScope.launch {
		clipboard.setClipEntry(ClipData.newPlainText("reference", text).toClipEntry())
	}
}
