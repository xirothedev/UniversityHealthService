-- 01_schema.sql: D2 draft schema for Topic 14 University Health Service.
-- Contract: docs/er/UHS-ERD.drawio (17 tables). Decisions: ADR-17.
-- Naming: CamelCase tables (Patient, Student, ...) per D1; lower_case columns.
-- Every FK states ON DELETE and ON UPDATE. ON DELETE CASCADE only where the
-- child PK contains the parent PK (Student/Staff -> Patient,
-- PrescriptionLine -> Prescription); every other FK RESTRICTs so a booking,
-- visit, or outbreak record can never vanish with its parent. ON UPDATE CASCADE
-- on the same PK-containing FKs, RESTRICT elsewhere (surrogate keys are
-- never updated; the actions only document intent). DEFAULTs only on
-- Appointment.status and Medicine.stock_quantity.
-- Runs twice in a row without manual repair: DROP IF EXISTS first.

DROP DATABASE IF EXISTS uhs_db;

CREATE DATABASE uhs_db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE uhs_db;

-- Parents first: tables no FK depends on.

CREATE TABLE Building (
    building_id INT AUTO_INCREMENT PRIMARY KEY,
    building_name VARCHAR(100) NOT NULL,
    building_type VARCHAR(20) NOT NULL,
    CONSTRAINT uq_building_name UNIQUE (building_name),
    CONSTRAINT ck_building_type CHECK (
        building_type IN ('hall', 'dorm', 'clinic')
    )
) ENGINE = InnoDB;

CREATE TABLE Patient (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10),
    phone_number VARCHAR(20)
) ENGINE = InnoDB;

CREATE TABLE Allergy (
    allergy_id INT AUTO_INCREMENT PRIMARY KEY,
    allergen_name VARCHAR(100) NOT NULL,
    CONSTRAINT uq_allergen_name UNIQUE (allergen_name)
) ENGINE = InnoDB;

CREATE TABLE MedicalStaff (
    med_staff_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    specialty VARCHAR(100),
    phone_number VARCHAR(20)
) ENGINE = InnoDB;

CREATE TABLE Medicine (
    medicine_id INT AUTO_INCREMENT PRIMARY KEY,
    medicine_name VARCHAR(100) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    unit_price DECIMAL(10, 2) NOT NULL,
    CONSTRAINT uq_medicine_name UNIQUE (medicine_name),
    CONSTRAINT ck_stock_nonneg CHECK (stock_quantity >= 0),
    CONSTRAINT ck_price_nonneg CHECK (unit_price >= 0)
) ENGINE = InnoDB;

CREATE TABLE VaccinationCourse (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    target_disease VARCHAR(100),
    required_doses INT NOT NULL,
    CONSTRAINT uq_course_name UNIQUE (course_name),
    CONSTRAINT ck_required_doses CHECK (required_doses >= 1)
) ENGINE = InnoDB;

-- ISA children: PK is also the FK to Patient (ADR-03), CASCADE both ways.

CREATE TABLE Student (
    patient_id INT PRIMARY KEY,
    student_code VARCHAR(20) NOT NULL,
    faculty VARCHAR(100),
    dorm_building_id INT,
    CONSTRAINT uq_student_code UNIQUE (student_code),
    CONSTRAINT fk_student_patient FOREIGN KEY (patient_id)
    REFERENCES Patient (patient_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_student_dorm FOREIGN KEY (dorm_building_id)
    REFERENCES Building (building_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE Staff (
    patient_id INT PRIMARY KEY,
    uni_staff_code VARCHAR(20) NOT NULL,
    faculty VARCHAR(100),
    office_room VARCHAR(50),
    CONSTRAINT uq_uni_staff_code UNIQUE (uni_staff_code),
    CONSTRAINT fk_staff_patient FOREIGN KEY (patient_id)
    REFERENCES Patient (patient_id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

-- M-N with attributes (ADR-05, BR-12): severity lives on the pair.

CREATE TABLE PatientAllergy (
    patient_id INT NOT NULL,
    allergy_id INT NOT NULL,
    severity VARCHAR(10) NOT NULL,
    onset_date DATE,
    note TEXT,
    CONSTRAINT pk_patient_allergy PRIMARY KEY (patient_id, allergy_id),
    CONSTRAINT ck_severity CHECK (severity IN ('mild', 'moderate', 'severe')),
    CONSTRAINT fk_pa_patient FOREIGN KEY (patient_id)
    REFERENCES Patient (patient_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_pa_allergy FOREIGN KEY (allergy_id)
    REFERENCES Allergy (allergy_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB;

-- Weekly template (ADR-12, BR-04): booking_unit_min is the bookable increment
-- inside the window, not the window's length.

CREATE TABLE ConsultSlot (
    slot_id INT AUTO_INCREMENT PRIMARY KEY,
    med_staff_id INT NOT NULL,
    -- ERD/BR-04 name; WEEKDAY is non-reserved in MySQL 8.4.
    weekday TINYINT NOT NULL, -- noqa: RF04
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    booking_unit_min INT NOT NULL,
    CONSTRAINT ck_weekday CHECK (weekday BETWEEN 1 AND 7),
    CONSTRAINT ck_slot_window CHECK (end_time > start_time),
    CONSTRAINT ck_booking_unit CHECK (booking_unit_min > 0),
    CONSTRAINT fk_slot_medstaff FOREIGN KEY (med_staff_id)
    REFERENCES MedicalStaff (med_staff_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE Appointment (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    med_staff_id INT NOT NULL,
    appointment_time DATETIME NOT NULL,
    -- ERD/BR-05 name; STATUS is non-reserved in MySQL 8.4.
    status VARCHAR(20) NOT NULL DEFAULT 'scheduled', -- noqa: RF04
    CONSTRAINT ck_appt_status CHECK (
        status IN ( -- noqa: RF04
            'scheduled', 'completed', 'cancelled', 'no-show'
        )
    ),
    CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id)
    REFERENCES Patient (patient_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_appt_medstaff FOREIGN KEY (med_staff_id)
    REFERENCES MedicalStaff (med_staff_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE Consultation (
    consultation_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    med_staff_id INT NOT NULL,
    appointment_id INT,
    consultation_date DATETIME NOT NULL,
    symptoms TEXT,
    diagnosis VARCHAR(255),
    notes TEXT,
    CONSTRAINT uq_consultation_appointment UNIQUE (appointment_id),
    CONSTRAINT fk_consult_patient FOREIGN KEY (patient_id)
    REFERENCES Patient (patient_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_consult_medstaff FOREIGN KEY (med_staff_id)
    REFERENCES MedicalStaff (med_staff_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_consult_appointment FOREIGN KEY (appointment_id)
    REFERENCES Appointment (appointment_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB;

-- Dose log (BR-14): surrogate PK plus the real candidate key; the UQ triple
-- rejects a double dose_no. Deliberate deviation from the plain M-N mapping
-- used for PatientAllergy — justified in D2.

CREATE TABLE VaccinationRecord (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    course_id INT NOT NULL,
    dose_number INT NOT NULL,
    date_given DATE NOT NULL,
    CONSTRAINT uq_dose UNIQUE (patient_id, course_id, dose_number),
    CONSTRAINT ck_dose_number CHECK (dose_number >= 1),
    CONSTRAINT fk_vr_patient FOREIGN KEY (patient_id)
    REFERENCES Patient (patient_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_vr_course FOREIGN KEY (course_id)
    REFERENCES VaccinationCourse (course_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB;

-- One consultation issues at most one prescription (BR-08): UQ on the FK.

CREATE TABLE Prescription (
    prescription_id INT AUTO_INCREMENT PRIMARY KEY,
    consultation_id INT NOT NULL,
    issue_date DATE NOT NULL,
    CONSTRAINT uq_presc_consultation UNIQUE (consultation_id),
    CONSTRAINT fk_presc_consult FOREIGN KEY (consultation_id)
    REFERENCES Consultation (consultation_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB;

-- Always derived from exactly one consultation (BR-15, ADR-13); the
-- building, not the patient's dorm, drives the 14-day cluster (BR-16).

CREATE TABLE NotifiableCase (
    case_id INT AUTO_INCREMENT PRIMARY KEY,
    consultation_id INT NOT NULL,
    disease_name VARCHAR(100) NOT NULL,
    date_reported DATE NOT NULL,
    building_id INT NOT NULL,
    CONSTRAINT uq_case_consultation UNIQUE (consultation_id),
    CONSTRAINT fk_case_consult FOREIGN KEY (consultation_id)
    REFERENCES Consultation (consultation_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_case_building FOREIGN KEY (building_id)
    REFERENCES Building (building_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB;

-- Weak entity (ADR-04, BR-09): identified only inside its prescription by the
-- partial key line_no. unit_price is frozen at order time (BR-20).

CREATE TABLE PrescriptionLine (
    prescription_id INT NOT NULL,
    line_no INT NOT NULL,
    medicine_id INT NOT NULL,
    dosage VARCHAR(50),
    frequency VARCHAR(50),
    duration_days INT,
    quantity INT,
    unit_price DECIMAL(10, 2) NOT NULL,
    CONSTRAINT pk_presc_line PRIMARY KEY (prescription_id, line_no),
    CONSTRAINT ck_duration CHECK (duration_days > 0),
    CONSTRAINT ck_quantity CHECK (quantity > 0),
    CONSTRAINT fk_line_presc FOREIGN KEY (prescription_id)
    REFERENCES Prescription (prescription_id)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_line_medicine FOREIGN KEY (medicine_id)
    REFERENCES Medicine (medicine_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB;

-- Patient-to-Patient self-rel with two roles (ADR-06, BR-17).

CREATE TABLE ContactTrace (
    trace_id INT AUTO_INCREMENT PRIMARY KEY,
    source_patient_id INT NOT NULL,
    contact_patient_id INT NOT NULL,
    contact_date DATE NOT NULL,
    source_case_id INT,
    CONSTRAINT ck_no_self_trace CHECK (source_patient_id <> contact_patient_id),
    CONSTRAINT fk_trace_source FOREIGN KEY (source_patient_id)
    REFERENCES Patient (patient_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_trace_contact FOREIGN KEY (contact_patient_id)
    REFERENCES Patient (patient_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_trace_case FOREIGN KEY (source_case_id)
    REFERENCES NotifiableCase (case_id)
    ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB;
