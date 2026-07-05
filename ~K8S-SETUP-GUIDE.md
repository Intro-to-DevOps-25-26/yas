# Hướng dẫn triển khai K8S 4 máy

Hướng dẫn này áp dụng cho mô hình:

- 4 máy vật lý
- 1 máy `control-plane`
- 3 máy `worker`
- Tất cả máy đã join chung 1 Tailnet
- Deploy app bằng `kubectl` và `helm`

## 1. Mục tiêu

- Dùng Tailscale để 4 máy nhìn thấy nhau ổn định
- Dùng Kubernetes để phân role master / worker rõ ràng
- Dùng Helm để deploy YAS app
- Dùng NodePort cho web demo
- Dùng ClusterIP cho service nội bộ
- Dùng Job cho `sampledata`

## 2. Phân công node

### 2.1 Mapping tên node

- `master` - `Tú`: control-plane
- `worker-1` - `Hòa`: chạy workload app
- `worker-2` - `Luân`: chạy workload app
- `worker-3` - `Khoa`: chạy workload app

### 2.2 Ghi chú

- 1 máy có thể chạy nhiều node nếu dùng `kind`, `k3d`, hoặc VM
- 1 máy vừa master vừa worker thì vẫn làm được, nhưng không khuyến khích cho đồ án này vì làm mờ mô hình role

## 3. Danh sách cần cài đặt

### 3.1 Trên cả 4 máy

- `Tailscale`
- `containerd` hoặc `Docker`
- `kubectl`
- `kubeadm`
- `kubelet`
- `helm`
- `curl`
- `jq`
- `git`
- `vim` hoặc editor tương đương
- `net-tools` hoặc `iproute2`
- `bash`
- `ca-certificates`

### 3.2 Trên máy master

- `kubeadm init`
- CNI plugin như `Calico`, `Flannel`, hoặc plugin mà cluster chọn
- kubeconfig cho admin
- `openssl`

### 3.3 Trên worker

- `kubeadm join`
- Kubelet đang chạy
- Container runtime đang chạy

### 3.4 Trên máy admin/dev

- `kubectl`
- `helm`
- `tailscale`
- `curl`
- `jq`
- `git`

### 3.5 Nếu muốn debug nhanh

- `wget`
- `telnet` hoặc `nc`
- `openssl`

## 4. Cài Tailnet

Làm trên cả 4 máy:

1. Cài Tailscale
2. Đăng nhập cùng 1 tailnet
3. Đặt tên máy:
   - `master`
   - `worker-1`
   - `worker-2`
   - `worker-3`
4. Ghi lại IP Tailnet của từng máy

Lệnh tham khảo:

```bash
tailscale status
tailscale ip -4
```

Nếu dùng máy server unattended:

- dùng auth key
- gán tag
- không cần login tay mỗi lần

## 5. Chuẩn bị trước khi cài Kubernetes

Trên mỗi máy:

- tắt swap
- cài container runtime
- cài `kubeadm`, `kubelet`, `kubectl`
- bật bridge networking nếu cần

Nếu dùng Linux, `kubeadm` là cách rõ ràng nhất cho mô hình master/worker.

## 6. Bootstrap master

Trên máy master:

1. Cài runtime và Kubernetes tool
2. Chạy `kubeadm init`
3. Dùng Tailnet IP của master làm `advertise address`
4. Cài `kubectl` config cho user admin
5. Cài CNI plugin

Ví dụ ý tưởng:

```bash
kubeadm init \
  --apiserver-advertise-address=<master-tailnet-ip> \
  --pod-network-cidr=10.244.0.0/16
```

Sau đó:

- copy cấu hình `kubectl`
- cài CNI plugin
- lấy lệnh `kubeadm join` để worker tham gia cluster

## 7. Join worker

Trên mỗi worker:

1. Cài runtime và Kubernetes tool
2. Chạy lệnh `kubeadm join` do master sinh ra
3. Kiểm tra node đã vào cluster

Ví dụ:

```bash
kubeadm join <master-tailnet-ip>:6443 --token <token> --discovery-token-ca-cert-hash sha256:<hash>
```

## 8. Kiểm tra cluster

Từ máy master hoặc máy admin có kubeconfig:

```bash
kubectl get nodes -o wide
kubectl get pods -A
```

Kỳ vọng:

- master hiện `control-plane`
- 3 worker hiện `Ready`

## 9. Cách đặt workload

### 9.1 Rule chung

- `Deployment` cho service chạy lâu dài
- `Service` cho expose nội bộ / ngoài
- `Job` cho tác vụ 1 lần

### 9.2 Service mapping chốt

- `NodePort`: `swagger-ui`, `storefront-ui`, `backoffice-ui`
- `ClusterIP`: `storefront-bff`, `backoffice-bff`, `product`, `cart`, `order`, `customer`, `inventory`, `tax`, `media`, `search`
- `Job`: `sampledata`

## 10. Cài Helm

Trên máy admin:

```bash
helm version
```

Nếu chưa có:

- cài Helm trước khi deploy

## 11. Deploy YAS

Thứ tự gợi ý:

1. Deploy shared config
2. Deploy infra nếu cần
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

## 12. Kiểm tra sau deploy

```bash
kubectl get pods -n yas -o wide
kubectl get svc -n yas
kubectl get job -n yas
kubectl logs -n yas deploy/search --tail=100
```

Nếu `sampledata` là Job:

- check pod của Job
- check log của Job
- xác nhận DB đã có dữ liệu

## 13. Truy cập app

### 13.1 Nếu dùng NodePort

Lấy node port:

```bash
kubectl get svc -n yas
```

Truy cập:

```text
http://<tailscale-ip-cua-worker>:<nodeport>
```

### 13.2 Nếu dùng domain qua hosts

Trên máy dev/debug, add hosts:

```text
<tailscale-ip-cua-worker> storefront
<tailscale-ip-cua-worker> backoffice
<tailscale-ip-cua-worker> api.yas.local
```

Sau đó mở:

```text
http://storefront:<nodeport>
http://backoffice:<nodeport>
http://api.yas.local:<nodeport>
```

Nếu muốn test nhanh không cần hosts:

- dùng thẳng `tailscale-ip:port`

## 14. Kiểm tra web để test

- `swagger-ui`
- `storefront`
- `backoffice`
- `search` nếu cần debug

Kỳ vọng:

- `swagger-ui` hiện API docs
- `storefront` vào UI shop
- `backoffice` vào UI admin

## 15. Kiểm tra nội bộ trong cluster

Dùng khi muốn test service-to-service:

```bash
kubectl exec -n yas <pod-khac> -- curl -v http://product.yas.svc.cluster.local
```

Hoặc:

```bash
kubectl exec -n yas <pod-khac> -- curl -v http://search.yas.svc.cluster.local
```

## 16. Nếu muốn 1 máy chạy nhiều node

Khuyến nghị dùng:

- `kind`
- `k3d`
- VM nested

Không nên ép 4 node thật trên 1 máy vật lý nếu mục tiêu là demo role master/worker rõ ràng.

## 17. Lưu ý quan trọng

- Tailnet chỉ giải quyết network
- Kubernetes mới giải quyết scheduling
- Helm mới deploy app
- `kubectl` mới là công cụ kiểm tra cluster và troubleshooting
