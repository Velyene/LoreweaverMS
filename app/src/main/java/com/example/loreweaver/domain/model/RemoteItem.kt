package com.example.loreweaver.domain.model

/**
 * A unified model used by the UI to display items from any API category.
 */
data class RemoteItem(
	val id: String, // Typically the slug or name
	val name: String,
	val category: String,
	val detail: String,
	val fullDescription: String = ""
)
