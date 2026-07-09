# Ubuntu Master YAS Deploy Pod Status Checklist

Ngay: 2026-07-06

## 1. Pod da deploy on

- [x] `postgres-operator-6f6844846-flntn`
- [x] `postgresql-0`
- [x] `elastic-operator-0`
- [x] `kibana-kb-54dd77d49d-cwll7`
- [x] `strimzi-cluster-operator-7bf889cd5c-p75ts`
- [x] `kafka-cluster-zookeeper-0`
- [x] `redis-master-0`
- [x] `keycloak-operator-6f8898f9bf-8zd6j`
- [x] `backoffice-ui-7fc65d58b8-jlhkg`
- [x] `storefront-ui-8596bcd69d-4258b`
- [x] `swagger-ui-6b5c6bd67-62rms`
- [x] `yas-reloader-c7c545cbf-75qfs`
- [x] `seed-schema-and-sampledata-worker3`

## 2. Pod van con loi

- [ ] `elasticsearch-es-node-0`
- [ ] `debezium-connect-cluster-connect-0`
- [ ] `keycloak-0`
- [ ] `redis-replicas-0`
- [ ] `backoffice-bff-5959897f4f-nxg7g`
- [ ] `backoffice-bff-d47f896bc-hrzrq`
- [ ] `cart-6f45ffc58-nbc5v`
- [ ] `cart-75dcc8f96b-ztmpb`
- [ ] `customer-6f9c5d4f6-7745j`
- [ ] `customer-ddfbb79fc-vs59c`
- [ ] `inventory-5cdcdbf9f-7n2bn`
- [ ] `inventory-78c79d5787-tvd6j`
- [ ] `media-65dfbbfccd-5l7v8`
- [ ] `media-fb8b4b8bd-p5v68`
- [ ] `order-6466cf4788-n8kh7`
- [ ] `order-6d89d75dfb-gsbgp`
- [ ] `product-57cbf45d9c-ltx92`
- [ ] `product-79d85cb4bc-qkp4r`
- [ ] `search-7cf85d657-kgtzf`
- [ ] `search-8654565bbf-c69wn`
- [ ] `storefront-bff-77d44f9f76-xqmdr`
- [ ] `storefront-bff-7bb884f4bb-pmx54`
- [ ] `tax-bdf6d7bbb-pkrrx`
- [ ] `tax-d6fddd4d4-xkkgg`

## 3. Pod debug/tam thoi

- [ ] `crictl-check-state-worker3`
- [ ] `crictl-conn-test-worker3`
- [ ] `crictl-count-worker3`
- [ ] `crictl-list-worker3`
- [ ] `crictl-pg-du-worker3`
- [ ] `crictl-pg-listdb-worker3`
- [ ] `crictl-postgres-id-worker3`
- [ ] `crictl-psql-test-worker3`
- [ ] `crictl-psql-test-worker3-v2`
- [ ] `dnscheck-master`
- [ ] `dnscheck-worker1`
- [ ] `host-tools-worker3`
- [ ] `hostnet-master`
- [ ] `hostnet-worker3`
- [ ] `psql-count-media2`
- [ ] `psql-count-product2`
- [ ] `psql-count-product3`
- [ ] `sampledata-seed-manual`
- [ ] `sampledata-seed-manual-cp`
- [ ] `sampledata-seed-manual-ip`
- [ ] `sampledata-seed-manual-worker3`
- [ ] `seed-sampledata-worker3`
- [ ] `seed-sampledata-worker3-v2`

## 4. Ghi chu

- `sampledata` da seed thu cong thanh cong, khong dung `Job`
- `backoffice-ui`, `storefront-ui`, `swagger-ui` la cac pod da on dinh va co the tiep tuc dung de test
- Cac pod backend con lai can tiep tuc debug theo tung service va dependency
