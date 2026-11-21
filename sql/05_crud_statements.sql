-- University Health Service - Topic 14
-- 05_crud_statements.sql: one UPDATE for each main table, plus a two-table
-- transaction with COMMIT and a two-table transaction with ROLLBACK.
-- Prerequisites: 01_schema.sql and 02_data.sql. Run after the seed.
-- All identifiers are fixed values from the seed; no user value is
-- concatenated into SQL. Every statement uses standard SQL.

USE uhs_db;

-- ============================================================
-- One UPDATE per main table. Each WHERE clause names one seed row.
-- Running the file twice sets the same values again; no row is added.
-- ============================================================

UPDATE Building
SET building_name = 'Main Hall (reviewed)'
WHERE building_id = 1;

UPDATE Patient
SET phone_number = '0900000001'
WHERE patient_id = 1;

UPDATE Allergy
SET allergen_name = 'Peanuts (reviewed)'
WHERE allergy_id = 1;

UPDATE MedicalStaff
SET specialty = 'General Practice'
WHERE med_staff_id = 1;

UPDATE Medicine
SET stock_quantity = 99
WHERE medicine_id = 1;

UPDATE VaccinationCourse
SET target_disease = 'Hepatitis B'
WHERE course_id = 1;

UPDATE Student
SET faculty = 'Computer Science'
WHERE patient_id = 1;

UPDATE Staff
SET office_room = 'A101'
WHERE patient_id = 11;

UPDATE PatientAllergy
SET severity = 'moderate'
WHERE patient_id = 1 AND allergy_id = 1;

UPDATE ConsultSlot
SET booking_unit_min = 30
WHERE slot_id = 1;

UPDATE Appointment
SET status = 'completed'
WHERE appointment_id = 1;

UPDATE Consultation
SET notes = 'Reviewed in CRUD demo'
WHERE consultation_id = 1;

UPDATE Prescription
SET issue_date = '2026-10-01'
WHERE prescription_id = 1;

UPDATE PrescriptionLine
SET quantity = 10
WHERE prescription_id = 1 AND line_no = 1;

UPDATE VaccinationRecord
SET date_given = '2025-01-15'
WHERE record_id = 1;

UPDATE NotifiableCase
SET disease_name = 'Dengue'
WHERE case_id = 1;

UPDATE ContactTrace
SET contact_date = '2026-10-05'
WHERE trace_id = 1;

-- ============================================================
-- Two-table transaction A - COMMIT.
-- Both the Consultation note and the Prescription date must change
-- together. COMMIT makes both changes permanent.
-- ============================================================

START TRANSACTION;

UPDATE Consultation
SET notes = 'Committed two-table change'
WHERE consultation_id = 1;

UPDATE Prescription
SET issue_date = '2026-10-02'
WHERE prescription_id = 1;

COMMIT;

-- ============================================================
-- Two-table transaction B - ROLLBACK.
-- The second UPDATE sets a foreign key to a missing Consultation.
-- MySQL rejects it with ERROR 1452. ROLLBACK then discards the
-- first UPDATE, so neither table keeps a change.
-- A batch client must continue after the expected error for the
-- ROLLBACK to run; 04_view-and-transactions-demo.sql uses the
-- same technique with INSERT statements.
-- ============================================================

START TRANSACTION;

UPDATE Consultation
SET notes = 'This note must be discarded'
WHERE consultation_id = 2;

UPDATE Prescription
SET consultation_id = 999999
WHERE prescription_id = 2;

ROLLBACK;
