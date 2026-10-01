<!-- GOOGLE DOCS CONVERSION INSTRUCTION: Format this exact content as a three-page-or-fewer A4 proposal, including the cover. The final output must not spill onto a fourth page. Use 0.5-inch margins, 9-point body text, 7.5-point table text, compact paragraph spacing, and a real page break after Section 1. Keep Sections 7–9 together on the final page when possible. Do not add, remove, or substantively rewrite content. -->

# Project Proposal

## University Health Service

**Course:** IT079IU – Principles of Database Management  
**Project topic:** Topic 14 — University Health Service  
**Group:** 2  
**Instructor:** Assoc. Prof. Dr. Nguyen Thi Thuy Loan  
**Semester:** 1, Academic Year 2026–2027  
**Submission deadline:** 27 September 2026, 22:30

## 1. Cover Page

| No. | Member | Student ID | Phone | Topic |
|---:|---|---|---|---|
| 1 | Lê Thành Trung | ITCSIU25055 | 0359186887 | 14 — University Health Service |
| 2 | Nguyễn Trần Quốc Vinh | MAMAIU25041 | 0837991399 | 14 — University Health Service |
| 3 | Phan Nguyên Vũ | ITITIU25047 | 0945719666 | 14 — University Health Service |
| 4 | Nguyễn Hoàng Thiên Tứ | ITDSIU25050 | 0374749542 | 14 — University Health Service |
| 5 | Nguyễn Quốc Duy Lộc | ITITIU25018 | 0906648951 | 14 — University Health Service |
| 6 | Trần Pham Quốc Nam | ITCSIU25037 | 0375314749 | 14 — University Health Service |
| 7 | Lê Đức Phát | ITCSIU25042 | 0767393239 | 14 — University Health Service |
| 8 | Nguyễn Minh Khôi | SESEIU24013 | 0902305920 | 14 — University Health Service |
| 9 | Lê Thảo My | SESEIU23018 | 0835931468 | 14 — University Health Service |
| 10 | Nguyễn Trần Anh Tuấn | SESEIU24030 | 0972235718 | 14 — University Health Service |
| 11 | Nguyễn Trọng Đức | ITITIU25003 | 0775327007 | 14 — University Health Service |
| 12 | Nguyễn Hiền Minh | ITITWE25011 | 0919150220 | 14 — University Health Service |

<div style="break-after: page; page-break-after: always;"></div>

## 2. Abstract

The project will develop a normalized relational database and Java desktop application through which health-service staff can retrieve medical records for registered students and staff patients, manage appointments, consultations, prescriptions, and vaccinations, and investigate disease clusters. Query-based surveillance will identify cases of the same disease reported in the same building within fourteen days and maintain patient-to-patient contact traces for follow-up. The design is informed by WHO early-warning guidance and a case study of integrating student health services with a health-system electronic health record [1], [2]. A 17-table model, integrity constraints, prepared JDBC statements, and five required queries will support the prototype. It will use synthetic data; production access control, clinical deployment, and real-time alerts are outside scope.

## 3. Task Allocation Table

Live allocation sheet: <https://docs.google.com/spreadsheets/d/1aK9sBoS4vLbmFEiUzMBYHttklbzfKpBDz51cXHGFQ4M/edit?usp=sharing>

| Role | Confirmed members | W3 | W5 | W10 | W13–15 |
|---|---|---|---|---|---|
| R1 — Group leader | Nguyễn Hiền Minh | Coordinate proposal, scope, schedule, and group communication | Review requirements and track D2 deliverables | Integrate D3, D5, and D6; resolve schedule risks | Lead rehearsals, demonstration, submission, and responses |
| R2 — Data modeller | Nguyễn Quốc Duy Lộc; Nguyễn Trọng Đức | Draft business rules, entities, relationships, and ER model | Complete ER diagram, cardinalities, participation, and schema mapping | Refine the model and support schema/query implementation | Validate the model, explain design decisions, and support the demonstration |
| R3 — Normalization lead | Lê Thảo My; Nguyễn Trần Anh Tuấn | Identify functional dependencies, derive candidate keys, and derive a minimal cover | Complete 3NF/BCNF justification | Finalize normalization and decomposition evidence | Check proofs, resolve revisions, and defend the analysis |
| R4 — Database developer | Trần Pham Quốc Nam; Lê Đức Phát | Plan tables, constraints, sample data, and implementation order | Draft DDL, data requirements, and integrity checks | Deliver rerunnable schema/data scripts, transaction, and justified indexes | Rerun scripts, repair defects, and prepare the demonstration database |
| R5 — Query developer | Lê Thành Trung; Nguyễn Hoàng Thiên Tứ | Define English questions and expected results for the five required queries | Prototype joins, aggregation, nested queries, and date/building filters | Complete at least 12 non-empty queries, including a view and every required query form; provide four relational-algebra versions | Verify results, optimise presentation queries, and support demonstrations |
| R6 — Java developer | Nguyễn Trần Quốc Vinh; Phan Nguyên Vũ | Define the Swing screens, DAO boundary, validation, and error-message plan | Prototype browse and parameterised-search workflows | Implement CRUD, master–detail, reports, and at least four topic queries | Integrate, test, repair defects, and prepare application demonstrations |
| R7 — Tester & Writer/Integrator | Nguyễn Minh Khôi | Establish review criteria and check proposal completeness | Verify the ER model, schema, rules, and progress evidence | Test scripts and application behaviour; integrate report, references, and AI disclosure | Perform regression checks, maintain backups, and coordinate documentation and slides |

All members will participate in the ER-modelling discussion and must be able to explain the complete design. Members assigned to the same role share that role’s responsibilities and outputs; the final signed work record may document individual contributions.

## 4. Tools and Programming Languages

The implementation will use **JDK 25 LTS**, **Swing**, **JDBC**, and **SQL (MySQL dialect)** with **MySQL 8.4 LTS**, **Connector/J 8.4.x**, **Maven 3.9.x**, **JUnit**, and **draw.io**. The relational choices follow established database-modelling and normalisation principles [3], while official documentation supports the selected MySQL, Java, and Maven platform [4]–[6].

## 5. Objective

> Give the health service reliable access to student medical records and let it detect and follow up outbreaks early.

## 6. Business Rules

1. **BR-01.** Every Patient is exactly one of Student or Staff, never both and never neither.
2. **BR-02.** A Student has one unique student ID, one faculty, and one dorm Building, whereas a Staff member has one unique staff ID and one office.
3. **BR-03.** MedicalStaff is separate from Patient; the same human may have one unlinked record in each, while specialty and consulting hours belong only to MedicalStaff.
4. **BR-04.** Each MedicalStaff member has weekly ConsultSlots; scheduled or completed Appointments block overlapping slots, whereas cancelled or no-show Appointments do not.
5. **BR-05.** An Appointment books exactly one Patient and one MedicalStaff member at one date-time, and its status is scheduled, completed, cancelled, or no-show.
6. **BR-06.** An Appointment has zero or one Consultation, and a Consultation has zero or one Appointment; zero on the Consultation side denotes a walk-in.
7. **BR-07.** A Consultation records symptoms, diagnosis, notes, attending MedicalStaff, and an optional source Appointment.
8. **BR-08.** A Consultation may issue at most one Prescription, and every Prescription belongs to exactly one Consultation.
9. **BR-09.** PrescriptionLine is a weak entity identified by `(presc_id, line_no)` and is deleted with its Prescription.
10. **BR-10.** Each PrescriptionLine identifies one Medicine and records dose, frequency, duration, and quantity.
11. **BR-11.** Medicine records price and stock of at least zero, but prescribing does not automatically decrement stock.
12. **BR-12.** PatientAllergy represents a many-to-many relationship and stores onset date, an optional note, and severity as mild, moderate, or severe.
13. **BR-13.** VaccinationCourse defines the number of doses required for completion.
14. **BR-14.** VaccinationRecord logs one dose date per patient and course, is unique by dose number, and completes when the distinct dose count reaches the requirement; dose intervals are not checked.
15. **BR-15.** NotifiableCase derives from exactly one Consultation, with external reports first recorded through a walk-in Consultation.
16. **BR-16.** NotifiableCase records disease, report date, and one exposure/report Building, and the outbreak query groups cases of the same disease in that Building within fourteen days.
17. **BR-17.** ContactTrace is a Patient-to-Patient self-relationship with source and contact roles, contact date, optional source case, and no self-contact.
18. **BR-18.** Building represents a hall, dorm, or clinic and is referenced by Student residence and NotifiableCase exposure location.

<div style="break-before: page; page-break-before: always;"></div>

## 7. Entity List

The planned relational model will contain 17 tables:

- **Identity and allergy:** `Patient`, `Student`, `Staff`, `Allergy`, `PatientAllergy`.
- **Care delivery:** `MedicalStaff`, `ConsultSlot`, `Appointment`, `Consultation`, `Prescription`, `PrescriptionLine`, `Medicine`.
- **Prevention:** `VaccinationCourse`, `VaccinationRecord`.
- **Outbreak management:** `NotifiableCase`, `ContactTrace`, `Building`.

The model includes the disjoint, total ISA hierarchy `Patient → Student | Staff`; the weak entity `PrescriptionLine` with partial key `line_no`; the attribute-carrying many-to-many entity `PatientAllergy`; and the two-role Patient self-relationship represented by `ContactTrace`.

## 8. Plan and Roles Table

| Period | Main activities and deliverables | Accountable roles |
|---|---|---|
| W3 | Finalise the proposal, business rules, role allocation, ER requirements, and work schedule | R1–R7 |
| W5 | Complete and review the ER model, draft schema, mapping rules, functional dependencies, and normalisation evidence | R2, R3, R4, R7 |
| W10 | Complete the final report, executable SQL, realistic sample data, required queries, Java application, and test evidence | R1–R7 |
| W13–15 | Rehearse the 15-minute demonstration, verify individual understanding, finalise slides and package, and submit | R1, R5, R6, R7 |

The detailed shared responsibilities for every period are defined in Section 3. Progress will be reviewed at each milestone, and unresolved modelling or implementation defects will be recorded and resolved before the next submission.

## 9. References and AI Disclosure

### References

[1] World Health Organization, *Early Warning Alert and Response (EWAR) in Emergencies: An Operational Guide*. Geneva, Switzerland: WHO, 2022. [Online]. Available: https://iris.who.int/handle/10665/365730 [Accessed: Sep. 24, 2026].

[2] J. J. Reeves et al., “Bringing student health and well-being onto a health system EHR: The benefits of integration in the COVID-19 era,” *Journal of American College Health*, vol. 70, no. 7, pp. 1968–1974, 2022, doi: https://doi.org/10.1080/07448481.2020.1843468.

[3] A. Silberschatz, H. F. Korth, and S. Sudarshan, *Database System Concepts*, 7th ed. New York, NY, USA: McGraw-Hill, 2020, ISBN 978-0-07-802215-9.

[4] Oracle, *MySQL 8.4 Reference Manual*. [Online]. Available: https://dev.mysql.com/doc/refman/8.4/en/ [Accessed: Sep. 24, 2026].

[5] Oracle, *Java Platform, Standard Edition & Java Development Kit Version 25 API Specification*. [Online]. Available: https://docs.oracle.com/en/java/javase/25/docs/api/ [Accessed: Sep. 24, 2026].

[6] Apache Software Foundation, “Introduction to the Build Lifecycle.” Apache Maven. [Online]. Available: https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html [Accessed: Sep. 24, 2026].

### Appendix A — AI-Use Disclosure

<!-- TEAM FINAL CHECK: After Gemini conversion, change future-tense conversion wording to past tense only if the converted document was actually compared with this Markdown. -->

Substantial AI assistance was used to generate this initial Markdown proposal from the course brief, original submission guideline, project glossary, architecture decision records, business rules, and team-supplied roster and roles. During drafting, the generated content was checked against the technical source of truth, the original proposal guideline, the domain glossary, business rules, relevant ADRs, confirmed member data, and source metadata; unsupported claims of completed implementation were removed. Before submission, the group will perform a final academic and technical review and ensure that every member can explain the design. Google Gemini will then be used only to convert the approved Markdown into Google Docs and PDF; the converted document must be compared with the Markdown, and any changed wording must be corrected before submission.
