/*
 * FILE: SessionSummaryScreen.kt
 */

package com.example.loreweaver.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.loreweaver.R

@Composable
fun SessionSummaryScreen(onDone: () -> Unit, onViewLog: () -> Unit = {}) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Badge(containerColor = MaterialTheme.colorScheme.primary) {
			Text(
				stringResource(R.string.resume_encounter),
				color = MaterialTheme.colorScheme.onPrimary,
				modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
				fontWeight = FontWeight.Bold
			)
		}

		Spacer(modifier = Modifier.height(32.dp))

		SummaryItem("Snapshot state", "Current round and HP preserved", Icons.Default.CloudDone)
		SummaryItem("Continue later", "Available on Home screen", Icons.Default.Update)
		SummaryItem("Offline safe", "Data persisted to local storage", Icons.Default.SdStorage)

		Spacer(modifier = Modifier.height(48.dp))

		Box(
			modifier = Modifier
				.fillMaxWidth()
				.border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
				.padding(24.dp),
			contentAlignment = Alignment.Center
		) {
			Text(
				"Persist local state\nfor later pickup",
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
				stringResource(R.string.home_title),
				color = MaterialTheme.colorScheme.onPrimary,
				fontWeight = FontWeight.Bold
			)
		}

		Spacer(modifier = Modifier.height(12.dp))

		OutlinedButton(
			onClick = onViewLog,
			modifier = Modifier
				.fillMaxWidth()
				.height(48.dp)
		) {
			Text("View Adventure Log")
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
