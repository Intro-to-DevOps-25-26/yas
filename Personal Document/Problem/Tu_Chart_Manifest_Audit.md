# Tu Chart / Manifest Audit

## Muc tieu

- Chot chart/manifest sao cho ArgoCD co the sync on dinh.
- Tach ro phan nao la blocker, phan nao chi la cleanup/tinh chinh.
- Bien checklist thanh cac viec co the lam ngay tren repo.

## Pham vi da audit

- Shared backend chart
- Shared UI chart
- Swagger UI chart
- `sampledata` chart
- Service values cho 14 service core
- Skeleton ArgoCD `AppProject` / `Application`

## Ket luan nhanh

- `sampledata` hien chua dung dung mo hinh seed mot lan.
- `backend` chart da co khung deploy/chay chung kha on, nhung con can chot values theo moi truong.
- `storefront-bff` va `backoffice-bff` con lien quan den ingress/route, can doi chieu voi scope NodePort/hosts truoc khi chot.
- Nhom service core dang dung `latest` o nhieu chart, khong phu hop neu muon ArgoCD sync co dinh theo dev/staging.

## Hien trang sau khi audit

- `sampledata` da duoc chuyen sang `Job` seed 1 lan.
- Da co file overlay chung `values-dev.yaml` va `values-staging.yaml` de khoa tag cho `backend` va `ui`.
- `storefront-ui` va `backoffice-ui` co the test qua `NodePort` trong overlay dev/staging.
- `swagger-ui` va `storefront-bff` / `backoffice-bff` van giu huong expose qua `Ingress` theo chart hien tai.

## Finding da xac nhan

### 1. `sampledata` van dang model nhu workload lau dai

- Hien chart `sampledata` dung backend dependency va render ra `Deployment` / `Service` nhu service thuong.
- Scope do an chot `sampledata` la seed mot lan, xong thi dung.
- Neu giu kieu Deployment thi:
  - pod co the restart lai ngoai y muon
  - ArgoCD va rollout de bi hieu nham la workload thuong
  - seed co the bi chay lai khong can thiet
- Huong sua:
  - chuyen `sampledata` sang `Job`
  - tach resource seed ra khoi service thuong
  - sau khi seed xong thi job ket thuc va khong coi la pod thuong tru

### 2. Tag image con `latest` lam sync khong on dinh

- Nhieu chart con dung `tag: latest`.
- Dieu nay khong phu hop cho:
  - test tay
  - rollback
  - ArgoCD `dev/staging`
- Huong sua:
  - pin tag co dinh cho moi lan test
  - chi cho `latest` o giai doan local / ung dung tam
  - khi vao ArgoCD phai co tag ro rang theo branch / release

### 3. `storefront-bff` va `backoffice-bff` con co ingress enabled

- Hai chart nay dang co `ingress.enabled: true`.
- Trong scope hien tai, route test chu yeu dang di theo NodePort / hosts.
- Day khong phai loi chay ngay, nhung la diem can chot scope:
  - neu khong dung ingress thi nen tat de chart gon hon
  - neu co dung ingress thi phai chot luon controller, host, path, va flow test

### 4. `product` chart co duoi khoang trang thua trong `extraApplicationConfigPaths`

- O `product/values.yaml` co duoi khoang trang cuoi dong.
- Khong phai blocker runtime, nhung tao diff ban dau va lam chart review kho doc hon.
- Nhat la khi chot manifest / ArgoCD, can clean de khong tao noise.

### 5. `backend` chart co default map / secret / probe can doi chieu lai

- `hostAliases` dang map `identity.yas.local.com -> 10.98.44.199`.
- `databaseConnectionUrl` mac dinh tro ve `postgresql.postgres.svc.cluster.local`.
- Probe mac dinh dung actuator tren port `metric`.
- Day la khung dung, nhung moi service phai duoc verify voi cluster that truoc khi chot ArgoCD.

### 6. `serviceMonitor.enabled` dang bat mac dinh trong backend

- Neu scope hien tai chua chot observability thi resource bo sung nay co the tao nhieu noise.
- Khong phai blocker chay app, nhung can quyet dinh ro:
  - giu neu muon quan sat
  - tat neu muon chart gon cho demo core

## Expose matrix hien tai

| Nhom | Cach expose | Ghi chu |
|---|---|---|
| `storefront-ui`, `backoffice-ui` | `NodePort` | Phu hop test browser / demo, co the doi qua host map neu can |
| `swagger-ui` | `Ingress` | Da co host `api.yas.local.com`, doc API tap trung |
| `storefront-bff`, `backoffice-bff` | `Ingress` | Giu backend API di qua route host, khong expose truc tiep |
| Core backend `product/cart/order/customer/inventory/tax/media/search` | `ClusterIP` | Chi goi noi bo trong cluster |
| `sampledata` | `Job` | Seed 1 lan, khong phai service thuong truc |

## Health check

- `readinessProbe` / `livenessProbe` van dung actuator cho backend.
- `ui` chart dung probe `/`.
- `swagger-ui` dung probe `/swagger-ui`.
- `sampledata` la `Job`, khong can probe lau dai; chi can xem log va exit code cua Job.

## Viec can lam ngay

1. Chuyen `sampledata` sang mo hinh `Job`.
2. Pin image tag co dinh cho chart can test.
3. Tach `values.yaml` `dev` / `staging`.
4. Chot ingress hoac NodePort cho tung service co expose.
5. Doi chieu lai probe, port, secret, config map sau khi render chart.
6. Dung ArgoCD skeleton sau khi manifest/chart render on dinh.

## Danh sach chart can audit

- `k8s/charts/product`
- `k8s/charts/cart`
- `k8s/charts/order`
- `k8s/charts/customer`
- `k8s/charts/inventory`
- `k8s/charts/tax`
- `k8s/charts/media`
- `k8s/charts/search`
- `k8s/charts/storefront-bff`
- `k8s/charts/storefront-ui`
- `k8s/charts/backoffice-bff`
- `k8s/charts/backoffice-ui`
- `k8s/charts/swagger-ui`
- `k8s/charts/sampledata`

## Thu tu lam

1. Chart/manifest
2. Health check
3. ArgoCD skeleton
4. Test tag co dinh
5. Bao cao / screenshot
