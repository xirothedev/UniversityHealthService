# ADR-06: Self-rel ContactTrace

## 1. Bối cảnh
Truy ổ dịch cần biết ai tiếp xúc ai. Cả 2 đầu đều là Patient.

## 2. Quyết định
Bảng ContactTrace nối Patient với Patient. Ghi rõ 2 vai: nguồn bệnh / người tiếp xúc + ngày tiếp xúc.

## 3. Bỏ gì
Bỏ quan hệ Doctor-quản-Doctor (cũng là self-rel nhưng không giúp tìm dịch).

## 4. Hậu quả
Được: trả lời câu ổ dịch 14 ngày cùng tòa. Mất: phải chặn tự tiếp xúc chính mình bằng CHECK/test.

## 5. Liên quan
Bảng NotifiableCase, ContactTrace, Building. Query Q-T5.
