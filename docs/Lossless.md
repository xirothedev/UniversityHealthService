### **2.1 Definition**

Lossless decomposition means that after a relation is divided into smaller relations, joining them can reconstruct the original relation without losing information or creating extra records.

For a decomposition into two relations R1 and R2, the common attributes must determine all attributes of at least one relation. In other words, either `(R1 ∩ R2) → R1` or `(R1 ∩ R2) → R2` must hold.

### **2.2 Example 1: Patient and Student**

The `Patient` and `Student` tables share the attribute `patient_id`.

* `Patient`: `patient_id` is the primary key.  
* `Student`: `patient_id` is also the primary key and a foreign key referencing `Patient`.

Since `patient_id` determines all attributes in both tables, joining the two tables using `patient_id` can reconstruct the combined information without creating duplicate matches.

Therefore, this decomposition satisfies the lossless-join condition.

### **2.3 Example 2: Patient and Staff**

The `Patient` and `Staff` tables also share `patient_id`.

In both tables, `patient_id` is the primary key. Therefore, it determines all attributes in each table.

When the two tables are joined using `patient_id`, each staff record matches at most one patient record. Thus, the decomposition satisfies the lossless-join condition.

### **2.4 Example 3: Patient and PatientAllergy**

The `PatientAllergy` table stores the relationship between patients and allergies. Its primary key is `(patient_id, allergy_id)`.

The common attribute between `Patient` and `PatientAllergy` is `patient_id`. Since `patient_id` is the primary key of `Patient`, it determines all attributes in that table.

Therefore, joining `Patient` and `PatientAllergy` using `patient_id` satisfies the lossless-join condition for this relationship.

### **2.5 Example 4: Prescription and PrescriptionLine**

The `Prescription` and `PrescriptionLine` tables share `prescription_id`.

In `Prescription`, `prescription_id` is the primary key. In `PrescriptionLine`, it is part of the composite primary key `(prescription_id, line_no)`.

The attribute `prescription_id` determines all attributes in `Prescription`. Each prescription line refers to one prescription, so joining the tables using `prescription_id` satisfies the lossless-join condition.

### **2.6 Other Relationships**

The same reasoning can be applied to other parent-child relationships in the schema, such as:

* `Appointment` and `Patient`  
* `Appointment` and `MedicalStaff`  
* `ConsultSlot` and `MedicalStaff`  
* `VaccinationRecord` and `VaccinationCourse`  
* `PrescriptionLine` and `Medicine`  
* `NotifiableCase` and `Building`

In these cases, the foreign key references the primary key of the parent table. This means each non-null foreign-key value can match at most one parent record.

Some relationships are optional. For example, `Consultation.appointment_id` and `ContactTrace.source_case_id` can be NULL. When reconstructing all records, these rows must also be preserved because an ordinary inner join would exclude rows with NULL references.

