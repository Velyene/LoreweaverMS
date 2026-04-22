package com.example.encountertimer.domain.util
internal const val EQUIPMENT_REFERENCE_ARCANE_FOCUS_ITEM = "Arcane Focus"
internal const val EQUIPMENT_REFERENCE_HOLY_SYMBOL_ITEM = "Holy Symbol"
internal const val EQUIPMENT_REFERENCE_DRUIDIC_FOCUS_ITEM = "Druidic Focus"
internal const val EQUIPMENT_REFERENCE_TEN_POUNDS = "10 lb."
internal const val EQUIPMENT_REFERENCE_SIMPLE_MELEE = "Simple Melee"
internal const val EQUIPMENT_REFERENCE_SIMPLE_RANGED = "Simple Ranged"
internal const val EQUIPMENT_REFERENCE_MARTIAL_MELEE = "Martial Melee"
internal const val EQUIPMENT_REFERENCE_MARTIAL_RANGED = "Martial Ranged"
internal fun gearDetailTitleToName(title: String): String {
return when {
title.startsWith("Spell Scroll") -> "Spell Scroll"
else -> title.substringBefore(" (")
}
}
internal fun magicItem(
name: String,
subtitle: String,
body: String,
vararg tables: ReferenceTable
) = MagicItemReferenceEntry(
name = name,
subtitle = subtitle,
body = body,
tables = tables.toList()
)
