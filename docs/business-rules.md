# Business rules — Topic 14 University Health Service (D1 §6)

Source: `docs/assignment.md` Topic 14 + `CONTEXT.md` + ADR-03/04/05/06/12/13/14/17.
BR-01..BR-18 ship in D1 as submitted. BR-19 was added afterwards (D2 revision) to justify the
Patient demographic columns; BR-20 was added in D2 to justify the frozen order-line price.
Log both in the D2 "Updated Information" section.
Each rule is one numbered plain-English sentence. Every rule maps to at least one ER element (see `docs/ER-checklist.md`).

## A. Who-is-who

- **BR-01.** Every Patient is exactly one of Student or Staff, never both and never neither (disjoint + total ISA).
- **BR-02.** A Student has one unique student code, a faculty, and at most one dorm Building (a day student boards nowhere); a Staff has one unique university staff code and one office.
- **BR-03.** MedicalStaff is separate from Patient; the same human may have one row in each with no link between them. Specialty and consulting hours belong to MedicalStaff only.
- **BR-04.** Each MedicalStaff member has one or more weekly ConsultSlots (weekday + start + end + booking unit, where the unit is the bookable increment inside the window rather than the window's length); a slot with no blocking Appointment is free.
- **BR-19.** Patient demographics — full name, date of birth, gender, phone — are recorded once on Patient and inherited by both subtypes; Student and Staff add only their own subtype attributes and never copy demographics.

## B. Care episode

- **BR-05.** An Appointment books exactly one Patient with exactly one MedicalStaff member at one date-time; status is one of scheduled / completed / cancelled / no-show.
- **BR-06.** At most one Consultation results from one Appointment, and a Consultation comes from at most one Appointment; walk-in Consultations without Appointment and Appointments without Consultation (no-show / cancelled) are both allowed.
- **BR-07.** A Consultation records symptoms, diagnosis (free text, consistent spelling), notes, the attending MedicalStaff member, and optionally its source Appointment.
- **BR-08.** One Consultation issues at most one Prescription; every Prescription belongs to exactly one Consultation.
- **BR-09.** A PrescriptionLine is identified only inside its Prescription by (prescription_id + line_no); deleting a Prescription deletes its lines.
- **BR-10.** Each PrescriptionLine gives exactly one Medicine with its own dose, frequency, duration in days, and quantity; durations may differ between lines of the same order.
- **BR-11.** Medicine has a price and a stock level with stock >= 0; stock is informational only and is not auto-decremented by prescribing (D2 scope).
- **BR-20.** Each PrescriptionLine freezes the Medicine unit price at order time; today's Medicine price never overwrites history.

## C. Allergy

- **BR-12.** A Patient may have many Allergies and an Allergy may affect many Patients; severity (mild / moderate / severe) plus onset date belong to each Patient–Allergy pair, with extra detail in a free-text note.

## D. Prevention

- **BR-13.** A VaccinationCourse defines how many doses are required (required_doses).
- **BR-14.** One VaccinationRecord logs one dose for one Patient in one Course (dose number + date given), unique on (patient + course + dose number); the course is complete when the distinct dose-number count >= required_doses.

## E. Outbreak

- **BR-15.** A NotifiableCase is always derived from exactly one Consultation; external lab reports enter via a walk-in Consultation first.
- **BR-16.** Each NotifiableCase records disease (free text, consistent spelling), report date, and exactly one exposure/report Building; this building — not the patient's dorm — drives the 14-day cluster query.
- **BR-17.** ContactTrace links Patient to Patient with roles source / contact plus contact date and optional source case; a source cannot trace to itself.
- **BR-18.** A Building is a hall, dorm, or clinic location; Students point at one dorm Building for admin, cases point at one Building for outbreak detection.

## Coverage check (data modeller)

- ISA disjoint/total: BR-01 (ADR-03). Weak entity: BR-09 (ADR-04). M-N with attribute: BR-12 (ADR-05). Self-rel with 2 roles: BR-17 (ADR-06).
- Columns with no BR behind them are defects. Dropped during D2 review: `Patient.email` (no query
  uses it) and `Allergy.category` (would need its own BR + CHECK enum; the pair-level `severity`
  in BR-12 already carries the useful half). Kept `Patient.gender` under BR-19. Kept
  `PrescriptionLine.unit_price` (frozen snapshot, no current price overwriting history) under BR-20.
- Required queries: history BR-07; free slots BR-04+BR-05; top-5 diagnoses BR-07; every dose BR-13+BR-14; 14-day cluster BR-16+BR-17.
- Blocking rules: scheduled + completed block a slot, cancelled + no-show free it (BR-04+BR-05, ADR-12). Double dose_no rejected by uniqueness (BR-14). Self-contact rejected (BR-17).
