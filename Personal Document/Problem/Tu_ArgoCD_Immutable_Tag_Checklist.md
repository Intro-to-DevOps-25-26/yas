# Tu ArgoCD Immutable Tag Checklist

## Muc tieu

- Khong dung `latest` cho deploy final.
- De CI/CD tao image tag bat bien theo commit SHA hoac digest.
- De ArgoCD deploy dung tag that, sau do sync / rollback / screenshot.

## Lam ngay

- Xac nhan CI/CD da build va push image voi tag SHA that.
- Xac nhan image do ton tai trong registry:
  - `repo/service:<git-sha>`
  - hoac `repo/service@sha256:<digest>`
- Xac nhan CI workflow co xuat ra metadata:
  - `image_tag`
  - `image_ref`
  - `image_digest`
- Xac nhan workflow deploy/cooordinator da doc duoc `image_digest` khi co immutable image that.
- Kiem tra app / chart nao con trỏ `latest`.
- Kiem tra `argocd/apps/dev` va `argocd/apps/staging` con tag nao phai sua.

## File ArgoCD con `latest`

### Dev

- `argocd/apps/dev/backoffice-bff.yaml`
- `argocd/apps/dev/backoffice-ui.yaml`
- `argocd/apps/dev/cart.yaml`
- `argocd/apps/dev/customer.yaml`
- `argocd/apps/dev/inventory.yaml`
- `argocd/apps/dev/media.yaml`
- `argocd/apps/dev/order.yaml`
- `argocd/apps/dev/product.yaml`
- `argocd/apps/dev/search.yaml`
- `argocd/apps/dev/storefront-bff.yaml`
- `argocd/apps/dev/storefront-ui.yaml`
- `argocd/apps/dev/tax.yaml`

### Staging

- `argocd/apps/staging/backoffice-bff.yaml`
- `argocd/apps/staging/backoffice-ui.yaml`
- `argocd/apps/staging/cart.yaml`
- `argocd/apps/staging/customer.yaml`
- `argocd/apps/staging/inventory.yaml`
- `argocd/apps/staging/media.yaml`
- `argocd/apps/staging/order.yaml`
- `argocd/apps/staging/product.yaml`
- `argocd/apps/staging/search.yaml`
- `argocd/apps/staging/storefront-bff.yaml`
- `argocd/apps/staging/storefront-ui.yaml`
- `argocd/apps/staging/tax.yaml`

### Da khong can doi ngay

- `argocd/apps/dev/sampledata.yaml`
- `argocd/apps/staging/sampledata.yaml`
- `k8s/charts/values-dev.yaml`
- `k8s/charts/values-staging.yaml`

### Trang thai moi

- `argocd/apps/dev` va `argocd/apps/staging` da duoc thay het `latest` bang `image.digest` that lay tu image dang chay trong cluster.
- Khong con file ArgoCD core nao giu `latest` la gia tri chot cho deploy.


## Vong dau can sua

- `argocd/apps/dev/*.yaml` con `latest` o:
  - `backoffice-bff`
  - `backoffice-ui`
  - `cart`
  - `customer`
  - `inventory`
  - `media`
  - `order`
  - `product`
  - `search`
  - `storefront-bff`
  - `storefront-ui`
  - `tax`
- `argocd/apps/staging/*.yaml` con `latest` o cung cac app tuong ung.
- `k8s/charts/values-dev.yaml` va `k8s/charts/values-staging.yaml` da khong con `latest` cho backend/ui, nhung can doi sang tag that khi CI/CD publish xong.

## Lam khi co immutable tag that

- Cap nhat `values-dev.yaml` / `values-staging.yaml` sang tag SHA that hoac digest that.
- Cap nhat ArgoCD `Application` / `ApplicationSet` sang tag that.
- Deploy thu cong 1 app de xac minh:
  - `Synced`
  - `Healthy`
  - khong con drift voi Git
- Neu can, prune resource cu cua app da doi kind / hook.
- Neu app da co helper `image.digest`, uu tien dung digest thay vi `tag`.

## Cach doi khi co SHA that

- Lay SHA / digest do CI/CD tra ra, vi du:
  - `75bb697ddf24c20437203ab72882bfb8700269e2`
  - hoac `sha256:abcd...`
- Uu tien lay gia tri tu workflow output / artifact / job summary thay vi suy doan tu git local.
- Doi tat ca file ArgoCD con `latest` sang SHA that:
  - `tag: "latest"` -> `tag: "<SHA that>"`
  - neu dung digest thi doi sang `image: repo@sha256:...` neu chart ho tro
- Lam theo thu tu:
  1. `backoffice-bff`, `backoffice-ui`
  2. `product`, `cart`, `customer`, `inventory`
  3. `media`, `order`, `search`, `tax`
  4. `storefront-bff`, `storefront-ui`
- Sau khi replace xong:
  - `git diff` kiem tra
  - `helm template` neu can
  - `kubectl apply` hoac push len remote de ArgoCD refresh
  - `argocd app refresh` / `sync` theo app

## Cach CI phai xuat ra

- Workflow build image phai ghi ro trong step summary:
  - `Image tag`
  - `Image ref`
  - `Image digest`
- Workflow phai upload artifact JSON metadata de CD co the doc lai khi can.
- Workflow deploy thu cong co the nhan them `image_tag` / `image_digest` va uu tien digest neu co.
- CD / ArgoCD chi dung gia tri da duoc publish nay, khong lay `latest`.

## Cho ArgoCD sync

- Hard refresh app sau khi commit/push tag that.
- Sync tung app theo thu tu:
  1. app co runtime on dinh
  2. app expose ngoai
  3. app phu thuoc lẫn nhau
- Kiem tra:
  - pod `Running/Ready`
  - service co endpoint
  - ArgoCD `OutOfSync` ve `Synced`

## Chot cuoi

- Test rollback bang revision ArgoCD.
- Chup screenshot:
  - YAML `Application` / `AppProject`
  - UI ArgoCD `Synced/Healthy`
  - diff / rollback
- Cap nhat report final.

## Blocker hien tai

- Neu registry chua co immutable tag that, ArgoCD chi co the test tam bang `latest`.
- `sampledata` la hook / Job tam thoi, phai ghi ro trong report neu con `OutOfSync`.
- Cac app `Progressing/Degraded` phai duoc phan loai: runtime that hay status cu.
- ArgoCD manifest final chi co the chot sau khi co tag SHA that hoac digest that tren registry.
