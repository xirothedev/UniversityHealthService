# Chi tiết 6 đầu nộp D1–D6 (nguồn: `docs/assignment.md` §2.1–2.5, §2.7)

Hạn tính từ Week 1 = 8/9/2026. Trễ hạn chỉ nhận lý do bất khả kháng báo trước (ốm); "bận môn khác" không tính.

## D1 — Đề cương, 1–3 trang, hạn 28/9 (W3)

- Nội dung: câu mục tiêu UHS (tra hồ sơ nhanh + phát hiện ổ dịch sớm), business rules 15–20 câu đánh số, entity list 11 bảng, stack khóa (JDK 21 LTS + MySQL 8.4 LTS + Connector/J 8.4 + Maven 3.9 + Swing), bảng chia việc.
- Kèm theo mọi bản nộp: bảng thông tin nhóm (tên + MSSV + SĐT + topic).
- Chốt topic tại D1. Ai chưa có nhóm/topic thì giảng viên xếp/xuống đề có sẵn.
- Chủ trì: Nhóm trưởng + Mô hình dữ liệu. Ăn điểm Motivation (10) + Requirements (15).

## D2 — Báo cáo giữa kỳ, 3–5 trang, hạn 12/10 (W5)

- Nội dung: ER đầy đủ (≥8 entity, ≥8 quan hệ, weak + partial key, M-M có thuộc tính, ISA ghi disjoint/total, self-rel ghi tên 2 vai, cardinality + participation mọi quan hệ) + draft schema + nhóm mapping rule.
- Hạn cuối đổi topic (cần cô duyệt).
- Chủ trì: Mô hình dữ liệu + Chuẩn hóa (FD draft).

## D3 — Báo cáo cuối, 25+ trang, hạn 16/11 (W10), đúng 12 mục theo thứ tự

1. Giới thiệu (đối tượng dùng, quyết định hỗ trợ). 2. Requirements + business rules. 3. ER + giải thích quyết định khó. 4. Schema + mapping rule từng bảng. 5. FD + minimal cover + khóa ứng viên (chứng minh). 6. Chuẩn hóa: dạng chuẩn từng quan hệ + phân rã + chứng minh lossless (dừng ở 3NF phải nêu ràng buộc nào mất ở BCNF). 7. Cài đặt DB: hệ + version, script, data mẫu, transaction + lý do, index + giúp query nào. 8. Query: câu hỏi tiếng Anh + SQL + kết quả mẫu. 9. App: cách kết nối, screenshot mọi form + SQL đằng sau. 10. Thảo luận: cái chưa diễn tả được + muốn đổi gì. 11. Phân công (ký đủ 12 người). 12. References + phụ lục khai AI (dùng gì + verify sao).
- Chủ trì: Kiểm thử & Viết báo cáo ghép, mọi role viết mục của mình.

## D4 — Demo 15 phút, 1–21/12 (W13–15)

- Demo live INSERT/UPDATE/DELETE rồi show thay đổi trong DB. Mỗi người bị hỏi riêng toàn bài (cấm nói "phần người khác"). Ăn điểm demo/trả lời (20) + viết (10).
- 30% điểm project là hỏi/nhận xét nhóm khác trong 3 buổi báo cáo — chuẩn bị câu hỏi từ T31.

## D5 — File `.sql`, nộp cùng D3

- 1 script chạy từ đầu đến cuối 2 lần không sửa tay: drop → create (cha trước con sau) → insert.
- Đủ ràng buộc trong script: PK, FK + ON DELETE/UPDATE, NOT NULL, UNIQUE, CHECK, DEFAULT. Ràng buộc chỉ nằm ở Java không tính điểm.
- ≥15 dòng thật/bảng, 12/12 query đều non-empty. File query riêng: mỗi câu có comment câu hỏi tiếng Anh.
- Kèm: INSERT/UPDATE/DELETE cho bảng chính + 1 transaction 2 bảng (COMMIT/ROLLBACK) + 1 index. Test trước: trùng khóa, FK sai, vỡ CHECK đều phải bị chặn.
- Chủ trì: Lập trình DB + Truy vấn. Trọng số lớn nhất (Technical 45).

## D6 — App Java, nộp cùng D3

- 6 màn bắt buộc: connection ngoài code (properties, marker trỏ server khác không cần build lại), browse grid + đếm dòng, search có tham số, master–detail (Consultation→PrescriptionLine), editing CRUD + confirm xóa, report aggregate (Top-5 chẩn đoán). Đưa ≥4/5 query đề tài lên menu.
- Luật code: mọi câu có giá trị người nhập dùng PreparedStatement (nối chuỗi = lỗi, marker test bằng dấu nháy), lỗi FK báo câu thường không stack trace, validate required/range/thứ tự ngày, đóng connection bằng try-with-resources.
- Nộp source + README (JDK, driver, connection, bước build/run). Không chạy được = mất điểm màn hình.
- Chủ trì: Lập trình Java + Kiểm thử.
