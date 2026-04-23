/*
 * FILE: ReferenceScreenCoreRules.kt
 *
 * TABLE OF CONTENTS:
 * 1. Core Rules content entry point and derived state
 * 2. Glossary and table filtering helpers
 * 3. Lazy-list renderers for rules, glossary, and quick tables
 * 4. Core Rules glossary and subtab UI components
 * 5. Search helpers
 */

package com.example.loreweaver.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loreweaver.R
import com.example.loreweaver.domain.util.CoreGlossaryEntry
import com.example.loreweaver.domain.util.CoreRuleSection
import com.example.loreweaver.domain.util.CoreRulesReference
import com.example.loreweaver.domain.util.ReferenceTable

private const val CORE_RULES_SEARCH_LABEL = "Core Rules"

@Composable
@Suppress("kotlin:S3776")
internal fun CoreRulesContent(searchQuery: String, listState: LazyListState) {
	var selectedSubtabName by rememberSaveable { mutableStateOf(CoreRulesSubtab.ALL.name) }
	val selectedSubtab =
		remember(selectedSubtabName) { CoreRulesSubtab.valueOf(selectedSubtabName) }
	val state =
		rememberCoreRulesContentState(searchQuery = searchQuery, selectedSubtab = selectedSubtab)

	if (!state.hasResults) {
		ReferenceNoResultsState()
		return
	}

	Column(modifier = Modifier.fillMaxSize()) {
		ReferenceSubtabRow(
			options = CoreRulesSubtab.entries,
			selectedOption = state.effectiveSubtab,
			onOptionSelected = { selectedSubtabName = it.name },
			label = { it.label }
		)

		LazyColumn(
			state = listState,
			modifier = Modifier.fillMaxSize(),
			contentPadding = PaddingValues(16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			renderCoreRulesItems(state)
		}
	}
}

private data class CoreRulesContentState(
	val effectiveSubtab: CoreRulesSubtab,
	val showIntroduction: Boolean,
	val showGlossaryIntroduction: Boolean,
	val visibleSections: List<CoreRuleSection>,
	val visibleTables: List<ReferenceTable>,
	val visibleGlossaryEntries: List<CoreGlossaryEntry>,
	val visibleGlossaryTables: List<ReferenceTable>,
	val hasResults: Boolean
)

@Composable
@Suppress("kotlin:S3776")
private fun rememberCoreRulesContentState(
	searchQuery: String,
	selectedSubtab: CoreRulesSubtab
): CoreRulesContentState {
	val normalizedQuery = searchQuery.trim()
	val searchActive = normalizedQuery.isNotBlank()
	val effectiveSubtab = if (searchActive) CoreRulesSubtab.ALL else selectedSubtab
	val filteredSections = remember(normalizedQuery) {
		CoreRulesReference.SECTIONS.filter { it.matchesQuery(normalizedQuery) }
	}
	val filteredTables = remember(normalizedQuery) {
		CoreRulesReference.ALL_TABLES.filter { it.matchesQuery(normalizedQuery) }
	}
	val filteredGlossaryEntries = remember(normalizedQuery) {
		filterCoreGlossaryEntries(normalizedQuery)
	}
	val filteredGlossaryTables = remember(normalizedQuery) {
		filterCoreGlossaryTables(normalizedQuery)
	}
	val visibleSections = remember(filteredSections, effectiveSubtab) {
		filteredSections.filter { effectiveSubtab.matches(it) }
	}
	val visibleTables = remember(filteredTables, effectiveSubtab) {
		filteredTables.filter { effectiveSubtab.matches() }
	}
	val visibleGlossaryEntries = remember(filteredGlossaryEntries, effectiveSubtab, searchActive) {
		filterVisibleGlossaryEntries(filteredGlossaryEntries, effectiveSubtab, searchActive)
	}
	val visibleGlossaryTables = remember(filteredGlossaryTables, effectiveSubtab, searchActive) {
		filterVisibleGlossaryTables(filteredGlossaryTables, effectiveSubtab, searchActive)
	}
	val showIntroduction = matchesQuery(
		normalizedQuery,
		CORE_RULES_SEARCH_LABEL,
		"System Loop",
		CoreRulesReference.INTRODUCTION
	) && effectiveSubtab.showsIntroduction()
	val showGlossaryIntroduction =
		effectiveSubtab.showsGlossaryIntroduction(searchActive) && matchesQuery(
			normalizedQuery,
			"Rules Glossary",
			"Glossary Conventions",
			CoreRulesReference.GLOSSARY_INTRODUCTION,
			*CoreRulesReference.GLOSSARY_CONVENTIONS.toTypedArray()
		)
	val hasResults = showIntroduction ||
		visibleSections.isNotEmpty() ||
		visibleTables.isNotEmpty() ||
		showGlossaryIntroduction ||
		visibleGlossaryEntries.isNotEmpty() ||
		visibleGlossaryTables.isNotEmpty()
	return CoreRulesContentState(
		effectiveSubtab = effectiveSubtab,
		showIntroduction = showIntroduction,
		showGlossaryIntroduction = showGlossaryIntroduction,
		visibleSections = visibleSections,
		visibleTables = visibleTables,
		visibleGlossaryEntries = visibleGlossaryEntries,
		visibleGlossaryTables = visibleGlossaryTables,
		hasResults = hasResults
	)
}

internal fun filterCoreGlossaryEntries(query: String): List<CoreGlossaryEntry> {
	return CoreRulesReference.GLOSSARY_ENTRIES.filter { it.matchesQuery(query.trim()) }
}

internal fun filterCoreGlossaryTables(query: String): List<ReferenceTable> {
	return CoreRulesReference.GLOSSARY_TABLES.filter { it.matchesQuery(query.trim()) }
}

private fun filterVisibleGlossaryEntries(
	filteredGlossaryEntries: List<CoreGlossaryEntry>,
	effectiveSubtab: CoreRulesSubtab,
	searchActive: Boolean
): List<CoreGlossaryEntry> {
	if (!effectiveSubtab.showsGlossary(searchActive)) return emptyList()
	return filteredGlossaryEntries.filter { searchActive || effectiveSubtab.matchesGlossary() }
}

private fun filterVisibleGlossaryTables(
	filteredGlossaryTables: List<ReferenceTable>,
	effectiveSubtab: CoreRulesSubtab,
	searchActive: Boolean
): List<ReferenceTable> {
	if (!effectiveSubtab.showsGlossary(searchActive)) return emptyList()
	return filteredGlossaryTables.filter { searchActive || effectiveSubtab.matchesGlossaryTable() }
}

private fun LazyListScope.renderCoreRulesItems(state: CoreRulesContentState) {
	renderCoreRulesIntroduction(state)
	renderCoreRulesSections(state)
	renderCoreRulesTables(state)
	renderCoreRulesGlossaryIntro(state)
	renderCoreRulesGlossaryTables(state)
	renderCoreRulesGlossaryEntries(state)
}

private fun LazyListScope.renderCoreRulesIntroduction(state: CoreRulesContentState) {
	if (!state.showIntroduction) return
	item {
		ReferenceSectionHeader(stringResource(R.string.reference_tab_core_rules))
		InfoCard(title = stringResource(R.string.reference_core_rules_system_loop_title), body = CoreRulesReference.INTRODUCTION)
	}
}

private fun LazyListScope.renderCoreRulesSections(state: CoreRulesContentState) {
	if (state.visibleSections.isEmpty()) return
	item {
		ReferenceSectionHeader(
			if (state.effectiveSubtab == CoreRulesSubtab.COMBAT) {
				stringResource(R.string.reference_core_rules_combat_topics)
			} else {
				stringResource(R.string.reference_core_rules_rules_topics)
			}
		)
	}
	items(state.visibleSections, key = { it.title }) { section ->
		CoreRuleSectionCard(section)
	}
}

private fun LazyListScope.renderCoreRulesTables(state: CoreRulesContentState) {
	if (state.visibleTables.isEmpty()) return
	item { ReferenceSectionHeader(stringResource(R.string.reference_core_rules_quick_tables)) }
	items(state.visibleTables, key = { it.title }) { table ->
		ReferenceTableCard(table)
	}
}

private fun LazyListScope.renderCoreRulesGlossaryIntro(state: CoreRulesContentState) {
	if (!state.showGlossaryIntroduction) return
	item {
		ReferenceSectionHeader(stringResource(R.string.reference_core_rules_glossary))
		InfoCard(title = stringResource(R.string.reference_core_rules_glossary_overview), body = CoreRulesReference.GLOSSARY_INTRODUCTION)
	}
	item {
		BulletListCard(CoreRulesReference.GLOSSARY_CONVENTIONS)
	}
}

private fun LazyListScope.renderCoreRulesGlossaryTables(state: CoreRulesContentState) {
	if (state.visibleGlossaryTables.isEmpty()) return
	item { ReferenceSectionHeader(stringResource(R.string.reference_core_rules_glossary_tables)) }
	items(state.visibleGlossaryTables, key = { it.title }) { table ->
		ReferenceTableCard(table)
	}
}

private fun LazyListScope.renderCoreRulesGlossaryEntries(state: CoreRulesContentState) {
	if (state.visibleGlossaryEntries.isEmpty()) return
	item { ReferenceSectionHeader(stringResource(R.string.reference_core_rules_glossary_terms)) }
	items(state.visibleGlossaryEntries, key = { it.title }) { entry ->
		CoreGlossaryCard(entry)
	}
}

@Composable
private fun CoreRuleSectionCard(section: CoreRuleSection) {
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
			Text(text = section.summary, style = MaterialTheme.typography.bodyMedium)

			if (section.bullets.isNotEmpty()) {
				ReferenceSubtleDivider()
				section.bullets.forEach { bullet ->
					ReferenceBulletRow(text = bullet)
				}
			}
		}
	}
}

@Composable
private fun CoreGlossaryCard(entry: CoreGlossaryEntry) {
	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			CoreGlossaryHeader(entry)
			CoreGlossaryBullets(entry.bullets)
			CoreGlossarySeeAlso(entry.seeAlso)
		}
	}
}

@Composable
private fun CoreGlossaryHeader(entry: CoreGlossaryEntry) {
	Text(
		text = entry.title,
		style = MaterialTheme.typography.titleMedium,
		fontWeight = FontWeight.Bold
	)
	Text(text = entry.summary, style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun CoreGlossaryBullets(bullets: List<String>) {
	if (bullets.isEmpty()) return
	ReferenceSubtleDivider()
	bullets.forEach { bullet ->
		CoreGlossaryBulletRow(bullet)
	}
}

@Composable
private fun CoreGlossaryBulletRow(bullet: String) {
	ReferenceBulletRow(text = bullet)
}

@Composable
private fun CoreGlossarySeeAlso(seeAlso: List<String>) {
	if (seeAlso.isEmpty()) return
	ReferenceSubtleDivider()
	Text(
		text = "See also: ${seeAlso.joinToString()}",
		style = MaterialTheme.typography.bodySmall,
		color = MaterialTheme.colorScheme.onSurfaceVariant
	)
}

@Composable
private fun ReferenceSubtleDivider() {
	HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
}

@Composable
private fun <T> ReferenceSubtabRow(
	options: List<T>,
	selectedOption: T,
	onOptionSelected: (T) -> Unit,
	label: (T) -> String
) {
	PrimaryScrollableTabRow(
		selectedTabIndex = options.indexOf(selectedOption).coerceAtLeast(0),
		containerColor = MaterialTheme.colorScheme.surfaceVariant,
		contentColor = MaterialTheme.colorScheme.onSurface
	) {
		options.forEach { option ->
			Tab(
				selected = option == selectedOption,
				onClick = { onOptionSelected(option) },
				text = {
					Text(
						text = label(option),
						fontSize = 12.sp,
						fontWeight = if (option == selectedOption) FontWeight.Bold else FontWeight.Normal
					)
				}
			)
		}
	}
}

private fun CoreRuleSection.matchesQuery(query: String): Boolean {
	return matchesQuery(query, title, summary, *bullets.toTypedArray(), *keywords.toTypedArray())
}
