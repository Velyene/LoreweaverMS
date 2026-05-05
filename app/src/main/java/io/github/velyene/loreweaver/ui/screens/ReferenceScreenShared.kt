/*
 * FILE: ReferenceScreenShared.kt
 *
 * TABLE OF CONTENTS:
 * 1. Shared master-detail scaffolding
 * 2. Shared detail-layout scaffold
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R

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
		modifier = Modifier
			.fillMaxSize()
			.visibleVerticalScrollbar(listState),
		contentPadding = PaddingValues(16.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
		content = listContent
	)
}

@Composable
internal fun ReferenceDetailLayout(
	onBack: () -> Unit,
	content: LazyListScope.() -> Unit
) {
	val detailListState = rememberLazyListState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp)
	) {
		BackToReferenceListButton(onBack)
		Spacer(modifier = Modifier.height(8.dp))
		LazyColumn(
			state = detailListState,
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.visibleVerticalScrollbar(detailListState),
			verticalArrangement = Arrangement.spacedBy(12.dp),
			content = content
		)
	}
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

