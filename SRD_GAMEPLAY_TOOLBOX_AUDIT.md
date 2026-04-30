# SRD Gameplay Toolbox Audit

Snapshot date: 2026-04-20

## Contents

- [Summary](#summary)
- [Hard release overlay](#hard-release-overlay)
- [Scope reviewed](#scope-reviewed)
    - [Directly matched structured datasets](#directly-matched-structured-datasets)
    - [Related adjacent datasets or systems](#related-adjacent-datasets-or-systems)
- [Findings by category](#findings-by-category)
    - [Travel Pace](#travel-pace)
    - [Creating a Background](#creating-a-background)
    - [Curses and Magical Contagions](#curses-and-magical-contagions)
    - [Environmental Effects](#environmental-effects)
    - [Fear and Mental Stress](#fear-and-mental-stress)
    - [Poison](#poison)
    - [Traps](#traps)
    - [Combat Encounters / Combat Encounter Difficulty](#combat-encounters--combat-encounter-difficulty)
- [Verification anchors](#verification-anchors)
- [Notes for future updates](#notes-for-future-updates)

## Summary

This note records the Gameplay Toolbox audit completed against the current structured reference
content in:

- `app/src/main/java/io/github/velyene/loreweaver/domain/util/CoreRulesReference.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/CharacterCreationReference.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/PoisonReference.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/TrapReference.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/DiseaseReference.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MadnessReference.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/EncounterDifficulty.kt`

The audit baseline is the user-provided SRD 5.2.1 Gameplay Toolbox excerpt covering:

- Travel Pace
- Creating a Background
- Curses and Magical Contagions
- Environmental Effects
- Fear and Mental Stress
- Poison
- Traps
- Combat Encounters / Combat Encounter Difficulty

Goal: treat the pasted excerpt as the current SRD-backed allowlist for those related categories
and explicitly flag anything in the app that falls outside that excerpt.

## Hard release overlay

Use this file with the repo's hard ship rule, not as a soft reference note:

- if a named background, rules term, or structured example is outside the approved excerpt and
  cannot be re-proven from the approved SRD source, it should not ship as source-derived content
- if a label or alias came from prior Basic Rules / core-book material or unknown provenance,
  remove it or quarantine it until verified
- if content is only "related" to the excerpt, that is not enough by itself to keep it in the
  shipped app

## Scope reviewed

### Directly matched structured datasets

- `CoreRulesReference.TRAVEL_PACE_TABLE`
- `CharacterCreationReference.BACKGROUNDS`
- `PoisonReference.TEMPLATES`
- `TrapReference.TEMPLATES`
- `DiseaseReference.TEMPLATES`

### Related adjacent datasets or systems

- `CoreRulesReference.SECTIONS` travel and hazard summaries
- `CoreRulesReference.GLOSSARY_ENTRIES` hazard / condition / travel-adjacent definitions
- `MadnessReference.*`
- `EncounterDifficulty.*`

## Findings by category

### Travel Pace

**In excerpt / partially aligned**

- `CoreRulesReference.TRAVEL_PACE_TABLE` contains the core Fast / Normal / Slow pace rows.

**Related but outside the provided excerpt**

- `CoreRulesReference.SECTIONS` includes original digest summaries such as
  `"Travel and Marching Order"`; those are adjacent app-authored guidance, not the exact
  Gameplay Toolbox travel-terrain table.
- The excerpt's `Travel Terrain` table, good-road modifier, extended-travel save ramp, and
  vehicle-speed handling are **not** represented as an exact structured allowlist dataset today.

### Creating a Background

**In excerpt / partially aligned**

- `CharacterCreationReference` does include background-related structure and the 50 GP reminder.

**Related but outside the provided excerpt**

- `CharacterCreationReference.BACKGROUNDS` currently stores named sample backgrounds:
	- `Acolyte`
	- `Criminal`
	- `Sage`
	- `Soldier`
- These named backgrounds are related to the excerpt's background-creation rules, but they are
  **outside the provided excerpt**, which only describes a creation procedure and does not provide
  a named background allowlist.

**Hard-rule interpretation**

- Treat named background examples and any class-qualified feat aliases attached to them as
  `needs proof before ship`, not as automatically safe because they are adjacent to SRD-backed
  character-creation content.

### Curses and Magical Contagions

**In excerpt / aligned by named contagions**

`DiseaseReference.TEMPLATES` matches the three contagion names in the excerpt:

- `Cackle Fever`
- `Sewer Plague`
- `Sight Rot`

**Related but outside the provided excerpt**

- `DiseaseReference` uses compact app-authored summaries rather than exact excerpt text.
- No dedicated structured curse dataset currently models:
	- Bestow Curse benchmarks
	- Cursed creatures
	- Cursed magic items
	- Narrative curses
	- Environmental curses / Demonic Possession

### Environmental Effects

**Related but outside the provided excerpt**

The excerpt enumerates specific environmental effects such as:

- Deep Water
- Extreme Cold
- Extreme Heat
- Frigid Water
- Heavy Precipitation
- High Altitude
- Slippery Ice
- Strong Wind
- Thin Ice

The app currently has no dedicated structured Gameplay Toolbox environmental-effects dataset
matching that list. Any hazard-adjacent guidance in `CoreRulesReference` is therefore
adjacent/original reference material rather than excerpt-backed structured content.

### Fear and Mental Stress

**Related but outside the provided excerpt**

- `MadnessReference` is adjacent to this SRD topic, but it goes beyond the excerpt's sample fear
  DCs, sample mental stress effects, and prolonged-effects guidance.
- The following `MadnessReference` content should be treated as **outside the provided excerpt**:
	- `SHORT_TERM_EFFECTS`
	- `LONG_TERM_EFFECTS`
	- `INDEFINITE_FLAWS`
	- trigger guidance and DM usage guidance

### Poison

**In excerpt / aligned**

`PoisonReference.TEMPLATES` now matches the canonical poison list from the provided excerpt:

- `Assassin's Blood`
- `Burnt Othur Fumes`
- `Crawler Mucus`
- `Essence of Ether`
- `Malice`
- `Midnight Tears`
- `Oil of Taggit`
- `Pale Tincture`
- `Purple Worm Poison`
- `Serpent Venom`
- `Spider's Sting`
- `Torpor`
- `Truth Serum`
- `Wyvern Poison`

### Traps

**In excerpt / aligned**

`TrapReference.TEMPLATES` now matches the canonical trap list from the provided excerpt:

- `Collapsing Roof`
- `Falling Net`
- `Fire-Casting Statue`
- `Hidden Pit`
- `Poisoned Darts`
- `Poisoned Needle`
- `Rolling Stone`
- `Spiked Pit`

### Combat Encounters / Combat Encounter Difficulty

**Related but outside the provided excerpt**

- `EncounterDifficulty.kt` implements the older Easy / Medium / Hard / Deadly
  threshold-and-multiplier model, plus `TRIVIAL` and `BEYOND_DEADLY` extensions.
- The excerpt instead defines:
	- `Low Difficulty`
	- `Moderate Difficulty`
	- `High Difficulty`
	- `XP Budget per Character`
	- a spend-your-budget encounter-building flow
- Therefore `EncounterDifficulty.kt` should be treated as **related but outside this provided
  excerpt**.

## Verification anchors

- `app/src/test/java/io/github/velyene/loreweaver/domain/util/GameplayToolboxSrdAuditTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/domain/util/CharacterCreationReferenceTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/domain/util/CoreRulesReferenceTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/domain/util/ContentSafetyAuditTest.kt`

## Notes for future updates

- Treat the provided Gameplay Toolbox excerpt as a conservative allowlist for these categories
  unless a later re-audited SRD excerpt expands it.
- If a poison, trap, contagion, environmental effect, or encounter-building rule is added to
  app-owned content, explicitly classify it as either:
	- in the audited excerpt,
	- related but outside the excerpt, or
	- app-authored guidance.
- Prefer exact canonical names when the goal is SRD alignment; if a variant label is kept for UX
  reasons, document it as outside the excerpt rather than silently treating it as canonical.
- If source provenance is unknown, default to `QUARANTINE`, not `KEEP`.

