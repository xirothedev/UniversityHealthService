# T19 — 12/12 query non-emptiness check

**Task:** T19 — Kiểm tra 12/12 query đều non-empty (D5, owner Lê Thành Trung).
**Result:** **12/12 non-empty.**
**Run date:** 2026-11-04.
**Branch:** `t19/query-nonempty-check` (base `main` at `3ccd45d`).

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
4. `sql/03_queries_completed.sql` — 12 result sets, Q01–Q12.

`sql/03_queries.sql` was not used for this check. It is T18's separate
view/index/transaction demonstration, not the twelve-query file.

`02b_query_demo_supplement.sql` is required, not optional:

- Q02 needs its Monday appointments to demonstrate booked and cancelled slots.
- Q05 needs its two Dengue reports exactly fourteen days apart.
- Q12 needs its one genuinely zero-stock medicine; the base seed has none.

The supplement only adds rows in the reserved `800001`–`800104` range and never
overwrites an original row. No query predicate was weakened to make a result
appear.

## Results

| Query | Question | Rows |
|---|---|---:|
| Q01 | Consultation history of one patient, including clinician and prescribed medicines | 2 |
| Q02 | Clinicians with unbooked appointment intervals in the reference week | 118 |
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

## Notes

- Q07 returns Dr. Tien Thi Ngoc (staff 15), the one MedicalStaff row with no
  appointment in either data file.
- Q04 uses course 1 (Hepatitis B, three required doses); patient 1 has all three
  distinct dose numbers.
- Q11 selects from `v_patient_summary`, created by the same script with
  `CREATE OR REPLACE VIEW`. The view definition is T18's and is reused unchanged.
- Results are tied to the fixed demonstration variables at the top of
  `03_queries_completed.sql` (`@as_of_datetime`, semester bounds, selected
  patient, course, medicine, disease, and thresholds).
- This check covers non-emptiness only. T14's run-twice test and the final
  MySQL 8.4 LTS rerun remain separate tasks.

## Open item

The final D5 submission must still run the same stack on MySQL 8.4 LTS. This
record is TiDB v8.5.3 evidence only.
