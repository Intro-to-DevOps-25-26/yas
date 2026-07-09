# Ubuntu Master Tailscale IP Switch Checklist

Checklist nay dung de dong bo cluster sang Tailnet IP `100.x.x.x` theo cach it rui ro nhat.

## 0. Trang thai hien tai da xac nhan

- [x] Master control-plane dang `Ready`
- [x] `default/kubernetes` endpoint dang tro ve `100.86.160.41:6443`
- [ ] `worker-1` dang `NotReady`
- [x] `worker-3` dang `Ready`
- [ ] `naul1-pc` dang `NotReady`
- [x] CoreDNS da tung co nhieu error lien quan upstream Tailnet DNS, nhung da verify resolve noi bo / external khi node tam on
- [x] `product` va mot so app backend dang crash do node / DNS / Istio network chua on dinh

## 1. Muc tieu

- Dung `100.x.x.x` lam IP chinh cho master va worker trong cluster.
- Tranh lech giua LAN IP va Tailnet IP khi:
  - worker goi API server
  - CoreDNS forward DNS
  - Istio webhook / xDS connect
  - Flannel overlay giao tiep giua cac node

## 2. Backup bat buoc truoc khi doi IP

- [ ] Export `kubectl get nodes -o wide`
- [ ] Export `kubectl get pods -A -o wide`
- [ ] Backup `/etc/kubernetes/`
- [ ] Backup `/etc/cni/net.d/`
- [ ] Backup `/var/lib/kubelet/`
- [ ] Backup `/var/lib/etcd/` neu can rollback control-plane
- [ ] Backup file kubeadm config / init config
- [ ] Luu lai `kubeadm join` command moi nhat
- [ ] Luu lai cert SAN dang co cua apiserver

## 3. Kiem tra truoc khi doi

- [ ] Master co `tailscale status` on dinh
- [ ] `tailscale ip -4` tren master ra dung `100.86.160.41`
- [ ] `nc -vz 100.86.160.41 6443` thanh cong tu worker
- [ ] `kubectl get endpoints -n default kubernetes` trỏ dung ve Tailnet IP
- [ ] `kubectl get pods -n kube-system -l k8s-app=kube-dns` dang `Running`
- [ ] Kiem tra worker con lai co the resolve:
  - [ ] `postgresql.postgres.svc.cluster.local`
  - [ ] `istiod.istio-system.svc`
  - [ ] `keycloak-service.keycloak.svc.cluster.local`

## 4. Thu tu doi IP it rui ro nhat

### 4.1 Master

- [x] Chot `kube-apiserver advertise address` sang `100.86.160.41`
- [x] Dam bao SAN cert co:
  - [ ] `100.86.160.41`
  - [ ] `192.168.2.16`
  - [ ] `master.tailb6a561.ts.net`
- [x] Kiem tra kubelet tren master dung node IP phu hop
- [x] Restart `kubelet` / apiserver neu can
- [x] Kiem tra lai `kubectl get nodes -o wide`

### 4.2 Worker

- [ ] Dong bo `kubelet --node-ip` tren worker sang Tailnet IP
- [ ] Kiem tra `kube-proxy` nhan dung node IP moi
- [ ] Restart `kube-proxy` sau khi doi IP
- [ ] Kiem tra `flannel` backend-data / public-ip tren node
- [ ] Chi them worker moi sau khi worker cu da on

### 4.3 DNS / networking

- [ ] Kiem tra CoreDNS `forward` khong phu thuoc upstream Tailnet DNS loi
- [ ] Neu can, pin upstream DNS on dinh hon
- [ ] Restart CoreDNS sau khi sua config
- [x] Da verify bang pod test:
  - [x] `identity.yas.local.com -> 10.98.44.199`
  - [x] `postgresql.postgres.svc.cluster.local -> 10.108.199.46`
  - [x] `github.com` va `google.com` resolve duoc
- [ ] Kiem tra `nslookup` cho:
  - [ ] `postgresql.postgres.svc.cluster.local`
  - [ ] `istiod.istio-system.svc`
  - [ ] `identity.yas.local.com`

## 5. Kiem tra sau doi

- [ ] Tat ca node `Ready`
- [ ] `kubectl get pods -A -o wide` khong con node `Unknown`
- [ ] `kube-dns` / CoreDNS `2/2 Ready`
- [ ] `istiod` san sang
- [ ] `postgresql` san sang
- [ ] `product`, `customer`, `inventory`, `cart`, `search`, `tax`, `storefront-bff` rollout thanh cong

## 6. Trang thai cap nhat sau khi doi master

- Master da chuyen sang `InternalIP = 100.86.160.41`.
- Apiserver manifest da advertise qua Tailnet IP.
- Kubelet config da duoc restart va node object da cap nhat.
- `worker-3` dang `Ready`.
- `worker-1` dang `NotReady` do kubelet stopped posting node status va reboot flap.
- `naul1-pc` hien tai `NotReady`, can theo doi rieng.

## 7. Neu gap loi

- [ ] Neu apiserver khong tu dong len lai, rollback ngay bang backup
- [ ] Neu DNS khong resolve, kiem tra lai upstream CoreDNS va kube-proxy rules
- [ ] Neu worker `NotReady`, khong rollout app tiep
- [ ] Neu `flannel` mat `subnet.env`, restart CNI tren node do truoc

## 8. Ghi chu rui ro

- Doi `InternalIP` khong phai la buoc doc lap, ma phai di kem:
  - kubelet
  - kube-proxy
  - flannel
  - CoreDNS
  - cert SAN cua apiserver
- Khong nen doi nua LAN nua Tailnet trong cung mot cluster vi de sinh loi DNS, xDS va service routing.
