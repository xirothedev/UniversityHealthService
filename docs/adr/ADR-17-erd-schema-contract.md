# ADR-17: D2 ERD–schema contract alignment

## 1. Context
The D2 deliverable (due 12/10/2026, W5) is the ERD plus a draft schema plus one mapping
rule per table group. The ERD (`docs/er/UHS-ERD.drawio`, 17 tables) calls itself the schema
contract for `sql/01_schema.sql`, but a line-by-line check against `docs/D1-outline.md`,
`CONTEXT.md`, and `sql/04_view-and-transactions-demo.sql` (T18 view + transactions) found
the contract and its consumers disagreeing on column names, one column's meaning, one
column's nullability, and one missing price column — plus a legend that documents only
half the referential actions the brief requires.

## 2. Decision
- **ERD names win.** The diagram uses `med_staff_id` (MedicalStaff PK and every FK to it)
  and `STAFF.uni_staff_code`; `CONTEXT.md`, `docs/D1-outline.md`, ADR-16, and
  `sql/04_view-and-transactions-demo.sql` used `staff_id` / `staff_code`. The diagram is the lone outlier
  numerically but the only D2 artefact the marker reads first, so everything else is
  rewritten to it. `CONTEXT.md` now states the rule as: `med_staff_id` always means
  MedicalStaff; the Patient subtype is identified only by `STAFF.uni_staff_code`.
- **`booking_unit_min`, not `slot_length_min`.** `CONTEXT.md` defines the ConsultSlot unit
  as the bookable increment inside the window, not the window's length (the length is
  already derivable from `end_time > start_time`). The old name reads as a duration, so
  the column is renamed and BR-04 / ADR-12 wording follows it.
- **Dorm is optional.** `STUDENT.dorm_building_id` is nullable (`0..1`): a day student
  boards nowhere. The ERD column said NOT NULL while its own edge said `0..1`; the edge
  and the domain win. BR-02 now reads "at most one dorm Building".
- **BR-20 for the frozen price.** `PRESCRIPTION_LINE.unit_price` is a snapshot taken at
  order time; today's `Medicine.unit_price` never overwrites history (ER checklist:
  "no current price overwriting historical price"). No BR covered it, and
  `business-rules.md` calls columns with no BR defects — so BR-20 is added instead of
  dropping the column.
- **Prescription carries no doctor.** The header holds `consultation_id` + `issue_date`
  only; the prescriber is read through the visit's Consultation. A redundant `staff_id`
  would let an order and its visit disagree about who prescribed, and the T18 rollback
  demo leaned on exactly that redundant FK (`fk_presc_staff`). The demo's trip-wire moves
  to the diagram's own constraint: inserting a Prescription for a non-existent
  `consultation_id` (`fk_presc_consult`).
- **Referential policy for `01_schema.sql`.** Every FK carries explicit `ON DELETE` and
  `ON UPDATE` (brief §2.2). `ON DELETE`: CASCADE where the child PK contains the parent
  PK (Student/Staff → Patient, PrescriptionLine → Prescription) or the line cannot exist
  without its owner; RESTRICT everywhere else so a booking, visit, or outbreak record can
  never vanish with its parent. `ON UPDATE`: CASCADE on the same PK-containing FKs,
  RESTRICT elsewhere — surrogate keys are never updated, so the actions document intent
  rather than fire. Minimal `DEFAULT`s: `Appointment.status = 'scheduled'`,
  `Medicine.stock_quantity = 0`.
- **Mapping codes become `MR*`.** The legend's `R1..R5,R8` collide with the D1 role codes
  `R1..R7` (an "R4" tag on Appointment reads as "Database Developer"). All table tags and
  the legend now read `MR1/MR2/MR3/MR4/MR5/MR8`.

## 3. Dropped
- **Renaming the ERD to `staff_id`.** Would have kept the demonstration script untouched, but the
  glossary rule "`staff_id` means MedicalStaff" next to a `STAFF.staff_code` column is the
  exact ambiguity ADR-14 was written to kill; `med_staff_id` / `uni_staff_code` keeps the
  two identifiers visually distinct.
- **Keeping `slot_length_min` with a clarifying comment.** A misleading name with a
  correct comment still misleads every future `INSERT`; renaming is cheaper than
  re-explaining.
- **Mandatory dorm.** Would force fabricated dorm assignments for every day student in
  the sample data — false data to satisfy a NOT NULL.
- **Living without BR-20.** Dropping `unit_price` would surrender the one checklist line
  ("no current price overwriting historical price") the brief does not even require but
  the checklist does.

## 4. Consequences
Gain: one contract — ERD, `01_schema.sql`, D1 entity list, and the query script name the
same columns; every column has a BR; every FK states both referential actions. Loss: the
T18 live verification (ADR-16) predates the renames and must be rerun on MySQL 8.4 before
D3 — stated in ADR-16, not assumed. Known gaps left for the follow-up PR before 12/10:
three participation minima with no BR (`attending` ≥1 Consultation per MedicalStaff,
`exposure site` ≥1 case per Building, BR-04 "one or more" slots vs the `0..n` edge) and
the missing per-element BR tags on the diagram (the D2 ER checklist's last box stayed unticked).

## 5. Post-review decisions (grill-with-docs, 3/10/2026)
A review of this PR against real-life domain knowledge and the assignment
produced 14 settled decisions. The fixes are applied to the schema and ERD;
the new work is tracked as tasks.

**Applied fixes:**
- **NotifiableCase 1:1 → 1:N.** Dropped `UNIQUE(consultation_id)`; kept NOT
  NULL. BR-15 says "exactly one Consultation" per case — that is many-to-one,
  not one-to-one. The UNIQUE over-constrained it.
- **Building type CHECK widened.** Added `lab`, `pharmacy`, `admin` to the
  CHECK. The original three values were arbitrarily narrow for a university
  campus.

**Accepted simplifications (no change):**
- Prescription 1:1 with Consultation (BR-08). Real life allows multiple
  prescriptions per visit; the assignment does not require it. Keep for D2,
  revisit at D3 if time permits.
- Single doctor per consultation. Referrals and teams are out of scope.
- No emergency contact on Patient. Not an assignment requirement.

**New work (tracked as tasks):**
- Draft FD set + minimal cover + candidate keys before D2 (12/10). R2 leads.
- Fill `02_data.sql` (≥15 rows/table) once the schema freezes. R4 leads.
- Run `01_schema.sql` on MySQL 8.4 and verify it executes twice. R4 leads.
- Write relational algebra for 4 queries (Q-T1, Q-T3, Q-T4, Q-T5). R5 leads.
- R2 drafts the D2 document skeleton (Updated Info, TOC, ERD, schema,
  mapping rules, initial FDs).

**Verification done:**
- Edge count: 21 edges in the ERD, all 21 map to schema FKs. No orphans.
- Column audit: all 17 tables match the schema column-for-column (name,
  type, nullability, constraints). No drift.

## 6. Related
Tables MedicalStaff, ConsultSlot, Appointment, Consultation, Prescription,
PrescriptionLine, Student, Staff. Rules BR-02, BR-03, BR-04, BR-08, BR-20. Amends ADR-12
(wording) and ADR-16 (column names, rollback trip-wire); depends on ADR-03, ADR-04,
ADR-13, ADR-14.
