# CONTEXT.md — University Health Service (UHS)

Người mới đọc file này trước. Từ nào dùng từ đó, không chế từ mới.

## Thuật ngữ

- **Patient**: người khám. Gồm 2 loại, không trùng nhau.
- **Student / Staff**: 2 loại của Patient. Student có mã sinh viên, Staff có mã nhân viên.
- **Allergy**: dị ứng (đậu phộng, penicillin...).
- **PatientAllergy**: ai bị dị ứng gì + nặng cỡ nào. Nặng nhẹ nằm ở đây, không nằm ở Patient hay Allergy.
- **MedicalStaff**: bác sĩ/y tá. Có chuyên khoa + giờ khám.
- **Appointment**: lịch hẹn. Ai + với ai + lúc nào + trạng thái.
- **Consultation**: buổi khám. Ghi triệu chứng + chẩn đoán.
- **Prescription**: đơn thuốc của 1 buổi khám.
- **PrescriptionLine**: 1 dòng trong đơn. Không có đơn thì dòng vô nghĩa.
- **Medicine**: thuốc. Có tồn kho + giá.
- **VaccinationCourse / VaccinationRecord**: khóa tiêm (cần mấy mũi) + sổ đã tiêm mũi nào.
- **NotifiableCase**: ca bệnh phải báo (sốt xuất huyết...). Ghi bệnh gì + ngày nào + ở tòa nào.
- **ContactTrace**: ai tiếp xúc ai. Dùng để tìm ổ dịch.
- **Building**: tòa/ký túc xá. Dùng để truy ổ dịch 14 ngày.

## Luật đọc

- Làm việc với DB thì đọc thêm `docs/adr/` có chạm chỗ đó không.
- Tên trong code/issues phải khớp từ trên. Không gọi Patient là Customer/User.
- Thiếu từ thì ghi lại cho `/domain-modeling`, không tự chế.
