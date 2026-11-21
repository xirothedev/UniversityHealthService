# ADR-08: Index + Transaction

**Status: superseded by ADR-16** (view + index + transaction, T18). Kept for history: the
decision to index and to transact stands, but every column name here is stale — `doctor_id`
predates ADR-14 and is retired, `slot_time` is now `appointment_time`, and `consult_date`
is now `consultation_date`. ADR-17 fixed the clinician key as `med_staff_id`. Do not
implement from this file.

## 1. Context
Free-slot lookup and top-diagnosis scan whole tables when slow. Creating a visit + its order must succeed or fail together.

## 2. Decision
Index `Appointment(doctor_id, slot_time)` for the free-slot query (now Q02) and `Consultation(diagnosis, consult_date)` for the top-diagnosis query (now Q03). Create Consultation + Prescription in one transaction with COMMIT and ROLLBACK.

## 3. Dropped
Dropped indexing every column (faster reads but slower writes, hard to justify).

## 4. Consequences
Gain: fast queries, correct numbers. Loss: the report must state which query each index serves.

## 5. Related
Tables Appointment, Consultation, Prescription. Queries Q02, Q03. Superseded by ADR-16.
