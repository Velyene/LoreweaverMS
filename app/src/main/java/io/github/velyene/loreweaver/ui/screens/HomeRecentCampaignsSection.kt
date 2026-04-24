package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fort
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.Campaign
import io.github.velyene.loreweaver.ui.theme.MutedText
import io.github.velyene.loreweaver.ui.theme.PanelSurface

@Composable
internal fun RecentCampaignsSection(
	campaigns: List<Campaign>,
	onCampaignClick: (String) -> Unit
) {
	HomeSectionHeader(stringResource(R.string.recent_campaigns))
	if (campaigns.isEmpty()) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.clip(MaterialTheme.shapes.medium)
				.background(PanelSurface)
				.padding(20.dp),
			contentAlignment = Alignment.Center
		) {
			Text(
				text = stringResource(R.string.no_campaigns),
				style = MaterialTheme.typography.bodyMedium,
				color = MutedText,
				textAlign = TextAlign.Center
			)
		}
	} else {
		Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
			campaigns.take(3).forEach { campaign ->
				HomeItem(
					title = campaign.title,
					icon = Icons.Default.Fort,
					onClick = { onCampaignClick(campaign.id) }
				)
			}
		}
	}
}
