package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.MutedText

@Composable
internal fun CenteredLoadingState(modifier: Modifier = Modifier) {
	val loadingLabel = stringResource(R.string.loading_label)

	Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
		CircularProgressIndicator(
			color = ArcaneTeal,
			modifier = Modifier.semantics {
				contentDescription = loadingLabel
			}
		)
	}
}

@Composable
internal fun CenteredEmptyState(
	message: String,
	modifier: Modifier = Modifier
) {
	Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
		Text(
			text = message,
			color = MutedText,
			style = MaterialTheme.typography.bodyMedium,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(24.dp)
		)
	}
}

@Composable
internal fun ConfirmationDialog(
	title: String,
	message: String,
	onConfirm: () -> Unit,
	onDismiss: () -> Unit,
	confirmLabel: String = stringResource(R.string.clear_button),
	dismissLabel: String = stringResource(R.string.cancel_button)
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text(title, modifier = Modifier.semantics { heading() }) },
		text = { Text(message) },
		confirmButton = {
			TextButton(onClick = onConfirm) {
				Text(confirmLabel)
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(dismissLabel)
			}
		}
	)
}
