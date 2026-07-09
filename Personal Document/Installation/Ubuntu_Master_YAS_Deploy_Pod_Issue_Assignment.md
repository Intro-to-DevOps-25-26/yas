# Ubuntu Master YAS Deploy Pod Issue Assignment

Ngay: 2026-07-06

## 1. Node / Person Mapping

- `master` - `Tú`: control-plane
- `worker-1` - `Hòa`: chay workload app
- `naul1-pc` - `Luân`: chay workload app
- `worker-3` - `Khoa`: chay workload app

## 2. Pod Dang Loi / Chua On Dinh

### `master` - `Tú`

- Control-plane van chay on
- `kube-apiserver` / `etcd` / `scheduler` / `controller-manager` dang `Running`
- Cac van de hien tai khong con nam o master, ma nam o node worker / service network
- Nen tiep tuc theo doi cac log lien quan `istiod`, `coredns`, `kube-proxy`

### `worker-1` - `Hòa`

- `worker-1` hien tai `Ready`
- Da dung de chay lai `backoffice-bff`, `backoffice-ui`, `swagger-ui`
- DNS noi bo da verify duoc trong luc node on dinh:
  - `identity.yas.local.com -> 10.98.44.199`
  - `postgresql.postgres.svc.cluster.local -> 10.108.199.46`
  - `github.com`, `google.com` resolve duoc qua CoreDNS

### `naul1-pc` - `Luân`

- `naul1-pc` hien tai `Ready,SchedulingDisabled`
- Da khong con la diem chay chinh cho rollout moi sau khi `backoffice-*` duoc day sang node khoe
- Van co the dung de theo doi, nhung khong nen coi la node chay workload chinh luc nay

### `worker-3` - `Khoa`

- `worker-3` hien tai `Ready`
- Da nhan lai pod moi cho rollout sau khi node on dinh

## 3. Phan Cong Fix

### `master` - `Tú`

- Giam sat control-plane, DNS, service discovery.
- Kiem tra lai `coredns`, `istiod`, `kube-proxy` neu service DNS tiep tuc loi.
- Neu co thay doi infra, can verify lai ngay sau moi lan cleanup / recreate pod.

### `worker-1` - `Hòa`

- `backoffice-bff`, `backoffice-ui`, `swagger-ui` da duoc rollout lai sang node khoe va da chay on dinh.
- Phan can luu y them:
  - probe cu ban vao `15020` chi hop le khi co Istio sidecar
  - neu khong co sidecar thi phai doi sang probe truc tiep vao cong app

### `naul1-pc` - `Luân`

- `naul1-pc` van co the theo doi cac pod stateful khi can, nhung khong con la node rollout chinh.

### `worker-3` - `Khoa`

- `worker-3` da on dinh lai va co the tiep tuc schedule workload.

## 4. Thu Tu De Xu Ly

1. `worker-1` - `Hòa`: lam on dinh node va kubelet first.
2. `worker-3` - `Khoa`: khoi phuc node `NotReady`.
3. `naul1-pc` - `Luân`: theo doi pod stateful / pod pending sau cleanup.
4. `master` - `Tú`: giam sat control-plane va rollout lai sau khi node on dinh.
5. Phan con lai: don dep cac pod test/one-off con treo.

## 5. Ghi Chu

- Cac pod `*_debug`, `host-*`, `crictl-*`, `dnscheck-*`, `netcheck-*`, `sampledata-seed-*` van la pod tam thoi de debug, khong tinh vao workload chinh.
- `sampledata` khong con la blocked item chinh, nhung chi nen chot lai sau khi cluster on dinh.

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

- Sau rerollout:
  - `backoffice-bff` -> `1/1 Running`
  - `backoffice-ui` -> `1/1 Running`
  - `swagger-ui` -> `1/1 Running`
- `sampledata` da seed thu cong xong:
  - `product` db: `brand=4`, `category=9`, `product=14`, `product_attribute_value=91`, `product_image=56`, `product_option_value=0`, `product_option_combination=0`
  - `media` db: `media=82`
- Neu can tiep tuc rollout cac service core khac, nen lam tren node khoe va kiem tra probe / mesh config truoc.
