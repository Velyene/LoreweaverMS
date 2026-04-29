/*
 * FILE: ReferenceDetailResolver.kt
 *
 * TABLE OF CONTENTS:
 * 1. Reference detail models (ReferenceDetailSection, ReferenceDetailContent)
 * 2. ReferenceDetailResolver singleton â€” category constants, slug helpers, and detail resolution
 */

package io.github.velyene.loreweaver.domain.util

data class ReferenceDetailSection(
	val title: String,
	val body: String? = null,
	val bullets: List<String> = emptyList()
)

data class ReferenceDetailContent(
	val title: String,
	val subtitle: String = "",
	val overview: String? = null,
	val statRows: List<Pair<String, String>> = emptyList(),
	val sections: List<ReferenceDetailSection> = emptyList(),
	val tables: List<ReferenceTable> = emptyList(),
	val note: String? = null
)

object ReferenceDetailResolver {
	const val CATEGORY_CONDITIONS = "Conditions"
	const val CATEGORY_GLOSSARY = "Core Rules"
	const val CATEGORY_ACTIONS = "Actions"
	const val CATEGORY_HAZARDS = "Hazards"
	const val CATEGORY_FEATS = "Feats"
	const val CATEGORY_MONSTERS = "Monsters"
	const val CATEGORY_SPELLS = "Spells"
	const val CATEGORY_WEAPONS = "Weapons"
	const val CATEGORY_ARMOR = "Armor"
	const val CATEGORY_TOOLS = "Tools"
	const val CATEGORY_ADVENTURING_GEAR = "Adventuring Gear"
	const val CATEGORY_MAGIC_ITEMS = "Magic Items"
	const val CATEGORY_AMMUNITION = "Ammunition"
	const val CATEGORY_SPELLCASTING_FOCUSES = "Spellcasting Focuses"
	const val CATEGORY_MOUNTS = "Mounts and Other Animals"
	const val CATEGORY_TACK_AND_VEHICLES = "Tack and Vehicles"
	const val CATEGORY_LARGE_VEHICLES = "Airborne and Waterborne Vehicles"
	const val CATEGORY_LIFESTYLES = "Lifestyle Expenses"
	const val CATEGORY_FOOD_AND_LODGING = "Food, Drink, and Lodging"
	const val CATEGORY_HIRELINGS = "Hirelings"
	internal const val STAT_LABEL_WEIGHT = "Weight"
	internal const val STAT_LABEL_COST = "Cost"

	private val whitespaceRegex = Regex("\\s+")
	private val punctuationRegex = Regex("[^a-z0-9]+")
	private val apostropheVariantRegex = Regex("â€™|â€˜|`|Â´")

	fun slugFor(name: String): String = normalizeKey(name).replace(' ', '-')

	fun resolve(category: String, slug: String): ReferenceDetailContent? {
		if (category.isBlank() || slug.isBlank()) return null

		return when {
			matchesCategory(category, CATEGORY_CONDITIONS, "Condition") -> resolveCondition(slug)
			matchesCategory(category, CATEGORY_ACTIONS, "Action") -> resolveGlossary(
				slug = slug,
				tag = "Action",
				subtitle = CATEGORY_ACTIONS
			)

			matchesCategory(category, CATEGORY_HAZARDS, "Hazard") -> resolveGlossary(
				slug = slug,
				tag = "Hazard",
				subtitle = CATEGORY_HAZARDS
			)

			matchesCategory(category, CATEGORY_GLOSSARY, "Glossary", "Core Rule", "Rule") ->
				resolveGlossary(slug = slug, subtitle = CATEGORY_GLOSSARY)
			matchesCategory(category, CATEGORY_FEATS, "Feat") -> resolveFeat(slug)
			matchesCategory(category, CATEGORY_MONSTERS, "Monster", "Monster Reference") -> MonsterReferenceCatalog.resolve(
				slug
			)
			matchesCategory(category, CATEGORY_SPELLS, "Spell") -> resolveSpell(slug)
			matchesCategory(category, CATEGORY_WEAPONS, "Weapon") -> resolveWeapon(slug)
			matchesCategory(category, CATEGORY_ARMOR, "Armour") -> resolveArmor(slug)
			matchesCategory(category, CATEGORY_TOOLS, "Tool", "Tool Reference") -> resolveTool(slug)
			matchesCategory(category, CATEGORY_ADVENTURING_GEAR) -> resolveAdventuringGear(slug)
			matchesCategory(category, CATEGORY_MAGIC_ITEMS, "Magic Item", "Magic Items A-Z", "Magic Items Aâ€“Z") -> resolveMagicItem(
				slug
			)

			matchesCategory(category, CATEGORY_AMMUNITION) -> resolveAmmunition(slug)
			matchesCategory(category, CATEGORY_SPELLCASTING_FOCUSES, "Spellcasting Focus", "Focus", "Focuses") -> resolveFocus(
				slug
			)

			matchesCategory(category, CATEGORY_MOUNTS, "Mount", "Mounts") -> resolveMount(slug)
			matchesCategory(category, CATEGORY_TACK_AND_VEHICLES, "Tack and Drawn Vehicles", "Vehicle", "Vehicles") -> resolveTackAndVehicle(
				slug
			)

			matchesCategory(category, CATEGORY_LARGE_VEHICLES, "Airborne Vehicle", "Waterborne Vehicle", "Large Vehicle", "Large Vehicles") -> resolveLargeVehicle(
				slug
			)

			matchesCategory(category, CATEGORY_LIFESTYLES, "Lifestyle", "Lifestyles") -> resolveTableRow(
				EquipmentReference.LIFESTYLES_TABLE,
				slug
			)

			matchesCategory(category, CATEGORY_FOOD_AND_LODGING, "Food and Lodging", "Food, Drink & Lodging") -> resolveTableRow(
				EquipmentReference.FOOD_DRINK_AND_LODGING_TABLE,
				slug
			)

			matchesCategory(category, CATEGORY_HIRELINGS, "Hireling") -> resolveHireling(slug)
			else -> null
		}
	}

	private fun matchesCategory(category: String, vararg aliases: String): Boolean {
		val normalizedCategory = normalizeKey(category)
		return aliases.any { alias -> normalizeKey(alias) == normalizedCategory }
	}

	private fun resolveCondition(slug: String): ReferenceDetailContent? {
		return resolveGlossary(
			slug = slug,
			tag = "Condition",
			subtitle = CATEGORY_CONDITIONS,
			note = "This condition can be tracked in the character's Status field.",
			primarySectionTitle = "Effects"
		)
	}

	private fun resolveGlossary(
		slug: String,
		tag: String? = null,
		subtitle: String,
		note: String? = null,
		primarySectionTitle: String = "Details"
	): ReferenceDetailContent? {
		val entry = CoreRulesReference.GLOSSARY_ENTRIES.firstOrNull { glossaryEntry ->
			(tag == null || glossaryEntry.tag == tag) && matchesLookup(glossaryEntry.term, slug)
		} ?: return null

		return ReferenceDetailContent(
			title = entry.term,
			subtitle = subtitle,
			overview = entry.summary,
			sections = buildList {
				if (entry.bullets.isNotEmpty()) {
					add(ReferenceDetailSection(title = primarySectionTitle, bullets = entry.bullets))
				}
				if (entry.seeAlso.isNotEmpty()) {
					add(ReferenceDetailSection(title = "See Also", bullets = entry.seeAlso))
				}
			},
			note = note
		)
	}

	private fun resolveFeat(slug: String): ReferenceDetailContent? {
		val feat = CharacterCreationReference.FEATS.firstOrNull { matchesLookup(it.name, slug) }
			?: return null
		return ReferenceDetailContent(
			title = feat.name,
			subtitle = buildString {
				append(CATEGORY_FEATS)
				append(" â€¢ ")
				append(feat.category)
			},
			statRows = buildList {
				add("Category" to feat.category)
				feat.prerequisite?.let { add("Prerequisite" to it) }
				if (feat.repeatable) add("Repeatable" to "Yes")
			},
			sections = listOf(ReferenceDetailSection(title = "Benefits", bullets = feat.benefits))
		)
	}

	private fun resolveSpell(slug: String): ReferenceDetailContent? {
		val spellName = SrdSpellIndexReference.canonicalNameFor(slug)
			?: SrdSpellIndexReference.canonicalNameFor(titleFromSlug(slug))
			?: return null
		return ReferenceDetailContent(
			title = spellName,
			subtitle = CATEGORY_SPELLS,
			overview =
				"Detailed spell text is not bundled in this build. This reference keeps only the verified " +
					"SRD spell-name index.",
			note =
				"Rebuild spell descriptions from verified SRD 5.2.1 source material before shipping detailed " +
					"spell entries again."
		)
	}

	private fun resolveWeapon(slug: String): ReferenceDetailContent? {
		val weapon =
			EquipmentReference.WEAPONS.firstOrNull { matchesLookup(it.name, slug) } ?: return null
		return ReferenceDetailContent(
			title = weapon.name,
			subtitle = weapon.category,
			statRows = listOf(
				"Damage" to weapon.damage,
				"Mastery" to weapon.mastery,
				STAT_LABEL_WEIGHT to weapon.weight,
				STAT_LABEL_COST to weapon.cost
			),
			sections = weapon.properties
				.takeIf { it.isNotEmpty() }
				?.let { listOf(ReferenceDetailSection(title = "Properties", bullets = it)) }
				.orEmpty()
		)
	}

	private fun resolveArmor(slug: String): ReferenceDetailContent? {
		val armor =
			EquipmentReference.ARMOR.firstOrNull { matchesLookup(it.name, slug) } ?: return null
		return ReferenceDetailContent(
			title = armor.name,
			subtitle = CATEGORY_ARMOR,
			statRows = listOf(
				"Category / Don-Doff" to armor.categoryDonDoff,
				"Armor Class" to armor.armorClass,
				"Strength" to armor.strength,
				"Stealth" to armor.stealth,
				STAT_LABEL_WEIGHT to armor.weight,
				STAT_LABEL_COST to armor.cost
			)
		)
	}

	private fun resolveTool(slug: String): ReferenceDetailContent? {
		val tool =
			EquipmentReference.ALL_TOOLS.firstOrNull { matchesLookup(it.name, slug) } ?: return null
		return ReferenceDetailContent(
			title = tool.name,
			subtitle = tool.category.label,
			statRows = listOf(
				"Ability" to tool.ability,
				STAT_LABEL_WEIGHT to tool.weight,
				STAT_LABEL_COST to tool.cost
			),
			sections = buildList {
				add(ReferenceDetailSection(title = "Utilize", bullets = tool.utilize))
				if (tool.craft.isNotEmpty()) add(
					ReferenceDetailSection(
						title = "Craft",
						bullets = tool.craft
					)
				)
				if (tool.variants.isNotEmpty()) add(
					ReferenceDetailSection(
						title = "Variants",
						bullets = tool.variants
					)
				)
				if (tool.notes.isNotEmpty()) add(
					ReferenceDetailSection(
						title = "Notes",
						bullets = tool.notes
					)
				)
			}
		)
	}

	private fun resolveAdventuringGear(slug: String): ReferenceDetailContent? {
		val gear = EquipmentReference.ADVENTURING_GEAR.firstOrNull { matchesLookup(it.name, slug) }
			?: return null
		return ReferenceDetailContent(
			title = gear.name,
			subtitle = CATEGORY_ADVENTURING_GEAR,
			overview = gear.body,
			statRows = listOf(
				STAT_LABEL_WEIGHT to gear.weight,
				STAT_LABEL_COST to gear.cost
			)
		)
	}

	private fun resolveMagicItem(slug: String): ReferenceDetailContent? {
		val item =
			EquipmentReference.MAGIC_ITEMS_A_TO_Z.firstOrNull { matchesLookup(it.name, slug) }
				?: return null
		return ReferenceDetailContent(
			title = item.name,
			subtitle = item.subtitle,
			overview = item.body,
			tables = item.tables
		)
	}

	private fun resolveAmmunition(slug: String): ReferenceDetailContent? {
		val ammunition = EquipmentReference.AMMUNITION.firstOrNull { matchesLookup(it.type, slug) }
			?: return null
		return ReferenceDetailContent(
			title = ammunition.type,
			subtitle = CATEGORY_AMMUNITION,
			statRows = listOf(
				"Amount" to ammunition.amount,
				"Storage" to ammunition.storage,
				STAT_LABEL_WEIGHT to ammunition.weight,
				STAT_LABEL_COST to ammunition.cost
			)
		)
	}

	private fun resolveFocus(slug: String): ReferenceDetailContent? {
		val focus =
			EquipmentReference.FOCUSES.firstOrNull { matchesLookup(it.name, slug) } ?: return null
		return ReferenceDetailContent(
			title = focus.name,
			subtitle = focus.group.label,
			overview = focus.usage,
			statRows = listOf(
				"Group" to focus.group.label,
				STAT_LABEL_WEIGHT to focus.weight,
				STAT_LABEL_COST to focus.cost
			),
			sections = focus.notes
				.takeIf { it.isNotEmpty() }
				?.let { listOf(ReferenceDetailSection(title = "Notes", bullets = it)) }
				.orEmpty()
		)
	}

	private fun resolveMount(slug: String): ReferenceDetailContent? {
		val mount =
			EquipmentReference.MOUNTS.firstOrNull { matchesLookup(it.item, slug) } ?: return null
		return ReferenceDetailContent(
			title = mount.item,
			subtitle = CATEGORY_MOUNTS,
			statRows = listOf(
				"Carrying Capacity" to mount.carryingCapacity,
				STAT_LABEL_COST to mount.cost
			)
		)
	}

	private fun resolveTackAndVehicle(slug: String): ReferenceDetailContent? {
		val item =
			EquipmentReference.TACK_AND_DRAWN_ITEMS.firstOrNull { matchesLookup(it.item, slug) }
				?: return null
		return ReferenceDetailContent(
			title = item.item,
			subtitle = CATEGORY_TACK_AND_VEHICLES,
			statRows = listOf(
				STAT_LABEL_WEIGHT to item.weight,
				STAT_LABEL_COST to item.cost
			)
		)
	}

	private fun resolveLargeVehicle(slug: String): ReferenceDetailContent? {
		val vehicle = EquipmentReference.LARGE_VEHICLES.firstOrNull { matchesLookup(it.ship, slug) }
			?: return null
		return ReferenceDetailContent(
			title = vehicle.ship,
			subtitle = CATEGORY_LARGE_VEHICLES,
			statRows = listOf(
				"Speed" to vehicle.speed,
				"Crew" to vehicle.crew,
				"Passengers" to vehicle.passengers,
				"Cargo Tons" to vehicle.cargoTons,
				"AC" to vehicle.ac,
				"HP" to vehicle.hp,
				"Damage Threshold" to vehicle.damageThreshold,
				STAT_LABEL_COST to vehicle.cost
			)
		)
	}

	private fun resolveHireling(slug: String): ReferenceDetailContent? {
		val key = normalizeKey(slug)
		val rows = EquipmentReference.HIRELINGS_TABLE.rows
		val row = rows.firstOrNull { candidate ->
			val service = candidate.firstOrNull().orEmpty()
			matchesLookup(service, slug) ||
				(service == "Skilled" && key == normalizeKey("Skilled Hireling")) ||
				(service == "Untrained" && key == normalizeKey("Untrained Hireling"))
		} ?: return null
		return tableRowToDetail(EquipmentReference.HIRELINGS_TABLE, row)
	}

	private fun resolveTableRow(table: ReferenceTable, slug: String): ReferenceDetailContent? {
		val row = table.rows.firstOrNull { candidate ->
			candidate.firstOrNull()?.let { matchesLookup(it, slug) } == true
		} ?: return null
		return tableRowToDetail(table, row)
	}

	private fun tableRowToDetail(table: ReferenceTable, row: List<String>): ReferenceDetailContent {
		val columns = table.columns.drop(1)
		val values = row.drop(1)
		return ReferenceDetailContent(
			title = row.firstOrNull().orEmpty(),
			subtitle = table.title,
			statRows = columns.zip(values)
				.filter { (_, value) -> value.isNotBlank() && value != "â€”" }
		)
	}

	private fun matchesLookup(name: String, slug: String): Boolean {
		return normalizeKey(name) == normalizeKey(slug)
	}

	private fun titleFromSlug(slug: String): String {
		return slug
			.replace('-', ' ')
			.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
	}

	private fun normalizeKey(value: String): String {
		return value
			.trim()
			.replace(apostropheVariantRegex, "'")
			.replace("&", " and ")
			.lowercase()
			.replace(punctuationRegex, " ")
			.replace(whitespaceRegex, " ")
			.trim()
	}
}
