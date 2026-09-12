# Kiểm tra trang admin — 11/09/2026

## Phạm vi

Đăng nhập admin → form Thymeleaf → controller/service → dữ liệu → thông báo/danh sách; khách đặt sách → admin xử lý đơn → báo cáo doanh thu.

Các thao tác tạo/sửa/xóa thủ công dùng H2 trong RAM tại `127.0.0.1:8082`, không dùng Supabase. Server QA đã dừng sau kiểm tra. Giao diện có layout dùng chung, màu xanh nhà sách, bảng dữ liệu và menu thu gọn trên mobile.

## Build và kiểm thử tự động

Lệnh: `mvn clean verify`, sau đó `mvn verify` sau khi bổ sung favicon (Java 26.0.2, target Java 21).

```text
Tests run: 47, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

- `AdminCatalogTests` (10): CRUD, ISBN/slug trùng, giá/ảnh/trạng thái không hợp lệ, phiên bản, liên kết, snapshot đơn, phân trang nhiều tác giả.
- `AdminWebTests` (10): render danh sách/form/chi tiết, 404, phân quyền/CSRF, POST CRUD/tồn kho, tab cũ, trạng thái đơn, đăng nhập admin, khóa/mở khách.
- `AdminReportTests` (1): ranh giới ngày Việt Nam, trạng thái, tổng tiền/số cuốn bán.
- `OrderConcurrencyTests` (1): hai yêu cầu hủy đồng thời; chỉ một thành công, hoàn kho đúng một lần.
- 25 test nền tảng: auth, mapping, Flyway, catalog, giỏ, checkout/validation, rollback, ownership, security và báo cáo.

Chi tiết: `target/surefire-reports/`. Bản chạy: `target/yukihira-bookstore-0.0.1-SNAPSHOT.jar`.

## Kiểm tra trình duyệt Chrome

| Luồng | Kết quả quan sát |
|---|---|
| Đăng nhập admin | Chuyển tới `/admin`, dashboard/menu đúng |
| Tạo sách | Lưu tên, ISBN, slug, thể loại, tác giả, NXB, giá 125.000₫, kho 12 |
| Sửa sách | Tên mới và giá 135.000₫ hiện cả ở cửa hàng |
| Nhập kho | Xác nhận 12 → 5; trang công khai hiển thị còn 5 cuốn |
| CRUD thể loại | Tạo, sửa tên/ẩn, xóa mục chưa liên kết |
| CRUD tác giả / NXB | Tạo, sửa, xóa từng loại qua form/hộp xác nhận |
| Khách đặt hàng | Đăng ký, đăng nhập, chọn 2 cuốn, checkout 270.000₫; admin thấy cùng đơn |
| Xử lý đơn | PENDING → CONFIRMED → SHIPPING → COMPLETED; thanh toán thành PAID |
| Báo cáo | 1 đơn hoàn thành, doanh thu 270.000₫, 2 cuốn bán, trung bình 270.000₫ |
| Phân quyền | Phiên CUSTOMER gọi `/admin` nhận HTTP 403 |
| Khóa tài khoản | Phiên khách đang mở chuyển `/login?locked` khi truy cập tiếp; admin mở khóa được |
| Xóa sách đã bán | Chuyển ngừng bán, kho vẫn 3; giữ đơn hoàn thành và tổng 270.000₫ |
| Hộp xác nhận | “Quay lại” không sửa dữ liệu; submit không có submitter vẫn hoạt động |
| Responsive | 390×844: sách, form sách, chi tiết đơn, khách và báo cáo không tràn ngang; menu mobile mở/đóng đúng |
| Hiển thị | Đã xem ảnh dashboard desktop/báo cáo mobile; console admin không có lỗi/cảnh báo ở các trang kiểm tra |

## Lỗi đã xử lý khi kiểm tra

1. QA chưa nạp test resources nên đọc placeholder `DATABASE_URL`: nạp rõ thư mục cấu hình test và đặt Flyway test độc lập.
2. Form xác nhận có thể nhận submitter rỗng: chỉ truyền submitter khi có; trường hợp còn lại gọi `requestSubmit()` không tham số.
3. Trình duyệt yêu cầu favicon mặc định bị 404: bổ sung SVG favicon cho login/register và toàn bộ layout admin, kiểm tra resource bằng MockMvc.

## Smoke test Supabase

Server mới chạy tại `127.0.0.1:8081`; Flyway xác nhận schema version 1 đã cập nhật, không cần migration mới. Đăng nhập bằng tài khoản admin hiện có thành công. Dashboard, sách (keyword + sort), tồn kho, thể loại (keyword), tác giả, NXB, đơn hàng (khoảng ngày), khách hàng và báo cáo (khoảng ngày) đều trả HTTP 200 với layout admin. Chỉ đọc dữ liệu trong lượt smoke test này; không thử CRUD trên Supabase.

## Giới hạn

- Chưa kiểm tra tải lớn và cạnh tranh trên PostgreSQL thật; test đồng thời hiện dùng H2.
- Ảnh bìa dùng URL, chưa có dịch vụ upload. In phiếu/báo cáo dùng hộp in của trình duyệt.
- Các luồng Chrome là kiểm tra thủ công bằng công cụ; chưa tích hợp browser automation vào Maven. 47 test là hồi quy JUnit/MockMvc/service.
- JDK 26 có cảnh báo tương thích tương lai từ Mockito/Spring test (dynamic agent/final-field reflection); không có lỗi compile/test.
