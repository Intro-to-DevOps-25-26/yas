# Problem: Docker Desktop service could not be started

## Log lỗi

Khi chạy:

```powershell
Start-Service com.docker.service
```

PowerShell trả về:

```text
Start-Service : Service 'Docker Desktop Service (com.docker.service)' cannot be started due to the following error:
Cannot open com.docker.service service on computer '.'.
```

## Giải thích lỗi

Docker Desktop trên Windows phụ thuộc vào service nền `com.docker.service`. Nếu service này chưa chạy hoặc phiên hiện tại không có quyền đủ cao, các lệnh `docker` sẽ không kết nối được tới Docker Engine.

## Nguyên nhân

- Docker Desktop Service đang ở trạng thái `Stopped`
- Phiên PowerShell hiện tại không thể mở/khởi động service đó trực tiếp
- Docker Engine chưa sẵn sàng nên các lệnh Compose bị treo chờ daemon
