# CONTRIBUTING — Rules for maximum-score PRs

SSOT is `docs/assignment.md` (original brief, do not edit). Every rule below traces back to that file.

## 1. Branches & PRs

- 1 Sheet task = 1 branch: `d1/rules`, `d2/er`, `d5/schema`, `d6/form-f3`... Never push straight to `main`.
- Small PRs, one deliverable/change type each. Fill in the full PR template (tick the checklist honestly, no box-ticking for show).
- Merge with **squash** (repo blocks merge commits).

## 2. Review & merge (no reviewer required, admin-only merge)

- A PR can merge once CI is green, no reviewer approval needed. Asking a teammate from a different role to take a look is encouraged (individual grading).
- Only admins press merge into `main` (team convention — personal repos cannot technically lock the merge button by role on GitHub; for hard enforcement, move the repo to an organization and enable push-restriction).
- Authors must hand-check 3 things CI cannot catch before requesting an admin merge: string-concatenated SQL, edits to `docs/assignment.md`, sloppily ticked PR checklists.

## 3. Definition of Done

- **SQL:** script runs twice with no manual fix; full PK/FK/NOT NULL/UNIQUE/CHECK/DEFAULT + ON DELETE/UPDATE; realistic data ≥15 rows/table; non-empty queries; clean `sqlfluff`.
- **Java:** PreparedStatement for every input; externalized connection; try-with-resources; validation + readable error messages; English; green `mvn verify`.
- **Docs:** correct sections, minimum page counts, screenshots + SQL, AI appendix when AI was used.

## 4. Forbidden

- Editing `docs/assignment.md` (blocked by CI). To interpret the SSOT differently → open an issue for discussion first.
- Hard-coding DB user/pass/URL, hard-coded data arrays/files, committing the real `db.properties` or `.env`, committing IDE files (`.classpath`, `.project`, `.settings/`).
