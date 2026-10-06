# ER Checklist Review Report (Task T07)

- **Role:** R2 - Data Modeller
- **Task:** T07 — Review ER Diagram against Checklist 2.2 (8 entities, Weak entity, M-N with attributes, ISA, Self-rel)
- **Target Deliverable:** D2 - Midterm Progress Report
- **Diagram References:** `docs/er/UHS-ERD.drawio`, `docs/er/UHS-ERD.png`
- **Architectural Reference:** ADR-17 (D2 ERD–Schema Contract Alignment)
- **Review Date:** October 2026

---

## 1. Executive Summary

This report conducts a thorough, element-by-element verification of the **University Health Service (UHS)** Entity-Relationship Diagram against the coursework assignment requirements (§2.2) and the internal checklist (`docs/ER-checklist.md`). 

The current ER design models **17 entities/tables** and **21 relationships**, strictly adhering to the schema contract established in ADR-17. All structural patterns mandated by the course—including weak entity identification, associative entities with operational attributes, specialization hierarchies, and recursive contact tracing—have been validated.

---

## 2. Core Checklist Evaluation

| Checklist Requirement | Evaluation Status | Diagram Implementation Details | Compliance Evidence |
| :--- | :---: | :--- | :--- |
| **1. $\ge 8$ Entities & $\ge 8$ Relationships** | **PASS** | 17 relations / tables and 21 relationship edges modeled in total. | Far exceeds the minimum threshold ($\ge 8$). Captures complete clinic domain: demographics, slots, appointments, consultations, pharmacy, vaccines, and outbreak tracing. |
| **2. Weak Entity with Identifying Relationship & Partial Key** | **PASS** | `PRESCRIPTION_LINE` identified by owner `PRESCRIPTION`. | - Composite PK: (`prescription_id`, `line_no`).<br>- Identifying owner: `PRESCRIPTION(prescription_id)`.<br>- Partial key (discriminator): `line_no`.<br>- Referential action: `ON DELETE CASCADE, ON UPDATE CASCADE`. |
| **3. M-N Relationship with Own Attributes** | **PASS** | `PATIENT_ALLERGY` linking `PATIENT` and `ALLERGY`. | Carries relationship-specific attributes:<br>- `severity`: `VARCHAR(10)` with `CHECK (severity IN ('mild', 'moderate', 'severe'))`.<br>- `onset_date`: `DATE`.<br>- `note`: `TEXT`. |
| **4. ISA Hierarchy (Supertype / Subtype)** | **PASS** | `PATIENT` specialized into `STUDENT` and `STAFF`. | - Total participation: Every registered patient is either a student or staff.<br>- Disjoint constraint: An individual cannot simultaneously hold both patient subtypes.<br>- Both subtypes use `patient_id` as PK and FK referencing `PATIENT(patient_id)` (`ON DELETE CASCADE`). |
| **5. Self-referencing (Recursive) Relationship** | **PASS** | `CONTACT_TRACE` modeling close contact tracing on `PATIENT`. | - Resolves recursive epidemiological tracking between two patient roles: `source_patient_id` (index case / F0) and `contact_patient_id` (exposed contact / F1).<br>- Enforces reflexive prevention: `CHECK (source_patient_id <> contact_patient_id)`. |
| **6. Complete Cardinality & Participation Notation** | **PASS** | Crow's foot notation applied across all 21 edges. | Explicit notation for min/max cardinalities ($1..1$, $0..1$, $1..N$, $0..N$). Participation constraints reflect operational reality (e.g., commuter students board at $0..1$ dorm building). |
| **7. Explicit Mapping Codes (`MR*`)** | **PASS** | Relational mapping tagged from `MR1` through `MR8`. | Disambiguated from project member roles (`R1`–`R7`) per ADR-17. |
| **8. Historical Price Preservation (BR-20)** | **PASS** | `PRESCRIPTION_LINE.unit_price` captures execution-time price snapshot. | Protects financial audit history from post-hoc updates to `MEDICINE.unit_price`. |

---

## 3. Deep-Dive Structural Breakdown

### 3.1 Weak Entity Verification: `PRESCRIPTION_LINE`
- **Owner Entity:** `PRESCRIPTION` (Identifying relationship: `identifying weak`).
- **Partial Key (Discriminator):** `line_no` (ordinal position within the prescription ticket).
- **Referential Integrity:** Since a line item cannot exist without its prescription ticket, `ON DELETE CASCADE` and `ON UPDATE CASCADE` are strictly enforced.
- **Formulary Link:** Linked via $1:N$ to `MEDICINE(medicine_name)` with `ON DELETE RESTRICT` to prevent catalog deletion of actively dispensed pharmaceuticals.

### 3.2 Associative Entity with Attributes: `PATIENT_ALLERGY`
- **Entities Involved:** `PATIENT` ($M$) to `ALLERGY` ($N$).
- **Resolution:** Associative entity `PATIENT_ALLERGY`.
- **Primary Key:** Composite key `(patient_id, allergy_id)`.
- **Relationship Attributes:**
  - `severity`: Standardized clinical categorization (`mild`, `moderate`, `severe`).
  - `onset_date`: Date when reaction symptoms first manifested.
  - `note`: Clinical descriptions or trigger specifics.

### 3.3 Specialization Hierarchy: `PATIENT` $\rightarrow$ {`STUDENT`, `STAFF`}
- **Supertype:** `PATIENT` (holds shared attributes: `full_name`, `date_of_birth`, `gender`, `phone_number` per BR-19).
- **Subtypes:**
  - `STUDENT`: Captures academic context (`student_code` [UNIQUE], `faculty`, and optional boarding reference `dorm_building_id` referencing `BUILDING`).
  - `STAFF`: Captures university employment context (`uni_staff_code` [UNIQUE], `department`, `office_room`).
- **Architectural Boundary:** Decoupled completely from `MEDICAL_STAFF` (ADR-14 and ADR-17) to prevent role-state explosion.

### 3.4 Self-Referencing Recursive Relationship: `CONTACT_TRACE`
- **Domain Context:** Contact tracing for infectious outbreaks (e.g., COVID-19, Influenza, Tuberculosis).
- **Entity Involved:** `PATIENT` (Dual-role association).
- **Roles:**
  - `source_patient_id`: The infected carrier / index case.
  - `contact_patient_id`: The exposed peer / contact individual.
- **Integrity Invariant:** `CHECK (source_patient_id <> contact_patient_id)` prevents anomalous self-loops.
- **Outbreak Anchor:** Directly links to `NOTIFIABLE_CASE(case_id)` to connect clinical epidemiology with exposure locations.

---

## 4. Resolution of Known Gaps & Alignment Check

In accordance with ADR-17, the following discrepancies were audited and settled:

1. **Identifier Separation:** Standardized `med_staff_id` exclusively for clinical medical staff and `uni_staff_code` for university employee patients.
2. **Dorm Assignment Optionality:** Validated that `STUDENT.dorm_building_id` is nullable ($0..1$), accurately accommodating non-resident commuter students without dummy data fabrication.
3. **NotifiableCase Multiplicity:** Settled as $1:N$ with `CONSULTATION` by removing `UNIQUE(consultation_id)`, fulfilling BR-15 without over-constraining the model.
4. **Historical Price Freezing:** Validated `PRESCRIPTION_LINE.unit_price` against checklist item *"no current price overwriting historical price"*.

---

## 5. Sign-off & Handover Readiness

- **Status:** **PASSED** (Ready for merge and inclusion in Deliverable D2).
- **Consumer Readiness:**
  - **For R3 (Normalization Lead):** Candidate keys and dependency scopes are stabilized across all 17 tables for minimal cover analysis.
  - **For R4 (Database Developer):** Explicit referential actions (`CASCADE` vs. `RESTRICT`) and table constraints are fully specified for `sql/01_schema.sql`.