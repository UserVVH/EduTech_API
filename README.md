# Thư viện học liệu trực tuyến cho sinh viên khoa CNTT

## Mô tả
Hệ thống quản lý tài liệu cho phép người dùng đăng ký tài khoản, đăng nhập, quản lý tài liệu, và nhiều chức năng khác. Hệ thống cũng cung cấp các công cụ cho quản trị viên để quản lý người dùng và tài liệu hiệu quả.

## Chức năng Chính

### 1. Người dùng (User)
- **Tài khoản**
  - Đăng ký tài khoản
  - Đăng nhập
  - Đổi mật khẩu
  - Quên mật khẩu
  - Đổi thông tin
  - Xem thông tin người dùng khác trong hệ thống
  - Tìm kiếm người dùng theo tên

- **Tài liệu**
  - Thêm tài liệu
  - Sửa thông tin tài liệu
  - Xóa tài liệu người dùng sở hữu
  - Tìm kiếm tài liệu theo tiêu đề, tên loại tài liệu, tên người tạo
  - Bình luận tài liệu
  - Lọc tài liệu theo số lượt xem
  - Xem những tài liệu đang chờ phê duyệt của bản thân
- **Nhận thông báo bài viết mới**
  - Đăng ký nhận email thông báo khi có bài viết mới.
  - Huỷ đăng ký nhận thông báo email.
  

### 2. Quản trị viên (Admin)
- **Tài khoản**
  - Đăng nhập

- **Quản lý người dùng**
  - Tìm kiếm, sắp xếp người dùng 
  - Khóa tài khoản người dùng
  - Mở khóa tài khoản người dùng

- **Tài liệu**
  - CRUD danh mục tài liệu
  - Tìm kiếm, sắp xếp tài liệu
  - Duyệt tài liệu
  - Từ chối tài liệu
  
- **Thể loại**
  - CRUD danh mục thể loại

## Hướng dẫn Cài đặt

1. Clone repository:
   ```bash
   git clone https://github.com/UserVVH/EduTech_API
2. Thay đổi thông tin MySQL Database Configuration:
   ```bash
   spring.datasource.url={connection string}
   spring.datasource.username={username}
   spring.datasource.password={password}
3. Chạy chương trình:
4. Vào mysql tạo role:
   ```bash
   INSERT INTO defaultdb.role (id, name) VALUES (1, 'ADMIN');
   INSERT INTO defaultdb.role (id, name) VALUES (2, 'USER');
   INSERT INTO defaultdb.role (id, name) VALUES (3, 'STUDENT');
   INSERT INTO defaultdb.role (id, name) VALUES (4, 'TEACHER');
5. Chạy lại chương trình 1 lần nữa và tận hưởng thành quả!!!


## Thông tin liên hệ
- **Email**: vuvanhaidt1@gmail.com
- **Phone/Zalo**: 0965119221
   
