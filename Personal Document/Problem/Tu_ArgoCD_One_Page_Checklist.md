# Tu ArgoCD One-Page Checklist

## Lam ngay

- Chot `Deployment`, `Service`, `Job` cho 14 core service.
- Tach `values-dev.yaml` va `values-staging.yaml`.
- Kiem tra `namespace`, `selector`, `port`, `probe`, `configMap`, `secret`.
- Chot `AppProject` skeleton.
- Chot `Application` / `ApplicationSet` skeleton.
- Chot `syncPolicy`, `prune`, `selfHeal`.

## Lam khi co tag co dinh

- Pin image tag cho cac chart can test.
- Chay `helm template`.
- Chay `helm upgrade --install` thu cong.
- Kiem tra pod `Running` / `Ready`.
- Kiem tra `Healthy` / `Synced` trong ArgoCD.
- Kiem tra `diff` giua Git va cluster.

## Cho ArgoCD sync

- Sync thu cong mot app truoc.
- Test rollback bang revision.
- Chup screenshot chart render.
- Chup screenshot ArgoCD app/project.
- Chup screenshot sync / rollback.
- Tong hop report theo luong: chart -> health -> GitOps -> sync -> rollback.

