# Tu ArgoCD One-Page Checklist

## Da lam

- Da verify `helm template` va `helm lint` cho toan bo chart core.
- Da co `values-dev.yaml` va `values-staging.yaml`.
- Da co `AppProject` skeleton.
- Da co `Application` / `ApplicationSet` skeleton.
- Da bo sung day du `Application` cho `dev/staging`.
- Da install `yas-configuration` vao namespace test de tao configmap/secret phu thuoc.
- Da disable `serviceMonitor` o overlay dev/staging vi cluster hien tai chua co CRD.
- Da test `helm upgrade --install` fallback voi `latest` cho `storefront-ui`, `sampledata`, va `product` de kiem tra duong apply.
- Da verify `kubectl kustomize` pass cho `argocd/apps/dev` va `argocd/apps/staging`.
- Da verify `sampledata` Job seeding thanh cong va `product` da ve `1/1 Running`.
- Da gom xong ArgoCD YAML thanh commit rieng.
- Da gom xong report + overlay values thanh commit rieng.
- Da chuan hoa workflow service rieng sang SHA tag cho build/push.
- Da co helper script de sync SHA tag vao overlay dev/staging.
- Da pin tam thoi `values-dev.yaml` va `values-staging.yaml` sang `0c605cb4` de test.
- Da verify `helm template` lai cho `product` va `storefront-ui` voi overlay SHA moi.
- ArgoCD namespace `argocd` da duoc cai dat va cac component chinh da Ready.
- Da port-forward duoc `argocd-server` de test UI local.
- Da lay duoc initial admin password tu `argocd-initial-admin-secret`.
- Da apply `AppProject` va `Application` ban dau len cluster.
- Da sync thu cong `product-dev` thanh cong qua ArgoCD.
- Da xac minh `product` ve lai `1/1 Running` sau khi apply tag tam `latest` de bo qua blocker image SHA chua co tren registry.
- Da chuyen cac app con lai trong `argocd/apps/dev` va `argocd/apps/staging` sang inline values de tranh overlay ngoai chart path.
- Da apply lai bo Application `dev/staging` len cluster sau khi chuyen inline values.
- Da clear het reference `../values-dev.yaml` / `../values-staging.yaml` khoi `argocd/apps`.
- Da apply `ApplicationSet` live cho `yas-dev` va `yas-staging` len cluster.
- Da force delete controller pod cu bi ket tren `worker-3` de cho pod moi len `worker-1`.
- `argocd-application-controller` da quay lai `Running`.
- Da doi `sampledata` sang `Job` va bat `automated.prune=true` de prune resource cu khi sync.
- Da chuyen `sampledata` Job sang dang hook PostSync de seed 1 lan, tu xoa sau khi chay xong, tranh OutOfSync do workload tam thoi.

## Lam ngay

### 1. Chot chart / manifest

- Kiem tra tung chart core con lai co dung mo hinh:
  - `Deployment`
  - `Service`
  - `Job`
- Doi chieu lai:
  - `namespace`
  - `selector`
  - `port`
  - `probe`
  - `configMap`
  - `secret`
- Dam bao `sampledata` chi con la `Job` seed 1 lan.
- Dam bao `yas-configuration` da co san va khong bi thieu config phu thuoc.
- Kiem tra them cac service runtime khac ngoai `yas-dev` neu can mo rong pham vi.

### 2. Tach overlay

- Duy tri `values-dev.yaml` va `values-staging.yaml`.
- Pin tag test tam thoi trong overlay de khong dung hard-code lan lon trong chart goc.
- Ghi ro cai nao chi dung cho dev, cai nao giu cho staging.

### 3. Chot ArgoCD skeleton

- Hoan thien `AppProject` cho:
  - `yas-dev`
  - `yas-staging`
- Hoan thien `Application` / `ApplicationSet` cho day du service.
- Chot `syncPolicy`:
  - `CreateNamespace=true`
  - `automated.prune=false`
  - `automated.selfHeal=false`
- Kiem tra cluster co CRD ArgoCD truoc khi apply `AppProject` / `Application` that.
- Viec apply that phai doi cluster co ArgoCD controller/CRD.

### 4. Kiem tra dau ra can co

- Moi app phai co manifest ro rang trong `argocd/apps/dev` va `argocd/apps/staging`.
- Moi app phai map dung chart path va values overlay.
- Phan deliverable phai co the chup man hinh duoc.

## Lam khi co tag co dinh

### 1. Pin image tag

- Dung tag co dinh cho cac chart can test.
- Tren repo hien tai, neu tag co dinh chua co trong registry thi ghi ro blocker.
- Khong dung `latest` lam chot cuoi neu can report final.

### 2. Render va install thu cong

- Chay `helm template` cho tung chart can xac minh.
- Chay `helm upgrade --install` thu cong theo namespace test.
- Kiem tra output:
  - `kubectl get pods`
  - `kubectl get svc`
  - `kubectl describe pod`

### 3. Xac minh trang thai

- Pod phai sang `Running` va `Ready`.
- ArgoCD phai bao:
  - `Synced`
  - `Healthy`
- Doi chieu diff giua Git va cluster.
- Xac nhan `sampledata` Job `Complete 1/1` khi seed xong.

### 4. Danh dau blocker

- Neu registry chua co immutable tag:
  - ghi vao report
  - dung `latest` chi de test duong di
  - khong chot la trang thai final
- Day la blocker chinh con lai phia CI/CD.

## Cho ArgoCD sync

### 1. Sync thu cong

- Sync tung app mot, uu tien app don gian truoc.
- Kiem tra `project`, `namespace`, `source path`, `valueFiles`.

### 2. Kiem tra state

- Xac minh:
  - `Synced`
  - `Healthy`
  - `OutOfSync` neu co sai
- Ghi lai revision, image tag, va namespace dang sync.

### 3. Test rollback

- Doi manifest hoac image tag de tao revision moi.
- Rollback bang revision ArgoCD.
- Kiem tra app quay ve state cu on dinh.

### 4. Chup bang chung

- Chup screenshot chart render.
- Chup screenshot app/project trong ArgoCD.
- Chup screenshot sync / rollback.

### 5. Report

- Tong hop report theo luong:
  - chart
  - health
  - GitOps
  - sync
  - rollback
- Neu co blocker thi ghi ro:
  - do cluster
  - do registry
  - do config
  - do app runtime

## Con lai theo trang thai hien tai

- Dang cho CI/CD cua Luân publish immutable SHA tag that cho tat ca image can chot.
- Cac app da duoc chuan hoa inline values, chi con doi tag immutable that de thay `latest`.
- `argocd-application-controller` dang bootstrap lai tren `worker-1`, nen status cua cac app moi tao co the chua hien day du ngay.
- Cac app khac ngoai `product-dev` hien da tao ra, nhung con dang chua sync day du theo status live.
- `sampledata-dev/staging` con can prune/sync lai de bo resource cu con treo.
- `sampledata-staging` co the fail generate manifest neu repo-server khong resolve duoc `github.com`.
- `sampledata` hook job can refresh/sync lai sau khi doi sang PostSync hook.
- Sau khi co tag that:
  - sync thu cong app con lai
  - test rollback
  - chup screenshot
  - dong goi report final

## Checklist can lam de chot

1. Pin image tag final bang commit SHA that hoac digest that.
2. Cap nhat inline values / overlay neu co app moi trong future.
3. Sync thu cong app con lai theo dung tag that.
4. Kiem tra `Healthy` / `Synced`.
5. Test rollback bang revision ArgoCD.
6. Chup screenshot va dong goi report final.
