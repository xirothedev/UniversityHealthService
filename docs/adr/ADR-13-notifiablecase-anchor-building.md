# ADR-13: NotifiableCase anchored to Consultation, carries building

## 1. Context
Outbreak detection asks for cases of one disease in the same building within fourteen days. The case must point at where the patient was exposed and at the visit that diagnosed it, not at the patient's dorm.

## 2. Decision
Every NotifiableCase has a mandatory FK to exactly one Consultation plus disease + report date + building_id. External lab reports enter through a walk-in Consultation first. The building on the case drives the 14-day cluster query.

## 3. Dropped
Dropped an independent case report with only patient + disease + date (allows cases with no traceable visit, splits the outbreak trail).

## 4. Consequences
Gain: every case is traceable to symptoms and findable by building and date. Loss: one extra walk-in row for externally reported cases.

## 5. Related
Tables NotifiableCase, Consultation, Building, ContactTrace. Query Q-T5 (same building within 14 days).
