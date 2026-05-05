# SRD Magic Items Audit

Snapshot date: 2026-04-20


## Contents

- [Summary](#summary)
- [Hard release overlay](#hard-release-overlay)
- [Scope reviewed](#scope-reviewed)
  - [Directly related local datasets](#directly-related-local-datasets)
  - [Adjacent supporting content](#adjacent-supporting-content)
- [Current alignment status](#current-alignment-status)
  - [Magic Items chapter guidance](#magic-items-chapter-guidance)
  - [Magic item category coverage](#magic-item-category-coverage)
  - [Structured excerpt-backed tables now modeled locally](#structured-excerpt-backed-tables-now-modeled-locally)
  - [Magic-item crafting references embedded in tool data](#magic-item-crafting-references-embedded-in-tool-data)
  - [Adjacent spellcasting references](#adjacent-spellcasting-references)
- [Verification anchors](#verification-anchors)
- [Notes for future updates](#notes-for-future-updates)

## Summary

This note records the magic-item audit completed against the current local reference content in:

- `app/src/main/java/io/github/velyene/loreweaver/domain/util/EquipmentReference.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/SpellcastingReference.kt`

The current local equipment reference also includes a dedicated
`EquipmentReference.MAGIC_ITEMS_A_TO_Z` dataset with condensed searchable summaries for the
user-provided alphabetical excerpt from `Adamantine Armor` through `Wand of Wonder`.

The audit baseline is the user-provided SRD 5.2.1 excerpt covering:

- Magic Item categories
- Category-specific rules for Armor, Potions, Rings, Rods, Scrolls, Staffs, Wands, Weapons, and
  Wondrous Items
- Magic Item rarity and value
- Activating a Magic Item
- Cursed Items
- Magic Item resilience
- Crafting Magic Items
- Sentient Magic Items

Goal: treat the pasted excerpt as the current SRD-backed allowlist for magic-item-related
categories and keep the local reference aligned to that excerpt.

## Contents

- [Summary](#summary)
- [Hard release overlay](#hard-release-overlay)
- [Scope reviewed](#scope-reviewed)
- [Current alignment status](#current-alignment-status)
- [Verification anchors](#verification-anchors)
- [Notes for future updates](#notes-for-future-updates)

## Hard release overlay

For ship decisions, apply this stricter rule on top of the excerpt audit:

- if a named item is not proven against the approved SRD source, remove it from shipped data
- if a named item came from older Basic Rules / core-book material, legacy imports, or unknown
  provenance, do not ship it until re-verified
- if provenance cannot be shown quickly, move the entry to `quarantine_do_not_ship/` rather than
  leaving it searchable in `app/src/main`

This matters for both full `MAGIC_ITEMS_A_TO_Z` entries and embedded example names inside tables,
notes, and searchable summaries.

## Scope reviewed

### Directly related local datasets

- `EquipmentReference.CHAPTER_SECTIONS`
- `EquipmentReference.ADVENTURING_GEAR`
- `EquipmentReference.MAGIC_ITEMS_A_TO_Z`
- `EquipmentReference.ALL_TABLES`
- selected `ToolReferenceEntry.craft` lists in `EquipmentReference.ARTISANS_TOOLS` and
  `EquipmentReference.OTHER_TOOLS`

### Adjacent supporting content

- `SpellcastingReference` references to Spell Scroll usage from broader spellcasting rules

## Current alignment status

### Magic Items chapter guidance

`EquipmentReference.CHAPTER_SECTIONS` now exposes excerpt-backed magic-item guidance through these
section titles:

- `Magic Items`
- `Magic Item Rules`
- `Activating a Magic Item`
- `Crafting Magic Items`
- `Sentient Magic Items`

Out-of-scope adjacent sections that were previously present are no longer surfaced:

- `Brewing Potions of Healing`
- `Scribing Spell Scrolls`

### Magic item category coverage

The provided excerpt names these example items in category descriptions:

- `Potion of Healing`
- `Spell Scroll`
- `+1 Leather Armor`
- `+1 Shield`
- `Ring of Invisibility`
- `Immovable Rod`
- `Staff of Striking`
- `Wand of Fireballs`
- `+1 Ammunition`
- `+1 Longsword`
- `Bag of Holding`
- `Boots of Elvenkind`

`EquipmentReference.ADVENTURING_GEAR` now keeps the generic excerpt-backed entries:

- `Potion of Healing`
- `Spell Scroll`

The prior level-specific local rows are no longer present:

- `Spell Scroll (Cantrip)`
- `Spell Scroll (Level 1)`

`EquipmentReference.MAGIC_ITEMS_A_TO_Z` now captures the provided alphabetical item excerpt as
condensed local reference cards, including item-specific embedded tables for entries such as:

- `Ammunition of Slaying`
- `Amulet of the Planes`
- `Apparatus of the Crab`
- `Bag of Beans`
- `Bag of Tricks`
- `Belt of Giant Strength`
- `Candle of Invocation`
- `Carpet of Flying`
- `Cube of Force`
- `Deck of Illusions`
- `Dragon Orb`
- `Dragon Scale Mail`
- `Efreeti Bottle`
- `Elemental Gem`
- `Feather Token`
- `Figurine of Wondrous Power`
- `Hat of Many Spells`
- `Horn of Valhalla`
- `Ioun Stone`
- `Manual of Golems`
- `Mysterious Deck`
- `Necklace of Prayer Beads`
- `Portable Hole`
- `Potion of Giant Strength`
- `Potions of Healing`
- `Ring of Elemental Command`
- `Ring of Resistance`
- `Ring of Shooting Stars`
- `Robe of Useful Items`
- `Sphere of Annihilation`
- `Spell Scroll`
- `Wand of Wonder`

### Structured excerpt-backed tables now modeled locally

`EquipmentReference.ALL_TABLES` now includes these excerpt-backed magic-item tables:

- `Magic Item Categories`
- `Potion Miscibility`
- `Magic Item Rarities and Values`
- `Magic Item Tools`
- `Magic Item Crafting Time and Cost`
- `Sentient Item’s Alignment`
- `Sentient Item’s Communication`
- `Sentient Item’s Senses`
- `Sentient Item’s Special Purpose`

The prior adjacent tables outside the excerpt are no longer surfaced through `ALL_TABLES`:

- `Spellcasting Services`
- `Spell Scroll Costs`

### Magic-item crafting references embedded in tool data

Magic-item crafting requirements now live in `Magic Item Tools` rather than tool craft outputs.

Current local tool data no longer encodes magic-item-adjacent outputs directly:

- `Calligrapher's Supplies` no longer crafts `Spell Scroll`
- `Herbalism Kit` no longer crafts `Potion of Healing`

### Adjacent spellcasting references

`SpellcastingReference` mentions `Spell Scroll` as one way to cast without expending a spell slot.
This is related to magic items but comes from the broader spellcasting rules reference, not from
the provided Magic Items excerpt itself.

## Verification anchors

- `app/src/test/java/io/github/velyene/loreweaver/domain/util/MagicItemsSrdAuditTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/domain/util/EquipmentReferenceTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/ui/screens/`
  `ReferenceScreenAdventuringGearSearchTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/ui/screens/ReferenceScreenMagicItemSearchTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/ui/screens/ReferenceScreenSpellEconomySearchTest.kt`

- `app/src/test/java/io/github/velyene/loreweaver/ui/screens/ReferenceScreenToolSearchTest.kt`

## Notes for future updates

- Treat the provided Magic Items excerpt as the allowlist for local magic-item-specific reference
  content unless a later re-audited SRD excerpt expands it.
- If new local magic-item content is added, classify it explicitly as either:
    - in the audited excerpt,
    - related but outside the excerpt, or
    - app-authored guidance.
- Keep canonical item/category labels exact when the goal is SRD alignment.
- If the source of a magic-item name is unknown, treat that as a quarantine case, not as a
  temporary ship-safe default.

