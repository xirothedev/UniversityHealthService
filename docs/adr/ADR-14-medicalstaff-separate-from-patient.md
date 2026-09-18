# ADR-14: MedicalStaff separate from Patient ISA

## 1. Context
Doctors and nurses can also get sick and visit the service. The brief lists registered students/staff and medical staff as separate groups, and consulting hours do not belong on a patient.

## 2. Decision
MedicalStaff is its own entity, outside the Patient Student/Staff ISA. The same human may hold one MedicalStaff row and one Patient row with no DB link between them.

## 3. Dropped
Dropped putting MedicalStaff inside the Patient ISA (one hierarchy but forces specialty and hours onto patients, muddies the disjointness rule).

## 4. Consequences
Gain: clean ISA, hours stay with staff only. Loss: the app must look in two places to show one human's both roles.

## 5. Related
Tables Patient, Student, Staff, MedicalStaff, ConsultSlot.
