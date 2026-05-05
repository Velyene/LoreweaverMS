package io.github.velyene.loreweaver.ui.screens

import io.github.velyene.loreweaver.domain.util.CoreRuleSection

internal enum class CoreRulesSubtab(val label: String) {
	ALL("All"),
	FUNDAMENTALS("Fundamentals"),
	ADVENTURING("Adventuring"),
	COMBAT("Combat"),
	GLOSSARY("Glossary"),
	QUICK_TABLES("Quick Tables")
}

internal fun CoreRulesSubtab.matches(section: CoreRuleSection): Boolean = when (this) {
	CoreRulesSubtab.ALL -> true
	CoreRulesSubtab.FUNDAMENTALS -> section.title in setOf(
		"General Principles",
		"Rhythm of Play",
		"D20 Tests",
		"Ability Checks",
		"Saving Throws",
		"Attack Rolls and Armor Class",
		"Advantage, Disadvantage, and Heroic Inspiration",
		"Proficiency",
		"Actions, Bonus Actions, and Reactions"
	)

	CoreRulesSubtab.ADVENTURING -> section.title in setOf(
		"Social Interaction",
		"Exploration Basics",
		"Travel and Marching Order"
	)

	CoreRulesSubtab.COMBAT -> section.title in setOf(
		"Combat Sequence",
		"Movement and Position",
		"Making Attacks",
		"Special Combat Cases",
		"Monster Stat Blocks",
		"Running a Monster",
		"Monster Attack and Usage Notation",
		"Damage, Healing, and Dying",
		"Temporary Hit Points"
	)

	CoreRulesSubtab.GLOSSARY -> false
	CoreRulesSubtab.QUICK_TABLES -> false
}

internal fun CoreRulesSubtab.matches(): Boolean {
	return this == CoreRulesSubtab.ALL || this == CoreRulesSubtab.QUICK_TABLES
}

internal fun CoreRulesSubtab.matchesGlossary(): Boolean {
	return this == CoreRulesSubtab.GLOSSARY
}

internal fun CoreRulesSubtab.matchesGlossaryTable(): Boolean {
	return this == CoreRulesSubtab.GLOSSARY
}

internal fun CoreRulesSubtab.showsIntroduction(): Boolean {
	return this == CoreRulesSubtab.ALL || this == CoreRulesSubtab.FUNDAMENTALS
}

internal fun CoreRulesSubtab.showsGlossary(searchActive: Boolean = false): Boolean {
	return this == CoreRulesSubtab.GLOSSARY || (this == CoreRulesSubtab.ALL && searchActive)
}

internal fun CoreRulesSubtab.showsGlossaryIntroduction(searchActive: Boolean = false): Boolean {
	return showsGlossary(searchActive)
}

