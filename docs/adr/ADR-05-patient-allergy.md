# ADR-05: PatientAllergy M-M with attributes

## 1. Context
1 person has many allergies, 1 allergy affects many people. Severity belongs to each pair, not to either side alone.

## 2. Decision
Junction table PatientAllergy: key (patient_id + allergy_id) + severity + onset_date.

## 3. Dropped
Dropped a string allergy_list column (fast but unnormalized, unqueryable).

## 4. Consequences
Gain: any who-has-what-severe question is answerable. Loss: one extra join table.

## 5. Related
Tables Patient, Allergy, PatientAllergy. Query Q6.
