# SRD Equipment Allowlist Audit

Snapshot date: 2026-04-20

## Summary

This note records the equipment and feat allowlist audit completed against the current
SRD-backed reference content in
`app/src/main/java/io/github/velyene/loreweaver/domain/util/EquipmentReference.kt` and
`app/src/main/java/io/github/velyene/loreweaver/domain/util/CharacterCreationReference.kt`.

The goal of the pass was exact allowlist alignment for canonical labels used by the app's
structured reference data, while preserving searchability through supporting notes where needed.

## Hard release overlay

For release decisions, use this stricter interpretation:

- if a feat is not in the approved SRD feat set, remove it
- if a weapon is not in the approved SRD weapons table, remove it
- if an armor / mount / vehicle / tool / item label falls outside the approved SRD table for
  this audit, remove it or quarantine it until verified
- if a supporting alias or search string introduces a non-SRD canonical name, remove the alias
  rather than silently widening the shipped corpus

This file should be treated as an allowlist document, not merely a normalization note.

## Scope reviewed

- `CharacterCreationReference.FEATS`
- `EquipmentReference.WEAPONS`
- `EquipmentReference.ARMOR`
- `EquipmentReference.ARTISANS_TOOLS`
- `EquipmentReference.OTHER_TOOLS`
- `EquipmentReference.ADVENTURING_GEAR`
- `EquipmentReference.AMMUNITION`
- `EquipmentReference.FOCUSES`
- `EquipmentReference.MOUNTS`
- `EquipmentReference.TACK_AND_DRAWN_ITEMS`
- `EquipmentReference.LARGE_VEHICLES`
- `EquipmentReference.FOOD_DRINK_AND_LODGING_TABLE`
- `EquipmentReference.SPELLCASTING_SERVICES`

## Canonical label normalization completed

### Focus types

- `Staff (also a Quarterstaff)` â†’ `Staff`
- `Wooden staff (also a Quarterstaff)` â†’ `Wooden Staff`
- `Amulet (worn or held)` â†’ `Amulet`
- `Emblem (borne on fabric or a Shield)` â†’ `Emblem`
- `Reliquary (held)` â†’ `Reliquary`

The removed explanatory wording was retained in `notes` so focus search remains discoverable.

### Tack and vehicle labels

- `Feed per day` â†’ `Feed`
- `Stabling per day` â†’ `Stabling`

### Food, drink, and lodging labels

- `Ale (mug)` â†’ `Ale`
- `Bread (loaf)` â†’ `Bread`
- `Cheese (wedge)` â†’ `Cheese`
- summarized inn rows â†’ `Inn Stay`
- summarized meal rows â†’ `Meal`
- retained exact labels `Wine, Common` and `Wine, Fine`

### Spellcasting service categories

- `1` â†’ `Level 1`
- `2` â†’ `Level 2`
- `3` â†’ `Level 3`
- `4â€“5` â†’ `Level 4â€“5`
- `6â€“8` â†’ `Level 6â€“8`
- `9` â†’ `Level 9`

### Tool variant normalization

`Gaming Set` variants were normalized to exact labels:

- `Dice`
- `Dragonchess`
- `Playing Cards`
- `Three-Dragon Ante`

`Musical Instrument` variants were normalized to exact labels:

- `Bagpipes`
- `Drum`
- `Dulcimer`
- `Flute`
- `Horn`
- `Lute`
- `Lyre`
- `Pan Flute`
- `Shawm`
- `Viol`

Variant price and weight details were moved into notes instead of remaining inside canonical names.

### Supporting alias cleanup

Supporting craft/reference strings were normalized where they diverged from the allowlist:

- `Portable Ram` â†’ `Ram, Portable`
- `Glass Bottle` â†’ `Bottle, Glass`
- `Crossbow Bolt Case` â†’ `Case, Crossbow Bolt`
- `Map or Scroll Case` â†’ `Case, Map or Scroll`
- `Firearm Bullets` â†’ `Bullets, Firearm`
- `Sling Bullets` â†’ `Bullets, Sling`
- `Iron Pot` â†’ `Pot, Iron`
- `Iron Spikes` â†’ `Spikes, Iron`
- `Bullseye Lantern` â†’ `Lantern, Bullseye`
- `Hooded Lantern` â†’ `Lantern, Hooded`

## Already aligned

The following canonical categories were already aligned with the audited SRD allowlist and did
not require label changes:

- feats
- weapons
- armor
- top-level artisan's tools
- top-level other tools
- top-level adventuring gear
- ammunition types
- mounts and other animals
- airborne and waterborne vehicles
- lifestyle categories

## Release-blocking interpretation by dataset

- `CharacterCreationReference.FEATS` = exact feat allowlist
- `EquipmentReference.WEAPONS` = exact weapon allowlist
- `EquipmentReference.ARMOR` = exact armor allowlist
- `EquipmentReference.MOUNTS` = exact mounts-and-other-animals allowlist for shipped structured rows
- `EquipmentReference.TACK_AND_DRAWN_ITEMS` = exact tack / drawn vehicle allowlist for shipped
  structured rows
- `EquipmentReference.LARGE_VEHICLES` = exact airborne / waterborne vehicle allowlist

If a future entry is not present in the corresponding approved SRD table, it should be treated as
`DO NOT SHIP` until separately verified and re-audited.

## Verification anchors

- `app/src/test/java/io/github/velyene/loreweaver/domain/util/EquipmentReferenceTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/ui/screens/ReferenceScreenFocusSearchTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/ui/screens/ReferenceScreenToolSearchTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/ui/screens/ReferenceScreenTransportSearchTest.kt`
-

`app/src/test/java/io/github/velyene/loreweaver/ui/screens/ReferenceScreenSpellEconomySearchTest.kt`

- `app/src/test/java/io/github/velyene/loreweaver/domain/util/ContentSafetyAuditTest.kt`

`ContentSafetyAuditTest` already treats
`java/io/github/velyene/loreweaver/domain/util/EquipmentReference.kt` and
`java/io/github/velyene/loreweaver/domain/util/CharacterCreationReference.kt` as reviewed baseline
files for prose-safety auditing.

## Notes for future updates

- Treat canonical item names in `EquipmentReference` as exact-label allowlist entries unless
  there is a deliberate, re-audited reason to change them.
- If explanatory wording is useful for search, prefer `notes` or short supporting text instead of
  altering the canonical label.
- Keep future additions concise and mechanically focused, consistent with `SRD_NOTICE.md` and the
  prose-review guardrails enforced by `ContentSafetyAuditTest`.

