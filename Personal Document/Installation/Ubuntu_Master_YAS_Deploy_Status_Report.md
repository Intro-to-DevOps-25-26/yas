# Ubuntu Master YAS Deploy Status Report

Ngay: 2026-07-06

## 1. Da on dinh

- `kubectl get nodes -o wide`: 4/4 node `Ready`
- `yas-configuration` da deploy thanh cong
- `backoffice-ui`, `storefront-ui`, `swagger-ui` da len pod va co `NodePort`
- `kafka-operator` va `kafka-cluster` da duoc tao, `Kafka` / `KafkaConnect` / `KafkaConnector` da co object trong cluster
- `postgres-operator` va `postgresql` da len
- `local-path-provisioner` da co storage class
- `sampledata` da seed thu cong 1 lan thanh cong, khong can Job lau dai

## 2. Da deploy nhung chua on dinh

- `keycloak`: pod van crashloop, can kiem tra lai bootstrap admin / rollout cua operator
- `elasticsearch`: pod van crashloop, can theo doi log va tinh tiep resource / config cua ECK
- `redis-replicas`: van crashloop
- `debezium-connect-cluster-connect`: van crashloop
- `product`, `cart`, `customer`, `inventory`, `media`, `order`, `tax`, `search`, `storefront-bff`, `backoffice-bff`:
  - da tao pod
  - nhung van crashloop do cau hinh noi bo chua khop
  - log hien tai cho thay nhieu service dang bi loi DNS / dependency

## 3. Nguyen nhan da xac dinh

- `kube-dns` ban dau khong co endpoints, nen pod khong resolve duoc service name
- Service IP cua PostgreSQL va CoreDNS deu timeout tu pod, nen seed qua `ClusterIP` hoac DNS deu that bai
- `yasadminuser` ban dau chua ton tai trong PostgreSQL, va database `product` / `media` cung chua duoc tao
- `sampledata` khong can chay dang `Job`; seed duoc lam thu cong 1 lan sau khi schema va role/database da co

## 4. Da sua

- Tat `ServiceMonitor` khi deploy cac backend chart
- Sua `yas-configuration` de dung:
  - Keycloak issuer noi bo
  - PostgreSQL FQDN noi bo
- Doi huong `sampledata` sang seed thu cong 1 lan, khong giu Job
- Sua Kafka chart ve `kafka.strimzi.io/v1beta2`
- Sua Elasticsearch chart de co resource hop ly hon
- Tao lai role `yasadminuser`
- Tao database `product` va `media`
- Nạp schema `product` va `media` bang cac changelog Liquibase, sau do seed sampledata thanh cong

## 5. Viec con lai

1. Giam crashloop cua `keycloak`, `elasticsearch`, `redis-replicas`, `debezium-connect`
2. Khi cluster on dinh hon, tiep tuc doi chieu backend crashloop voi config va dependency
3. Cap nhat checklist theo trang thai thuc te moi nhat neu co them thay doi
