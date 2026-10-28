2\. Minimal Cover 

| Relation | Minimal Cover |
| :---- | :---- |
| `Building` | `building_id → building_name`  `building_id → building_type`  `building_name → building_id` |
| `Patient` | `patient_id → full_name`  `patient_id → date_of_birth`  `patient_id → gender`  `patient_id → phone_number` |
| `Allergy` | `allergy_id → allergen_name`  `allergen_name → allergy_id` |
| `MedicalStaff` | `med_staff_id → full_name`  `med_staff_id → specialty`  `med_staff_id → phone_number` |
| `Medicine` | `medicine_id → medicine_name`  `medicine_id → stock_quantity`  `medicine_id → unit_price`  `medicine_name → medicine_id` |
| `VaccinationCourse` | `course_id → course_name`  `course_id → target_disease`  `course_id → required_doses`  `course_name → course_id` |
| `Student` | `patient_id → student_code`  `patient_id → faculty`  `patient_id → dorm_building_id`  `student_code → patient_id` |
| `Staff` | `patient_id → uni_staff_code`  `patient_id → faculty`  `patient_id → office_room`  `uni_staff_code → patient_id` |
| `PatientAllergy` | `(patient_id, allergy_id) → severity`  `(patient_id, allergy_id) → onset_date`  `(patient_id, allergy_id) → note` |
| `ConsultSlot` | `slot_id → med_staff_id`  `slot_id → weekday`  `slot_id → start_time`  `slot_id → end_time`  `slot_id → booking_unit_min` |
| `Appointment` | `appointment_id → patient_id`  `appointment_id → med_staff_id`  `appointment_id → appointment_time`  `appointment_id → status` |
| `Consultation` | `consultation_id → patient_id`  `consultation_id → med_staff_id`  `consultation_id → appointment_id`  `consultation_id → consultation_date`  `consultation_id → symptoms`  `consultation_id → diagnosis` `consultation_id → notes` |
| `VaccinationRecord` | `record_id → patient_id`  `record_id → course_id`  `record_id → dose_number`  `record_id → date_given`  `(patient_id, course_id, dose_number) → record_id` |
| `Prescription` | `prescription_id → consultation_id`  `prescription_id → issue_date`  `consultation_id → prescription_id`  |
| `NotifiableCase` | `case_id → consultation_id`  `case_id → disease_name`  `case_id → date_reported`  `case_id → building_id` |
| `PrescriptionLine` | `(prescription_id, line_no) → medicine_id` `(prescription_id, line_no) → dosage`  `(prescription_id, line_no) → frequency`  `(prescription_id, line_no) → duration_days`  `(prescription_id, line_no) → quantity`  `(prescription_id, line_no) → unit_price` |
| `ContactTrace` | `trace_id → source_patient_id`  `trace_id → contact_patient_id`  `trace_id → contact_date`; `trace_id → source_case_id` |

