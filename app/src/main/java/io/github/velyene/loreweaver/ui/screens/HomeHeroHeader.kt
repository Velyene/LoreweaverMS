package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.ui.theme.AntiqueGold
import io.github.velyene.loreweaver.ui.theme.ArcaneTeal
import io.github.velyene.loreweaver.ui.theme.DeepSurface
import io.github.velyene.loreweaver.ui.theme.MutedText
import io.github.velyene.loreweaver.ui.theme.ObsidianBlack

@Composable
internal fun LoreweaverHeroHeader() {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				Brush.verticalGradient(colors = listOf(DeepSurface, ObsidianBlack))
			)
			.padding(top = 40.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
		contentAlignment = Alignment.Center
	) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Icon(
				imageVector = Icons.Default.Shield,
				contentDescription = null,
				tint = ArcaneTeal,
				modifier = Modifier.size(40.dp)
			)
			Spacer(modifier = Modifier.height(12.dp))
			Text(
				text = stringResource(R.string.home_brand_title),
				style = MaterialTheme.typography.headlineLarge.copy(
					fontWeight = FontWeight.Bold,
					letterSpacing = 4.sp
				),
				color = AntiqueGold
			)
			Spacer(modifier = Modifier.height(4.dp))
			Box(
				modifier = Modifier
					.width(80.dp)
					.height(2.dp)
					.background(
						Brush.horizontalGradient(
							listOf(Color.Transparent, AntiqueGold, Color.Transparent)
						)
					)
			)
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				text = stringResource(R.string.home_brand_subtitle),
				style = MaterialTheme.typography.bodyMedium,
				color = MutedText,
				textAlign = TextAlign.Center
			)
		}
	}
}
