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
- 1 worker node

Hoac co the dung:

- Minikube
- Bat ky mo hinh K8S tuong duong

### 3.3 Expose service

Sau khi deploy tren K8S:

- Dung `NodePort`
- Developer se tu them `hosts` tren may minh de tro domain ve worker node

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

Tao them Jenkins job de xoa phan trien khai cua `developer_build`.

## 5. Nang cao tuy chon

### 5.1 Dev/Staging

Neu lam phan nang cao:

- `main` thay doi thi auto deploy vao namespace `dev`
- khi co release tag nhu `v1.2.3`, CI/CD build image theo tag do
- deploy image vao namespace `staging`

### 5.2 GitOps

Phuong an nang cao:

- dung ArgoCD de quan ly `dev` va `staging`

## 6. Service Mesh nang cao

Neu lam phan service mesh:

- bat mTLS giua cac service
- cau hinh `AuthorizationPolicy`
- cau hinh `VirtualService` retry cho luong loi 500
- dung Kiali de xem topology
- chuan bi kich ban test bang `kubectl exec` va `curl`

Kich ban test can chung minh:

- service bi loi 500 thi retry tu dong
- chi service duoc phep moi goi duoc nhau
- service khong duoc phep thi bi chan ket noi

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

## 8. Ghi chu trien khai thuc te

- Neu chi can demo co ban, hay uu tien core service truoc
- `sampledata` chi dung de seed
- `swagger-ui` la entry point de nhat de verify API
- `storefront-ui` va `backoffice-ui` la phan demo nguoi dung nhin thay truc tiep
- Khi lam K8S, nen chot som domain mapping de khong phai sua nhieu lan

## 9. Ket luan

Uu tien thuc thi:

1. Chuyen Docker Desktop storage sang `D:`
2. Deploy core 14 services theo scope tren
3. Bo observability cho ban co ban
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
   - chon Minikube hoac cluster 1 master + 1 worker
   - deploy 14 service core
   - expose bang `NodePort`
6. Sau khi K8S on dinh, lam CI/CD:
   - build image theo commit id cua branch
   - push len Docker Hub
   - tao job `developer_build`
7. Chi lam service mesh khi phan deploy co ban da chay duoc:
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

- Probe `http://localhost/product/actuator/health` tra ve `500` vi endpoint nay khong co trong cau hinh hien tai
- Cac service dang co log OTEL loi `collector: Name does not resolve` vi stack nay chua co OpenTelemetry Collector

Huong xu ly:

- Su dung cac route API that su co san de kiem tra nhanh
- Neu can health check cho K8S, bat actuator health ro rang trong service
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
