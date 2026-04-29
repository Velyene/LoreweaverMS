# Loreweaver Hard Do-Not-Ship Audit

Snapshot date: 2026-04-20

This note translates the project's current repo state into the user's **"If I cannot prove this came
from SRD 5.2.1, it does not go in the shipped app"** rule.

## Release-blocking do-not-ship checklist

Treat the following as a **hard release gate** for Loreweaver. If any item below fails, the content
must not ship from `app/src/main`.

- [ ] **Feat rule:** if a feat is not one of the exact approved SRD feat names, remove it from
  shipped data.
    - Current approved feat set is the 17-name subset already represented in
      `CharacterCreationReference.FEATS` and verified by `CharacterCreationReferenceTest`.
- [ ] **Weapon rule:** if a weapon is not in the approved SRD weapons table, remove it from shipped
  data.
    - Canonical weapon labels live in `EquipmentReference.WEAPONS` and should be treated as an exact
      allowlist, not a suggestion.
- [ ] **Named item provenance rule:** if an item name came from Basic Rules / core books / legacy
  imports and is not proven against the approved SRD source, remove it.
    - This applies to structured item rows, embedded example names, and searchable aliases.
- [ ] **Unknown-source rule:** if the source is unknown, mixed, legacy, or cannot be re-proven
  quickly, quarantine it until verified.
    - Quarantine target: `quarantine_do_not_ship/`

### One-line decision rule

| Condition                                         | Action                      |
|---------------------------------------------------|-----------------------------|
| Feat not in approved SRD feat list                | `REMOVE`                    |
| Weapon not in approved SRD weapon table           | `REMOVE`                    |
| Named item not proven against approved SRD source | `REMOVE`                    |
| Source unknown / mixed / legacy / uncertain       | `QUARANTINE UNTIL VERIFIED` |

## Loreweaver hard do-not-ship check

If any item below is true, do not include it in the bundled app build.

### 1. Source check

If any of the following are true, quarantine it and do not ship it:

- copied from PHB / MM / DMG / Xanathar's / Tasha's / D&D Beyond / Basic Rules pages
- AI output based on copied official text
- source unknown
- cannot be pointed back to the exact SRD 5.2.1 source entry
- SRD wording mixed with non-SRD wording and provenance can no longer be separated

### 2. Spells

Do not ship a spell entry if any of the following are true:

- the spell name is not in the approved SRD spell list from the uploaded spell section
- the spell description was copied from somewhere other than SRD 5.2.1
- the app includes the spell, but its name and text have not been verified against SRD 5.2.1

### 3. Feats

Do not ship a feat if any of the following are true:

- the feat name is not one of the approved SRD feat names
- the feat text was copied from outside the SRD
- the feat exists in the app but is not one of:
    - `Alert`
    - `Magic Initiate`
    - `Savage Attacker`
    - `Skilled`
    - `Ability Score Improvement`
    - `Grappler`
    - `Archery`
    - `Defense`
    - `Great Weapon Fighting`
    - `Two-Weapon Fighting`
    - `Boon of Combat Prowess`
    - `Boon of Dimensional Travel`
    - `Boon of Fate`
    - `Boon of Irresistible Offense`
    - `Boon of Spell Recall`
    - `Boon of the Night Spirit`
    - `Boon of Truesight`

### 4. Weapons

Do not ship a weapon entry if any of the following are true:

- the weapon name is not in the approved SRD weapons table
- the stats / properties / mastery came from outside the SRD
- the weapon is not one of the listed Simple or Martial SRD weapons

### 5. Armor

Do not ship an armor entry if any of the following are true:

- the armor name is not one of the approved SRD armor names
- the armor table values came from somewhere other than SRD 5.2.1
- the armor is not one of:
    - `Padded Armor`
    - `Leather Armor`
    - `Studded Leather Armor`
    - `Hide Armor`
    - `Chain Shirt`
    - `Scale Mail`
    - `Breastplate`
    - `Half Plate Armor`
    - `Ring Mail`
    - `Chain Mail`
    - `Splint Armor`
    - `Plate Armor`
    - `Shield`

### 6. Tools

Do not ship a tool entry if any of the following are true:

- the tool name is not in the approved SRD tools section
- the rules / crafting data came from outside the SRD
- the tool is not one of the listed Artisan's Tools / Other Tools / variants from the approved SRD
  section

### 7. Adventuring gear

Do not ship an item entry if any of the following are true:

- the item name is not in the approved SRD Adventuring Gear / equipment tables
- rules, costs, weights, or effects came from outside the SRD
- the item is not one of the uploaded section's allowed gear entries, ammunition types, focuses,
  holy symbols, packs, mounts, vehicles, or lifestyle tables

### 8. Text blocks

Do not ship a text block if any of the following are true:

- it is a paragraph of official-looking rules prose not copied directly from the SRD
- it is a "summary" that tracks non-SRD official wording too closely
- it is flavor / lore text from an official source outside the SRD
- it is a stat block copied from outside the SRD

### 9. Branding / presentation

Do not ship until fixed if any of the following are true:

- the app uses Wizards logos
- the app looks official or claims to be official D&D content
- the app says or implies "official Dungeons & Dragons app"
- the legal screen is missing SRD attribution

### 10. Unknown or messy content

Assume `NO` and quarantine it if any of the following are true:

- the file was generated during the old 2014-copy workflow
- the file contains mixed content that cannot be cleanly separated
- the review conclusion is "it's probably fine"

## Safe replacement rule

If something fails the check, do one of these:

- `DELETE` it
- `REBUILD` it from SRD 5.2.1 only
- convert it into a blank custom/homebrew user entry
- move it into `quarantine_do_not_ship/` and keep it out of the shipped build

## File-by-file one-line rule

> If I cannot prove this came from SRD 5.2.1, it does not go in the shipped app.

See `quarantine_do_not_ship/README.md` for the tiny audit template and quarantine workflow.

## Audit basis used for this snapshot

- existing repo audit notes:
    - `SRD_EQUIPMENT_ALLOWLIST_AUDIT.md`
    - `SRD_GAMEPLAY_TOOLBOX_AUDIT.md`
    - `SRD_MAGIC_ITEMS_AUDIT.md`
    - `EXCLUDED_REFERENCE_CORPUS_AUDIT.md`
- current shipped source under `app/src/main`
- targeted text searches for:
    - `2014`
    - `SpellDescriptionReference`
    - `MonsterReference`
    - `CharacterClassReference`
    - `ClassSpellListReference`
    - `Magic Initiate (Cleric)` / `Magic Initiate (Wizard)`
    - `Musket` / `Pistol`
    - `Three-Dragon Ante`
    - `Energy Bow`
    - `Hat of Many Spells`
    - `Arcanist’s Magic Aura`

## Current repo state note

This file now serves as both:

- the current **policy gate** for strict SRD-only shipping decisions, and
- a **historical record** of the corpora that were previously flagged during quarantine work.

At the current repo snapshot:

- the previously flagged bundled monster/class/spell corpora are no longer present under
  `app/src/main`
- `EXCLUDED_REFERENCE_CORPUS_AUDIT.md` reports **0** discovered excluded-corpus files
- the Reference screen includes reviewed SRD-derived monster reference content
- concise monster-running guidance still ships through reviewed `CoreRulesReference` content

## High-level result

**Treat this note as the strict shipping policy, not as evidence that the previously flagged corpora
still ship today.**

The current workspace still requires SRD-only proof discipline, but the specific bundled corpora
called out below were historical findings that have since been removed from `app/src/main` during
the quarantine/alignment work.

## Existing checks that already pass

The following existing unit tests passed in this workspace during this audit:

- `ContentSafetyAuditTest`
- `CharacterCreationReferenceTest`
- `EquipmentReferenceTest`
- `GameplayToolboxSrdAuditTest`
- `MagicItemsSrdAuditTest`

That means the repo already has a useful **baseline audit system**, but those tests are **not
equivalent** to the stricter hard rule in this note.

## Historical hard do-not-ship findings

The sections below document the repo findings that originally triggered quarantine/removal work.
They remain useful as a release-review checklist and provenance history, but they should not be read
as a claim that those files are still present in the current shipped source tree.

### 1. Quarantine now — bundled reference corpora with explicit risk signals

#### `app/src/main/java/com/example/loreweaver/domain/util/ClassSpellListReference.kt`

**Why flagged**

- file header says:
  `Static class spell lists sourced from the provided 2014 spellcasting chapter excerpt.`
- the hard rule says files from an old 2014-copy workflow or mixed/uncertain provenance should not
  ship.

**Action**

- `QUARANTINE` or `REBUILD`
- do not include this file's embedded spell-name corpus in a bundled release until it is re-proven
  against the approved SRD 5.2.1 source file

#### `app/src/main/java/com/example/loreweaver/domain/util/SpellDescriptionReference.kt`

#### `app/src/main/java/com/example/loreweaver/domain/util/SpellDescriptionReferenceSetTwo.kt`

#### `app/src/main/java/com/example/loreweaver/domain/util/SpellDescriptionReferenceSetThree.kt`

#### `app/src/main/java/com/example/loreweaver/domain/util/SpellDescriptionReferenceSetFour.kt`

#### `app/src/main/java/com/example/loreweaver/domain/util/SpellDescriptionReferenceSetFive.kt`

#### `app/src/main/java/com/example/loreweaver/domain/util/SpellDescriptionReferenceSetSix.kt`

####

`app/src/main/java/com/example/loreweaver/domain/util/SpellDescriptionReferenceSrdAdditions.kt`

**Why flagged**

- these files are a large bundled spell corpus
- the hard rule requires each shipped spell name and spell text to be verified against the approved
  SRD 5.2.1 spell source
- repo-level prose auditing currently treats them as a separate excluded corpus inventory, not as
  fully app-owned verified content
- search hit found `Arcanist’s Magic Aura`, which is exactly the kind of name variant that needs
  strict provenance confirmation before shipping

**Action**

- `QUARANTINE` unless every shipped spell entry is mapped to the approved SRD 5.2.1 spell source
  file
- safest replacement is a blank/custom spell-entry flow or a rebuilt SRD-only subset

#### `app/src/main/java/com/example/loreweaver/domain/util/MonsterReference.kt`

**Why flagged**

- large bundled monster corpus remains under `app/src/main`
- current repo prose auditing inventories it as an excluded reference corpus, which is not the same
  thing as “safe to ship under strict proof rules”
- search hit found monster text referencing `Pistol`, which is a strong signal that this file needs
  per-entry provenance review before release

**Action**

- `QUARANTINE` or `REBUILD`
- do not ship monster stat-block summaries unless each entry is traced back to approved SRD 5.2.1
  source material

#### `app/src/main/java/com/example/loreweaver/domain/util/CharacterClassReference.kt`

**Why flagged**

- large bundled class reference remains under `app/src/main`
- current repo prose auditing inventories it as excluded corpus content rather than fully trusted
  app-owned prose
- class progression/features are exactly the kind of structured rules text that the hard rule says
  to quarantine if provenance is uncertain

**Action**

- `QUARANTINE` or `REBUILD`
- ship only minimal app-authored summaries if they can be kept clearly separate from source-derived
  class text

### 2. Re-verify before ship — specific structured entries that conflict with the hard checklist

#### `app/src/main/java/com/example/loreweaver/domain/util/CharacterCreationReference.kt`

**Observed hits**

- `Magic Initiate (Cleric)`
- `Magic Initiate (Wizard)`

**Why flagged**

- the hard feat rule allowlist includes `Magic Initiate`, not class-qualified variants
- even if these are intended as background-linked spell-list choices, they should not be shipped as
  feat names unless the approved SRD source explicitly supports that exact bundled representation

**Action**

- `REVERIFY` representation against the approved SRD 5.2.1 source
- if uncertain, normalize bundled data to the exact allowed feat name `Magic Initiate` and move
  list-specific detail into user-entered/custom content or clearly-audited supporting metadata

#### `app/src/main/java/com/example/loreweaver/domain/util/EquipmentReference.kt`

**Observed hits**

- `Musket`
- `Pistol`
- `Three-Dragon Ante`
- `Energy Bow`
- `Hat of Many Spells`

**Why flagged**

- the hard checklist requires weapons, tools, and text-heavy item entries to map to the approved SRD
  5.2.1 source
- these entries should be treated as **needs-proof** items, not assumed-safe items
- `Energy Bow` and `Hat of Many Spells` are especially important to re-check because they stand out
  as entries that could be outside a conservative SRD-only ship scope

**Action**

- `REVERIFY` each hit against the approved SRD 5.2.1 source file
- if provenance cannot be shown quickly, `QUARANTINE` those specific entries from shipped structured
  data

### 3. Search-surface warning — current UI still exposes high-risk corpora

Current app source still wires bundled high-risk corpora into the reference UI:

- `ReferenceScreen.kt` imports and surfaces `CharacterClassReference`
- `ReferenceScreen.kt` imports and surfaces `MonsterReference`
- `ReferenceScreen.kt` surfaces `CharacterCreationReference.BACKGROUNDS` and
  `CharacterCreationReference.FEATS`
- `ReferenceScreen.kt` surfaces `EquipmentReference.MAGIC_ITEMS_A_TO_Z`

**Action**

Before a strict-compliance release build:

- remove or hide quarantined datasets from the UI, not just from tests/docs
- ensure quarantined content is no longer under `app/src/main`

## Items that currently look comparatively safer

These areas already have repo-local audit notes that make them better candidates for a strict
rebuild from proven sources:

- `EquipmentReference.WEAPONS`, `ARMOR`, `ARTISANS_TOOLS`, `OTHER_TOOLS`, `ADVENTURING_GEAR`,
  `AMMUNITION`, `FOCUSES`, `MOUNTS`, `TACK_AND_DRAWN_ITEMS`, `LARGE_VEHICLES`,
  `FOOD_DRINK_AND_LODGING_TABLE`
- `CharacterCreationReference.FEATS`
- `PoisonReference`
- `TrapReference`
- `DiseaseReference`
- selected `CoreRulesReference` material

**Important:**
Even these should be shipped only if the maintainer can still point back to the exact approved SRD
5.2.1 source file or excerpt used for verification.

## Branding / presentation check

No obvious bundled app-owned branding claims were found for:

- `official Dungeons & Dragons`
- `official D&D`
- `official app`
- Wizards logos claims in source text

The repo already includes SRD attribution in `SRD_NOTICE.md`.

## Recommended ship gate

For a strict release candidate, use this rule:

### KEEP in shipped build only after proof

- `EquipmentReference` subsets already covered by the equipment audits, after final source-file
  verification
- `CharacterCreationReference.FEATS`, after confirming exact naming and background-link formatting
- Gameplay Toolbox subsets already matched by the gameplay audit

### QUARANTINE before ship

- `ClassSpellListReference.kt`
- `SpellDescriptionReference*.kt`
- `MonsterReference.kt`
- `CharacterClassReference.kt`
- any `EquipmentReference` item rows that cannot be pointed back to the approved SRD source quickly

## Minimal next-step checklist

- [ ] Apply the four hard rules above before any release build
- [ ] Apply the 10-section Loreweaver hard do-not-ship check before any release build
- [ ] Move high-risk corpora out of `app/src/main`
- [ ] Replace them with empty/custom/homebrew placeholders if the UI still needs those screens
- [ ] Normalize any shipped feat/background labels to exact allowlisted names
- [ ] Re-run the existing audit tests
- [ ] Add a stricter release-blocking test for the hard rule once the quarantine pass is complete

## Fast Android Studio search terms

Search the project for these terms during ship review:

- `Player's Handbook`
- `Monster Manual`
- `Dungeon Master's Guide`
- `Basic Rules`
- `D&D Beyond`
- `Xanathar`
- `Tasha`
- `spell`
- `feat`
- `weapon`
- `armor`
- `item`
- `monster`
- `background`
- `subclass`

Anything suspicious gets audited before shipping.

