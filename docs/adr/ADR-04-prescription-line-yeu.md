# ADR-04: PrescriptionLine yếu

## 1. Bối cảnh
1 dòng thuốc không đứng một mình được. Dòng số 3 vô nghĩa nếu không biết của đơn nào.

## 2. Quyết định
PrescriptionLine là weak entity. Khóa gồm (presc_id + line_no). Xóa đơn kéo theo dòng.

## 3. Bỏ gì
Bỏ cho nó id riêng (dễ code hơn nhưng mất điểm weak entity).

## 4. Hậu quả
Được: đúng đề, demo master-detail đẹp. Mất: thêm thuốc phải biết đơn trước.

## 5. Liên quan
Bảng Prescription, PrescriptionLine, Medicine. Form F3.
