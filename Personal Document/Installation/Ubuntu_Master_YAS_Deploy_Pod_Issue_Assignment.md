# Ubuntu Master YAS Deploy Pod Issue Assignment

Ngay: 2026-07-06

## 1. Node / Person Mapping

- `master` - `Tú`: control-plane
- `worker-1` - `Hòa`: chay workload app
- `naul1-pc` - `Luân`: chay workload app
- `worker-3` - `Khoa`: chay workload app

## 2. Pod Dang Loi / Chua On Dinh

### `master` - `Tú`

- `kube-system/coredns-66bc5c9577-7bhpt`: `0/1 Running`, DNS service chua on dinh
- `kube-system/coredns-66bc5c9577-s8sfr`: `0/1 Running`, DNS service chua on dinh

### `worker-1` - `Hòa`

- `keycloak/keycloak-0`: `CrashLoopBackOff`
- `yas/customer-6f9c5d4f6-7745j`: `CrashLoopBackOff`
- `yas/customer-ddfbb79fc-vs59c`: `CrashLoopBackOff`
- `yas/inventory-5cdcdbf9f-7n2bn`: `CrashLoopBackOff`
- `yas/product-57cbf45d9c-ltx92`: `CrashLoopBackOff`
- `yas/tax-d6fddd4d4-xkkgg`: `CrashLoopBackOff`

### `naul1-pc` - `Luân`

- `elasticsearch/elasticsearch-es-node-0`: `CrashLoopBackOff`
- `yas/backoffice-bff-5959897f4f-nxg7g`: `CrashLoopBackOff`
- `yas/backoffice-bff-d47f896bc-hrzrq`: `CrashLoopBackOff`
- `yas/inventory-78c79d5787-tvd6j`: `CrashLoopBackOff`
- `yas/order-6d89d75dfb-gsbgp`: `CrashLoopBackOff`
- `yas/storefront-bff-77d44f9f76-xqmdr`: `CrashLoopBackOff`
- `yas/tax-bdf6d7bbb-pkrrx`: `CrashLoopBackOff`

### `worker-3` - `Khoa`

- `kafka/debezium-connect-cluster-connect-0`: `CrashLoopBackOff`
- `redis/redis-replicas-0`: `Running` nhung `0/1 Ready`, con unstable
- `yas/cart-6f45ffc58-nbc5v`: `CrashLoopBackOff`
- `yas/cart-75dcc8f96b-ztmpb`: `CrashLoopBackOff`
- `yas/media-65dfbbfccd-5l7v8`: `CrashLoopBackOff`
- `yas/media-fb8b4b8bd-p5v68`: `CrashLoopBackOff`
- `yas/storefront-bff-7bb884f4bb-pmx54`: `CrashLoopBackOff`

## 3. Phan Cong Fix

### `master` - `Tú`

- Kiem tra va fix `CoreDNS` / `kube-dns` de DNS cluster hoi phuc.
- Xac minh service discovery cho `postgresql`, `kafka-cluster-kafka-bootstrap`, `redis-master`, `keycloak-service`.
- Sau khi DNS on, restart lai cac pod backend dang phu thuoc DNS.

### `worker-1` - `Hòa`

- Fix `keycloak-0` truoc, vi dang fail JDBC connection va DNS toi PostgreSQL.
- Fix `customer`, `inventory`, `product`, `tax` tren `worker-1` sau khi Keycloak va DNS on dinh.
- Kiem tra env, issuer, datasource URL va readiness probe cua cac pod nay.

### `naul1-pc` - `Luân`

- Fix `elasticsearch-es-node-0` truoc, vi dang crashloop va anh huong den search stack.
- Fix `backoffice-bff`, `inventory`, `order`, `storefront-bff`, `tax` tren node nay.
- Kiem tra resource, bootstrap checks, config va log cua Elasticsearch.

### `worker-3` - `Khoa`

- Fix `debezium-connect-cluster-connect-0` truoc, vi dang fail resolve Kafka bootstrap service.
- Kiem tra va on dinh `redis-replicas-0`.
- Fix `cart`, `media`, `storefront-bff` tren `worker-3` sau khi dependency on.

## 4. Thu Tu De Xu Ly

1. `master` - `Tú`: khong cho DNS / CoreDNS on dinh.
2. `worker-1` - `Hòa`: fix `keycloak-0`.
3. `worker-3` - `Khoa`: fix `debezium-connect-cluster-connect-0` va `redis-replicas-0`.
4. `naul1-pc` - `Luân`: fix `elasticsearch-es-node-0`.
5. Sau do quay lai restart / rollout cac pod backend app con lai.

## 5. Ghi Chu

- Cac pod `*_debug`, `host-*`, `crictl-*`, `dnscheck-*`, `netcheck-*`, `sampledata-seed-*` la pod tam thoi de debug, khong tinh vao workload chinh.
- `sampledata` da seed thu cong thanh cong, khong con la blocked item.

## 6. Nguyen Nhan Van Hanh

- Hien tai viec deploy dang chay tu workspace tren `master`, nen code va file config sua tren `master` khong tu dong sang cac may worker.
- Neu muon debug tren worker theo cach binh thuong, may do phai co source code cung branch/version voi `master`, thuong la `git pull` hoac dong bo repo truoc khi sua.
- Neu worker khong co code moi, debug se bi lech trang thai: log, chart, manifest va script khong khop voi ban dang test tren `master`.
- Cach lam dung la: chot 1 branch chung, day thay doi len remote, sau do tung may `git pull` de cung mot version truoc khi debug.
- Neu can test thu he thong tren cluster ma khong can sua code tren worker, thi chi can `kubectl` tu `master`; con neu can sua chart/script/app, worker phai cap nhat source truoc.

## 7. Checklist Cuc Ngan

- `master` sua: chart, manifest, config deploy, lenh `kubectl`, script rollout va verify cluster.
- `worker` pull: cung branch/commit voi `master`, sau do moi sua code app hoac debug local.
- Duoc debug tiep khi: worker da `git pull`, restart service/pod can test, va log/status tren worker khop voi ban vua sua.
- Luu y khi debug worker: thay doi tren worker khong lam cluster tren `master` tu dong doi theo; phai deploy/rebuild/rollout lai thi moi biet co hoat dong hay chua.
- Luu y khi fix worker: neu sua code app tren worker, phai xac dinh ro dang test local, container image, hay pod trong cluster de tranh nham moi truong.

## 8. Quy Trinh Test Dung

- Buoc 1: sua local tren worker de kiem tra y tuong va xem pod/ung dung co chay dung huong khong.
- Buoc 2: neu local test tot, commit va push len remote de giu 1 nguon source chung.
- Buoc 3: `master` pull code moi va deploy/rebuild/rollout lai de xac nhan trong cluster that.
- Buoc 4: chi can lặp buoc 1-2 nhieu lan tren worker khi dang tinh chinh, khong can moi lan sua nho deu bat `master` pull.
- Buoc 5: khi muon ket luan “pod da fix xong” thi phai co ket qua sau buoc 3, khong chi duoc dua vao local test tren worker.
- Neu pod trong cluster chay tu image da build san, thi sua file local tren worker khong co tac dung cho pod cho den khi image duoc build/push/deploy lai.

## 9. Rebuild / Deploy Tren Master

- Hien tai repo khong co 1 script “one-shot” cho toan bo rebuild/deploy YAS.
- Cac script dang co chi tach theo lop:
  - `k8s/deploy/setup-cluster.sh`: infra chung nhu PostgreSQL, Kafka, Elasticsearch, observability.
  - `k8s/deploy/setup-keycloak.sh`: Keycloak.
  - `k8s/deploy/setup-redis.sh`: Redis.
  - `k8s/deploy/deploy-yas-configuration.sh`: config/secret chung cho YAS.
  - `k8s/deploy/deploy-yas-applications.sh`: cac service YAS va `sampledata`.
- Thu tu chay tren `master` khi can rebuild/deploy lai:
  1. `cd k8s/deploy`
  2. `git pull`
  3. Neu chi doi config/secret chung thi chay `./deploy-yas-configuration.sh`
  4. Neu chi doi app chart/service thi chay `./deploy-yas-applications.sh`
  5. Neu doi infra phu thuoc thi chay `./setup-cluster.sh`, `./setup-keycloak.sh`, `./setup-redis.sh` tuy phan can cap nhat
  6. Kiem tra lai `kubectl get pods -A -o wide`, `kubectl rollout status`, va `kubectl logs`
- Neu source app thay doi va image phai build lai, can co buoc build/push image roi cap nhat tag trong values/chart truoc khi redeploy.
- Neu chi sua local tren worker ma chua push source/image moi, `master` deploy lai se van chay code cu.
- Neu muon biet pod co fix on chua trong cluster that, phai test sau buoc deploy tren `master`, khong chi duoc dua vao local test tren worker.
