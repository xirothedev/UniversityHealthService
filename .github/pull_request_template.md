## Which deliverable? (delete the lines that do not apply)

- [ ] D1 proposal · [ ] D2 ER/schema · [ ] D3 report · [ ] D5 SQL · [ ] D6 Java · [ ] D4 demo · [ ] Repo/CI scaffolding

## SSOT check (`docs/assignment.md`)

- [ ] I have read the matching SSOT section and this PR does not edit `docs/assignment.md`
- [ ] Related task IDs (separate Sheet): <!-- e.g. T13, T14 -->

## Checklist by change type (tick only what you touch)

**SQL (`sql/`):**
- [ ] Script runs top to bottom twice with no manual repair (drop → create parents-before-children → insert)
- [ ] Full PK, FK + ON DELETE/UPDATE, NOT NULL, UNIQUE, CHECK, DEFAULT in the script (no Java-only constraints)
- [ ] Sample data ≥15 realistic rows/table, 12/12 queries non-empty
- [ ] Clean `sqlfluff lint sql/` (CI checks too)

**Java (`src/`):**
- [ ] Every user-valued statement uses `PreparedStatement ?` (no concatenation — CI cannot catch it, hand-check + apostrophe `'` test)
- [ ] Connection read from settings outside the code (ENV/`.env`/`db.properties`), `try-with-resources` for every connection/statement/result set
- [ ] Validate required/range/date order before the DB; FK errors in plain language, no stack trace
- [ ] Comments + messages in English (CI checks ASCII)

**Report (`docs/`):**
- [ ] Correct D3 section order (all 12), minimum page counts
- [ ] Form screenshots + SQL behind each (section 9), AI appendix when AI was used

## Review & merge

- [ ] CI green (java + sql + guards)
- [ ] Hand-checked 3 things CI cannot catch: concatenated SQL, `docs/assignment.md` edits, sloppily ticked checklist
- [ ] Request an admin merge (only admins merge into `main`; no reviewer approval needed)
