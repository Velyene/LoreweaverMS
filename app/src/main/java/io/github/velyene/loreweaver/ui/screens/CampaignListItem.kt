package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal

@Composable
internal fun CampaignListItem(
	campaign: Campaign,
	onCampaignClick: (String) -> Unit,
	onEditCampaign: (Campaign) -> Unit,
	onDeleteCampaign: (Campaign) -> Unit,
) {
	ListItem(
		headlineContent = { Text(text = campaign.title, fontWeight = FontWeight.Bold) },
		supportingContent = { Text(text = campaign.description) },
		leadingContent = {
			Icon(
				imageVector = Icons.Default.Map,
				contentDescription = stringResource(R.string.campaign_map_desc),
				tint = ArcaneTeal
			)
		},
		trailingContent = {
			Row {
				IconButton(onClick = { onEditCampaign(campaign) }) {
					Icon(
						imageVector = Icons.Default.Edit,
						contentDescription = stringResource(R.string.edit_campaign, campaign.title)
					)
				}
				IconButton(onClick = { onDeleteCampaign(campaign) }) {
					Icon(
						imageVector = Icons.Default.Delete,
						contentDescription = stringResource(R.string.delete_campaign, campaign.title)
					)
				}
			}
		},
		modifier = Modifier.clickable(
			role = Role.Button,
			onClickLabel = stringResource(R.string.open_campaign_action, campaign.title),
			onClick = { onCampaignClick(campaign.id) },
		)
	)
	HorizontalDivider()
}
