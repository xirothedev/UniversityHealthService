# Cover page

VIETNAM NATIONAL UNIVERSITY — HO CHI MINH CITY
INTERNATIONAL UNIVERSITY
**SCHOOL OF COMPUTER SCIENCE AND ENGINEERING**

**PRINCIPLES OF DATABASE MANAGEMENT (IT079IU)**
**FINAL REPORT FOR TOPIC 14 — UNIVERSITY HEALTH SERVICE**
**GROUP 2 — MEMBER LIST**

- Course: IT079IU – Principles of Database Management
- Project topic: Topic 14 — University Health Service
- Instructor: Assoc. Prof. Dr. Nguyen Thi Thuy Loan
- Semester: 1, Academic Year 2026–2027
- Deliverable: D3 — Final report, submitted with D5 (SQL) and D6 (Java application)

| # | Name | Student ID | Phone | Topic |
|---|---|---|---|---|
| 1 | Lê Thành Trung | ITCSIU25055 | 0359186887 | 14 — University Health Service |
| 2 | Nguyễn Trần Quốc Vinh | MAMAIU25041 | 0837991399 | 14 — University Health Service |
| 3 | Phan Nguyên Vũ | ITITIU25047 | 0945719666 | 14 — University Health Service |
| 4 | Nguyễn Hoàng Thiên Tứ | ITDSIU25050 | 0374749542 | 14 — University Health Service |
| 5 | Nguyễn Quốc Duy Lộc | ITITIU25018 | 0906648951 | 14 — University Health Service |
| 6 | Trần Pham Quốc Nam | ITCSIU25037 | 0375314749 | 14 — University Health Service |
| 7 | Lê Đức Phát | ITCSIU25042 | 0767393239 | 14 — University Health Service |
| 8 | Nguyễn Minh Khôi | SESEIU24013 | 0902305920 | 14 — University Health Service |
| 9 | Lê Thảo My | SESEIU23018 | 0835931468 | 14 — University Health Service |
| 10 | Nguyễn Trần Anh Tuấn | SESEIU24030 | 0972235718 | 14 — University Health Service |
| 11 | Nguyễn Trọng Đức | ITITIU25003 | 0775327007 | 14 — University Health Service |
| 12 | Nguyễn Hiền Minh | ITITWE25011 | 0919150220 | 14 — University Health Service |

---

# 1. Introduction

The University Health Service (UHS) keeps the medical records of students and
staff registered with a university clinic. The service records appointments,
consultations, prescriptions, allergies and vaccinations. It also records
notifiable diseases and the contacts traced for each case.

The database has three groups of users. Reception staff create appointments and
look up patient demographics. Clinicians record symptoms, diagnoses and
prescriptions, and review a patient's history before a visit. Public-health
officers record notifiable cases and search for clusters: cases of one disease
reported in one building within fourteen days. Managers ask aggregate questions
such as the five most frequent diagnoses in a semester.

The database supports these decisions:

- Which clinician has a free bookable slot in a given week?
- What happened at a patient's past visits, and which medicines were prescribed?
- Which patients completed a vaccination course?
- Where did the same disease appear twice in one building within fourteen days?
- Which medicines are out of stock?

The project follows the assignment stages in order: requirements, ER model,
relational mapping, relational algebra, SQL creation and data, SQL queries,
functional dependencies and normalization. A Java Swing application sits on top
of the database. The application is a window onto the schema, not a separate
system; every screen reads or writes through SQL that the group wrote.

This report describes the complete design. Supporting artefacts are the ER
diagram (`docs/er/UHS-ERD.drawio`), the schema contract
(`docs/er/UHS-relational-schema.md`), the SQL scripts (`sql/`), the Java source
(`src/`), the README and the architecture decision records (`docs/adr/`).

# 2. Requirements and business rules

The assignment sets eight minimum technical requirements in §2.2. The design
meets them as follows.

| Minimum requirement | Evidence in this project |
|---|---|
| At least eight entity sets and eight relationships | 17 tables and 21 relationships in the ER diagram |
| One weak entity with identifying relationship and partial key | `PrescriptionLine`, partial key `line_no` (ADR-04) |
| One many-to-many relationship with its own attribute | `PatientAllergy` with severity and onset date (ADR-05) |
| One ISA hierarchy, multivalued attribute or ternary relationship | ISA `Patient` → `Student` / `Staff`, disjoint and total (ADR-03) |
| One self-relationship with role names on both edges | `ContactTrace`, roles source and contact (ADR-06) |
| Cardinality and participation on every relationship | Marked on all 21 edges; minima stated in BR-21 and BR-22 |
| Complete FDs, minimal cover and justified candidate keys | `docs/functional-dependencies.md`, summarised in section 5 |
| Every relation in 3NF or BCNF | All 17 relations are in BCNF, section 6 |

The business rules are numbered statements in plain English. BR-01 to BR-18
were submitted in D1. BR-19 and BR-20 were added in D2. BR-21 and BR-22 state
participation minima raised by the D2 ERD review.

## A. Who is who

- **BR-01.** Every Patient is exactly one of Student or Staff, never both and never neither (disjoint + total ISA).
- **BR-02.** A Student has one unique student code, a faculty, and at most one dorm Building (a day student boards nowhere); a Staff has one unique university staff code and one office.
- **BR-03.** MedicalStaff is separate from Patient; the same human may have one row in each with no link between them. Specialty and consulting hours belong to MedicalStaff only.
- **BR-04.** A MedicalStaff member may have weekly ConsultSlots (weekday + start + end + booking unit, where the unit is the bookable increment inside the window rather than the window's length); a slot with no blocking Appointment is free.
- **BR-19.** Patient demographics — full name, date of birth, gender, phone — are recorded once on Patient and inherited by both subtypes; Student and Staff add only their own subtype attributes and never copy demographics.
- **BR-21.** Each MedicalStaff member attends at least one Consultation over time (participation minimum 1..n on `attending`).

## B. Care episode

- **BR-05.** An Appointment books exactly one Patient with exactly one MedicalStaff member at one date-time; status is one of scheduled / completed / cancelled / no-show.
- **BR-06.** At most one Consultation results from one Appointment, and a Consultation comes from at most one Appointment; walk-in Consultations without Appointment and Appointments without Consultation (no-show / cancelled) are both allowed.
- **BR-07.** A Consultation records symptoms, diagnosis (free text, consistent spelling), notes, the attending MedicalStaff member, and optionally its source Appointment.
- **BR-08.** One Consultation issues at most one Prescription; every Prescription belongs to exactly one Consultation.
- **BR-09.** A PrescriptionLine is identified only inside its Prescription by (prescription_id + line_no); deleting a Prescription deletes its lines.
- **BR-10.** Each PrescriptionLine gives exactly one Medicine with its own dose, frequency, duration in days, and quantity; durations may differ between lines of the same order.
- **BR-11.** Medicine has a price and a stock level with stock >= 0; stock is informational only and is not auto-decremented by prescribing (D2 scope).
- **BR-20.** Each PrescriptionLine freezes the Medicine unit price at order time; today's Medicine price never overwrites history.

## C. Allergy

- **BR-12.** A Patient may have many Allergies and an Allergy may affect many Patients; severity (mild / moderate / severe) plus onset date belong to each Patient–Allergy pair, with extra detail in a free-text note.

## D. Prevention

- **BR-13.** A VaccinationCourse defines how many doses are required (required_doses).
- **BR-14.** One VaccinationRecord logs one dose for one Patient in one Course (dose number + date given), unique on (patient + course + dose number); the course is complete when the distinct dose-number count >= required_doses.

## E. Outbreak

- **BR-15.** A NotifiableCase is always derived from exactly one Consultation; external lab reports enter via a walk-in Consultation first.
- **BR-16.** Each NotifiableCase records disease (free text, consistent spelling), report date, and exactly one exposure/report Building; this building — not the patient's dorm — drives the 14-day cluster query.
- **BR-17.** ContactTrace links Patient to Patient with roles source / contact plus contact date and optional source case; a source cannot trace to itself.
- **BR-18.** A Building is a hall, dorm, clinic, lab, pharmacy, or admin location; Students point at one dorm Building for admin, cases point at one Building for outbreak detection.
- **BR-22.** Each Building used as an exposure site has at least one NotifiableCase (participation minimum 1..n on `exposure site`).

BR-21 and BR-22 are honest limits. The schema does not enforce them with a
declarative constraint. Section 10 records them as known limits.

# 3. ER diagram

The diagram is `docs/er/UHS-ERD.drawio`; a rendered copy is
`docs/er/UHS-ERD.png`. It has 17 entity sets and 21 relationships. Cardinality
and participation are marked on every relationship. `docs/er/er-checklist-review.md`
records the checklist review.

## 3.1 Design decisions that are not obvious

**ISA hierarchy.** Patient is either Student or Staff, never both and never
neither. The subtypes are disjoint and total. Demographics live once on Patient.
Each subtype adds only its own attributes: `student_code` and optional dorm for
Student, `uni_staff_code` and office for Staff. Faculty is the one
organisational-unit term for both subtypes. The word "department" is not used.
MedicalStaff is not part of this hierarchy: the same human may have rows in both
Patient and MedicalStaff, and the rows are not linked (ADR-03, ADR-14).

**Weak entity.** A prescription line has no meaning without its prescription.
Its key is (prescription_id, line_no); `line_no` is the partial key. The
identifying relationship cascades delete and update (ADR-04).

**Many-to-many with attributes.** A patient may have many allergies and an
allergy may affect many patients. Severity and onset date describe the pair, so
`PatientAllergy` is an associative entity with a composite key (ADR-05).

**Self-relationship.** `ContactTrace` links a Patient to another Patient with
two role names, source and contact. A CHECK rejects a trace from a patient to
themselves (ADR-06).

**Consulting hours.** `ConsultSlot` is a weekly template, not a booking. Its
`booking_unit_min` is the bookable increment inside the window, not the window
length. A slot is free when no scheduled or completed appointment covers it
(ADR-12).

**Outbreak anchor.** Every notifiable case comes from exactly one Consultation
and points at one Building, the exposure site. The building, not the patient's
dorm, drives the 14-day cluster query. External reports first enter as a
walk-in Consultation (ADR-13).

**Prescription has no doctor column.** The prescriber is read through the
Consultation. A redundant doctor key would let an order and its visit disagree
about who prescribed (ADR-17).

**Optional dorm.** A day student boards nowhere, so
`Student.dorm_building_id` is nullable. The `0..1` edge and the domain win over
the old NOT NULL column.

**Frozen price.** `PrescriptionLine.unit_price` is a snapshot taken at order
time. Today's `Medicine.unit_price` never overwrites history (BR-20).

**Mapping codes.** The legend uses `MR1` to `MR8`, not `R1` to `R8`, to avoid a
collision with the seven D1 role codes `R1` to `R7`.

# 4. Relational schema derived from the diagram

The full column-level contract is `docs/er/UHS-relational-schema.md`. It matches
`sql/01_schema.sql` column for column. Each mapping rule is stated below.

| Rule | Mapping technique | Tables |
|---|---|---|
| MR1 | Regular entity → independent table with its own PK | Building, Patient, Allergy, MedicalStaff, Medicine, VaccinationCourse |
| MR2 | Weak entity → composite PK of owner PK plus partial key; owner FK uses CASCADE | PrescriptionLine |
| MR3 | 1:1 relationship → FK with UNIQUE on the child side | Prescription |
| MR4 | 1:N relationship → FK on the N side | ConsultSlot, Appointment, Consultation, VaccinationRecord, NotifiableCase |
| MR5 | M:N associative or recursive entity → junction table with relationship attributes | PatientAllergy, ContactTrace |
| MR8 | ISA hierarchy → subtype PK is also FK to supertype, with CASCADE | Student, Staff |

`PatientAllergy` carries the M:N attributes severity, onset date and note.
`ContactTrace` carries contact date and optional source case. `VaccinationRecord`
uses a surrogate PK plus UNIQUE on (patient_id, course_id, dose_number), a
deliberate deviation from a plain M:N mapping.

Every foreign key declares both ON DELETE and ON UPDATE. CASCADE is used only
where the child key contains the parent key or the child cannot exist without
its owner. RESTRICT is used elsewhere, so a booking, visit or outbreak record
cannot vanish with its parent. Primary keys, NOT NULL, UNIQUE, CHECK and DEFAULT
are all declared in `sql/01_schema.sql`.

# 5. Functional dependencies, minimal cover and candidate keys

The full derivation uses SA (single-attribute key), IA (inferred attributes) and
TA (truly algorithm) classification and is in
`docs/functional-dependencies.md`. The complete minimal cover is in its section
V. The results are summarised here.

| Relation | Candidate keys |
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
| Consultation | {consultation_id} |
| VaccinationRecord | {record_id}, {patient_id, course_id, dose_number} |
| Prescription | {prescription_id}, {consultation_id} |
| NotifiableCase | {case_id} |
| PrescriptionLine | {prescription_id, line_no} |
| ContactTrace | {trace_id} |

**Justification.** Surrogate keys are system-generated identifiers; each one
determines every non-key attribute. A NOT NULL UNIQUE column creates an
alternative natural key: `building_name`, `allergen_name`, `medicine_name`,
`course_name`, `student_code` and `uni_staff_code`. In Prescription,
`consultation_id` is a candidate key because of the 1:1 rule BR-08. In
Consultation, `appointment_id` is nullable UNIQUE, so it is a partial constraint
and not a candidate key. Composite keys are justified by the pair-level or
weak-entity rules: PatientAllergy, VaccinationRecord and PrescriptionLine.

**Minimal cover, examples.** For Building:
`building_id → building_name`; `building_id → building_type`;
`building_name → building_id`. For PrescriptionLine:
`(prescription_id, line_no) → medicine_id`; `(prescription_id, line_no) → dosage`;
`(prescription_id, line_no) → frequency`; `(prescription_id, line_no) →
duration_days`; `(prescription_id, line_no) → quantity`; `(prescription_id,
line_no) → unit_price`. The full cover for all 17 relations is in the referenced
document.

# 6. Normalization

For every functional dependency in every relation, the left-hand side is a
superkey. Therefore all 17 relations are in BCNF, which is stricter than 3NF.
No relation is left in 3NF only, so there is no dependency that BCNF would make
unenforceable and no stopping point to justify. No decomposition was needed to
reach BCNF.

The mapping from ER constructs to relations already performs the lossless
decompositions. Four cases are proved below.

**Patient and Student.** The shared attribute is `patient_id`, the primary key
of both relations. It determines all attributes in each. The natural join
reconstructs the subtype rows with no extra tuples. The same proof holds for
Patient and Staff.

**Patient and PatientAllergy.** The shared attribute is `patient_id`, primary
key of Patient. In PatientAllergy it is part of the composite key. Each
allergy row matches at most one patient, so the join is lossless.

**Prescription and PrescriptionLine.** The shared attribute `prescription_id`
is the key of Prescription and part of the composite key of PrescriptionLine.
It determines every attribute of Prescription, so the join is lossless. The
CASCADE on the foreign key also keeps referential integrity.

**VaccinationCourse and VaccinationRecord.** The shared attribute `course_id`
is the key of VaccinationCourse and part of the natural key of
VaccinationRecord. It determines the course attributes, so the join is lossless.

The same reasoning applies to Appointment–Patient, Appointment–MedicalStaff,
ConsultSlot–MedicalStaff, PrescriptionLine–Medicine and NotifiableCase–Building.
Two foreign keys are optional: `Consultation.appointment_id` and
`ContactTrace.source_case_id` may be NULL. An inner join would drop those rows,
so the reconstruction must use an outer join when those relationships are
included.

# 7. Database implementation

## 7.1 System and version

The target system is **MySQL 8.4 LTS**, with Connector/J 8.4.0. The pinned stack
also uses JDK 25 LTS, Maven 3.9.x and Swing. These choices are recorded in ADR-01
and ADR-02. Verification runs used MySQL 8.4.11 and 8.4.13. TiDB v8.5.3
serverless was used as a development-only compatible server (ADR-11); TiDB is
not the submission target.

MySQL-specific spellings are commented in the scripts: the collation
`utf8mb4_0900_ai_ci` and `ENGINE = InnoDB` in `01_schema.sql`, the
`ON DUPLICATE KEY UPDATE` modifier in `02_data.sql`, and the two-argument
DATEDIFF and `UNION DISTINCT` in `03_queries.sql`. Every other statement uses
standard SQL.

## 7.2 Scripts and run order

The group submits `sql/00_setup.sql` as the single script required by
assignment §2.3. It runs on an empty server from top to bottom: drop the
database, create it, create the 17 tables in foreign-key order, then insert the
data. It runs twice without manual repair. It is generated from `01_schema.sql`
and `02_data.sql`, which remain the development sources.

| File | Role |
|---|---|
| `00_setup.sql` | Single submission script: drop, create, tables, data |
| `01_schema.sql` | DDL, constraints, and the index; source of the setup script |
| `02_data.sql` | Seed data: 17 tables, at least 15 rows each; source of the setup script |
| `02b_query_demo_supplement.sql` | Optional extra demonstration rows in a reserved id range |
| `03_queries.sql` | The 12 submission queries, each preceded by its English question |
| `04_view-and-transactions-demo.sql` | View and transaction demonstration |
| `05_crud_statements.sql` | One UPDATE per table and two two-table transactions |

## 7.3 Constraints and sample data

Every table has a primary key. Every foreign key has ON DELETE and ON UPDATE.
NOT NULL, UNIQUE, CHECK and DEFAULT are declared in the DDL. The seed holds at
least 15 realistic rows per table. Medicine row 17 has zero stock, so query Q12
returns a result from the base seed. The supplement adds further rows but is not
needed for non-empty results. Constraint tests for a duplicate key, a missing
foreign key and a CHECK violation are part of the verification record.

## 7.4 Transaction and index

`04_view-and-transactions-demo.sql` has two transactions over Consultation and
Prescription. The first inserts a visit, then fails the Prescription insert on
a missing `consultation_id` (ERROR 1452 on `fk_presc_consult`), then rolls back
so the visit disappears. The second inserts both rows and commits, then deletes
them in child-first order. `05_crud_statements.sql` adds UPDATE-based two-table
transactions with the same COMMIT and ROLLBACK pattern.

The index `idx_appt_medstaff_time` is on
`Appointment(med_staff_id, appointment_time)` and is created in `01_schema.sql`.
It serves **query Q02**, the search for clinicians with free appointment slots
in a week: the query seeks `med_staff_id` for equality and then narrows on
`appointment_time`. A Q-T2-shaped seek was observed to use the index in the
verification run (ADR-16).

## 7.5 Verification evidence

- `docs/query-nonemptiness-verification.md` records 12 of 12 queries returning
  rows, first on TiDB v8.5.3 and again on MySQL 8.4.11 after the dataset update.
- `docs/query-development-report.docx` records a native MySQL 8.4.13 run.
- `docs/er/er-checklist-review.md` records the ER checklist and schema alignment.
- ADR-16 records the index and transaction verification. It also records a
  re-verification owed after the ADR-17 column renames; this report does not
  claim that re-verification has been performed.
# 8. Queries

All twelve queries are in `sql/03_queries.sql`. Each is preceded by the

question it answers in English. The question and the full SQL statement are

reproduced below. The row count is the result on the sample data recorded in

`docs/query-nonemptiness-verification.md` (run date 2026-11-04).


## Q01 — What is the consultation history of a selected patient, including the attending clinician and any prescribed medicines? One row per prescription line. LEFT JOIN retains visits without prescriptions or lines. The historical price comes from PrescriptionLine, not the current Medicine price.


```sql
-- What is the consultation history of a selected patient, including the
-- attending clinician and any prescribed medicines?
-- One row per prescription line. LEFT JOIN retains visits without
-- prescriptions or lines. The historical price comes from PrescriptionLine,
-- not the current Medicine price.
SELECT
    c.consultation_id,
    c.consultation_date,
    p.patient_id,
    p.full_name AS patient_name,
    ms.full_name AS clinician_name,
    c.symptoms,
    c.diagnosis,
    c.notes AS consultation_notes,
    pr.prescription_id,
    pr.issue_date,
    pl.line_no,
    m.medicine_name,
    pl.dosage,
    pl.frequency,
    pl.duration_days,
    pl.quantity,
    pl.unit_price AS prescribed_unit_price
FROM Consultation AS c
INNER JOIN Patient AS p
    ON c.patient_id = p.patient_id
INNER JOIN MedicalStaff AS ms
    ON c.med_staff_id = ms.med_staff_id
LEFT JOIN Prescription AS pr
    ON c.consultation_id = pr.consultation_id
LEFT JOIN PrescriptionLine AS pl
    ON pr.prescription_id = pl.prescription_id
LEFT JOIN Medicine AS m
    ON pl.medicine_id = m.medicine_id
WHERE c.patient_id = @patient_id
ORDER BY c.consultation_date DESC, c.consultation_id DESC, pl.line_no ASC;
```


**Result on the sample data:** 2 row(s).


## Q02 — Which clinicians have unbooked appointment intervals remaining in the week containing the reference date? MySQL recursive CTE. weekday 1=Monday through 7=Sunday. Each appointment is assumed to last one matching booking unit because duration is absent from the schema. Half-open intervals [start,end) overlap; touching endpoints do not. MedicalStaff has no doctor/nurse role flag. The result lists clinicians, all labelled Dr. in the seed. Templates are same-day only; recursion requires fewer than 1000 increments per window under the default MySQL limit.


```sql
-- Which clinicians have unbooked appointment intervals remaining in the week
-- containing the reference date?
-- MySQL recursive CTE. weekday 1=Monday through 7=Sunday. Each appointment is
-- assumed to last one matching booking unit because duration is absent from
-- the schema. Half-open intervals [start,end) overlap; touching endpoints do
-- not. MedicalStaff has no doctor/nurse role flag. The result lists
-- clinicians, all labelled Dr. in the seed. Templates are same-day only;
-- recursion requires fewer than 1000 increments per window under the default
-- MySQL limit.
WITH RECURSIVE slot_grid AS (
    SELECT
        cs.slot_id,
        cs.med_staff_id,
        cs.booking_unit_min,
        TIMESTAMP(
            DATE_ADD(@week_start, INTERVAL (cs.weekday - 1) DAY),
            cs.start_time
        ) AS slot_start,
        TIMESTAMP(
            DATE_ADD(@week_start, INTERVAL (cs.weekday - 1) DAY),
            cs.end_time
        ) AS window_end
    FROM ConsultSlot AS cs
    WHERE
        TIME_TO_SEC(cs.end_time) - TIME_TO_SEC(cs.start_time)
        >= cs.booking_unit_min * 60
    UNION ALL
    SELECT
        sg.slot_id,
        sg.med_staff_id,
        sg.booking_unit_min,
        TIMESTAMPADD(MINUTE, sg.booking_unit_min, sg.slot_start) AS slot_start,
        sg.window_end
    FROM slot_grid AS sg
    WHERE
        TIMESTAMPADD(MINUTE, 2 * sg.booking_unit_min, sg.slot_start)
        <= sg.window_end
)

SELECT DISTINCT
    ms.med_staff_id,
    ms.full_name AS clinician_name,
    sg.slot_start,
    TIMESTAMPADD(MINUTE, sg.booking_unit_min, sg.slot_start) AS slot_end
FROM slot_grid AS sg
INNER JOIN MedicalStaff AS ms ON sg.med_staff_id = ms.med_staff_id
LEFT JOIN Appointment AS ap
    ON
        sg.med_staff_id = ap.med_staff_id
        AND ap.status IN ('scheduled', 'completed')
        AND ap.appointment_time
        < TIMESTAMPADD(MINUTE, sg.booking_unit_min, sg.slot_start)
        AND TIMESTAMPADD(MINUTE, sg.booking_unit_min, ap.appointment_time)
        > sg.slot_start
WHERE
    ap.appointment_id IS NULL
    AND sg.slot_start >= @as_of_datetime
ORDER BY sg.slot_start, ms.med_staff_id;
```


**Result on the sample data:** 154 row(s).


## Q03 — What are the five most frequent diagnoses in the selected semester? The default minimum count is 1, so the five most frequent nonblank diagnoses are eligible. A higher value intentionally changes the report to a minimum- frequency report. Ties are broken alphabetically; LIMIT 5 returns at most five rows, not all tied diagnoses.


```sql
-- What are the five most frequent diagnoses in the selected semester?
-- The default minimum count is 1, so the five most frequent nonblank diagnoses
-- are eligible. A higher value intentionally changes the report to a minimum-
-- frequency report. Ties are broken alphabetically; LIMIT 5 returns at most
-- five rows, not all tied diagnoses.
SELECT
    c.diagnosis,
    COUNT(*) AS consultation_count
FROM Consultation AS c
WHERE
    c.consultation_date >= @semester_start
    AND c.consultation_date < @semester_end
    AND c.diagnosis IS NOT NULL
    AND TRIM(c.diagnosis) <> ''
GROUP BY c.diagnosis
HAVING COUNT(*) >= @min_diagnosis_count
ORDER BY consultation_count DESC, c.diagnosis ASC
LIMIT 5;
```


**Result on the sample data:** 5 row(s).


## Q04 — Which patients have completed the selected vaccination course under the group's distinct-dose-count rule? Proposal BR-14 and CONTEXT define completion as distinct recorded dose count >= required_doses; neither label continuity nor intervals are checked. Rank distinct recorded dose labels into positions 1..K. For-all means there is no required position in 1..N without a received position. Thus labels 1, 2, 99 satisfy a three-dose course under this agreed project rule. Seed courses have at most four required doses.


```sql
-- Which patients have completed the selected vaccination course under the
-- group's distinct-dose-count rule?
-- Proposal BR-14 and CONTEXT define completion as distinct recorded dose
-- count >= required_doses; neither label continuity nor intervals are checked.
-- Rank distinct recorded dose labels into positions 1..K. For-all means there
-- is no required position in 1..N without a received position. Thus labels
-- 1, 2, 99 satisfy a three-dose course under this agreed project rule.
-- Seed courses have at most four required doses.
WITH RECURSIVE required_positions AS (
    SELECT
        vc.course_id,
        vc.required_doses,
        1 AS dose_position
    FROM VaccinationCourse AS vc
    WHERE vc.course_id = @course_id
    UNION ALL
    SELECT
        rp.course_id,
        rp.required_doses,
        rp.dose_position + 1 AS dose_position
    FROM required_positions AS rp
    WHERE rp.dose_position < rp.required_doses
),

distinct_received_doses AS (
    SELECT DISTINCT
        vr.patient_id,
        vr.course_id,
        vr.dose_number
    FROM VaccinationRecord AS vr
    WHERE vr.course_id = @course_id
),

received_positions AS (
    SELECT
        drd.patient_id,
        drd.course_id,
        ROW_NUMBER() OVER (
            PARTITION BY drd.patient_id, drd.course_id
            ORDER BY drd.dose_number
        ) AS dose_position
    FROM distinct_received_doses AS drd
)

SELECT
    p.patient_id,
    p.full_name AS patient_name,
    vc.course_id,
    vc.course_name,
    vc.required_doses
FROM Patient AS p
CROSS JOIN VaccinationCourse AS vc
WHERE
    vc.course_id = @course_id
    AND NOT EXISTS (
        SELECT 1
        FROM required_positions AS rp
        WHERE
            rp.course_id = vc.course_id
            AND NOT EXISTS (
                SELECT 1
                FROM received_positions AS rec
                WHERE
                    rec.patient_id = p.patient_id
                    AND rec.course_id = rp.course_id
                    AND rec.dose_position = rp.dose_position
            )
    )
ORDER BY p.patient_id;
```


**Result on the sample data:** 1 row(s).


## Q05 — Which pairs of reports of a selected disease occurred in the same building no more than fourteen days apart? Each unordered pair appears once. The boundary includes elapsed date differences 0 through 14 days, not exactly fourteen distinct calendar dates. Results are candidate report pairs, not proof of transmission or a confirmed outbreak. Multiple reports from one patient remain visible and are labelled by distinct_patient_count. Exposure building comes from NotifiableCase, not Student.dorm_building_id.


```sql
-- Which pairs of reports of a selected disease occurred in the same building
-- no more than fourteen days apart?
-- Each unordered pair appears once. The boundary includes elapsed date
-- differences 0 through 14 days, not exactly fourteen distinct calendar dates.
-- Results are candidate report pairs, not proof of transmission or a confirmed
-- outbreak. Multiple reports from one patient remain visible and are labelled
-- by distinct_patient_count. Exposure building comes from NotifiableCase, not
-- Student.dorm_building_id.
SELECT
    b.building_id,
    b.building_name,
    nc1.disease_name,
    nc1.case_id AS case_a,
    nc2.case_id AS case_b,
    nc1.consultation_id AS consultation_a,
    nc2.consultation_id AS consultation_b,
    c1.patient_id AS patient_a,
    c2.patient_id AS patient_b,
    nc1.date_reported AS reported_a,
    nc2.date_reported AS reported_b,
    ABS(DATEDIFF(nc1.date_reported, nc2.date_reported)) AS days_apart,
    CASE WHEN c1.patient_id = c2.patient_id THEN 1 ELSE 2 END
        AS distinct_patient_count
FROM NotifiableCase AS nc1
INNER JOIN NotifiableCase AS nc2
    ON
        nc1.case_id < nc2.case_id
        AND nc1.building_id = nc2.building_id
        AND nc1.disease_name = nc2.disease_name
INNER JOIN Building AS b ON nc1.building_id = b.building_id
INNER JOIN Consultation AS c1
    ON nc1.consultation_id = c1.consultation_id
INNER JOIN Consultation AS c2
    ON nc2.consultation_id = c2.consultation_id
WHERE
    nc1.disease_name = @disease_name
    AND ABS(DATEDIFF(nc1.date_reported, nc2.date_reported)) <= 14
ORDER BY b.building_id, nc1.case_id, nc2.case_id;
```


**Result on the sample data:** 4 row(s).


## Q06 — Which allergies are recorded for each patient, and what is the severity of each association? The composite key (patient_id,allergy_id) makes each returned association unique. Translated as RA1 in `docs/query-development-report.docx`.


```sql
-- Which allergies are recorded for each patient, and what is the severity of
-- each association?
-- The composite key (patient_id,allergy_id) makes each returned association
-- unique. Translated as RA1 in docs/query-development-report.docx.
SELECT
    p.patient_id,
    p.full_name AS patient_name,
    al.allergy_id,
    al.allergen_name,
    pa.severity
FROM Patient AS p
INNER JOIN PatientAllergy AS pa ON p.patient_id = pa.patient_id
INNER JOIN Allergy AS al ON pa.allergy_id = al.allergy_id
ORDER BY p.patient_id, al.allergy_id;
```


**Result on the sample data:** 16 row(s).


## Q07 — Which medical staff members have never had any appointment recorded? Any recorded appointment counts, including cancelled and no-show. This is not the same as currently having free slots. Translated as RA2 (set difference) in `docs/query-development-report.docx`.


```sql
-- Which medical staff members have never had any appointment recorded?
-- Any recorded appointment counts, including cancelled and no-show. This is
-- not the same as currently having free slots. Translated as RA2 (set
-- difference) in docs/query-development-report.docx.
SELECT
    ms.med_staff_id,
    ms.full_name AS clinician_name,
    ms.specialty
FROM MedicalStaff AS ms
WHERE
    NOT EXISTS (
        SELECT 1
        FROM Appointment AS ap
        WHERE ap.med_staff_id = ms.med_staff_id
    )
ORDER BY ms.med_staff_id;
```


**Result on the sample data:** 1 row(s).


## Q08 — Which patients have at least the selected number of recorded severe allergies? The default threshold is one recorded severe allergy. This operational flag is not a clinical risk model. WHERE filters associations before grouping; HAVING filters patients after counting.


```sql
-- Which patients have at least the selected number of recorded severe
-- allergies?
-- The default threshold is one recorded severe allergy. This operational flag
-- is not a clinical risk model. WHERE filters associations before grouping;
-- HAVING filters patients after counting.
SELECT
    p.patient_id,
    p.full_name AS patient_name,
    COUNT(*) AS severe_allergy_count
FROM Patient AS p
INNER JOIN PatientAllergy AS pa ON p.patient_id = pa.patient_id
WHERE pa.severity = 'severe'
GROUP BY p.patient_id, p.full_name
HAVING COUNT(*) >= @min_severe_allergies
ORDER BY severe_allergy_count DESC, p.patient_id ASC;
```


**Result on the sample data:** 4 row(s).


## Q09 — Which patients have been prescribed a selected medicine at least once? EXISTS returns each patient once even when several prescriptions contain the medicine. Translated as RA3 in `docs/query-development-report.docx`.


```sql
-- Which patients have been prescribed a selected medicine at least once?
-- EXISTS returns each patient once even when several prescriptions contain the
-- medicine. Translated as RA3 (selection, joins, projection) in
-- docs/query-development-report.docx.
SELECT
    p.patient_id,
    p.full_name AS patient_name
FROM Patient AS p
WHERE
    EXISTS (
        SELECT 1
        FROM Consultation AS c
        INNER JOIN Prescription AS pr
            ON c.consultation_id = pr.consultation_id
        INNER JOIN PrescriptionLine AS pl
            ON pr.prescription_id = pl.prescription_id
        WHERE
            c.patient_id = p.patient_id
            AND pl.medicine_id = @medicine_id
    )
ORDER BY p.patient_id;
```


**Result on the sample data:** 2 row(s).


## Q10 — Which patients have at least one consultation record or at least one vaccination record? UNION removes duplicates within and across branches. Both branches project the same columns and compatible types. Translated as RA4 (union) in `docs/query-development-report.docx`.


```sql
-- Which patients have at least one consultation record or at least one
-- vaccination record?
-- UNION removes duplicates within and across branches. Both branches project
-- the same columns and compatible types. Translated as RA4 (union) in
-- docs/query-development-report.docx.
SELECT
    p.patient_id,
    p.full_name AS patient_name
FROM Patient AS p
INNER JOIN Consultation AS c ON p.patient_id = c.patient_id
UNION DISTINCT
SELECT
    p.patient_id,
    p.full_name
FROM Patient AS p
INNER JOIN VaccinationRecord AS vr ON p.patient_id = vr.patient_id
ORDER BY patient_id;
```


**Result on the sample data:** 20 row(s).


## Q11 — What patient demographics and registration subtype details are provided by the shared patient summary view? Shared integration query. The view SELECT definition belongs to Le Thanh Trung / T18 and is reused unchanged. T16 does not claim authorship of the original view. The registered subtypes must remain disjoint and total.


```sql
-- What patient demographics and registration subtype details are provided by
-- the shared patient summary view?
-- Shared integration query. The view SELECT definition belongs to Le Thanh
-- Trung / T18 and is reused unchanged. T16 does not claim authorship of the
-- original view. The registered subtypes must remain disjoint and total.
SELECT
    vps.patient_id,
    vps.full_name,
    vps.date_of_birth,
    vps.phone_number,
    vps.subtype,
    vps.subtype_code,
    vps.faculty
FROM v_patient_summary AS vps
ORDER BY vps.patient_id;
```


**Result on the sample data:** 30 row(s).


## Q12 — Which medicines are currently out of stock? Zero stock means out of stock. The base seed includes one zero-stock row (medicine_id 17), so this query returns a result without the supplement. Do not weaken the predicate to make a result appear.


```sql
-- Which medicines are currently out of stock?
-- Zero stock means out of stock. The base seed includes one zero-stock row
-- (medicine_id 17), so this query returns a result without the supplement.
-- Do not weaken the predicate to make a result appear.
SELECT
    m.medicine_id,
    m.medicine_name,
    m.stock_quantity
FROM Medicine AS m
WHERE m.stock_quantity = 0
ORDER BY m.medicine_id;
```


**Result on the sample data:** 1 row(s).


All twelve queries return a non-empty result. Query Q01 is a multi-table

join with outer joins, Q03 groups with HAVING, Q04 uses double NOT EXISTS

for the for-all pattern, Q10 uses a set operation, and Q11 reads the view.

Eight queries are expressed in relational algebra across two deliverables.

`docs/relational-algebra.md` covers Q01, Q03, Q04 and Q05 with its own RA1–RA4
labels. `docs/query-development-report.docx` covers Q06, Q07, Q09 and Q10 with
its own RA1–RA4 labels. The two label sets are local to their document and are
not interchangeable. Both sets are retained.
# 9. The application

## 9.1 Connection

The Java application is a Swing desktop client in `src/main/java/com/uhs`.
`com.uhs.util.DBConnection` reads settings from outside the code. The lookup
order is: real environment variables, then `.env`, then `db.properties`, then
built-in defaults for host, port and database name. No user name or password is
hard-coded. The marker can point the application at another server by editing
`.env`, with no recompilation. The JDBC driver is MySQL Connector/J 8.4.0.

Every statement that takes a user value uses `PreparedStatement` with bound
parameters. User text is never concatenated into SQL. Table names in the browse
screen come from a whitelist of 17 fixed literals. Connections, statements and
result sets use try-with-resources. Input is validated before a database call:
required fields, date format, year range, text length and date order. Database
errors are translated into plain-language messages by `com.uhs.util.DbErrors`;
no stack trace is shown to the user.

## 9.2 Screens and evidence

Screenshots live in `docs/screenshots/`.

| Screen | Screenshot | SQL behind the screen |
|---|---|---|
| Main menu | `01-Overview.png` | No SQL; opens the forms and queries below |
| F0 connection | `11-Connection.png` | No SQL; reads settings and opens a JDBC connection |
| F1 browse | `02-Browse.png` | `SELECT * FROM <table>`; the table name is whitelisted. The form shows the grid row count |
| F2 search | `05-Consultations-Patient-history.png`, `04-Appointments.png` | Patient history: `SELECT ... FROM Patient p JOIN Consultation c ... JOIN MedicalStaff m ... WHERE p.full_name LIKE ? ESCAPE '\'`. Free slots: `SELECT start_time, end_time, booking_unit_min FROM ConsultSlot WHERE med_staff_id = ? AND weekday = ?` plus `SELECT TIME(appointment_time) FROM Appointment WHERE med_staff_id = ? AND DATE(appointment_time) = ?` |
| F3 master–detail | `05-Consultations-Prescription-lines.png` | Upper grid: `SELECT c.consultation_id, c.consultation_date, p.full_name, m.full_name, c.diagnosis FROM Consultation c JOIN Patient p ... JOIN MedicalStaff m ...`. Lower grid: `SELECT pl.line_no, md.medicine_name, pl.dosage, pl.frequency, pl.duration_days, pl.quantity, pl.unit_price, (pl.quantity * pl.unit_price) AS line_total FROM Prescription pr JOIN PrescriptionLine pl ... JOIN Medicine md ... WHERE pr.consultation_id = ?` |
| F4 editing | `05-Consultation-editor.png`, `05-Consultation-editor-Validation-error.png` | `SELECT` with joins to Patient and MedicalStaff; `INSERT INTO Consultation (patient_id, med_staff_id, appointment_id, consultation_date, symptoms, diagnosis, notes) VALUES (?, ?, ?, ?, ?, ?, ?)`; matching UPDATE and DELETE; a dependency check before DELETE and a confirmation dialog |
| F5 report | `09-Reports-Top-five-diagnoses.png` | `SELECT diagnosis, COUNT(*) AS visits FROM Consultation WHERE diagnosis IS NOT NULL [AND consultation_date >= ?] [AND consultation_date < ?] GROUP BY diagnosis HAVING COUNT(*) >= ? ORDER BY visits DESC, diagnosis ASC LIMIT 5` |
| Q-T1 history | `10-Topic-queries-Consultation-history.png` | Multi-table join with LEFT JOINs to Prescription, PrescriptionLine and Medicine; GROUP_CONCAT of medicine names; `WHERE p.patient_id = ?` |
| Q-T2 free slots | `10-Topic-queries-Free-slots.png` | Recursive CTE over ConsultSlot, LEFT JOIN Appointment with `status IN ('scheduled', 'completed')`, `WHERE a.appointment_id IS NULL` |
| Q-T3 top-5 diagnoses | `09-Reports-Top-five-diagnoses-from-Q-T3.png` | Opens F5; aggregate query shown above |
| Q-T4 full dose | `10-Topic-queries-Full-dose.png` | Recursive dose-number CTE and double NOT EXISTS |
| Q-T5 fourteen-day cluster | `10-Topic-queries-Fourteen-day-cluster.png` | Self-join on NotifiableCase, `ABS(DATEDIFF(...)) <= 14`, grouped by disease and building |

The source of each screen is in `com.uhs.ui` (`MainFrame`, `F0Connection`,
`F1Browse`, `F2Search`, `F3MasterDetail`, `ConsultationForm`, `Top5ReportForm`,
`TopicQueryForm`). The SQL is in `com.uhs.dao` (`UhsDao`, `ConsultationDao`,
`LookupDao`, `ReportDao`, `TopicQueries`). Three JUnit test classes cover URL
building, date parsing and database error messages. The README gives the exact
build and run steps: `mvn -B verify`, then `mvn -q exec:java`.

## 9.3 Mapping to the assignment screens

| Assignment screen | Implementation |
|---|---|
| Connection layer with external settings | F0 and `DBConnection` |
| Browse form with grid and row count | F1 |
| Parameterised search form | F2 |
| Master–detail form | F3, Consultation → PrescriptionLine |
| Editing form with insert, update, delete and delete confirmation | F4 |
| Aggregate report screen | F5, top-5 diagnoses |
| At least four of the five topic queries | All five: Q-T1, Q-T2, Q-T4, Q-T5 as query screens and Q-T3 through F5 |

# 10. Discussion

This section states what the design cannot express and what the group would
change with more time.

**Participation minima not enforced.** BR-21 and BR-22 state that each clinician
attends at least one consultation and each exposure building has at least one
case. The schema does not enforce either rule with a declarative constraint.
A trigger or a transaction-level check would be needed. The rules are documented
instead of being silently assumed.

**Free-text diagnosis and disease.** Diagnosis and disease name are free text.
GROUP BY and equality work only because the sample data uses consistent
spelling. A controlled vocabulary table would remove the risk of two spellings
of the same diagnosis being counted separately.

**One clinician and one prescription per visit.** A consultation records one
attending MedicalStaff member and issues at most one prescription. Real life
allows referrals, teams and several prescriptions per visit. The group kept the
simpler rule because the assignment does not require the richer case.

**Vaccination completion rule.** Completion is a count of distinct dose labels.
The project rule does not check that the labels are continuous or that minimum
intervals between doses were respected. Dose labels 1, 2 and 99 therefore
satisfy a three-dose course. A date-interval rule would be clinically better.

**No clinical deployment features.** There is no access control, no audit log
of record changes, no emergency contact on Patient, and no automatic stock
decrease when a prescription is issued. All are outside the assignment scope.
The application uses synthetic data only.

**Diagram presentation limit.** Cardinality and participation are marked on all
21 relationships, and every column has a business rule. The grey BR strip tags
are not rendered on the diagram file. This is a presentation limit, not a
modelling gap.

**Verification limits.** The original evidence for the index and transaction
was gathered before the ADR-17 column renames and on TiDB, which ADR-11 marks as
development-only. A re-run on MySQL 8.4 after the renames is recorded as owed in
ADR-16 and is not claimed here. The query non-emptiness check was re-run on
MySQL 8.4.11. The final submission target remains MySQL 8.4 LTS.

**Possible changes.** Add controlled lists for diagnosis and disease; enforce
the two participation minima with triggers; add dose-interval checks; allow
several prescriptions per consultation; and add role-based access control before
any real patient data is stored.

# 11. Work allocation

The seven role groups below are the groups agreed in D1. Members of a group
share its responsibilities and outputs. All 12 members took part in the ER
modelling discussion in the first three weeks. Every member must be able to
explain the whole design; a member may not answer a question with "that was
someone else's part".

| Role | Members | W3 output | W5 output | W10 output | W13–15 output |
|---|---|---|---|---|---|
| R1 — Group leader | Nguyễn Hiền Minh | Coordinate proposal, scope, schedule, communication | Review requirements and track D2 deliverables | Integrate D3, D5, D6; resolve schedule risks | Lead rehearsals, demonstration, submission, responses |
| R2 — Data modeller | Nguyễn Quốc Duy Lộc; Nguyễn Trọng Đức | Draft business rules, entities, relationships, ER model | Complete ER diagram, cardinalities, participation, mapping | Refine the model and support schema/query implementation | Validate the model, explain decisions, support demonstration |
| R3 — Normalization lead | Lê Thảo My; Nguyễn Trần Anh Tuấn | Identify FDs, derive candidate keys and minimal cover | Complete 3NF/BCNF justification | Finalize normalization and decomposition evidence | Check proofs, resolve revisions, defend the analysis |
| R4 — Database developer | Trần Pham Quốc Nam; Lê Đức Phát | Plan tables, constraints, sample data, implementation order | Draft DDL, data requirements, integrity checks | Deliver rerunnable scripts, transaction, justified index | Rerun scripts, repair defects, prepare demonstration database |
| R5 — Query developer | Lê Thành Trung; Nguyễn Hoàng Thiên Tứ | Define English questions and expected results | Prototype joins, aggregation, nested queries, filters | Complete 12 non-empty queries, view, four RA versions | Verify results, optimise queries, support demonstrations |
| R6 — Java developer | Nguyễn Trần Quốc Vinh; Phan Nguyên Vũ | Define screens, DAO boundary, validation and error plan | Prototype browse and parameterised search | Implement CRUD, master–detail, reports, topic queries | Integrate, test, repair defects, prepare demonstration |
| R7 — Tester & Writer/Integrator | Nguyễn Minh Khôi | Establish review criteria and check proposal completeness | Verify ER model, schema, rules, progress evidence | Test scripts and application; integrate report and disclosure | Regression checks, backups, documentation and slides |

**Agreement record.** Each member agrees that the table above records their
contribution. Typed names are used here; the submitted PDF carries the wet
signatures.

| # | Member | Agreement (typed name) |
|---|---|---|
| 1 | Lê Thành Trung | Lê Thành Trung |
| 2 | Nguyễn Trần Quốc Vinh | Nguyễn Trần Quốc Vinh |
| 3 | Phan Nguyên Vũ | Phan Nguyên Vũ |
| 4 | Nguyễn Hoàng Thiên Tứ | Nguyễn Hoàng Thiên Tứ |
| 5 | Nguyễn Quốc Duy Lộc | Nguyễn Quốc Duy Lộc |
| 6 | Trần Pham Quốc Nam | Trần Pham Quốc Nam |
| 7 | Lê Đức Phát | Lê Đức Phát |
| 8 | Nguyễn Minh Khôi | Nguyễn Minh Khôi |
| 9 | Lê Thảo My | Lê Thảo My |
| 10 | Nguyễn Trần Anh Tuấn | Nguyễn Trần Anh Tuấn |
| 11 | Nguyễn Trọng Đức | Nguyễn Trọng Đức |
| 12 | Nguyễn Hiền Minh | Nguyễn Hiền Minh |

# 12. References

[1] World Health Organization, *Early Warning Alert and Response (EWAR) in
Emergencies: An Operational Guide*. Geneva: WHO, 2022.
https://iris.who.int/handle/10665/365730 [Accessed: Sep. 24, 2026].

[2] J. J. Reeves et al., "Bringing student health and well-being onto a health
system EHR: The benefits of integration in the COVID-19 era," *Journal of
American College Health*, vol. 70, no. 7, pp. 1968–1974, 2022,
doi: 10.1080/07448481.2020.1843468.

[3] A. Silberschatz, H. F. Korth and S. Sudarshan, *Database System Concepts*,
7th ed. New York: McGraw-Hill, 2020. ISBN 978-0-07-802215-9.

[4] Oracle, *MySQL 8.4 Reference Manual*.
https://dev.mysql.com/doc/refman/8.4/en/ [Accessed: Sep. 24, 2026].

[5] Oracle, *Java Platform, Standard Edition & Java Development Kit Version 25
API Specification*.
https://docs.oracle.com/en/java/javase/25/docs/api/ [Accessed: Sep. 24, 2026].

[6] Apache Software Foundation, "Introduction to the Build Lifecycle," Apache
Maven.
https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html
[Accessed: Sep. 24, 2026].

[7] Course brief, *IT079IU Project Assignment — Topics, Requirements and
Assessment*, Semester 1, 2026–2027. SSOT: `docs/assignment.md`.

## Appendix A — AI-use disclosure

Substantial AI assistance was used to produce this report's Markdown from the
course brief, the submitted proposal and midterm report, the project glossary,
the business rules, the decision records and the SQL and Java files. The group
verified the generated content against the source of truth: every business rule
was checked against `docs/business-rules.md`, every table and column against
`docs/er/UHS-relational-schema.md` and `sql/01_schema.sql`, every query against
`sql/03_queries.sql`, and every screen against the source files and screenshots.
Claims of verification were kept only where a run record exists in the
repository; the owed re-verification in ADR-16 is stated as owed, not as done.
Every member reviewed the report and can explain and defend the whole design
during the presentation. AI was also used to convert the approved Markdown into
the submitted PDF; the converted document was compared with this Markdown and
any changed wording was corrected.
