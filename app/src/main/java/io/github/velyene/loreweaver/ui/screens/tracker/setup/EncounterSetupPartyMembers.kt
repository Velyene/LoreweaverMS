/*
 * FILE: EncounterSetupPartyMembers.kt
 *
 * TABLE OF CONTENTS:
 * 1. Party members section
 * 2. Saved party member cards
 */

package io.github.velyene.loreweaver.ui.screens.tracker.setup

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.model.CharacterEntry

@Composable
internal fun PartyMembersSection(
	savedPartyMembers: List<CharacterEntry>,
	selectedPartyIds: Set<String>,
	onTogglePartyMember: (CharacterEntry) -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.border(1.dp, MaterialTheme.colorScheme.outline, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
			.padding(12.dp)
	) {
		Text(
			text = stringResource(R.string.encounter_party_members_title),
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier.semantics { heading() }
		)
		Spacer(modifier = Modifier.height(4.dp))
		Text(
			text = stringResource(R.string.encounter_party_members_supporting_text),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		Spacer(modifier = Modifier.height(8.dp))

		if (savedPartyMembers.isEmpty()) {
			Text(
				text = stringResource(R.string.encounter_party_members_empty_message),
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
				fontSize = 13.sp,
				lineHeight = 20.sp
			)
			return@Column
		}

		savedPartyMembers.forEach { character ->
			val isSelected = selectedPartyIds.contains(character.id)
			SavedPartyMemberCard(
				character = character,
				isSelected = isSelected,
				onTogglePartyMember = { onTogglePartyMember(character) }
			)
			Spacer(modifier = Modifier.height(8.dp))
		}
	}
}

@Composable
private fun SavedPartyMemberCard(
	character: CharacterEntry,
	isSelected: Boolean,
	onTogglePartyMember: () -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onTogglePartyMember),
		colors = CardDefaults.cardColors(
			containerColor = if (isSelected) {
				MaterialTheme.colorScheme.primaryContainer
			} else {
				MaterialTheme.colorScheme.surfaceVariant
			}
		)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 12.dp, vertical = 10.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = character.name,
					style = MaterialTheme.typography.titleSmall,
					fontWeight = FontWeight.SemiBold,
					color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
				)
				Text(
					text = stringResource(
						R.string.encounter_party_member_summary,
						character.level,
						character.hp,
						character.maxHp,
						character.initiative
					),
					style = MaterialTheme.typography.bodySmall,
					color = if (isSelected) {
						MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
					} else {
						MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
					}
				)
			}
			Text(
				text = if (isSelected) {
					stringResource(R.string.encounter_party_member_selected)
				} else {
					stringResource(R.string.encounter_party_member_add)
				},
				style = MaterialTheme.typography.labelMedium,
				fontWeight = FontWeight.Bold,
				color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
			)
		}
	}
}

