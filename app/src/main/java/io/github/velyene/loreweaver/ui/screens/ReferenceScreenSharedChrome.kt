/*
 * FILE: ReferenceScreenSharedChrome.kt
 *
 * TABLE OF CONTENTS:
 * 1. Shared Reference Screen Chrome
 * 2. Search and Header Composables
 * 3. Shared Empty and Section States
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.theme.LoreGold

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
internal fun ReferenceDetailHeader(
	title: String,
	isFavorite: Boolean,
	onToggleFavorite: () -> Unit,
	actions: ReferenceTextActions,
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
			IconButton(onClick = actions.onCopy) {
				Icon(
					imageVector = Icons.Default.ContentCopy,
					contentDescription = stringResource(R.string.reference_copy_to_clipboard)
				)
			}
			IconButton(onClick = actions.onShare) {
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
internal fun BackToReferenceListButton(onBack: () -> Unit) {
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
