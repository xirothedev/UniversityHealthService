# Relational Mapping Rules Specification — University Health Service (Topic 14)

- **Role:** R2 - Data Modeller
- **Deliverable:** D2 (ER Diagram & Draft Schema)
- **Standard Reference:** Elmasri & Navathe (Lecture 3 — ER-to-Relational Mapping Algorithm)
- **Contract Alignment:** Aligned with `docs/er/UHS-ERD.drawio` and ADR-17

---

## 1. Mapping Rule Code Definition (`MR*`)

To prevent naming collisions with project member role codes (`R1`–`R7`), all relational mapping rules adopt the `MR*` taxonomy documented in ADR-17:

| Code | Mapping Type | Description |
|---|---|---|
| **MR1** | Regular Entity Mapping | Map each strong entity to a distinct relation containing all simple attributes. Choose one key as the Primary Key (PK). |
| **MR2** | Weak Entity Mapping | Map each weak entity to a distinct relation. The PK is formed by combining the partial key (discriminator) with the PK of the identifying owner entity. The owner's PK becomes a Foreign Key (FK) with `ON DELETE CASCADE`. |
| **MR3** | 1:1 Binary Relationship Mapping | Foreign Key approach: choose one participating entity relation (preferably with total participation) and embed the PK of the other entity as an FK with a `UNIQUE` constraint. |
| **MR4** | 1:N Binary Relationship Mapping | Foreign Key approach: embed the PK of the relation on the "1" side as an FK in the relation representing the "N" side. |
| **MR5** | M:N Binary / Associative Mapping | Create a new junction/associative relation. The composite PK comprises the FKs referencing the PKs of both participating entities, alongside any relationship-specific attributes. |
| **MR8** | Specialization / Generalization (ISA) | Option 8A (Multiple relations for supertype and subtypes): create a relation for the supertype and separate relations for each subtype. Subtype PKs are identical to and reference the supertype PK as an FK with `ON DELETE CASCADE`. |

---

## 2. Table-by-Table Mapping Documentation

### 2.1 Regular Entities (`MR1`)

#### `PATIENT`
- **Applied Rule:** **MR1**
- **Mapping Logic:** `PATIENT` is a strong entity representing any campus member registered for healthcare services. An auto-increment surrogate key `patient_id` is chosen as the Primary Key. Core demographic attributes (`full_name`, `date_of_birth`, `gender`, `phone_number`) are placed here once and inherited by subtypes per BR-19.

#### `BUILDING`
- **Applied Rule:** **MR1**
- **Mapping Logic:** Strong entity modeling university physical facilities. `building_id` is chosen as the PK. Attributes include `building_name` (`UNIQUE`) and `building_type`, restricted by a `CHECK` constraint widened per ADR-17.

#### `MEDICAL_STAFF`
- **Applied Rule:** **MR1**
- **Mapping Logic:** Independent strong entity representing clinical healthcare practitioners. `med_staff_id` serves as the PK. As settled in ADR-14 and ADR-17, this is deliberately decoupled from `PATIENT` to avoid polymorphic complexity.

#### `MEDICINE`
- **Applied Rule:** **MR1**
- **Mapping Logic:** Strong entity for the pharmaceutical formulary. Per ADR-17 and `UHS-ERD.drawio`, `medicine_name` serves as the natural Primary Key. Default stock is enforced (`DEFAULT 0`).

#### `ALLERGY`
- **Applied Rule:** **MR1**
- **Mapping Logic:** Master catalog entity for known allergen substances. `allergy_id` is the surrogate PK, and `allergy_name` carries a `UNIQUE` constraint.

#### `VACCINATION_COURSE`
- **Applied Rule:** **MR1**
- **Mapping Logic:** Protocol entity defining immunization regimens (e.g., Hepatitis B, Tetanus). `course_id` serves as the PK, and `required_doses` is constrained by a `CHECK (required_doses >= 1)`.

---

### 2.2 Specialization / ISA Hierarchy (`MR8`)

#### `STUDENT` & `STAFF`
- **Applied Rule:** **MR8** (Option 8A — Class hierarchy with subtype relations)
- **Mapping Logic:** The ISA hierarchy `PATIENT -> {STUDENT, STAFF}` is disjoint and total:
  - Every individual in the health service is either a student or a staff member, but not both.
  - Common attributes reside in `PATIENT(patient_id)`.
  - Subtype tables `STUDENT` and `STAFF` do not generate independent surrogate keys; instead, their Primary Key is `patient_id`, which simultaneously acts as an FK referencing `PATIENT(patient_id)`.
  - Referential action is set to **`ON DELETE CASCADE, ON UPDATE CASCADE`**, ensuring that removing a patient record safely removes the specialized profile.
  - In `STUDENT`, `dorm_building_id` is embedded via **MR4** as an optional FK (`0..1`) referencing `BUILDING(building_id)` with `ON DELETE RESTRICT` (commuter students do not board on campus per ADR-17).

---

### 2.3 Weak Entities (`MR2`)

#### `PRESCRIPTION_LINE`
- **Applied Rule:** **MR2** (Weak entity identified by owner `PRESCRIPTION`)
- **Mapping Logic:** 
  - A prescription item line has no independent existence without its parent prescription header (`PRESCRIPTION`).
  - Its partial discriminator key is `line_no` (the ordinal item number within the prescription).
  - The composite PK is defined as `(prescription_id, line_no)`.
  - `prescription_id` is an FK referencing `PRESCRIPTION(prescription_id)` with **`ON DELETE CASCADE, ON UPDATE CASCADE`**.
  - Concurrently, **MR4** is applied to embed `medicine_name` as an FK referencing `MEDICINE(medicine_name)` with `ON DELETE RESTRICT`.
  - `unit_price` captures the frozen historical unit cost at order time, preventing future inventory price changes from corrupting financial audit history (BR-20).

---

### 2.4 One-to-One Relationships (`MR3`)

#### `PRESCRIPTION`
- **Applied Rule:** **MR3** (1:1 Relationship referencing `CONSULTATION`)
- **Mapping Logic:** Per business rule BR-08, each consultation produces at most one prescription header. The relationship is mapped by embedding `consultation_id` into `PRESCRIPTION` with both a `NOT NULL` and a `UNIQUE` constraint, enforcing a strict 1:1 cardinal constraint.
- **Referential Action:** `ON DELETE RESTRICT, ON UPDATE RESTRICT` to preserve medical audit records.

---

### 2.5 One-to-Many Binary Relationships (`MR4`)

#### `CONSULT_SLOT`
- **Applied Rule:** **MR4** (1:N between `MEDICAL_STAFF` and `CONSULT_SLOT`)
- **Mapping Logic:** Each medical staff member defines zero, one, or multiple weekly recurring consultation slots. `med_staff_id` is embedded as an FK on the "N" side (`CONSULT_SLOT`).

#### `APPOINTMENT`
- **Applied Rule:** **MR4** (Dual 1:N from `PATIENT` and `MEDICAL_STAFF`)
- **Mapping Logic:** Represents scheduled bookings. Embeds `patient_id` (the booking requester) and `med_staff_id` (the attending practitioner) as mandatory FKs.

#### `CONSULTATION`
- **Applied Rule:** **MR4** (Multiple 1:N relationships)
- **Mapping Logic:** Represents an actual clinical encounter.
  - Embeds mandatory FK `patient_id` referencing `PATIENT(patient_id)`.
  - Embeds mandatory FK `med_staff_id` referencing `MEDICAL_STAFF(med_staff_id)`.
  - Embeds optional FK `appointment_id` (`0..1`, `UNIQUE`) referencing `APPOINTMENT(appointment_id)` for pre-booked visits (retains NULL for walk-in encounters).

#### `VACCINATION_RECORD`
- **Applied Rule:** **MR4** (Dual 1:N from `PATIENT` and `VACCINATION_COURSE`)
- **Mapping Logic:** Logs vaccine dose administrations. Embeds `patient_id` and `course_id` as FKs. Uniqueness of a specific dose iteration is enforced by a composite table-level constraint `UNIQUE(patient_id, course_id, dose_number)`.

#### `NOTIFIABLE_CASE`
- **Applied Rule:** **MR4** (N:1 with `CONSULTATION` and `BUILDING`)
- **Mapping Logic:** Per ADR-17, the `UNIQUE` constraint on `consultation_id` was dropped because BR-15 states "exactly one Consultation per case" (N:1 semantic). It embeds `consultation_id` and `building_id` (the epidemiological exposure site driving the 14-day cluster window per BR-16).

---

### 2.6 Many-to-Many & Recursive Relationships (`MR5`)

#### `PATIENT_ALLERGY`
- **Applied Rule:** **MR5** (M:N Associative Entity with Attributes)
- **Mapping Logic:** Resolves the M:N relationship between `PATIENT` and `ALLERGY`.
  - The composite PK is `(patient_id, allergy_id)`.
  - Both components serve as FKs referencing `PATIENT` and `ALLERGY` respectively with `ON DELETE RESTRICT`.
  - Stores relationship-specific descriptive attributes: `severity` (`CHECK IN ('mild', 'moderate', 'severe')`), `onset_date`, and `note`.

#### `CONTACT_TRACE`
- **Applied Rule:** **MR5** (Self-Referencing / Recursive M:N on `PATIENT`)
- **Mapping Logic:** Resolves the recursive contact tracing relationship where an infected patient (source / F0) contacts another patient (contact / F1).
  - Uses an independent surrogate PK `trace_id`.
  - Embeds two distinct FKs referencing `PATIENT(patient_id)` representing two distinct roles: `source_patient_id` and `contact_patient_id`.
  - Enforces `CHECK (source_patient_id <> contact_patient_id)` to forbid reflexive self-contact loops.
  - Embeds `source_case_id` referencing `NOTIFIABLE_CASE(case_id)` to link contact tracing back to the verified disease outbreak record.

---

## 3. Summary Mapping Matrix

| Table Name | Primary Mapping Code | Subordinate Mapping Code(s) | Identifying / Referenced Tables |
|---|---|---|---|
| `PATIENT` | **MR1** | — | — |
| `STUDENT` | **MR8** | **MR4** | `PATIENT`, `BUILDING` |
| `STAFF` | **MR8** | — | `PATIENT` |
| `BUILDING` | **MR1** | — | — |
| `MEDICAL_STAFF` | **MR1** | — | — |
| `CONSULT_SLOT` | **MR1** | **MR4** | `MEDICAL_STAFF` |
| `APPOINTMENT` | **MR1** | **MR4** | `PATIENT`, `MEDICAL_STAFF` |
| `CONSULTATION` | **MR1** | **MR4**, **MR3** | `PATIENT`, `MEDICAL_STAFF`, `APPOINTMENT` |
| `PRESCRIPTION` | **MR1** | **MR3** | `CONSULTATION` |
| `PRESCRIPTION_LINE` | **MR2** | **MR4** | `PRESCRIPTION`, `MEDICINE` |
| `MEDICINE` | **MR1** | — | — |
| `ALLERGY` | **MR1** | — | — |
| `PATIENT_ALLERGY` | **MR5** | — | `PATIENT`, `ALLERGY` |
| `VACCINATION_COURSE` | **MR1** | — | — |
| `VACCINATION_RECORD` | **MR1** | **MR4** | `PATIENT`, `VACCINATION_COURSE` |
| `NOTIFIABLE_CASE` | **MR1** | **MR4** | `CONSULTATION`, `BUILDING` |
| `CONTACT_TRACE` | **MR5** | **MR4** | `PATIENT`, `NOTIFIABLE_CASE` |