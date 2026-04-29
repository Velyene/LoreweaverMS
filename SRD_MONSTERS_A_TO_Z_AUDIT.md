# SRD Monsters A-Z Audit

Snapshot date: 2026-04-28

## Summary

This note records the **current shipped state** for monster-related reference content.

Current local behavior is centered on:

- `app/src/main/java/io/github/velyene/loreweaver/ui/screens/ReferenceScreen.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceCatalog.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataAnimals.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataAtoC.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataDtoG.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataHtoM.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataNtoR.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataStoV.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceDataXtoZ.kt`
- `app/src/main/java/io/github/velyene/loreweaver/domain/util/CoreRulesReference.kt`

Goal: document that Loreweaver currently ships **reviewed SRD-derived monster reference content**
alongside monster-running guidance and monster-stat-block explanations.

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

There **is** bundled reviewed monster reference content under `app/src/main` at this snapshot.

Current bundled sources include:

- `MonsterReferenceCatalog.kt`
- `MonsterReferenceDataAnimals.kt`
- `MonsterReferenceDataAtoC.kt`
- `MonsterReferenceDataDtoG.kt`
- `MonsterReferenceDataHtoM.kt`
- `MonsterReferenceDataNtoR.kt`
- `MonsterReferenceDataStoV.kt`
- `MonsterReferenceDataXtoZ.kt`

### UI exposure

The top-level `Monsters` tab in `ReferenceScreen` exposes the bundled reviewed monster reference
content rather than an unavailable-state placeholder.

### Still-shipped monster guidance

Monster-related guidance still also ships through the **Core Rules** reference content, including
concise reviewed sections such as:

- `Monster Stat Blocks`
- `Running a Monster`
- `Monster Attack and Usage Notation`

That guidance now sits alongside the bundled monster reference catalog rather than replacing it.

Related tables and glossary content remain available through the audited core-rules dataset.

## Verification anchors

- `app/src/test/java/io/github/velyene/loreweaver/ui/screens/ReferenceScreenMonsterSearchTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/domain/util/MonsterReferenceCatalogTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/domain/util/CoreRulesReferenceTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/ui/screens/ReferenceScreenNavigationTest.kt`
- `app/src/test/java/io/github/velyene/loreweaver/domain/util/ContentSafetyAuditTest.kt`
- optional local corroboration: ignored `EXCLUDED_REFERENCE_CORPUS_AUDIT.md` if regenerated

## Notes for future updates

- Keep monster-running/reference material concise, procedural, and clearly tied to reviewed
  SRD-derived sources.
- If the bundled monster dataset expands, document the reviewed source basis before shipping it.

