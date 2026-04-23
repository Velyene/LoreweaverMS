# SRD Monsters A–Z Audit

Snapshot date: 2026-04-21

## Summary

This note records the **post-quarantine shipped state** for monster-related reference content.

Current local behavior is centered on:

- `app/src/main/java/com/example/loreweaver/ui/screens/ReferenceScreen.kt`
- `app/src/main/java/com/example/loreweaver/domain/util/CoreRulesReference.kt`

The previously bundled local `Monsters A–Z` corpus is **no longer present under `app/src/main`**.

Goal: document that Loreweaver currently ships **monster-running guidance and monster-stat-block
explanations only**, not a bundled searchable monster corpus.

## Scope reviewed

### Current shipped monster-facing surfaces

- `ReferenceCategory.MONSTERS`
- `MonstersContent(searchQuery: String)`
- `CoreRulesReference.SECTIONS`
- `CoreRulesReference.ALL_TABLES`
- `CoreRulesReference.GLOSSARY_ENTRIES`

### Current audit classification

- `ContentSafetyAuditTest` currently discovers **zero** excluded SRD/reference corpus files.
- `EXCLUDED_REFERENCE_CORPUS_AUDIT.md` currently reports **0 discovered excluded corpus files**
  and **0 findings**.

## Current alignment status

### Bundled monster corpus

There is **no bundled local monster corpus** under `app/src/main` at this snapshot.

That means the app no longer ships:

- `MonsterReference.kt`
- local `Monsters A–Z` searchable summaries
- local spell-linked monster supplement cards
- local monster alias tables tied to that removed corpus

### UI exposure

The top-level `Monsters` tab still exists in `ReferenceScreen`, but it is now an
**unavailable-state placeholder** rather than a bundled searchable corpus.

Current behavior:

- blank query → `reference_monster_unavailable`
- active query → `reference_monster_search_unavailable`

### Still-shipped monster guidance

Monster-related guidance still ships through the **Core Rules** reference content, including
concise reviewed sections such as:

- `Monster Stat Blocks`
- `Running a Monster`
- `Monster Attack and Usage Notation`

Related tables and glossary content remain available through the audited core-rules dataset.

## Verification anchors

- `app/src/test/java/com/example/loreweaver/ui/screens/ReferenceScreenMonsterSearchTest.kt`
- `app/src/test/java/com/example/loreweaver/domain/util/CoreRulesReferenceTest.kt`
- `app/src/test/java/com/example/loreweaver/ui/screens/ReferenceScreenNavigationTest.kt`
- `app/src/test/java/com/example/loreweaver/domain/util/ContentSafetyAuditTest.kt`
- `EXCLUDED_REFERENCE_CORPUS_AUDIT.md`

## Notes for future updates

- Treat any future reintroduction of bundled monster entries as a new audit event, not a
  continuation of the removed corpus.
- If a future monster dataset is added, document whether it is app-authored, excerpt-backed, or
  remote-only before shipping it.
- Keep monster-running/reference material concise, procedural, and clearly distinct from any
  removed corpus-style content.

