/*
 * FILE: ReferenceScreenDetail.kt
 *
 * TABLE OF CONTENTS:
 * 1. Function: GenericReferenceDetailView
 * 2. Value: detailClipboardText
 * 3. Value: referenceTextActions
 * 4. Function: GenericReferenceDetailStatsCard
 * 5. Function: GenericReferenceDetailSectionCard
 * 6. Function: buildReferenceDetailClipboardText
 */

package io.github.velyene.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.velyene.loreweaver.R
import io.github.velyene.loreweaver.domain.util.MONSTER_SECTION_ABILITY_SCORES
import io.github.velyene.loreweaver.domain.util.MONSTER_SECTION_ACTIONS
import io.github.velyene.loreweaver.domain.util.MONSTER_SECTION_BONUS_ACTIONS
import io.github.velyene.loreweaver.domain.util.MONSTER_SECTION_LEGENDARY_ACTIONS
import io.github.velyene.loreweaver.domain.util.MONSTER_SECTION_PROFILE
import io.github.velyene.loreweaver.domain.util.MONSTER_SECTION_REACTIONS
import io.github.velyene.loreweaver.domain.util.MONSTER_SECTION_TRAIT
import io.github.velyene.loreweaver.domain.util.MONSTER_SECTION_TRAITS
import io.github.velyene.loreweaver.domain.util.MONSTER_STAT_AC
import io.github.velyene.loreweaver.domain.util.MONSTER_STAT_CR
import io.github.velyene.loreweaver.domain.util.MONSTER_STAT_HP
import io.github.velyene.loreweaver.domain.util.MONSTER_STAT_INITIATIVE
import io.github.velyene.loreweaver.domain.util.MONSTER_STAT_SPEED
import io.github.velyene.loreweaver.domain.util.ReferenceDetailContent
import io.github.velyene.loreweaver.domain.util.ReferenceDetailResolver
import io.github.velyene.loreweaver.domain.util.ReferenceDetailSection

private val REFERENCE_DETAIL_LABEL_RES_IDS = mapOf(
	ReferenceDetailResolver.CATEGORY_CONDITIONS to R.string.reference_detail_category_conditions,
	ReferenceDetailResolver.CATEGORY_GLOSSARY to R.string.reference_tab_core_rules,
	ReferenceDetailResolver.CATEGORY_ACTIONS to R.string.reference_detail_category_actions,
	ReferenceDetailResolver.CATEGORY_HAZARDS to R.string.reference_detail_category_hazards,
	ReferenceDetailResolver.CATEGORY_FEATS to R.string.reference_detail_category_feats,
	ReferenceDetailResolver.CATEGORY_MONSTERS to R.string.reference_detail_category_monsters,
	ReferenceDetailResolver.CATEGORY_SPELLS to R.string.reference_detail_category_spells,
	ReferenceDetailResolver.CATEGORY_WEAPONS to R.string.reference_detail_category_weapons,
	ReferenceDetailResolver.CATEGORY_ARMOR to R.string.reference_detail_category_armor,
	ReferenceDetailResolver.CATEGORY_TOOLS to R.string.reference_detail_category_tools,
	ReferenceDetailResolver.CATEGORY_ADVENTURING_GEAR to R.string.reference_detail_category_adventuring_gear,
	ReferenceDetailResolver.CATEGORY_MAGIC_ITEMS to R.string.reference_detail_category_magic_items,
	ReferenceDetailResolver.CATEGORY_AMMUNITION to R.string.reference_detail_category_ammunition,
	ReferenceDetailResolver.CATEGORY_SPELLCASTING_FOCUSES to R.string.reference_detail_category_spellcasting_focuses,
	ReferenceDetailResolver.CATEGORY_MOUNTS to R.string.reference_detail_category_mounts,
	ReferenceDetailResolver.CATEGORY_TACK_AND_VEHICLES to R.string.reference_detail_category_tack_and_vehicles,
	ReferenceDetailResolver.CATEGORY_LARGE_VEHICLES to R.string.reference_detail_category_large_vehicles,
	ReferenceDetailResolver.CATEGORY_LIFESTYLES to R.string.reference_detail_category_lifestyles,
	ReferenceDetailResolver.CATEGORY_FOOD_AND_LODGING to R.string.reference_detail_category_food_and_lodging,
	ReferenceDetailResolver.CATEGORY_HIRELINGS to R.string.reference_detail_category_hirelings,
	MONSTER_STAT_AC to R.string.ac_label,
	MONSTER_STAT_INITIATIVE to R.string.initiative_label,
	MONSTER_STAT_HP to R.string.hp_label,
	MONSTER_STAT_SPEED to R.string.speed_label,
	MONSTER_STAT_CR to R.string.challenge_rating_label,
	ReferenceDetailResolver.STAT_LABEL_WEIGHT to R.string.reference_detail_stat_weight,
	ReferenceDetailResolver.STAT_LABEL_COST to R.string.reference_detail_stat_cost,
	"Damage" to R.string.reference_damage_label,
	"Mastery" to R.string.reference_character_creation_equipment_mastery,
	"Category" to R.string.reference_character_creation_category,
	"Prerequisite" to R.string.reference_character_creation_prerequisite,
	"Repeatable" to R.string.reference_detail_stat_repeatable,
	"Ability" to R.string.reference_character_creation_equipment_ability,
	"Category / Don-Doff" to R.string.reference_character_creation_equipment_category_don_doff,
	"Armor Class" to R.string.reference_character_creation_equipment_armor_class,
	"Strength" to R.string.reference_character_creation_equipment_strength,
	"Stealth" to R.string.skill_stealth,
	"Amount" to R.string.reference_character_creation_equipment_amount,
	"Storage" to R.string.reference_character_creation_equipment_storage,
	"Group" to R.string.reference_character_creation_group,
	"Carrying Capacity" to R.string.reference_character_creation_equipment_carrying_capacity,
	"Crew" to R.string.reference_character_creation_equipment_crew,
	"Passengers" to R.string.reference_character_creation_equipment_passengers,
	"Cargo Tons" to R.string.reference_detail_stat_cargo_tons,
	"Damage Threshold" to R.string.reference_character_creation_equipment_damage_threshold,
	"Daily Cost" to R.string.reference_detail_stat_daily_cost,
	"Pay" to R.string.reference_detail_stat_pay,
	"Effects" to R.string.reference_effects_title,
	"Details" to R.string.reference_detail_section_details,
	"See Also" to R.string.reference_detail_section_see_also,
	"Benefits" to R.string.reference_character_creation_benefits,
	"Properties" to R.string.reference_character_creation_equipment_properties,
	"Utilize" to R.string.reference_detail_section_utilize,
	"Craft" to R.string.reference_detail_section_craft,
	"Variants" to R.string.reference_detail_section_variants,
	MONSTER_SECTION_TRAIT to R.string.reference_detail_section_traits,
	MONSTER_SECTION_TRAITS to R.string.reference_detail_section_traits,
	MONSTER_SECTION_ACTIONS to R.string.reference_detail_section_actions,
	MONSTER_SECTION_BONUS_ACTIONS to R.string.reference_detail_section_bonus_actions,
	MONSTER_SECTION_REACTIONS to R.string.reference_detail_section_reactions,
	MONSTER_SECTION_LEGENDARY_ACTIONS to R.string.reference_detail_section_legendary_actions,
	MONSTER_SECTION_ABILITY_SCORES to R.string.reference_detail_section_ability_scores,
	MONSTER_SECTION_PROFILE to R.string.reference_detail_section_profile,
	"Origin" to R.string.reference_detail_feat_category_origin,
	"General" to R.string.reference_detail_feat_category_general,
	"Fighting Style" to R.string.reference_detail_feat_category_fighting_style,
	"Epic Boon" to R.string.reference_detail_feat_category_epic_boon,
	"Artisan's Tools" to R.string.reference_character_creation_tool_category_artisans,
	"Other Tools" to R.string.reference_character_creation_tool_category_other,
	"Arcane Focus" to R.string.reference_character_creation_focus_group_arcane,
	"Druidic Focus" to R.string.reference_character_creation_focus_group_druidic,
	"Holy Symbol" to R.string.reference_character_creation_focus_group_holy_symbol
)

private data class LocalizedReferenceDetailContent(
	val title: String,
	val subtitle: String,
	val overview: String?,
	val statRows: List<Pair<String, String>>,
	val sections: List<ReferenceDetailSection>,
	val tables: List<io.github.velyene.loreweaver.domain.util.ReferenceTable>,
	val note: String?
)

@Composable
internal fun GenericReferenceDetailView(
	detail: ReferenceDetailContent,
	onBack: () -> Unit
) {
	val localizedDetail = localizedReferenceDetailContent(detail)
	val detailClipboardText = remember(localizedDetail) { buildReferenceDetailClipboardText(localizedDetail) }
	val referenceTextActions = rememberReferenceTextActions(detailClipboardText)

	ReferenceDetailLayout(onBack = onBack) {
		item {
			ReferenceDetailHeader(
				title = detail.title,
				isFavorite = false,
				onToggleFavorite = {},
				onCopy = referenceTextActions.onCopy,
				onShare = referenceTextActions.onShare,
				showFavoriteAction = false
			)
		}

		localizedDetail.subtitle.takeIf { it.isNotBlank() }?.let { subtitle ->
			item {
				Text(
					text = subtitle,
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.secondary
				)
			}
		}

		localizedDetail.overview?.takeIf { it.isNotBlank() }?.let { overview ->
			item { DetailTextSection(title = stringResource(R.string.reference_overview_title), body = overview) }
		}

		if (localizedDetail.statRows.isNotEmpty()) {
			item { GenericReferenceDetailStatsCard(localizedDetail.statRows) }
		}

		localizedDetail.sections.forEach { section ->
			item(key = "${localizedDetail.title}-${section.title}") {
				GenericReferenceDetailSectionCard(section)
			}
		}

		localizedDetail.tables.forEach { table ->
			item(key = "${localizedDetail.title}-${table.title}") {
				ReferenceTableCard(table)
			}
		}

		localizedDetail.note?.takeIf { it.isNotBlank() }?.let { note ->
			item {
				Text(
					text = note,
					style = MaterialTheme.typography.labelMedium,
					color = MaterialTheme.colorScheme.secondary
				)
			}
		}
	}
}

@Composable
private fun localizedReferenceDetailContent(detail: ReferenceDetailContent): LocalizedReferenceDetailContent {
	return LocalizedReferenceDetailContent(
		title = detail.title,
		subtitle = localizedReferenceDetailSubtitle(detail.subtitle),
		overview = detail.overview,
		statRows = detail.statRows.map { (label, value) -> localizedReferenceDetailLabel(label) to value },
		sections = detail.sections.map { section ->
			section.copy(title = localizedReferenceDetailLabel(section.title))
		},
		tables = detail.tables,
		note = detail.note?.let(::normalizeReferenceDetailDisplayText)
	)
}

@Composable
private fun GenericReferenceDetailStatsCard(statRows: List<Pair<String, String>>) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			statRows.forEach { (label, value) ->
				DetailRow(label, value)
			}
		}
	}
}

@Composable
private fun GenericReferenceDetailSectionCard(section: ReferenceDetailSection) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = section.title,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			section.body?.takeIf { it.isNotBlank() }?.let { body ->
				Text(text = body, style = MaterialTheme.typography.bodyMedium)
			}
			if (section.bullets.isNotEmpty()) {
				section.bullets.forEach { bullet ->
					ReferenceBulletRow(text = bullet)
				}
			}
		}
	}
}

@Composable
private fun localizedReferenceDetailSubtitle(subtitle: String): String {
	val normalizedSubtitle = normalizeReferenceDetailDisplayText(subtitle)
	return when {
		normalizedSubtitle.isBlank() -> normalizedSubtitle
		normalizedSubtitle.startsWith("${ReferenceDetailResolver.CATEGORY_FEATS} • ") -> {
			val type = normalizedSubtitle.substringAfter(" • ")
			stringResource(
				R.string.reference_detail_feat_subtitle_format,
				localizedReferenceDetailLabel(ReferenceDetailResolver.CATEGORY_FEATS),
				localizedReferenceDetailLabel(type)
			)
		}

		else -> localizedReferenceDetailLabel(normalizedSubtitle)
	}
}

@Composable
private fun localizedReferenceDetailLabel(label: String): String {
	val normalizedLabel = normalizeReferenceDetailDisplayText(label)
	val labelResId = REFERENCE_DETAIL_LABEL_RES_IDS[normalizedLabel]
	return labelResId?.let { stringResource(it) } ?: normalizedLabel
}

private fun normalizeReferenceDetailDisplayText(text: String): String {
	return text
		.replace("â€¢", "•")
		.replace("Aâ€“Z", "A–Z")
		.replace("â€”", "—")
}

private fun buildReferenceDetailClipboardText(detail: LocalizedReferenceDetailContent): String =
	buildString {
		appendLine(detail.title)
		if (detail.subtitle.isNotBlank()) {
			appendLine(detail.subtitle)
		}
		appendLine()
		detail.overview?.takeIf { it.isNotBlank() }?.let { overview ->
			appendLine(overview)
			appendLine()
		}
		if (detail.statRows.isNotEmpty()) {
			detail.statRows.forEach { (label, value) ->
				appendLine("$label: $value")
			}
			appendLine()
		}
		detail.sections.forEach { section ->
			appendLine(section.title)
			section.body?.takeIf { it.isNotBlank() }?.let { body -> appendLine(body) }
			section.bullets.forEach { bullet -> appendLine("• $bullet") }
			appendLine()
		}
		detail.tables.forEach { table ->
			appendLine(table.title)
			appendLine(table.columns.joinToString(" • "))
			table.rows.forEach { row -> appendLine(row.joinToString(" • ")) }
			appendLine()
		}
		detail.note?.takeIf { it.isNotBlank() }?.let { note ->
			appendLine(note)
		}
	}.trim()
