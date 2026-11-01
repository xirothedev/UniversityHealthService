-- Additional synthetic rows for query demonstration only.
-- Owner of original sample data remains R4. No original row is overwritten.
-- Run only after the supplied schema/data. Reserved IDs: 800001-800104.
-- Each insert skips its ID on rerun. If an ID/name is already used for other
-- content, stop and choose a fresh reserved range; do not overwrite it.
-- The supplied original data files are preserved byte-for-byte.
USE uhs_db;
START TRANSACTION;

-- Q12: one genuinely zero-stock synthetic medicine.
INSERT INTO Medicine (
    medicine_id,
    medicine_name,
    stock_quantity,
    unit_price
)
SELECT
    800001 AS medicine_id,
    'Oral rehydration salts sachet' AS medicine_name,
    0 AS stock_quantity,
    3500.0 AS unit_price
WHERE NOT EXISTS (
    SELECT 1 FROM Medicine AS existing_row
    WHERE existing_row.medicine_id = 800001
);

-- Q2: scheduled blocks 08:00.
INSERT INTO Appointment (
    appointment_id,
    patient_id,
    med_staff_id,
    appointment_time,
    status
)
SELECT
    800001 AS appointment_id,
    16 AS patient_id,
    1 AS med_staff_id,
    '2026-10-26 08:00:00' AS appointment_time,
    'scheduled' AS appointment_status
WHERE NOT EXISTS (
    SELECT 1 FROM Appointment AS existing_row
    WHERE existing_row.appointment_id = 800001
);

-- Q2: completed blocks 08:30.
INSERT INTO Appointment (
    appointment_id,
    patient_id,
    med_staff_id,
    appointment_time,
    status
)
SELECT
    800002 AS appointment_id,
    17 AS patient_id,
    1 AS med_staff_id,
    '2026-10-26 08:30:00' AS appointment_time,
    'completed' AS appointment_status
WHERE NOT EXISTS (
    SELECT 1 FROM Appointment AS existing_row
    WHERE existing_row.appointment_id = 800002
);

-- Q2: cancelled leaves 09:00 free.
INSERT INTO Appointment (
    appointment_id,
    patient_id,
    med_staff_id,
    appointment_time,
    status
)
SELECT
    800003 AS appointment_id,
    18 AS patient_id,
    1 AS med_staff_id,
    '2026-10-26 09:00:00' AS appointment_time,
    'cancelled' AS appointment_status
WHERE NOT EXISTS (
    SELECT 1 FROM Appointment AS existing_row
    WHERE existing_row.appointment_id = 800003
);

-- Q2: no-show leaves 09:30 free.
INSERT INTO Appointment (
    appointment_id,
    patient_id,
    med_staff_id,
    appointment_time,
    status
)
SELECT
    800004 AS appointment_id,
    19 AS patient_id,
    1 AS med_staff_id,
    '2026-10-26 09:30:00' AS appointment_time,
    'no-show' AS appointment_status
WHERE NOT EXISTS (
    SELECT 1 FROM Appointment AS existing_row
    WHERE existing_row.appointment_id = 800004
);

-- Q1/Q3: walk-in without prescription and a repeated diagnosis.
INSERT INTO Consultation (
    consultation_id,
    patient_id,
    med_staff_id,
    appointment_id,
    consultation_date,
    symptoms,
    diagnosis,
    notes
)
SELECT
    800101 AS consultation_id,
    17 AS patient_id,
    1 AS med_staff_id,
    NULL AS appointment_id,
    '2026-10-20 09:00:00' AS consultation_date,
    'Cough' AS symptoms,
    'Common cold' AS diagnosis,
    'Synthetic query demonstration' AS notes
WHERE NOT EXISTS (
    SELECT 1 FROM Consultation AS existing_row
    WHERE existing_row.consultation_id = 800101
);

-- Q3: common cold now has three visits.
INSERT INTO Consultation (
    consultation_id,
    patient_id,
    med_staff_id,
    appointment_id,
    consultation_date,
    symptoms,
    diagnosis,
    notes
)
SELECT
    800102 AS consultation_id,
    18 AS patient_id,
    1 AS med_staff_id,
    NULL AS appointment_id,
    '2026-10-21 09:00:00' AS consultation_date,
    'Sore throat' AS symptoms,
    'Common cold' AS diagnosis,
    'Synthetic query demonstration' AS notes
WHERE NOT EXISTS (
    SELECT 1 FROM Consultation AS existing_row
    WHERE existing_row.consultation_id = 800102
);

-- Q5: separate patient for a same-building report.
INSERT INTO Consultation (
    consultation_id,
    patient_id,
    med_staff_id,
    appointment_id,
    consultation_date,
    symptoms,
    diagnosis,
    notes
)
SELECT
    800103 AS consultation_id,
    19 AS patient_id,
    1 AS med_staff_id,
    NULL AS appointment_id,
    '2026-10-14 09:00:00' AS consultation_date,
    'Fever' AS symptoms,
    'Dengue' AS diagnosis,
    'Synthetic query demonstration' AS notes
WHERE NOT EXISTS (
    SELECT 1 FROM Consultation AS existing_row
    WHERE existing_row.consultation_id = 800103
);

-- Q5: a second separate patient exactly fourteen days later.
INSERT INTO Consultation (
    consultation_id,
    patient_id,
    med_staff_id,
    appointment_id,
    consultation_date,
    symptoms,
    diagnosis,
    notes
)
SELECT
    800104 AS consultation_id,
    20 AS patient_id,
    1 AS med_staff_id,
    NULL AS appointment_id,
    '2026-10-28 09:00:00' AS consultation_date,
    'Fever' AS symptoms,
    'Dengue' AS diagnosis,
    'Synthetic query demonstration' AS notes
WHERE NOT EXISTS (
    SELECT 1 FROM Consultation AS existing_row
    WHERE existing_row.consultation_id = 800104
);

-- Q10: patient 16 has vaccination but no consultation.
INSERT INTO VaccinationRecord (
    record_id,
    patient_id,
    course_id,
    dose_number,
    date_given
)
SELECT
    800001 AS record_id,
    16 AS patient_id,
    2 AS course_id,
    1 AS dose_number,
    '2026-10-20' AS date_given
WHERE NOT EXISTS (
    SELECT 1 FROM VaccinationRecord AS existing_row
    WHERE existing_row.record_id = 800001
);

-- Q5: report linked to consultation 800103.
INSERT INTO NotifiableCase (
    case_id,
    consultation_id,
    disease_name,
    date_reported,
    building_id
)
SELECT
    800001 AS case_id,
    800103 AS consultation_id,
    'Dengue' AS disease_name,
    '2026-10-14' AS date_reported,
    4 AS building_id
WHERE NOT EXISTS (
    SELECT 1 FROM NotifiableCase AS existing_row
    WHERE existing_row.case_id = 800001
);

-- Q5: fourteen-day boundary with case 800001.
INSERT INTO NotifiableCase (
    case_id,
    consultation_id,
    disease_name,
    date_reported,
    building_id
)
SELECT
    800002 AS case_id,
    800104 AS consultation_id,
    'Dengue' AS disease_name,
    '2026-10-28' AS date_reported,
    4 AS building_id
WHERE NOT EXISTS (
    SELECT 1 FROM NotifiableCase AS existing_row
    WHERE existing_row.case_id = 800002
);

COMMIT;
