3/ Candidate Keys

Relation

Candidate Keys

**`Building`**

`{building_id}`, `{building_name}`

**`Patient`**

`{patient_id}`

**`Allergy`**

`{allergy_id}`, `{allergen_name}`

**`MedicalStaff`**

`{med_staff_id}`

**`Medicine`**

`{medicine_id}`, `{medicine_name}`

**`VaccinationCourse`**

`{course_id}`, `{course_name}`

**`Student`**

`{patient_id}`, `{student_code}`

**`Staff`**

`{patient_id}`, `{uni_staff_code}`

**`PatientAllergy`**

`{patient_id, allergy_id}`

**`ConsultSlot`**

`{slot_id}`

**`Appointment`**

`{appointment_id}`

**`Consultation`**

`{consultation_id}`

**`VaccinationRecord`**

`{record_id}`, `{patient_id, course_id, dose_number}`

**`Prescription`**

`{prescription_id}`, `{consultation_id}`

**`NotifiableCase`**

`{case_id}`

**`PrescriptionLine`**

`{prescription_id, line_no}`

**`ContactTrace`**

`{trace_id}`

