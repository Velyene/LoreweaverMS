# SRD Notice

This work includes material from the System Reference Document 5.2.1 ("SRD 5.2.1") by
Wizards of the Coast LLC, available at https://www.dndbeyond.com/srd. The SRD 5.2.1 is licensed
under the Creative Commons Attribution 4.0 International License, available at
https://creativecommons.org/licenses/by/4.0/legalcode.

## Project audit notes

- App-owned rules and reference text should be either verified against the approved SRD source or
  written as original material.
- Automated safety tests scan `app/src/main` for blocked branded references, removed legacy
  proprietary names, and unreviewed long prose blocks.
- The current audited repo snapshot has no separately inventoried excluded reference corpus files
  under `app/src/main`.
- Monster-running guidance and bundled local monster reference content currently ship from
  `app/src/main`; keep the related provenance and scope aligned with the SRD audit documents,
  especially `SRD_MONSTERS_A_TO_Z_AUDIT.md`.
- When adding new rules content, prefer concise summaries and keep source verification explicit.

