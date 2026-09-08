# Details of the 6 deliverables D1–D6 (source: `docs/assignment.md` §2.1–2.5, §2.7)

Deadlines count from Week 1 = 8/9/2026. Late submission only for a pre-reported force-majeure reason (illness); "busy with another course" does not count.

## D1 — Proposal, 1–3 pages, due 28/9 (W3)

- Content: UHS objective sentence (fast record lookup + early outbreak detection), 15–20 numbered business rules, entity list of 11 tables, pinned stack (JDK 21 LTS + MySQL 8.4 LTS + Connector/J 8.4 + Maven 3.9 + Swing), work-allocation table.
- Attach to every submission: team info table (name + student ID + phone + topic).
- Lock the topic at D1. Anyone without a group/topic gets assigned one by the instructor from the remaining pool.
- Leads: Team lead + Data modeler. Scores Motivation (10) + Requirements (15).

## D2 — Progress report, 3–5 pages, due 12/10 (W5)

- Content: full ER (≥8 entities, ≥8 relationships, weak + partial key, attribute-carrying M-M, ISA with disjoint/total, self-rel with 2 role names, cardinality + participation on every relationship) + draft schema + mapping rule per table group.
- Last chance to change topic (needs instructor approval).
- Leads: Data modeler + Normalization (FD draft).

## D3 — Final report, 25+ pages, due 16/11 (W10), exact 12 sections in order

1. Introduction (users, decisions supported). 2. Requirements + business rules. 3. ER + hard-decision rationale. 4. Schema + mapping rule per table. 5. FDs + minimal cover + candidate keys (proved). 6. Normalization: normal form per relation + decompositions + lossless proof (stopping at 3NF requires naming which constraint BCNF would lose). 7. DB implementation: system + version, script, sample data, transaction + why, index + which query it helps. 8. Queries: English question + SQL + sample result. 9. App: connection method, screenshot of every form + SQL behind it. 10. Discussion: what cannot be expressed + what to change. 11. Work allocation (all 12 sign). 12. References + AI-use appendix (what was used + how it was verified).
- Leads: Tester & Writer assembles, every role writes its own section.

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

- 6 mandatory screens: externalized connection (properties file, grader points at another server with no rebuild), browse grid + row count, parameterized search, master–detail (Consultation→PrescriptionLine), editing CRUD + delete confirmation, aggregate report (Top-5 diagnoses). Put ≥4/5 topic queries on the menu.
- Code rules: PreparedStatement for every user-valued statement (concatenation = defect, grader tests with an apostrophe), FK errors in plain language not stack traces, validate required/range/date order, close connections with try-with-resources.
- Submit source + README (JDK, driver, connection, build/run steps). A project that does not run scores zero for screens.
- Leads: Java developer + Tester.
