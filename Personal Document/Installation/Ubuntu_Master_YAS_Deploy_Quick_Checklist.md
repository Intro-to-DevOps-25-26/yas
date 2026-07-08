# Ubuntu Master YAS Deploy Quick Checklist

Checklist ngan gon cho master truoc khi deploy YAS bang Helm.

## 1. Preflight

- [x] `kubectl get nodes -o wide` tat ca node core `Ready`
- [x] `kubectl get pods -A` khong con loi CNI / CoreDNS / control-plane
- [x] `helm version` chay duoc
- [x] `yq --version` chay duoc
- [x] `~/.kube/config` da co tren master

## 2. Thu tu deploy

- [x] `helm upgrade --install yas-configuration k8s/charts/yas-configuration -n yas --create-namespace`
- [x] `helm upgrade --install product k8s/charts/product -n yas --create-namespace`
- [x] `helm upgrade --install cart k8s/charts/cart -n yas --create-namespace`
- [x] `helm upgrade --install order k8s/charts/order -n yas --create-namespace`
- [x] `helm upgrade --install customer k8s/charts/customer -n yas --create-namespace`
- [x] `helm upgrade --install inventory k8s/charts/inventory -n yas --create-namespace`
- [x] `helm upgrade --install tax k8s/charts/tax -n yas --create-namespace`
- [x] `helm upgrade --install media k8s/charts/media -n yas --create-namespace`
- [x] `helm upgrade --install search k8s/charts/search -n yas --create-namespace`
- [x] `helm upgrade --install storefront-bff k8s/charts/storefront-bff -n yas --create-namespace`
- [x] `helm upgrade --install storefront-ui k8s/charts/storefront-ui -n yas --create-namespace`
- [x] `helm upgrade --install backoffice-bff k8s/charts/backoffice-bff -n yas --create-namespace`
- [x] `helm upgrade --install backoffice-ui k8s/charts/backoffice-ui -n yas --create-namespace`
- [x] `helm upgrade --install swagger-ui k8s/charts/swagger-ui -n yas --create-namespace`
- [x] Seed `sampledata` thu cong 1 lan, khong dung Job

## 3. Sau deploy

- [x] `kubectl get pods -n yas -o wide`
- [x] `kubectl get svc -n yas`
- [ ] `kubectl logs -n yas deploy/search`
- [x] Chay SQL seed `sampledata` cho `product` va `media`
- [x] Xac nhan `sampledata` seed xong

## 4. Hien trang

- `backoffice-ui`, `storefront-ui`, `swagger-ui` da co `NodePort`
- `yas-configuration` da xong
- `sampledata` da duoc seed thu cong 1 lan, khong giu Job chay dai
- Cac pod can tiep tuc theo doi: `keycloak`, `elasticsearch`, `redis-replicas`, `debezium-connect`, mot so app backend con dang crashloop do dependency chua on dinh

## 5. Neu chot scope nang cao

- [ ] Chot `ArgoCD` lam cong cu quan ly `dev` va `staging`
- [ ] Tao app/project rieng cho `dev` va `staging`
- [ ] Day `main` vao namespace `dev`
- [ ] Day release tag (`v1.2.3`...) vao namespace `staging`
- [ ] Chot `Istio` cho service mesh
- [ ] Bat `mTLS` giua cac service
- [ ] Cau hinh `PeerAuthentication` / `DestinationRule`
- [ ] Cau hinh `AuthorizationPolicy` gioi han service-to-service
- [ ] Cau hinh `VirtualService` retry + timeout cho loi 500
- [ ] Lay screenshot Kiali topology
- [ ] Test bang `kubectl exec` + `curl`
- [ ] Cap nhat README huong dan tung buoc neu lam service mesh
