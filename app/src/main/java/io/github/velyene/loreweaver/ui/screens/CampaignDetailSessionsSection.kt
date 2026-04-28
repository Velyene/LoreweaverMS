package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.SessionRecord
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal

@Composable
internal fun SessionHistoryList(sessions: List<SessionRecord>) {
	if (sessions.isEmpty()) {
		CenteredEmptyState(stringResource(R.string.sessions_empty_message))
	} else {
		val listState = rememberLazyListState()

		LazyColumn(
			state = listState,
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp)
				.visibleVerticalScrollbar(listState)
		) {
			items(sessions, key = { it.id }) { session ->
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 4.dp)
						.background(
							MaterialTheme.colorScheme.surfaceVariant,
							RoundedCornerShape(8.dp)
						)
						.padding(12.dp)
				) {
					Row(verticalAlignment = Alignment.CenterVertically) {
						TextSessionHistoryIcon()
						Spacer(modifier = Modifier.size(12.dp))
						Column {
							Text(
								text = session.title,
								color = MaterialTheme.colorScheme.onSurfaceVariant,
								fontSize = 14.sp
							)
							Text(
								text = stringResource(R.string.session_entries_count, session.log.size),
								color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
								fontSize = 11.sp
							)
						}
					}
				}
			}
		}
	}
}

@Composable
private fun TextSessionHistoryIcon() {
	androidx.compose.material3.Icon(
		imageVector = Icons.Default.History,
		contentDescription = null,
		tint = ArcaneTeal,
		modifier = Modifier.size(18.dp)
	)
}
