# ADR-10: Format + lint + test + kiểm SQL trong CI

## 1. Bối cảnh
PR cần kiểm tự động ngoài compile: format thống nhất, lint bắt lỗi
cơ bản, test chạy được không cần DB, SQL đúng syntax. Nhóm 12 người
mới nên mọi kiểm phải gói trong 1 lệnh local duy nhất.

## 2. Quyết định
Format: Spotless 3.10.2 + google-java-format. Sửa local bằng
`mvn spotless:apply`, CI kiểm bằng `spotless:check` (gắn sẵn vào
`verify`). Lint: maven-checkstyle-plugin 3.6.0 + `google_checks.xml`
(lib checkstyle 14.1.0), chạy ở `verify`, 0 violation mới xanh. Test:
JUnit 5.14.4 (`scope: test`); tách `DBConnection.buildUrl(Properties)`
làm mối test được (không cần DB thật), `App.main` dùng chung để khỏi
trùng code; 2 test trong `DBConnectionTest`. SQL: sqlfluff
(`dialect = mysql` trong `.sqlfluff`), CI chạy `sqlfluff lint sql/`.
CI tách 2 job `java` (`mvn -B verify`) và `sql` (pip + lint).

## 3. Bỏ gì
Bỏ PMD/ErrorProne (mạnh nhưng config nặng, khó cho người mới). Bỏ
JUnit 6 (mới, ít tài liệu tiếng Việt, Surefire mặc định chưa chắc ăn).
Bỏ service MySQL live-DB ở bước này (schema R4 chưa có, chạy container
chỉ xanh giả; để dành bước sau khi schema chạy 2 lần không sửa tay).
Bỏ `sqlfluff parse` thuần syntax (mất tín hiệu style như LT05).

## 4. Hậu quả
Được: PR vỡ format/lint/test/SQL là đỏ ngay; local chỉ cần nhớ
`mvn -B verify` + `sqlfluff lint sql/`. Mất: google style 2-space lạ
với ai quen 4-space; sqlfluff gắt dòng dài — fix bằng xuống dòng,
không tắt rule. Ai chịu: cả nhóm, R6 (Java) chịu format/test, R4/R5
chịu SQL xanh.

## 5. Liên quan
`pom.xml`, `src/main/java/com/uhs/App.java`,
`src/main/java/com/uhs/util/DBConnection.java`,
`src/test/java/com/uhs/util/DBConnectionTest.java`, `.sqlfluff`,
`sql/*.sql`, `.github/workflows/ci.yml`. SSOT `docs/assignment.md`
§§2.3-2.4 không đòi CI nên không xung đột.
