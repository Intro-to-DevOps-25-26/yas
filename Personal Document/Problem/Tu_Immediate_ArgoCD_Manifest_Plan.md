# Tu Immediate Work Plan

## Muc tieu

- Chot duoc chart/manifest va health check co ban truoc.
- Tao khung ArgoCD de co the sync `dev` va `staging` sau nay.
- Khong phu thuoc vao CI/CD cua Luan de bat dau.

## Pham vi lam ngay

### 1. Chart / Manifest

- Da xong:
  - Audit chart/manifest co ban cho 14 service core.
  - Chuyen `sampledata` sang `Job` seed 1 lan.
  - Tao overlay chung `values-dev.yaml` va `values-staging.yaml`.
  - Test render `helm template` cho `sampledata`, `product`, `storefront-ui`, `backoffice-ui`.
  - Mo rong `swagger-ui` ingress de route duoc den 12 backend API docs path.
  - Noi duoc ArgoCD app skeleton vao chart/values thong qua `ApplicationSet` cho `dev` va `staging`.
- Dang lam:
  - Chot image tag co dinh thay cho `latest` tren cac chart can test khi co tag release thuc.
- Con lai:
  - Tach overlay theo tung chart neu can chi tiet hon cho `dev` va `staging`.
  - Chot manifest/reference cho ArgoCD render dung tag theo moi moi truong.
  - Doi chieu lai namespace, selector, port, va config map/secret cho tat ca chart con lai.

### 2. Health Check

- Da xong:
  - Xac nhan backend chart dung actuator health tren `metric` port.
  - Xac nhan `ui` chart dung probe `/`.
  - Xac nhan `swagger-ui` dung probe `/swagger-ui`.
- Con lai:
  - Kiem tra lai probe cho tung service khi rollout thuc te.
  - Dam bao khong con probe rewrite/kieu Istio cu con sot trong deployment spec.
  - Chot log startup/readiness sau khi pin tag co dinh.

### 3. ArgoCD Skeleton

- Da xong:
  - Tao `AppProject` skeleton.
  - Tao `Application` skeleton cho `dev` va `staging`.
  - Co template ArgoCD app de nhan values overlay.
  - Co `ApplicationSet` skeleton cho `dev` va `staging` noi thang vao chart/values.
- Con lai:
  - Chot sync policy, prune, selfHeal, rollback flow theo tag co dinh.
  - Test sync thu cong sau khi tag duoc pin.

### 4. Screenshot / Report

- Da xong:
  - Co audit doc rieng cho chart/manifest.
  - Co skeleton ArgoCD va ghi chu trong report.
- Con lai:
  - Chup screenshot render chart/values sau khi pin tag xong.
  - Chup screenshot ArgoCD app/project khi sync on dinh.
  - Tong hop ket qua `sampledata Job`, expose matrix, va health check vao report.

## Thu tu lam

1. Chot image tag co dinh cho chart can test.
2. Kiem tra lai health check sau khi rollout.
3. Hoan thien expose matrix `NodePort` / `Ingress` va xac nhan route swagger docs.
4. Test sync thu cong ArgoCD voi chart/values da noi.
5. Ghi lai ket qua va screenshot.

## Deliverables

- Danh sach chart/manifest da chot.
- Danh sach probe da chuan hoa.
- Overlay `values-dev.yaml` va `values-staging.yaml`.
- ArgoCD skeleton cho `dev/staging`.
- Report ngan ve thu tu lam va ket qua.
