# CONTEXT.md — University Health Service (UHS)

Newcomers read this file first. Use each term as defined, do not invent new ones.

## Glossary

- **Patient**: person receiving care. Two disjoint subtypes.
- **Student / Staff**: the 2 subtypes of Patient. Student has a student ID, Staff has a staff ID.
- **Allergy**: allergy (peanuts, penicillin...).
- **PatientAllergy**: who has which allergy + how severe. Severity lives here, not in Patient or Allergy.
- **MedicalStaff**: doctors/nurses. Have a specialty + consulting hours.
- **Appointment**: booking. Who + with whom + when + outcome.
- **Consultation**: visit. Recorded symptoms + diagnosis.
- **Prescription**: medication order of 1 visit.
- **PrescriptionLine**: 1 line of an order. Meaningless without its order.
- **Medicine**: drug. Has stock + price.
- **VaccinationCourse / VaccinationRecord**: course (how many doses needed) + log of doses given.
- **NotifiableCase**: reportable case (dengue...). Records disease + report date + building.
- **ContactTrace**: who contacted whom. Used to find outbreak clusters.
- **Building**: hall/dorm. Used for 14-day cluster tracing.

## Reading rules

- When working with the DB, also check whether any `docs/adr/` touches that area.
- Names in code/issues must match the terms above. Never call Patient Customer/User.
- For a missing term, record it for `/domain-modeling`; do not invent one yourself.
