# ADR-03: ISA Patient in 3 tables

## 1. Context
Patients are students and staff. Shared name, birth date, phone. Separate IDs, faculty/office.

## 2. Decision
3 tables: Patient (parent) + Student + Staff (children). Each child PK is also an FK to the parent; deleting the parent cascades to the child.

## 3. Dropped
Dropped a single merged table (fewer tables but many null columns, loses ISA marks).

## 4. Consequences
Gain: matches the brief, explicit. Loss: more joins to fetch full info.

## 5. Related
Tables Patient, Student, Staff. Queries Q-T1, Q6.
