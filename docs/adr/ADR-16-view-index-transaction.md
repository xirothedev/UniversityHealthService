# ADR-16: One view, one index, two transactions

Supersedes ADR-08, now kept in `docs/adr/superseded/ADR-08-index-transaction.md`.

**Update 2026-11-21:** the index now lives in `01_schema.sql` so every build has
it; the view and transactions live in `sql/04_t18-view-index-transactions.sql`.
The twelve-query script is `sql/03_queries.sql`.

## 1. Context
The brief requires the submitted scripts to include at least one view, at least one index
justified against a named query, and a two-table transaction demonstrating COMMIT and
ROLLBACK. One sheet task owns all three. ADR-08 had already claimed index + transaction,
but its column names were stale.

ADR-08 is superseded, not reversed: it named `Appointment(doctor_id, slot_time)` and
`Consultation(diagnosis, consult_date)`. None of those three columns exist. `doctor_id`
predates ADR-14, which split MedicalStaff out of the Patient ISA. ADR-17 fixed the current
name as `med_staff_id`; both `doctor_id` and `staff_id` are retired spellings. `slot_time`
became `appointment_time` and `consult_date` became `consultation_date`. The decision to
index and to transact stands; only the names were wrong.

## 2. Decision

**One index, not two.** `Appointment(med_staff_id, appointment_time)` serves Q02 (clinicians with
free slots this week), the only required query that does a genuine seek — `med_staff_id`
equality plus a time range. ADR-08's second index, `Consultation(diagnosis, …)`, is dropped:
`diagnosis` is free text (`CONTEXT.md`), so a B-tree over it is never sought by equality, and
Q-T3 groups by it rather than filtering on it.

**One view, `v_patient_summary`.** A `UNION ALL` over `Patient JOIN Student` and
`Patient JOIN Staff`, exposing demographics from Patient plus `subtype`, `subtype_code`, and
`faculty`. It reaches the subtypes by union because `faculty` is declared on both subtypes
(BR-02) while `student_code` and `uni_staff_code` exist on only one side each. It joins
MedicalStaff nowhere: `CONTEXT.md` states the two are never joined. The union doubles as the
set operation §2.2 requires. It is Q11 — one of the twelve, not a thirteenth.

**Two transactions, rollback first.** Both span Consultation + Prescription.

1. Insert a Consultation, then fail the Prescription insert on a non-existent
   `consultation_id` (foreign key violation on `fk_presc_consult`), then ROLLBACK.
   Proves the visit did not survive. The Prescription carries no doctor column: the
   prescriber is read through the visit's Consultation, so a wrong-doctor failure is
   inexpressible by construction (ADR-17).
2. Insert a Consultation and its Prescription, COMMIT, then DELETE both rows in reverse
   order so the script reruns clean.

Ordering matters: the rollback demo runs first so the committed rows are the last thing
written and the cleanup is unambiguous. Both use ids in the 9000 block to stay clear of
`02_data.sql`.

**Where the artefacts live.** The view and the two transactions live in
`sql/04_t18-view-index-transactions.sql`. The index lives in `sql/01_schema.sql`, directly
after the Appointment table. This keeps the index with the schema that creates the table,
so a marker who builds the database always gets the index. The earlier choice to keep it in
the demonstration file is reversed below.

## 3. Dropped
- **Second index on `Consultation`.** An index that exists but that `EXPLAIN` ignores is
  worse than none — a marker can spot it in one query. If a second index is wanted later,
  `Consultation(consultation_date)` is the honest one: it serves Q-T3's semester range filter.
- **Prescription + PrescriptionLine as the transaction pair.** A better domain story (it
  exercises the CASCADE), but it demonstrates nothing. Consultation + Prescription lets the
  Prescription insert fail on a constraint and show the Consultation disappearing.
- **Rollback-only demo.** Idempotent, but the brief names COMMIT as well as ROLLBACK and a
  script that only ever rolls back never exercises it.
- **Keeping the index in the demonstration file.** Reversed on 2026-11-21. Ownership was the
  only reason to keep it there. The submission script is now the single source of truth, so
  the index belongs with the DDL.
- **A second index, kept "just in case".** Verified unnecessary — the single index is chosen
  by the query it was built for.
- **Widening the rerun caveat to "the whole database must be recreated".** Overstated. Only
  the view and the index need to be absent; the DML half is idempotent on its own. Verified,
  and the file header now says the narrow thing.

## 4. Consequences
Gain: three brief requirements met by checkable artefacts; the rollback is demonstrated
rather than asserted; the view is reusable as F1's browse grid and shows subtype without
the Java layer knowing about the ISA. Loss: `CREATE VIEW` is DDL inside the demonstration
script, and MySQL has no `IF NOT EXISTS` for it, so a second run needs the view dropped first
— the usual `01_schema.sql` run order satisfies this. The index has no such problem because
it is created with the tables. Report cost: the report must name Q02 as the query the index
helps.

Verified 2026-10-01 against a live server, using the 7 tables these three blocks touch
(`Patient`, `Student`, `Staff`, `MedicalStaff`, `Appointment`, `Consultation`,
`Prescription`) built from the column names in `docs/D1-outline.md`: all 11 statements run,
the only error being the intended foreign key violation (`ERROR 1452`, constraint
`fk_presc_consult`); ROLLBACK does discard `consultation_id 9001` and the commit demo's DELETE
leaves the 9000 block empty; `v_patient_summary` returns one correctly labelled row per
Patient; `EXPLAIN` for a Q-T2-shaped seek picks `IndexRangeScan_8` on
`idx_appt_medstaff_time(med_staff_id, appointment_time)`, so the index is used rather than merely
present; and the file runs twice over a fresh database with no unexpected failure.

Re-verification owed after ADR-17 renamed the columns (`staff_id` → `med_staff_id`,
`staff_code` → `uni_staff_code`, `slot_length_min` → `booking_unit_min`) and moved the
rollback trip-wire from `fk_presc_staff` to `fk_presc_consult`: R5/T18 must rerun the
`00_setup.sql` + `04_t18-view-index-transactions.sql` sequence on MySQL 8.4 and confirm the
single intended `ERROR 1452` (now on `fk_presc_consult`) before D3. What CI can check
(sqlfluff parse, guards) is green on this branch; what it cannot (live execution) is
explicitly deferred, not assumed.

Two limits on that evidence. The server was TiDB v8.5.3 serverless, and ADR-11 makes TiDB
dev-only — MySQL 8.4 remains the submission target, so MySQL-specific syntax is still
unconfirmed. And the tables came from the D1 outline, not from R4's `01_schema.sql`, which is
still a stub; re-check the column names when that lands.

**Case-sensitivity bug, found and fixed 2026-10-01.** The first version of this SQL
lower-cased every identifier (`FROM patient`, `INSERT INTO consultation`) to satisfy
sqlfluff's CP02 default. That passes lint and fails on the submission target: MySQL 8.4
on Linux runs with `lower_case_table_names=0`, where `FROM patient` cannot find a table
created as `Patient`. Verified against a real MySQL 8.4.11 container — 8 of 11 statements
failed with `ERROR 1146 Table 'uhs_t18_scratch.patient' doesn't exist`. Fixed by writing
table names in the schema's exact CamelCase, unquoted. All 11 statements then executed,
the only error being the intended foreign key violation.

`.sqlfluff` now excludes CP02 from its rule allowlist. The schema mixes CamelCase tables
with lower_case columns, and no `capitalisation_policy` accepts both; the convention is
fixed by D1. Other rules (LT01, LT05, RF06, ...) remain active — verified.

## 5. Related
Tables Appointment, Patient, Student, Staff, Consultation, Prescription. Queries Q02, Q03,
Q11. Rules BR-02, BR-07, BR-08, BR-19. Supersedes ADR-08; depends on ADR-01, ADR-11, ADR-14;
column renames and the rollback trip-wire move decided in ADR-17.
