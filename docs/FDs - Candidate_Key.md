# LIST OF FDs & CANDIDATE KEYS DRAFT

**1. Building**

**Attribute Set:** U = {building_id, building_name, building_type}  
**Functional Dependencies (FDs):**  
1. **FD1:** building_id → {building_name, building_type}  
2. **FD2:** building_name → {building_id, building_type}  

**Candidate Key Identification:**  
- SA = ∅  
- IA = {building_id, building_name}  
- TA = {building_type}  
- building_id⁺ = U  
- building_name⁺ = U  

**Candidate Keys Draft:** {building_id}, {building_name}

**2. Patient**

**Attribute Set:** U = {patient_id, full_name, date_of_birth, gender, phone_number}  
**Functional Dependencies (FDs):**  
1. **FD1:** patient_id → {full_name, date_of_birth, gender, phone_number}  

**Candidate Key Identification:**  
- SA = {patient_id}  
- IA = ∅  
- TA = {full_name, date_of_birth, gender, phone_number}  
- SA⁺ = U  

**Candidate Keys Draft:** {patient_id}

**3. Allergy**

**Attribute Set:** U = {allergy_id, allergen_name}  
**Functional Dependencies (FDs):**  
1. **FD1:** allergy_id → allergen_name  
2. **FD2:** allergen_name → allergy_id  

**Candidate Key Identification:**  
- SA = ∅  
- IA = {allergy_id, allergen_name}  
- TA = ∅  
- allergy_id⁺ = U  
- allergen_name⁺ = U  

**Candidate Keys Draft:** {allergy_id}, {allergen_name}

**4. MedicalStaff**

**Attribute Set:** U = {med_staff_id, full_name, specialty, phone_number}  
**Functional Dependencies (FDs):**  
1. **FD1:** med_staff_id → {full_name, specialty, phone_number}  

**Candidate Key Identification:**  
- SA = {med_staff_id}  
- IA = ∅  
- TA = {full_name, specialty, phone_number}  
- SA⁺ = U  

**Candidate Keys Draft:** {med_staff_id}

**5. Medicine**

**Attribute Set:** U = {medicine_id, medicine_name, stock_quantity, unit_price}  
**Functional Dependencies (FDs):**  
1. **FD1:** medicine_id → {medicine_name, stock_quantity, unit_price}  
2. **FD2:** medicine_name → {medicine_id, stock_quantity, unit_price}  

**Candidate Key Identification:**  
- SA = ∅  
- IA = {medicine_id, medicine_name}  
- TA = {stock_quantity, unit_price}  
- medicine_id⁺ = U  
- medicine_name⁺ = U  

**Candidate Keys Draft:** {medicine_id}, {medicine_name}

**6. VaccinationCourse**

**Attribute Set:** U = {course_id, course_name, target_disease, required_doses}  
**Functional Dependencies (FDs):**  
1. **FD1:** course_id → {course_name, target_disease, required_doses}  
2. **FD2:** course_name → {course_id, target_disease, required_doses}  

**Candidate Key Identification:**  
- SA = ∅  
- IA = {course_id, course_name}  
- TA = {target_disease, required_doses}  
- course_id⁺ = U  
- course_name⁺ = U  

**Candidate Keys Draft:** {course_id}, {course_name}

**7. Student**

**Attribute Set:** U = {patient_id, student_code, faculty, dorm_building_id}  
**Functional Dependencies (FDs):**  
1. **FD1:** patient_id → {student_code, faculty, dorm_building_id}  
2. **FD2:** student_code → {patient_id, faculty, dorm_building_id}  

**Candidate Key Identification:**  
- SA = ∅  
- IA = {patient_id, student_code}  
- TA = {faculty, dorm_building_id}  
- patient_id⁺ = U  
- student_code⁺ = U  

**Candidate Keys Draft:** {patient_id}, {student_code}

**8. Staff**

**Attribute Set:** U = {patient_id, uni_staff_code, faculty, office_room}  
**Functional Dependencies (FDs):**  
1. **FD1:** patient_id → {uni_staff_code, faculty, office_room}  
2. **FD2:** uni_staff_code → {patient_id, faculty, office_room}  

**Candidate Key Identification:**  
- SA = ∅  
- IA = {patient_id, uni_staff_code}  
- TA = {faculty, office_room}  
- patient_id⁺ = U  
- uni_staff_code⁺ = U  

**Candidate Keys Draft:** {patient_id}, {uni_staff_code}

**9. PatientAllergy**

**Attribute Set:** U = {patient_id, allergy_id, severity, onset_date, note}  
**Functional Dependencies (FDs):**  
1. **FD1:** {patient_id, allergy_id} → {severity, onset_date, note}  

**Candidate Key Identification:**  
- SA = {patient_id, allergy_id}  
- IA = ∅  
- TA = {severity, onset_date, note}  
- SA⁺ = U  

**Candidate Keys Draft:** {patient_id, allergy_id}

**10. ConsultSlot**

**Attribute Set:** U = {slot_id, med_staff_id, weekday, start_time, end_time, booking_unit_min}  
**Functional Dependencies (FDs):**  
1. **FD1:** slot_id → {med_staff_id, weekday, start_time, end_time, booking_unit_min}  

**Candidate Key Identification:**  
- SA = {slot_id}  
- IA = ∅  
- TA = {med_staff_id, weekday, start_time, end_time, booking_unit_min}  
- SA⁺ = U  

**Candidate Keys Draft:** {slot_id}

**11. Appointment**

**Attribute Set:** U = {appointment_id, patient_id, med_staff_id, appointment_time, status}  
**Functional Dependencies (FDs):**  
1. **FD1:** appointment_id → {patient_id, med_staff_id, appointment_time, status}  

**Candidate Key Identification:**  
- SA = {appointment_id}  
- IA = ∅  
- TA = {patient_id, med_staff_id, appointment_time, status}  
- SA⁺ = U  

**Candidate Keys Draft:** {appointment_id}

**12. Consultation**

**Attribute Set:** U = {consultation_id, patient_id, med_staff_id, appointment_id, consultation_date, symptoms, diagnosis, notes}  
**Functional Dependencies (FDs):**  
1. **FD1:** consultation_id → {patient_id, med_staff_id, appointment_id, consultation_date, symptoms, diagnosis, notes}  
2. **FD2:** appointment_id → {consultation_id, patient_id, med_staff_id, consultation_date, symptoms, diagnosis, notes}  

**Candidate Key Identification:**  
- SA = ∅  
- IA = {consultation_id, appointment_id}  
- TA = {patient_id, med_staff_id, consultation_date, symptoms, diagnosis, notes}  
- consultation_id⁺ = U  
- appointment_id⁺ = U  

**Candidate Keys Draft:** {consultation_id}, {appointment_id}

**13. VaccinationRecord**

**Attribute Set:** U = {record_id, patient_id, course_id, dose_number, date_given}  
**Functional Dependencies (FDs):**  
1. **FD1:** record_id → {patient_id, course_id, dose_number, date_given}  
2. **FD2:** {patient_id, course_id, dose_number} → {record_id, date_given}  

**Candidate Key Identification:**  
- record_id⁺ = U  
- {patient_id, course_id, dose_number}⁺ = U  

**Candidate Keys Draft:** {record_id}, {patient_id, course_id, dose_number}

**14. Prescription**

**Attribute Set:** U = {prescription_id, consultation_id, issue_date}  
**Functional Dependencies (FDs):**  
1. **FD1:** prescription_id → {consultation_id, issue_date}  
2. **FD2:** consultation_id → {prescription_id, issue_date}  

**Candidate Key Identification:**  
- prescription_id⁺ = U  
- consultation_id⁺ = U  

**Candidate Keys Draft:** {prescription_id}, {consultation_id}

**15. NotifiableCase**

**Attribute Set:** U = {case_id, consultation_id, disease_name, date_reported, building_id}  
**Functional Dependencies (FDs):**  
1. **FD1:** case_id → {consultation_id, disease_name, date_reported, building_id}  

**Candidate Key Identification:**  
- SA = {case_id}  
- IA = ∅  
- TA = {consultation_id, disease_name, date_reported, building_id}  
- SA⁺ = U  

**Candidate Keys Draft:** {case_id}

**16. PrescriptionLine**

**Attribute Set:** U = {prescription_id, line_no, medicine_id, dosage, frequency, duration_days, quantity, unit_price}  
**Functional Dependencies (FDs):**  
1. **FD1:** {prescription_id, line_no} → {medicine_id, dosage, frequency, duration_days, quantity, unit_price}  

**Candidate Key Identification:**  
- SA = {prescription_id, line_no}  
- SA⁺ = U  

**Candidate Keys Draft:** {prescription_id, line_no}

**17. ContactTrace**

**Attribute Set:** U = {trace_id, source_patient_id, contact_patient_id, contact_date, source_case_id}  
**Functional Dependencies (FDs):**  
1. **FD1:** trace_id → {source_patient_id, contact_patient_id, contact_date, source_case_id}  

**Candidate Key Identification:**  
- SA = {trace_id}  
- IA = ∅  
- TA = {source_patient_id, contact_patient_id, contact_date, source_case_id}  
- SA⁺ = U  

**Candidate Keys Draft:** {trace_id}