package io.github.velyene.loreweaver.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.SessionRecord
import java.text.SimpleDateFormat
import java.util.Date

@Composable
internal fun LatestCompletedSessionSection(
	session: SessionRecord?,
	dateFormat: SimpleDateFormat,
	onSessionClick: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	session ?: return

	HomeSectionHeader(stringResource(R.string.home_latest_session_title))
	HomeDetailItem(
		title = session.title,
		subtitle = stringResource(
			R.string.home_latest_session_saved_at,
			dateFormat.format(Date(session.date)),
		),
		icon = Icons.Default.History,
		modifier = modifier,
		onClickLabel = stringResource(R.string.open_session_action, session.title),
		onClick = { onSessionClick(session.id) },
	)
}


