# Báo cáo luồng MVC - Bài 13

## Thành phần

- Model: `SinhVien`, `Sach`, `SanPham` là các JavaBean.
- Repository: `DataRepository` quản lý ba `List` trong bộ nhớ và cung cấp find/add/update/delete.
- Controller: `AdminCrudServlet` nhận action, kiểm tra dữ liệu, gọi repository và điều hướng.
- View: JSP trong `WEB-INF/views` chỉ hiển thị dữ liệu bằng EL/JSTL, không truy cập repository.
- Session/Filter: `LoginServlet` lưu `username`; `AuthFilter` chặn toàn bộ `/admin/*` khi chưa đăng nhập.

## Luồng xem danh sách

```text
Browser GET /admin/sinh-vien
  -> AuthFilter kiểm tra session
  -> AdminCrudServlet gọi DataRepository.findAllSinhVien()
  -> đặt List vào request attribute "items"
  -> forward tới list.jsp
  -> JSP/JSTL tạo HTML trả về Browser
```

## Luồng lưu dữ liệu

```text
Browser POST form
  -> AuthFilter
  -> AdminCrudServlet validate dữ liệu
  -> DataRepository thêm hoặc cập nhật phần tử trong List
  -> redirect về URL danh sách (Post/Redirect/Get)
```
