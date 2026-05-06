package io.github.velyene.loreweaver.ui.viewmodels

import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.util.AppText

internal val fakeAppText: AppText = object : AppText {
	override fun getString(resId: Int, vararg formatArgs: Any): String {
		return when (resId) {
			R.string.error_with_detail -> "${formatArgs[0]}: ${formatArgs[1]}"
			R.string.adventure_log_error_clear -> "Failed to clear adventure log"
			R.string.adventure_log_error_load -> "Failed to load adventure log"
			R.string.campaign_not_found_message -> "Campaign not found."
			R.string.reference_preferences_update_error -> "Failed to update reference favorites."
			R.string.reference_preferences_load_error -> "Failed to load reference favorites."
			else -> buildString {
				append("resId=")
				append(resId)
				if (formatArgs.isNotEmpty()) {
					append(" args=")
					append(formatArgs.toList())
				}
			}
		}
	}

	override fun getQuantityString(resId: Int, quantity: Int, vararg formatArgs: Any): String {
		return buildString {
			append("resId=")
			append(resId)
			append(" quantity=")
			append(quantity)
			if (formatArgs.isNotEmpty()) {
				append(" args=")
				append(formatArgs.toList())
			}
		}
	}
}

