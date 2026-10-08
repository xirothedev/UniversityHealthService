# TASK 2: FUNCTIONAL DEPENDENCIES & CANDIDATE KEYS 

## I. Attribute Classification Methodology

- SA (Source Attributes): Attributes appearing only on the Left-Hand Side HS of FDs, or not appearing in any FD. These attributes *must* be included in every candidate key.

- IA (Intermediate Attributes): Attributes appearing on both Left-Hand Side and Right-Hand Side of FDs.

- TA (Target Attributes): Attributes appearing only on the Right-Hand Side of FDs. These attributes can *never* belong to any candidate key.

## II. Functional Dependencies & Candidate Key Identification

### 1. Building

- Attribute Set: U = {building_id, building_name, building_type}

- Functional Dependencies (FDs):
  - FD₁: building_id → {building_name, building_type}
  - FD₂: building_name → {building_id, building_type}

- Candidate Key Identification:
  - SA = ∅
  - IA = {building_id, building_name}
  - TA = {building_type}

- Candidate Keys: {building_id}, {building_name}

- Normal Form: BCNF

### 2. Patient

- Attribute Set: U = {patient_id, full_name, date_of_birth, gender, phone_number}

- Functional Dependencies (FDs):
  - FD₁: patient_id → {full_name, date_of_birth, gender, phone_number}

- Candidate Key Identification:
  - SA = {patient_id}
  - IA = ∅
  - TA = {full_name, date_of_birth, gender, phone_number}

- Candidate Keys: {patient_id}

- Normal Form: BCNF

### 3. Allergy

- Attribute Set: U = {allergy_id, allergen_name}

- Functional Dependencies (FDs):
  - FD₁: allergy_id → allergen_name
  - FD₂: allergen_name → allergy_id

- Candidate Key Identification:
  - SA = ∅
  - IA = {allergy_id, allergen_name}
  - TA = ∅

- Candidate Keys: {allergy_id}, {allergen_name}

- Normal Form: BCNF

### 4. MedicalStaff

- Attribute Set: U = {med_staff_id, full_name, specialty, phone_number}

- Functional Dependencies (FDs):
  - FD₁: med_staff_id → {full_name, specialty, phone_number}

- Candidate Key Identification:
  - SA = {med_staff_id}
  - IA = ∅
  - TA = {full_name, specialty, phone_number}

- Candidate Keys: {med_staff_id}

- Normal Form: BCNF

### 5. Medicine

- Attribute Set: U = {medicine_id, medicine_name, stock_quantity, unit_price}

- Functional Dependencies (FDs):
  - FD₁: medicine_id → {medicine_name, stock_quantity, unit_price}
  - FD₂: medicine_name → {medicine_id, stock_quantity, unit_price}

- Candidate Key Identification:
  - SA = ∅
  - IA = {medicine_id, medicine_name}
  - TA = {stock_quantity, unit_price}

- Candidate Keys: {medicine_id}, {medicine_name}

- Normal Form: BCNF

### 6. VaccinationCourse

- Attribute Set: U = {course_id, course_name, target_disease, required_doses}

- Functional Dependencies (FDs):
  - FD₁: course_id → {course_name, target_disease, required_doses}
  - FD₂: course_name → {course_id, target_disease, required_doses}

- Candidate Key Identification:
  - SA = ∅
  - IA = {course_id, course_name}
  - TA = {target_disease, required_doses}

- Candidate Keys: {course_id}, {course_name}

- Normal Form: BCNF

### 7. Student

- Attribute Set: U = {patient_id, student_code, faculty, dorm_building_id}

- Functional Dependencies (FDs):
  - FD₁: patient_id → {student_code, faculty, dorm_building_id}
  - FD₂: student_code → {patient_id, faculty, dorm_building_id}

- Candidate Key Identification:
  - SA = ∅
  - IA = {patient_id, student_code}
  - TA = {faculty, dorm_building_id}

- Candidate Keys: {patient_id}, {student_code}

- Normal Form: BCNF

### 8. Staff

- Attribute Set: U = {patient_id, uni_staff_code, faculty, office_room}

- Functional Dependencies (FDs):
  - FD₁: patient_id → {uni_staff_code, faculty, office_room}
  - FD₂: uni_staff_code → {patient_id, faculty, office_room}

- Candidate Key Identification:
  - SA = ∅
  - IA = {patient_id, uni_staff_code}
  - TA = {faculty, office_room}

- Candidate Keys: {patient_id}, {uni_staff_code}

- Normal Form: BCNF

### 9. PatientAllergy

- Attribute Set: U = {patient_id, allergy_id, severity, onset_date, note}

- Functional Dependencies (FDs):
  - FD₁: {patient_id, allergy_id} → {severity, onset_date, note}

- Candidate Key Identification:
  - SA = {patient_id, allergy_id}
  - IA = ∅
  - TA = {severity, onset_date, note}

- Candidate Keys: {patient_id, allergy_id}

- Normal Form: BCNF

### 10. ConsultSlot

- Attribute Set: U = {slot_id, med_staff_id, weekday, start_time, end_time, booking_unit_min}

- Functional Dependencies (FDs):
  - FD₁: slot_id → {med_staff_id, weekday, start_time, end_time, booking_unit_min}

- Candidate Key Identification:
  - SA = {slot_id}
  - IA = ∅
  - TA = {med_staff_id, weekday, start_time, end_time, booking_unit_min}

- Candidate Keys: {slot_id}

- Normal Form: BCNF

### 11. Appointment

- Attribute Set: U = {appointment_id, patient_id, med_staff_id, appointment_time, status}

- Functional Dependencies (FDs):
  - FD₁: appointment_id → {patient_id, med_staff_id, appointment_time, status}

- Candidate Key Identification:
  - SA = {appointment_id}
  - IA = ∅
  - TA = {patient_id, med_staff_id, appointment_time, status}

- Candidate Keys: {appointment_id}

- Normal Form: BCNF

### 12. Consultation

- Attribute Set: U = {consultation_id, patient_id, med_staff_id, appointment_id, consultation_date, symptoms, diagnosis, notes}

- Functional Dependencies (FDs):
  - FD₁: consultation_id → {patient_id, med_staff_id, appointment_id, consultation_date, symptoms, diagnosis, notes}

- Candidate Key Identification:
  - SA = {consultation_id}
  - IA = ∅
  - TA = {patient_id, med_staff_id, appointment_id, consultation_date, symptoms, diagnosis, notes}

- Candidate Keys: {consultation_id}

- Normal Form: BCNF

### 13. VaccinationRecord

- Attribute Set: U = {record_id, patient_id, course_id, dose_number, date_given}

- Functional Dependencies (FDs):
  - FD₁: record_id → {patient_id, course_id, dose_number, date_given}
  - FD₂: {patient_id, course_id, dose_number} → {record_id, date_given}

- Candidate Key Identification:
  - SA = ∅
  - IA = {record_id, patient_id, course_id, dose_number}
  - TA = {date_given}

- Candidate Keys: {record_id}, {patient_id, course_id, dose_number}

- Normal Form: BCNF

### 14. Prescription

- Attribute Set: U = {prescription_id, consultation_id, issue_date}

- Functional Dependencies (FDs):
  - FD₁: prescription_id → {consultation_id, issue_date}
  - FD₂: consultation_id → {prescription_id, issue_date}

- Candidate Key Identification:
  - SA = ∅
  - IA = {prescription_id, consultation_id}
  - TA = {issue_date}

- Candidate Keys: {prescription_id}, {consultation_id}

- Normal Form: BCNF

### 15. NotifiableCase

- Attribute Set: U = {case_id, consultation_id, disease_name, date_reported, building_id}

- Functional Dependencies (FDs):
  - FD₁: case_id → {consultation_id, disease_name, date_reported, building_id}

- Candidate Key Identification:
  - SA = {case_id}
  - IA = ∅
  - TA = {consultation_id, disease_name, date_reported, building_id}

- Candidate Keys: {case_id}

- Normal Form: BCNF

### 16. PrescriptionLine

- Attribute Set: U = {prescription_id, line_no, medicine_id, dosage, frequency, duration_days, quantity, unit_price}

- Functional Dependencies (FDs):
  - FD₁: {prescription_id, line_no} → {medicine_id, dosage, frequency, duration_days, quantity, unit_price}

- Candidate Key Identification:
  - SA = {prescription_id, line_no}
  - IA = ∅
  - TA = {medicine_id, dosage, frequency, duration_days, quantity, unit_price}

- Candidate Keys: {prescription_id, line_no}

- Normal Form: BCNF

### 17. ContactTrace

- Attribute Set: U = {trace_id, source_patient_id, contact_patient_id, contact_date, source_case_id}

- Functional Dependencies (FDs):
  - FD₁: trace_id → {source_patient_id, contact_patient_id, contact_date, source_case_id}

- Candidate Key Identification:
  - SA = {trace_id}
  - IA = ∅
  - TA = {source_patient_id, contact_patient_id, contact_date, source_case_id}

- Candidate Keys: {trace_id}

- Normal Form: BCNF

## III. Summary Table

| Table | Candidate Keys | Normal Form |
|---|---|---|
| Building | {building_id}, {building_name} | BCNF |
| Patient | {patient_id} | BCNF |
| Allergy | {allergy_id}, {allergen_name} | BCNF |
| MedicalStaff | {med_staff_id} | BCNF |
| Medicine | {medicine_id}, {medicine_name} | BCNF |
| VaccinationCourse | {course_id}, {course_name} | BCNF |
| Student | {patient_id}, {student_code} | BCNF |
| Staff | {patient_id}, {uni_staff_code} | BCNF |
| PatientAllergy | {patient_id, allergy_id} | BCNF |
| ConsultSlot | {slot_id} | BCNF |
| Appointment | {appointment_id} | BCNF |
| Consultation | {consultation_id} | BCNF |
| VaccinationRecord | {record_id}, {patient_id, course_id, dose_number} | BCNF |
| Prescription | {prescription_id}, {consultation_id} | BCNF |
| NotifiableCase | {case_id} | BCNF |
| PrescriptionLine | {prescription_id, line_no} | BCNF |
| ContactTrace | {trace_id} | BCNF |

## IV. Justification

- Surrogate PKs (Primary Key): System-generated IDs (patient_id, med_staff_id, etc.) form singleton candidate keys determining all non-key attributes.

- Natural UNIQUE Constraints:
  - NOT NULL UNIQUE columns (building_name, allergen_name, medicine_name, course_name, student_code, uni_staff_code) create alternative natural candidate keys.
  - consultation_id in Prescription is a candidate key due to the 1:1 constraint with Consultation (BR-08).
  - Correction: appointment_id in Consultation is NULLABLE UNIQUE (for walk-in visits), so it acts as a partial constraint, not a candidate key.

- Composite Constraints:
  - PatientAllergy: Composite PK (patient_id, allergy_id) determines M:N attributes.
  - VaccinationRecord: Composite UNIQUE (patient_id, course_id, dose_number) serves as the natural key alongside surrogate record_id.
  - PrescriptionLine: Composite PK (prescription_id, line_no) determines weak-entity details.

- Normal Form Conclusion: Every FD's Left-Hand Side (LHS) across all 17 tables is a Superkey. Thus, all relations satisfy BCNF without needing decomposition.