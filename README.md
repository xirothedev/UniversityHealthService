# UniversityHealthService (IT079IU — Topic 14)

Trạm y tế đại học: hồ sơ khám + phát hiện ổ dịch sớm.

## Stack khóa

JDK 21 LTS + MySQL 8.4 LTS + Connector/J 8.4.x + Maven 3.9.x + Swing + draw.io.

## Chạy

1. Copy `.env.example` thành `.env`, sửa host/user/pass (local MySQL hoặc TiDB dev tạm). `db.properties` vẫn đọc được để tương thích cũ, nhưng ENV/`.env` ưu tiên hơn.
2. Chạy SQL theo thứ tự: `sql/01_schema.sql` → `02_data.sql` → `03_queries.sql`.
3. Build + chạy: `mvn -q -DskipTests package && mvn -q exec:java`.
4. Kiểm connector TiDB/MySQL: `mvn -q test` (unit, không cần DB) rồi nối thật theo `.env`.

Chi tiết chấm: `CONTEXT.md`, `docs/adr/`, `docs/D1-outline.md`.
