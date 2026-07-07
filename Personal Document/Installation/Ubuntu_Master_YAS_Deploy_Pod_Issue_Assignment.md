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
  - `yas/backoffice-bff-5959897f4f-shlts`: `CrashLoopBackOff`
  - `yas/backoffice-bff-d47f896bc-m5vjn`: `CrashLoopBackOff`
  - `yas/media-65dfbbfccd-p77wm`: `CrashLoopBackOff`
  - `yas/media-fb8b4b8bd-nlp47`: `CrashLoopBackOff`
  - `yas/search-7cf85d657-lf4ts`: `CrashLoopBackOff`
  - `yas/search-8654565bbf-npv9m`: `CrashLoopBackOff`
  - `yas/storefront-bff` khong con nam tren `worker-1`; da chuyen sang `worker-3`

### `naul1-pc` - `Luân`

- `elasticsearch/elasticsearch-es-node-0`: `Running`
- `yas` workload tren node nay da duoc giat ve `worker-1` sau khi rollout lai

### `worker-3` - `Khoa`

- `kafka/debezium-connect-cluster-connect-0`: `Running`
- `redis/redis-replicas-0`: `Running`
- `yas/cart-*`: `Running`
- `yas/media-*`: `Running`
- `yas/storefront-bff-*`: `Running` nhung chua Ready
- `yas/storefront-bff-6cf797d46c-99z7t`: `CrashLoopBackOff` do `UnknownHostException: identity.yas.local.com`
- `yas/storefront-bff-7759d6f44d-qv5kg`: `Error` / khong bind duoc `8090`

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

### `naul1-pc` - `Luân`

- `elasticsearch-es-node-0` da `Running`.
- Cac pod app lien quan da duoc chuyen ve node khac sau khi overlay on dinh.
- Phan con lai hien chu yeu nam o `backoffice-bff`, `media`, `search`, `storefront-bff` tren `worker-1`.

### `worker-3` - `Khoa`

- `debezium-connect-cluster-connect-0` da `Running`.
- `redis-replicas-0` da `Running`.
- Cac pod app lien quan chua on dinh hoan toan:
  - `storefront-bff` van fail startup do khong resolve duoc `identity.yas.local.com`
  - can kiem tra lai DNS / route tu pod tren `worker-3` toi `10.96.0.10:53`
- Khong the ket luan `worker-3` da hoan thanh cho toan bo workload app.

## 4. Thu Tu De Xu Ly

1. `master` - `Tú`: da hoan tat DNS / CoreDNS va overlay.
2. `worker-1` - `Hòa`: da hoan thanh.
3. `worker-3` - `Khoa`: da hoan thanh.
4. `naul1-pc` - `Luân`: da hoan thanh cho phan node / infrastructure, nhung van co pod `Unknown` theo node hien tai.
5. Phan con lai: debug `backoffice-bff`, `media`, `search`, `storefront-bff` tren cac node dang giu pod thuc te.

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

- Da doi chieu lai sau restart app gan nhat:
  - `keycloak`, `customer`, `inventory`, `product`, `tax`, `debezium-connect-cluster-connect`, `redis-replicas`, `elasticsearch-es-node` dang o trang thai dung.
  - `coredns` khong con la 2/2 Ready trong thuc te hien tai; co it nhat 1 pod `Unknown` o `naul1-pc`.
  - `storefront-bff` da chuyen sang `worker-3` nhung van chua Ready:
    - startup fail do `identity.yas.local.com` khong resolve duoc ngay trong pod
    - restart deployment chua lam pod bind duoc `8090`
  - Cac pod con loi tap trung o `worker-1`:
    - `backoffice-bff-*`
    - `media-*`
    - `search-*`
- Cac muc da danh dau `hoan thanh` trong file hien chua thay lo phan tra ve.
- Neu can cap nhat tiep, uu tien xu ly `storefront-bff` tren `worker-3` va kiem tra lai DNS / service discovery, sau do moi quay lai 3 nhom pod con loi tren `worker-1`.
