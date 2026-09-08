# ADR-01: Dùng MySQL 8.4 LTS

## 1. Bối cảnh
Đề cho MySQL hoặc SQL Server, lab dùng MySQL. Cần DB chặn được khóa chính, khóa ngoại, UNIQUE, CHECK.

## 2. Quyết định
Dùng MySQL 8.4 LTS. Ghi version vào báo cáo + README.

## 3. Bỏ gì
Bỏ Postgres (không trong đề, dễ bị trừ điểm). Bỏ MySQL 9.x (bản mới thử nghiệm, máy chấm không có).

## 4. Hậu quả
Được: khớp lab, dễ chấm. Mất: CHECK yếu hơn Postgres, phải bù bằng test tay.

## 5. Liên quan
Mọi bảng. Query Q-T2, Q-T3 dùng index.
