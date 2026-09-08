# ADR-09: CI GitHub Actions build tối thiểu

## 1. Bối cảnh
Nhóm 12 người đẩy PR lên `main`, chưa có kiểm tra tự động. Cần biết sớm khi `pom.xml` hay `src/` vỡ, trước D1 (W3 22/9-28/9). `sql/*.sql` mới là placeholder nên CI chưa thể đòi DB.

## 2. Quyết định
Dùng GitHub Actions: `.github/workflows/ci.yml`, chạy `push` + `pull_request` lên `main`. Job `build` trên `ubuntu-latest`: `actions/checkout@v7` + `actions/setup-java@v6` (Temurin 21, `cache: maven`) + `mvn -B package`. Không DB, không branch protection ở V1.

## 3. Bỏ gì
Bỏ GitLab CI/Jenkins (tốn infra, repo đã ở GitHub). Bỏ service `mysql:8.4` ở V1 (schema chưa có, CI sẽ đỏ từ ngày 1). Bỏ Maven wrapper/pin cứng 3.9.x (runner đã có 3.9.x, thêm wrapper chỉ rối cho người mới).

## 4. Hậu quả
Được: PR nào vỡ compile là thấy ngay, ít tốn phút chạy. Mất: không bắt lỗi SQL, không bắt thiếu `db.properties`; phải bù bằng review tay tới khi R4/R5 giao schema (lên ADR-10).

## 5. Liên quan
`pom.xml`, `src/main/java/com/uhs/`. SSOT `docs/assignment.md` §§2.3-2.4 không đòi CI nên không xung đột. Khi schema chạy 2 lần không sửa tay thì mở rộng CI thêm job MySQL.
