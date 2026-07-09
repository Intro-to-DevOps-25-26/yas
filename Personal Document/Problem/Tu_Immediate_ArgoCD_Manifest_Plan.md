# Tu Immediate Work Plan

## Muc tieu

- Chot duoc chart/manifest va health check co ban truoc.
- Tao khung ArgoCD de co the sync `dev` va `staging` sau nay.
- Co the bat dau GitOps voi image tag co dinh truoc khi CI/CD hoan thien.

## Pham vi lam ngay

### 1. Chart / Manifest

- Da xong:
  - Audit chart/manifest co ban cho 14 service core.
  - Chuyen `sampledata` sang `Job` seed 1 lan.
  - Tao overlay chung `values-dev.yaml` va `values-staging.yaml`.
  - Test render `helm template` cho toan bo chart core.
  - `helm lint` pass cho toan bo chart core.
  - Mo rong `swagger-ui` ingress de route duoc den 12 backend API docs path.
  - Noi duoc ArgoCD app skeleton vao chart/values thong qua `ApplicationSet` cho `dev` va `staging`.
- Dang lam:
  - Pin image tag co dinh cho cac chart can test.
- Con lai:
  - Tach overlay theo tung chart neu can chi tiet hon cho `dev` va `staging`.
  - Doi chieu lai namespace, selector, port, probe, config map va secret cho tat ca chart con lai.
  - Test `helm upgrade --install` voi tag co dinh va ghi lai ket qua render.

### 2. Health Check

- Da xong:
  - Xac nhan backend chart dung actuator health tren `metric` port.
  - Xac nhan `ui` chart dung probe `/`.
  - Xac nhan `swagger-ui` dung probe `/swagger-ui`.
  - Xac nhan `product` da ve `1/1 Running`.
  - Xac nhan `sampledata` Job da seeding xong.
- Con lai:
  - Kiem tra lai probe cho tung service khi rollout thuc te.
  - Dam bao khong con probe rewrite/kieu Istio cu con sot trong deployment spec.
  - Chot log startup/readiness sau khi pin tag co dinh.
  - Xac nhan service nao con 0/1, service nao da Ready 1/1.

### 3. ArgoCD Skeleton

- Da xong:
  - Tao `AppProject` skeleton.
  - Tao `Application` skeleton cho `dev` va `staging`.
  - Co template ArgoCD app de nhan values overlay.
  - Co `ApplicationSet` skeleton cho `dev` va `staging` noi thang vao chart/values.
  - `kubectl kustomize` pass cho `argocd/apps/dev` va `argocd/apps/staging`.
- Con lai:
  - Chot sync policy, prune, selfHeal, rollback flow theo tag co dinh.
  - Test sync thu cong sau khi tag duoc pin.
  - Kiem tra ArgoCD app/project status, diff, va rollback bang revision.
  - Xac nhan CRD ArgoCD da co tren cluster truoc khi apply that.

### 4. Screenshot / Report

- Da xong:
  - Co audit doc rieng cho chart/manifest.
  - Co skeleton ArgoCD va ghi chu trong report.
- Con lai:
  - Chup screenshot render chart/values sau khi pin tag xong.
  - Chup screenshot ArgoCD app/project khi sync on dinh.
  - Tong hop ket qua `sampledata Job`, expose matrix, va health check vao report.
  - Viet report ngan theo luong: chart -> health -> GitOps -> sync -> rollback.

## Thu tu lam

1. Chot image tag co dinh cho chart can test.
2. Kiem tra lai health check sau khi rollout.
3. Hoan thien expose matrix `NodePort` / `Ingress` va xac nhan route swagger docs.
4. Test sync thu cong ArgoCD voi chart/values da noi.
5. Ghi lai ket qua va screenshot.

## GitOps flow de lam

1. Chot `Deployment`, `Service`, `Job`, `values-dev.yaml`, `values-staging.yaml`.
2. Pin image tag co dinh cho tung chart can test.
3. Render chart bang `helm template` va test `helm upgrade --install` thu cong.
4. Tao `AppProject` va `Application` / `ApplicationSet` cho `dev` va `staging`.
5. Sync thu cong mot app truoc, xem `Healthy` / `Synced`.
6. Kiem tra `diff` giua Git va cluster.
7. Test rollback bang revision ArgoCD.
8. Chup screenshot va tong hop vao report.

## Dan y report theo de bai

1. Mo ta muc tieu GitOps / ArgoCD cho `dev` va `staging`.
2. Chup cac buoc cau hinh:
   - chart/manifest
   - values `dev/staging`
   - `AppProject`
   - `Application` / `ApplicationSet`
   - sync policy, prune, selfHeal
3. Chup ket qua render va rollout thu cong:
   - `helm template`
   - `helm upgrade --install`
   - `kubectl get pods`, `kubectl get svc`
4. Chup ket qua sync thu cong ArgoCD:
   - `Synced`
   - `Healthy`
   - diff neu co
5. Chup ket qua rollback:
   - revision truoc/sau
   - trang thai app sau rollback
6. Tong hop ket luan:
   - cai gi da chot
   - cai gi con do CI/CD hay cluster
   - huong lam tiep cho `dev/staging`

## Deliverables

- Danh sach chart/manifest da chot.
- Danh sach probe da chuan hoa.
- Overlay `values-dev.yaml` va `values-staging.yaml`.
- ArgoCD skeleton cho `dev/staging`.
- Report ngan ve thu tu lam va ket qua.
- Trang thai validate `kustomize` cho `argocd/apps/dev` va `argocd/apps/staging`.
- Trang thai validate `helm lint` va `helm template` cho toan bo chart core.
