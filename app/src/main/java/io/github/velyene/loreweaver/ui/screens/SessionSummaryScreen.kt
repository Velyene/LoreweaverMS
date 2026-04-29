/*
 * FILE: SessionSummaryScreen.kt
 *
 * TABLE OF CONTENTS:
 * 1. Session summary screen entry point
 * 2. Summary cards and stat badges
 * 3. Primary and secondary action rows
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R

@Composable
fun SessionSummaryScreen(onDone: () -> Unit, onOpenAdventureLog: () -> Unit = {}) {
	val scrollState = rememberScrollState()

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
			.verticalScroll(scrollState)
			.visibleVerticalScrollbar(scrollState)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(24.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Badge(containerColor = MaterialTheme.colorScheme.primary) {
				Text(
					stringResource(R.string.session_summary_badge),
					color = MaterialTheme.colorScheme.onPrimary,
					modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
					fontWeight = FontWeight.Bold
				)
			}

			Spacer(modifier = Modifier.height(32.dp))

			SummaryItem(
				stringResource(R.string.session_summary_snapshot_title),
				stringResource(R.string.session_summary_snapshot_subtitle),
				Icons.Default.CloudDone
			)
			SummaryItem(
				stringResource(R.string.session_summary_continue_title),
				stringResource(R.string.session_summary_continue_subtitle),
				Icons.Default.Update
			)
			SummaryItem(
				stringResource(R.string.session_summary_offline_title),
				stringResource(R.string.session_summary_offline_subtitle),
				Icons.Default.SdStorage
			)

			Spacer(modifier = Modifier.height(48.dp))

			Box(
				modifier = Modifier
					.fillMaxWidth()
					.border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
					.padding(24.dp),
				contentAlignment = Alignment.Center
			) {
				Text(
					stringResource(R.string.session_summary_persist_state_message),
					color = MaterialTheme.colorScheme.primary,
					fontSize = 16.sp,
					fontWeight = FontWeight.Light,
					textAlign = androidx.compose.ui.text.style.TextAlign.Center
				)
			}

			Spacer(modifier = Modifier.height(48.dp))

			Button(
				onClick = onDone,
				modifier = Modifier
					.fillMaxWidth()
					.height(56.dp),
				colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
			) {
				Text(
					stringResource(R.string.session_summary_done_button),
					color = MaterialTheme.colorScheme.onPrimary,
					fontWeight = FontWeight.Bold
				)
			}

			Spacer(modifier = Modifier.height(12.dp))

			OutlinedButton(
				onClick = onOpenAdventureLog,
				modifier = Modifier
					.fillMaxWidth()
					.height(48.dp)
			) {
				Text(stringResource(R.string.session_summary_open_adventure_log))
			}
		}
	}
}

@Composable
fun SummaryItem(
	title: String,
	subtitle: String,
	icon: androidx.compose.ui.graphics.vector.ImageVector
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 12.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Box(
			modifier = Modifier
				.size(48.dp)
				.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
			contentAlignment = Alignment.Center
		) {
			Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
		}
		Spacer(modifier = Modifier.width(16.dp))
		Column {
			Text(
				title,
				color = MaterialTheme.colorScheme.onSurface,
				fontWeight = FontWeight.Bold,
				fontSize = 16.sp
			)
			Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
		}
	}
}
