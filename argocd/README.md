# ArgoCD Skeleton

This folder holds the GitOps skeleton for YAS.

Current intent:
- keep `dev` and `staging` separated
- define `AppProject` first
- bootstrap from `applications/`
- keep child app definitions under `apps/<env>/`

Notes:
- this is a skeleton only
- do not enable aggressive auto-sync until charts and health checks are stable
- use fixed image tags for manual validation before wiring CI outputs

