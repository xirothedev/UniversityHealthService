1\. Functional Dependencies (FD Full).

Relation

Functional Dependencies

**`Building`**

`building_id → building_name, building_type`; `building_name → building_id`

**`Patient`**

`patient_id → full_name, date_of_birth, gender, phone_number`

**`Allergy`**

`allergy_id → allergen_name`; `allergen_name → allergy_id`

**`MedicalStaff`**

`med_staff_id → full_name, specialty, phone_number`

**`Medicine`**

`medicine_id → medicine_name, stock_quantity, unit_price`; `medicine_name → medicine_id`

**`VaccinationCourse`**

`course_id → course_name, target_disease, required_doses`; `course_name → course_id`

**`Student`**

`patient_id → student_code, faculty, dorm_building_id`; `student_code → patient_id`

**`Staff`**

`patient_id → uni_staff_code, faculty, office_room`; `uni_staff_code → patient_id`

**`PatientAllergy`**

`(patient_id, allergy_id) → severity, onset_date, note`

**`ConsultSlot`**

`slot_id → med_staff_id, weekday, start_time, end_time, booking_unit_min`

**`Appointment`**

`appointment_id → patient_id, med_staff_id, appointment_time, status`

**`Consultation`**

`consultation_id → patient_id, med_staff_id, appointment_id, consultation_date, symptoms, diagnosis, notes`

**`VaccinationRecord`**

`record_id → patient_id, course_id, dose_number, date_given`; `(patient_id, course_id, dose_number) → record_id, date_given`

**`Prescription`**

`prescription_id → consultation_id, issue_date`; `consultation_id → prescription_id, issue_date`

**`NotifiableCase`**

`case_id → consultation_id, disease_name, date_reported, building_id`

**`PrescriptionLine`**

`(prescription_id, line_no) → medicine_id, dosage, frequency, duration_days, quantity, unit_price`

**`ContactTrace`**

`trace_id → source_patient_id, contact_patient_id, contact_date, source_case_id`

