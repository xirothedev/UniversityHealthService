# ADR-02: JDK 21 + Maven + Swing

## 1. Bối cảnh
App Java phải chạy được ở máy chấm mà không build lại. Nhóm 12 người mới, cần ít lỗi setup nhất.

## 2. Quyết định
JDK 21 LTS + Maven 3.9.x + Swing (có sẵn trong JDK) + Connector/J 8.4.x.

## 3. Bỏ gì
Bỏ JavaFX (đẹp hơn nhưng phải cài thêm, dễ vỡ demo). Bỏ Gradle mới, bỏ JDK non-LTS 22/23/24.

## 4. Hậu quả
Được: ai cũng chạy được, ít phụ thuộc. Mất: giao diện xấu, không sao vì đề không chấm đẹp.

## 5. Liên quan
6 form F0-F5. File `db.properties` + `pom.xml`.
