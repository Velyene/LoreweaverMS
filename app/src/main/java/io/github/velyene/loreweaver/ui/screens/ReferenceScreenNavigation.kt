/*
 * FILE: ReferenceScreenNavigation.kt
 *
 * TABLE OF CONTENTS:
 * 1. Character creation and core-rules subsection enums
tr */

package io.github.velyene.loreweaver.ui.screens

import androidx.annotation.StringRes
import io.github.velyene.loreweaver.R

internal enum class CharacterCreationSubsection(@param:StringRes val labelResId: Int) {
	ALL(R.string.reference_filter_all),
	OVERVIEW(R.string.reference_filter_overview),
	SPECIES_ORIGIN(R.string.reference_filter_species_origin),
	ABILITIES(R.string.reference_filter_abilities),
	CLASSES(R.string.reference_filter_classes),
	EQUIPMENT(R.string.reference_filter_equipment),
	ADVANCEMENT(R.string.reference_filter_advancement),
	FLAVOR(R.string.reference_filter_flavor)
}

internal enum class CoreRulesSubtab(@param:StringRes val labelResId: Int) {
	ALL(R.string.reference_filter_all),
	FUNDAMENTALS(R.string.reference_core_rules_fundamentals),
	ADVENTURING(R.string.reference_core_rules_adventuring),
	COMBAT(R.string.reference_core_rules_combat),
	GLOSSARY(R.string.reference_core_rules_glossary),
	QUICK_TABLES(R.string.reference_core_rules_quick_tables)
}

