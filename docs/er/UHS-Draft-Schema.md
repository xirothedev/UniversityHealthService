# Draft Relational Schema — University Health Service (Topic 14)

- **Role:** R2 - Data Modeller
- **Deliverable:** D2 (ER Diagram & Draft Schema)
- **Schema Contract:** Aligned with `docs/er/UHS-ERD.drawio` and ADR-17 (17 relations, 21 foreign keys)

---

## 1. Legend & Notation
- <u>**ColumnName**</u>: Primary Key (PK)
- ***ColumnName***: Primary Key & Foreign Key (PK & FK)
- **ColumnName\***: Foreign Key (FK)
- **[UQ]**: Unique constraint
- **[NN]**: NOT NULL constraint

---

## 2. Relational Schema by Functional Groups

### Group 1: Patient Demographics & Specialization (ISA Hierarchy)

#### 1. `PATIENT` (Supertype)
- **Schema:** `PATIENT` (<u>patient_id</u>, full_name [NN], date_of_birth [NN], gender [NN], phone_number [NN])
- **Constraints:**
  - PK: `patient_id` (INT AUTO_INCREMENT)
  - CHECK: `gender IN ('male', 'female', 'other')`

#### 2. `STUDENT` (Subtype)
- **Schema:** `STUDENT` (<u>*patient_id*</u>, student_code [UQ, NN], faculty [NN], dorm_building_id\*)
- **Foreign Keys & Referential Actions:**
  - FK `patient_id` -> `PATIENT(patient_id)`: **ON DELETE CASCADE, ON UPDATE CASCADE**
  - FK `dorm_building_id` -> `BUILDING(building_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - `dorm_building_id` is nullable (`0..1`) for commuter/day students (ADR-17).

#### 3. `STAFF` (Subtype)
- **Schema:** `STAFF` (<u>*patient_id*</u>, uni_staff_code [UQ, NN], department [NN], office_room [NN])
- **Foreign Keys & Referential Actions:**
  - FK `patient_id` -> `PATIENT(patient_id)`: **ON DELETE CASCADE, ON UPDATE CASCADE**

---

### Group 2: Facilities & Medical Staff

#### 4. `BUILDING`
- **Schema:** `BUILDING` (<u>building_id</u>, building_name [UQ, NN], building_type [NN])
- **Constraints:**
  - PK: `building_id`
  - CHECK: `building_type IN ('dorm', 'lecture', 'clinic', 'lab', 'pharmacy', 'admin')` (Widened per ADR-17)

#### 5. `MEDICAL_STAFF`
- **Schema:** `MEDICAL_STAFF` (<u>med_staff_id</u>, full_name [NN], specialty [NN], phone_number [UQ, NN])
- **Constraints:**
  - PK: `med_staff_id`

#### 6. `CONSULT_SLOT`
- **Schema:** `CONSULT_SLOT` (<u>slot_id</u>, med_staff_id\* [NN], slot_day [NN], start_time [NN], end_time [NN], booking_unit_min [NN], booking_capacity [NN])
- **Foreign Keys & Referential Actions:**
  - FK `med_staff_id` -> `MEDICAL_STAFF(med_staff_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - CHECK: `end_time > start_time`
  - CHECK: `booking_unit_min IN (10, 15, 20, 30)`
  - CHECK: `booking_capacity >= 1`

---

### Group 3: Encounters, Prescriptions & Medicines

#### 7. `APPOINTMENT`
- **Schema:** `APPOINTMENT` (<u>appointment_id</u>, patient_id\* [NN], med_staff_id\* [NN], appointment_time [NN], status [NN])
- **Foreign Keys & Referential Actions:**
  - FK `patient_id` -> `PATIENT(patient_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `med_staff_id` -> `MEDICAL_STAFF(med_staff_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - DEFAULT: `status = 'scheduled'`
  - CHECK: `status IN ('scheduled', 'completed', 'cancelled', 'no_show')`

#### 8. `CONSULTATION`
- **Schema:** `CONSULTATION` (<u>consultation_id</u>, patient_id\* [NN], med_staff_id\* [NN], appointment_id\*, consult_date [NN], symptoms [NN], diagnosis [NN], notes)
- **Foreign Keys & Referential Actions:**
  - FK `patient_id` -> `PATIENT(patient_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `med_staff_id` -> `MEDICAL_STAFF(med_staff_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `appointment_id` -> `APPOINTMENT(appointment_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - UQ: `appointment_id` (an appointment links to at most one completed consultation)

#### 9. `PRESCRIPTION`
- **Schema:** `PRESCRIPTION` (<u>prescription_id</u>, consultation_id\* [UQ, NN], issue_date [NN])
- **Foreign Keys & Referential Actions:**
  - FK `consultation_id` -> `CONSULTATION(consultation_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**

#### 10. `PRESCRIPTION_LINE` (Weak Entity)
- **Schema:** `PRESCRIPTION_LINE` (<u>*prescription_id*, line_no</u>, medicine_name\* [NN], dosage [NN], frequency [NN], quantity [NN], unit_price [NN])
- **Foreign Keys & Referential Actions:**
  - FK `prescription_id` -> `PRESCRIPTION(prescription_id)`: **ON DELETE CASCADE, ON UPDATE CASCADE**
  - FK `medicine_name` -> `MEDICINE(medicine_name)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - Composite PK: (`prescription_id`, `line_no`) with `line_no` as partial key.
  - `unit_price`: Historical price snapshot frozen at order time (BR-20, ADR-17).
  - CHECK: `quantity > 0 AND unit_price >= 0`

#### 11. `MEDICINE`
- **Schema:** `MEDICINE` (<u>medicine_name</u>, stock_quantity [NN], unit_price [NN])
- **Constraints:**
  - PK: `medicine_name` (Natural Key per ERD and ADR-17)
  - DEFAULT: `stock_quantity = 0`
  - CHECK: `stock_quantity >= 0 AND unit_price >= 0`

---

### Group 4: Allergies, Immunization & Communicable Disease Tracking

#### 12. `ALLERGY`
- **Schema:** `ALLERGY` (<u>allergy_id</u>, allergy_name [UQ, NN])
- **Constraints:**
  - PK: `allergy_id`

#### 13. `PATIENT_ALLERGY` (M:N Associative Entity)
- **Schema:** `PATIENT_ALLERGY` (<u>*patient_id*, *allergy_id*</u>, severity [NN], onset_date [NN], note)
- **Foreign Keys & Referential Actions:**
  - FK `patient_id` -> `PATIENT(patient_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `allergy_id` -> `ALLERGY(allergy_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - Composite PK: (`patient_id`, `allergy_id`)
  - CHECK: `severity IN ('mild', 'moderate', 'severe')`

#### 14. `VACCINATION_COURSE`
- **Schema:** `VACCINATION_COURSE` (<u>course_id</u>, course_name [UQ, NN], target_disease [NN], required_doses [NN])
- **Constraints:**
  - PK: `course_id`
  - CHECK: `required_doses >= 1`

#### 15. `VACCINATION_RECORD`
- **Schema:** `VACCINATION_RECORD` (<u>record_id</u>, patient_id\* [NN], course_id\* [NN], dose_number [NN], date_given [NN])
- **Foreign Keys & Referential Actions:**
  - FK `patient_id` -> `PATIENT(patient_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `course_id` -> `VACCINATION_COURSE(course_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - PK: `record_id`
  - UQ: (`patient_id`, `course_id`, `dose_number`)
  - CHECK: `dose_number >= 1`

#### 16. `NOTIFIABLE_CASE`
- **Schema:** `NOTIFIABLE_CASE` (<u>case_id</u>, consultation_id\* [NN], disease_name [NN], date_reported [NN], building_id\* [NN])
- **Foreign Keys & Referential Actions:**
  - FK `consultation_id` -> `CONSULTATION(consultation_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `building_id` -> `BUILDING(building_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT** (Exposure site for 14-day cluster tracking, BR-16)
- **Constraints:**
  - PK: `case_id`

#### 17. `CONTACT_TRACE` (Self-Referencing Relationship)
- **Schema:** `CONTACT_TRACE` (<u>trace_id</u>, source_patient_id\* [NN], contact_patient_id\* [NN], contact_date [NN], source_case_id\* [NN])
- **Foreign Keys & Referential Actions:**
  - FK `source_patient_id` -> `PATIENT(patient_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `contact_patient_id` -> `PATIENT(patient_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `source_case_id` -> `NOTIFIABLE_CASE(case_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - PK: `trace_id`
  - CHECK: `source_patient_id <> contact_patient_id`