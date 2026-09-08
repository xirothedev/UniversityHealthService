# UniversityHealthService (IT079IU — Topic 14)

Trạm y tế đại học: hồ sơ khám + phát hiện ổ dịch sớm.

## Stack khóa

JDK 21 LTS + MySQL 8.4 LTS + Connector/J 8.4.x + Maven 3.9.x + Swing + draw.io.

## Chạy

1. Copy `db.properties.example` thành `db.properties`, sửa host/user/pass.
2. Chạy SQL theo thứ tự: `sql/01_schema.sql` → `02_data.sql` → `03_queries.sql`.
3. Build + chạy: `mvn -q -DskipTests package && mvn -q exec:java`.

Chi tiết chấm: `CONTEXT.md`, `docs/adr/`, `docs/D1-outline.md`.

## Nhóm (12 người, tự điền)

| # | Họ tên | MSSV | SĐT | Role tự nhận |
|---|--------|------|-----|--------------|
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
