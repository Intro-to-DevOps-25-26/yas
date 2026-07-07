# Ubuntu Master YAS Current Remaining Issues

Ngay: 2026-07-07

## 1. Backoffice BFF

- Pod:
  - `yas/backoffice-bff-5959897f4f-shlts`
  - `yas/backoffice-bff-d47f896bc-m5vjn`
- Node:
  - `worker-1`
- Trang thai:
  - `CrashLoopBackOff`
- Loi chinh:
  - OAuth2 issuer mismatch
  - Config dang dung `http://keycloak-service.keycloak.svc.cluster.local/realms/Yas`
  - Keycloak metadata tra ve `http://identity.yas.local.com/realms/Yas`

## 2. Media

- Pod:
  - `yas/media-65dfbbfccd-p77wm`
  - `yas/media-fb8b4b8bd-nlp47`
- Node:
  - `worker-1`
- Trang thai:
  - `CrashLoopBackOff`
- Loi chinh:
  - Liquibase migration fail
  - `relation "media" already exists`
  - Can reset dung schema/database de chay lai DDL sach

## 3. Search

- Pod:
  - `yas/search-7cf85d657-lf4ts`
  - `yas/search-8654565bbf-npv9m`
- Node:
  - `worker-1`
- Trang thai:
  - `CrashLoopBackOff`
- Loi chinh:
  - Elasticsearch repository bootstrap fail
  - `es/indices.exists` tra ve `400`
  - Can kiem tra lai endpoint / compatibility / index bootstrap

## 4. Storefront BFF

- Pod:
  - `yas/storefront-bff-6cf797d46c-99z7t`
  - `yas/storefront-bff-7759d6f44d-qv5kg`
- Node:
  - `worker-3`
- Trang thai:
  - `CrashLoopBackOff` / `Error`
- Loi chinh:
  - App chua bind duoc `8090`
  - Startup fail o buoc OAuth2 issuer resolution
  - `UnknownHostException: identity.yas.local.com`
  - DNS query tu pod tren `worker-3` bi timeout, trong khi pod tren `worker-1` resolve binh thuong
  - Restart deployment chua khac phuc duoc

## 5. Tong Ket Ngan

- Hien chi con 4 nhom tren la chua on dinh.
- Cac service core da on:
  - `customer`
  - `inventory`
  - `product`
  - `tax`
  - `keycloak`
  - `postgresql`
  - `redis`
  - `debezium-connect`
  - `elasticsearch`
- `storefront-bff` da duoc restart nhung van chua ready, nen chua the ket luan la da fix xong.
- Cac loi con lai la loi app/config rieng cua tung service, khong con la loi pod-network hoac cluster-wide nua.
