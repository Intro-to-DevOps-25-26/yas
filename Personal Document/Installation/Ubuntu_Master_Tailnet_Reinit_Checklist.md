# Ubuntu Master Tailnet Reinit Checklist

Checklist nay dung de sua cluster hien tai, cho phep worker join qua Tailnet thay vi chi dung LAN IP.

Ap dung khi:

- Master da `kubeadm init` xong
- Worker chua join
- Muon worker truy cap control-plane qua Tailnet IP hoac Tailnet DNS

## 1. Muc tieu

- Dung Tailnet lam duong ket noi on dinh giua master va worker
- Chua `API Server` cert SAN cho:
  - Tailnet IPv4 cua master
  - LAN IP cua master
  - Tailnet DNS cua master
- Re-init cluster sao cho `kubeadm join` chay qua Tailnet duoc

## 2. Thong tin chot

- Tailnet IPv4 cua master: `100.86.160.41`
- LAN IP cua master: `192.168.2.16`
- Tailnet DNS cua master: `master.tailb6a561.ts.net`

## 3. Khuyen nghi

- Vi cluster hien tai chi co master, chua co worker
- Cach an toan nhat la `kubeadm reset` va `kubeadm init` lai
- Khong nen sua chay lung tung SAN cua cert neu co the reset va init lai

## 4. Checklist re-init master

### 4.1 Backup va reset

- [x] Kiem tra chua co worker nao join
- [x] Backup file can thiet neu muon giu lai thong tin cu
- [x] Chay `kubeadm reset -f`
- [x] Xoa file / thu muc kubeadm con sot:
  - [x] `/etc/kubernetes/`
  - [x] `/var/lib/etcd/`
  - [x] `/etc/cni/net.d/`
  - [x] `/var/lib/kubelet/*`
- [x] Kiem tra `containerd` van chay

### 4.2 Tao config init

- [x] Tao file config `kubeadm-tailnet.yaml`
- [x] Dat `localAPIEndpoint.advertiseAddress` bang `100.86.160.41`
- [x] Dat `controlPlaneEndpoint` bang `master.tailb6a561.ts.net:6443`
- [x] Them SAN trong `apiServer.certSANs`:
  - [x] `100.86.160.41`
  - [x] `192.168.2.16`
  - [x] `master.tailb6a561.ts.net`
- [x] Dat `podSubnet` phu hop voi CNI dang dung, vi du `10.244.0.0/16` neu dung Flannel
- [x] Dat `criSocket = unix:///run/containerd/containerd.sock`

### 4.3 Init lai cluster

- [x] Chay `kubeadm init --config kubeadm-tailnet.yaml`
- [x] Kiem tra control-plane init thanh cong
- [x] Copy kubeconfig cho user admin
- [x] Chay `kubectl get nodes -o wide`
- [x] Kiem tra node master hien `Ready`

### 4.4 Cai lai CNI

- [x] Cai CNI plugin lai sau init
- [x] Kiem tra pod `kube-flannel` hoac CNI tuong duong chay on dinh
- [x] Kiem tra CoreDNS chay binh thuong

### 4.5 Tao join command moi

- [x] Chay `kubeadm token create --print-join-command`
- [x] Xac nhan command join dung endpoint tailnet DNS hoac Tailnet IP
- [x] Luu lai command cho worker

## 5. Checklist worker truoc join qua Tailnet

- [ ] Worker da cai `containerd`
- [ ] Worker da cai `kubeadm`
- [ ] Worker da cai `kubelet`
- [ ] Worker da tat swap
- [ ] Worker da load `overlay`
- [ ] Worker da load `br_netfilter`
- [ ] Worker da bat `net.ipv4.ip_forward=1`
- [x] Worker da ket noi cung tailnet voi master
- [ ] Worker ping duoc master qua Tailnet
- [ ] Worker mo duoc port `6443` tren master qua Tailnet
- [x] Worker da co lenh `kubeadm join` moi nhat

## 6. Chay join tren worker

- [ ] Chay `sudo kubeadm join <tailnet-dns-hoac-tailnet-ip>:6443 --token <token> --discovery-token-ca-cert-hash sha256:<hash>`
- [ ] Kiem tra khong bi timeout
- [ ] Kiem tra khong bi loi TLS / x509
- [ ] Kiem tra node xuat hien tren master

## 7. Kiem tra sau join

- [ ] `kubectl get nodes -o wide` hien master va worker
- [ ] Tat ca node `Ready`
- [ ] Pod co the schedule len worker
- [ ] Neu can, fix label / taint theo scope cluster

## 8. Cach test nhanh khi gap loi

- [ ] `nc -vz 100.86.160.41 6443`
- [ ] `curl -k https://100.86.160.41:6443/livez`
- [ ] `tailscale status`
- [ ] `tailscale ip -4`
- [ ] `kubectl get cm -n kube-public cluster-info`
