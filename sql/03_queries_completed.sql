-- University Health Service - Group 02 - Topic 14
-- T16: Nguyen Hoang Thien Tu (ITDSIU25050), Q1-Q10 and Q12.
-- Q11 reuses Le Thanh Trung's T18 view; original T18 is archived unchanged.
-- Read assignment.md section 2.2, 2.3, 2.5 and Topic 14.
-- Prerequisites: 01_schema.sql, 02_data.sql, 02b_query_demo_supplement.sql.
-- This script returns exactly twelve result sets. CREATE VIEW and SET are
-- setup statements, not additional result queries.
-- Standalone rerunnable query script. It does not contain the T18 intentional
-- FK-error demo, index creation, or table-reset statements.
-- The view SELECT is unchanged; OR REPLACE makes setup rerunnable.
-- Preserve the original T18 file for its separate demonstration.

USE uhs_db;

-- Fixed demonstration inputs make results reproducible after submission.
-- Live Q2: replace the next value with CURRENT_TIMESTAMP.
-- All date-times are interpreted as campus local time.
SET @as_of_datetime = CAST('2026-10-26 00:00:00' AS DATETIME);
SET @week_start = DATE_SUB(
    DATE(@as_of_datetime), INTERVAL WEEKDAY(@as_of_datetime) DAY
);
-- These semester dates are demonstration bounds, not a verified calendar.
-- Start inclusive, end exclusive. Replace both with the actual semester.
SET @semester_start = CAST('2026-09-01' AS DATE);
SET @semester_end = CAST('2027-02-01' AS DATE);
SET @patient_id = 1;
SET @course_id = 1;
SET @medicine_id = 1;
SET @disease_name = 'Dengue';
SET @min_diagnosis_count = 1;
SET @min_severe_allergies = 1;

-- Shared T18 view setup
CREATE OR REPLACE VIEW v_patient_summary AS
SELECT
    p.patient_id,
    p.full_name,
    p.date_of_birth,
    p.phone_number,
    'STUDENT' AS subtype,
    s.student_code AS subtype_code,
    s.faculty
FROM Patient AS p
INNER JOIN Student AS s ON p.patient_id = s.patient_id
UNION ALL
SELECT
    p.patient_id,
    p.full_name,
    p.date_of_birth,
    p.phone_number,
    'STAFF' AS subtype,
    st.uni_staff_code AS subtype_code,
    st.faculty
FROM Patient AS p
INNER JOIN Staff AS st ON p.patient_id = st.patient_id;

-- Q01 BEGIN
-- What is the consultation history of a selected patient, including the
-- attending clinician and any prescribed medicines?
-- One row per prescription line. LEFT JOIN retains visits without
-- prescriptions or lines. The historical price comes from PrescriptionLine,
-- not the current Medicine price.
SELECT
    c.consultation_id,
    c.consultation_date,
    p.patient_id,
    p.full_name AS patient_name,
    ms.full_name AS clinician_name,
    c.symptoms,
    c.diagnosis,
    c.notes AS consultation_notes,
    pr.prescription_id,
    pr.issue_date,
    pl.line_no,
    m.medicine_name,
    pl.dosage,
    pl.frequency,
    pl.duration_days,
    pl.quantity,
    pl.unit_price AS prescribed_unit_price
FROM Consultation AS c
INNER JOIN Patient AS p
    ON c.patient_id = p.patient_id
INNER JOIN MedicalStaff AS ms
    ON c.med_staff_id = ms.med_staff_id
LEFT JOIN Prescription AS pr
    ON c.consultation_id = pr.consultation_id
LEFT JOIN PrescriptionLine AS pl
    ON pr.prescription_id = pl.prescription_id
LEFT JOIN Medicine AS m
    ON pl.medicine_id = m.medicine_id
WHERE c.patient_id = @patient_id
ORDER BY c.consultation_date DESC, c.consultation_id DESC, pl.line_no ASC;
-- Q01 END

-- Q02 BEGIN
-- Which clinicians have unbooked appointment intervals remaining in the week
-- containing the reference date?
-- MySQL recursive CTE. weekday 1=Monday through 7=Sunday. Each appointment is
-- assumed to last one matching booking unit because duration is absent from
-- the schema. Half-open intervals [start,end) overlap; touching endpoints do
-- not. MedicalStaff has no doctor/nurse role flag. The result lists
-- clinicians, all labelled Dr. in the seed. Templates are same-day only;
-- recursion requires fewer than 1000 increments per window under the default
-- MySQL limit.
WITH RECURSIVE slot_grid AS (
    SELECT
        cs.slot_id,
        cs.med_staff_id,
        cs.booking_unit_min,
        TIMESTAMP(
            DATE_ADD(@week_start, INTERVAL (cs.weekday - 1) DAY),
            cs.start_time
        ) AS slot_start,
        TIMESTAMP(
            DATE_ADD(@week_start, INTERVAL (cs.weekday - 1) DAY),
            cs.end_time
        ) AS window_end
    FROM ConsultSlot AS cs
    WHERE
        TIME_TO_SEC(cs.end_time) - TIME_TO_SEC(cs.start_time)
        >= cs.booking_unit_min * 60
    UNION ALL
    SELECT
        sg.slot_id,
        sg.med_staff_id,
        sg.booking_unit_min,
        TIMESTAMPADD(MINUTE, sg.booking_unit_min, sg.slot_start) AS slot_start,
        sg.window_end
    FROM slot_grid AS sg
    WHERE
        TIMESTAMPADD(MINUTE, 2 * sg.booking_unit_min, sg.slot_start)
        <= sg.window_end
)

SELECT DISTINCT
    ms.med_staff_id,
    ms.full_name AS clinician_name,
    sg.slot_start,
    TIMESTAMPADD(MINUTE, sg.booking_unit_min, sg.slot_start) AS slot_end
FROM slot_grid AS sg
INNER JOIN MedicalStaff AS ms ON sg.med_staff_id = ms.med_staff_id
LEFT JOIN Appointment AS ap
    ON
        sg.med_staff_id = ap.med_staff_id
        AND ap.status IN ('scheduled', 'completed')
        AND ap.appointment_time
        < TIMESTAMPADD(MINUTE, sg.booking_unit_min, sg.slot_start)
        AND TIMESTAMPADD(MINUTE, sg.booking_unit_min, ap.appointment_time)
        > sg.slot_start
WHERE
    ap.appointment_id IS NULL
    AND sg.slot_start >= @as_of_datetime
ORDER BY sg.slot_start, ms.med_staff_id;
-- Q02 END

-- Q03 BEGIN
-- What are the five most frequent diagnoses in the selected semester?
-- The default minimum count is 1, so the five most frequent nonblank diagnoses
-- are eligible. A higher value intentionally changes the report to a minimum-
-- frequency report. Ties are broken alphabetically; LIMIT 5 returns at most
-- five rows, not all tied diagnoses.
SELECT
    c.diagnosis,
    COUNT(*) AS consultation_count
FROM Consultation AS c
WHERE
    c.consultation_date >= @semester_start
    AND c.consultation_date < @semester_end
    AND c.diagnosis IS NOT NULL
    AND TRIM(c.diagnosis) <> ''
GROUP BY c.diagnosis
HAVING COUNT(*) >= @min_diagnosis_count
ORDER BY consultation_count DESC, c.diagnosis ASC
LIMIT 5;
-- Q03 END

-- Q04 BEGIN
-- Which patients have completed the selected vaccination course under the
-- group's distinct-dose-count rule?
-- Proposal BR-14 and CONTEXT define completion as distinct recorded dose
-- count >= required_doses; neither label continuity nor intervals are checked.
-- Rank distinct recorded dose labels into positions 1..K. For-all means there
-- is no required position in 1..N without a received position. Thus labels
-- 1, 2, 99 satisfy a three-dose course under this agreed project rule.
-- Seed courses have at most four required doses.
WITH RECURSIVE required_positions AS (
    SELECT
        vc.course_id,
        vc.required_doses,
        1 AS dose_position
    FROM VaccinationCourse AS vc
    WHERE vc.course_id = @course_id
    UNION ALL
    SELECT
        rp.course_id,
        rp.required_doses,
        rp.dose_position + 1 AS dose_position
    FROM required_positions AS rp
    WHERE rp.dose_position < rp.required_doses
),

distinct_received_doses AS (
    SELECT DISTINCT
        vr.patient_id,
        vr.course_id,
        vr.dose_number
    FROM VaccinationRecord AS vr
    WHERE vr.course_id = @course_id
),

received_positions AS (
    SELECT
        drd.patient_id,
        drd.course_id,
        ROW_NUMBER() OVER (
            PARTITION BY drd.patient_id, drd.course_id
            ORDER BY drd.dose_number
        ) AS dose_position
    FROM distinct_received_doses AS drd
)

SELECT
    p.patient_id,
    p.full_name AS patient_name,
    vc.course_id,
    vc.course_name,
    vc.required_doses
FROM Patient AS p
CROSS JOIN VaccinationCourse AS vc
WHERE
    vc.course_id = @course_id
    AND NOT EXISTS (
        SELECT 1
        FROM required_positions AS rp
        WHERE
            rp.course_id = vc.course_id
            AND NOT EXISTS (
                SELECT 1
                FROM received_positions AS rec
                WHERE
                    rec.patient_id = p.patient_id
                    AND rec.course_id = rp.course_id
                    AND rec.dose_position = rp.dose_position
            )
    )
ORDER BY p.patient_id;
-- Q04 END

-- Q05 BEGIN
-- Which pairs of reports of a selected disease occurred in the same building
-- no more than fourteen days apart?
-- Each unordered pair appears once. The boundary includes elapsed date
-- differences 0 through 14 days, not exactly fourteen distinct calendar dates.
-- Results are candidate report pairs, not proof of transmission or a confirmed
-- outbreak. Multiple reports from one patient remain visible and are labelled
-- by distinct_patient_count. Exposure building comes from NotifiableCase, not
-- Student.dorm_building_id.
SELECT
    b.building_id,
    b.building_name,
    nc1.disease_name,
    nc1.case_id AS case_a,
    nc2.case_id AS case_b,
    nc1.consultation_id AS consultation_a,
    nc2.consultation_id AS consultation_b,
    c1.patient_id AS patient_a,
    c2.patient_id AS patient_b,
    nc1.date_reported AS reported_a,
    nc2.date_reported AS reported_b,
    ABS(DATEDIFF(nc1.date_reported, nc2.date_reported)) AS days_apart,
    CASE WHEN c1.patient_id = c2.patient_id THEN 1 ELSE 2 END
        AS distinct_patient_count
FROM NotifiableCase AS nc1
INNER JOIN NotifiableCase AS nc2
    ON
        nc1.case_id < nc2.case_id
        AND nc1.building_id = nc2.building_id
        AND nc1.disease_name = nc2.disease_name
INNER JOIN Building AS b ON nc1.building_id = b.building_id
INNER JOIN Consultation AS c1
    ON nc1.consultation_id = c1.consultation_id
INNER JOIN Consultation AS c2
    ON nc2.consultation_id = c2.consultation_id
WHERE
    nc1.disease_name = @disease_name
    AND ABS(DATEDIFF(nc1.date_reported, nc2.date_reported)) <= 14
ORDER BY b.building_id, nc1.case_id, nc2.case_id;
-- Q05 END

-- Q06 BEGIN
-- Which allergies are recorded for each patient, and what is the severity of
-- each association?
-- The composite key (patient_id,allergy_id) makes each returned association
-- unique. This query is translated in T17 RA1.
SELECT
    p.patient_id,
    p.full_name AS patient_name,
    al.allergy_id,
    al.allergen_name,
    pa.severity
FROM Patient AS p
INNER JOIN PatientAllergy AS pa ON p.patient_id = pa.patient_id
INNER JOIN Allergy AS al ON pa.allergy_id = al.allergy_id
ORDER BY p.patient_id, al.allergy_id;
-- Q06 END

-- Q07 BEGIN
-- Which medical staff members have never had any appointment recorded?
-- Any recorded appointment counts, including cancelled and no-show. This is
-- not the same as currently having free slots. T17 RA2 uses set difference.
SELECT
    ms.med_staff_id,
    ms.full_name AS clinician_name,
    ms.specialty
FROM MedicalStaff AS ms
WHERE
    NOT EXISTS (
        SELECT 1
        FROM Appointment AS ap
        WHERE ap.med_staff_id = ms.med_staff_id
    )
ORDER BY ms.med_staff_id;
-- Q07 END

-- Q08 BEGIN
-- Which patients have at least the selected number of recorded severe
-- allergies?
-- The default threshold is one recorded severe allergy. This operational flag
-- is not a clinical risk model. WHERE filters associations before grouping;
-- HAVING filters patients after counting.
SELECT
    p.patient_id,
    p.full_name AS patient_name,
    COUNT(*) AS severe_allergy_count
FROM Patient AS p
INNER JOIN PatientAllergy AS pa ON p.patient_id = pa.patient_id
WHERE pa.severity = 'severe'
GROUP BY p.patient_id, p.full_name
HAVING COUNT(*) >= @min_severe_allergies
ORDER BY severe_allergy_count DESC, p.patient_id ASC;
-- Q08 END

-- Q09 BEGIN
-- Which patients have been prescribed a selected medicine at least once?
-- EXISTS returns each patient once even when several prescriptions contain the
-- medicine. T17 RA3 uses selection, joins and projection.
SELECT
    p.patient_id,
    p.full_name AS patient_name
FROM Patient AS p
WHERE
    EXISTS (
        SELECT 1
        FROM Consultation AS c
        INNER JOIN Prescription AS pr
            ON c.consultation_id = pr.consultation_id
        INNER JOIN PrescriptionLine AS pl
            ON pr.prescription_id = pl.prescription_id
        WHERE
            c.patient_id = p.patient_id
            AND pl.medicine_id = @medicine_id
    )
ORDER BY p.patient_id;
-- Q09 END

-- Q10 BEGIN
-- Which patients have at least one consultation record or at least one
-- vaccination record?
-- UNION removes duplicates within and across branches. Both branches project
-- the same columns and compatible types. T17 RA4 uses union.
SELECT
    p.patient_id,
    p.full_name AS patient_name
FROM Patient AS p
INNER JOIN Consultation AS c ON p.patient_id = c.patient_id
UNION DISTINCT
SELECT
    p.patient_id,
    p.full_name
FROM Patient AS p
INNER JOIN VaccinationRecord AS vr ON p.patient_id = vr.patient_id
ORDER BY patient_id;
-- Q10 END

-- Q11 BEGIN
-- What patient demographics and registration subtype details are provided by
-- the shared patient summary view?
-- Shared integration query. The view SELECT definition belongs to Le Thanh
-- Trung / T18 and is reused unchanged. T16 does not claim authorship of the
-- original view. The registered subtypes must remain disjoint and total.
SELECT
    vps.patient_id,
    vps.full_name,
    vps.date_of_birth,
    vps.phone_number,
    vps.subtype,
    vps.subtype_code,
    vps.faculty
FROM v_patient_summary AS vps
ORDER BY vps.patient_id;
-- Q11 END

-- Q12 BEGIN
-- Which medicines are currently out of stock?
-- Zero stock means out of stock. The original seed has no such row, so the
-- separate disclosed demo supplement supplies one. Do not weaken the predicate
-- to make a result appear.
SELECT
    m.medicine_id,
    m.medicine_name,
    m.stock_quantity
FROM Medicine AS m
WHERE m.stock_quantity = 0
ORDER BY m.medicine_id;
-- Q12 END
