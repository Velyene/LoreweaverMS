package com.example.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.loreweaver.R

@Composable
internal fun ReferenceSearchStatusRow(
	searchQuery: String,
	appliedSearchQuery: String,
	isSearchPending: Boolean,
	visibleResultsCount: Int?
) {
	if (searchQuery.isBlank() && !isSearchPending) return

	val message = when {
		isSearchPending -> stringResource(R.string.reference_search_updating)
		visibleResultsCount != null -> stringResource(
			R.string.reference_search_results_count,
			visibleResultsCount,
			appliedSearchQuery
		)

		else -> stringResource(R.string.reference_search_results_ready, appliedSearchQuery)
	}

	Text(
		text = message,
		style = MaterialTheme.typography.bodySmall,
		color = MaterialTheme.colorScheme.onSurfaceVariant,
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
			.semantics { liveRegion = LiveRegionMode.Polite }
	)
}

@Composable
internal fun FavoritesFilterRow(
	showFavoritesOnly: Boolean,
	favoritesCount: Int,
	onToggleFavoritesOnly: () -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
		horizontalArrangement = Arrangement.End
	) {
		FilterChip(
			selected = showFavoritesOnly,
			onClick = onToggleFavoritesOnly,
			label = {
				Text(
					text = if (favoritesCount > 0) {
						stringResource(R.string.reference_favorites_only_with_count, favoritesCount)
					} else {
						stringResource(R.string.reference_favorites_only)
					},
					fontWeight = if (showFavoritesOnly) FontWeight.Bold else FontWeight.Normal
				)
			}
		)
	}
}

@Composable
internal fun SearchBar(
	query: String,
	onQueryChange: (String) -> Unit,
	onClear: () -> Unit
) {
	OutlinedTextField(
		value = query,
		onValueChange = onQueryChange,
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp),
		placeholder = { Text(stringResource(R.string.reference_search_hint)) },
		leadingIcon = { Icon(Icons.Default.Search, stringResource(R.string.search_button)) },
		trailingIcon = { SearchBarTrailingIcon(query = query, onClear = onClear) },
		singleLine = true
	)
}

@Composable
private fun SearchBarTrailingIcon(
	query: String,
	onClear: () -> Unit
) {
	if (query.isEmpty()) return

	IconButton(onClick = onClear) {
		Icon(Icons.Default.Close, stringResource(R.string.clear_button))
	}
}
