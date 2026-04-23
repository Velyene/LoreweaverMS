package com.example.loreweaver.ui.screens.tracker.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loreweaver.R

@Composable
internal fun SetupQuickAddRow(
	partyCount: Int,
	onAddParty: () -> Unit,
	onAddEnemy: () -> Unit
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Button(
			onClick = onAddParty,
			modifier = Modifier.weight(1f),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
		) {
			Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp))
			Spacer(modifier = Modifier.size(6.dp))
			Text(
				if (partyCount > 0) {
					stringResource(R.string.add_party_button_with_count, partyCount)
				} else {
					stringResource(R.string.add_party_button)
				},
				fontSize = 12.sp
			)
		}
		Button(
			onClick = onAddEnemy,
			modifier = Modifier.weight(1f),
			colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
		) {
			Icon(
				Icons.Default.PersonAdd,
				contentDescription = null,
				modifier = Modifier.size(16.dp)
			)
			Spacer(modifier = Modifier.size(6.dp))
			Text(stringResource(R.string.add_enemy_button), fontSize = 12.sp)
		}
	}
}

