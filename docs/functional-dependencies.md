# Functional Dependencies — Topic 14 University Health Service (D2 draft)

Source: `sql/01_schema.sql` (17 tables). Every FD is derived from the schema's
PK, FK, UNIQUE, and CHECK constraints. Minimal cover is computed per table.
Candidate keys are derived and justified.

## 1. Building

**FDs:**
- building_id → building_name, building_type
- building_name → building_id, building_type

**Minimal cover:** Already minimal. Both FDs are necessary; neither can be
derived from the other.

**Candidate keys:** {building_id}, {building_name}
- building_id is the PK.
- building_name is UQ, so it determines all other attributes.

**Normal form:** BCNF. Every FD's LHS is a candidate key.

---

## 2. Patient

**FDs:**
- patient_id → full_name, date_of_birth, gender, phone_number

**Minimal cover:** Already minimal.

**Candidate keys:** {patient_id}
- patient_id is the PK. No other attribute is UQ.

**Normal form:** BCNF.

---

## 3. Allergy

**FDs:**
- allergy_id → allergen_name
- allergen_name → allergy_id

**Minimal cover:** Already minimal.

**Candidate keys:** {allergy_id}, {allergen_name}
- allergy_id is the PK.
- allergen_name is UQ.

**Normal form:** BCNF.

---

## 4. MedicalStaff

**FDs:**
- med_staff_id → full_name, specialty, phone_number

**Minimal cover:** Already minimal.

**Candidate keys:** {med_staff_id}
- med_staff_id is the PK. No other attribute is UQ.

**Normal form:** BCNF.

---

## 5. Medicine

**FDs:**
- medicine_id → medicine_name, stock_quantity, unit_price
- medicine_name → medicine_id, stock_quantity, unit_price

**Minimal cover:** Already minimal.

**Candidate keys:** {medicine_id}, {medicine_name}
- medicine_id is the PK.
- medicine_name is UQ.

**Normal form:** BCNF.

---

## 6. VaccinationCourse

**FDs:**
- course_id → course_name, target_disease, required_doses
- course_name → course_id, target_disease, required_doses

**Minimal cover:** Already minimal.

**Candidate keys:** {course_id}, {course_name}
- course_id is the PK.
- course_name is UQ.

**Normal form:** BCNF.

---

## 7. Student

**FDs:**
- patient_id → student_code, faculty, dorm_building_id
- student_code → patient_id, faculty, dorm_building_id

**Minimal cover:** Already minimal.

**Candidate keys:** {patient_id}, {student_code}
- patient_id is the PK (and FK to Patient).
- student_code is UQ.

**Normal form:** BCNF.

---

## 8. Staff

**FDs:**
- patient_id → uni_staff_code, faculty, office_room
- uni_staff_code → patient_id, faculty, office_room

**Minimal cover:** Already minimal.

**Candidate keys:** {patient_id}, {uni_staff_code}
- patient_id is the PK (and FK to Patient).
- uni_staff_code is UQ.

**Normal form:** BCNF.

---

## 9. PatientAllergy

**FDs:**
- (patient_id, allergy_id) → severity, onset_date, note

**Minimal cover:** Already minimal.

**Candidate keys:** {patient_id, allergy_id}
- Composite PK. No single attribute determines the others.

**Normal form:** BCNF.

---

## 10. ConsultSlot

**FDs:**
- slot_id → med_staff_id, weekday, start_time, end_time, booking_unit_min

**Minimal cover:** Already minimal.

**Candidate keys:** {slot_id}
- slot_id is the PK. No other attribute is UQ.

**Normal form:** BCNF.

---

## 11. Appointment

**FDs:**
- appointment_id → patient_id, med_staff_id, appointment_time, status

**Minimal cover:** Already minimal.

**Candidate keys:** {appointment_id}
- appointment_id is the PK. No other attribute is UQ.

**Normal form:** BCNF.

---

## 12. Consultation

**FDs:**
- consultation_id → patient_id, med_staff_id, appointment_id, consultation_date, symptoms, diagnosis, notes
- appointment_id → consultation_id, patient_id, med_staff_id, consultation_date, symptoms, diagnosis, notes

**Minimal cover:** Already minimal.

**Candidate keys:** {consultation_id}, {appointment_id}
- consultation_id is the PK.
- appointment_id is UQ (at most one consultation per appointment).

**Normal form:** BCNF.

---

## 13. VaccinationRecord

**FDs:**
- record_id → patient_id, course_id, dose_number, date_given
- (patient_id, course_id, dose_number) → record_id, date_given

**Minimal cover:** Already minimal.

**Candidate keys:** {record_id}, {patient_id, course_id, dose_number}
- record_id is the PK (surrogate).
- (patient_id, course_id, dose_number) is UQ (the real candidate key).

**Normal form:** BCNF.

---

## 14. Prescription

**FDs:**
- prescription_id → consultation_id, issue_date
- consultation_id → prescription_id, issue_date

**Minimal cover:** Already minimal.

**Candidate keys:** {prescription_id}, {consultation_id}
- prescription_id is the PK.
- consultation_id is UQ (1:1 with Consultation, BR-08).

**Normal form:** BCNF.

---

## 15. NotifiableCase

**FDs:**
- case_id → consultation_id, disease_name, date_reported, building_id

**Minimal cover:** Already minimal.

**Candidate keys:** {case_id}
- case_id is the PK. No other attribute is UQ.

**Normal form:** BCNF.

---

## 16. PrescriptionLine

**FDs:**
- (prescription_id, line_no) → medicine_id, dosage, frequency, duration_days, quantity, unit_price

**Minimal cover:** Already minimal.

**Candidate keys:** {prescription_id, line_no}
- Composite PK (weak entity, ADR-04). No single attribute determines the others.

**Normal form:** BCNF.

---

## 17. ContactTrace

**FDs:**
- trace_id → source_patient_id, contact_patient_id, contact_date, source_case_id

**Minimal cover:** Already minimal.

**Candidate keys:** {trace_id}
- trace_id is the PK. No other attribute is UQ.

**Normal form:** BCNF.

---

## Summary

| Table | Candidate keys | Normal form |
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
| Consultation | {consultation_id}, {appointment_id} | BCNF |
| VaccinationRecord | {record_id}, {patient_id, course_id, dose_number} | BCNF |
| Prescription | {prescription_id}, {consultation_id} | BCNF |
| NotifiableCase | {case_id} | BCNF |
| PrescriptionLine | {prescription_id, line_no} | BCNF |
| ContactTrace | {trace_id} | BCNF |

**All 17 relations are in BCNF.** No decomposition is needed. Every FD's LHS
is a candidate key, so no dependency would become unenforceable.

## Justification

- **Surrogate keys** (patient_id, med_staff_id, etc.) are assigned by the
  system and determine all other attributes in their table.
- **UNIQUE constraints** (building_name, allergen_name, medicine_name,
  course_name, student_code, uni_staff_code, appointment_id on Consultation,
  consultation_id on Prescription) create additional candidate keys.
- **Composite keys** (PatientAllergy, VaccinationRecord, PrescriptionLine)
  are the minimal sets of attributes that determine all other attributes.
- **No transitive dependencies** exist because every non-key attribute
  depends directly on the candidate key, not on another non-key attribute.
- **No partial dependencies** exist because every non-key attribute depends
  on the full candidate key, not on a subset of it.
