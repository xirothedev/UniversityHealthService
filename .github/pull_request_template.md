## Đầu nộp nào? (xóa mục không liên quan)

- [ ] D1 đề cương · [ ] D2 ER/schema · [ ] D3 báo cáo · [ ] D5 SQL · [ ] D6 Java · [ ] D4 demo · [ ] Khung repo/CI

## Đối chiếu SSOT (`docs/assignment.md`)

- [ ] Tôi đã đọc mục SSOT tương ứng và PR này không sửa `docs/assignment.md`
- [ ] Task IDs liên quan (Sheet riêng): <!-- VD: T13, T14 -->

## Checklist theo loại thay đổi (chỉ tick mục mình đụng)

**SQL (`sql/`):**
- [ ] Script chạy từ đầu đến cuối 2 lần không sửa tay (drop → create cha-trước-con-sau → insert)
- [ ] Đủ PK, FK + ON DELETE/UPDATE, NOT NULL, UNIQUE, CHECK, DEFAULT trong script (không để ràng buộc chỉ nằm ở Java)
- [ ] Data mẫu ≥15 dòng thật/bảng, 12/12 query non-empty
- [ ] `sqlfluff lint sql/` sạch (CI cũng check)

**Java (`src/`):**
- [ ] Mọi câu có giá trị người nhập dùng `PreparedStatement ?` (không nối chuỗi — CI không bắt được, reviewer check tay + test dấu `'`)
- [ ] Connection đọc từ `db.properties` ngoài code, `try-with-resources` mọi connection/statement/result set
- [ ] Validate required/range/thứ tự ngày trước khi vào DB; lỗi FK báo câu thường, không stack trace
- [ ] Comment + message tiếng Anh (CI check ASCII)

**Báo cáo (`docs/`):**
- [ ] Đúng mục/thứ tự 12 mục D3, số trang tối thiểu
- [ ] Screenshot form + SQL đằng sau (mục 9), AI appendix nếu có dùng AI

## Review

- [ ] Reviewer khác vai trò với người làm (quy tắc chấm cá nhân: ai cũng phải hiểu bài người khác)
- [ ] CI xanh (java + sql + guards)
