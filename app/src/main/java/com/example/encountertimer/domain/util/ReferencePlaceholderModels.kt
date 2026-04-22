package com.example.encountertimer.domain.util

data class ClassFeatureReference(
	val name: String,
	val description: String
)

data class ClassArchetypeReference(
	val name: String,
	val overview: String,
	val tables: List<ReferenceTable> = emptyList(),
	val features: List<ClassFeatureReference> = emptyList(),
	val notes: List<String> = emptyList()
)

data class ClassReferenceStats(
	val primaryAbility: String = "",
	val hitDice: String = "",
	val hitPointsAt1stLevel: String = "",
	val hitPointsAtHigherLevels: String = "",
	val armor: String = "",
	val weapons: String = "",
	val tools: String = "",
	val savingThrows: String = "",
	val skills: String = ""
)

data class ClassReference(
	val name: String,
	val overview: String,
	val quickBuild: String = "",
	val sections: List<CharacterCreationTextSection> = emptyList(),
	val progressionTable: ReferenceTable = ReferenceTable(
		title = "",
		columns = emptyList(),
		rows = emptyList()
	),
	val tables: List<ReferenceTable> = emptyList(),
	val stats: ClassReferenceStats = ClassReferenceStats(),
	val equipment: List<String> = emptyList(),
	val features: List<ClassFeatureReference> = emptyList(),
	val archetypes: List<ClassArchetypeReference> = emptyList(),
	val notes: List<String> = emptyList()
)

data class MonsterReferenceEntry(
	val name: String,
	val subtitle: String,
	val body: String,
	val group: String? = null,
	val tables: List<ReferenceTable> = emptyList()
)

