package io.github.velyene.loreweaver.ui.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
	data class DynamicString(val value: String) : UiText

	data class StringResource(
		@param:StringRes val resId: Int,
		val formatArgs: List<Any> = emptyList()
	) : UiText
}

@Composable
fun UiText.asString(): String = when (this) {
	is UiText.DynamicString -> value
	is UiText.StringResource -> stringResource(
		resId,
		*formatArgs.map { arg -> if (arg is UiText) arg.asString() else arg }.toTypedArray()
	)
}


