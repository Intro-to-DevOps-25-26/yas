# Local Test Guide

Muc tieu cua file nay la test tay nhanh sau khi chay Docker Compose, de double check cac API va trang web chinh hoat dong on dinh.

## 1. Truoc khi test

Dam bao:

- Docker Desktop da chay binh thuong
- Da chay `docker compose -f docker-compose.yml -f docker-compose.search.yml up -d`
- Da cap nhat `hosts` voi:
  - `127.0.0.1 api.yas.local`
  - `127.0.0.1 storefront`
  - `127.0.0.1 backoffice`
  - `127.0.0.1 identity`
- Neu test K8S local, da co cluster 1 master + 3 worker va co the dung `kubectl port-forward` de kiem tra service truc tiep
- Neu test K8S tren 4 may qua Tailscale, da co IP tailnet cua tung node va da deploy xong Helm chart

## 2. Thu tu test thu cong

Nen test theo thu tu nay:

1. `swagger-ui`
2. `storefront`
3. `backoffice`
4. `identity`
5. Cac API docs cua `product`, `cart`, `order`, `customer`, `inventory`, `tax`, `media`, `search`
6. `sampledata`
7. `docker ps` hoac `kubectl get pods -n yas`
8. `docker logs` hoac `kubectl logs` neu co service nao sai
9. `kubectl get svc -n yas`
10. `kubectl get job -n yas`

## 3. Test trang web bang trinh duyet

Mo cac URL sau tren browser:

- `http://api.yas.local/swagger-ui/`
- `http://storefront/`
- `http://backoffice/`
- `http://identity/`

Ky vong:

- `swagger-ui` hien giao dien Swagger
- `storefront` hien trang cua shop
- `backoffice` redirect sang trang quan tri
- `identity` redirect sang Keycloak login
- Neu test K8S, `search` co the kiem tra bang `kubectl port-forward -n yas svc/search 18080:80` roi mo `http://127.0.0.1:18080/search/v3/api-docs`
- Neu test K8S qua Tailnet, co the mo `http://<tailscale-ip-cua-worker>:<nodeport>` thay vi localhost

## 4. Test API bang browser hoac curl

### 4.1 URL can mo thu cong

Mo tung URL sau va kiem tra trang tra ve `200 OK`:

- `http://api.yas.local/product/v3/api-docs`
- `http://api.yas.local/cart/v3/api-docs`
- `http://api.yas.local/order/v3/api-docs`
- `http://api.yas.local/customer/v3/api-docs`
- `http://api.yas.local/inventory/v3/api-docs`
- `http://api.yas.local/tax/v3/api-docs`
- `http://api.yas.local/media/v3/api-docs`
- `http://api.yas.local/search/v3/api-docs`

### 4.2 Lenh curl nhanh

Neu muon test nhanh bang terminal:

```powershell
curl.exe -I -H "Host: api.yas.local" http://localhost/product/v3/api-docs
curl.exe -I -H "Host: api.yas.local" http://localhost/cart/v3/api-docs
curl.exe -I -H "Host: api.yas.local" http://localhost/order/v3/api-docs
curl.exe -I -H "Host: api.yas.local" http://localhost/customer/v3/api-docs
curl.exe -I -H "Host: api.yas.local" http://localhost/inventory/v3/api-docs
curl.exe -I -H "Host: api.yas.local" http://localhost/tax/v3/api-docs
curl.exe -I -H "Host: api.yas.local" http://localhost/media/v3/api-docs
curl.exe -I -H "Host: api.yas.local" http://localhost/search/v3/api-docs
curl.exe -I -H "Host: api.yas.local" http://localhost/swagger-ui/
curl.exe -I -H "Host: storefront" http://localhost/
curl.exe -I -H "Host: backoffice" http://localhost/
curl.exe -I -H "Host: identity" http://localhost/
```

Neu can verify `search` tren K8S local:

```powershell
kubectl port-forward -n yas svc/search 18080:80
curl.exe -I http://127.0.0.1:18080/search/v3/api-docs
```

Neu test NodePort qua Tailnet:

```powershell
curl.exe -I http://<tailscale-ip-cua-worker>:<nodeport>/swagger-ui/
curl.exe -I http://<tailscale-ip-cua-worker>:<nodeport>/
```

## 5. Ket qua ky vong

### Trang thai hien tai quan sat duoc

#### API services

- `product` -> `200 OK`
- `cart` -> `200 OK`
- `order` -> `200 OK`
- `customer` -> `200 OK`
- `inventory` -> `200 OK`
- `tax` -> `200 OK`
- `media` -> `200 OK`
- `search` -> `200 OK`
- `search` tren K8S -> `200 OK` qua `port-forward`

#### UI / BFF

- `storefront` -> `200 OK`
- `backoffice` -> `302 Found`
- `swagger-ui` -> `200 OK`
- `identity` -> `302 Found` hoac trang login Keycloak

#### Seed service

- `sampledata` -> `200 OK` tren `/sampledata/v3/api-docs`
- Endpoint seed thuc su nam tai `POST /sampledata/storefront/sampledata`
- Da xac minh seed thanh cong bang POST va nhan ve:
  - `{"message":"Insert Sample Data successfully!"}`
- Tren K8S, `sampledata` da duoc xac minh seed du lieu thanh cong va co the tat sau khi nap du lieu

#### Service mesh

- `mTLS` duoc bat neu da deploy service mesh
- `AuthorizationPolicy` chuyen request tu `allow` va `deny` phai test bang pod khac
- `VirtualService retry` phai co evidence request bi retry khi service tra 500
- `Kiali` phai hien topology va flow neu scope nang cao da deploy

## 6. Check container va log

Kiem tra nhanh xem service con dang chay:

```powershell
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Image}}"
```

Neu can xem log mot service:

```powershell
docker logs --tail 100 yas-product-1
docker logs --tail 100 yas-storefront-1
docker logs --tail 100 yas-backoffice-1
kubectl logs -n yas search-bddfb9f94-th77f --tail 100
kubectl logs -n yas job/sampledata --tail 100
```

## 7. Dau hieu deploy on dinh

Deploy local duoc xem la on dinh khi:

- Tat ca container core deu `Up`
- API docs tra ve `200 OK`
- `storefront` load duoc
- `backoffice` redirect duoc
- `swagger-ui` load duoc
- `sampledata` seed thanh cong neu can dung du lieu mau
- Khong co container core bi restart lien tuc
- `search` khong con fallback ve `localhost:9092` va route qua Nginx tra `200 OK`
- `search` tren K8S da start thanh cong va tra `200 OK` qua `port-forward`
- `mTLS` / `AuthorizationPolicy` / `VirtualService` / `Kiali` co evidence neu scope nang cao da lam

## 8. Dau hieu can xem lai

- `product/actuator/health` tra ve `500`
- Container bi `Restarting`
- API docs khong tra ve `200 OK`
- `storefront` hoac `backoffice` tra ve `502 Bad Gateway`
- `sampledata/storefront/sampledata` tra ve `500` hoac khong insert duoc du lieu
- Log co `UnknownHostException: collector`
- `search` log ra `bootstrap.servers = [localhost:9092]` nghia la runtime bi fallback ve default Kafka localhost, can override bang `SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092`
- `wait-for-it.sh` co CRLF line ending va bao `env: 'bash\r': No such file or directory` neu file chua duoc chuyen sang LF
- Neu `curl localhost` bi `Could not connect to server`, kiem tra lai daemon Docker hoac port 80/route nginx dang co hay khong
- Neu test mesh ma curl van vao duoc service bi chan, kiem tra lai `AuthorizationPolicy` va namespace labels
- Neu retry khong xuat hien, kiem tra lai `VirtualService` va service port/backend selector

Neu gap cac dau hieu tren, ghi lai vao `Personal Document/ErrorEncounter/` neu da sua xong, va cap nhat `Personal Document/Problem/Problem.md`.

## 9. Reset roi test lai

Neu muon tat sach stack, restart Docker Desktop, roi test lai tu dau:

### 9.1 Tat stack hien tai

Chay:

```powershell
docker compose -f docker-compose.yml -f docker-compose.search.yml down
```

Neu muon xoa ca volume va seed data de chay sach hon:

```powershell
docker compose -f docker-compose.yml -f docker-compose.search.yml down -v
```

### 9.2 Dong va mo lai Docker Desktop

Lam thu cong:

1. Thoat Docker Desktop hoac `Quit Docker Desktop`
2. Mo lai Docker Desktop
3. Cho den khi engine san sang

### 9.3 Kiem tra lai sau khi mo Docker Desktop

Chay:

```powershell
docker version
docker ps
```

Ky vong:

- `docker version` ket noi duoc vao engine
- `docker ps` khong bao loi `docker_engine`

### 9.4 Chay lai stack

Sau khi Docker Desktop da len on dinh:

```powershell
docker compose -f docker-compose.yml -f docker-compose.search.yml up -d
```

### 9.5 Test lai theo thu tu cu

Sau khi stack len lai:

1. `http://api.yas.local/swagger-ui/`
2. `http://storefront/`
3. `http://backoffice/`
4. Cac API docs cua `product`, `cart`, `order`, `customer`, `inventory`, `tax`, `media`, `search`
5. `docker ps`
6. Neu test K8S, dung `kubectl get pods -n yas` va `kubectl port-forward` de doi chieu
7. Neu test mesh, dung `kubectl exec` tu pod khac va `curl -v` de chung minh allow/deny

## 10. Checklist nhanh

### Da xong

- [x] Docker Desktop storage da chuyen sang `D:`
- [x] 14 service core theo scope da duoc xac dinh
- [x] `search` da override Kafka bootstrap dung `kafka:9092`
- [x] `search`, `storefront`, `backoffice` dang `Up`
- [x] `search` da start thanh cong trong K8S va tra ve `200 OK` qua `port-forward`
- [x] `storefront` tra ve `200 OK`
- [x] `backoffice` tra ve `302 Found`
- [x] Seed `sampledata` da xac minh thanh cong
- [x] Log OTEL `collector` da duoc ghi nhan vao `ErrorEncounter`
- [x] Da co checklist K8S qua Tailnet va test service mesh trong local_test

### Con lai

- [ ] Neu muon, tat OTEL local hoac add collector de giam log noise
- [ ] Khi sang K8S, lam health check ro rang cho tat ca service
- [ ] Chuan bi manifest `Deployment` / `Service` / `NodePort`
- [ ] Lam CI/CD build image theo branch/tag/commit
- [ ] Neu can, tiep tuc lam service mesh cho retry, mTLS, authorization
