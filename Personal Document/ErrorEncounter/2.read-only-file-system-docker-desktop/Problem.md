# Problem

## Error log

```text
write /var/lib/desktop-containerd/daemon/io.containerd.metadata.v1.bolt/meta.db:
read-only file system
```

## Explanation

Docker Compose started pulling and creating containers successfully for a while, but Docker Desktop's internal containerd metadata store became read-only during image pull/write operations.

## Root cause

This is a Docker Desktop backend/storage issue, not a problem in the YAS compose files themselves. The Docker engine could not write to its own metadata database under `/var/lib/desktop-containerd/...`.

