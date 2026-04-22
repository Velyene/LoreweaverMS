package com.example.loreweaver.ui.screens

import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.loreweaver.ui.viewmodels.ReferenceCategory

internal const val CORE_RULES_LABEL = "Core Rules"

@Composable
internal fun CategoryTabs(
	selectedCategory: ReferenceCategory,
	favoriteCounts: Map<ReferenceCategory, Int>,
	onCategorySelected: (ReferenceCategory) -> Unit
) {
	PrimaryScrollableTabRow(
		selectedTabIndex = selectedCategory.ordinal,
		containerColor = MaterialTheme.colorScheme.surface,
		contentColor = MaterialTheme.colorScheme.onSurface
	) {
		ReferenceCategory.entries.forEach { category ->
			ReferenceCategoryTab(
				category = category,
				isSelected = selectedCategory == category,
				favoriteCount = favoriteCounts[category] ?: 0,
				onClick = { onCategorySelected(category) }
			)
		}
	}
}

@Composable
private fun ReferenceCategoryTab(
	category: ReferenceCategory,
	isSelected: Boolean,
	favoriteCount: Int,
	onClick: () -> Unit
) {
	Tab(
		selected = isSelected,
		onClick = onClick,
		text = {
			CategoryTabLabel(
				category = category,
				isSelected = isSelected,
				favoriteCount = favoriteCount
			)
		}
	)
}

@Composable
private fun CategoryTabLabel(
	category: ReferenceCategory,
	isSelected: Boolean,
	favoriteCount: Int
) {
	if (favoriteCount > 0 && category.supportsFavoritesFilter()) {
		BadgedBox(
			badge = {
				Badge {
					Text(favoriteCount.toString())
				}
			}
		) {
			CategoryTabText(
				category = category,
				isSelected = isSelected,
				modifier = Modifier.wrapContentWidth()
			)
		}
		return
	}

	CategoryTabText(category = category, isSelected = isSelected)
}

@Composable
private fun CategoryTabText(
	category: ReferenceCategory,
	isSelected: Boolean,
	modifier: Modifier = Modifier
) {
	Text(
		text = category.toDisplayLabel(),
		fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
		modifier = modifier
	)
}

internal fun ReferenceCategory.toDisplayLabel(): String = when (this) {
	ReferenceCategory.TRAPS -> "Traps"
	ReferenceCategory.POISONS -> "Poisons"
	ReferenceCategory.DISEASES -> "Diseases"
	ReferenceCategory.SPELLCASTING -> "Spellcasting"
	ReferenceCategory.OBJECTS -> "Objects"
	ReferenceCategory.MADNESS -> "Madness"
	ReferenceCategory.MONSTERS -> "Monsters"
	ReferenceCategory.CORE_RULES -> CORE_RULES_LABEL
	ReferenceCategory.CHARACTER_CREATION -> "Character Creation"
}

internal fun ReferenceCategory.supportsFavoritesFilter(): Boolean =
	this == ReferenceCategory.TRAPS ||
		this == ReferenceCategory.POISONS ||
		this == ReferenceCategory.DISEASES
