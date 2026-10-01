# Details of the 6 deliverables D1–D6 (source: `docs/assignment.md` §2.1–2.5, §2.7)

Formatting/packaging source: `Project Submittion Guideline.pdf` (course submission stages).
Authority rule: `docs/assignment.md` is SSOT for technical content (never edit by hand, CI-blocked).
The guideline PDF governs packaging/format only. Where dates disagree, a dated instructor
announcement (Zalo/email) overrides both documents; failing that, earliest-deadline-wins.
Concretely: the instructor set D1 at 27/9/2026 22:30 (one PDF via Blackboard), superseding
the guideline's W2 and the assignment's W3; D3 ships 16/11 W10, which beats the guideline's W11.

Deadlines count from Week 1 = 8/9/2026. Late submission only for a pre-reported force-majeure reason (illness); "busy with another course" does not count.

## Stage map (guideline ↔ D-system)

| Guideline stage | Repo deliverable | Due (resolved) | Length |
|---|---|---|---|
| Proposal Report (week 2) | D1 — Proposal | 27/9/2026, 22:30 (instructor announcement) | 1–3 pages |
| Midterm Report (week 5) | D2 — Progress report | 12/10 (W5, both agree) | 3–5 pages |
| Final Report (week 11) | D3 + D5 + D6, D3 due 16/11 (W10, earlier of W10 vs guideline W11); D4 demo W13–15 | see below | 25+ pages + code + SQL + slides |

## Cover rule (every submission)

Superset cover on all three stages — satisfies both the guideline and assignment §1.2:

- Course name: IT079IU – Principles of Database Management.
- Project topic name: Topic 14 — University Health Service.
- Instructor: Assoc. Prof. Dr. Nguyen Thi Thuy Loan.
- Team table: name + student ID + phone + topic (assignment §1.2 requires a copy with every deliverable).
- Template: `docs/templates/cover.md`.

Group size note: assignment §1.2 suggests ten or eleven and freezes membership at registration.
This group has **12 confirmed members**, the size the instructor set at registration — that
override outranks the §1.2 suggestion, so every team table keeps all 12 rows. Never invent or
drop a row to match a number. Roster of record: `.private/group02-members.csv`.

## D1 — Proposal (= guideline Proposal Report), 1–3 pages, due 27/9/2026 at 22:30

Section order: guideline sections first, then assignment sections (both checklists pass):

1. Cover Page (see Cover rule).
2. Abstract — brief topic + objectives (guideline).
3. Task Allocation Table — seven named role groups (R1–R7) with specific shared responsibilities and outputs per week-window (guideline). Ban generic `All`; template `docs/templates/task-allocation.md`.
4. Tools and Programming Languages — pinned stack: JDK 25 LTS + MySQL 8.4 LTS + Connector/J 8.4.x + Maven 3.9.x + Swing + draw.io (ADR-01, ADR-02).
5. Objective in 1 sentence: fast record lookup for the health station + early outbreak detection (topic 14 objective).
6. Business rules, 15–20 numbered sentences.
7. Entity list of 17 tables + where the weak / M-M / ISA / self-rel sit.
8. Plan W3/W5/W10/W13–15 + roles table.
9. References + AI disclosure (what was used + how it was verified, per assignment §2.6).

- Lock the topic at D1. Anyone without a group/topic gets assigned one by the instructor from the remaining pool.
- Leads: Team lead + Data modeler. Scores Motivation (10) + Requirements (15).
- Detail: `docs/D1-outline.md`.

## D2 — Progress report (= guideline Midterm Report), 3–5 pages, due 12/10 (W5)

Section order:

1. Cover Page (Cover rule).
2. Updated Information — revisions to proposal (task allocation, tools, languages) (guideline + assignment).
3. Table of Contents (guideline).
4. Database Design (guideline) = full ER (≥8 entities, ≥8 relationships, weak + partial key, attribute-carrying M-M, ISA with disjoint/total, self-rel with 2 role names, cardinality + participation on every relationship) + draft schema + mapping rule per table group.

- Last chance to change topic (needs instructor approval).
- Leads: Data modeler + Normalization (FD draft).

## D3 — Final report (= guideline Final Report), 25+ pages, due 16/11 (W10), exact 12 sections in order

Keep the assignment §2.5 order exactly (no reordering):

1. Introduction (users, decisions supported). 2. Requirements + business rules. 3. ER + hard-decision rationale. 4. Schema + mapping rule per table. 5. FDs + minimal cover + candidate keys (proved). 6. Normalization: normal form per relation + decompositions + lossless proof (stopping at 3NF requires naming which constraint BCNF would lose). 7. DB implementation: system + version, script, sample data, transaction + why, index + which query it helps. 8. Queries: English question + SQL + sample result. 9. App: connection method, screenshot of every form + SQL behind it. 10. Discussion: what cannot be expressed + what to change. 11. Work allocation (all members sign). 12. References + AI-use appendix (what was used + how it was verified).
- Leads: Tester & Writer assembles, every role writes its own section.

## Final supporting package (= guideline Supporting Materials, submitted with D3)

```
submit/final/
├── report.pdf            # D3, 25+ pages, 12 sections in order
├── slides/               # presentation slides (D4)
├── src/                  # D6 Java source + README (JDK, driver, connection, build/run)
└── sql/
    ├── 01_schema.sql
    ├── 02_data.sql
    └── 03_queries.sql    # each statement preceded by its English question comment
```

- `src/` + `README` build/run steps: a project that does not run scores zero for screens.
- Pre-submit check: `docs/submission-checklist.md`.

## D4 — 15-minute demo, 1–21/12 (W13–15)

- Live INSERT/UPDATE/DELETE demo then show the change in the DB. Each member is questioned individually on the whole project (saying "that was someone else's part" is banned). Scores demo/answers (20) + writing (10).
- 30% of the project mark is questioning/commenting on other groups across 3 report sessions — prepare questions from T31.

## D5 — `.sql` file, submitted with D3

- 1 script running top to bottom twice with no manual repair: drop → create (parents before children) → insert.
- All constraints in the script: PK, FK + ON DELETE/UPDATE, NOT NULL, UNIQUE, CHECK, DEFAULT. Constraints enforced only in Java score nothing.
- ≥15 realistic rows/table, 12/12 queries non-empty. Separate query file: each statement preceded by its English question comment.
- Also: INSERT/UPDATE/DELETE for the main tables + 1 two-table transaction (COMMIT/ROLLBACK) + 1 index. Test first: duplicate keys, bad FKs, CHECK violations must all be rejected.
- Leads: DB developer + Query developer. Largest weight (Technical 45).

## D6 — Java app, submitted with D3

- 6 mandatory screens, numbered F0–F5 (the numbering ADR-02 uses; ADR-07's "F1–F5" predates F0): F0 externalized connection (properties file, grader points at another server with no rebuild), F1 browse grid + row count, F2 parameterized search, F3 master–detail (Consultation→PrescriptionLine), F4 editing CRUD + delete confirmation, F5 aggregate report (Top-5 diagnoses). Put ≥4/5 topic queries on the menu.
- Code rules: PreparedStatement for every user-valued statement (concatenation = defect, grader tests with an apostrophe), FK errors in plain language not stack traces, validate required/range/date order, close connections with try-with-resources.
- Submit source + README (JDK, driver, connection, build/run steps). A project that does not run scores zero for screens.
- Leads: Java developer + Tester.
