# ADR-08: Index + Transaction

## 1. Context
Free-slot lookup and top-diagnosis scan whole tables when slow. Creating a visit + its order must succeed or fail together.

## 2. Decision
Index `Appointment(doctor_id,slot_time)` for Q-T2 and `Consultation(diagnosis,consult_date)` for Q-T3. Create Consultation + Prescription in 1 transaction with COMMIT/ROLLBACK.

## 3. Dropped
Dropped indexing every column (faster reads but slower writes, hard to justify).

## 4. Consequences
Gain: fast queries, correct numbers. Loss: the report must state which query each index serves.

## 5. Related
Tables Appointment, Consultation, Prescription. Queries Q-T2, Q-T3.
