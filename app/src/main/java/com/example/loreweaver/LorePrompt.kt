package com.example.loreweaver

import androidx.annotation.DrawableRes

data class LorePrompt(
	val id: String,
	val title: String,
	val content: String,
	val category: String,
	@param:DrawableRes val imageResId: Int? = null
)
