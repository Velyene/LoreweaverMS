package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.viewmodels.ReferenceCategory
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReferenceAccessibilityTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context
		get() = ApplicationProvider.getApplicationContext()

	@Test
	fun searchBar_usesResourceLabelAndClearDescription() {
		composeRule.setContent {
			MaterialTheme {
				SearchBar(
					query = "dragon",
					onQueryChange = {},
					onClear = {}
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.reference_search_label)).assertExists()
		composeRule.onNodeWithContentDescription(context.getString(R.string.clear_button)).assertExists()
	}

	@Test
	fun categoryTabs_usesFavoritesCountContentDescription() {
		val expectedDescription = context.getString(
			R.string.reference_tab_with_favorites_count,
			context.getString(R.string.reference_tab_traps),
			2
		)

		composeRule.setContent {
			MaterialTheme {
				CategoryTabs(
					selectedCategory = ReferenceCategory.TRAPS,
					favoriteCounts = mapOf(ReferenceCategory.TRAPS to 2),
					onCategorySelected = {}
				)
			}
		}

		composeRule.onNodeWithContentDescription(expectedDescription).assertExists()
	}
}

