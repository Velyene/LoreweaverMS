package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.PoisonTemplate
import io.github.velyene.loreweaver.domain.util.PoisonType
import io.github.velyene.loreweaver.domain.util.ReferenceDetailContent
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.domain.util.ReferenceDetailSection
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReferenceCardFlowsUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()

	@Test
	fun referenceDetailHeader_actionButtonsInvokeCallbacksWithAccessibleLabels() {
		var favoriteClicks = 0
		var copyClicks = 0
		var shareClicks = 0

		composeRule.setContent {
			LoreweaverTheme {
				ReferenceDetailHeader(
					title = "Poison Details",
					isFavorite = false,
					onToggleFavorite = { favoriteClicks++ },
					actions = ReferenceTextActions(
						onCopy = { copyClicks++ },
						onShare = { shareClicks++ }
					)
				)
			}
		}

		composeRule.onNodeWithContentDescription(context.getString(R.string.reference_toggle_favorite)).performClick()
		composeRule.onNodeWithContentDescription(context.getString(R.string.reference_copy_to_clipboard)).performClick()
		composeRule.onNodeWithContentDescription(context.getString(R.string.reference_share)).performClick()

		assertEquals(1, favoriteClicks)
		assertEquals(1, copyClicks)
		assertEquals(1, shareClicks)
	}

	@Test
	fun poisonsContent_favoriteButtonStaysOnCardFlowAndCardClickOpensDetail() {
		val poison = PoisonTemplate(
			name = "Moonshade Toxin",
			type = PoisonType.INJURY,
			saveDC = 13,
			damageOnFail = "2d6",
			pricePerDose = "250 gp",
			description = "A rare toxin favored by assassins."
		)
		var selectedPoison: PoisonTemplate? = null
		var favoriteToggledName: String? = null

		composeRule.setContent {
			LoreweaverTheme {
				PoisonsContent(
					poisons = listOf(poison),
					selectedPoison = selectedPoison,
					listState = rememberLazyListState(),
					showFavoritesOnly = false,
					searchQuery = "",
					favoritePoisonNames = emptySet(),
					onPoisonSelected = { selectedPoison = it },
					onToggleFavorite = { favoriteToggledName = it }
				)
			}
		}

		composeRule.onNodeWithContentDescription(context.getString(R.string.reference_toggle_favorite)).performClick()
		assertEquals(poison.name, favoriteToggledName)
		assertNull(selectedPoison)

		composeRule.onNodeWithText(poison.name).performClick()
		assertEquals(poison.name, selectedPoison?.name)
		composeRule.onNodeWithContentDescription(context.getString(R.string.reference_copy_to_clipboard)).assertIsDisplayed()
		composeRule.onNodeWithContentDescription(context.getString(R.string.reference_share)).assertIsDisplayed()
	}

	@Test
	fun genericReferenceDetailView_localizesCanonicalDetailLabelsAndHandlesBack() {
		var backClicks = 0
		val detail = ReferenceDetailContent(
			title = "Magic Initiate",
			subtitle = "${ReferenceDetailResolver.CATEGORY_FEATS} • Origin",
			statRows = listOf("Cost" to "—", "Repeatable" to "Yes"),
			sections = listOf(
				ReferenceDetailSection(title = "Benefits", bullets = listOf("Two Cantrips")),
				ReferenceDetailSection(title = "See Also", bullets = listOf("Spellcasting"))
			),
			note = "Detailed feat note."
		)

		composeRule.setContent {
			LoreweaverTheme {
				GenericReferenceDetailView(detail = detail, onBack = { backClicks++ })
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.reference_detail_feat_subtitle_format, context.getString(R.string.reference_detail_category_feats), context.getString(R.string.reference_detail_feat_category_origin))).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.reference_detail_section_see_also)).assertIsDisplayed()
		composeRule.onNodeWithText(context.getString(R.string.reference_back_to_list)).performClick()

		assertEquals(1, backClicks)
	}
}

