# Ubuntu Worker Before Join Checklist

Checklist nay dung cho may Ubuntu worker truoc khi chay `kubeadm join` vao cluster YAS.

Ap dung cho mo hinh:

- 1 master control-plane
- nhieu worker
- worker se chay workload app

## 1. Muc tieu

- Chuan bi worker de join Kubernetes cluster
- Dam bao worker co runtime va kernel settings phu hop
- Dam bao worker co the giao tiep on dinh voi master

## 2. San pham can co

### 2.1 Bat buoc

- [ ] Ubuntu Server LTS da cai
- [ ] Hostname dat ro rang
- [ ] `sudo` quyen admin
- [ ] `Tailscale` da cai va da ket noi cung tailnet voi master
- [ ] `containerd`
- [ ] `kubeadm`
- [ ] `kubelet`
- [ ] `curl`
- [ ] `ca-certificates`
- [ ] `gnupg`
- [ ] `lsb-release`
- [ ] `iproute2`
- [ ] `conntrack`
- [ ] `socat`
- [ ] `ethtool`

### 2.2 Nen co

- [ ] `wget`
- [ ] `vim` hoac `nano`
- [ ] `bash-completion`
- [ ] `net-tools`
- [ ] `telnet` hoac `nc`
- [ ] `kubectl` de debug neu can

## 3. Chuan bi he thong

- [ ] `sudo apt update`
- [ ] `sudo apt upgrade -y`
- [ ] Tat swap
- [ ] Bo swap khoi `/etc/fstab`
- [ ] Load kernel module `overlay`
- [ ] Load kernel module `br_netfilter`
- [ ] Bat `net.ipv4.ip_forward=1`
- [ ] Bat bridge netfilter sysctl
- [ ] Kiem tra gio he thong dong bo tot

## 4. Cai container runtime

- [ ] Cai `containerd`
- [ ] Cau hinh `containerd` dung `systemd cgroup`
- [ ] Khoi dong `containerd`
- [ ] Enable `containerd` luc boot
- [ ] Kiem tra `containerd` dang chay

## 5. Cai Kubernetes tools

- [ ] Cai `kubeadm`
- [ ] Cai `kubelet`
- [ ] Kiem tra version cac tool
- [ ] Khoa version neu can de dong bo voi cluster

## 6. Kiem tra network va tailnet

- [ ] Worker nhin thay master qua Tailscale
- [ ] Co IP no bo on dinh neu cluster dung LAN IP
- [ ] Kiem tra `tailscale status`
- [ ] Kiem tra `ping` toi master
- [ ] Kiem tra co the ket noi toi port `6443` cua master

## 7. Truoc khi join

- [ ] Da co lenh `kubeadm join` tu master
- [ ] Da kiem tra token con hieu luc
- [ ] Da kiem tra CA hash dung
- [ ] Da dam bao worker chua tung join nham cluster khac
- [ ] Da dam bao `kubelet` dang dung `containerd` lam runtime

## 8. Chay lenh join

- [ ] Chay `kubeadm join <master-ip>:6443 --token <token> --discovery-token-ca-cert-hash sha256:<hash>`
- [ ] Kiem tra lenh join khong bi loi
- [ ] Kiem tra `kubelet` da bat len tren worker
- [ ] Kiem tra node da xuat hien trong `kubectl get nodes` tren master

## 9. Sau khi join

- [ ] Worker hien `Ready`
- [ ] Worker co the schedule pod
- [ ] `kubectl get nodes -o wide` hien day du worker
- [ ] Kiem tra worker khong bi taint bat thuong

