# K8S 4 Machine Deploy Guide

Huong dan nay ap dung cho mo hinh:

- 4 may vat ly
- 1 may `control-plane`
- 3 may `worker`
- tat ca may da join chung 1 Tailnet
- deploy app bang `kubectl` va `helm`

## 1. Muc tieu

- Dung Tailscale de 4 may nhin thay nhau on dinh
- Dung Kubernetes de phan role master / worker ro rang
- Dung Helm de deploy YAS app
- Dung NodePort cho web demo
- Dung ClusterIP cho service noi bo
- Dung Job cho `sampledata`

## 2. Phan cong node

### 2.1 Mapping ten node

- `master` - `Tú`: control-plane
- `worker-1` - `Hòa`: chay workload app
- `worker-2` - `Luân`: chay workload app
- `worker-3` - `Khoa`: chay workload app

### 2.2 Ghi chu

- 1 may co the chay nhieu node neu dung `kind`, `k3d`, hoac VM
- 1 may vua master vua worker thi van lam duoc, nhung khong khuyen khich cho do an nay vi lam mo mo hinh role

## 3. Danh sach can cai dat

### 3.1 Tren ca 4 may

- `Tailscale`
- `containerd` hoac `Docker`
- `kubectl`
- `kubeadm`
- `kubelet`
- `helm`
- `curl`
- `jq`
- `git`
- `vim` hoac editor tuong duong
- `net-tools` hoac `iproute2`
- `bash`
- `ca-certificates`

### 3.2 Tren may master

- `kubeadm init`
- CNI plugin nhu `Calico`, `Flannel`, hoac plugin ma cluster chon
- kubeconfig cho admin
- `openssl`

### 3.3 Tren worker

- `kubeadm join`
- Kubelet dang chay
- Container runtime dang chay

### 3.4 Tren may admin/dev

- `kubectl`
- `helm`
- `tailscale`
- `curl`
- `jq`
- `git`

### 3.5 Neu muon debug nhanh

- `wget`
- `telnet` hoac `nc`
- `openssl`

## 4. Cai Tailnet

Lam tren ca 4 may:

1. Cai Tailscale
2. Dang nhap cung 1 tailnet
3. Dat ten may:
   - `master`
   - `worker-1`
   - `worker-2`
   - `worker-3`
4. Ghi lai IP Tailnet cua tung may

Lenh tham khao:

```bash
tailscale status
tailscale ip -4
```

Neu dung may server unattended:

- dung auth key
- gan tag
- khong can login tay moi lan

## 5. Chuẩn bi truoc khi cai Kubernetes

Tren moi may:

- tat swap
- cai container runtime
- cai `kubeadm`, `kubelet`, `kubectl`
- bat bridge networking neu can

Neu dung Linux, `kubeadm` la cach ro rang nhat cho mo hinh master/worker.

## 6. Bootstrap master

Tren may master:

1. Cai runtime va Kubernetes tool
2. Chay `kubeadm init`
3. Dung Tailnet IP cua master lam `advertise address`
4. Cai `kubectl` config cho user admin
5. Cai CNI plugin

Vi du y tuong:

```bash
kubeadm init \
  --apiserver-advertise-address=<master-tailnet-ip> \
  --pod-network-cidr=10.244.0.0/16
```

Sau do:

- copy cau hinh `kubectl`
- cai CNI plugin
- lay lenh `kubeadm join` de worker tham gia cluster

## 7. Join worker

Tren moi worker:

1. Cai runtime va Kubernetes tool
2. Run lenh `kubeadm join` do master sinh ra
3. Kiem tra node da vao cluster

Vi du:

```bash
kubeadm join <master-tailnet-ip>:6443 --token <token> --discovery-token-ca-cert-hash sha256:<hash>
```

## 8. Kiem tra cluster

Tu may master hoac may admin co kubeconfig:

```bash
kubectl get nodes -o wide
kubectl get pods -A
```

Ky vong:

- master hien `control-plane`
- 3 worker hien `Ready`

## 9. Cach dat workload

### 9.1 Rule chung

- `Deployment` cho service chay lau dai
- `Service` cho expose noi bo / ngoai
- `Job` cho tac vu 1 lan

### 9.2 Service mapping chot

- `NodePort`: `swagger-ui`, `storefront-ui`, `backoffice-ui`
- `ClusterIP`: `storefront-bff`, `backoffice-bff`, `product`, `cart`, `order`, `customer`, `inventory`, `tax`, `media`, `search`
- `Job`: `sampledata`

## 10. Cai Helm

Tren may admin:

```bash
helm version
```

Neu chua co:

- cai Helm truoc khi deploy

## 11. Deploy YAS

Thu tu goi y:

1. Deploy shared config
2. Deploy infra neu can
3. Deploy app charts
4. Deploy `sampledata` Job

### 11.1 Shared config

```bash
helm upgrade --install yas-configuration k8s/charts/yas-configuration \
  -n yas --create-namespace
```

### 11.2 App charts

```bash
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
```

## 12. Kiem tra sau deploy

```bash
kubectl get pods -n yas -o wide
kubectl get svc -n yas
kubectl get job -n yas
kubectl logs -n yas deploy/search --tail=100
```

Neu `sampledata` la Job:

- check pod cua Job
- check log cua Job
- xac nhan DB da co du lieu

## 13. Truy cap app

### 13.1 Neu dung NodePort

Lay node port:

```bash
kubectl get svc -n yas
```

Truy cap:

```text
http://<tailscale-ip-cua-worker>:<nodeport>
```

### 13.2 Neu dung domain qua hosts

Tren may dev/debug, add hosts:

```text
<tailscale-ip-cua-worker> storefront
<tailscale-ip-cua-worker> backoffice
<tailscale-ip-cua-worker> api.yas.local
```

Sau do mo:

```text
http://storefront:<nodeport>
http://backoffice:<nodeport>
http://api.yas.local:<nodeport>
```

Neu muon test nhanh khong can hosts:

- dung thang `tailscale-ip:port`

## 14. Kiem tra web de test

- `swagger-ui`
- `storefront`
- `backoffice`
- `search` neu can debug

Ky vong:

- `swagger-ui` hien API docs
- `storefront` vao UI shop
- `backoffice` vao UI admin

## 15. Kiem tra noi bo trong cluster

Dung khi muon test service-to-service:

```bash
kubectl exec -n yas <pod-khac> -- curl -v http://product.yas.svc.cluster.local
```

Hoac:

```bash
kubectl exec -n yas <pod-khac> -- curl -v http://search.yas.svc.cluster.local
```

## 16. Neu muon 1 may chay nhieu node

Khuyen nghi dung:

- `kind`
- `k3d`
- VM nested

Khong nen ep 4 node that tren 1 may vat ly neu muc tieu la demo role master/worker ro rang.

## 17. Luu y quan trong

- Tailnet chi giai quyet network
- Kubernetes moi giai quyet scheduling
- Helm moi deploy app
- `kubectl` moi la cong cu kiem tra cluster va troubleshooting

