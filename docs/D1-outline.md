# D1 outline (= guideline Proposal Report, 1–3 pages, due 27/9/2026 at 22:30) — R1+R2+Writer fill in

Section order is fixed: guideline sections first, then assignment sections.
Both checklists pass only in this order. Source: `docs/deliverables-detail.md`.

1. Cover Page — template `docs/templates/cover.md`. Course + topic 14 + instructor + team table (name + student ID + phone + topic).
2. Abstract — 1 short paragraph: topic and objectives (reliable access to student medical records and early outbreak detection).
3. Task Allocation Table — template `docs/templates/task-allocation.md`. Per-member × per-week specific tasks; generic `All` rows score low / are banned by `CONTRIBUTING.md`.
4. Tools and Programming Languages — pinned stack: JDK 25 LTS + MySQL 8.4 LTS + Connector/J 8.4.x + Maven 3.9.x + Swing + draw.io.

   **Pinned Stack (R2 writes):**

   - **DBMS:** MySQL Server 8.4 LTS (InnoDB, strict declarative constraints: PK, NOT NULL, UNIQUE, CHECK, FK with an explicit `ON DELETE`/`ON UPDATE` action on every key, transactions) (ADR-01). Referential actions are chosen per relationship, not blanket: CASCADE only where ADR-03 (ISA parent → child) and ADR-04 (Prescription → lines) decided it; every other FK restricts, so a booking, visit or outbreak record can never vanish with its parent.
   - **Application:** Java (JDK 25 LTS), Maven 3.9.x (`pom.xml`) (ADR-02).
   - **Driver & Persistence:** JDBC (`com.mysql:mysql-connector-j:8.4.0`).
   - **Connection & Security:** connection settings read outside the code, lookup order real ENV > `.env` > `db.properties` > defaults (ADR-07, ADR-11); parameterized queries via `PreparedStatement` only.
   - **GUI:** Java Swing (6 screens, F0–F5, see `docs/deliverables-detail.md`).
   - **Tools:** draw.io for ER modeling, Git/GitHub for version control.

5. Objective in 1 sentence: reliable access to student medical records and early outbreak detection.
6. Business rules, 15–20 numbered sentences (R2 writes, whole team approves).

   Done: BR-01..BR-18 in `docs/business-rules.md`; copy the approved wording here.

7. Entity list of 17 tables + where the weak / M-M / ISA / self-rel sit.

   Legend: UQ = UNIQUE, NN = NOT NULL, CK = CHECK. FK targets follow `CONTEXT.md` terms.

| Entity / Table Name | Construct Type | Primary Key (PK) | Attributes & Foreign Keys (FK) | Technical Requirement Mapping (Section 2.2 & ADR) |
| :--- | :--- | :--- | :--- | :--- |
| **Patient** | Regular (Superclass) | `patient_id` | `full_name` (NN), `date_of_birth`, `gender`, `phone_number` | **ISA Superclass (ADR-03):** Root entity for all healthcare recipients. Demographics are recorded once here and inherited by both subtypes (BR-19). |
| **Student** | Subclass (ISA) | `patient_id` | `patient_id` (FK), `student_code` (UQ), `faculty`, `dorm_building_id` (FK, nullable — a day student boards nowhere) | **ISA Subclass (ADR-03):** Disjoint + total `(d, total)` with Staff; child PK is also the FK, CASCADE on delete. |
| **Staff** | Subclass (ISA) | `patient_id` | `patient_id` (FK), `uni_staff_code` (UQ), `faculty`, `office_room` | **ISA Subclass (ADR-03):** Disjoint + total `(d, total)` with Student; `faculty` is the one organisational-unit term for Students and Staff alike (BR-02). |
| **MedicalStaff** | Regular Entity | `med_staff_id` | `full_name` (NN), `specialty`, `phone_number` | **Independent Entity (ADR-14):** Kept outside the Patient ISA; `med_staff_id` always denotes this table, never the Staff Patient subtype (BR-03). |
| **ConsultSlot** | Regular Entity | `slot_id` | `med_staff_id` (FK, NN), `weekday` (NN), `start_time` (NN), `end_time` (NN, CK end > start), `booking_unit_min` (NN, CK > 0) | Weekly template hours used to compute free slots (ADR-12, BR-04). The booking unit is the bookable increment inside the window, not the window's length. |
| **Appointment** | Regular Entity | `appointment_id` | `patient_id` (FK, NN), `med_staff_id` (FK, NN), `appointment_time` (NN), `status` (NN, CK in scheduled / completed / cancelled / no-show) | Booking episode; the `status` CHECK is what frees a slot on cancelled / no-show (BR-05, ADR-12). |
| **Consultation** | Regular Entity | `consultation_id` | `patient_id` (FK, NN), `appointment_id` (FK, nullable), `med_staff_id` (FK, NN), `consultation_date` (NN), `symptoms`, `diagnosis`, `notes` | Walk-in visits allowed with no prior Appointment; at most one Appointment per Consultation (BR-06, BR-07). |
| **Prescription** | Regular (Owner) | `prescription_id` | `consultation_id` (FK, NN, UQ), `issue_date` (NN) | UQ on `consultation_id` enforces the 1:1 of BR-08 (one Consultation issues at most one Prescription); the issuing doctor is read through the visit's Consultation and never copied here, so an order and its visit can never disagree (ADR-04). Owner of the lines. |
| **PrescriptionLine** | **Weak Entity** | `(prescription_id, line_no)` | `prescription_id` (FK), `line_no` (Partial Key), `medicine_id` (FK, NN), `dosage`, `frequency`, `duration_days` (CK > 0), `quantity` (CK > 0), `unit_price` (NN, frozen snapshot at order time, BR-20) | **Weak Entity (ADR-04):** Identified only inside Prescription via partial discriminator `line_no`; CASCADE on delete (BR-09). |
| **Medicine** | Regular Entity | `medicine_id` | `medicine_name` (NN, UQ), `stock_quantity` (NN, CK >= 0), `unit_price` (NN, CK >= 0) | Drug catalog and pricing; stock is informational only, never auto-decremented by prescribing (BR-11). |
| **Allergy** | Regular Entity | `allergy_id` | `allergen_name` (NN, UQ) | Regular entity for allergens; carries the allergen name only. Severity belongs to the Patient–Allergy pair, not here (BR-12). |
| **PatientAllergy** | **M:N Associative** | `(patient_id, allergy_id)` | `patient_id` (FK), `allergy_id` (FK), `severity` (NN, CK in mild / moderate / severe), `onset_date`, `note` | **M:N with Attributes (ADR-05):** Severity and onset date live on the pair, not on Patient or Allergy (BR-12). |
| **VaccinationCourse** | Regular Entity | `course_id` | `course_name` (NN, UQ), `target_disease`, `required_doses` (NN, CK >= 1) | Regular entity defining vaccine regimens (BR-13). |
| **VaccinationRecord** | Associative Log | `record_id` (surrogate); UQ `(patient_id, course_id, dose_number)` | `patient_id` (FK, NN), `course_id` (FK, NN), `dose_number` (NN, CK >= 1), `date_given` (NN) | Dose log; the UQ triple is the real candidate key and rejects a double dose (BR-14). Deliberate deviation from the plain M:N mapping rule used for PatientAllergy — justified in D2. |
| **Building** | Regular Entity | `building_id` | `building_name` (NN, UQ), `building_type` (NN, CK in hall / dorm / clinic / lab / pharmacy / admin) | Campus locations; one Building per Student dorm and per NotifiableCase (BR-18). |
| **NotifiableCase** | Regular Entity | `case_id` | `consultation_id` (FK, NN), `disease_name` (NN), `date_reported` (NN), `building_id` (FK, NN) | NN enforces BR-15: every case is derived from exactly one Consultation, so no lab report stands on its own (ADR-13). `building_id` is the exposure building that drives the 14-day cluster, not the student's dorm (BR-16). |
| **ContactTrace** | **Self-Rel Entity** | `trace_id` | `source_patient_id` (FK, NN), `contact_patient_id` (FK, NN), `contact_date` (NN), `source_case_id` (FK, nullable), CK `source_patient_id <> contact_patient_id` | **Self-referencing (ADR-06):** Patient-to-Patient trace with 2 explicit roles (`source` and `contact`); the CHECK rejects a source tracing itself (BR-17). |

   Construct coverage for §2.2: weak entity = PrescriptionLine; M-M with its own attributes = PatientAllergy;
   ISA with disjoint + total = Patient / Student / Staff; self-rel with 2 role names = ContactTrace.
   17 entity sets and ≥ 8 relationships, both above the §2.2 minimum of 8.

8. Plan W3/W5/W10/W13–15 + roles table.

   ### Milestone Plan (confirmed instructor dates, ADR-15)

| Milestone | Timeline / Deadline | Deliverables | Key Responsibilities |
| :--- | :---: | :--- | :--- |
| **D1. Proposal Report** | **27/09/2026, 22:30** (submitted) | 1–3 pages: Cover, Abstract, Task Allocation Table, Pinned Stack, 18 BRs, Entity list, Plan, References & AI disclosure | Lead (R1) + Data Modeller (R2) + Writer |
| **D2. Progress Report** | **W5 (11/10/2026, 22:30)** (instructor announcement 4/10) | 3–5 pages: Updated Info, TOC, full ERD (draw.io) + draft schema + mapping rules + initial FDs | Data Modeller (R2) + Normalization (R3) |
| **D3. Final Report** | **W10 (16/11/2026)** | 25+ pages (exact 12 sections in order), lossless proofs, normalization to 3NF/BCNF | Tester & Writer (R7) assembles; all roles write their sections |
| **D5. SQL Scripts** | **W10 (16/11/2026)** | `01_schema.sql`, `02_data.sql` (≥15 rows/table), `03_queries.sql` (12/12 queries non-empty), transaction + index | DB Developer (R4) + Query Developer (R5) |
| **D6. Java Desktop App** | **W10 (16/11/2026)** | Java Swing project (`src/`), Maven, `db.properties`, 6 screens (F0–F5), `README.md` build/run instructions | Java Developer (R6) + Tester (R7) |
| **D4. Demo & Presentation** | **W13–15 (01/12 – 21/12/2026)** | 15-minute live demo (CRUD + DB reflection) + individual Q&A defense | All team members |

   ### Role Allocation Matrix (R1–R7)

| Role Code | Role Title | Slots | Primary Responsibility | Assigned Members |
| :---: | :--- | :---: | :--- | :--- |
| **R1** | Group Leader | 1 | Schedule, deadlines, submissions, work-allocation sheet | Nguyễn Hiền Minh |
| **R2** | Data Modeller | 2 | Business rules, ER diagram (draw.io), mapping schema | Nguyễn Quốc Duy Lộc, Nguyễn Trọng Đức |
| **R3** | Normalization Lead | 2 | Functional dependencies, minimal cover, 3NF/BCNF proofs | Lê Thảo My, Nguyễn Trần Anh Tuấn |
| **R4** | Database Developer | 2 | `01_schema.sql` (DDL + constraints), `02_data.sql` (≥15 rows) | Lê Đức Phát, Trần Pham Quốc Nam |
| **R5** | Query Developer | 2 | 12 SQL queries + 4 relational algebra, views, transactions | Nguyễn Hoàng Thiên Tứ, Lê Thành Trung |
| **R6** | Java Developer | 2 | JDBC layer, Swing UI (F0–F5), input validation, properties | Nguyễn Trần Quốc Vinh, Phan Nguyên Vũ |
| **R7** | Tester / Writer | 1 | End-to-end testing, report integration, AI disclosure appendix | Nguyễn Minh Khôi |

   Section 3's Task Allocation Table shipped with D1 as a 7-row R1–R7 role matrix: each row
   names its confirmed members and each week-window cell states that role's specific outputs
   (template `docs/templates/task-allocation.md`). This matrix is the role-to-member mapping
   that table is built from. At D2, section 3 is revised to break each group out per member.

9. References + AI disclosure (what was used + how it was verified, per assignment §2.6).

Note: D1 was submitted on time. The dated instructor announcement (27/9/2026, 22:30,
one PDF via Blackboard) superseded the earlier guideline W2 and assignment W3 planning
dates; it is recorded in ADR-15 and `docs/deliverables-detail.md`.

Once D1 is done the topic is locked; no changes after W5 without instructor approval.
