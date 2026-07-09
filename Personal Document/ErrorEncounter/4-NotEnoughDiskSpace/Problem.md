# Problem: Docker Desktop cannot write logs because the disk is full

## Log lỗi

Trong `C:\Users\Admin\AppData\Local\Docker\log\host\com.docker.backend.exe.log` có dòng:

```text
Failed to fire hook: writing to log file: write \\?\C:\Users\Admin\AppData\Local\Docker\log\host\com.docker.backend.exe.log: There is not enough space on the disk.
```

## Giải thích lỗi

Docker Desktop cần ghi log và metadata vào ổ đĩa hệ thống. Khi ổ đĩa đầy, backend không thể ghi tiếp, dẫn tới lỗi khởi động engine, timeout khi ping daemon, và Compose không thể pull image hay tạo container.

## Nguyên nhân

- Ổ đĩa hệ thống không còn đủ dung lượng trống
- Docker Desktop log files tăng kích thước lớn theo thời gian
- Backend và container runtime không ghi được dữ liệu cần thiết để khởi động ổn định
