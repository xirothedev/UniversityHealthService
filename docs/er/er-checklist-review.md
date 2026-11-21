# ER Checklist Review Report (T07) — University Health Service

- **Role:** R2 - Data Modeller
- **Deliverable:** D2 - Midterm Progress Report
- **Reference:** `docs/Group02_UniversityHealthService_Midterm_Report.md` §6 and ADR-17 (§4, §5)

---

## 1. Checklist Evaluation Table

| Checklist Item | Status | Verification Detail |
|---|:---:|---|
| **>=8 entities, >=8 rels, clear singular/plural names** | **PASS** | 17 tables, 21 relationships. Naming standardizes on CamelCase entity tables and lower_case attributes matching `sql/01_schema.sql`. |
| **Weak PrescriptionLine + identifying rel + partial key line_no** | **PASS** | Mapped as weak entity identified by `Prescription(prescription_id)` with composite key `(prescription_id, line_no)` and `ON DELETE CASCADE`. |
| **M-N PatientAllergy with attrs severity/onset_date** | **PASS** | Junction table with composite PK `(patient_id, allergy_id)` containing `severity`, `onset_date`, and `note`. |
| **ISA Patient <- Student/Staff, marked disjoint/total** | **PASS** | Specialization hierarchy mapped with `patient_id` as both PK and FK cascading both ways. |
| **Self-rel Patient-contact-Patient via ContactTrace, 2 roles marked** | **PASS** | Recursive relationship mapped via `ContactTrace` with roles `source_patient_id` and `contact_patient_id`, protected by `CHECK (source_patient_id <> contact_patient_id)`. |
| **No list-like string columns, no current price overwriting historical price** | **PASS** | `PrescriptionLine.unit_price` captures the frozen historical unit price at order time (BR-20). |
| **Each table notes which mapping rule created it** | **PASS** | Standardized to `MR1..MR8` mapping taxonomy to avoid collision with team roles R1..R7. |
| **Cardinality + participation on every rel** | **PASS** | Cardinality and participation are marked on all 21 relationships. BR-21 and BR-22 now state the `attending` and `exposure site` participation minima; BR-04 now matches the `0..n` slot edge. |
| **Each business rule appears in at least 1 place on the ER** | **ACCEPTED LIMIT** | Every column has an underlying rule in `docs/business-rules.md` (BR-01..BR-22). Visual grey BR strip tags are not rendered on the `.drawio` diagram; this is recorded as a presentation limit in the D3 discussion. |

---

## 2. Alignment Audit Against `sql/01_schema.sql`

All schema drifts previously flagged have been fully resolved:
- Standardized `allergen_name` in `Allergy` (was previously mislabeled as `allergy_name`).
- Standardized `medicine_id` surrogate PK in `Medicine` and referenced FK in `PrescriptionLine`.
- Added `duration_days` to `PrescriptionLine`.
- Standardized `weekday` and `booking_unit_min` in `ConsultSlot`.
- Standardized `consultation_date` in `Consultation`.
- Corrected `NotifiableCase` classification to **MR4** (1:N, no `UNIQUE(consultation_id)` per ADR-17 §5).