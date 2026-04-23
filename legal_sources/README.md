# Legal Sources / Provenance Notes

This folder is for **verification artifacts only**.

It should contain the source materials or notes used to prove that bundled Loreweaver content
comes from approved SRD 5.2.1 material.

## Purpose

Use this folder for items such as:

- `SRD_5_2_1.txt`
- audit notes that map bundled content to SRD entries
- provenance spreadsheets or review notes
- local proof files used during ship review

## Do not use this folder for shipped app content

This folder lives outside `app/src/main`, so it is suitable for review-only materials.

Do **not** treat files here as automatically safe to bundle. A file can live here for proof
purposes and still be rejected from the shipped app.

## Relationship to other folders

- `app/src/main/assets/srd_verified` = bundled, verified content only
- `app/src/main/assets/custom_templates` = bundled custom/homebrew templates
- `quarantine_do_not_ship/` = rejected or unverified content that must not ship
- `HARD_DO_NOT_SHIP_AUDIT.md` = master release gate

## Minimal provenance checklist

For any bundled file or dataset, maintainers should be able to answer:

- What is the source?
- Is the source known exactly?
- Can the exact SRD 5.2.1 entry be pointed to?
- Is the bundled wording/data still cleanly separable from non-SRD material?
- If not, should it be rebuilt, deleted, or quarantined?

## One-line rule

> If I cannot prove this came from SRD 5.2.1, it does not go in the shipped app.

