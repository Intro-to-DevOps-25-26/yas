# Problem

## Symptom

After rebuilding `search`, the container failed during startup with:

- `env: 'bash\r': No such file or directory`
- `env: use -[v]S to pass options in shebang lines`

This prevented Nginx from proxying `http://api.yas.local/search/v3/api-docs` successfully and caused `502 Bad Gateway`.

## Explanation

The `wait-for-it.sh` script was copied into the container with Windows CRLF line endings.
Inside Linux-based containers, the shebang line becomes `#!/usr/bin/env bash\r`, so the kernel tries to execute `bash\r` instead of `bash`.

## Root cause

- `search/wait-for-it.sh` was stored with CRLF line endings.
- The same script pattern exists in `storefront-bff/wait-for-it.sh` and `backoffice-bff/wait-for-it.sh`.
- Docker copied the file as-is into the image, so the startup script failed before the service could finish booting.

