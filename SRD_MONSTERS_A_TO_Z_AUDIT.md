# SRD Monsters A-Z Audit

Snapshot date: 2026-04-28

## Summary

This note records the current shipped state for monster-related local reference content.

Current local behavior is centered on:

- `app/src/main/java/io/github/velyene/loreweaver/ui/screens/ReferenceScreenMonsters.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceCatalog.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataAnimals.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataAtoC.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataDtoG.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataHtoN.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataOtoR.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataStoW.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataXtoZ.kt`

The shipped app once had this corpus removed during provenance review, but it now includes a
restored **local A-Z monster corpus** plus a reviewed **animal/beast corpus** based on
user-provided SRD-backed content.

Goal: document that Loreweaver now ships a **searchable local monster reference** for the current
uploaded slices, including the dedicated animal import, while keeping the process incremental and
auditable.

## Scope reviewed

### Current shipped monster-facing surfaces

- `ReferenceCategory.MONSTERS`
- `MonstersContent(monsters, listState, onOpenDetail)`
- `filterMonsterEntries(query: String)`
- `ReferenceDetailResolver.CATEGORY_MONSTERS`
- `CoreRulesReference.SECTIONS`
- `CoreRulesReference.ALL_TABLES`
- `CoreRulesReference.GLOSSARY_ENTRIES`

### Current audit classification

- `ContentSafetyAuditTest` currently discovers **zero** excluded SRD/reference corpus files.
- If regenerated locally, the ignored `EXCLUDED_REFERENCE_CORPUS_AUDIT.md` snapshot should report
  **0 discovered excluded corpus files** and **0 findings**.

## Current alignment status

### Bundled monster corpus

There is a bundled local monster corpus under `app/src/main` at this snapshot.

Current shipped slice includes monsters from:

- `Aboleth` through `Zombie`, plus grouped late-range entries such as `Ogre Zombie`
- chromatic and metallic dragon age categories included in the uploaded A-G slice
- a dedicated reviewed animal corpus under `MonsterReferenceDataAnimals.kt`, including entries such
  as `Allosaurus`, `Brown Bear`, `Giant Ape`, `Wolf`, and other beast/dinosaur/swarm stat blocks
  grouped as `Animals`
- humanoids, fiends, elementals, oozes, giants, fey, undead, dragons, monstrosities, constructs,
  celestials, plants, lycanthropes, titans, beasts, dinosaurs, and swarms from the current local
  corpus

The legacy plain `MonsterReference.kt` file remains intentionally absent.

### UI exposure

The top-level `Monsters` tab in `ReferenceScreen` now renders the restored local corpus.

Current behavior:

- blank query -> full local monster list for the imported slice
- active query -> filtered local monster list using name, stat summary, and embedded rules text
- tapping a monster card -> generic reference detail view via `ReferenceDetailResolver`
- encounter monster picker -> supports an explicit `Animals` shortcut for quickly narrowing to the
  local animal corpus during encounter setup

### Still-shipped monster guidance

Monster-related guidance still also ships through the **Core Rules** reference content, including
concise reviewed sections such as:

- `Monster Stat Blocks`
- `Running a Monster`
- `Monster Attack and Usage Notation`

Related tables and glossary content remain available through the audited core-rules dataset.

## Verification anchors

- `app/src/test/java/io/github/velyene/loreweaver/ui/screens/ReferenceScreenMonsterSearchTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/domain/util/ReferenceDetailResolverTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/ui/viewmodels/ReferenceViewModelTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/domain/util/CoreRulesReferenceTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/ui/screens/ReferenceScreenNavigationTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/domain/util/ContentSafetyAuditTest.kt`
- optional local corroboration: ignored `EXCLUDED_REFERENCE_CORPUS_AUDIT.md` if regenerated

## Notes for future updates

- Continue importing future monster sections as additive reviewed slices rather than recreating
  the removed legacy file layout.
- Prefer the split `MonsterReferenceData*.kt` structure so later uploads remain reviewable and
  easier to audit.
- If a future monster dataset is added, document whether it is app-authored, excerpt-backed, or
  remote-only before shipping it.
- Keep monster-running/reference material concise, procedural, and clearly distinct from any
  removed corpus-style content.

