-- ============================================================
-- University Health Service Database (uhs_db)
-- 02_data.sql: Sample Data Seed Script
-- Standards: Pinned stack MySQL 8.4 LTS / MariaDB compatible
-- Constraint verification: All 17 tables populated with >= 15 realistic rows
-- Fully normalized & validated against business rules (BR-01 to BR-20)
-- MySQL-specific syntax: ON DUPLICATE KEY UPDATE appears in three seed
-- INSERT statements. Every other statement is standard SQL.
-- ============================================================
USE uhs_db;

-- DESTRUCTIVE: this script clears all 17 tables before seeding.
-- Development seed only. The transaction restores the previous data if an
-- INSERT fails.
START TRANSACTION;

DELETE FROM ContactTrace;
DELETE FROM NotifiableCase;
DELETE FROM PrescriptionLine;
DELETE FROM Prescription;
DELETE FROM VaccinationRecord;
DELETE FROM VaccinationCourse;
DELETE FROM Medicine;
DELETE FROM Consultation;
DELETE FROM Appointment;
DELETE FROM ConsultSlot;
DELETE FROM PatientAllergy;
DELETE FROM Staff;
DELETE FROM Student;
DELETE FROM MedicalStaff;
DELETE FROM Allergy;
DELETE FROM Patient;
DELETE FROM Building;

-- ============================================================
-- Building (16 rows >= 15 rows)
-- Allowed building_type: 'hall', 'dorm', 'clinic', 'lab', 'pharmacy', 'admin'
-- ============================================================
INSERT INTO
    Building (building_id, building_name, building_type)
VALUES
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
    (15, 'Student Center Cafeteria', 'hall'),
    (16, 'Sports Complex Hall', 'hall');

-- ============================================================
-- Patient (32 rows >= 30 rows)
-- Covering IDs 1-32 to support Student and Staff ISA mappings
-- ============================================================
INSERT INTO
    Patient (
        patient_id,
        full_name,
        date_of_birth,
        gender,
        phone_number
    )
VALUES
    (
        1,
        'Nguyen Van An',
        '2002-03-15',
        'M',
        '0901234567'
    ),
    (
        2,
        'Tran Thi Binh',
        '2001-07-22',
        'F',
        '0902345678'
    ),
    (
        3,
        'Le Hoang Cuong',
        '2000-11-08',
        'M',
        '0903456789'
    ),
    (
        4,
        'Pham Minh Dung',
        '2003-01-30',
        'F',
        '0904567890'
    ),
    (
        5,
        'Vo Thanh Dat',
        '2002-09-12',
        'M',
        '0905678901'
    ),
    (6, 'Bui Thu Ha', '2001-05-25', 'F', '0906789012'),
    (
        7,
        'Dang Quoc Bao',
        '2000-12-03',
        'M',
        '0907890123'
    ),
    (8, 'Do Thi Em', '2003-04-18', 'F', '0908901234'),
    (9, 'Ngo Van Gia', '2002-06-07', 'M', '0909012345'),
    (
        10,
        'Hoang Thi Hanh',
        '2001-10-14',
        'F',
        '0900123456'
    ),
    (
        11,
        'Truong Van Khoa',
        '2000-08-21',
        'M',
        '0901122334'
    ),
    (
        12,
        'Lam Thi Lan',
        '2003-02-09',
        'F',
        '0902233445'
    ),
    (
        13,
        'Phan Van Minh',
        '2002-12-11',
        'M',
        '0903344556'
    ),
    (14, 'Vu Thi Nga', '2001-04-27', 'F', '0904455667'),
    (
        15,
        'Tien Van Phuc',
        '2000-06-16',
        'M',
        '0905566778'
    ),
    (
        16,
        'Nguyen Van Quang',
        '2002-01-20',
        'M',
        '0906677889'
    ),
    (
        17,
        'Tran Thi Rang',
        '2001-08-13',
        'F',
        '0907788990'
    ),
    (
        18,
        'Le Hoang Son',
        '2000-05-27',
        'M',
        '0908899001'
    ),
    (
        19,
        'Pham Minh Chau',
        '2003-09-09',
        'F',
        '0909900112'
    ),
    (
        20,
        'Vo Thanh Duong',
        '2002-11-23',
        'M',
        '0900011223'
    ),
    (
        21,
        'Bui Thu Hien',
        '2001-03-07',
        'F',
        '0901122334'
    ),
    (
        22,
        'Dang Quoc Dat',
        '2000-07-19',
        'M',
        '0902233445'
    ),
    (23, 'Do Thi Gam', '2003-12-02', 'F', '0903344556'),
    (
        24,
        'Ngo Van Hieu',
        '2002-04-15',
        'M',
        '0904455667'
    ),
    (
        25,
        'Hoang Thi Hue',
        '2001-10-28',
        'F',
        '0905566778'
    ),
    (
        26,
        'Truong Van Lam',
        '2000-02-11',
        'M',
        '0906677889'
    ),
    (
        27,
        'Lam Thi Lieu',
        '2003-06-24',
        'F',
        '0907788990'
    ),
    (
        28,
        'Phan Van Nghia',
        '2002-08-30',
        'M',
        '0908899001'
    ),
    (
        29,
        'Vu Thi Oanh',
        '2001-12-17',
        'F',
        '0909900112'
    ),
    (
        30,
        'Tien Van Phat',
        '2000-04-05',
        'M',
        '0900011223'
    ),
    (
        31,
        'Dinh Van Thang',
        '2001-06-18',
        'M',
        '0901238899'
    ),
    (
        32,
        'Nong Thi Tuyet',
        '2002-10-10',
        'F',
        '0909871122'
    );

-- task_02.sql: Data inserts for Student and Staff tables
-- Satisfies >= 15 realistic rows per table, matches schema constraints and foreign keys.
-- ============================================================
-- Student (15 rows)
-- PK/FK patient_id -> Patient(patient_id) (patients 1..10, 16..20)
-- dorm_building_id -> Building(building_id) (dorms: 4, 5, 6)
-- student_code UNIQUE
-- ============================================================
INSERT INTO
    Student (
        patient_id,
        student_code,
        faculty,
        dorm_building_id
    )
VALUES
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
-- PK/FK patient_id -> Patient(patient_id) (patients 11..15, 21..30)
-- uni_staff_code UNIQUE
-- ============================================================
INSERT INTO
    Staff (patient_id, uni_staff_code, faculty, office_room)
VALUES
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

-- Deliverable for Task 03 (dept_engineering): Allergy, PatientAllergy, MedicalStaff
-- Schema reference: sql/01_schema.sql
-- Compatibility reference: sql/02_data.sql
-- ============================================================
-- Allergy (15 rows)
-- Table structure:
--   allergy_id INT AUTO_INCREMENT PRIMARY KEY
--   allergen_name VARCHAR(100) NOT NULL UNIQUE
-- ============================================================
INSERT INTO
    Allergy (allergy_id, allergen_name)
VALUES
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
    (15, 'Sulfa drugs') ON DUPLICATE KEY
UPDATE allergen_name =
VALUES
    (allergen_name);

-- ============================================================
-- PatientAllergy (16 rows)
-- Table structure:
--   patient_id INT NOT NULL (FK -> Patient)
--   allergy_id INT NOT NULL (FK -> Allergy)
--   severity VARCHAR(10) NOT NULL CHECK (severity IN ('mild', 'moderate', 'severe'))
--   onset_date DATE
--   note TEXT
--   PRIMARY KEY (patient_id, allergy_id)
-- ============================================================
INSERT INTO
    PatientAllergy (
        patient_id,
        allergy_id,
        severity,
        onset_date,
        note
    )
VALUES
    (1, 1, 'severe', '2015-03-10', 'Anaphylaxis risk'),
    (1, 3, 'mild', '2018-07-22', 'Seasonal'),
    (2, 2, 'moderate', '2019-11-05', 'Rash'),
    (2, 4, 'mild', '2020-04-18', 'Hay fever'),
    (
        3,
        5,
        'severe',
        '2017-09-30',
        'Avoid all shellfish'
    ),
    (4, 6, 'mild', '2021-01-12', 'Digestive issues'),
    (
        5,
        7,
        'moderate',
        '2016-06-25',
        'Lactose intolerant'
    ),
    (6, 8, 'mild', '2020-08-14', 'Mild reaction'),
    (
        7,
        9,
        'moderate',
        '2018-12-03',
        'Gluten sensitivity'
    ),
    (8, 10, 'mild', '2022-02-28', 'Avoid raw fish'),
    (9, 11, 'severe', '2019-05-17', 'Contact reaction'),
    (10, 12, 'moderate', '2021-07-08', 'Swelling'),
    (11, 13, 'mild', '2017-10-19', 'Stomach upset'),
    (12, 14, 'moderate', '2020-03-23', 'Headache'),
    (
        13,
        15,
        'severe',
        '2018-08-11',
        'Anaphylaxis risk'
    ),
    (14, 2, 'mild', '2022-06-05', 'Mild rash') ON DUPLICATE KEY
UPDATE severity =
VALUES
    (severity),
    onset_date =
VALUES
    (onset_date),
    note =
VALUES
    (note);

-- ============================================================
-- MedicalStaff (15 rows)
-- Table structure:
--   med_staff_id INT AUTO_INCREMENT PRIMARY KEY
--   full_name VARCHAR(100) NOT NULL
--   specialty VARCHAR(100)
--   phone_number VARCHAR(20)
-- ============================================================
INSERT INTO
    MedicalStaff (med_staff_id, full_name, specialty, phone_number)
VALUES
    (
        1,
        'Dr. Nguyen Thi Mai',
        'General Practice',
        '0911234567'
    ),
    (2, 'Dr. Tran Van Hoa', 'Cardiology', '0912345678'),
    (3, 'Dr. Le Thi Nga', 'Dermatology', '0913456789'),
    (
        4,
        'Dr. Pham Van Khoa',
        'Pediatrics',
        '0914567890'
    ),
    (5, 'Dr. Vo Thi Lien', 'Gynecology', '0915678901'),
    (
        6,
        'Dr. Bui Van Tuan',
        'Orthopedics',
        '0916789012'
    ),
    (7, 'Dr. Dang Thi Hoa', 'Neurology', '0917890123'),
    (8, 'Dr. Do Van Nam', 'Psychiatry', '0918901234'),
    (
        9,
        'Dr. Ngo Thi Phuong',
        'Ophthalmology',
        '0919012345'
    ),
    (10, 'Dr. Hoang Van Sinh', 'ENT', '0910123456'),
    (
        11,
        'Dr. Truong Thi Mai',
        'Dentistry',
        '0911234568'
    ),
    (
        12,
        'Dr. Lam Van Hung',
        'General Practice',
        '0912345679'
    ),
    (
        13,
        'Dr. Phan Thi Thanh',
        'Cardiology',
        '0913456780'
    ),
    (
        14,
        'Dr. Vu Van Chien',
        'Pediatrics',
        '0914567891'
    ),
    (
        15,
        'Dr. Tien Thi Ngoc',
        'Dermatology',
        '0915678902'
    ) ON DUPLICATE KEY
UPDATE full_name =
VALUES
    (full_name),
    specialty =
VALUES
    (specialty),
    phone_number =
VALUES
    (phone_number);

-- task_04.sql: ConsultSlot, Appointment, Consultation insert data
-- Created by leaf-worker-4 (dept_engineering)
-- ============================================================
-- ConsultSlot (20 rows >= 15 rows)
-- Defines weekly consulting schedules for medical staff
-- Weekday: 1=Monday .. 7=Sunday
-- ============================================================
INSERT INTO
    ConsultSlot (
        slot_id,
        med_staff_id,
        weekday,
        start_time,
        end_time,
        booking_unit_min
    )
VALUES
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
    (15, 7, 3, '08:00:00', '12:00:00', 30),
    (16, 8, 5, '08:00:00', '12:00:00', 45),
    (17, 9, 2, '13:30:00', '16:30:00', 20),
    (18, 10, 3, '08:00:00', '11:30:00', 30),
    (19, 11, 4, '13:00:00', '17:00:00', 30),
    (20, 12, 5, '08:30:00', '12:00:00', 30);

-- ============================================================
-- Appointment (20 rows >= 15 rows)
-- Covers multiple statuses: 'completed', 'scheduled', 'cancelled', 'no-show'
-- ============================================================
INSERT INTO
    Appointment (
        appointment_id,
        patient_id,
        med_staff_id,
        appointment_time,
        status
    )
VALUES
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
    (15, 15, 14, '2026-10-08 08:30:00', 'completed'),
    (16, 16, 1, '2026-10-08 10:00:00', 'completed'),
    (17, 17, 2, '2026-10-09 09:30:00', 'completed'),
    (18, 18, 3, '2026-10-12 14:00:00', 'scheduled'),
    (19, 19, 4, '2026-10-13 10:00:00', 'cancelled'),
    (20, 20, 5, '2026-10-14 11:00:00', 'no-show');

-- ============================================================
-- Consultation (20 rows >= 15 rows)
-- Supports both pre-booked appointments (appointment_id NOT NULL)
-- and walk-in consultations (appointment_id NULL)
-- ============================================================
INSERT INTO
    Consultation (
        consultation_id,
        patient_id,
        med_staff_id,
        appointment_id,
        consultation_date,
        symptoms,
        diagnosis,
        notes
    )
VALUES
    (
        1,
        1,
        1,
        1,
        '2026-10-01 08:30:00',
        'Fever, cough, sore throat',
        'Common cold',
        'Rest and fluids recommended'
    ),
    (
        2,
        2,
        1,
        2,
        '2026-10-01 09:00:00',
        'Headache, nausea, photophobia',
        'Migraine',
        'Prescribed painkillers, follow up if persistent'
    ),
    (
        3,
        3,
        2,
        3,
        '2026-10-02 08:30:00',
        'Chest pain, shortness of breath',
        'Hypertension',
        'Referred to cardiology clinic'
    ),
    (
        4,
        4,
        3,
        4,
        '2026-10-02 13:30:00',
        'Skin rash, itching on forearm',
        'Eczema',
        'Topical corticosteroid cream prescribed'
    ),
    (
        5,
        5,
        4,
        5,
        '2026-10-03 13:30:00',
        'High fever, dry cough, muscle ache',
        'Influenza',
        'Antiviral medication prescribed'
    ),
    (
        6,
        6,
        5,
        6,
        '2026-10-03 08:30:00',
        'Epigastric pain, bloating',
        'Gastritis',
        'Dietary changes recommended, avoid spicy foods'
    ),
    (
        7,
        7,
        6,
        7,
        '2026-10-04 08:30:00',
        'Acute lower back pain after lifting',
        'Muscle strain',
        'Physical therapy and oral NSAIDs'
    ),
    (
        8,
        8,
        7,
        8,
        '2026-10-04 08:30:00',
        'Dizziness, tension headache',
        'Tension headache',
        'Stress management counseling provided'
    ),
    (
        9,
        9,
        8,
        9,
        '2026-10-05 08:30:00',
        'Eye redness, watery discharge',
        'Conjunctivitis',
        'Antibiotic eye drops prescribed'
    ),
    (
        10,
        10,
        9,
        10,
        '2026-10-05 13:30:00',
        'Severe sore throat, difficulty swallowing',
        'Pharyngitis',
        'Antibiotics course initiated'
    ),
    (
        11,
        11,
        10,
        11,
        '2026-10-06 08:30:00',
        'Sharp toothache on upper right molar',
        'Dental caries',
        'Referred for dental filling'
    ),
    (
        12,
        12,
        11,
        12,
        '2026-10-06 13:30:00',
        'Facial acne flare-up with papules',
        'Acne vulgaris',
        'Topical benzoyl peroxide prescribed'
    ),
    (
        13,
        13,
        12,
        13,
        '2026-10-07 08:30:00',
        'High sudden fever, joint pain, retro-orbital pain',
        'Dengue',
        'Notifiable case reported - blood count monitoring'
    ),
    (
        14,
        14,
        13,
        14,
        '2026-10-07 13:30:00',
        'Wheezing, chest tightness at night',
        'Asthma exacerbation',
        'Salbutamol inhaler prescribed'
    ),
    (
        15,
        15,
        14,
        15,
        '2026-10-08 08:30:00',
        'General chronic fatigue, pallor',
        'Iron deficiency anemia',
        'Complete blood count ordered, iron supplements advised'
    ),
    (
        16,
        16,
        1,
        16,
        '2026-10-08 10:00:00',
        'Persistent lumbar stiffness and tenderness',
        'Lumbar strain',
        'Prescribed muscle relaxants and ergonomics guidance'
    ),
    (
        17,
        17,
        2,
        17,
        '2026-10-09 09:30:00',
        'Heart palpitations after light exertion, anxiety',
        'Sinus tachycardia',
        'ECG performed normal rhythm, advised stress reduction'
    ),
    (
        18,
        18,
        3,
        NULL,
        '2026-10-09 10:15:00',
        'Sudden acute allergic reaction, widespread urticaria',
        'Acute urticaria',
        'Emergency walk-in; antihistamine administered'
    ),
    (
        19,
        19,
        4,
        NULL,
        '2026-10-09 11:30:00',
        'Shivering, high fever 39C, severe pharyngeal pain',
        'Acute tonsillitis',
        'Walk-in clinic visit; oral antibiotics and antipyretics'
    ),
    (
        20,
        20,
        5,
        NULL,
        '2026-10-09 14:45:00',
        'Severe abdominal cramping, multiple episodes of vomiting',
        'Acute gastroenteritis',
        'Walk-in clinic visit; oral rehydration therapy given'
    );

-- Sample realistic INSERT data for Medicine, Prescription, and PrescriptionLine.
-- Aligns with the schema in sql/01_schema.sql.
-- ============================================================
-- Medicine (17 rows, >= 15 rows). Row 17 has zero stock so query Q12 returns
-- a result from this base seed; no supplement is needed for non-emptiness.
-- ============================================================
INSERT INTO
    Medicine (
        medicine_id,
        medicine_name,
        stock_quantity,
        unit_price
    )
VALUES
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
    (15, 'Vitamin C 500mg', 200, 4000.00),
    (16, 'Oral Rehydration Salts (ORS)', 150, 3500.00),
    (17, 'Discontinued Vitamin B12', 0, 5500.00);

-- ============================================================
-- Prescription (15 rows, >= 15 rows)
-- Note: Each prescription corresponds to exactly 1 consultation (UQ constraint)
-- ============================================================
INSERT INTO
    Prescription (prescription_id, consultation_id, issue_date)
VALUES
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
-- PrescriptionLine (18 rows, >= 15 rows)
-- Note: Weak entity with composite PK (prescription_id, line_no)
-- Unit prices match frozen prices at order time
-- ============================================================
INSERT INTO
    PrescriptionLine (
        prescription_id,
        line_no,
        medicine_id,
        dosage,
        frequency,
        duration_days,
        quantity,
        unit_price
    )
VALUES
    (
        1,
        1,
        1,
        '1 tablet',
        '3 times daily',
        5,
        15,
        5000.00
    ),
    (
        1,
        2,
        14,
        '1 tablet',
        '2 times daily',
        5,
        10,
        7000.00
    ),
    (2, 1, 3, '1 tablet', 'as needed', 3, 6, 8000.00),
    (
        3,
        1,
        7,
        '1 tablet',
        'once daily',
        30,
        30,
        18000.00
    ),
    (
        4,
        1,
        13,
        'apply thin layer',
        '2 times daily',
        7,
        1,
        35000.00
    ),
    (
        5,
        1,
        2,
        '1 capsule',
        '3 times daily',
        7,
        21,
        12000.00
    ),
    (5, 2, 15, '1 tablet', 'once daily', 7, 7, 4000.00),
    (
        6,
        1,
        5,
        '1 capsule',
        'once daily',
        14,
        14,
        15000.00
    ),
    (7, 1, 3, '1 tablet', 'as needed', 5, 10, 8000.00),
    (8, 1, 4, '1 tablet', 'once daily', 7, 7, 6000.00),
    (9, 1, 4, '1 tablet', 'once daily', 7, 7, 6000.00),
    (
        10,
        1,
        2,
        '1 capsule',
        '3 times daily',
        7,
        21,
        12000.00
    ),
    (
        11,
        1,
        1,
        '1 tablet',
        '3 times daily',
        3,
        9,
        5000.00
    ),
    (
        12,
        1,
        13,
        'apply thin layer',
        '2 times daily',
        10,
        1,
        35000.00
    ),
    (
        13,
        1,
        11,
        '1 tablet',
        '3 times daily',
        5,
        15,
        30000.00
    ),
    (
        13,
        2,
        16,
        '1 sachet',
        '2 times daily',
        3,
        6,
        3500.00
    ),
    (14, 1, 8, '2 puffs', 'as needed', 30, 1, 45000.00),
    (
        15,
        1,
        15,
        '1 tablet',
        'once daily',
        30,
        30,
        4000.00
    );

-- Deliverable Task 06: dept_engineering
-- Tables: VaccinationCourse, VaccinationRecord, NotifiableCase, ContactTrace
-- Ensures realistic data, >= 15 rows per table, respecting all schema constraints and foreign keys.
-- ============================================================
-- 1. VaccinationCourse (15 rows)
-- Constraints: course_id PK, course_name UNIQUE, required_doses >= 1
-- ============================================================
INSERT INTO
    VaccinationCourse (
        course_id,
        course_name,
        target_disease,
        required_doses
    )
VALUES
    (1, 'Hepatitis B Adult Series', 'Hepatitis B', 3),
    (
        2,
        'Tetanus, Diphtheria, Pertussis (Tdap)',
        'Tetanus, Diphtheria, Pertussis',
        1
    ),
    (
        3,
        'Seasonal Influenza Quadrivalent',
        'Influenza',
        1
    ),
    (
        4,
        'Measles, Mumps, Rubella (MMR)',
        'Measles, Mumps, Rubella',
        2
    ),
    (
        5,
        'Varicella (Chickenpox) Vaccine',
        'Varicella',
        2
    ),
    (
        6,
        'Human Papillomavirus 9-valent (HPV)',
        'Human Papillomavirus',
        3
    ),
    (
        7,
        'Pneumococcal Conjugate (PCV20)',
        'Pneumococcal Disease',
        1
    ),
    (
        8,
        'Meningococcal ACWY Conjugate',
        'Meningococcal Disease',
        1
    ),
    (
        9,
        'Inactivated Polio Vaccine (IPV)',
        'Poliomyelitis',
        4
    ),
    (
        10,
        'Rabies Pre-exposure Prophylaxis',
        'Rabies',
        3
    ),
    (
        11,
        'Typhoid Vi Polysaccharide',
        'Typhoid Fever',
        1
    ),
    (
        12,
        'Inactivated Oral Cholera Vaccine',
        'Cholera',
        2
    ),
    (
        13,
        'Japanese Encephalitis Vaccine',
        'Japanese Encephalitis',
        2
    ),
    (
        14,
        'Yellow Fever Live Attenuated',
        'Yellow Fever',
        1
    ),
    (
        15,
        'COVID-19 mRNA Updated Booster',
        'COVID-19',
        2
    );

-- ============================================================
-- 2. VaccinationRecord (16 rows)
-- Constraints: record_id PK, patient_id FK, course_id FK, dose_number >= 1,
-- UNIQUE(patient_id, course_id, dose_number)
-- Valid Patient IDs: 1-30, Valid Course IDs: 1-15
-- ============================================================
INSERT INTO
    VaccinationRecord (
        record_id,
        patient_id,
        course_id,
        dose_number,
        date_given
    )
VALUES
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
    (15, 9, 9, 1, '2025-11-15'),
    (16, 10, 15, 1, '2025-12-01');

-- ============================================================
-- 3. NotifiableCase (15 rows)
-- Constraints: case_id PK, consultation_id FK (Consultation 1..15),
-- disease_name, date_reported, building_id FK (Building 1..15)
-- Note: Consultation 13 is Dengue, 5 is Influenza, etc.
-- ============================================================
INSERT INTO
    NotifiableCase (
        case_id,
        consultation_id,
        disease_name,
        date_reported,
        building_id
    )
VALUES
    (1, 13, 'Dengue', '2026-10-07', 4),
    (2, 13, 'Dengue', '2026-10-08', 4),
    (3, 5, 'Influenza A', '2026-10-03', 5),
    (4, 5, 'Influenza A', '2026-10-04', 5),
    (5, 10, 'Pharyngitis', '2026-10-05', 6),
    (6, 3, 'Hypertension Crisis', '2026-10-02', 7),
    (7, 7, 'Acute Muscle Strain', '2026-10-04', 8),
    (8, 9, 'Viral Conjunctivitis', '2026-10-05', 9),
    (
        9,
        11,
        'Dental Caries Infection',
        '2026-10-06',
        10
    ),
    (10, 12, 'Acne Vulgaris Severe', '2026-10-06', 11),
    (
        11,
        14,
        'Bronchial Asthma Exacerbation',
        '2026-10-07',
        12
    ),
    (12, 15, 'Microcytic Anemia', '2026-10-08', 13),
    (13, 1, 'Acute Rhinopharyngitis', '2026-10-01', 14),
    (14, 2, 'Migraine with Aura', '2026-10-01', 15),
    (15, 4, 'Atopic Eczema', '2026-10-02', 4);

-- ============================================================
-- 4. ContactTrace (15 rows)
-- Constraints: trace_id PK, source_patient_id FK, contact_patient_id FK,
-- contact_date DATE, source_case_id FK NULLABLE,
-- CHECK(source_patient_id <> contact_patient_id)
-- Patient IDs: 1..30, Case IDs: 1..15
-- ============================================================
INSERT INTO
    ContactTrace (
        trace_id,
        source_patient_id,
        contact_patient_id,
        contact_date,
        source_case_id
    )
VALUES
    (1, 13, 1, '2026-10-05', 1),
    (2, 13, 2, '2026-10-06', 1),
    (3, 13, 4, '2026-10-06', 1),
    (4, 13, 7, '2026-10-07', 2),
    (5, 5, 6, '2026-10-02', 3),
    (6, 5, 8, '2026-10-02', 3),
    (7, 5, 9, '2026-10-03', 4),
    (8, 10, 11, '2026-10-04', 5),
    (9, 10, 12, '2026-10-04', 5),
    (10, 9, 14, '2026-10-04', 8),
    (11, 14, 15, '2026-10-06', 11),
    (12, 1, 3, '2026-09-30', 13),
    (13, 2, 5, '2026-09-30', 14),
    (14, 4, 16, '2026-10-01', 15),
    (15, 11, 17, '2026-10-05', NULL);

COMMIT;
