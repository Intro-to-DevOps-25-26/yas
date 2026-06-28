# Problem

## Error log

```text
CreateFile D:\VisualStudio\DevOps\yas\docker-compose.yml:docker-compose.search.yml:docker-compose.o11y.yml: The filename, directory name, or volume label syntax is incorrect.
```

## Explanation

Docker Compose was started from Windows PowerShell in the repo root, but the root `.env` file defines:

```text
COMPOSE_FILE=docker-compose.yml:docker-compose.search.yml:docker-compose.o11y.yml
```

On Windows, the colon-separated `COMPOSE_FILE` value is treated as one invalid path instead of a file list, so Compose cannot resolve the configuration files.

## Root cause

The project uses a Unix-style file list separator in `COMPOSE_FILE`. That works in some shells, but it does not work correctly in this Windows PowerShell run.

