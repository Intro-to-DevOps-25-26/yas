# Problem Task Assignment Report

## Muc tieu

- Chot phan viec con lai cua do an theo 4 nguoi.
- Sap xep thu tu lam de tranh lam sai scope, tranh ArgoCD lam truoc khi chart/manifest chua on dinh.

## Thu tu uu tien

1. Chot chart/manifest + health check cho 14 service core.
2. Hoan thien CI/CD build/push/deploy/cleanup.
3. Rollout/rollback, NodePort, hosts va test route.
4. Lam ArgoCD dev/staging sau cung.
5. Cap nhat screenshot, report va checklist theo trang thai thuc te.

## Phan cong

| Nguoi | Viec chinh | Deliverables |
|---|---|---|
| Tú | Manifest/Chart + ArgoCD | `Deployment`, `Service`, `Job`, `values.yaml` cho `dev/staging`, `Application`/`AppProject`, sync policy, rollback flow, screenshot ArgoCD |
| Luân | CI/CD build/push/deploy/cleanup | GitHub Actions build/push image, `developer_build`, cleanup job, rule branch/tag -> image, screenshot workflow |
| Hòa | Health check + chart tuning | `readinessProbe`, `livenessProbe`, `startupProbe`, actuator health, sua chart/values, `sampledata` Job, log startup/runtime |
| Khoa | Rollout/rollback + NodePort + test route | Rollout status, rollback, NodePort, hosts, test web app/API docs, cleanup pod cu, screenshot cluster/UI |

## Ghi chu

- Bo qua service mesh trong scope hien tai.
- ArgoCD chi lam sau khi manifest/chart va health check da on dinh.
- Cac screenshot va log chung can doi chieu voi cluster thuc te truoc khi nop report.

