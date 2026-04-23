# Quarantine Do Not Ship

Anything placed in this folder is **not part of the Android app bundle** because it lives outside
`app/src/main`.

Use this folder for any file or exported data block that fails the project's hard-ship rule:

> If I cannot prove this came from SRD 5.2.1, it does not go in the shipped app.

## Important Android packaging warning

Do **not** use `app/src/assets/quarantine_do_not_ship` for rejected material.

On Android, files under `app/src/main/assets` are bundled into the app. For Loreweaver,
quarantine content must stay **outside** `app/src/main` if the goal is "do not ship."

## Recommended folder workflow

Use a layout like this:

```text
/app
  /src
    /main
      /assets
        /srd_verified
        /custom_templates
/legal_sources
  SRD_5_2_1.txt
/quarantine_do_not_ship
```

Meaning:

- `app/src/main/assets/srd_verified` = only files that are proven safe to bundle
- `app/src/main/assets/custom_templates` = blank/custom/homebrew templates that are app-authored
- `legal_sources/` = provenance-only source material and verification anchors
- `quarantine_do_not_ship/` = rejected or unverified material that must stay out of the shipped
  app

## Suggested workflow

1. Copy or move questionable source material here.
2. Record the original path.
3. Record the reason it failed review.
4. Replace shipped app content with one of:
    - deletion
    - SRD-only rebuild
    - blank custom/homebrew entry
    - temporary unavailable notice in the UI

## Hard decision rules

- If a feat is not in the SRD list -> remove it from shipped app data
- If a weapon is not in the SRD table -> remove it from shipped app data
- If an item name came from Basic Rules / core books / legacy imports and lacks approved SRD proof,
  remove from shipped app data
- If the source is unknown -> quarantine until verified

When in doubt, choose quarantine over shipment.

## Safe replacement rule

If something fails the check, do one of these:

- delete it
- rebuild it from SRD 5.2.1 only
- convert it into a blank custom/homebrew user entry
- move it into this folder and keep it out of the shipped build

## One-line rule for every file

> If I cannot prove this came from SRD 5.2.1, it does not go in the shipped app.

## Tiny audit template

```text
File:
Original path:
Content type:
Source known? Y/N
Verified in SRD 5.2.1? Y/N
Bundled in app? Y/N
Action: KEEP / REBUILD / QUARANTINE / DELETE
Notes:
```

## Historical quarantine examples from the repo audit

These file paths were previously called out during the quarantine pass. They remain here as
examples of the kinds of corpora that should be moved or rebuilt when provenance is uncertain.

They should not be assumed to still exist under `app/src/main` in the current repo snapshot.

- `app/src/main/java/com/example/loreweaver/domain/util/ClassSpellListReference.kt`
- `app/src/main/java/com/example/loreweaver/domain/util/SpellDescriptionReference.kt`
- `app/src/main/java/com/example/loreweaver/domain/util/SpellDescriptionReferenceSetTwo.kt`
- `app/src/main/java/com/example/loreweaver/domain/util/SpellDescriptionReferenceSetThree.kt`
- `app/src/main/java/com/example/loreweaver/domain/util/SpellDescriptionReferenceSetFour.kt`
- `app/src/main/java/com/example/loreweaver/domain/util/SpellDescriptionReferenceSetFive.kt`
- `app/src/main/java/com/example/loreweaver/domain/util/SpellDescriptionReferenceSetSix.kt`
- `app/src/main/java/com/example/loreweaver/domain/util/`
  `SpellDescriptionReferenceSrdAdditions.kt`
- `app/src/main/java/com/example/loreweaver/domain/util/MonsterReference.kt`
- `app/src/main/java/com/example/loreweaver/domain/util/CharacterClassReference.kt`

See `HARD_DO_NOT_SHIP_AUDIT.md` for the repo-specific rationale behind those flags.

Use `legal_sources/README.md` to document where verification artifacts belong.

