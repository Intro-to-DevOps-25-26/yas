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

## Viec con lai cua Tú

### 1. Chart / Manifest

- Pin image tag co dinh cho chart can test.
- Doi chieu lai namespace, selector, port, probe, config map va secret.
- Test `helm upgrade --install` voi tag co dinh va luu ket qua.
- Chot manifest/chart de ArgoCD lay lam nguon chan ly.

### 2. Health Check

- Kiem tra service nao con `0/1` va service nao da `1/1`.
- Xac nhan `readinessProbe`, `livenessProbe`, `startupProbe` khong con di theo co che Istio cu.
- Luu log startup/readiness sau khi rollout.

### 3. ArgoCD

- Chot `syncPolicy`, `prune`, `selfHeal`, rollback flow.
- Test sync thu cong voi image tag co dinh.
- Kiem tra `Healthy` / `Synced` / diff.
- Test rollback bang revision ArgoCD.

### 4. Report / Screenshot

- Chup screenshot chart render.
- Chup screenshot ArgoCD app/project.
- Tong hop bang chung `sampledata Job`, health check, expose matrix.
- Viet report cuoi theo luong: chart -> health -> GitOps -> sync -> rollback.

## Trinh tu thuc hien thuc te

1. Chot chart/manifest va values.
2. Pin image tag co dinh.
3. Render test bang Helm.
4. Tao ArgoCD app/project.
5. Sync thu cong.
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
- Image tag da duoc pin ro rang.
- Probe va service port da dung.
- Cluster khong con loi nen nhu DNS, node, webhook, hay rollout bi chan.
- Da co quy uoc ro ve `dev` va `staging`.

## Thu tu uu tien

1. Pin image tag co dinh.
2. Kiem tra lai health check.
3. Test expose matrix.
4. Sync ArgoCD thu cong.
5. Test rollback.
6. Chup screenshot va dong goi report.

## File lien quan

- [Problem.md](./Problem.md)
- [Problem_Task_Assignment_Report.md](./Problem_Task_Assignment_Report.md)
- [Tu_Immediate_ArgoCD_Manifest_Plan.md](./Tu_Immediate_ArgoCD_Manifest_Plan.md)
- [Tu_Chart_Manifest_Audit.md](./Tu_Chart_Manifest_Audit.md)
