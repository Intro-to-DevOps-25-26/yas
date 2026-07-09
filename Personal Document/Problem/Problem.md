# YAS Deployment Scope and Long-term Setup

## 1. Muc tieu

Muc tieu la deploy du an microservices YAS theo dung pham vi do an, sao cho:

- Chay duoc local truoc bang Docker Desktop / Docker Compose
- Co the mo rong sang Kubernetes cho demo chinh thuc
- De tach moi truong `dev` va `staging` cho CI/CD
- Co san duong huong cho service mesh nang cao

## 2. Pham vi service can deploy

### 2.1 Phai giu

Day la cac service cot loi cho demo E-commerce + Service Mesh:

- `product`
- `cart`
- `order`
- `customer`
- `inventory`
- `tax`
- `media`
- `search`
- `storefront-bff`
- `storefront-ui`
- `backoffice-bff`
- `backoffice-ui`
- `swagger-ui`
- `sampledata`

### 2.2 Quy uoc voi `sampledata`

- `sampledata` chi chay 1 lan de seed du lieu
- Sau khi du lieu da duoc nap thanh cong thi co the tat service nay

### 2.3 Khong bat buoc cho do an co ban

Theo yeu cau moi, phan Observability co the bo qua:

- `grafana`
- `prometheus`

Cac thanh phan log/trace/metrics chi dung neu lam phan nang cao hoac can quan sat sau hon.

## 3. Mo hinh trien khai

### 3.1 Local / Dev

- Dung Docker Desktop + Docker Compose de chay nhanh
- Co the bind mount source code tu o `D:` de lam viec lau dai
- Nen giu project source tren `D:` de giam ap luc len `C:`

### 3.2 Kubernetes

Yeu cau:

- 1 master node
- 3 worker node

Hoac co the dung:

- Minikube
- Bat ky mo hinh K8S tuong duong

### 3.3 Expose service

Sau khi deploy tren K8S:

- Dung `NodePort` de cung cap domain name:port dung theo yeu cau original
- Developer se tu them `hosts` tren may minh de tro domain ve worker node
- Neu can test noi bo, co the truy cap thong qua IP node + NodePort

Cach test web app:

- `swagger-ui`: `http://<worker-ip>:32044/`
- `storefront-ui`: `http://<worker-ip>:30179/`
- `backoffice-ui`: `http://<worker-ip>:31790/`
- Neu da map `hosts`, co the dung domain tuong ung thay cho `<worker-ip>`
- Khi test qua may khac mang, co the dung IP Tailscale cua worker thay cho IP LAN neu route LAN khong on dinh

### 3.4 Ket noi node qua Tailscale

Vi cac may khong cung mang, dung `Tailscale` de noi cac node trong cung 1 cluster.

- Cac node worker phai ket noi on dinh ve master
- May cua developer debug cung co the join Tailscale de truy cap cluster on dinh nhu may master
- Muc tieu la tat ca may co lien quan deu truy cap duoc control plane va NodePort mot cach on dinh
- Tailscale dung de tao duong noi mang rieng, khong phai cluster member

### 3.5 Thiet ke chot

- `master`: chi chay control plane, khong chay workload ung dung
- `worker`: chay tat ca service core, `sampledata` Job, va cac pod can expose
- `NodePort`: dung cho cac service can cung cap `domain name:port` theo original
- `ClusterIP`: dung cho cac service noi bo khong can expose truc tiep
- `sampledata`: chay duoi dang `Job`, seed xong thi ket thuc
- `Tailscale`: dung cho ca node cluster va may dev/debug de truy cap cluster on dinh

### 3.6 Mapping service

| Service | Loai | Ly do |
|---|---|---|
| `swagger-ui` | `NodePort` | Can mo browser de xem API docs |
| `storefront-ui` | `NodePort` | Trang demo chinh cho nguoi dung |
| `backoffice-ui` | `NodePort` | Trang demo chinh cho admin |
| `storefront-bff` | `ClusterIP` | Chi phuc vu UI noi bo |
| `backoffice-bff` | `ClusterIP` | Chi phuc vu UI noi bo |
| `product` | `ClusterIP` | Core backend |
| `cart` | `ClusterIP` | Core backend |
| `order` | `ClusterIP` | Core backend |
| `customer` | `ClusterIP` | Core backend |
| `inventory` | `ClusterIP` | Core backend |
| `tax` | `ClusterIP` | Core backend |
| `media` | `ClusterIP` | Core backend |
| `search` | `ClusterIP` | Core backend, chi mo NodePort khi debug tam |
| `sampledata` | `Job` | Seed 1 lan roi tat |

Quy tac chot:

- `NodePort` chi dung cho 3 service demo chinh: `swagger-ui`, `storefront-ui`, `backoffice-ui`
- `ClusterIP` cho tat ca service con lai de giu cluster gon va an toan
- `sampledata` khong chay lau dai, phai la `Job`
- Neu chon dung `Ingress` cho `swagger-ui` thi phai co du route toi cac API docs backend (`/product`, `/order`, `/tax`, ...) hoac co gateway trung gian, neu khong Swagger UI se khong load duoc danh sach API definition.

### 3.7 Trinh tu task

1. Hoan tat Docker Desktop storage va local compose
2. Kiem tra lai core service local va `sampledata`
3. Dung K8S cluster `1 master + 3 worker`
4. Ket noi cac node qua `Tailscale`
5. Tao manifest `Deployment`, `Service`, `Job`
6. Expose service bang `NodePort`
7. Cap nhat `hosts`
8. Mo web app de test:
   - `swagger-ui` -> `http://<worker-ip>:32044/`
   - `storefront-ui` -> `http://<worker-ip>:30179/`
   - `backoffice-ui` -> `http://<worker-ip>:31790/`
9. Hoan thien GitHub Actions CD de build, push, deploy, cleanup
10. Lam `dev/staging` neu chot scope nang cao
11. Lam service mesh nang cao bat buoc, observability chi giu neu can

### 3.8 Tien do hien tai

- Da hoan thanh phan lon hoat dong deploy tren K8S:
  - cluster `1 master + 3 worker` da len on dinh
  - `Tailscale` da noi cac node trong cung mot tailnet
  - `CoreDNS` da khong con loi `NXDOMAIN` cho `identity.yas.local.com`
  - cac service chinh da Running/Ready
  - `storefront-bff` da bind duoc va chay on dinh
- Da co the test truy cap qua `NodePort` cho cac service demo chinh
- Web app demo hien co the mo bang browser qua:
  - `swagger-ui`: `http://<worker-ip>:32044/`
  - `storefront-ui`: `http://<worker-ip>:30179/`
  - `backoffice-ui`: `http://<worker-ip>:31790/`
- `sampledata` da seed thu cong thanh cong, khong con blocked
- `sampledata` chart da duoc scaffold lai thanh Job seed 1 lan
- Da co overlay values chung cho `dev` / `staging` de pin tag va test NodePort cho UI

### 3.9 Task con lai

- Don dep cac pod kiem tra / one-off / rollout cu khong con dung nua
- Chot scope nang cao theo huong sau:
  - CI/CD chot dung `GitHub Actions`
  - `main` thay doi thi auto deploy vao namespace `dev`
  - release tag nhu `v1.2.3` thi build image theo tag do va deploy vao namespace `staging`
  - co job/flow deploy theo branch cho developer build
  - co cleanup job de xoa ban deploy cu
  - `sampledata` giu dang seed mot lan, khong chay lau dai
  - service mesh chot dung `Istio` + `Kiali`
  - bat `mTLS`, `AuthorizationPolicy`, `VirtualService` retry/timeout
  - test bang `kubectl exec` + `curl`, va chup topology Kiali lam bang chung
- Observability (`Grafana`, `Prometheus`, `Loki`, `Tempo`) khong chot trong scope co ban, chi lam neu co them thoi gian hoac can demo nang cao
- Hoan thien checklist/CD theo scope da chot:
  - build image theo branch / commit / tag
  - push len Docker Hub
  - deploy vao K8S dung namespace phu hop
  - cung cap dia chi truy cap sau deploy
  - co cleanup job cho ban deploy cu

### 3.10 Phan cong 4 nguoi

Muc tieu la 4 nguoi lam song song, moi nguoi co 1 trach nhiem ro rang, khong doi nhau xong moi bat dau.

| Nguoi | Viec chinh | Deliverables |
|---|---|---|
| `Tú` | Chot `manifest/chart` cho `dev/staging` va `ArgoCD`, gom app/project, sync policy, rollback | Screenshot ArgoCD sync/health, manifest namespace/project, values `dev/staging`, ghi chu flow `dev` va `staging` |
| `Hòa` | Lam `Istio` service mesh cho namespace app, bat `mTLS`, `AuthorizationPolicy`, `VirtualService` retry/timeout | YAML policy, screenshot Kiali topology, log `curl` test cho retry / allow / deny |
| `Luân` | Hoan thien GitHub Actions CD cho build/push/deploy/cleanup, gom job `developer_build` va job cleanup | Screenshot workflow run, log build/push, manifest/job deploy, ghi chu cach map branch/tag |
| `Khoa` | Don dep pod kiem tra / pod cu / rollout cu, test `NodePort`, doi chieu web app, ho tro tong hop report va screenshot | Screenshot `kubectl get pods -A -o wide`, screenshot NodePort truy cap UI, checklist pod da clean, bang tong hop trang thai |

#### `manifest` / `chart` la gi

- `manifest` la tap cac file YAML mo ta resource K8S:
  - `Deployment`
  - `Service`
  - `Job`
  - `ConfigMap`
  - `Secret`
  - `Ingress` hoac `NodePort` expose
  - `HorizontalPodAutoscaler` neu co
- `chart` la bo Helm gom:
  - `Chart.yaml`
  - `values.yaml`
  - template trong `templates/`
- Phan nay quyet dinh:
  - pod chay image nao
  - service nao expose ra sao
  - port nao mo
  - selector nao match
  - namespace nao dung
  - probe nao duoc bat
  - Job nao seed du lieu
- Neu manifest/chart chua on dinh thi ArgoCD se khong co “nguon chan ly” ro rang de sync.

#### Ai phu trach manifest/chart

- Nguoi lam manifest/chart phai chot:
  - template Helm hoac YAML
  - values cho `dev`, `staging`
  - image tag mapping
  - service type `NodePort` / `ClusterIP`
  - probe / resource / env / secret mount
  - Job seed `sampledata`
- Phan nay phai khop voi:
  - `Luân`: CD build/push/deploy/cleanup
  - `Tú`: ArgoCD sync/rollback
  - `Hòa`: policy mesh / injection
  - `Khoa`: test pod / NodePort / web app

#### Can chup man hinh

- `kubectl get nodes -o wide`
- `kubectl get pods -A -o wide`
- `kubectl get svc -n yas`
- `ArgoCD` app sync/healthy cho `dev` va `staging`
- `Kiali` topology sau khi bat mesh
- Ket qua `curl` / browser khi test `swagger-ui`, `storefront-ui`, `backoffice-ui`
- GitHub Actions workflow run cho build/deploy/cleanup
- `Khoa` tong hop screenshot trang thai pod / NodePort / web app de dua vao report

#### ArgoCD chi nen bat khi

- `Tú` da chot xong manifest/chart co ban:
  - Helm template/YAML chay ra dung `Deployment`, `Service`, `Job`
  - `values` cho `dev/staging` da tach ro va khong con copy/paste hard-code
  - service type, selector, port, probe da on dinh
  - `sampledata` chay dung kieu seed mot lan, khong con coi nhu workload lau dai
  - manifest co the `helm template` ra YAML hop le va co the apply thu cong thanh cong
- `Luân` da co CI/CD build/push image on dinh:
  - image build thanh cong theo branch / commit / tag
  - push len Docker Hub
  - mapping image tag khong con thay doi lung tung
  - workflow deploy co the nhan dung image tag can de rollout
  - cleanup job da biet xoa ban deploy cu ma khong lam hong ban moi
- `Hòa` da xac nhan Istio/mTLS khong con chan rollout:
  - webhook / injection / policy da test on
  - rollout restart khong bi timeout nua
  - `mTLS` / `AuthorizationPolicy` / `VirtualService` da test co ket qua
  - service mesh khong con tao them lỗi webhook khi deploy app
- `Khoa` da xac nhan web app demo va NodePort chay duoc:
  - swagger/storefront/backoffice truy cap duoc
  - pod cu / pod test khong con lam nhieu noise
  - screenshot browser/curl duoc chup day du
  - cac endpoint demo co the verify nhanh sau moi deploy
- `Tú` da co ArgoCD app/project/rollback flow:
  - app healthy/sync
  - rollback test duoc
  - `dev` co auto sync duoc, `staging` co quy tac release ro
  - co app/project rieng cho tung namespace
  - ArgoCD doc duoc manifest/chart da chot o tren
- cluster khong con loi node/dns/changing selector lam lech deploy:
  - node `Ready`
  - DNS/CoreDNS on
  - NodePort truy cap duoc
  - khong con loi webhook/sidecar/injection chan rollout
  - rollout restart khong bi timeout do loi he thong

#### Can viet report

- Tom tat cluster hien tai va ai phu trach phan nao
- Cach deploy `dev/staging` bang `ArgoCD`
- Cach cau hinh `Istio` va test mTLS / policy / retry
- Cach test web app qua `NodePort`
- Cach build/push/deploy/cleanup bang GitHub Actions
- Danh sach pod one-off / pod cu da dọn
- Phan tong hop trang thai test web app va NodePort do `Khoa` phu trach

## 4. CI/CD yeu cau

### 4.1 CI

Voi moi branch cua user:

- Sau khi user commit code thay doi
- CI phai build image cho service bi thay doi
- Tag image phai la commit id cuoi cung cua branch do
- Push image len Docker Hub

### 4.2 CD job `developer_build`

Tao job CD cho developer lam viec ten:

- `developer_build`

Job nay nhan input:

- branch can deploy

Vi du:

- branch dang lam: `dev_tax_service`
- branch can test: `tax-service = dev_tax_service`
- cac service con lai dung tag mac dinh `main` hoac `latest`

Muc tieu la deploy mot service dang thay doi ma khong anh huong toan bo he thong.

### 4.3 Cleanup job

Tao them GitHub Actions workflow hoac job tuong duong de xoa phan trien khai cua `developer_build`.

Workflow CD can bao gom:

- build image theo commit id / tag / branch quy dinh
- push image len Docker Hub
- deploy image vao cluster dung namespace phu hop
- cleanup khi can xoa deployment cu

## 5. Nang cao tuy chon

### 5.1 Dev/Staging

Neu lam phan nang cao:

- `main` thay doi thi auto deploy vao namespace `dev`
- khi co release tag nhu `v1.2.3`, CI/CD build image theo tag do
- deploy image vao namespace `staging`
- `dev` dung cho test thay doi lien tuc
- `staging` dung cho ban chuan bi release

### 5.2 GitOps

Phuong an nang cao:

- dung ArgoCD de quan ly `dev` va `staging`
- ArgoCD sync tu Git repo, khong deploy thu cong vao 2 namespace nang cao nay
- moi namespace co app/project rieng:
  - `dev`: auto sync cho branch main / integration
  - `staging`: sync theo release tag / release branch
- neu can rollback thi rollback bang revision cua ArgoCD, khong sua tay tren cluster

#### Dieu kien de bat dau ArgoCD

- CI build image da on dinh:
  - build thanh cong theo branch / commit / tag
  - image push len Docker Hub dung ten va tag ro rang
  - cac service thay doi co the map duoc sang dung image tag
- Manifest deploy da on dinh:
  - chart/manifest khong con thay doi lien tuc
  - namespace, service name, selector, port da chot
  - values cho `dev` va `staging` da tach ro
- Cluster da duoc chuan bi:
  - node `Ready`
  - DNS / CoreDNS / NodePort / web app demo da test duoc
  - neu co service mesh thi webhook / injection / istio da on dinh
- Quy tac rollout da ro:
  - `dev` chap nhan auto sync lien tuc
  - `staging` chi sync khi co release tag / release branch
  - rollback phai lam duoc bang Git/ArgoCD revision
- Co quy uoc ownership:
  - ai sua manifest
  - ai approve release
  - ai rollback neu co loi
- Co deliverables co the chup / bao cao:
  - ArgoCD app healthy/sync
  - log sync/rollback
  - diff Git truoc va sau khi ArgoCD apply

#### Tien do chi tiet can dat cua tung nguoi

- `Luân`:
  - it nhat 1 service da build/push image on dinh tu GitHub Actions
  - tag image theo commit/branch/release da co quy uoc ro
  - deploy job co the rollout mot service ma khong anh huong service khac
  - cleanup job co the xoa ban cu ma khong xoa nham ban moi
  - log workflow run duoc luu de chup man hinh vao report
- `Hòa`:
  - Istio da cai xong va `istiod` chay on
  - namespace app da bat injection va khong con timeout khi restart pod
  - `mTLS` da bat va test `curl` duoc ca allow/deny
  - `AuthorizationPolicy` va `VirtualService` da co bang chung retry/timeout
  - rollout app khong con bi webhook/injection chan
- `Khoa`:
  - `swagger-ui`, `storefront-ui`, `backoffice-ui` truy cap duoc qua `NodePort`
  - browser/curl test da chup duoc screenshot
  - pod cu/pod test da clean hoac da danh dau ro
  - co bang tong hop trang thai web app va NodePort
  - can co it nhat 1 lan verify lai sau deploy that de chac khong bi noise
- `Tú`:
  - manifest/chart da on dinh va render ra YAML hop le
  - values `dev/staging` da tach
  - ArgoCD app/project/rollback flow da co san
  - app deploy thu cong con chay duoc truoc khi chuyen qua sync tu Git

#### Khi chua nen bat ArgoCD

- CI chua build/push image on dinh
- manifest con sua lien tuc hoac selector/port con loang
- cluster van con loi node, DNS, webhook, hoac sidecar injection
- chua co quy uoc ro ve branch/tag cho `dev` va `staging`
- chua co cach rollback / thay doi release ro rang

### 5.3 CD chot su dung GitHub Actions

Voi scope hien tai, chot su dung GitHub Actions cho CD.

- CI/CD co the tach thanh workflow build image va workflow deploy
- Moi branch hoac tag map sang tag image phu hop
- Khong bat buoc phai dung Jenkins neu repo da co san GitHub Actions
- Neu lam `dev/staging`, workflow can phan biet env, namespace, va tag image theo branch / release
- CD phai deploy luon, khong chi dung o muc build/push
- Workflow CD phai dap ung full scope trong original: build image, push Docker Hub, deploy vao K8S, cung cap dia chi truy cap sau deploy, va co cleanup job

### 5.4 Service mesh bat buoc

Phan nang cao service mesh la phan can lam trong scope do an:

- chot dung `Istio` lam service mesh
- bat mTLS giua cac service deploy tren K8S
- cau hinh `PeerAuthentication` va `DestinationRule` de bat mTLS toan mesh hoac theo namespace
- cau hinh `AuthorizationPolicy` de gioi han service-to-service access
- cau hinh `VirtualService` retry + timeout cho loi 500
- dung `Kiali` de quan sat topology
- test bang `kubectl exec` va `curl`
- deliverables can co:
  - YAML manifest cho mTLS va authorization policy
  - screenshot Kiali topology
  - test plan + log curl/retry
  - README huong dan tung buoc

## 6. Service Mesh nang cao

Neu lam phan service mesh:

- bat mTLS giua cac service
- cau hinh `PeerAuthentication` / `DestinationRule`
- cau hinh `AuthorizationPolicy`
- cau hinh `VirtualService` retry cho luong loi 500
- dung Kiali de xem topology
- chuan bi kich ban test bang `kubectl exec` va `curl`

Kich ban test can chung minh:

- service bi loi 500 thi retry tu dong
- chi service duoc phep moi goi duoc nhau
- service khong duoc phep thi bi chan ket noi

### 6.1 Health check cho K8S

Khi deploy len K8S, can co health check ro rang de dam bao pod on dinh:

- `readinessProbe` de chi nhan traffic khi service da san sang
- `livenessProbe` de restart pod neu app bi treo
- Neu la Spring Boot, nen mo `actuator health` de dung lam endpoint kiem tra

Day khong chi la "nen co" ma la yeu cau can thiet neu muon deploy K8S on dinh va khong bi restart hoac route sai.

## 7. Docker Desktop storage cho chay lau dai

Hien tai Docker Desktop nen duoc chuyen storage sang o `D:` de tranh day o he thong.

### 7.1 Diem can hieu

- Source code co the nam tren `D:`
- Nhung Docker Desktop van luu image, container data, log, va WSL data theo storage cua Docker
- Neu storage van nam o `C:` thi Docker co the fail khi pull image hoac ghi metadata

### 7.2 Cach chuyen

Lam theo Docker Desktop UI:

1. Mo Docker Desktop
2. Vao `Settings`
3. Chon `Resources`
4. Chon `Advanced`
5. Tim muc `Disk image location`
6. Chuyen duong dan luu disk image sang mot thu muc tren `D:`
7. Apply va restart Docker Desktop

### 7.3 Luu y

- Docker Desktop WSL 2 backend mac dinh luu data o `C:\\Users\\[USERNAME]\\AppData\\Local\\Docker\\wsl`
- Docker Docs cho biet ban co the doi vi tri nay trong `Settings -> Resources -> Advanced`
- Sau khi doi, can dam bao o `D:` co du dung luong trong lau dai

## 7.4 OTEL local

Voi scope hien tai, collector duoc giu lai neu can quan sat sau hon.

- neu can demo co ban va muon giam log noise thi co the tat OTEL local
- neu tat OTEL, app van chay binh thuong, nhung khong con trace, metrics, hoac log export ra collector
- neu muon giu observability, can them OpenTelemetry Collector vao compose hoac cluster
- vi yeu cau hien tai da chon giu collector, khong xem day la blocker cua luong chinh
- collector se phuc vu cho cac task observability/service mesh ve sau

## 8. Ghi chu trien khai thuc te

- Neu chi can demo co ban, hay uu tien core service truoc
- `sampledata` chi dung de seed
- Tren local thi `sampledata` chi chay 1 lan, sau do co the tat
- Tren K8S neu can seed rieng, nen tach thanh job seed
- `swagger-ui` la entry point de nhat de verify API
- `storefront-ui` va `backoffice-ui` la phan demo nguoi dung nhin thay truc tiep
- Khi lam K8S, nen chot som domain mapping de khong phai sua nhieu lan

## 9. Ket luan

Uu tien thuc thi:

1. Chuyen Docker Desktop storage sang `D:`
2. Deploy core 14 services theo scope tren
3. Giu collector neu can observability/service mesh
4. Dung `NodePort` + hosts mapping cho K8S
5. Sau do moi mo rong sang CI/CD nang cao va service mesh

## 10. Next steps

Sau khi chuyen storage sang `D:` thanh cong, thu tu nen lam la:

1. Khoi dong lai Docker Desktop va kiem tra `docker version` chay on dinh.
2. Chay local stack bang Docker Compose truoc, uu tien `core` truoc neu full stack chua on.
3. Chay `sampledata` de seed du lieu, sau do tat service nay.
4. Xac nhan cac URL quan trong:
   - `http://storefront/`
   - `http://backoffice/`
   - `http://api.yas.local/swagger-ui/`
5. Neu local on, chuyen sang K8S:
   - chon cluster 1 master + 3 worker hoac Minikube neu can
   - deploy 14 service core
   - expose bang `NodePort`
   - neu can truy cap giua cac may khac mang, dung Tailscale de noi cluster va debug
6. Sau khi K8S on dinh, lam CI/CD:
   - build image theo commit id cua branch
   - push len Docker Hub
   - tao job `developer_build`
   - chot dev/staging neu lam nang cao
7. Lam service mesh sau khi phan deploy co ban da chay duoc:
   - mTLS
   - AuthorizationPolicy
   - VirtualService retry
   - Kiali topology

## 11. Trang thai deploy hien tai

Da chay Docker Compose va xac nhan cac service co ban dang len:

- `storefront` tra ve `200 OK` tren host `storefront`
- `backoffice` tra ve `302 Found` tren host `backoffice`
- `identity` tra ve redirect sang login
- `swagger-ui` tra ve `200 OK` tren `http://api.yas.local/swagger-ui/`

Van de da ghi nhan trong qua trinh kiem tra:

- Probe `http://localhost/product/actuator/health` tra ve `500` vi endpoint nay chua duoc mo trong cau hinh hien tai
- Cac service dang co log OTEL loi `collector: Name does not resolve` vi stack nay chua co OpenTelemetry Collector

Huong xu ly:

- Su dung cac route API that su co san de kiem tra nhanh
- Neu can health check cho K8S, bat actuator health ro rang trong service va khai bao readiness / liveness probe
- Neu khong can observability cho scope nay, tat OTEL export hoac bo sung collector

## 12. Xac nhan cac service core

Da kiem tra bang endpoint that qua Docker Compose + nginx host routing.

### 12.1 API services

- `product` -> `http://api.yas.local/product/v3/api-docs` -> `200 OK`
- `cart` -> `http://api.yas.local/cart/v3/api-docs` -> `200 OK`
- `order` -> `http://api.yas.local/order/v3/api-docs` -> `200 OK`
- `customer` -> `http://api.yas.local/customer/v3/api-docs` -> `200 OK`
- `inventory` -> `http://api.yas.local/inventory/v3/api-docs` -> `200 OK`
- `tax` -> `http://api.yas.local/tax/v3/api-docs` -> `200 OK`
- `media` -> `http://api.yas.local/media/v3/api-docs` -> `200 OK`
- `search` -> `http://api.yas.local/search/v3/api-docs` -> `200 OK`

### 12.2 UI and BFF services

- `storefront-bff` / `storefront-ui` -> `http://storefront/` -> `200 OK`
- `backoffice-bff` / `backoffice-ui` -> `http://backoffice/` -> `302 Found`
- `swagger-ui` -> `http://api.yas.local/swagger-ui/` -> `200 OK`

### 12.3 One-time seed service

- `sampledata` -> `http://api.yas.local/sampledata/` -> `401 Unauthorized`
- Service is reachable, but protected by auth in the current setup.
- This is acceptable for a seed container after data has been loaded once.

### 12.4 Kết luận ổn định

Hiện tại deploy local theo Docker Compose đã ổn định cho scope core:

- Tất cả service core đang `Up`
- Endpoint thật của từng service đã phản hồi đúng kiểu mong đợi
- Không có container core nào restart hoặc chết
- Các lỗi còn lại chỉ là telemetry OTEL không tìm thấy `collector`, không chặn luồng demo chính

Ket luan: co the dung stack nay de demo local va lam buoc tiep theo sang K8S / CI-CD.

### 12.5 Trang thai cap nhat

Trang thai thuc te hien tai:

- `search` da on dinh trong K8S sau khi pin `co.elastic.clients:elasticsearch-java` ve `8.15.0`, rollout lai va pod da len `1/1 Ready`
- `product`, `order`, `tax` da fix xong va Ready sau khi sua probe/runtime
- `cart`, `customer`, `inventory`, `media`, `storefront-bff`, `storefront-ui`, `backoffice-bff`, `backoffice-ui`, `swagger-ui` hien dang on dinh trong cluster
- `sampledata` la Job seed mot lan, khong chay thuong truc
- `Keycloak` va cac BFF van phu thuoc vao rollout lai cluster app sau khi cac service core duoc mo lai
- OTEL local co the coi la log noise khi khong can observability day du, nhung collector van duoc giu de phuc vu service mesh / observability ve sau
- K8S local da co cluster 1 master + 3 worker va co the rollout lai tung service khi can

## 13. Checklist trang thai

### Da lam thuc te

- [x] Chon huong deploy local bang Docker Compose
- [x] Chuyen Docker Desktop storage sang o `D:`
- [x] Khoi dong stack local thanh cong
- [x] Xac nhan cac service core dang `Up`
- [x] Kiem tra endpoint that cho `product`, `cart`, `order`, `customer`, `inventory`, `tax`, `media`, `search`
- [x] Kiem tra UI/BFF cho `storefront`, `backoffice`, `swagger-ui`
- [x] Giai quyet loi `search` bootstrap Elasticsearch (`indices.exists`)
- [x] Pin dung `co.elastic.clients:elasticsearch-java` ve `8.15.0` de dung `RangeQuery.Builder.number(...)`
- [x] Giai quyet loi runtime `slf4j-api` trong `search`
- [x] Xac nhan `search` tra `200 OK` sau khi fix va redeploy
- [x] Xac nhan `product`, `order`, `tax` da Ready sau khi fix probe/runtime
- [x] Seed `sampledata` thanh cong va xac nhan du lieu da vao DB
- [x] Khoi tao K8S local 1 master + 3 worker va deploy core service thanh cong
- [x] Giai quyet loi DNS / `identity` / Keycloak lam `storefront-bff` va `backoffice-bff` treo
- [x] Ghi log cac loi da gap vao `Personal Document/ErrorEncounter/`
- [x] Cap nhat scope service can giu va bo qua observability co ban
- [x] Xac dinh `sampledata` chi can chay mot lan de seed du lieu
- [x] Da fix `search` fallback Kafka ve `kafka:9092`
- [x] Da fix `wait-for-it.sh` CRLF sang LF
- [ ] Kiem tra lai cac service con lai neu co thay doi image/tag hoac chart

### Da chot thiet ke

- [x] Chot CD su dung GitHub Actions
- [x] Chot K8S dinh huong 1 master + 3 worker
- [x] Chot dung Tailscale de ket noi cac node khac mang
- [x] Chot giu collector neu can observability
- [x] Chot khong dung Ingress trong scope chinh, chi dung NodePort theo original
- [x] Chot `sampledata` se chay duoi dang Job trong K8S
- [x] Chot phan bo service: `NodePort` cho service can expose, `ClusterIP` cho noi bo

### Con lai

- Chot bao cao trang thai cuoi cung cua core service, khong de checklist con ghi nham service da fix
- Cap nhat cac file status/report lien quan neu rollout hay pin version co thay doi
- Ghi ro cac service con lai neu sau nay co phat sinh regression
- Tap trung vao chart/manifest, health check, rollout, cleanup, va ArgoCD
- Bo qua service mesh trong scope hien tai

#### 13.1 Local compose va local test

- [ ] Phan loai compose: giu co the giu service du neu khong gay hai, nhung phai document ro core 14 service + `sampledata`
- [ ] Cap nhat local_test.md neu co thay doi route, port, hay cach test K8S

#### 13.2 K8S cluster va deploy

- [ ] Hoan thien chart/manifest: `Deployment`, `Service`, `Job` chuan cho 14 service core
- [ ] Chuan hoa health check: `readinessProbe`, `livenessProbe`, actuator health cho toan bo service
- [ ] Kiem tra worker scheduling, pod anti-affinity neu can
- [ ] Kiem tra rollout / rollback cho cac chart quan trong
- [ ] Chot NodePort / ClusterIP / Job dung voi scope
- [ ] Cap nhat file `hosts` de map domain local ve worker node
- [ ] Kiem tra route NodePort / domain tren may dev khi ket noi qua Tailscale

#### 13.3 CI/CD

- [ ] Hoan thien workflow GitHub Actions cho CI/CD
- [ ] Build image theo commit id / tag / branch rule
- [ ] Tao flow `dev` va `staging`
- [ ] Hoan thien deploy/cleanup cho `developer_build`
- [ ] Chot namespace va quy tac image tag cho moi environment

#### 13.4 GitOps ArgoCD

- [ ] Chot app/project cho `dev` va `staging`
- [ ] Chot sync policy va rollback flow cua ArgoCD
- [ ] Chot manifest/chart da on dinh truoc khi ArgoCD lay lam nguon chan ly
- [ ] Kiem tra diff Git va cluster sau khi ArgoCD sync
- [ ] Viet huong dan deploy/rollback bang ArgoCD

#### 13.5 Tieu chi hoan thanh

- [ ] Core 14 service chay on dinh
- [ ] `sampledata` seed thanh cong va co the tat
- [ ] Chart/manifest + health check on dinh
- [ ] CI/CD build va deploy chay dung
- [ ] ArgoCD sync/rollback chay dung cho `dev` va `staging`
- [ ] Tai lieu va checklist dong bo voi trang thai thuc te

## 14. K8S 4 may + Tailscale

Muc tieu la dung 4 may vat ly/debian/ubuntu de tao 1 cluster K8S duy nhat:

- 1 may lam `control-plane`
- 3 may lam `worker`
- cac may co the khong chung LAN, nen dung `Tailscale` de noi mang

### 14.1 Can lam gi

- Cai Kubernetes runtime hoac `kind`/`kubeadm` tren 4 may
- Cai `Tailscale` tren ca 4 may
- Cho ca 4 may join chung 1 tailnet
- Dung dia chi Tailscale de cac node nhin thay nhau on dinh
- Khoi tao cluster voi 1 master va 3 worker
- Dam bao worker co the truy cap control-plane va control-plane co the giao tiep nguoc lai
- Mo cac port can thiet cho control-plane, kubelet, NodePort va cac service phu tro
- Kiem tra DNS noi bo, CoreDNS, va route NodePort tu may dev/debug

### 14.2 Workload giua 4 may

- `master`: chi chay control-plane, API server, scheduler, controller manager
- `worker 1-3`: chay tat ca workload app, bao gom core service, `sampledata` Job, va cac pod can expose
- Neu can debug, may dev/debug co the join tailnet de truy cap cluster qua NodePort/port-forward

### 14.3 Checklist trien khai

- [ ] Cai Tailscale tren 4 may
- [ ] Kiem tra 4 may nhin thay nhau bang IP Tailscale
- [ ] Cai Kubernetes cluster voi 1 control-plane + 3 worker
- [ ] Kiem tra `kubectl get nodes`
- [ ] Dat app workload chi tren worker
- [ ] Kiem tra NodePort tu may ngoai cluster
- [ ] Kiem tra port-forward va DNS noi bo

## 15. Phan bo cong viec

### 15.1 Workload so voi fix app

- `Fix app`
  - Thuong ton nhieu thoi gian debug code, log, dependency, va data flow
  - Phu thuoc nhieu vao source, build, va test runtime
  - Voi `search`, `bff`, `sampledata` va Keycloak, loi thuong lien hoan theo chuoi
- `Deploy app`
  - Ton nhieu cong xay dung helms/charts, namespace, service, ingress/nodeport, secret/config
  - Ton nhieu cong kich hoat cluster, route, DNS, image tag, va rollout
  - Sau khi khuon mau da on, deploy thuong lap lai nhanh hon fix app

### 15.2 Giao task cho 4 nguoi

Thu tu uu tien:

1. Chot chart/manifest + health check cho 14 service core.
2. Chot CI/CD build/push/deploy/cleanup.
3. Rollout/rollback, NodePort, hosts va test route.
4. Lam ArgoCD dev/staging sau cung khi manifest/chart da on dinh.

#### Tú: Manifest/Chart + ArgoCD

- Chot chart/manifest co ban cho 14 service core, gom `Deployment`, `Service`, `Job`
- Chot `values.yaml` cho `dev` va `staging`
- Tao `ArgoCD Application`/`AppProject`, sync policy, rollback flow
- Kiem tra manifest/chart da on dinh truoc khi ArgoCD sync
- Chup screenshot ArgoCD sync/health va tong hop flow `dev` / `staging`

##### Phan cong 3 muc cho Tú

- `Lam ngay`
  - Chuan hoa chart/manifest core cho `Deployment`, `Service`, `Job`
  - Tach `values.yaml` cho `dev` va `staging`
  - Viet ban dau `ArgoCD Application`/`AppProject` va sync policy skeleton
  - Kiem tra service name, selector, port, probe, namespace co hop le
  - Chot checklist screenshot can chup cho chart va ArgoCD
- `Lam tam bang tag co dinh`
  - Dung image tag co dinh de test sync/rollback khi chua co CI/CD on dinh
  - Test `helm template` / `helm upgrade` thu cong voi tag co san
  - Dung manifest da on dinh de verify ArgoCD sync khong bi lech
  - Kiem tra rollout va rollback voi tag thu cong
- `Cho Luan xong moi chot`
  - Mapping image tag theo branch/commit/release tu CI/CD
  - Auto sync cho `dev` va quy tac release cho `staging`
  - Cleanup job lien ket voi flow deploy moi
  - Screenshot end-to-end CI -> ArgoCD -> rollout

#### Luân: CI/CD build/push/deploy/cleanup

- Hoan thien GitHub Actions cho build/push image theo commit/tag/branch
- Hoan thien job `developer_build`
- Hoan thien job cleanup / xoa deployment cu
- Chot rule image tag va cach map branch -> image
- Chup screenshot workflow run, log build/push, va manifest/job deploy

#### Hòa: Health check va chart tuning

- Chuan hoa `readinessProbe`, `livenessProbe`, `startupProbe` va actuator health cho 14 service core
- Sua chart/values neu probe, port, config map, secret, hay volume mount bi sai
- Hoan thien `sampledata` theo Job 1 lan seed
- Kiem tra startup/runtime log de dam bao service len `Ready`
- Chup log fix, file chart/values, va bang checklist service da Ready

#### Khoa: Rollout/rollback + NodePort + test route

- Kiem tra rollout/rollback sau moi lan deploy
- Kiem tra NodePort, hosts, va route tu may dev
- Don pod cu / pod test / resource rac sau khi rollout
- Test web app va API docs bang browser/curl
- Tong hop screenshot `kubectl get pods -A -o wide`, NodePort truy cap UI, va trang thai cluster

### 15.3 Thu tu lam

- [ ] Chat luong chart/manifest va health check truoc
- [x] Chay audit chart/manifest theo `Tu_Chart_Manifest_Audit.md`
- [ ] Build/push/deploy/cleanup tiep theo
- [ ] Rollout/rollback + NodePort + hosts/test route
- [ ] ArgoCD `dev` / `staging` sau cung khi manifest/chart da on dinh
- [ ] Cap nhat screenshot va report theo trang thai thuc te

## 16. YML deploy Helm nhap vai tro de chot scope

Mau workflow nay chi la khung de deploy qua Helm cho cluster K8S 1 master + 3 worker:

```yaml
name: k8s-deploy

on:
  workflow_dispatch:
    inputs:
      environment:
        description: "dev or staging"
        required: true
        default: "dev"
      branch:
        description: "Branch to deploy"
        required: true
        default: "main"

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up kubectl
        uses: azure/setup-kubectl@v4

      - name: Set up Helm
        uses: azure/setup-helm@v4

      - name: Load kubeconfig
        run: |
          mkdir -p ~/.kube
          echo "${{ secrets.KUBECONFIG_B64 }}" | base64 -d > ~/.kube/config

      - name: Deploy shared config
        run: |
          helm upgrade --install yas-configuration k8s/charts/yas-configuration \
            -n yas --create-namespace

      - name: Deploy app stack
        run: |
          helm upgrade --install product k8s/charts/product -n yas --create-namespace
          helm upgrade --install cart k8s/charts/cart -n yas --create-namespace
          helm upgrade --install order k8s/charts/order -n yas --create-namespace
          helm upgrade --install customer k8s/charts/customer -n yas --create-namespace
          helm upgrade --install inventory k8s/charts/inventory -n yas --create-namespace
          helm upgrade --install tax k8s/charts/tax -n yas --create-namespace
          helm upgrade --install media k8s/charts/media -n yas --create-namespace
          helm upgrade --install search k8s/charts/search -n yas --create-namespace
          helm upgrade --install storefront-bff k8s/charts/storefront-bff -n yas --create-namespace
          helm upgrade --install storefront-ui k8s/charts/storefront-ui -n yas --create-namespace
          helm upgrade --install backoffice-bff k8s/charts/backoffice-bff -n yas --create-namespace
          helm upgrade --install backoffice-ui k8s/charts/backoffice-ui -n yas --create-namespace
          helm upgrade --install swagger-ui k8s/charts/swagger-ui -n yas --create-namespace
          helm upgrade --install sampledata k8s/charts/sampledata -n yas --create-namespace

      - name: Verify rollout
        run: |
          kubectl get pods -n yas
          kubectl rollout status deploy/product -n yas --timeout=10m || true
```

Huong dung thuc te:

- `workflow_dispatch` dung de deploy theo branch nhap tay
- `environment` co the map sang `dev` hoac `staging`
- image tag se lay tu branch/commit do workflow build cung cap
- `sampledata` co the chuyen thanh `Job` va chi chay 1 lan sau deploy
