# Submission checklist (guideline ↔ D1–D6 crosswalk)

Use at every hand-in. Authority: `docs/assignment.md` is SSOT (technical); the guideline PDF
governs packaging/format. Detail: `docs/deliverables-detail.md`.

## Every submission (D1, D2, Final)

- [ ] Cover from `docs/templates/cover.md`: course + topic 14 + instructor + team table (name + student ID + phone + topic).
- [ ] Team table rows match the real group (no invented/dropped rows).
- [ ] Task/work allocation uses seven named role groups (R1–R7) with specific shared outputs per week-window — no generic `All` rows.
- [ ] AI disclosure present wherever AI was used (what + how verified).

## Proposal = D1 (1–3 pages, due 27/9/2026 at 22:30)

- [ ] Order: Cover → Abstract → Task Allocation → Tools/Languages → Objective → Business rules → Entity list → Plan → References + AI disclosure.
- [ ] Abstract: topic + objectives in 1 short paragraph.
- [ ] Tools: pinned stack named (JDK 25 LTS + MySQL 8.4 LTS + Connector/J 8.4.x + Maven 3.9.x + Swing + draw.io).
- [ ] 15–20 numbered business rules; 17-table entity list with weak / M-M / ISA / self-rel marked.

## Midterm = D2 (3–5 pages, due 12/10 W5)

- [ ] Order: Cover → Updated Information → Table of Contents → Database Design.
- [ ] Updated Information lists every revision to proposal/tools/languages.
- [ ] ER meets §2.2 minima: ≥8 entities, ≥8 relationships, weak + partial key, attribute-carrying M-M, ISA (disjoint/total) or multivalued/ternary, self-rel with 2 role names, cardinality + participation everywhere.
- [ ] Draft schema + mapping rule per table group.

## Final = D3 + D5 + D6 (+ D4 slides/demo)

- [ ] D3 is 25+ pages with the exact 12-section order from assignment §2.5 (no reordering), §11 signed by all members, §12 References + AI appendix.
- [ ] Package layout matches `docs/deliverables-detail.md` (`report.pdf`, `slides/`, `src/` + README, `sql/01_schema.sql + 02_data.sql + 03_queries.sql` with English-question comments).
- [ ] D5: script runs twice with no manual repair; PK/FK + ON DELETE/UPDATE/NOT NULL/UNIQUE/CHECK/DEFAULT all in script; ≥15 realistic rows/table; 12/12 queries non-empty; INSERT/UPDATE/DELETE + 1 two-table transaction + 1 index; duplicate-key / bad-FK / CHECK-violation tests rejected.
- [ ] D6: 6 screens (connection externalized, browse grid + row count, parameterized search, master–detail Consultation→PrescriptionLine, CRUD + delete confirm, Top-5 diagnoses report); ≥4/5 topic queries on menu; PreparedStatement everywhere; plain-language FK errors; try-with-resources; README builds/runs on a clean machine.
- [ ] D4: live INSERT/UPDATE/DELETE then show the change in the DB; every member can explain the whole project.
