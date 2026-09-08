# ADR-07: Connection ngoài code + PreparedStatement

## 1. Bối cảnh
Máy chấm trỏ DB khác mà không muốn build lại. Nhập dấu `'` dễ vỡ SQL nối chuỗi.

## 2. Quyết định
Thông tin DB nằm ở `db.properties` ngoài code. Mọi câu có giá trị người nhập đều dùng `PreparedStatement ?` + try-with-resources.

## 3. Bỏ gì
Bỏ nối chuỗi vào SQL (nhanh nhưng lỗi injection, bị trừ điểm). Bỏ hard-code user/pass trong `.java`.

## 4. Hậu quả
Được: an toàn, dễ chấm. Mất: viết dài hơn chút.

## 5. Liên quan
Mọi DAO. Form F1-F5. File `db.properties.example`.
