-- 02_data.sql: sample data for Topic 14 University Health Service.
-- At least 15 realistic rows per table, enough for all 12 queries to
-- return non-empty results.
-- Insert order: parents before children.
-- (Building -> Patient -> Student/Staff -> Allergy
-- -> PatientAllergy -> MedicalStaff -> ConsultSlot -> Appointment
-- -> Consultation -> Medicine -> Prescription
-- -> PrescriptionLine -> VaccinationCourse
-- -> VaccinationRecord -> NotifiableCase -> ContactTrace).

USE uhs_db;

-- ============================================================
-- Building (15 rows)
-- ============================================================
INSERT INTO Building (building_id, building_name, building_type) VALUES
(1, 'Main Hall', 'hall'),
(2, 'Science Building', 'hall'),
(3, 'Library Hall', 'hall'),
(4, 'Dormitory A', 'dorm'),
(5, 'Dormitory B', 'dorm'),
(6, 'Dormitory C', 'dorm'),
(7, 'Health Center', 'clinic'),
(8, 'Emergency Clinic', 'clinic'),
(9, 'Lab Building', 'lab'),
(10, 'Research Lab', 'lab'),
(11, 'Pharmacy', 'pharmacy'),
(12, 'Main Pharmacy', 'pharmacy'),
(13, 'Admin Office', 'admin'),
(14, 'Records Office', 'admin'),
(15, 'Cafeteria', 'hall');

-- ============================================================
-- Patient (30 rows)
-- ============================================================
INSERT INTO Patient (
    patient_id, full_name, date_of_birth, gender,
    phone_number
) VALUES
(1, 'Nguyen Van An', '2002-03-15', 'M', '0901234567'),
(2, 'Tran Thi Binh', '2001-07-22', 'F', '0902345678'),
(3, 'Le Hoang Cuong', '2000-11-08', 'M', '0903456789'),
(4, 'Pham Minh Dung', '2003-01-30', 'F', '0904567890'),
(5, 'Vo Thanh Dat', '2002-09-12', 'M', '0905678901'),
(6, 'Bui Thu Ha', '2001-05-25', 'F', '0906789012'),
(7, 'Dang Quoc Bao', '2000-12-03', 'M', '0907890123'),
(8, 'Do Thi Em', '2003-04-18', 'F', '0908901234'),
(9, 'Ngo Van Gia', '2002-06-07', 'M', '0909012345'),
(10, 'Hoang Thi Hanh', '2001-10-14', 'F', '0900123456'),
(11, 'Truong Van Khoa', '2000-08-21', 'M', '0901122334'),
(12, 'Lam Thi Lan', '2003-02-09', 'F', '0902233445'),
(13, 'Phan Van Minh', '2002-12-11', 'M', '0903344556'),
(14, 'Vu Thi Nga', '2001-04-27', 'F', '0904455667'),
(15, 'Tien Van Phuc', '2000-06-16', 'M', '0905566778'),
(16, 'Nguyen Van Quang', '2002-01-20', 'M', '0906677889'),
(17, 'Tran Thi Rang', '2001-08-13', 'F', '0907788990'),
(18, 'Le Hoang Son', '2000-05-27', 'M', '0908899001'),
(19, 'Pham Minh Chau', '2003-09-09', 'F', '0909900112'),
(20, 'Vo Thanh Duong', '2002-11-23', 'M', '0900011223'),
(21, 'Bui Thu Hien', '2001-03-07', 'F', '0901122334'),
(22, 'Dang Quoc Dat', '2000-07-19', 'M', '0902233445'),
(23, 'Do Thi Gam', '2003-12-02', 'F', '0903344556'),
(24, 'Ngo Van Hieu', '2002-04-15', 'M', '0904455667'),
(25, 'Hoang Thi Hue', '2001-10-28', 'F', '0905566778'),
(26, 'Truong Van Lam', '2000-02-11', 'M', '0906677889'),
(27, 'Lam Thi Lieu', '2003-06-24', 'F', '0907788990'),
(28, 'Phan Van Nghia', '2002-08-30', 'M', '0908899001'),
(29, 'Vu Thi Oanh', '2001-12-17', 'F', '0909900112'),
(30, 'Tien Van Phat', '2000-04-05', 'M', '0900011223');

-- ============================================================
-- Student (15 rows)
-- ============================================================
INSERT INTO Student (patient_id, student_code, faculty, dorm_building_id) VALUES
(1, 'STU001', 'Computer Science', 4),
(2, 'STU002', 'Computer Science', 4),
(3, 'STU003', 'Electrical Engineering', 5),
(4, 'STU004', 'Business Administration', 5),
(5, 'STU005', 'Computer Science', 6),
(6, 'STU006', 'Electrical Engineering', 6),
(7, 'STU007', 'Business Administration', 4),
(8, 'STU008', 'Computer Science', 5),
(9, 'STU009', 'Electrical Engineering', 6),
(10, 'STU010', 'Business Administration', 4),
(16, 'STU011', 'Computer Science', 4),
(17, 'STU012', 'Electrical Engineering', 5),
(18, 'STU013', 'Business Administration', 6),
(19, 'STU014', 'Computer Science', 4),
(20, 'STU015', 'Electrical Engineering', 5);

-- ============================================================
-- Staff (15 rows)
-- ============================================================
INSERT INTO Staff (patient_id, uni_staff_code, faculty, office_room) VALUES
(11, 'STAFF001', 'Computer Science', 'A101'),
(12, 'STAFF002', 'Electrical Engineering', 'B202'),
(13, 'STAFF003', 'Business Administration', 'C303'),
(14, 'STAFF004', 'Computer Science', 'A102'),
(15, 'STAFF005', 'Electrical Engineering', 'B203'),
(21, 'STAFF006', 'Computer Science', 'A103'),
(22, 'STAFF007', 'Electrical Engineering', 'B204'),
(23, 'STAFF008', 'Business Administration', 'C304'),
(24, 'STAFF009', 'Computer Science', 'A104'),
(25, 'STAFF010', 'Electrical Engineering', 'B205'),
(26, 'STAFF011', 'Business Administration', 'C305'),
(27, 'STAFF012', 'Computer Science', 'A105'),
(28, 'STAFF013', 'Electrical Engineering', 'B206'),
(29, 'STAFF014', 'Business Administration', 'C306'),
(30, 'STAFF015', 'Computer Science', 'A106');

-- ============================================================
-- Allergy (15 rows)
-- ============================================================
INSERT INTO Allergy (allergy_id, allergen_name) VALUES
(1, 'Peanuts'),
(2, 'Penicillin'),
(3, 'Dust mites'),
(4, 'Pollen'),
(5, 'Shellfish'),
(6, 'Eggs'),
(7, 'Milk'),
(8, 'Soy'),
(9, 'Wheat'),
(10, 'Fish'),
(11, 'Latex'),
(12, 'Bee stings'),
(13, 'Aspirin'),
(14, 'Ibuprofen'),
(15, 'Sulfa drugs');

-- ============================================================
-- PatientAllergy (16 rows)
-- ============================================================
INSERT INTO PatientAllergy (
    patient_id, allergy_id, severity, onset_date,
    note
) VALUES
(1, 1, 'severe', '2015-03-10', 'Anaphylaxis risk'),
(1, 3, 'mild', '2018-07-22', 'Seasonal'),
(2, 2, 'moderate', '2019-11-05', 'Rash'),
(2, 4, 'mild', '2020-04-18', 'Hay fever'),
(3, 5, 'severe', '2017-09-30', 'Avoid all shellfish'),
(4, 6, 'mild', '2021-01-12', 'Digestive issues'),
(5, 7, 'moderate', '2016-06-25', 'Lactose intolerant'),
(6, 8, 'mild', '2020-08-14', 'Mild reaction'),
(7, 9, 'moderate', '2018-12-03', 'Gluten sensitivity'),
(8, 10, 'mild', '2022-02-28', 'Avoid raw fish'),
(9, 11, 'severe', '2019-05-17', 'Contact reaction'),
(10, 12, 'moderate', '2021-07-08', 'Swelling'),
(11, 13, 'mild', '2017-10-19', 'Stomach upset'),
(12, 14, 'moderate', '2020-03-23', 'Headache'),
(13, 15, 'severe', '2018-08-11', 'Anaphylaxis risk'),
(14, 2, 'mild', '2022-06-05', 'Mild rash');

-- ============================================================
-- MedicalStaff (15 rows)
-- ============================================================
INSERT INTO MedicalStaff (
    med_staff_id, full_name, specialty,
    phone_number
) VALUES
(1, 'Dr. Nguyen Thi Mai', 'General Practice', '0911234567'),
(2, 'Dr. Tran Van Hoa', 'Cardiology', '0912345678'),
(3, 'Dr. Le Thi Nga', 'Dermatology', '0913456789'),
(4, 'Dr. Pham Van Khoa', 'Pediatrics', '0914567890'),
(5, 'Dr. Vo Thi Lien', 'Gynecology', '0915678901'),
(6, 'Dr. Bui Van Tuan', 'Orthopedics', '0916789012'),
(7, 'Dr. Dang Thi Hoa', 'Neurology', '0917890123'),
(8, 'Dr. Do Van Nam', 'Psychiatry', '0918901234'),
(9, 'Dr. Ngo Thi Phuong', 'Ophthalmology', '0919012345'),
(10, 'Dr. Hoang Van Sinh', 'ENT', '0910123456'),
(11, 'Dr. Truong Thi Mai', 'Dentistry', '0911234568'),
(12, 'Dr. Lam Van Hung', 'General Practice', '0912345679'),
(13, 'Dr. Phan Thi Thanh', 'Cardiology', '0913456780'),
(14, 'Dr. Vu Van Chien', 'Pediatrics', '0914567891'),
(15, 'Dr. Tien Thi Ngoc', 'Dermatology', '0915678902');

-- ============================================================
-- ConsultSlot (15 rows)
-- ============================================================
INSERT INTO ConsultSlot (
    slot_id, med_staff_id, weekday, start_time, end_time,
    booking_unit_min
) VALUES
(1, 1, 1, '08:00:00', '12:00:00', 30),
(2, 1, 3, '08:00:00', '12:00:00', 30),
(3, 1, 5, '13:00:00', '17:00:00', 30),
(4, 2, 2, '08:00:00', '12:00:00', 30),
(5, 2, 4, '08:00:00', '12:00:00', 30),
(6, 3, 1, '13:00:00', '17:00:00', 30),
(7, 3, 3, '13:00:00', '17:00:00', 30),
(8, 4, 2, '13:00:00', '17:00:00', 30),
(9, 4, 4, '13:00:00', '17:00:00', 30),
(10, 5, 1, '08:00:00', '12:00:00', 30),
(11, 5, 3, '08:00:00', '12:00:00', 30),
(12, 6, 2, '08:00:00', '12:00:00', 30),
(13, 6, 4, '08:00:00', '12:00:00', 30),
(14, 7, 1, '08:00:00', '12:00:00', 30),
(15, 7, 3, '08:00:00', '12:00:00', 30);

-- ============================================================
-- Appointment (15 rows)
-- ============================================================
INSERT INTO Appointment (
    appointment_id, patient_id, med_staff_id,
    appointment_time, status
) VALUES
(1, 1, 1, '2026-10-01 08:30:00', 'completed'),
(2, 2, 1, '2026-10-01 09:00:00', 'completed'),
(3, 3, 2, '2026-10-02 08:30:00', 'completed'),
(4, 4, 3, '2026-10-02 13:30:00', 'completed'),
(5, 5, 4, '2026-10-03 13:30:00', 'completed'),
(6, 6, 5, '2026-10-03 08:30:00', 'completed'),
(7, 7, 6, '2026-10-04 08:30:00', 'completed'),
(8, 8, 7, '2026-10-04 08:30:00', 'completed'),
(9, 9, 8, '2026-10-05 08:30:00', 'completed'),
(10, 10, 9, '2026-10-05 13:30:00', 'completed'),
(11, 11, 10, '2026-10-06 08:30:00', 'completed'),
(12, 12, 11, '2026-10-06 13:30:00', 'completed'),
(13, 13, 12, '2026-10-07 08:30:00', 'completed'),
(14, 14, 13, '2026-10-07 13:30:00', 'completed'),
(15, 15, 14, '2026-10-08 08:30:00', 'no-show');

-- ============================================================
-- Consultation (15 rows)
-- ============================================================
INSERT INTO Consultation (
    consultation_id, patient_id, med_staff_id,
    appointment_id, consultation_date, symptoms, diagnosis, notes
) VALUES
(
    1,
    1,
    1,
    1,
    '2026-10-01 08:30:00',
    'Fever, cough, sore throat',
    'Common cold',
    'Rest and fluids'
),
(
    2, 2, 1, 2, '2026-10-01 09:00:00', 'Headache, nausea', 'Migraine',
    'Prescribed painkillers'
),
(
    3, 3, 2, 3, '2026-10-02 08:30:00', 'Chest pain, shortness of breath',
    'Hypertension', 'Referred to cardiology'
),
(
    4, 4, 3, 4, '2026-10-02 13:30:00', 'Skin rash, itching', 'Eczema',
    'Topical cream prescribed'
),
(
    5, 5, 4, 5, '2026-10-03 13:30:00', 'Fever, cough', 'Influenza',
    'Antiviral medication'
),
(
    6, 6, 5, 6, '2026-10-03 08:30:00', 'Abdominal pain', 'Gastritis',
    'Dietary changes recommended'
),
(
    7, 7, 6, 7, '2026-10-04 08:30:00', 'Back pain', 'Muscle strain',
    'Physical therapy'
),
(
    8,
    8,
    7,
    8,
    '2026-10-04 08:30:00',
    'Dizziness, headache',
    'Tension headache',
    'Stress management'
),
(
    9, 9, 8, 9, '2026-10-05 08:30:00', 'Eye redness', 'Conjunctivitis',
    'Eye drops prescribed'
),
(
    10, 10, 9, 10, '2026-10-05 13:30:00', 'Sore throat', 'Pharyngitis',
    'Antibiotics prescribed'
),
(
    11, 11, 10, 11, '2026-10-06 08:30:00', 'Toothache', 'Dental caries',
    'Filling needed'
),
(
    12, 12, 11, 12, '2026-10-06 13:30:00', 'Skin acne', 'Acne vulgaris',
    'Topical treatment'
),
(
    13, 13, 12, 13, '2026-10-07 08:30:00', 'Fever, body ache', 'Dengue',
    'Notifiable case reported'
),
(
    14, 14, 13, 14, '2026-10-07 13:30:00', 'Chest tightness', 'Asthma',
    'Inhaler prescribed'
),
(
    15, 15, 14, 15, '2026-10-08 08:30:00', 'Fatigue', 'Anemia',
    'Blood test ordered'
);

-- ============================================================
-- Medicine (15 rows)
-- ============================================================
INSERT INTO Medicine (
    medicine_id, medicine_name, stock_quantity,
    unit_price
) VALUES
(1, 'Paracetamol 500mg', 100, 5000.00),
(2, 'Amoxicillin 250mg', 80, 12000.00),
(3, 'Ibuprofen 400mg', 120, 8000.00),
(4, 'Cetirizine 10mg', 90, 6000.00),
(5, 'Omeprazole 20mg', 70, 15000.00),
(6, 'Metformin 500mg', 60, 10000.00),
(7, 'Amlodipine 5mg', 85, 18000.00),
(8, 'Salbutamol inhaler', 40, 45000.00),
(9, 'Atorvastatin 20mg', 55, 25000.00),
(10, 'Losartan 50mg', 65, 22000.00),
(11, 'Azithromycin 250mg', 75, 30000.00),
(12, 'Prednisone 5mg', 95, 12000.00),
(13, 'Hydrocortisone cream', 50, 35000.00),
(14, 'Chlorpheniramine 4mg', 110, 7000.00),
(15, 'Vitamin C 500mg', 200, 4000.00);

-- ============================================================
-- Prescription (15 rows)
-- ============================================================
INSERT INTO Prescription (prescription_id, consultation_id, issue_date) VALUES
(1, 1, '2026-10-01'),
(2, 2, '2026-10-01'),
(3, 3, '2026-10-02'),
(4, 4, '2026-10-02'),
(5, 5, '2026-10-03'),
(6, 6, '2026-10-03'),
(7, 7, '2026-10-04'),
(8, 8, '2026-10-04'),
(9, 9, '2026-10-05'),
(10, 10, '2026-10-05'),
(11, 11, '2026-10-06'),
(12, 12, '2026-10-06'),
(13, 13, '2026-10-07'),
(14, 14, '2026-10-07'),
(15, 15, '2026-10-08');

-- ============================================================
-- PrescriptionLine (15 rows)
-- ============================================================
INSERT INTO PrescriptionLine (
    prescription_id, line_no, medicine_id, dosage,
    frequency, duration_days, quantity, unit_price
) VALUES
(1, 1, 1, '1 tablet', '3 times daily', 5, 15, 5000.00),
(1, 2, 14, '1 tablet', '2 times daily', 5, 10, 7000.00),
(2, 1, 3, '1 tablet', 'as needed', 3, 6, 8000.00),
(3, 1, 7, '1 tablet', 'once daily', 30, 30, 18000.00),
(4, 1, 13, 'apply thin layer', '2 times daily', 7, 1, 35000.00),
(5, 1, 2, '1 capsule', '3 times daily', 7, 21, 12000.00),
(5, 2, 15, '1 tablet', 'once daily', 7, 7, 4000.00),
(6, 1, 5, '1 capsule', 'once daily', 14, 14, 15000.00),
(7, 1, 3, '1 tablet', 'as needed', 5, 10, 8000.00),
(8, 1, 4, '1 tablet', 'once daily', 7, 7, 6000.00),
(9, 1, 4, '1 tablet', 'once daily', 7, 7, 6000.00),
(10, 1, 2, '1 capsule', '3 times daily', 7, 21, 12000.00),
(11, 1, 1, '1 tablet', '3 times daily', 3, 9, 5000.00),
(12, 1, 13, 'apply thin layer', '2 times daily', 10, 1, 35000.00),
(13, 1, 11, '1 tablet', '3 times daily', 5, 15, 30000.00),
(14, 1, 8, '2 puffs', 'as needed', 30, 1, 45000.00);

-- ============================================================
-- VaccinationCourse (15 rows)
-- ============================================================
INSERT INTO VaccinationCourse (
    course_id, course_name, target_disease,
    required_doses
) VALUES
(1, 'Hepatitis B', 'Hepatitis B', 3),
(2, 'Tetanus', 'Tetanus', 1),
(3, 'Influenza', 'Influenza', 1),
(4, 'MMR', 'Measles, Mumps, Rubella', 2),
(5, 'Varicella', 'Chickenpox', 2),
(6, 'HPV', 'Human Papillomavirus', 3),
(7, 'Pneumococcal', 'Pneumonia', 1),
(8, 'Meningococcal', 'Meningitis', 1),
(9, 'Polio', 'Polio', 4),
(10, 'Rabies', 'Rabies', 3),
(11, 'Typhoid', 'Typhoid', 1),
(12, 'Cholera', 'Cholera', 2),
(13, 'Japanese Encephalitis', 'Japanese Encephalitis', 2),
(14, 'Yellow Fever', 'Yellow Fever', 1),
(15, 'COVID-19', 'COVID-19', 2);

-- ============================================================
-- VaccinationRecord (15 rows)
-- ============================================================
INSERT INTO VaccinationRecord (
    record_id, patient_id, course_id, dose_number,
    date_given
) VALUES
(1, 1, 1, 1, '2025-01-15'),
(2, 1, 1, 2, '2025-02-15'),
(3, 1, 1, 3, '2025-03-15'),
(4, 2, 2, 1, '2025-03-20'),
(5, 3, 3, 1, '2025-04-10'),
(6, 4, 4, 1, '2025-05-05'),
(7, 4, 4, 2, '2025-06-05'),
(8, 5, 5, 1, '2025-06-15'),
(9, 5, 5, 2, '2025-07-15'),
(10, 6, 6, 1, '2025-08-01'),
(11, 6, 6, 2, '2025-09-01'),
(12, 6, 6, 3, '2025-10-01'),
(13, 7, 7, 1, '2025-10-15'),
(14, 8, 8, 1, '2025-11-01'),
(15, 9, 9, 1, '2025-11-15');

-- ============================================================
-- NotifiableCase (15 rows)
-- ============================================================
INSERT INTO NotifiableCase (
    case_id, consultation_id, disease_name,
    date_reported, building_id
) VALUES
(1, 13, 'Dengue', '2026-10-07', 4),
(2, 13, 'Dengue', '2026-10-08', 4),
(3, 5, 'Influenza', '2026-10-03', 5),
(4, 5, 'Influenza', '2026-10-04', 5),
(5, 10, 'Pharyngitis', '2026-10-05', 6),
(6, 3, 'Hypertension', '2026-10-02', 7),
(7, 7, 'Muscle strain', '2026-10-04', 8),
(8, 9, 'Conjunctivitis', '2026-10-05', 9),
(9, 11, 'Dental caries', '2026-10-06', 10),
(10, 12, 'Acne vulgaris', '2026-10-06', 11),
(11, 14, 'Asthma', '2026-10-07', 12),
(12, 15, 'Anemia', '2026-10-08', 13),
(13, 1, 'Common cold', '2026-10-01', 14),
(14, 2, 'Migraine', '2026-10-01', 15),
(15, 4, 'Eczema', '2026-10-02', 4);

-- ============================================================
-- ContactTrace (15 rows)
-- ============================================================
INSERT INTO ContactTrace (
    trace_id, source_patient_id, contact_patient_id,
    contact_date, source_case_id
) VALUES
(1, 1, 2, '2026-10-07', 1),
(2, 1, 3, '2026-10-07', 1),
(3, 1, 4, '2026-10-08', 1),
(4, 2, 5, '2026-10-08', 2),
(5, 2, 6, '2026-10-08', 2),
(6, 3, 7, '2026-10-03', 3),
(7, 3, 8, '2026-10-04', 3),
(8, 4, 9, '2026-10-05', 5),
(9, 5, 10, '2026-10-06', 7),
(10, 6, 11, '2026-10-07', 9),
(11, 7, 12, '2026-10-08', 11),
(12, 8, 13, '2026-10-01', 13),
(13, 9, 14, '2026-10-02', 15),
(14, 10, 15, '2026-10-03', 3),
(15, 11, 1, '2026-10-04', NULL);
