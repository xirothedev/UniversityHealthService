# CONTRIBUTING — Luật đóng góp để PR đạt điểm tối đa

SSOT là `docs/assignment.md` (đề gốc, cấm sửa). Mọi yêu cầu dưới đây đối chiếu từ file đó.

## 1. Nhánh & PR

- 1 task Sheet = 1 nhánh: `d1/rules`, `d2/er`, `d5/schema`, `d6/form-f3`... Không push thẳng `main`.
- PR nhỏ, 1 đầu nộp/loại thay đổi. Điền đủ template PR (tick checklist thật, không tick cho có).
- Merge bằng **squash** (repo chặn merge commit).

## 2. Reviewer (bắt buộc, phục vụ chấm cá nhân)

- Mỗi PR cần 1 reviewer **khác vai trò** với người làm, approve mới merge.
- Reviewer check tay 3 thứ CI không bắt được: SQL nối chuỗi, `docs/assignment.md` bị sửa, checklist PR tick ẩu.

## 3. Definition of Done

- **SQL:** script chạy 2 lần không sửa; đủ PK/FK/NOT NULL/UNIQUE/CHECK/DEFAULT + ON DELETE/UPDATE; data thật ≥15 dòng/bảng; query non-empty; `sqlfluff` sạch.
- **Java:** PreparedStatement mọi input; connection ngoài code; try-with-resources; validate + báo lỗi dễ hiểu; tiếng Anh; `mvn verify` xanh.
- **Docs:** đúng mục, đủ trang, screenshot + SQL, AI appendix khi dùng AI.

## 4. Cấm

- Sửa `docs/assignment.md` (CI chặn). Muốn diễn giải khác SSOT → mở issue thảo luận trước.
- Hard-code user/pass/URL DB, mảng/file cứng chứa data, commit `db.properties` thật, commit file IDE (`.classpath`, `.project`, `.settings/`).
