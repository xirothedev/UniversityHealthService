# D2 — Progress Report: ER Diagram and Draft Schema

**Topic 14 — University Health Service**
**Course:** IT079IU – Principles of Database Management
**Instructor:** Assoc. Prof. Dr. Nguyen Thi Thuy Loan
**Due:** 12/10/2026 (W5)

---

## 1. Cover Page

See `docs/templates/cover.md`. Team table with 12 members (name + student ID +
phone + topic). Group size confirmed by instructor at registration.

---

## 2. Updated Information

Revisions to the D1 proposal (submitted 27/9/2026):

- **BR-19 added** — Patient demographics (full name, date of birth, gender,
  phone) are recorded once on Patient and inherited by both subtypes.
- **BR-20 added** — PrescriptionLine.unit_price is frozen at order time;
  today's Medicine price never overwrites history.
- **Building type CHECK widened** — Added `lab`, `pharmacy`, `admin` to the
  CHECK constraint. The original three values (hall/dorm/clinic) were
  arbitrarily narrow for a university campus.
- **NotifiableCase 1:1 → 1:N** — Dropped `UNIQUE(consultation_id)` on
  NotifiableCase. BR-15 says "exactly one Consultation" per case — that is
  many-to-one, not one-to-one. The UNIQUE over-constrained it.
- **ERD–schema contract aligned** — ADR-17 records the full alignment: ERD
  names win (`med_staff_id`, `uni_staff_code`, `booking_unit_min`), the
  Prescription carries no redundant doctor column, and every FK states both
  referential actions.

---

## 3. Table of Contents

1. Cover Page
2. Updated Information
3. Table of Contents
4. Database Design
   4.1 ER Diagram
   4.2 Draft Schema
   4.3 Mapping Rules
   4.4 Initial Functional Dependencies

---

## 4. Database Design

### 4.1 ER Diagram

**File:** `docs/er/UHS-ERD.drawio` (source), `docs/er/UHS-ERD.png` (rendered).

17 tables, 21 relationships. Crow's-foot notation. MySQL 8.4 types.

**Construct coverage (assignment §2.2):**

| Construct | Where |
|---|---|
| Weak entity + partial key | PrescriptionLine (line_no) |
| M-N with own attribute | PatientAllergy (severity, onset_date) |
| ISA disjoint + total | Patient → Student / Staff |
| Self-rel with 2 roles | ContactTrace (source / contact) |
| Cardinality + participation | All 21 edges marked |

**Design decisions not obvious:**

- **MedicalStaff is separate from Patient** (ADR-14). The same human may have
  one row in each with no link between them. `med_staff_id` always denotes
  MedicalStaff; the Patient subtype is only ever identified by
  `STAFF.uni_staff_code`.
- **Prescription carries no doctor column** (ADR-17). The prescriber is read
  through the visit's Consultation, so an order and its visit can never
  disagree about who prescribed.
- **ConsultSlot is a weekly template** (ADR-12). The booking unit is the
  bookable increment inside the window, not the window's length.
- **NotifiableCase building drives the 14-day cluster** (BR-16). The exposure
  building — not the patient's dorm — is what matters for outbreak detection.

### 4.2 Draft Schema

**File:** `sql/01_schema.sql`

17 tables. Every table has PK, FK with ON DELETE + ON UPDATE, NOT NULL, UNIQUE,
CHECK, and DEFAULT where appropriate. The script runs twice in a row without
manual repair (DROP DATABASE IF EXISTS first).

**Referential action policy (ADR-17):**

- ON DELETE CASCADE where the child PK contains the parent PK (Student/Staff →
  Patient, PrescriptionLine → Prescription).
- ON DELETE RESTRICT everywhere else, so a booking, visit, or outbreak record
  can never vanish with its parent.
- ON UPDATE CASCADE on the same PK-containing FKs, RESTRICT elsewhere.

### 4.3 Mapping rules

| Code | Rule | Tables |
|---|---|---|
| MR1 | Regular entity → table | Building, Patient, Allergy, MedicalStaff, Medicine, VaccinationCourse, ConsultSlot, Appointment |
| MR2 | Weak entity → table with owner PK + partial key | PrescriptionLine |
| MR3 | 1:1 relationship → table with UQ on FK | Prescription, NotifiableCase |
| MR4 | 1:N relationship → FK on the many side | ConsultSlot, Appointment, Consultation |
| MR5 | M-N relationship → associative table | PatientAllergy, VaccinationRecord, ContactTrace |
| MR8 | ISA hierarchy → child PK is also FK to parent | Student, Staff |

### 4.4 Initial Functional Dependencies

**File:** `docs/functional-dependencies.md`

All 17 relations are in **BCNF**. Every FD's LHS is a candidate key, so no
dependency would become unenforceable. No decomposition is needed.

**Candidate keys summary:**

| Table | Candidate keys |
|---|---|
| Building | {building_id}, {building_name} |
| Patient | {patient_id} |
| Allergy | {allergy_id}, {allergen_name} |
| MedicalStaff | {med_staff_id} |
| Medicine | {medicine_id}, {medicine_name} |
| VaccinationCourse | {course_id}, {course_name} |
| Student | {patient_id}, {student_code} |
| Staff | {patient_id}, {uni_staff_code} |
| PatientAllergy | {patient_id, allergy_id} |
| ConsultSlot | {slot_id} |
| Appointment | {appointment_id} |
| Consultation | {consultation_id}, {appointment_id} |
| VaccinationRecord | {record_id}, {patient_id, course_id, dose_number} |
| Prescription | {prescription_id}, {consultation_id} |
| NotifiableCase | {case_id} |
| PrescriptionLine | {prescription_id, line_no} |
| ContactTrace | {trace_id} |

---

## 5. Sample Data

**File:** `sql/02_data.sql`

≥15 rows per table. All 12 queries return non-empty results.

---

## 6. Queries

**File:** `sql/03_queries.sql`

12 queries: multi-join, outer join, GROUP BY/HAVING, nested IN/EXISTS,
NOT EXISTS for-all, set operation, view. Four also in relational algebra
(`docs/relational-algebra.md`).

---

## 7. Indexes and Transactions

- **Index:** `idx_appt_medstaff_time` on Appointment(med_staff_id,
  appointment_time) — serves Q-T2 (free slots).
- **Transactions:** Two demos in `03_queries.sql` — ROLLBACK (failed FK) and
  COMMIT (successful two-table insert).

---

## 8. Verification Results

- **Schema runs twice in a row** on TiDB v8.5.3 (cloud, MySQL-compatible) —
  no errors, no manual repair.
- **All 17 tables have ≥15 rows** (Building 15, Patient 30, Student 15,
  Staff 15, Allergy 15, PatientAllergy 16, MedicalStaff 15, ConsultSlot 15,
  Appointment 15, Consultation 15, Medicine 15, Prescription 15,
  PrescriptionLine 16, VaccinationCourse 15, VaccinationRecord 15,
  NotifiableCase 15, ContactTrace 15).
- **All key queries return non-empty results** (Q-T1, Q-T3, Q-T4, Q-T5
  verified against the cloud database).

## 9. Known Gaps

- Three participation minima with no BR (ER-checklist.md last box unticked).
- Per-element BR tags not on the diagram yet.
- T18 live verification must be rerun on MySQL 8.4 after the renames.
