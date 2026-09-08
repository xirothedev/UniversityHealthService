# ADR-03: ISA Patient 3 bảng

## 1. Bối cảnh
Người khám gồm sinh viên và nhân viên. Chung tên, ngày sinh, SĐT. Riêng mã số, khoa/phòng.

## 2. Quyết định
3 bảng: Patient (cha) + Student + Staff (con). Khóa chính con cũng là khóa ngoại tới cha, xóa cha kéo theo con.

## 3. Bỏ gì
Bỏ gộp 1 bảng (ít bảng hơn nhưng nhiều cột null, khó ăn điểm ISA).

## 4. Hậu quả
Được: đúng đề, rõ ràng. Mất: join nhiều hơn khi lấy đủ thông tin.

## 5. Liên quan
Bảng Patient, Student, Staff. Query Q-T1, Q6.
