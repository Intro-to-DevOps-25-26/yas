# Ubuntu Master YAS Deploy Pod Issue Assignment

Ngay: 2026-07-06

## 1. Node / Person Mapping

- `master` - `Tú`: control-plane
- `worker-1` - `Hòa`: chay workload app
- `naul1-pc` - `Luân`: chay workload app
- `worker-3` - `Khoa`: chay workload app

## 2. Pod Dang Loi / Chua On Dinh

### `master` - `Tú`

- CoreDNS da duoc pin sang worker node va da dat `2/2 Ready`
- Pod network tren master da duoc fix:
  - `kube-apiserver` advertise ve `192.168.2.16`
  - `Endpoints/default/kubernetes` tro ve `192.168.2.16`
  - `ufw` da mo `6443/tcp` cho `10.244.0.0/16`
  - pod tren master da chạm duoc `10.96.0.1:443`

### `worker-1` - `Hòa`

- Da hoan thanh phan giao:
  - `keycloak/keycloak-0`: `Running`
  - `yas/customer-*`: `Running`
  - `yas/inventory-*`: `Running`
  - `yas/product-*`: `Running`
  - `yas/tax-*`: `Running`
- Cac pod con loi tren `worker-1` nhung thuoc scope nhom khac:
  - Hien tai khong con pod app active nao loi tren `worker-1`
  - Cac pod rollout cu/obsolete neu co da khong con thuoc scope debug chinh

### `naul1-pc` - `Luân`

- `elasticsearch/elasticsearch-es-node-0`: `Running`
- `yas/storefront-bff-6b58dd48c7-zr2rb`: `Running`
- Mot so pod cu cua rollout truoc con hien `Unknown`, nhung khong con la pod active

### `worker-3` - `Khoa`

- `kafka/debezium-connect-cluster-connect-0`: `Running`
- `redis/redis-replicas-0`: `Running`
- `yas/cart-*`: `Running`
- `yas/media-*`: `Running`
- `yas/search-*`: `Running`
- `yas/storefront-bff-*`: khong con pod active loi tren node nay
- `strimzi-cluster-operator-7bf889cd5c-9k6cr`: `Running` nhung chua `Ready` 100%

## 3. Phan Cong Fix

### `master` - `Tú`

- DNS cluster da hoi phuc.
- Pod network tren master da on dinh lai cho service `kubernetes`/API server.
- Xac minh service discovery cho `postgresql`, `kafka-cluster-kafka-bootstrap`, `redis-master`, `keycloak-service`.
- Neu co thay doi infra, chi can kiem tra lai DNS/service discovery va rollout cac pod backend dang phu thuoc.

### `worker-1` - `Hòa`

- Da hoan thanh phan giao:
  - `keycloak-0` da len `Running`
  - `customer`, `inventory`, `product`, `tax` da rollout thanh cong
- Khong con phan fix thuoc scope Hòa trong assignment ban dau.
- Cac pod app tren `worker-1` hien da on dinh.

### `naul1-pc` - `Luân`

- `elasticsearch-es-node-0` da `Running`.
- `storefront-bff` hien dang Running/Ready tren node nay.
- Cac pod cu cua rollout truoc con ton tai nhung khong con la workload active.

### `worker-3` - `Khoa`

- `debezium-connect-cluster-connect-0` da `Running`.
- `redis-replicas-0` da `Running`.
- Cac pod app lien quan da on dinh.
- Khong con storefront-bff loi tren `worker-3`.

## 4. Thu Tu De Xu Ly

1. `master` - `Tú`: da hoan tat DNS / CoreDNS va overlay.
2. `worker-1` - `Hòa`: da hoan thanh.
3. `worker-3` - `Khoa`: da hoan thanh.
4. `naul1-pc` - `Luân`: da hoan thanh cho phan node / infrastructure.
5. Phan con lai: don dep pod cu va pod kiem tra.

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
- `master` da sua them phan host firewall va apiserver advertise IP de pod network chay lai.
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

## 10. Trang Thai Doi Chieu Moi Nhat

- Da doi chieu lai sau cap nhat gan nhat:
  - `keycloak`, `customer`, `inventory`, `product`, `tax`, `debezium-connect-cluster-connect`, `redis-replicas`, `elasticsearch-es-node`, `coredns`, `storefront-bff` dang o trang thai dung.
  - Khong con nhom app chinh nao CrashLoopBackOff.
  - Cac pod con loi chu yeu la pod cu, pod one-off, va pod test/verify:
    - `dns-master`
    - `master-iptables-check`
    - `master-routing-check`
    - `sampledata-seed-manual-cp`
    - `sampledata-seed-manual-ip`
    - `strimzi-cluster-operator`
    - mot so pod rollout cu tren `naul1-pc`
- Cac muc da danh dau `hoan thanh` trong file hien nay phu hop hon voi thuc te.
- Neu can cap nhat tiep, uu tien don dep pod cu va pod kiem tra khong con dung nua.
