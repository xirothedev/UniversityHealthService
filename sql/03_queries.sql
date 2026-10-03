-- 03_queries.sql: owned by R5. Each statement has an English
-- comment above it. 12 statements, each returning non-empty results.
--
-- Line-range split inside R5: T16 owns Q1-Q10 and Q12; T18 owns
-- the three blocks marked "T18". See docs/adr/ADR-16.
--
-- Column contract: docs/er/UHS-ERD.drawio via sql/01_schema.sql
-- (med_staff_id, uni_staff_code, booking_unit_min — ADR-17).
--
-- DDL note (T18): CREATE VIEW and CREATE INDEX have no IF NOT
-- EXISTS in MySQL 8.4, so those two statements fail if the view
-- or the index already exists. Running this file a second time
-- therefore needs the view and index dropped first; the DML half
-- below is idempotent on its own, because the rollback demo leaves
-- nothing behind and the commit demo deletes its own rows.
-- Normal order (01_schema.sql drops and recreates the database,
-- then 02_data.sql, then this file) satisfies this already.

-- Q-T1 history 1 Patient (multi-join)
-- Q-T2 free slots (outer join)
-- Q-T3 top-5 diagnosis (GROUP BY/HAVING)
-- Q-T4 full-dose (NOT EXISTS for-all)
-- Q-T5 outbreak same building 14d (self-join)
-- Q6 allergy join | Q7 doctors never booked
-- Q8 high-risk HAVING | Q9 EXISTS nested | Q10 set op
-- Q11 view v_patient_summary | Q12 out-of-stock check


-- ============================================================
-- T18 / 1 of 3 -- the view. This is Q11, one of the twelve.
-- ============================================================

-- Q11. For every Patient, show their demographics, whether they
-- registered as a Student or as Staff, that subtype's code, and
-- their faculty. UNION ALL (not UNION) reaches both subtypes: the
-- ISA is disjoint and total, so each Patient appears exactly once.
-- MedicalStaff is deliberately not joined (CONTEXT.md).

CREATE VIEW v_patient_summary AS
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


-- ============================================================
-- T18 / 2 of 3 -- the index. DDL, not a query: it exists to serve
-- Q-T2 (doctors with free Appointment slots this week), which
-- seeks med_staff_id for equality then narrows on appointment_time.
-- ============================================================

-- DDL: one composite index serving Q-T2.
CREATE INDEX idx_appt_medstaff_time
ON Appointment (med_staff_id, appointment_time);


-- ============================================================
-- T18 / 3 of 3 -- two transactions over Consultation +
-- Prescription. Ids in the 9000 block keep clear of 02_data.sql.
-- Rollback demo runs first so the committed rows are the last
-- thing written and the cleanup is unambiguous.
-- ============================================================

-- Transaction A -- ROLLBACK. How many patients have visited and
-- received a Prescription? (Answer: none -- the visit below is
-- discarded when the order fails to write.)

START TRANSACTION;

-- patient_id 1 and med_staff_id 1 come from 02_data.sql; the demo
-- needs those parent rows the same way the old staff_id = 1 did.
INSERT INTO Consultation (
    consultation_id,
    patient_id,
    med_staff_id,
    consultation_date,
    symptoms,
    diagnosis,
    notes
) VALUES (
    9001, 1, 1, '2026-11-02',
    'Persistent cough',
    'Acute bronchitis',
    'T18 rollback demo'
);

-- consultation_id 999999 has no matching Consultation row, so this
-- fails on the Prescription -> Consultation foreign key (constraint
-- fk_presc_consult). The Prescription carries no doctor column by
-- design (ADR-17): the prescriber is read through the visit, so a
-- wrong-doctor failure is inexpressible and the trip-wire is the
-- diagram's own 1:1 constraint. The expected error is:
--   ERROR 1452 (23000): Cannot add or update a child row: a
--   foreign key constraint fails
-- That failure is the point of the demo: the statement is
-- rejected, the transaction is still open, and the ROLLBACK
-- below must then discard consultation_id 9001.
INSERT INTO Prescription (
    prescription_id,
    consultation_id,
    issue_date
) VALUES (
    9002, 999999, '2026-11-02'
);

ROLLBACK;  -- consultation_id 9001 is gone; the two tables moved together


-- Transaction B -- COMMIT. How many patients have visited and
-- received a Prescription? (Answer: the one inserted below, plus
-- everyone already in 02_data.sql.)

START TRANSACTION;

INSERT INTO Consultation (
    consultation_id,
    patient_id,
    med_staff_id,
    consultation_date,
    symptoms,
    diagnosis,
    notes
) VALUES (
    9003, 1, 1, '2026-11-02',
    'Headache, photophobia',
    'Migraine',
    'T18 commit demo'
);

INSERT INTO Prescription (
    prescription_id,
    consultation_id,
    issue_date
) VALUES (
    9004, 9003, '2026-11-02'
);

COMMIT;

-- Cleanup, so this file runs twice in a row without manual repair.
-- Child first: Prescription references Consultation.
DELETE FROM Prescription
WHERE prescription_id = 9004;

DELETE FROM Consultation
WHERE consultation_id = 9003;
