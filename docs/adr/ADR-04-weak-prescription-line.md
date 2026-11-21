# ADR-04: Weak PrescriptionLine

## 1. Context
1 medication line cannot stand alone. Line number 3 means nothing without knowing whose order it belongs to.

## 2. Decision
PrescriptionLine is a weak entity. Key is (presc_id + line_no). Deleting the order cascades to its lines.

## 3. Dropped
Dropped giving it its own id (easier to code but loses weak-entity marks).

## 4. Consequences
Gain: matches the brief, clean master-detail demo. Loss: adding a drug requires knowing the order first.

## 5. Related
Tables Prescription, PrescriptionLine, Medicine. Form F3.
