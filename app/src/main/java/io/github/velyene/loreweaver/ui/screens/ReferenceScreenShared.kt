/*
 * FILE: ReferenceScreenShared.kt
 *
 * TABLE OF CONTENTS:
 * 1. Shared master-detail scaffolding
 * 2. Reference detail layout
 * 3. Reference Text Actions (Copy/Share)
 */

package io.github.velyene.loreweaver.ui.screens

import android.content.ClipData
import android.content.Intent
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
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

// --- Section 3: Reference Text Actions ---

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

private fun copyReferenceText(
	clipboard: Clipboard,
	coroutineScope: CoroutineScope,
	text: String
) {
	coroutineScope.launch {
		clipboard.setClipEntry(ClipData.newPlainText("reference", text).toClipEntry())
	}
}
