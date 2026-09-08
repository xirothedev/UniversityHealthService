# ADR-08: Index + Transaction

## 1. Bối cảnh
Tìm slot trống và top chẩn đoán chậm nếu quét cả bảng. Tạo khám + đơn phải cùng thành công hoặc cùng hủy.

## 2. Quyết định
Đánh index `Appointment(doctor_id,slot_time)` cho Q-T2 và `Consultation(diagnosis,consult_date)` cho Q-T3. Tạo Consultation + Prescription trong 1 transaction có COMMIT/ROLLBACK.

## 3. Bỏ gì
Bỏ đánh index mọi cột (nhanh đọc nhưng chậm ghi, khó giải thích).

## 4. Hậu quả
Được: query nhanh, số liệu đúng. Mất: phải ghi trong báo cáo index giúp query nào.

## 5. Liên quan
Bảng Appointment, Consultation, Prescription. Query Q-T2, Q-T3.
