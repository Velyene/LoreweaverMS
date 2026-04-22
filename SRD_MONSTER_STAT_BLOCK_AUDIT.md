# SRD Monster Stat Block Audit

Snapshot date: 2026-04-20

## Summary

This note records the monster stat-block audit completed against the current local reference
content in:

- `app/src/main/java/com/example/loreweaver/domain/util/CoreRulesReference.kt`
- `app/src/main/java/com/example/loreweaver/ui/screens/ReferenceScreenNavigation.kt`

The audit baseline is the user-provided SRD 5.2.1 excerpt covering:

- monster stat block overview and labeled parts
- size, creature type, descriptive tags, and alignment
- Armor Class, Initiative, Hit Points, Speed, ability scores, skills, resistances,
  vulnerabilities, immunities, gear, senses, languages, telepathy, Challenge Rating,
  Experience Points, and Proficiency Bonus
- running a monster
- attack notation, save-effect notation, damage notation, Multiattack, monster spellcasting,
  bonus actions, reactions, legendary actions, and limited usage
- the `Hit Dice by Size`, `Experience Points by Challenge Rating`, and `Proficiency Bonus by
  Challenge Rating` tables

Goal: treat the pasted monster excerpt as the current SRD-backed allowlist for concise monster
stat-block reference content in the core-rules UI.

## Scope reviewed

### Directly related local datasets

- `CoreRulesReference.SECTIONS`
- `CoreRulesReference.GLOSSARY_ENTRIES`
- `CoreRulesReference.ALL_TABLES`

### UI routing

- `CoreRulesSubtab.COMBAT` classification in `ReferenceScreenNavigation.kt`

## Current alignment status

### Core rules sections

`CoreRulesReference.SECTIONS` now includes concise monster-running reference cards for:

- `Monster Stat Blocks`
- `Running a Monster`
- `Monster Attack and Usage Notation`

These are routed through the Core Rules **Combat** subtab.

### Monster tables modeled locally

`CoreRulesReference.ALL_TABLES` now includes:

- `Monster Creature Types`
- `Monster Hit Dice by Size`
- `Experience Points by Challenge Rating`

The existing `Proficiency Bonus` table already covers the same CR bands used by the excerpt.

### Glossary coverage

`CoreRulesReference.GLOSSARY_ENTRIES` now includes or expands entries for:

- `Alignment`
- `Creature Type`
- `Experience Points`
- `Gear`
- `Initiative`
- `Legendary Action`
- `Limited Usage`
- `Monster`
- `Multiattack`
- `Stat Block`
- `Study`
- `Telepathy`
- `Teleportation`

## Verification anchors

- `app/src/test/java/com/example/loreweaver/domain/util/CoreRulesReferenceTest.kt`
- `app/src/test/java/com/example/loreweaver/ui/screens/ReferenceScreenGlossarySearchTest.kt`
- `app/src/test/java/com/example/loreweaver/ui/screens/ReferenceScreenNavigationTest.kt`
- `app/src/test/java/com/example/loreweaver/domain/util/ContentSafetyAuditTest.kt`

## Notes for future updates

- Keep future monster-reference additions concise, search-friendly, and procedural rather than
  transcript-style prose.
- If a later SRD excerpt expands monster content, classify additions as excerpt-backed,
  adjacent-but-out-of-scope, or app-authored before extending the local dataset.
- Prefer exact canonical labels for monster-facing headings and glossary terms when the goal is
  SRD alignment.

