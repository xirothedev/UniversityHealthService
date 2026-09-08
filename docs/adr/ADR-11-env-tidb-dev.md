# ADR-11: ENV + .env cho DB, TiDB Cloud chỉ làm dev tạm

## 1. Bối cảnh
Máy chấm trỏ DB khác mà không muốn build lại (ADR-07). Docker/CI/TiDB
Cloud đưa credential qua ENV (DB_HOST...), còn local quen sửa file.
TiDB Cloud bắt buộc TLS nên `useSSL=false` cũ không nối được.

## 2. Quyết định
Thứ tự đọc: ENV thật > file `.env` > `db.properties` > default.
Key ENV: DB_HOST, DB_PORT, DB_DATABASE, DB_USERNAME, DB_PASSWORD,
DB_PARAMS. Parser `.env` tự viết, không thêm dependency.
TiDB Cloud chỉ là DB dev tạm để test connector; bài nộp vẫn MySQL 8.4
LTS (ADR-01 không đổi). TLS TiDB: `sslMode=VERIFY_IDENTITY`.

## 3. Bỏ gì
Bỏ đọc duy nhất `db.properties` (kém với Docker/CI). Bỏ lib dotenv-java
(thừa cho 6 key). Bỏ `useSSL=false` khi nối TiDB (server từ chối).
Bỏ commit `.env` thật (lộ pass); chỉ commit `.env.example`.

## 4. Hậu quả
Được: local + marker + CI cùng 1 code; không recompile. Mất: `buildUrl`
không còn `?` treo khi params rỗng (sửa test cũ cho đúng JDBC).
Ai chịu: R6 (Java) giữ `DBConnection`, cả nhóm copy `.env.example`.

## 5. Liên quan
`src/main/java/com/uhs/util/DBConnection.java`, `src/main/java/com/uhs/App.java`,
`.env.example`, `db.properties.example`, ADR-01, ADR-07.
SSOT `docs/assignment.md` §2.4 (settings ngoài code) vẫn đạt.
