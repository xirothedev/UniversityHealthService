# Draft Relational Schema & Mapping Rules Specification (Deliverable D2)

- **Role:** R2 - Data Modeller
- **Contract:** Aligned 100% with `sql/01_schema.sql`, `docs/er/UHS-ERD.drawio`, and ADR-17.
- **Convention:** CamelCase table names, lower_case column names, and `MR*` mapping codes.

---

## 1. Relational Schema Specification

*Notation: <u>Primary Key</u>, Foreign Key\*, [UQ] Unique, [NN] Not Null*

### Group 1: Independent Tables & Core Demographics
1. **Building** (<u>building_id</u>, building_name [UQ, NN], building_type [NN])
   - *Constraint:* `CHECK (building_type IN ('hall', 'dorm', 'clinic', 'lab', 'pharmacy', 'admin'))`
2. **Patient** (<u>patient_id</u>, full_name [NN], date_of_birth, gender, phone_number)
3. **Allergy** (<u>allergy_id</u>, allergen_name [UQ, NN])
4. **MedicalStaff** (<u>med_staff_id</u>, full_name [NN], specialty, phone_number)
5. **Medicine** (<u>medicine_id</u>, medicine_name [UQ, NN], stock_quantity [NN], unit_price [NN])
   - *Constraints:* `DEFAULT stock_quantity = 0`, `CHECK (stock_quantity >= 0)`, `CHECK (unit_price >= 0)`
6. **VaccinationCourse** (<u>course_id</u>, course_name [UQ, NN], target_disease, required_doses [NN])
   - *Constraint:* `CHECK (required_doses >= 1)`

### Group 2: Specialization / ISA Hierarchy
7. **Student** (<u>*patient_id*</u>, student_code [UQ, NN], faculty, dorm_building_id\*)
   - *FK patient_id -> Patient(patient_id):* ON DELETE CASCADE ON UPDATE CASCADE
   - *FK dorm_building_id -> Building(building_id):* ON DELETE RESTRICT ON UPDATE RESTRICT (Nullable `0..1` per ADR-17)
8. **Staff** (<u>*patient_id*</u>, uni_staff_code [UQ, NN], faculty, office_room)
   - *FK patient_id -> Patient(patient_id):* ON DELETE CASCADE ON UPDATE CASCADE

### Group 3: Schedules, Encounters & Prescriptions
9. **ConsultSlot** (<u>slot_id</u>, med_staff_id\* [NN], weekday [NN], start_time [NN], end_time [NN], booking_unit_min [NN])
   - *FK med_staff_id -> MedicalStaff(med_staff_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
   - *Constraints:* `CHECK (weekday BETWEEN 1 AND 7)`, `CHECK (end_time > start_time)`, `CHECK (booking_unit_min > 0)`
10. **Appointment** (<u>appointment_id</u>, patient_id\* [NN], med_staff_id\* [NN], appointment_time [NN], status [NN])
    - *FK patient_id -> Patient(patient_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
    - *FK med_staff_id -> MedicalStaff(med_staff_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
    - *Constraints:* `DEFAULT status = 'scheduled'`, `CHECK (status IN ('scheduled', 'completed', 'cancelled', 'no-show'))`
11. **Consultation** (<u>consultation_id</u>, patient_id\* [NN], med_staff_id\* [NN], appointment_id\*, consultation_date [NN], symptoms, diagnosis, notes)
    - *FK patient_id -> Patient(patient_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
    - *FK med_staff_id -> MedicalStaff(med_staff_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
    - *FK appointment_id -> Appointment(appointment_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
    - *Constraint:* `UNIQUE (appointment_id)` (Optional `0..1` FK)
12. **Prescription** (<u>prescription_id</u>, consultation_id\* [UQ, NN], issue_date [NN])
    - *FK consultation_id -> Consultation(consultation_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
13. **PrescriptionLine** (<u>*prescription_id*, line_no</u>, medicine_id\* [NN], dosage, frequency, duration_days, quantity, unit_price [NN])
    - *FK prescription_id -> Prescription(prescription_id):* ON DELETE CASCADE ON UPDATE CASCADE
    - *FK medicine_id -> Medicine(medicine_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
    - *Constraints:* `CHECK (duration_days > 0)`, `CHECK (quantity > 0)`, `unit_price` frozen historical snapshot (BR-20).

### Group 4: Associative, Immunization & Outbreak Tracing
14. **PatientAllergy** (<u>*patient_id*, *allergy_id*</u>, severity [NN], onset_date, note)
    - *FK patient_id -> Patient(patient_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
    - *FK allergy_id -> Allergy(allergy_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
    - *Constraint:* `CHECK (severity IN ('mild', 'moderate', 'severe'))`
15. **VaccinationRecord** (<u>record_id</u>, patient_id\* [NN], course_id\* [NN], dose_number [NN], date_given [NN])
    - *FK patient_id -> Patient(patient_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
    - *FK course_id -> VaccinationCourse(course_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
    - *Constraint:* `UNIQUE (patient_id, course_id, dose_number)`, `CHECK (dose_number >= 1)`
16. **NotifiableCase** (<u>case_id</u>, consultation_id\* [NN], disease_name [NN], date_reported [NN], building_id\* [NN])
    - *FK consultation_id -> Consultation(consultation_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
    - *FK building_id -> Building(building_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
17. **ContactTrace** (<u>trace_id</u>, source_patient_id\* [NN], contact_patient_id\* [NN], contact_date [NN], source_case_id\*)
    - *FK source_patient_id -> Patient(patient_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
    - *FK contact_patient_id -> Patient(patient_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
    - *FK source_case_id -> NotifiableCase(case_id):* ON DELETE RESTRICT ON UPDATE RESTRICT
    - *Constraint:* `CHECK (source_patient_id <> contact_patient_id)`

---

## 2. Mapping Rules Taxonomy & Matrix

| Rule Code | Mapping Technique | Applied Tables |
|---|---|---|
| **MR1** | Regular Entity -> Independent table with simple attributes and PK. | `Building`, `Patient`, `Allergy`, `MedicalStaff`, `Medicine`, `VaccinationCourse` |
| **MR2** | Weak Entity -> Composite PK (Owner PK + Partial Key); Owner FK has ON DELETE CASCADE. | `PrescriptionLine` |
| **MR3** | 1:1 Relationship -> Foreign Key with UNIQUE constraint on child side. | `Prescription` |
| **MR4** | 1:N Relationship -> Foreign Key placed on the N-side. | `ConsultSlot`, `Appointment`, `Consultation`, `VaccinationRecord`, `NotifiableCase` |
| **MR5** | M:N Associative / Recursive Entity -> Junction table with relationship attributes. | `PatientAllergy`, `ContactTrace` |
| **MR8** | Specialization / ISA Hierarchy -> Subtype PK is also FK to Supertype with CASCADE. | `Student`, `Staff` |