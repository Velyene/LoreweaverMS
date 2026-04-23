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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.example.loreweaver.R
import com.example.loreweaver.ui.viewmodels.ReferenceCategory

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
	val label = stringResource(category.labelResId())
	val favoriteCountDescription = if (favoriteCount > 0 && category.supportsFavoritesFilter()) {
		stringResource(R.string.reference_tab_with_favorites_count, label, favoriteCount)
	} else {
		null
	}
	val tabModifier = if (favoriteCountDescription != null) {
		Modifier.semantics { contentDescription = favoriteCountDescription }
	} else {
		Modifier
	}

	Tab(
		modifier = tabModifier,
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
		text = stringResource(category.labelResId()),
		fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
		modifier = modifier
	)
}

internal fun ReferenceCategory.labelResId(): Int = when (this) {
	ReferenceCategory.TRAPS -> R.string.reference_tab_traps
	ReferenceCategory.POISONS -> R.string.reference_tab_poisons
	ReferenceCategory.DISEASES -> R.string.reference_tab_diseases
	ReferenceCategory.SPELLCASTING -> R.string.reference_tab_spellcasting
	ReferenceCategory.OBJECTS -> R.string.reference_tab_objects
	ReferenceCategory.MADNESS -> R.string.reference_tab_madness
	ReferenceCategory.MONSTERS -> R.string.reference_tab_monsters
	ReferenceCategory.CORE_RULES -> R.string.reference_tab_core_rules
	ReferenceCategory.CHARACTER_CREATION -> R.string.reference_tab_character_creation
}

internal fun ReferenceCategory.supportsFavoritesFilter(): Boolean =
	this == ReferenceCategory.TRAPS ||
		this == ReferenceCategory.POISONS ||
		this == ReferenceCategory.DISEASES
