# CONTEXT.md — University Health Service (UHS)

Newcomers read this file first. Use each term as defined, do not invent new ones.

## Glossary

- **Patient**: person receiving care. Every Patient is exactly one of Student or Staff, never both, never neither.
- **Student / Staff**: the 2 disjoint + complete subtypes of Patient. Student has a student ID + faculty + dorm building; Staff has a staff ID + office + faculty. **Faculty** is the one organisational-unit term, used for both subtypes; "department" is not a second unit and must not be introduced.
- **Allergy**: allergy (peanuts, penicillin...).
- **PatientAllergy**: who has which allergy + how severe. Severity lives here, not in Patient or Allergy. Severity is one of mild / moderate / severe; the onset date of the allergy also lives here; extra detail goes in a free-text note.
- **Diagnosis / Disease**: free-text words for what the doctor found (on Consultation) and the reportable illness (on NotifiableCase). No controlled list for D2; sample data uses consistent spelling so GROUP BY works.
- **MedicalStaff**: doctors/nurses employed by the service. Separate from Patient; the same human may have one row in each and the rows are not linked. Has a specialty + consulting hours expressed as ConsultSlots. The bare word "staff" is ambiguous between MedicalStaff and the Staff subtype of Patient: in any identifier, `staff_id` means MedicalStaff, and the Patient subtype is only ever identified by its staff code. The two are never joined.
- **ConsultSlot**: weekly consulting-hours template of one MedicalStaff member (weekday + start + end + slot length). Free slots are template slots with no blocking Appointment.
- **Appointment**: booking. Who + with whom + when + status (scheduled / completed / cancelled / no-show). May exist without a Consultation; at most one Consultation per Appointment. Scheduled + completed block a slot; cancelled + no-show free it.
- **Consultation**: visit. Recorded symptoms + diagnosis + free-text notes. May be a walk-in with no Appointment; at most one Appointment per Consultation.
- **Prescription**: medication order of 1 visit. Header holds consultation + date + doctor only; all drug facts live on its lines.
- **PrescriptionLine**: 1 line of an order. Meaningless without its order. Holds one Medicine + dose + frequency + duration + quantity; durations may differ per line.
- **Medicine**: drug. Has stock + price.
- **VaccinationCourse / VaccinationRecord**: course (how many doses needed) + log of doses given. One record holds patient + course + dose number + date given; uniqueness on (patient + course + dose number). Completion means distinct dose-number count >= required doses, with no interval check.
- **NotifiableCase**: reportable case (dengue...). Always derived from exactly one Consultation; records disease + report date + exposure/report building + source consultation. External reports enter via a walk-in Consultation first.
- **ContactTrace**: who contacted whom. Patient-to-Patient link with roles source / contact + contact date + optional source case. Meaningful even before the contact becomes sick.
- **Building**: hall/dorm/clinic location. A NotifiableCase points at exactly one Building; a Student dorm points at one Building for admin.

## Reading rules

- When working with the DB, also check whether any `docs/adr/` touches that area.
- Names in code/issues must match the terms above. Never call Patient Customer/User.
- For a missing term, record it for `/domain-modeling`; do not invent one yourself.
