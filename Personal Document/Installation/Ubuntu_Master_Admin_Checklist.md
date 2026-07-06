# Ubuntu Master + Admin Checklist

Checklist nay dung cho may Ubuntu moi tinh, vai tro:

- `master` / `control-plane` cho K8S
- `admin deploy` de chay `kubectl`, `helm`, `tailscale`

## 1. Muc tieu

- Tao mot may Ubuntu san sang lam master K8S
- Co the dung may nay de deploy YAS bang Helm
- Co the quan ly cluster bang `kubectl`
- Co the ket noi cluster qua Tailscale

## 2. San pham can co

### 2.1 Bat buoc

- [x] Ubuntu Server LTS da cai
- [x] Hostname dat ro rang
- [x] `sudo` quyen admin
- [x] `containerd`
- [x] `kubeadm`
- [x] `kubelet`
- [x] `kubectl`
- [x] `Tailscale`
- [x] `Helm`
- [x] `git`
- [x] `curl`
- [x] `jq`
- [x] `openssl`
- [x] `ca-certificates`
- [x] `gnupg`
- [x] `lsb-release`
- [x] `iproute2`
- [x] `conntrack`
- [x] `socat`
- [x] `ethtool`

### 2.2 Nen co

- [x] `wget`
- [x] `vim` hoac `nano`
- [x] `bash-completion`
- [x] `net-tools`
- [x] `telnet` hoac `nc`

## 3. Chuan bi he thong

- [x] `sudo apt update`
- [x] `sudo apt upgrade -y`
- [x] Tat swap
- [x] Bo swap khoi `/etc/fstab`
- [x] Load kernel module `overlay`
- [x] Load kernel module `br_netfilter`
- [x] Bat `net.ipv4.ip_forward=1`
- [x] Bat bridge netfilter sysctl
- [x] Kiem tra gio he thong dong bo tot

## 4. Cai container runtime

- [x] Cai `containerd`
- [x] Cau hinh `containerd` dung `systemd cgroup`
- [x] Khoi dong `containerd`
- [x] Enable `containerd` luc boot
- [x] Kiem tra `containerd` dang chay

## 5. Cai Kubernetes tools

- [x] Cai `kubeadm`
- [x] Cai `kubelet`
- [x] Cai `kubectl`
- [x] Kiem tra version cac tool
- [x] Khoa version neu can de dong bo voi cluster

## 6. Cai Tailscale

- [x] Cai `Tailscale`
- [x] Dang nhap vao cung tailnet
- [x] Dat hostname / tag cho may master
- [x] Lay IP Tailscale cua master
- [x] Kiem tra `tailscale status`
- [x] Kiem tra may master nhin thay cac worker trong tailnet

## 7. Cai Helm va tool deploy

- [x] Cai `Helm`
- [x] Kiem tra `helm version`
- [x] Cai `git`
- [x] Cai `curl`
- [x] Cai `jq`
- [x] Cai `openssl`

## 8. Bootstrap control plane

- [x] Chon IP cua master lam advertise address (LAN IP 192.168.2.16 vi Tailscale da skip)
- [x] Chay `kubeadm init`
- [x] Copy kubeconfig cho user admin
- [x] Kiem tra `kubectl get nodes`
- [x] Cai CNI plugin
- [x] Kiem tra node control-plane sang `Ready`

## 9. Kiem tra worker join

- [ ] Worker da cai `containerd`
- [ ] Worker da cai `kubeadm`
- [ ] Worker da cai `kubelet`
- [ ] Worker da join cluster thanh cong
- [ ] `kubectl get nodes -o wide` hien 1 master + 3 worker

## 10. Cau hinh deploy YAS

- [x] Clone repo YAS len may
- [x] Kiem tra `k8s/charts/`
- [x] Kiem tra `k8s/deploy/`
- [x] Kiem tra `Problem.md`
- [x] Kiem tra `local_test.md`
- [x] Kiem tra `K8S_4Machine_Deploy_Guide.md`

## 11. Deploy YAS

- [ ] Deploy `yas-configuration`
- [ ] Deploy `product`
- [ ] Deploy `cart`
- [ ] Deploy `order`
- [ ] Deploy `customer`
- [ ] Deploy `inventory`
- [ ] Deploy `tax`
- [ ] Deploy `media`
- [ ] Deploy `search`
- [ ] Deploy `storefront-bff`
- [ ] Deploy `storefront-ui`
- [ ] Deploy `backoffice-bff`
- [ ] Deploy `backoffice-ui`
- [ ] Deploy `swagger-ui`
- [ ] Seed `sampledata` thu cong 1 lan tren master

## 12. Kiem tra sau deploy

- [ ] `kubectl get pods -n yas -o wide`
- [ ] `kubectl get svc -n yas`
- [ ] `kubectl logs -n yas deploy/search`
- [ ] Chay SQL seed `sampledata` cho `product` va `media`
- [ ] Xac nhan DB da co du lieu mau

## 13. Truy cap test

- [ ] Truy cap `swagger-ui` qua `tailscale-ip:nodeport`
- [ ] Truy cap `storefront-ui` qua `tailscale-ip:nodeport`
- [ ] Truy cap `backoffice-ui` qua `tailscale-ip:nodeport`
- [ ] Test `kubectl port-forward` cho `search`
- [ ] Test service-to-service trong cluster bang `kubectl exec`

## 14. Checklist hoan tat

- [ ] Master Ubuntu san sang lam control-plane
- [ ] Admin deploy san sang chay `kubectl` va `helm`
- [ ] Cluster join thanh cong 1 master + 3 worker
- [ ] YAS deploy thanh cong
- [ ] Web demo truy cap duoc
- [ ] `sampledata` da seed thanh cong theo cach thu cong 1 lan
- [ ] Tailscale truy cap on dinh
