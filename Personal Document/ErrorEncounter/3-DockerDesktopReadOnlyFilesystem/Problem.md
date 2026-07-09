# Problem: Docker Desktop VM reports read-only filesystem

## Log lỗi

Trong log `C:\Users\Admin\AppData\Local\Docker\log\vm\init.log` có dòng:

```text
write /var/lib/desktop-containerd/daemon/io.containerd.metadata.v1.bolt/meta.db: read-only file system
```

Sau đó Docker Desktop báo:

```text
backend process exited: exit status 1
```

## Giải thích lỗi

Docker Desktop VM đang cố ghi metadata của containerd nhưng filesystem bên trong VM đang ở trạng thái chỉ đọc. Khi đó Docker không thể pull image hay tạo container, nên Compose sẽ fail ngay trong bước tải image.

## Nguyên nhân

- Docker Desktop VM / WSL backend bị lỗi trạng thái filesystem
- Metadata store của container runtime không ghi được
- Docker Desktop tự shutdown daemon sau khi gặp lỗi hệ thống
