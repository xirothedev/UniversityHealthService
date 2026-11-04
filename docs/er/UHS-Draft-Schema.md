# Draft Relational Schema — University Health Service (Topic 14)

- **Role:** R2 - Data Modeller
- **Deliverable:** D2 (ER Diagram & Draft Schema)
- **Schema Contract:** Aligned with `sql/01_schema.sql` and `docs/er/UHS-ERD.drawio` (ADR-17: 17 relations, 21 foreign keys)

---

## 1. Legend & Notation

- <u>**ColumnName**</u>: Primary Key (PK)
- ***ColumnName***: Primary Key & Foreign Key (PK & FK)
- **ColumnName\***: Foreign Key (FK)
- **[UQ]**: Unique constraint
- **[NN]**: NOT NULL constraint
- A column without [NN] is nullable.

---

## 2. Relational Schema by Functional Groups

### Group 1: Independent Tables & Core Demographics

#### 1. `Building`
- **Schema:** `Building` (<u>building_id</u>, building_name [UQ, NN], building_type [NN])
- **Constraints:**
  - PK: `building_id` (INT AUTO_INCREMENT)
  - UQ: `building_name`
  - CHECK: `building_type IN ('hall', 'dorm', 'clinic', 'lab', 'pharmacy', 'admin')`

#### 2. `Patient`
- **Schema:** `Patient` (<u>patient_id</u>, full_name [NN], date_of_birth, gender, phone_number)
- **Constraints:**
  - PK: `patient_id` (INT AUTO_INCREMENT)

#### 3. `Allergy`
- **Schema:** `Allergy` (<u>allergy_id</u>, allergen_name [UQ, NN])
- **Constraints:**
  - PK: `allergy_id` (INT AUTO_INCREMENT)
  - UQ: `allergen_name`

#### 4. `MedicalStaff`
- **Schema:** `MedicalStaff` (<u>med_staff_id</u>, full_name [NN], specialty, phone_number)
- **Constraints:**
  - PK: `med_staff_id` (INT AUTO_INCREMENT)

#### 5. `Medicine`
- **Schema:** `Medicine` (<u>medicine_id</u>, medicine_name [UQ, NN], stock_quantity [NN], unit_price [NN])
- **Constraints:**
  - PK: `medicine_id` (INT AUTO_INCREMENT)
  - UQ: `medicine_name`
  - DEFAULT: `stock_quantity = 0`
  - CHECK: `stock_quantity >= 0`
  - CHECK: `unit_price >= 0`

#### 6. `VaccinationCourse`
- **Schema:** `VaccinationCourse` (<u>course_id</u>, course_name [UQ, NN], target_disease, required_doses [NN])
- **Constraints:**
  - PK: `course_id` (INT AUTO_INCREMENT)
  - UQ: `course_name`
  - CHECK: `required_doses >= 1`

---

### Group 2: Specialization (ISA Hierarchy)

#### 7. `Student` (Subtype)
- **Schema:** `Student` (<u>*patient_id*</u>, student_code [UQ, NN], faculty, dorm_building_id\*)
- **Foreign Keys & Referential Actions:**
  - FK `patient_id` -> `Patient(patient_id)`: **ON DELETE CASCADE, ON UPDATE CASCADE**
  - FK `dorm_building_id` -> `Building(building_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - PK: `patient_id`
  - UQ: `student_code`
  - `dorm_building_id` is nullable (`0..1`) for commuter/day students (ADR-17).

#### 8. `Staff` (Subtype)
- **Schema:** `Staff` (<u>*patient_id*</u>, uni_staff_code [UQ, NN], faculty, office_room)
- **Foreign Keys & Referential Actions:**
  - FK `patient_id` -> `Patient(patient_id)`: **ON DELETE CASCADE, ON UPDATE CASCADE**
- **Constraints:**
  - PK: `patient_id`
  - UQ: `uni_staff_code`

---

### Group 3: Schedules, Encounters & Prescriptions

#### 9. `ConsultSlot`
- **Schema:** `ConsultSlot` (<u>slot_id</u>, med_staff_id\* [NN], weekday [NN], start_time [NN], end_time [NN], booking_unit_min [NN])
- **Foreign Keys & Referential Actions:**
  - FK `med_staff_id` -> `MedicalStaff(med_staff_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - PK: `slot_id` (INT AUTO_INCREMENT)
  - CHECK: `weekday BETWEEN 1 AND 7`
  - CHECK: `end_time > start_time`
  - CHECK: `booking_unit_min > 0`

#### 10. `Appointment`
- **Schema:** `Appointment` (<u>appointment_id</u>, patient_id\* [NN], med_staff_id\* [NN], appointment_time [NN], status [NN])
- **Foreign Keys & Referential Actions:**
  - FK `patient_id` -> `Patient(patient_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `med_staff_id` -> `MedicalStaff(med_staff_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - PK: `appointment_id` (INT AUTO_INCREMENT)
  - DEFAULT: `status = 'scheduled'`
  - CHECK: `status IN ('scheduled', 'completed', 'cancelled', 'no-show')`

#### 11. `Consultation`
- **Schema:** `Consultation` (<u>consultation_id</u>, patient_id\* [NN], med_staff_id\* [NN], appointment_id\*, consultation_date [NN], symptoms, diagnosis, notes)
- **Foreign Keys & Referential Actions:**
  - FK `patient_id` -> `Patient(patient_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `med_staff_id` -> `MedicalStaff(med_staff_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `appointment_id` -> `Appointment(appointment_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - PK: `consultation_id` (INT AUTO_INCREMENT)
  - UQ: `appointment_id` (an appointment links to at most one completed consultation)

#### 12. `Prescription`
- **Schema:** `Prescription` (<u>prescription_id</u>, consultation_id\* [UQ, NN], issue_date [NN])
- **Foreign Keys & Referential Actions:**
  - FK `consultation_id` -> `Consultation(consultation_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - PK: `prescription_id` (INT AUTO_INCREMENT)
  - UQ: `consultation_id`

#### 13. `PrescriptionLine` (Weak Entity)
- **Schema:** `PrescriptionLine` (<u>*prescription_id*, line_no</u>, medicine_id\* [NN], dosage, frequency, duration_days, quantity, unit_price [NN])
- **Foreign Keys & Referential Actions:**
  - FK `prescription_id` -> `Prescription(prescription_id)`: **ON DELETE CASCADE, ON UPDATE CASCADE**
  - FK `medicine_id` -> `Medicine(medicine_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - Composite PK: (`prescription_id`, `line_no`) with `line_no` as partial key.
  - `unit_price`: Historical price snapshot frozen at order time (BR-20, ADR-17).
  - CHECK: `duration_days > 0`
  - CHECK: `quantity > 0`

---

### Group 4: Allergies, Immunization & Communicable Disease Tracking

#### 14. `PatientAllergy` (M:N Associative Entity)
- **Schema:** `PatientAllergy` (<u>*patient_id*, *allergy_id*</u>, severity [NN], onset_date, note)
- **Foreign Keys & Referential Actions:**
  - FK `patient_id` -> `Patient(patient_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `allergy_id` -> `Allergy(allergy_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - Composite PK: (`patient_id`, `allergy_id`)
  - CHECK: `severity IN ('mild', 'moderate', 'severe')`

#### 15. `VaccinationRecord`
- **Schema:** `VaccinationRecord` (<u>record_id</u>, patient_id\* [NN], course_id\* [NN], dose_number [NN], date_given [NN])
- **Foreign Keys & Referential Actions:**
  - FK `patient_id` -> `Patient(patient_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `course_id` -> `VaccinationCourse(course_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - PK: `record_id` (INT AUTO_INCREMENT)
  - UQ: (`patient_id`, `course_id`, `dose_number`)
  - CHECK: `dose_number >= 1`

#### 16. `NotifiableCase`
- **Schema:** `NotifiableCase` (<u>case_id</u>, consultation_id\* [NN], disease_name [NN], date_reported [NN], building_id\* [NN])
- **Foreign Keys & Referential Actions:**
  - FK `consultation_id` -> `Consultation(consultation_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `building_id` -> `Building(building_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT** (Exposure site for 14-day cluster tracking, BR-16)
- **Constraints:**
  - PK: `case_id` (INT AUTO_INCREMENT)

#### 17. `ContactTrace` (Self-Referencing Relationship)
- **Schema:** `ContactTrace` (<u>trace_id</u>, source_patient_id\* [NN], contact_patient_id\* [NN], contact_date [NN], source_case_id\*)
- **Foreign Keys & Referential Actions:**
  - FK `source_patient_id` -> `Patient(patient_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `contact_patient_id` -> `Patient(patient_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
  - FK `source_case_id` -> `NotifiableCase(case_id)`: **ON DELETE RESTRICT, ON UPDATE RESTRICT**
- **Constraints:**
  - PK: `trace_id` (INT AUTO_INCREMENT)
  - CHECK: `source_patient_id <> contact_patient_id`

---

## 3. Mapping Summary

| Rule | Technique | Tables |
|---|---|---|
| MR1 | Regular entity -> independent table | `Building`, `Patient`, `Allergy`, `MedicalStaff`, `Medicine`, `VaccinationCourse` |
| MR2 | Weak entity -> composite PK with owner FK CASCADE | `PrescriptionLine` |
| MR3 | 1:1 relationship -> FK with UNIQUE | `Prescription` |
| MR4 | 1:N relationship -> FK on the N side | `ConsultSlot`, `Appointment`, `Consultation`, `VaccinationRecord`, `NotifiableCase` |
| MR5 | M:N associative / recursive entity | `PatientAllergy`, `ContactTrace` |
| MR8 | ISA hierarchy -> subtype PK is FK to supertype | `Student`, `Staff` |

Full rule matrix: `docs/er/UHS-Mapping-Rules.md`. Diagram: `docs/er/UHS-ERD.drawio`.
