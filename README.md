# UniversityHealthService (IT079IU — Topic 14)

University health station: visit records + early outbreak detection.

## Pinned stack

JDK 21 LTS + MySQL 8.4 LTS + Connector/J 8.4.x + Maven 3.9.x + Swing + draw.io.

## Run

1. Copy `.env.example` to `.env`, edit host/user/pass (local MySQL or temporary TiDB dev DB). `db.properties` still works for backward compatibility, but ENV/`.env` take precedence.
2. Run SQL in order: `sql/01_schema.sql` → `02_data.sql` → `03_queries.sql`.
3. Build + run: `mvn -q -DskipTests package && mvn -q exec:java`.
4. Check the TiDB/MySQL connector: `mvn -q test` (unit, no DB needed) then a live connection using `.env`.

Grading details: `CONTEXT.md`, `docs/adr/`, `docs/D1-outline.md`.

## Submission format

Proposal = D1, Midterm = D2, Final = D3+D5+D6 (+ D4 slides/demo). Mapping + cover rule: `docs/deliverables-detail.md` (ADR-12). Pre-submit tick list: `docs/submission-checklist.md`. Templates: `docs/templates/cover.md`, `docs/templates/task-allocation.md`.

## Team (12 members, fill in)

| # | Name | Student ID | Phone | Claimed role |
|---|------|------------|-------|--------------|
| 1 | Lê Thành Trung | | | |
| 2 | Nguyễn Trần Quốc Vinh | | | |
| 3 | Phan Nguyên Vũ | | | |
| 4 | Nguyễn Hoàng Thiên Tứ | | | |
| 5 | Nguyễn Quốc Duy Lộc | | | |
| 6 | Trần Pham Quốc Nam | | | |
| 7 | Lê Đức Phát | | | |
| 8 | Nguyễn Minh Khôi | | | |
| 9 | Lê Thảo My | | | |
| 10 | Nguyễn Trần Anh Tuấn | | | |
| 11 | Nguyễn Trọng Đức | | | |
| 12 | Nguyễn Hiền Minh | | | |
