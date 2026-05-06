# Engineering Standards

This document defines the day-to-day engineering standards for `Loreweaver`.

It complements `AGENTS.md`, which explains project architecture and repo-specific facts. This file is the
team's working agreement for how code, tests, accessibility, localization, audits, and releases should
be handled.

## Contents

1. Core principles
2. Architecture expectations
3. Kotlin and Compose standards
4. File size and splitting standards
5. ViewModel and UI state standards
6. Accessibility standards
7. Localization and string handling
8. Comments and documentation style
9. Testing standards
10. Content and audit standards
11. Static analysis and warnings
12. Dependency and security hygiene
13. Pull request and review standards
14. CI and quality gates
15. Release readiness checklist
16. Definition of done
17. Project-specific priorities

---

## 1. Core principles

- Prefer **clear, maintainable code** over clever or compact code.
- Keep changes **small, focused, and reviewable**.
- Treat **Gradle builds and tests** as the source of truth for project health.
- Treat **text integrity** as part of build health: repo-owned text should remain valid UTF-8 and
  free of mojibake.
- Keep **docs aligned with shipped behavior**, not planned behavior.
- Treat **accessibility, localization, and audit safety** as normal engineering work, not polish.
- Comments should explain **why**, constraints, or tradeoffs—not restate what the code already says.

---

## 2. Architecture expectations

`Loreweaver` follows a local-first MVVM + Clean Architecture structure.

### Required boundaries

- `ui/` owns Compose rendering, view models, UI state, UI events, and presentation helpers.
- `domain/` owns models, repository contracts, use cases, and shared rules/utilities.
- `data/` owns Room entities, DAOs, mappers, converters, and repository implementations.
- `di/` owns wiring only.

### Rules

- UI code must not access Room, DAOs, or concrete repository implementations directly.
- Business rules should live in `domain/` or in clearly justified shared helpers, not inline in composables.
- Repository interfaces belong in `domain/repository/`; implementations belong in `data/repository/`.
- Shared state patterns should be extracted once they repeat across view models or screens.

---

## 3. Kotlin and Compose standards

### Kotlin

- Prefer immutable values (`val`) unless mutation is necessary.
- Use descriptive names for functions, models, and state fields.
- Define a constant instead of duplicating meaningful literals.
- Obvious one-off literals may stay inline.
- Use named constants for retry counts, status labels, sentinel values, and other shared domain values.
- Keep functions small and single-purpose where practical.
- Avoid broad suppression annotations; prefer the smallest local fix.

### Compose

- Composables should focus on rendering and direct UI interaction.
- Extract helpers when a composable mixes rendering, formatting, filtering, semantics, and state wiring.
- Prefer stateless child composables with explicit parameters when possible.
- Keep formatting helpers and semantics helpers separate from large screen bodies when they reduce scan time.
- Use `stringResource(...)` for user-facing text shown in composables.

---

## 4. File size and splitting standards

Large files are review and maintenance risks. Split before they become hard to reason about.

### Review triggers

These are not absolute limits, but they are prompts to split a file:

- screen/composable file grows past roughly **400–500 LOC**
- view model grows past roughly **250–300 LOC**
- a file mixes **layout + filtering + formatting + semantics + dialog state**
- a reference file mixes **raw dataset content + search logic + resolver logic**

### Table of contents requirement

Any source or repo-owned documentation file over **150 lines** must include a top-level table of
contents appropriate to the file type.

- Kotlin/Java files should use the standard header comment with `FILE:` and `TABLE OF CONTENTS:`.
- Markdown files should include a visible `Contents` section near the top.
- The table of contents should describe the real section groupings in the file and be kept updated
  when sections are added, removed, or split.
- If a large file is intentionally temporary during a refactor, restore its table of contents before
  considering the change complete.

### Preferred split patterns

For UI files:

- `ScreenRoute.kt`
- `ScreenContent.kt`
- `ScreenComponents.kt`
- `ScreenSemantics.kt`
- `ScreenFormatting.kt`

For large reference content:

- split by subdomain, section, or dataset slice
- keep raw content separate from filtering/search/helpers where practical

---

## 5. ViewModel and UI state standards

All view models should follow a consistent state model.

### Required patterns

- one immutable `UiState` model per screen/view model
- one private `MutableStateFlow`
- one public `StateFlow`
- one consistent loading/error/retry contract
- one-off UI effects should be separated from persistent screen state when needed

### Loading and error handling

- shared helper functions should be used when the same transitions repeat across view models
- user-facing errors should be short and safe
- technical detail can be logged or preserved for diagnostics, but should not overwhelm the UI
- retry behavior should be explicit when failure is recoverable

### Preferred conventions

- `beginLoading()`
- `withError(...)`
- `clearErrorState()`
- retry callback stored only when the screen can actually recover from the failure

---

## 6. Accessibility standards

Accessibility is required for all important user flows.

### Required

- meaningful labels for important interactive controls
- `heading()` semantics on important section headings
- `contentDescription` or `stateDescription` for dynamic or non-obvious controls
- no critical meaning conveyed by color alone
- large enough touch targets for common interactions
- semantics should read naturally in screen readers

### Preferred

- accessibility text should come from resources when it is user-facing text
- use formatted string resources instead of inline interpolation where a reusable spoken sentence exists
- add Compose accessibility tests for critical flows and dialogs

### Priority surfaces

Accessibility coverage is especially important for:

- combat tracker
- encounter setup
- dialogs
- character list/detail actions
- reference navigation and search

---

## 7. Localization and string handling

All user-facing text should be localization-ready.

### Required

- put user-visible strings in `app/src/main/res/values/strings.xml`
- avoid inline button labels, placeholders, titles, and accessibility summaries
- use formatted resources rather than concatenating sentence fragments in code
- keep canonical internal values separate from display labels where those concepts differ
- preserve UTF-8 cleanliness in repo-owned text resources, datasets, docs, and build/config files
  when editing them

### Examples that should be resource-backed

- dialog titles and button labels
- search placeholders
- empty states
- spoken accessibility summaries
- chip/button labels shown to users
- filter summaries and error messages shown in UI

---

## 8. Comments and documentation style

### Comments

Use comments for:

- rationale
- constraints
- tradeoffs
- framework quirks
- audit/provenance notes
- non-obvious behavior
- professional comments that clarify complex or multi-step logic when needed

Avoid comments that only restate the next line of code.

### Docs

When behavior changes, update the relevant repo-owned docs:

- `README.md`
- `AGENTS.md` when architecture or key conventions change
- SRD/audit markdown when shipped content expectations change
- engineering/review policy docs when quality gates or repo-wide expectations change

Docs must describe current shipped behavior.

---

## 9. Testing standards

Tests should protect behavior and reduce release risk.

### Required

- new logic should include tests at the most appropriate level
- bug fixes should include regression coverage when feasible
- content/audit logic changes must preserve or update the audit tests that govern them

### Expected test layers

#### Unit tests

Use for:

- business logic
- sorting/filtering/search helpers
- mappers/converters
- ViewModel state transitions
- formatting helpers
- detail resolver logic
- encounter difficulty calculations

#### Compose/UI tests

Use for:

- critical accessibility semantics
- important dialogs
- navigation behavior where practical
- search/filter behavior on important screens

#### Instrumented tests

Reserve for:

- Room integration
- Android/runtime behavior that JVM tests cannot cover
- Compose interactions that require device/emulator infrastructure

---

## 10. Content and audit standards

`Loreweaver` ships reviewed local rules/reference content. That makes content discipline part of normal
engineering work.

### Required

- keep audit tests green when changing shipped rules/reference content
- keep provenance and scope aligned with the repo-owned audit docs
- do not reintroduce unreviewed long-form content casually
- keep rules summaries concise and mechanically useful
- keep repo-owned text files valid UTF-8 and correct mojibake or replacement-character corruption
  before considering the change complete

### When editing large reference datasets

Check whether the change also requires review in:

- `ContentSafetyAuditTest`
- `GameplayToolboxSrdAuditTest`
- `MagicItemsSrdAuditTest`
- monster audit documents
- `SRD_NOTICE.md`
- other SRD/audit markdown tied to the edited corpus

### Preferred practice

Large reference files should make it easy to answer:

- what content is shipped
- what content is original summary vs reviewed SRD-backed content
- what tests/audits govern the file

---

## 11. Static analysis and warnings

### Required

- fix real warnings when practical
- prefer shared inspection/profile fixes over repeated local suppressions
- keep suppressions narrow and justified

### Preferred order of action

1. improve the code
2. improve shared inspection configuration
3. add a small scoped suppression only if framework behavior makes that necessary

False positives around Android components, Hilt, Room, Compose lambdas, and JUnit entry points should
be handled consistently rather than suppressed broadly.

---

## 12. Dependency and security hygiene

### Required

- do not add dependencies without a clear need
- prefer existing project patterns and libraries before introducing new ones
- keep runtime scope local-first unless there is an intentional architecture change
- never commit secrets or machine-specific credentials

### Preferred

- validate dependency safety periodically
- remove unused dependencies and dead integration stubs
- keep permissions, storage behavior, and backup behavior explicit

---

## 13. Pull request and review standards

### Required

- one logical purpose per PR
- no unrelated formatting churn
- keep diffs focused and reviewable
- include tests when behavior changes
- include doc updates when user-visible or audit-visible behavior changes

### Review checklist

Reviewers should check:

- correctness
- clarity
- architectural fit
- accessibility impact
- localization impact
- audit/provenance impact
- test coverage
- unnecessary churn

---

## 14. CI and quality gates

The exact CI workflow can evolve, but these are the expected quality gates.

### Baseline gates

- `auditUtf8Mojibake`
- `:app:testDebugUnitTest`
- `:app:assembleDebug`
- lint/static analysis when configured
- relevant audit tests when content changes

### Text integrity gate

Changes that touch repo-owned text, docs, resources, datasets, scripts, or build logic must pass the
UTF-8/
mojibake audit gate before they are considered ready. Local verification may use the repository's
documented
audit entrypoint, but the policy requirement is that the canonical audit gate remains green in CI.

### Content-sensitive changes

Changes in `domain/util/*Reference*` and related audit/docs should be checked against:

- `ContentSafetyAuditTest`
- gameplay/toolbox audit tests
- magic item audit tests
- monster/reference tests relevant to the changed dataset

A change is not complete if the governing audits are left broken or stale.

---

## 15. Release readiness checklist

Before shipping or cutting a release candidate:

- UTF-8/mojibake audit passes
- build passes
- unit tests pass
- relevant audit tests pass
- migrations still align with the current DB version and wired migrations
- docs match the shipped state
- no unreviewed content drift is introduced
- critical accessibility flows have been spot-checked

---

## 16. Definition of done

A change is done when:

- the behavior works
- the code is maintainable
- tests are appropriate for the change
- localization/accessibility were considered
- relevant docs are updated
- relevant audits remain accurate
- repo-owned text touched by the change remains UTF-8 clean and audit-clean
- unrelated churn is avoided

---

## 17. Project-specific priorities

Current ongoing priorities for `Loreweaver` should generally be:

1. standardize ViewModel state/error/loading patterns
2. continue splitting oversized UI and content files
3. finish resource-backed text and accessibility semantics cleanup
4. expand Compose accessibility coverage on critical screens
5. keep audit-driven content discipline strong as local reference datasets evolve

These priorities should guide refactors when choosing between equally valid implementation options.

