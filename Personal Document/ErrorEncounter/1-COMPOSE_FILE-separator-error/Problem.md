# Problem: Docker Compose file syntax error on Windows

## Log lỗi

Khi chạy:

```bash
docker compose up -d
```

Docker trả về:

```text
CreateFile D:\VisualStudio\DevOps\yas\docker-compose.yml:docker-compose.search.yml:docker-compose.o11y.yml: The filename, directory name, or volume label syntax is incorrect.
```

## Giải thích lỗi

Compose đang đọc biến `COMPOSE_FILE` trong `.env` và ghép các file bằng dấu `:`. Trên Windows, dấu `:` trong ngữ cảnh này bị hiểu như một phần của đường dẫn, nên Docker không thể mở file compose hợp lệ.

## Nguyên nhân

- `.env` đang dùng `COMPOSE_FILE=docker-compose.yml:docker-compose.search.yml:docker-compose.o11y.yml`
- Cú pháp này phù hợp hơn với môi trường Unix/Linux
- Trên Windows, chạy `docker compose up` ở root repo khiến Docker cố mở một tên file không hợp lệ
