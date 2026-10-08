# ER Checklist Review Report (T07) — University Health Service

- **Role:** R2 - Data Modeller
- **Deliverable:** D2 - Midterm Progress Report
- **Reference:** `docs/ER-checklist.md` and ADR-17 (§4, §5)

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
| **Cardinality + participation on every rel** | **UNTICKED (UNDER REVIEW)** | **Honest Gap per ADR-17 (§4):** Three participation minima currently lack clear business rules and remain under review: `attending` (MedicalStaff >= 1 Consultation), `exposure site` (Building >= 1 NotifiableCase), and BR-04 slot edge (`0..n` vs wording). |
| **Each business rule appears in at least 1 place on the ER** | **UNTICKED (UNDER REVIEW)** | **Honest Gap per ADR-17 (§4):** Every column has an underlying rule in `docs/business-rules.md`, but visual grey BR strip tags are pending final render onto the `.drawio` diagram. |

---

## 2. Alignment Audit Against `sql/01_schema.sql`

All schema drifts previously flagged have been fully resolved:
- Standardized `allergen_name` in `Allergy` (was previously mislabeled as `allergy_name`).
- Standardized `medicine_id` surrogate PK in `Medicine` and referenced FK in `PrescriptionLine`.
- Added `duration_days` to `PrescriptionLine`.
- Standardized `weekday` and `booking_unit_min` in `ConsultSlot`.
- Standardized `consultation_date` in `Consultation`.
- Corrected `NotifiableCase` classification to **MR4** (1:N, no `UNIQUE(consultation_id)` per ADR-17 §5).