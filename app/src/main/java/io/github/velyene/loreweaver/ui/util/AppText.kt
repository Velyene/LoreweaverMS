package io.github.velyene.loreweaver.ui.util

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

interface AppText {
	fun getString(@StringRes resId: Int, vararg formatArgs: Any): String

	fun getQuantityString(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any): String
}

class AndroidAppText(
	private val context: Context
) : AppText {
	override fun getString(resId: Int, vararg formatArgs: Any): String {
		return context.getString(resId, *formatArgs)
	}

	override fun getQuantityString(resId: Int, quantity: Int, vararg formatArgs: Any): String {
		return context.resources.getQuantityString(resId, quantity, *formatArgs)
	}
}

