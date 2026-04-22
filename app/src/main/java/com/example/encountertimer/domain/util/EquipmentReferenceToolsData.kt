@file:Suppress("kotlin:S1192")
package com.example.encountertimer.domain.util

internal object EquipmentReferenceToolsData {
	private fun artisan(
		name: String,
		ability: String,
		weight: String,
		cost: String,
		utilize: List<String>,
		craft: List<String> = emptyList()
	) = ToolReferenceEntry(name, ToolCategory.ARTISANS, ability, weight, cost, utilize, craft)

	private fun other(
		name: String,
		ability: String,
		weight: String,
		cost: String,
		utilize: List<String>,
		craft: List<String> = emptyList(),
		variants: List<String> = emptyList(),
		notes: List<String> = emptyList()
	) = ToolReferenceEntry(name, ToolCategory.OTHER, ability, weight, cost, utilize, craft, variants, notes)

	val ARTISANS_TOOLS = listOf(
		artisan(
			"Alchemist's Supplies",
			"Intelligence",
			"8 lb.",
			"50 GP",
			listOf("Identify a chemical or poison (DC 15)", "Start a fire with reagents (DC 10)"),
			listOf("Acid", "Alchemist’s Fire", "Antitoxin", "Basic Poison")
		),
		artisan("Brewer's Supplies", "Intelligence", "9 lb.", "20 GP", listOf("Detect spoiled drink (DC 10)", "Brew ale (DC 15)"), listOf("Ale")),
		artisan(
			"Calligrapher's Supplies",
			"Dexterity",
			"5 lb.",
			"10 GP",
			listOf("Write text with impressive flourishes that guard against forgery (DC 15)"),
			listOf("Ink")
		),
		artisan("Carpenter's Tools", "Strength", "6 lb.", "8 GP", listOf("Brace a door or window (DC 10)", "Build a simple wooden structure (DC 15)"), listOf("Club", "Greatclub", "Quarterstaff")),
		artisan("Cartographer's Tools", "Wisdom", "6 lb.", "15 GP", listOf("Draw a map from memory (DC 15)", "Survey terrain (DC 10)"), listOf("Map")),
		artisan("Cobbler's Tools", "Dexterity", "5 lb.", "5 GP", listOf("Repair footwear (DC 10)", "Modify shoes for travel (DC 15)"), listOf("Traveler’s Shoes")),
		artisan("Cook's Utensils", "Wisdom", "8 lb.", "1 GP", listOf("Prepare a nourishing meal (DC 10)", "Improve camp rations (DC 15)"), listOf("Meal")),
		artisan("Glassblower's Tools", "Dexterity", "5 lb.", "30 GP", listOf("Shape a simple glass object (DC 15)"), listOf("Bottle, Glass", "Vial")),
		artisan("Jeweler's Tools", "Dexterity", "2 lb.", "25 GP", listOf("Appraise a gem (DC 15)", "Set a jewel in metal (DC 15)"), listOf("Gem Setting")),
		artisan("Leatherworker's Tools", "Dexterity", "5 lb.", "5 GP", listOf("Repair leather goods (DC 10)", "Cut and fit leather (DC 15)"), listOf("Leather Armor", "Pouch")),
		artisan("Mason's Tools", "Strength", "8 lb.", "10 GP", listOf("Chisel a stone surface (DC 10)", "Build a simple stone wall (DC 15)"), listOf("Stone Block")),
		artisan("Painter's Supplies", "Dexterity", "5 lb.", "10 GP", listOf("Create a decorative image (DC 10)", "Forge a simple insignia (DC 15)"), listOf("Painted Sign")),
		artisan("Potter's Tools", "Dexterity", "3 lb.", "10 GP", listOf("Shape simple clay ware (DC 10)", "Glaze pottery (DC 15)"), listOf("Clay Vessel")),
		artisan("Smith's Tools", "Strength", "8 lb.", "20 GP", listOf("Repair a metal object (DC 10)", "Forge a simple metal item (DC 15)"), listOf("Chain", "Spikes, Iron")),
		artisan("Tinker's Tools", "Dexterity", "10 lb.", "50 GP", listOf("Repair a Tiny object (DC 10)", "Assemble a simple mechanism (DC 15)"), listOf("Clockwork Trinket")),
		artisan("Weaver's Tools", "Dexterity", "5 lb.", "1 GP", listOf("Weave cloth (DC 10)", "Mend torn fabric (DC 10)"), listOf("Robe", "String")),
		artisan("Woodcarver's Tools", "Dexterity", "5 lb.", "1 GP", listOf("Carve a small wooden item (DC 10)", "Whittle a figurine (DC 15)"), listOf("Wooden Figurine"))
	)

	val OTHER_TOOLS = listOf(
		other("Disguise Kit", "Charisma", "3 lb.", "25 GP", listOf("Create a disguise (DC 15)", "Hide a distinguishing feature (DC 10)")),
		other("Forgery Kit", "Dexterity", "5 lb.", "15 GP", listOf("Forge a simple document (DC 15)", "Copy handwriting (DC 15)")),
		other(
			"Gaming Set",
			"Wisdom",
			"—",
			"Varies",
			listOf("Win at a game of skill or chance (DC 15)"),
			variants = listOf("Dice", "Dragonchess", "Playing Cards", "Three-Dragon Ante"),
			notes = listOf(
				"Variants require separate proficiency",
				"Variant prices and weights vary by set, including Dragonchess and Three-Dragon Ante decks."
			)
		),
		other("Herbalism Kit", "Intelligence", "3 lb.", "5 GP", listOf("Identify a plant (DC 10)", "Prepare a salve (DC 15)"), listOf("Antitoxin", "Herbal Salve")),
		other(
			"Musical Instrument",
			"Charisma",
			"Varies",
			"Varies",
			listOf("Perform a song (DC 10)", "Play a complex composition (DC 15)"),
			variants = listOf("Bagpipes", "Drum", "Dulcimer", "Flute", "Horn", "Lute", "Lyre", "Pan Flute", "Shawm", "Viol"),
			notes = listOf("Variants require separate proficiency")
		),
		other("Navigator's Tools", "Wisdom", "2 lb.", "25 GP", listOf("Plot a course (DC 10)", "Determine position by stargazing (DC 15)")),
		other("Poisoner's Kit", "Intelligence", "2 lb.", "50 GP", listOf("Detect a poisoned object (DC 10)"), listOf("Basic Poison")),
		other("Thieves' Tools", "Dexterity", "1 lb.", "25 GP", listOf("Pick a lock (DC 15)", "Disarm a trap (DC 15)"))
	)

	val ALL_TOOLS = ARTISANS_TOOLS + OTHER_TOOLS

	val ARTISANS_TOOLS_TABLE = ReferenceTable(
		title = "Artisan’s Tools",
		columns = listOf("Name", "Ability", "Weight", "Cost", "Utilize", "Craft"),
		rows = ARTISANS_TOOLS.map { tool ->
			listOf(
				tool.name.replace("'", "’"),
				tool.ability,
				tool.weight,
				tool.cost,
				tool.utilize.joinToString(", or "),
				tool.craft.joinToString().ifBlank { "—" }
			)
		}
	)

	val OTHER_TOOLS_TABLE = ReferenceTable(
		title = "Other Tools",
		columns = listOf("Name", "Ability", "Weight", "Cost / Variants", "Utilize", "Craft / Variants"),
		rows = OTHER_TOOLS.map { tool ->
			listOf(
				tool.name.replace("'", "’"),
				tool.ability,
				tool.weight,
				if (tool.variants.isNotEmpty()) tool.variants.joinToString() else tool.cost,
				tool.utilize.joinToString(", or "),
				when {
					tool.craft.isNotEmpty() -> tool.craft.joinToString()
					tool.notes.isNotEmpty() -> tool.notes.joinToString()
					else -> "—"
				}
			)
		}
	)
}
