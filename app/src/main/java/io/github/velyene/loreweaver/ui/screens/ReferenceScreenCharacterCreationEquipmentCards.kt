/*
 * FILE: ReferenceScreenCharacterCreationEquipmentCards.kt
 *
 * TABLE OF CONTENTS:
 * 1. Equipment Summary Cards
 * 2. Inventory and Pack Cards
 * 3. Shared Equipment Row Helpers
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.AdventuringGearEntry
import io.github.velyene.loreweaver.domain.util.AmmunitionReferenceEntry
import io.github.velyene.loreweaver.domain.util.ArmorReferenceEntry
import io.github.velyene.loreweaver.domain.util.FocusReferenceEntry
import io.github.velyene.loreweaver.domain.util.LargeVehicleReferenceEntry
import io.github.velyene.loreweaver.domain.util.MagicItemReferenceEntry
import io.github.velyene.loreweaver.domain.util.MountReferenceEntry
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver.STAT_LABEL_COST
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver.STAT_LABEL_WEIGHT
import io.github.velyene.loreweaver.domain.util.TackDrawnReferenceEntry
import io.github.velyene.loreweaver.domain.util.ToolReferenceEntry
import io.github.velyene.loreweaver.domain.util.WeaponReferenceEntry

@Composable
internal fun ToolReferenceCard(tool: ToolReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = tool.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow(
				stringResource(R.string.reference_character_creation_category),
				stringResource(tool.category.labelResId)
			)
			StackedDetailRow(stringResource(R.string.reference_character_creation_equipment_ability), tool.ability)
			StackedDetailRow(STAT_LABEL_WEIGHT, tool.weight)
			StackedDetailRow(STAT_LABEL_COST, tool.cost)
			CharacterCreationCardSectionHeader(stringResource(R.string.reference_character_creation_equipment_utilize))
			BulletListCard(items = tool.utilize)
			if (tool.craft.isNotEmpty()) {
				CharacterCreationCardSectionHeader(stringResource(R.string.reference_character_creation_equipment_craft))
				BulletListCard(items = tool.craft)
			}
			if (tool.variants.isNotEmpty()) {
				CharacterCreationCardSectionHeader(stringResource(R.string.reference_character_creation_equipment_variants))
				BulletListCard(items = tool.variants)
			}
			if (tool.notes.isNotEmpty()) {
				CharacterCreationCardSectionHeader(stringResource(R.string.notes_label))
				BulletListCard(items = tool.notes)
			}
		}
	}
}

@Composable
internal fun WeaponReferenceCard(weapon: WeaponReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = weapon.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow(stringResource(R.string.reference_character_creation_category), weapon.category)
			StackedDetailRow(stringResource(R.string.reference_damage_label), weapon.damage)
			StackedDetailRow(stringResource(R.string.reference_character_creation_equipment_mastery), weapon.mastery)
			StackedDetailRow(STAT_LABEL_WEIGHT, weapon.weight)
			StackedDetailRow(STAT_LABEL_COST, weapon.cost)
			if (weapon.properties.isNotEmpty()) {
				CharacterCreationCardSectionHeader(stringResource(R.string.reference_character_creation_equipment_properties))
				BulletListCard(items = weapon.properties)
			}
		}
	}
}

@Composable
internal fun ArmorReferenceCard(armor: ArmorReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = armor.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow(
				stringResource(R.string.reference_character_creation_equipment_category_don_doff),
				armor.categoryDonDoff
			)
			StackedDetailRow(stringResource(R.string.reference_character_creation_equipment_armor_class), armor.armorClass)
			StackedDetailRow(stringResource(R.string.reference_character_creation_equipment_strength), armor.strength)
			StackedDetailRow(stringResource(R.string.skill_stealth), armor.stealth)
			StackedDetailRow(STAT_LABEL_WEIGHT, armor.weight)
			StackedDetailRow(STAT_LABEL_COST, armor.cost)
		}
	}
}

@Composable
internal fun AdventuringGearReferenceCard(gear: AdventuringGearEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = gear.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow(STAT_LABEL_WEIGHT, gear.weight)
			StackedDetailRow(STAT_LABEL_COST, gear.cost)
			gear.body?.let { body ->
				Text(text = body, style = MaterialTheme.typography.bodyMedium)
			}
		}
	}
}

@Composable
internal fun MagicItemReferenceCard(item: MagicItemReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = item.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			Text(
				text = item.subtitle,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Text(text = item.body, style = MaterialTheme.typography.bodyMedium)
			item.tables.forEach { table ->
				ReferenceTableCard(table)
			}
		}
	}
}

@Composable
internal fun AmmunitionReferenceCard(ammunition: AmmunitionReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = ammunition.type,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow(stringResource(R.string.reference_character_creation_equipment_amount), ammunition.amount)
			StackedDetailRow(stringResource(R.string.reference_character_creation_equipment_storage), ammunition.storage)
			StackedDetailRow(STAT_LABEL_WEIGHT, ammunition.weight)
			StackedDetailRow(STAT_LABEL_COST, ammunition.cost)
		}
	}
}

@Composable
internal fun FocusReferenceCard(focus: FocusReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = focus.name,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow(
				stringResource(R.string.reference_character_creation_group),
				stringResource(focus.group.labelResId)
			)
			StackedDetailRow(STAT_LABEL_WEIGHT, focus.weight)
			StackedDetailRow(STAT_LABEL_COST, focus.cost)
			Text(text = focus.usage, style = MaterialTheme.typography.bodyMedium)
			if (focus.notes.isNotEmpty()) {
				CharacterCreationCardSectionHeader(stringResource(R.string.notes_label))
				BulletListCard(items = focus.notes)
			}
		}
	}
}

@Composable
internal fun MountReferenceCard(mount: MountReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = mount.item,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow(
				stringResource(R.string.reference_character_creation_equipment_carrying_capacity),
				mount.carryingCapacity
			)
			StackedDetailRow(STAT_LABEL_COST, mount.cost)
		}
	}
}

@Composable
internal fun TackDrawnReferenceCard(item: TackDrawnReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = item.item,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow(STAT_LABEL_WEIGHT, item.weight)
			StackedDetailRow(STAT_LABEL_COST, item.cost)
		}
	}
}

@Composable
internal fun LargeVehicleReferenceCard(vehicle: LargeVehicleReferenceEntry, onClick: () -> Unit) {
	Card(
		modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = vehicle.ship,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			StackedDetailRow(stringResource(R.string.speed_label), vehicle.speed)
			StackedDetailRow(stringResource(R.string.reference_character_creation_equipment_crew), vehicle.crew)
			StackedDetailRow(stringResource(R.string.reference_character_creation_equipment_passengers), vehicle.passengers)
			StackedDetailRow(stringResource(R.string.reference_character_creation_equipment_cargo_tons), vehicle.cargoTons)
			StackedDetailRow(stringResource(R.string.ac_label), vehicle.ac)
			StackedDetailRow(stringResource(R.string.hp_label), vehicle.hp)
			StackedDetailRow(
				stringResource(R.string.reference_character_creation_equipment_damage_threshold),
				vehicle.damageThreshold
			)
			StackedDetailRow(STAT_LABEL_COST, vehicle.cost)
		}
	}
}
