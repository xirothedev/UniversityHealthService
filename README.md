# UniversityHealthService (IT079IU — Topic 14)

University health station: visit records + early outbreak detection.

## Pinned stack

JDK 25 LTS + MySQL 8.4 LTS + Connector/J 8.4.x + Maven 3.9.x + Swing + draw.io.

## Run

1. Copy `.env.example` to `.env`, edit host/user/pass (local MySQL or temporary TiDB dev DB). `db.properties` still works for backward compatibility, but ENV/`.env` take precedence.
2. Run SQL in order: `sql/01_schema.sql` → `02_data.sql` → `03_queries.sql`.
3. Build + run: `mvn -q -DskipTests package && mvn -q exec:java`.
4. Check the TiDB/MySQL connector: `mvn -q test` (unit, no DB needed) then a live connection using `.env`.

Grading details: `CONTEXT.md`, `docs/adr/`, `docs/D1-outline.md`.

## Submission format

Proposal = D1, Midterm = D2, Final = D3+D5+D6 (+ D4 slides/demo). Mapping + cover rule: `docs/deliverables-detail.md` (ADR-15). Pre-submit tick list: `docs/submission-checklist.md`. Templates: `docs/templates/cover.md`, `docs/templates/task-allocation.md`.

## Team (12 members, fill in)

| # | Name | Student ID | Phone | Claimed role |
|---|------|------------|-------|--------------|
| 1 | Lê Thành Trung |ITCSIU25055 |0359186887 |Query Developer |
| 2 | Nguyễn Trần Quốc Vinh |MAMAIU25041 |0837991399 |Java Developer |
| 3 | Phan Nguyên Vũ |ITITIU25047 |0945719666 |Java Developer |
| 4 | Nguyễn Hoàng Thiên Tứ |ITDSIU25050 |0374749542 |Query Developer |
| 5 | Nguyễn Quốc Duy Lộc |ITITIU25018 |0906648951 |Data Modeller |
| 6 | Trần Pham Quốc Nam |ITCSIU25037 |0375314749 |Database Developer |
| 7 | Lê Đức Phát |ITCSIU25042 |0767393239 |Database Developer |
| 8 | Nguyễn Minh Khôi |SESEIU24013 |0902305920 |Tester & Writer/Integrator |
| 9 | Lê Thảo My |SESEIU23018 |0835931468 |Normalization lead |
| 10 | Nguyễn Trần Anh Tuấn |SESEIU24030 |0972235718 |Normalization lead |
| 11 | Nguyễn Trọng Đức |ITITIU25003 |0775327007 |Data Modeller |
| 12 | Nguyễn Hiền Minh |ITITWE25011 |0919150220 |Group leader |
