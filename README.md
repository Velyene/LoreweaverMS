# Loreweaver

Loreweaver is an Android companion app for tabletop RPG groups. It helps Game Masters and
players manage campaigns, characters, encounters, and quick rules lookups from one local-first
app.

## Contents

- [App Description](#app-description)
- [Key Features](#key-features)
- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [Developer Workflow & Repository Hygiene](#developer-workflow--repository-hygiene)
  - [Security / Hardening](#security--hardening)
- [Notes on data and networking](#notes-on-data-and-networking)
- [SRD Notice](#srd-notice)

## App Description

The app is built around encounter flow. It reduces session bookkeeping by keeping combat state,
campaign notes, character resources, and reference material in one place. The UI uses Jetpack
Compose with a fixed dark-fantasy theme for a consistent in-game presentation.

## Key Features

- **Campaign management**: Create campaigns, attach encounters, keep session records, and store
  campaign notes.
- **Character management**: Track stats, saves, proficiencies, inventory, resources, conditions,
  inspiration, spell slots, and challenge rating.
- **Combat tracker**: Manage initiative order, round state, combatant health, and encounter flow.
- **Encounter difficulty**: Automatically calculate 5e encounter difficulty from party composition,
  monster count, CR, and adjusted XP.
- **Adventure logs**: Persist important session events in a Room-backed log capped to the most
  recent 100 entries.
- **Reference**: Browse local rules content for traps, poisons, diseases, spellcasting,
  objects, hysteria, core rules, and character creation.
- **Favorites, copy, and share**: Star reference entries, copy prompt/reference text, and share
  selected reference content with other apps.
- **Prompt library**: Access a small set of narrative prompts and copy them to the clipboard while
  running a session.

## Architecture Overview

The project follows an MVVM + Clean Architecture structure with a local-first data layer.

### Architecture at a glance

- **UI layer**: Jetpack Compose screens and `@HiltViewModel` classes expose `StateFlow`-backed UI
  state.
- **Domain layer**: Repository interfaces, domain models, and use cases keep business logic
  separated from framework details.
- **Data layer**: Room entities, DAOs, Gson-backed mappers, and repository implementations persist
  application data.
- **DI layer**: Hilt provides the database, DAOs, repositories, and shared preferences.

### Core technologies

- **Kotlin + Jetpack Compose** for the app UI.
- **Hilt** for dependency injection.
- **Room** for local persistence. The current `AppDatabase` version is `8`.
- **Gson** for JSON-backed Room columns and type conversion helpers.
- **Navigation Compose + Kotlin Serialization** for type-safe navigation.
- **Material 3** with a custom dark fantasy palette. Dynamic color is disabled by default.

## Project Structure

- `app/src/main/java/io/github/velyene/loreweaver/ui/` — Compose screens, view models, and UI
  helpers.
- `app/src/main/java/io/github/velyene/loreweaver/domain/` — domain models, repository interfaces,
  use cases, and utilities.
- `app/src/main/java/io/github/velyene/loreweaver/data/` — Room database, entities, DAOs, mappers,
  converters, and repository implementations.
- `app/src/main/java/io/github/velyene/loreweaver/di/` — Hilt module bindings.

## Developer Workflow & Repository Hygiene

See `ENGINEERING_STANDARDS.md` for the repo-wide expectations around Kotlin/Compose structure,
ViewModel state conventions, accessibility, localization, testing, audits, and release readiness.

- Gradle tasks are the source of truth for build health. JetBrains inspections can still raise
  false positives around manifest-owned Android components, Hilt providers/constructors, Room
  converters, and JUnit entry points.
- Shared JetBrains project settings are intentionally versioned from `.idea/inspectionProfiles/`,
  `.idea/codeStyles/`, `.idea/compiler.xml`, `.idea/gradle.xml`, `.idea/misc.xml`,
  `.idea/runConfigurations.xml`, `.idea/dictionaries/`, `.idea/.name`,
  `.idea/AndroidProjectSystem.xml`, `.idea/jsonCatalog.xml`, and `.idea/.gitignore`.
- Local-only IDE state stays ignored: `.idea/workspace.xml`, caches, shelves, HTTP requests,
  device/emulator selectors, preview state, `local.properties`, keystores, and all `build/`
  directories.

### Security / Hardening

- `app/build.gradle.kts` suppresses AGP dependency metadata in APKs and bundles, and release
  builds skip `extractReleaseVersionControlInfo` so shipped artifacts do not embed
  `META-INF/version-control-info.textproto` git provenance metadata.
- `gradle/verification-metadata.xml` pins trusted SHA-256 checksums for dependency and plugin
  resolution.
- `app/src/main/AndroidManifest.xml` removes Room's unused
  `androidx.room.MultiInstanceInvalidationService` because Loreweaver does not enable
  multi-instance invalidation.

- Root-level JetBrains inspection-export XML files created from `Problems` / `Inspect Code` runs
  are treated as disposable local artifacts and should not be committed.
- Prefer fixing shared inspection entry points in `.idea/inspectionProfiles/` before adding
  `@Suppress("unused")`; when a suppression is still necessary, keep it declaration-scoped on
  framework-owned entry points only.

## Notes on data and networking

The current app source is local-first. The runtime app code in `app/src/main` persists its data in
Room and `SharedPreferences`; it does not currently include a Retrofit-based remote API layer.

Android backup is intentionally explicit: cloud backup restores only
`reference_preferences.xml`, while device-to-device transfer preserves the full
`loreweaver_database` store, its SQLite sidecars, and `reference_preferences.xml`.

## SRD Notice

See `SRD_NOTICE.md` for the exact SRD 5.2.1 attribution text and current content-audit guardrails.

See `NOTICE` for the scoped repository notice explaining that Creative Commons Attribution 4.0
coverage applies only to reviewed SRD-derived material intentionally included in the repository.

The full Creative Commons Attribution 4.0 license text is mirrored locally in
`licenses/CC-BY-4.0.txt`.

Running the content-audit test suite can also regenerate the ignored local file
`EXCLUDED_REFERENCE_CORPUS_AUDIT.md`, which summarizes separately inventoried reference/SRD corpus
files for developer review. That excluded-corpus inventory is separate from the repo-owned audit
artifacts such as `SRD_MONSTERS_A_TO_Z_AUDIT.md`, which track bundled reviewed datasets that do
ship from `app/src/main`.

The Reference screen includes a local `Monsters` tab backed by bundled monster datasets plus
filtering and deep-link detail support. Monster content and provenance expectations are tracked by
the SRD audit documents in the repo, especially `SRD_MONSTERS_A_TO_Z_AUDIT.md`.

---

*Developed as part of the Mobile Software Development curriculum.*
