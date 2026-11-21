# Query non-emptiness verification — 12 of 12

**Task:** Check that all 12 queries return a non-empty result (D5, owner Lê Thành Trung).
**Result:** **12 of 12 non-empty.**
**Run date:** 2026-11-04 (TiDB); re-run 2026-11-04 on MySQL 8.4.11 after the T15 dataset update.
**Branch:** `t19/query-nonempty-check` (base `main` at `3ccd45d`); re-run after PR #35, which added at least 15 real rows per table.

## Environment

| Item | Value |
|---|---|
| Server | TiDB v8.5.3-serverless (`8.0.11-TiDB-v8.5.3-serverless`) |
| Database | `uhs_db` |
| Driver | MySQL Connector/J 8.4.0 over JDBC |
| Server time at start | `2026-11-04 14:40:57` |
| Run mode | Statement by statement, after a fresh schema load |

The `.env` file connects to the `test` database; every SQL file used here
targets `uhs_db` explicitly (`USE uhs_db`). No credentials are recorded in this
file.

## Run order

1. `sql/01_schema.sql` — drops and recreates `uhs_db`, 17 tables.
2. `sql/02_data.sql` — base sample data, at least 15 rows per table.
3. `sql/02b_query_demo_supplement.sql` — disclosed synthetic rows required by
   three queries.
4. `sql/03_queries.sql` — 12 result sets, Q01–Q12.

`sql/04_view-and-transactions-demo.sql` was not used for this check. It is
T18's separate view, index and transaction demonstration, not the twelve-query
file.

At verification time, `02b_query_demo_supplement.sql` was required:

- Q02 needed its Monday appointments to demonstrate booked and cancelled slots.
- Q05 needed its two Dengue reports exactly fourteen days apart.
- Q12 needed a zero-stock medicine; the base seed had none.

The current base seed now holds a zero-stock row (medicine id 17), so Q12 also
returns a result without the supplement. The supplement remains useful for a
richer demonstration. It adds rows only in the reserved `800001`–`800104` range
and never overwrites an original row. No query predicate was weakened to make a
result appear.

## Results

| Query | Question | Rows |
|---|---|---:|
| Q01 | Consultation history of one patient, including clinician and prescribed medicines | 2 |
| Q02 | Clinicians with unbooked appointment intervals in the reference week | 154 |
| Q03 | Five most frequent diagnoses in the selected semester | 5 |
| Q04 | Patients who completed the selected vaccination course | 1 |
| Q05 | Same disease in the same building within fourteen days | 4 |
| Q06 | Allergies and severity per patient | 16 |
| Q07 | Medical staff never booked | 1 |
| Q08 | Patients with at least one severe allergy | 4 |
| Q09 | Patients prescribed the selected medicine | 2 |
| Q10 | Patients with a consultation or a vaccination record | 20 |
| Q11 | Patient summary view (T18 view, reused unchanged) | 30 |
| Q12 | Medicines currently out of stock | 1 |

Every count above is a row count returned by the query itself on the run date.

**Re-run after PR #35 (T15):** the larger appointment set raises Q02 from 118 to
154. Q05 stays at 4 because both data files use the same `Dengue` spelling.
All other counts are unchanged. The 12/12 non-emptiness result still holds.

## Notes

- Q07 returns Dr. Tien Thi Ngoc (staff 15), the one MedicalStaff row with no
  appointment in either data file.
- Q04 uses course 1 (Hepatitis B, three required doses); patient 1 has all three
  distinct dose numbers.
- Q11 selects from `v_patient_summary`, created by the same script with
  `CREATE OR REPLACE VIEW`. The view definition is T18's and is reused unchanged.
- Results are tied to the fixed demonstration variables at the top of
  `03_queries.sql` (`@as_of_datetime`, semester bounds, selected
  patient, course, medicine, disease, and thresholds).
- This check covers non-emptiness only. T14 is covered by the transactional
  reset in `02_data.sql`; the script runs twice without manual repair.

## Re-run environment

The original record is TiDB v8.5.3 evidence. The re-run above uses MySQL 8.4.11
(Docker) with the same scripts and the transactional reset. The final D5
submission must still use the pinned MySQL 8.4 LTS stack.

Related evidence: `docs/query-development-report.docx` records a native MySQL 8.4.13
run (376 statements, 75 checks).
