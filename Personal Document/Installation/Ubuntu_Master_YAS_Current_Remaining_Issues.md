# Ubuntu Master YAS Current Remaining Issues

Ngay: 2026-07-07

## 1. Tong Quan Hien Tai

- Cac service app chinh da on dinh:
  - `backoffice-bff`
  - `customer`
  - `inventory`
  - `media`
  - `order`
  - `product`
  - `search`
  - `storefront-bff`
  - `storefront-ui`
  - `swagger-ui`
  - `tax`
- Cac service ha tang chinh da Running/Ready:
  - `keycloak`
  - `postgres`
  - `redis`
  - `kafka`
  - `elasticsearch`
  - `coredns`
  - `kube-flannel`

## 2. Con Lai Can Theo Doi

### Pod kiem tra / one-off

- `default/dns-master`: `Pending`
- `default/master-iptables-check`: `Error`
- `default/master-routing-check`: `Error`
- `default/sampledata-seed-manual-cp`: `Failed`
- `default/sampledata-seed-manual-ip`: `Failed`
- `kafka/strimzi-cluster-operator-7bf889cd5c-9k6cr`: `Running` nhung chua `Ready` 100%

### Pod cu / rollout cu

- Tren `naul1-pc` con mot so pod cu cua rollout cu dang hien `Unknown`, nhung khong phai workload active hien tai:
  - `customer-769d8454bc-5h455`
  - `inventory-56cb4b77fb-sm8mk`
  - `product-5ccdfdd77d-kv46d`
  - `search-5df58bc7b7-pwfr2`
  - `tax-c9f974f68-zm2db`
  - `redis-replicas-1`

## 3. Ket Luan Ngan

- Khong con nhom app chinh nao dang CrashLoopBackOff nhu truoc.
- `storefront-bff` da chuyen sang trang thai Running/Ready.
- `coredns` hien tai da Ready `2/2`.
- Cac van de con lai chu yeu la pod kiem tra, pod cu, mot so pod one-off chua clean up, va `strimzi-cluster-operator` chua Ready hoan toan.
