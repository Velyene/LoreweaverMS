package io.github.velyene.loreweaver.ui.screens

import android.content.Context
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.ui.theme.LoreweaverTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CampaignEditDeleteUiTest {

	@get:Rule
	val composeRule = createComposeRule()

	private val context: Context = ApplicationProvider.getApplicationContext()

	@Test
	fun createCampaignDialog_editModePrepopulatesAndSavesTrimmedName() {
		var confirmedName: String? = null
		var confirmedDescription: String? = null

		composeRule.setContent {
			LoreweaverTheme {
				CreateCampaignDialog(
					title = context.getString(R.string.edit_campaign_title),
					confirmLabel = context.getString(R.string.save_button),
					initialName = "Harbor Watch",
					initialDescription = "Before",
					onDismiss = {},
					onConfirm = { name, desc ->
						confirmedName = name
						confirmedDescription = desc
					}
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.edit_campaign_title)).assertIsDisplayed()
		composeRule.onNodeWithTag(CREATE_CAMPAIGN_NAME_FIELD_TAG).assertIsDisplayed()
		composeRule.onNodeWithTag(CREATE_CAMPAIGN_DESCRIPTION_FIELD_TAG).assertIsDisplayed()

		composeRule.onNodeWithTag(CREATE_CAMPAIGN_NAME_FIELD_TAG).performTextClearance()
		composeRule.onNodeWithTag(CREATE_CAMPAIGN_NAME_FIELD_TAG).performTextInput("  Harbor Watch Revised  ")
		composeRule.onNodeWithTag(CREATE_CAMPAIGN_DESCRIPTION_FIELD_TAG).performTextClearance()
		composeRule.onNodeWithTag(CREATE_CAMPAIGN_DESCRIPTION_FIELD_TAG).performTextInput("After")
		composeRule.onNodeWithText(context.getString(R.string.save_button)).performClick()

		assertEquals("Harbor Watch Revised", confirmedName)
		assertEquals("After", confirmedDescription)
	}

	@Test
	fun createCampaignDialog_cancelDoesNotInvokeConfirm() {
		var confirmedName: String? = null

		composeRule.setContent {
			LoreweaverTheme {
				CreateCampaignDialog(
					title = context.getString(R.string.edit_campaign_title),
					confirmLabel = context.getString(R.string.save_button),
					initialName = "Harbor Watch",
					initialDescription = "Before",
					onDismiss = {},
					onConfirm = { name, _ -> confirmedName = name }
				)
			}
		}

		composeRule.onNodeWithText(context.getString(R.string.cancel_button)).performClick()

		assertNull(confirmedName)
	}

	@Test
	fun campaignListItem_rowUsesSpecificOpenCampaignAccessibilityLabel() {
		val campaign = Campaign(id = "campaign-1", title = "Harbor Watch", description = "A fogbound frontier")

		composeRule.setContent {
			LoreweaverTheme {
				CampaignListItem(
					campaign = campaign,
					onCampaignClick = {},
					onEditCampaign = {},
					onDeleteCampaign = {},
				)
			}
		}

		composeRule
			.onNodeWithText(campaign.title)
			.assert(
				SemanticsMatcher("has specific open-campaign click label") { node ->
					node.config.getOrNull(SemanticsActions.OnClick)?.label ==
						context.getString(R.string.open_campaign_action, campaign.title)
				}
			)
	}

	@Test
	fun campaignListItem_editAndDeleteButtonsInvokeSpecificCallbacksWithoutOpeningDetail() {
		val campaign = Campaign(id = "campaign-1", title = "Harbor Watch", description = "A fogbound frontier")
		var openedCampaignId: String? = null
		var editedCampaignId: String? = null
		var deletedCampaignId: String? = null

		composeRule.setContent {
			LoreweaverTheme {
				CampaignListItem(
					campaign = campaign,
					onCampaignClick = { openedCampaignId = it },
					onEditCampaign = { editedCampaignId = it.id },
					onDeleteCampaign = { deletedCampaignId = it.id },
				)
			}
		}

		composeRule.onNodeWithContentDescription(context.getString(R.string.edit_campaign, campaign.title)).performClick()
		assertEquals(campaign.id, editedCampaignId)
		assertNull(openedCampaignId)
		assertNull(deletedCampaignId)

		composeRule.onNodeWithContentDescription(context.getString(R.string.delete_campaign, campaign.title)).performClick()
		assertEquals(campaign.id, deletedCampaignId)
		assertNull(openedCampaignId)
	}
}

