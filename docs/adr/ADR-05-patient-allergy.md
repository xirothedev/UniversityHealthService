# ADR-05: PatientAllergy M-M có thuộc tính

## 1. Bối cảnh
1 người có nhiều dị ứng, 1 dị ứng ở nhiều người. Mức nặng nhẹ là của từng cặp, không của riêng ai.

## 2. Quyết định
Bảng giữa PatientAllergy: khóa (patient_id + allergy_id) + severity + onset_date.

## 3. Bỏ gì
Bỏ cột allergy_list kiểu chuỗi (nhanh nhưng sai chuẩn, không truy vấn được).

## 4. Hậu quả
Được: hỏi ai bị gì nặng đều được. Mất: thêm 1 bảng join.

## 5. Liên quan
Bảng Patient, Allergy, PatientAllergy. Query Q6.
