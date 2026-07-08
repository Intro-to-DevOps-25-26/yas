# Ubuntu Master YAS Redeploy Quick Guide

## Muc tieu

- Dung tren `master` de pull code moi, rebuild/deploy lai infra, config, va app YAS theo dung thu tu.

## Script goi nhanh

- Script moi: [`k8s/deploy/redeploy-yas.sh`](/home/tu-nguyen/Public/code/devOps/org-yas/yas/k8s/deploy/redeploy-yas.sh)

## Cach dung

- `./redeploy-yas.sh all`
- `./redeploy-yas.sh infra`
- `./redeploy-yas.sh config`
- `./redeploy-yas.sh apps`
- `./redeploy-yas.sh status`
- `./redeploy-yas.sh all --skip-pull`

## Thu tu chay khi redeploy toan bo

1. `git pull --ff-only`
2. `setup-keycloak.sh`
3. `setup-redis.sh`
4. `setup-cluster.sh`
5. `deploy-yas-configuration.sh`
6. `deploy-yas-applications.sh`
7. Kiem tra lai `kubectl get pods -A -o wide`

## Luu y

- Neu chi sua chart/config thi chay `config` hoac `apps`, khong can chay lai toan bo infra.
- Neu app doi image thi phai build/push image truoc khi redeploy.
- Neu chi sua local tren worker ma chua commit/push, `master` redeploy se van dung source cu.
- Sau deploy, xem `kubectl rollout status` va `kubectl logs` de xac nhan pod da on dinh.

## Scope nang cao neu chot

- `ArgoCD` se dung de quan ly `dev` va `staging`, khong deploy thu cong vao 2 namespace nay nua
- `dev` auto sync tu branch chinh hoac branch integration
- `staging` sync theo release tag
- Neu lam service mesh:
  - cai `Istio`
  - bat `mTLS`
  - them `AuthorizationPolicy`
  - them `VirtualService` retry/timeout
  - quan sat topology bang `Kiali`
- Test service mesh bang `kubectl exec` + `curl`, ghi log va screenshot lam bang chung
