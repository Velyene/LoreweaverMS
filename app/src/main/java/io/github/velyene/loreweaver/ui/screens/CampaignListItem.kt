package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
	onCampaignClick: (String) -> Unit
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
			Icon(
				imageVector = Icons.Default.ChevronRight,
				contentDescription = stringResource(R.string.view_campaign_details_desc)
			)
		},
		modifier = Modifier.clickable(
			role = Role.Button,
			onClickLabel = campaign.title,
			onClick = { onCampaignClick(campaign.id) },
		)
	)
	HorizontalDivider()
}
