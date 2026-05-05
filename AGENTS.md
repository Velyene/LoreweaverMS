# AGENTS.md – Loreweaver

Android TTRPG combat-tracker app built with Kotlin, Jetpack Compose, Hilt, Room, and a
local-first MVVM + Clean Architecture structure.

Use `ENGINEERING_STANDARDS.md` as the source of truth for repo-wide development expectations such
as Kotlin/Compose conventions, ViewModel state patterns, accessibility, localization, testing,
review, and release-readiness policy. This file stays focused on project-specific architecture,
runtime behavior, content scope, and repo facts.

## Contents

- [Architecture Layers](#architecture-layers)
- [Navigation](#navigation)
- [Data Layer Conventions](#data-layer-conventions)
- [Error Handling Pattern](#error-handling-pattern)
- [External Integration](#external-integration)
- [Theme](#theme)
- [D&D 5e Encounter Difficulty](#dd-5e-encounter-difficulty)
- [D&D 5e Reference UI](#dd-5e-reference-ui)
- [Build & Run Commands](#build--run-commands)
- [Known Build Quirks](#known-build-quirks)
- [Repository Hygiene](#repository-hygiene)
- [Key Files Reference](#key-files-reference)

## Architecture Layers

```text
navigation/ -> @Serializable route types, bottom-bar metadata, and NavHost wiring
ui/      -> Compose screens plus @HiltViewModel classes with StateFlow-backed UiState
domain/  -> Repository interfaces, domain models, use cases, and shared utilities
data/    -> Room entities, DAOs, mappers, converters, and repository implementations
di/      -> Single Hilt module: AppModule.kt (SingletonComponent)
```

### Repository duality

Two `CampaignRepository` files exist at different paths:

- `domain/repository/CampaignRepository.kt` is the interface injected into view models and use
  cases.
- `data/repository/CampaignRepositoryImpl.kt` is the Room-backed implementation wired in
  `di/AppModule.kt`.

> The legacy plain-class `data/repository/CampaignRepository.kt` should not be recreated.

`CampaignRepository` is now a composite interface that extends `CampaignsRepository`,
`EncountersRepository`, `SessionsRepository`, `NotesRepository`, `CharactersRepository`, and
`LogsRepository`; `AppModule.kt` fans the same `CampaignRepositoryImpl` out to those narrower
interfaces.

## Navigation

Route types live in `navigation/Routes.kt`, while `navigation/LoreweaverNavGraph.kt` owns
`LoreweaverApp`, the `NavHost`, and bottom-bar wiring. `MainActivity.kt` now just applies
`LoreweaverTheme(darkTheme = true)` and hosts `LoreweaverApp()`.

Examples:

```kotlin
@Serializable object HomeRoute
@Serializable object AdventureLogRoute
@Serializable data class CampaignDetailRoute(val id: String)
@Serializable data class ReferenceRoute(
    val category: ReferenceCategory? = null,
    val query: String = "",
    val detailCategory: String? = null,
    val detailSlug: String? = null,
)
```

Navigation is type-safe via calls such as
`navController.navigate(CampaignDetailRoute(id))` and
`backStackEntry.toRoute<CampaignDetailRoute>()`.

Key route notes:

- Bottom-bar destinations are Home, Campaigns, Characters, and Reference.
- `AdventureLogRoute` is a secondary destination launched from `SessionSummaryScreen`.
- `PromptLibraryRoute` exists, but it is not a bottom-bar destination.
- `ReferenceRoute` supports initial category selection, free-text search, and deep linking into a
  specific detail section/slug within `ReferenceScreen`.
- `BottomBarScreen.kt` is the source of truth for bottom-bar items, and
  `NavDestination?.isBottomBarSelected()` in `LoreweaverNavGraph.kt` keeps Home selected while the
  user is in combat/session flow routes such as `CombatTrackerRoute`, `SessionSummaryRoute`,
  `AdventureLogRoute`, and `SessionHistoryRoute`.

## Data Layer Conventions

- **Room DB**: `AppDatabase` uses version **11**, database name `loreweaver_database`, and
  `exportSchema = false`.
- **Migrations**: `MIGRATION_4_5`, `MIGRATION_5_6`, `MIGRATION_6_7`, `MIGRATION_7_8`,
  `MIGRATION_8_9`, `MIGRATION_9_10`, and `MIGRATION_10_11` are all
  registered in `Room.databaseBuilder()`.
- **JSON columns**: Complex character fields such as `resources`, `actions`, `proficiencies`,
  `inventory`, and `spellSlotsJson` are stored as raw JSON strings and mapped with Gson in
  `data/mapper/DataMappers.kt`.
- **Type converters**: `data/Converters.kt` handles `List<String>` and `Set<String>` values via
  Gson.
- **Note subtype**: `Note` is a sealed class persisted with a `subtype` discriminator such as
  `General`, `Lore`, `NPC`, or `Location`.
- **Derived PC flag**: `isPlayerCharacter` is derived from `party == "Adventurers"` when mapping a
  `CharacterEntry` to its entity.
- **Spell slots**: Domain `spellSlots` uses `Map<Int, Pair<Int, Int>>`; the mapper serializes it as
  `Map<Int, List<Int>>` for Gson round-tripping.
- **Selected spells**: Domain `spells` uses `List<String>` and persists directly through Room list
  converters for readable spell visibility in the builder, detail screen, and action view.
- **Challenge rating**: `challengeRating: Double` was added to `CharacterEntity` in
  `MIGRATION_7_8`.
- **Character identity**: `species` and `background` were added to `CharacterEntity` in
  `MIGRATION_8_9` and now round-trip through the guided character builder.
- **Character spells**: `spells` was added to `CharacterEntity` in `MIGRATION_9_10` and now
  round-trips through the guided builder and action/detail character views.
- **Condition split**: `MIGRATION_10_11` adds `persistentConditions`, copies legacy saved
  `activeConditions` into that new column, and clears encounter-only condition state.
- **Character conditions**: Domain `CharacterEntry` now stores longer-lived
  `persistentConditions` separately from encounter `activeConditions`; the builder, detail views,
  and live tracker all use that split, and live combat can optionally promote a condition into the
  persisted set through `CombatViewModel`.
- **Reference favorites**: The reference feature stores trap, poison, and disease favorites in
  `SharedPreferences` through `ReferencePreferencesRepositoryImpl`.
- **Log capping**: `CampaignRepositoryImpl.insertLog()` keeps only the most recent 100
  `LogEntry` rows.

## Error Handling Pattern

Use `Resource<T>` from `domain/util/Resource.kt` for suspend operations that can fail:

```kotlin
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, ...) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}
```

ViewModel UI state commonly carries `error: String?` and optional retry callbacks so screens can
surface recoverable failures cleanly.

## External Integration

The current runtime app source under `app/src/main` is local-first.

- Data persistence is handled by Room and `SharedPreferences`.
- Backup policy is intentionally split: cloud backup keeps only `reference_preferences.xml`, while
  device-to-device transfer preserves `loreweaver_database` plus its SQLite sidecars and
  `reference_preferences.xml`.
- No Retrofit, OkHttp service layer, or runtime `ApiService` implementation is currently present in
  `app/src/main`.
- Legacy external-API smoke-test scripts are intentionally not kept in the active repo tree because
  they can imply a runtime dependency that the current local-first app does not have.

## Theme

- `MainActivity` calls `LoreweaverTheme(darkTheme = true)` so the app stays in dark mode.
- Dynamic color is disabled by default to preserve the custom fantasy palette defined in
  `ui/theme/Color.kt`.

## D&D 5e Encounter Difficulty

The app includes a full encounter-balancing implementation based on the DMG rules.

- Monsters store CR as `Double`, including fractional CRs such as `0.125`, `0.25`, and `0.5`.
- `CombatViewModel.updateEncounterDifficulty()` recalculates difficulty whenever combatants change.
- Ratings include Trivial, Easy, Medium, Hard, Deadly, and Beyond Deadly.
- Encounter multipliers are applied from monster count and party size.
- The setup section of `CombatTrackerScreen` shows a color-coded difficulty card with adjusted XP.
- `CharacterFormScreen` exposes CR input for non-Adventurer characters.

## D&D 5e Reference UI

`ReferenceScreen` and `ReferenceViewModel` provide a local reference experience with these
major areas:

- Traps
- Poisons
- Diseases
- Spellcasting
- Objects
- Hysteria
- Monsters
- Core rules
- Character creation

Reference behavior notes:

- Trap, poison, and disease sections support favorites, copy, share, and favorites-only filtering.
- Search is live for the searchable sections and debounced in `ReferenceViewModel`.
- Monsters are available through a local catalog with shortcut/type/CR filters and deep-link detail
  routing via `ReferenceDetailResolver`.
- Spellcasting content includes slot tables, formulas, and caster progression helpers from
  `domain/util/SpellcastingReference.kt`.
- The hysteria section includes a d100 roller backed by `ReferenceViewModel.rollHysteria()`.
- Deep-link/detail content is centralized in `domain/util/ReferenceDetailResolver.kt`, which
  resolves spells, conditions, feats, weapons, armor, tools, adventuring gear, magic items,
  ammunition, spellcasting focuses, mounts/vehicles, lifestyles, food/lodging, and hirelings.
- The reference datasets now also rely on `domain/util/CoreRulesReference.kt`,
  `domain/util/CharacterCreationReference.kt`, `domain/util/EquipmentReference.kt`, and
  `domain/util/SrdSpellIndexReference.kt` in addition to the trap/poison/disease/spellcasting
  helpers.

## Build & Run Commands

```bash
# Debug build
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug

# JVM unit tests
./gradlew :app:testDebugUnitTest

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```

`ContentSafetyAuditTest`, `GameplayToolboxSrdAuditTest`, and `MagicItemsSrdAuditTest` all run as
part of `:app:testDebugUnitTest`; the audit suite can rewrite `EXCLUDED_REFERENCE_CORPUS_AUDIT.md`
when the long-prose/reference-corpus inventory changes.

Key versions from `gradle/libs.versions.toml`:

- AGP `9.1.1`
- Kotlin `2.3.20`
- Room `2.8.4`
- KSP `2.3.6`
- Hilt `2.59.2`
- Compose BOM `2026.04.01`
- Lifecycle `2.10.0`
- Navigation `2.9.8`
- Activity Compose `1.13.0`

## Known Build Quirks

- AGP 9.x bundles built-in Kotlin Android support, so the old `org.jetbrains.kotlin.android`
  plugin is intentionally absent.
- The Kotlin serialization plugin must remain enabled because the nav routes use
  `@Serializable` types.
- Release packaging is intentionally hardened in `app/build.gradle.kts`: `dependenciesInfo`
  excludes AGP dependency metadata from APKs/bundles, and the release
  `extractReleaseVersionControlInfo` task is disabled so
  `META-INF/version-control-info.textproto` git provenance metadata is not embedded in shipped
  artifacts.
- Room 2.8.x DAO methods return `Long` and `Int`; `CampaignRepositoryImpl` uses block bodies so
  those return values do not leak through `Unit`-typed repository methods.
- All six migrations are wired into `Room.databaseBuilder()`.
- Some IDE `ComposableFunction0/1/2` errors around Compose lambdas in files such as
  `MainActivity.kt` and `navigation/LoreweaverNavGraph.kt` may be JetBrains IDE analysis false
  positives even when Gradle builds successfully.
- JetBrains `unused declaration` inspections can also produce false positives for manifest-owned
  Android components, Hilt providers/constructors, Room converters, and JUnit entry points even
  when Gradle builds and tests pass.
- Targeted `@Suppress("UNUSED_VALUE")` markers now appear across split Compose screen files such as
  `CampaignDetailEncountersSection.kt`, `CharacterListScreen.kt`, `ReferenceScreenHysteria.kt`, and
  `ui/screens/tracker/setup/EncounterSetupView.kt` to silence false positives on state writes
  inside callbacks.

## Repository Hygiene

For cross-project engineering policy, prefer updating `ENGINEERING_STANDARDS.md`; keep this section
focused on Loreweaver-specific repo hygiene facts and exceptions.

- `gradle/verification-metadata.xml` is repo-owned and should be updated whenever dependency or
  plugin resolution changes. On Windows PowerShell, regenerate it with a command such as:

```powershell
.\gradlew.bat --refresh-dependencies --write-verification-metadata sha256 help :app:assembleDebug :app:assembleRelease :app:assembleDebugAndroidTest :app:testDebugUnitTest --console=plain
```

- Verify checksum enforcement after regeneration with:

```powershell
.\gradlew.bat --refresh-dependencies help :app:assembleDebug :app:assembleRelease :app:assembleDebugAndroidTest :app:testDebugUnitTest --console=plain
```

- Shared JetBrains project settings are intentionally versioned from `.idea/inspectionProfiles/`,
  `.idea/codeStyles/`, `.idea/compiler.xml`, `.idea/gradle.xml`, `.idea/misc.xml`,
  `.idea/runConfigurations.xml`, `.idea/dictionaries/`, `.idea/.name`,
  `.idea/AndroidProjectSystem.xml`, `.idea/jsonCatalog.xml`, and `.idea/.gitignore`.
- Machine-specific IDE state such as `.idea/workspace.xml`, caches, shelves, HTTP requests,
  device/emulator selectors, preview state, and similar local files should remain ignored.
- `EXCLUDED_REFERENCE_CORPUS_AUDIT.md` is a locally regenerated audit artifact used for developer
  review; it should remain ignored and uncommitted unless the project policy explicitly changes.
- Root-level JetBrains inspection export XML files created from `Problems` / `Inspect Code`
  workflows are disposable local artifacts and should stay untracked.
- Root-level markdown audit artifacts such as `EXCLUDED_REFERENCE_CORPUS_AUDIT.md`,
  `HARD_DO_NOT_SHIP_AUDIT.md`, and the `SRD_*_AUDIT.md` files are intentional repo-owned
  documentation, not disposable inspection exports.
- `app/src/main/AndroidManifest.xml` explicitly removes
  `androidx.room.MultiInstanceInvalidationService` because Loreweaver does not enable Room
  multi-instance invalidation; do not restore that service unless `enableMultiInstanceInvalidation()`
  is intentionally adopted in `AppDatabase` creation.
- Prefer adjusting shared inspection entry points for Android/Hilt/Room/JUnit before adding
  `@Suppress("unused")` in source. If suppression is still required, keep it narrowly scoped to a
  verified framework-owned declaration.

## Key Files Reference

- `MainActivity.kt` — Android entry activity; applies `LoreweaverTheme(darkTheme = true)` and
  hosts `LoreweaverApp()`.
- `LoreweaverApplication.kt` — `@HiltAndroidApp` application entry point declared in the manifest.
- `navigation/Routes.kt` — type-safe `@Serializable` route declarations.
- `navigation/LoreweaverNavGraph.kt` — `LoreweaverApp`, `NavHost`, animated transitions, and
  bottom-bar setup/selection rules.
- `navigation/BottomBarScreen.kt` — bottom-navigation destination metadata.
- `di/AppModule.kt` — Hilt providers for Room, DAOs, repositories, and shared preferences.
- `data/AppDatabase.kt` — Room database definition, entity list, and migrations.
- `data/mapper/DataMappers.kt` — domain/entity mapping extensions and Gson conversions.
- `domain/model/Models.kt` — core campaign, encounter, combat, note, and session models.
- `domain/util/Resource.kt` — shared success/error/loading wrapper.
- `domain/util/EncounterDifficulty.kt` — 5e encounter difficulty tables and math.
- `domain/util/TrapReference.kt` — local trap reference dataset.
- `domain/util/PoisonReference.kt` — local poison reference dataset.
- `domain/util/DiseaseReference.kt` — local disease reference dataset.
- `domain/util/CoreRulesReference.kt` — condensed core-rules glossary and lookup content.
- `domain/util/CharacterCreationReference.kt` — character-building reference content and feat data.
- `domain/util/EquipmentReference.kt` — local weapons, armor, tools, gear, mounts, vehicles, and
  magic-item reference datasets.
- `domain/util/SrdSpellIndexReference.kt` — verified SRD spell-name index used when detailed spell
  text is intentionally unavailable.
- `domain/util/SpellcastingReference.kt` — spellcasting rules, slot tables, and formulas.
- `domain/util/ObjectStats.kt` — object AC, HP, and threshold tables.
- `domain/util/HysteriaReference.kt` — short-, long-, and indefinite-hysteria tables.
- `domain/util/ReferenceDetailResolver.kt` — detail/deep-link resolution across core rules,
  character creation, equipment, and spell index datasets.
- `ui/viewmodels/CombatViewModel.kt` — combat tracker state and difficulty updates.
- `ui/viewmodels/ReferenceViewModel.kt` — reference state, favorites, search, and hysteria.
- `ui/screens/CampaignDetailScreen.kt` — campaign detail UI and note-entry workflow.
- `ui/screens/CombatTrackerRoute.kt` — combat tracker route entry; setup/live views are split under
  `ui/screens/tracker/`.
- `ui/screens/ReferenceScreen.kt` — reference UI, copy/share helpers, and detail screens.
- `ui/screens/PromptLibraryScreen.kt` — narrative prompt cards with clipboard copy support.
