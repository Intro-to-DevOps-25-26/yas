# Tu ArgoCD GitOps Report

## Muc dich

- Tong hop phan viec cua Tú cho huong GitOps / ArgoCD.
- Ghi ro cac buoc lam, cai gi da xong, cai gi con lai.
- Lam tai lieu de doi chieu voi `Problem.md` va `Tu_Immediate_ArgoCD_Manifest_Plan.md`.

## Tong quan luong lam

GitOps o day khong bat dau bang ArgoCD ngay lap tuc. Luong hop ly la:

1. Chot chart / manifest truoc.
2. Chuan hoa health check va expose matrix.
3. Pin image tag co dinh de co nguon chan ly on dinh.
4. Tao ArgoCD `AppProject` va `Application` / `ApplicationSet`.
5. Sync thu cong, kiem tra diff, rollback.
6. Sau do moi chuyen sang flow `dev` / `staging` on dinh.

## Cac buoc cau hinh va thuc hien

### 1. Chot chart / manifest

- Kiem tra tung chart co `Deployment`, `Service`, `Job` dung mo hinh.
- Xac dinh `namespace`, `selector`, `ports`, `configMap`, `secret`, `resources`.
- Chuan hoa `readinessProbe`, `livenessProbe`, `startupProbe`.
- Tach `values-dev.yaml` va `values-staging.yaml`.
- Pin image tag co dinh cho tung chart can test.

### 2. Kiem tra render va rollout thu cong

- Chay `helm template` de xac nhan YAML sinh ra hop le.
- Chay `helm upgrade --install` thu cong voi tag co dinh.
- Kiem tra `kubectl get pods`, `kubectl get svc`, `kubectl describe pod`.
- Xac nhan service nao con `0/1`, service nao da `1/1`.

### 3. Tao ArgoCD skeleton

- Tao `AppProject` de gioi han repo, namespace va project scope.
- Tao `Application` hoac `ApplicationSet` cho `dev` va `staging`.
- Gan ArgoCD vao dung chart path va values overlay.
- Chot `syncPolicy`, `prune`, `selfHeal`, rollback flow.

### 4. Sync thu cong

- Apply skeleton len cluster co ArgoCD.
- Sync thu cong mot app truoc de test duong di.
- Kiem tra trang thai `Synced` / `Healthy`.
- Xem diff giua Git va cluster neu co sai khac.

### 5. Test rollback

- Doi image tag hoac manifest de tao mot revision moi.
- Kiem tra rollout co thanh cong khong.
- Rollback bang revision ArgoCD.
- Xac nhan app quay ve trang thai cu an toan.

### 6. Chup screenshot va viet report

- Chup man hinh chart render.
- Chup man hinh ArgoCD app/project.
- Chup man hinh diff / sync / rollback neu co.
- Tong hop ket qua vao report va checklist chung.

## Viec da xong

- Audit chart/manifest co ban cho 14 service core.
- Chuyen `sampledata` sang `Job` seed 1 lan.
- Tao overlay chung `values-dev.yaml` va `values-staging.yaml`.
- Test render mot so chart bang `helm template`.
- Mo rong `swagger-ui` ingress de route duoc den backend API docs.
- Tao skeleton ArgoCD cho `AppProject`, `Application`, `ApplicationSet`.

## Da xac minh them

- `helm template` pass cho toan bo chart core.
- Overlay `values-dev.yaml` va `values-staging.yaml` da co san de pin tag khi can test nhanh.
- `helm lint` pass cho toan bo chart core.
- `helm upgrade --install` da test fallback voi `latest` tren namespace `yas-dev`.
- Cac workflow service rieng da duoc chuan hoa sang SHA tag khi build/push, de tranh phu thuoc vao `latest`.
- Da co helper `k8s/deploy/sync-gitops-image-tag.sh` de cap nhat overlay dev/staging theo SHA tag.
- `values-dev.yaml` va `values-staging.yaml` da duoc pin tam thoi sang `0c605cb4` de test GitOps flow local.
- `helm template` da pass lai cho `product` va `storefront-ui` voi overlay SHA moi.
- `yas-configuration` da duoc install vao `yas-dev` de cap configmap/secret cho cac service core.
- `serviceMonitor.enabled` da duoc tat trong overlay dev/staging vi cluster hien tai chua co CRD `ServiceMonitor`.
- Tag immutable hien tai chua co trong registry; `dev-fixed` / `staging-fixed` dang blocked cho den khi CI/CD publish tag co dinh.
- `kubectl kustomize` pass cho:
  - `argocd/apps/dev`
  - `argocd/apps/staging`
- Cluster hien tai da co CRD ArgoCD (`AppProject`, `Application`), nen co the apply `AppProject` / `Application` that.
- Da port-forward thanh cong `argocd-server` de test UI local.
- Da lay initial admin password tu `argocd-initial-admin-secret`.
- Da apply `AppProject` / `Application` len cluster va sync thu cong `product-dev` thanh cong.
- `product-dev` da re-render dung khi bo overlay file ngoai chart path va chuyen sang values inline.
- `product` da chay lai 1/1 Running sau khi tạm pin image `latest` vi SHA `0c605cb4` chua co tren registry.
- Da chuyen toan bo Application `dev/staging` sang inline values hoac values.yaml trong chart path de tranh overlay ngoai path.
- Da apply lai bo Application `dev/staging` len cluster sau khi chuyen inline values.
- Da clear het reference overlay ngoai chart path trong `argocd/apps`.
- Da apply `ApplicationSet` live cho `yas-dev` va `yas-staging`.
- Da force delete `argocd-application-controller-0` bi ket tren `worker-3` de cho pod moi len `worker-1`.
- `argocd-application-controller` da quay lai `Running`.
- `product` da quay ve `1/1 Running`.
- `sampledata` Job da seeding xong va `Complete 1/1`.
- ArgoCD namespace `argocd` da install xong; controller, server, repo-server, redis, dex, notifications, applicationset deu da Ready.
- `argocd-initial-admin-secret` da duoc tao trong namespace `argocd`.

## Viec con lai cua Tú

### 1. Viec co the lam ngay da xong

- Chart / manifest da audit co ban.
- `sampledata` da chuyen sang `Job` seed 1 lan.
- `values-dev.yaml` va `values-staging.yaml` da co overlay.
- `helm template` va `helm lint` da pass cho toan bo chart core.
- `kubectl kustomize` da pass cho `argocd/apps/dev` va `argocd/apps/staging`.
- `product` da ve `1/1 Running`.
- `sampledata` Job da seeding xong va `Complete 1/1`.

### 2. Viec con lai chua lam xong

- Cho CI/CD cua Luân publish immutable tag that thi moi pin duoc tag that cho `dev` / `staging`.
- Sau khi co tag that:
  - sync thu cong cac app con lai
  - kiem tra `Healthy` / `Synced`
  - test rollback theo revision
  - chup screenshot va dong goi bao cao cuoi
- Hien tai cac Application da tao ra, nhung chi `product-dev` da sync xong; cac app con lai can sync tiep neu muon dong bo day du trang thai GitOps.

## Task con lai cho ArgoCD

- Thay `latest` bang immutable SHA tag khi CI/CD publish xong.
- Sync lai cac app con lai neu image registry da co tag that.
- Test rollback cho app da sync on dinh.
- Chup screenshot:
  - AppProject / Application YAML
  - ArgoCD UI `Synced` / `Healthy`
  - Diff / sync / rollback neu co
- Cap nhat report final theo ket qua that tren cluster.

### 3. Viec con lai chi phu thuoc CI/CD / cluster

- Immutable image tag thay cho `latest`.
- Sync/rollback that trong ArgoCD thay vi chi render local.
- Capture screenshot thuc te cua app/project trong ArgoCD.

## Trinh tu thuc hien thuc te

1. Chot chart/manifest va values.
2. Cho CI/CD co immutable tag that.
3. Cap nhat values dev/staging bang tag SHA that.
4. Apply ArgoCD app-project that.
5. Sync thu cong cac app con lai.
6. Test rollback.
7. Chup screenshot.
8. Cap nhat report va checklist.

## Dan y bao cao theo de bai

### 1. Gioi thieu

- Muc tieu GitOps / ArgoCD cho `dev` va `staging`
- Vi sao can tach ra khoi CD thu cong

### 2. Cau hinh chart / manifest

- `Deployment`, `Service`, `Job`
- `values-dev.yaml`, `values-staging.yaml`
- image tag co dinh
- namespace, selector, port, probe, configMap, secret

### 3. Cau hinh ArgoCD

- `AppProject`
- `Application` / `ApplicationSet`
- `syncPolicy`, `prune`, `selfHeal`
- rollback flow

### 4. Qu trinh thuc hien

- `helm template`
- `helm upgrade --install`
- sync thu cong ArgoCD
- kiem tra `Healthy` / `Synced`
- rollback theo revision

### 5. Ket qua

- screenshot chart render
- screenshot ArgoCD app/project
- screenshot diff / sync / rollback
- tong hop trang thai thuc te

### 6. Nhan xet

- cai gi da on dinh
- cai gi con phu thuoc CI/CD
- huu huong tiep theo cho `dev/staging`

## Dieu kien de ArgoCD chay on

- Chart/manifest da on dinh.
- Image tag da duoc pin ro rang bang SHA that / digest that.
- Probe va service port da dung.
- Cluster khong con loi nen nhu DNS, node, webhook, hay rollout bi chan.
- Da co quy uoc ro ve `dev` va `staging`.
- ArgoCD controller va CRD da duoc cai dat tren cluster.
- ArgoCD server va repo/controller da Ready trong namespace `argocd`.

## Thu tu uu tien

1. Cho CI/CD publish immutable tag that.
2. Kiem tra lai health check.
3. Cap nhat values dev/staging bang tag SHA that.
4. Apply ArgoCD AppProject/Application that.
5. Sync ArgoCD thu cong.
6. Test rollback.
7. Chup screenshot va dong goi report.

## File lien quan

- [Problem.md](./Problem.md)
- [Problem_Task_Assignment_Report.md](./Problem_Task_Assignment_Report.md)
- [Tu_Immediate_ArgoCD_Manifest_Plan.md](./Tu_Immediate_ArgoCD_Manifest_Plan.md)
- [Tu_Chart_Manifest_Audit.md](./Tu_Chart_Manifest_Audit.md)
