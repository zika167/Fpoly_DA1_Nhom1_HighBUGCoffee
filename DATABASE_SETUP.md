# Hướng dẫn cấu hình Database

## Cách 1: Sử dụng file cấu hình (Khuyến nghị)

1. Copy file `src/main/resources/database.properties.example` thành `src/main/resources/database.properties`
2. Chỉnh sửa thông tin trong file `database.properties`:
   ```properties
   database.url=jdbc:mysql://localhost:3306/duan1_highbugcoffee
   database.username=root
   database.password=your_password_here
   ```

## Cách 2: Sử dụng biến môi trường

Bạn có thể set biến môi trường thay vì dùng file cấu hình:

### Windows (Command Prompt)
```cmd
set DB_URL=jdbc:mysql://localhost:3306/duan1_highbugcoffee
set DB_USERNAME=root
set DB_PASSWORD=your_password_here
```

### Windows (PowerShell)
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/duan1_highbugcoffee"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password_here"
```

### Linux/Mac
```bash
export DB_URL=jdbc:mysql://localhost:3306/duan1_highbugcoffee
export DB_USERNAME=root
export DB_PASSWORD=your_password_here
```

## Lưu ý quan trọng

- File `database.properties` đã được thêm vào `.gitignore` nên sẽ không bị push lên GitHub
- Mỗi developer cần tạo file cấu hình riêng cho máy của mình
- Biến môi trường có độ ưu tiên cao hơn file cấu hình
- Nếu không có file cấu hình hoặc biến môi trường, hệ thống sẽ sử dụng cấu hình mặc định

## Kiểm tra kết nối

Chạy ứng dụng và kiểm tra console log để xem thông tin kết nối database đã được load đúng chưa. 