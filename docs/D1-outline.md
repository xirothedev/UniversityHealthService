# D1 outline (1-3 pages, due W3 22/9-28/9) — R1+R2+Writer fill in

1. Header: team name + topic 14 + 12-person table (name+student ID+phone) + pinned stack.
**Pinned Stack (R2 - Technology Stack):**
- **DBMS:** MySQL Server 8.4 LTS (InnoDB, strict declarative constraints: PK, FK with CASCADE, UNIQUE, CHECK, transactions) (ADR-01).
- **Application:** Java (JDK 21 LTS), Maven (`pom.xml`) (ADR-02).
- **Driver & Persistence:** JDBC (`com.mysql:mysql-connector-j:8.4.0`).
- **Connection & Security:** Externalized `db.properties` (with `.env` fallback), parameterized queries via `PreparedStatement` only (ADR-07).
- **GUI:** Java Swing.
- **Tools:** draw.io for ER modeling, Git/GitHub for version control.
2. Objective in 1 sentence: fast record lookup for the health station + early outbreak detection.
3. Business rules, 15-20 numbered sentences (R2 writes, whole team approves). Drafts already in chat, copy them here then revise.
4. Entity list of 11 tables + where the weak/M-M/ISA/self-rel sit.

| Entity / Table Name | Construct Type | Primary Key (PK) | Attributes & Foreign Keys (FK) | Technical Requirement Mapping (Section 2.2 & ADR) |
| :--- | :--- | :--- | :--- | :--- |
| **Patient** | Regular (Superclass) | `patient_id` | `full_name`, `date_of_birth`, `gender`, `phone_number`, `email` | **ISA Superclass (ADR-03):** Root entity for all healthcare recipients. |
| **Student** | Subclass (ISA) | `patient_id` | `patient_id` (FK), `student_code` (UQ), `faculty`, `dorm_building_id` (FK) | **ISA Subclass (ADR-03):** Disjoint + total `(d, total)` with Staff. |
| **Staff** | Subclass (ISA) | `patient_id` | `patient_id` (FK), `staff_code` (UQ), `department`, `office_room` | **ISA Subclass (ADR-03):** Disjoint + total `(d, total)` with Student. |
| **MedicalStaff** | Regular Entity | `staff_id` | `full_name`, `specialty`, `phone_number` | **Independent Entity (ADR-14):** Kept outside Patient ISA. |
| **ConsultSlot** | Regular Entity | `slot_id` | `staff_id` (FK), `weekday`, `start_time`, `end_time`, `slot_length` | Regular entity for weekly template hours to compute free slots (ADR-12). |
| **Appointment** | Regular Entity | `appointment_id` | `patient_id` (FK), `staff_id` (FK), `appointment_time`, `status` | Regular entity for booking episodes. |
| **Consultation** | Regular Entity | `consultation_id` | `appointment_id` (FK, nullable), `staff_id` (FK), `consultation_date`, `symptoms`, `diagnosis` | Regular entity; allows walk-in visits with no prior appointment. |
| **Prescription** | Regular (Owner) | `prescription_id` | `consultation_id` (FK), `staff_id` (FK), `issue_date` | Regular owner entity for prescription lines (ADR-04). |
| **PrescriptionLine** | **Weak Entity** | `(prescription_id, line_no)` | `prescription_id` (FK), `line_no` (Partial Key), `medicine_id` (FK), `dosage`, `frequency`, `duration_days`, `quantity` | **Weak Entity (ADR-04):** Identified only inside Prescription via partial discriminator `line_no`. |
| **Medicine** | Regular Entity | `medicine_id` | `medicine_name`, `stock_quantity`, `unit_price` | Regular entity for drug catalog and pricing. |
| **Allergy** | Regular Entity | `allergy_id` | `allergen_name` (UQ), `category` | Regular entity for allergens. |
| **PatientAllergy** | **M:N Associative** | `(patient_id, allergy_id)` | `patient_id` (FK), `allergy_id` (FK), `severity`, `onset_date`, `note` | **M:N with Attributes (ADR-05):** Carries severity and onset date. |
| **VaccinationCourse**| Regular Entity | `course_id` | `course_name`, `target_disease`, `required_doses` | Regular entity defining vaccine regimens. |
| **VaccinationRecord**| Associative Log | `record_id` | `patient_id` (FK), `course_id` (FK), `dose_number`, `date_given` | Associative log; unique on `(patient_id, course_id, dose_number)`. |
| **Building** | Regular Entity | `building_id` | `building_name`, `building_type` | Regular entity for campus locations (hall/dorm/clinic). |
| **NotifiableCase** | Regular Entity | `case_id` | `consultation_id` (FK), `disease_name`, `date_reported`, `building_id` (FK) | Regular entity anchored to Consultation; carries exposure building (ADR-13). |
| **ContactTrace** | **Self-Rel Entity** | `trace_id` | `source_patient_id` (FK), `contact_patient_id` (FK), `contact_date`, `source_case_id` (FK) | **Self-referencing (ADR-06):** Patient-to-Patient trace with 2 explicit roles (`source` and `contact`). |
5. Plan W3/W5/W10/W13-15 + R1-R7 table (names not assigned yet).
### Milestone Plan (Resolved Deadlines: Earliest-Deadline-Wins)
| Milestone | Timeline / Deadline | Deliverables | Key Responsibilities |
| :--- | :---: | :--- | :--- |
| **D1. Proposal Report** | **Week 2 (21/09/2026)** | 1–3 pages: Cover, Abstract, Task Allocation Table, Pinned Stack, 18 BRs, Entity list, Plan, References & AI disclosure | Lead (R1) + Data Modeller (R2) + Writer |
| **D2. Progress Report** | **Week 5 (12/10/2026)** | 3–5 pages: Updated Info, TOC, full ERD (draw.io) + draft schema + mapping rules + initial FDs | Data Modeller (R2) + Normalization (R3) |
| **D3. Final Report** | **Week 10 (16/11/2026)** | 25+ pages report (exact 12 sections in order), lossless proofs, normalization to 3NF/BCNF | Tester & Writer (R7) assembles; all roles write sections |
| **D5. SQL Scripts** | **Week 10 (16/11/2026)** | `01_schema.sql`, `02_data.sql` (≥15 rows/table), `03_queries.sql` (12/12 queries non-empty), transaction + index | DB Developer (R4) + Query Developer (R5) |
| **D6. Java Desktop App** | **Week 10 (16/11/2026)** | Java Swing project (`src/`), Maven, `db.properties`, 6 screens (F1–F5), `README.md` build/run instructions | Java Developer (R6) + Tester (R7) |
| **D4. Demo & Presentation** | **Weeks 13–15 (01/12 – 21/12/2026)** | 15-minute live demo (CRUD & DB reflection) + individual Q&A defense (Whole project scope) | All team members |

### Role Allocation Matrix (R1–R7)
| Role Code | Role Title | Slots | Primary Responsibility | Assigned Members |
| :---: | :--- | :---: | :--- | :--- |
| **R1** | Group Leader | 1 | Schedule, deadlines, submissions, work-allocation sheet | Nguyễn Hiền Minh |
| **R2** | Data Modeller | 2 | Business rules, ER diagram (draw.io), mapping schema | Nguyễn Quốc Duy Lộc, Nguyễn Trọng Đức |
| **R3** | Normalization Lead | 2 | Functional dependencies, minimal cover, 3NF/BCNF proofs | Lê Thảo My, Nguyễn Trần Anh Tuấn |
| **R4** | Database Developer | 2 | 01_schema.sql (DDL + constraints), 02_data.sql (>=15 rows) | Lê Đức Phát, Trần Pham Quốc Nam |
| **R5** | Query Developer | 2 | 12 SQL queries + 4 relational algebra, views, transactions | Nguyễn Hoàng Thiên Tứ, Lê Thành Trung |
| **R6** | Java Developer | 2 | JDBC layer, Swing UI (F1–F5), input validation, properties | Nguyễn Trần Quốc Vinh, Phan Nguyên Vũ |
| **R7** | Tester / Writer | 1 | End-to-end testing, report integration, AI disclosure appendix | 	Nguyễn Minh Khôi |
6. References + AI disclosure (what was used + how it was verified).

Once D1 is done the topic is locked; no changes after W5 without instructor approval.
