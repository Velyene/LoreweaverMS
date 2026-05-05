package io.github.velyene.loreweaver.ui.screens

import android.content.ClipData
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.stringResource
import io.github.velyene.loreweaver.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

