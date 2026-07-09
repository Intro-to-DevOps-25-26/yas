# Ubuntu Worker Tailnet Join Quick Checklist

Checklist ngan gon cho worker truoc khi join cluster qua Tailnet.

## 1. Truoc khi join

- [ ] Ubuntu Server LTS da cai
- [ ] Hostname dat ro rang
- [ ] `sudo` quyen admin
- [ ] `Tailscale` da cai
- [ ] Worker da join cung tailnet voi master
- [ ] `containerd` da cai va dang chay
- [ ] `kubeadm` da cai
- [ ] `kubelet` da cai
- [ ] Swap da tat
- [ ] `overlay` da load
- [ ] `br_netfilter` da load
- [ ] `net.ipv4.ip_forward=1`

## 2. Kiem tra ket noi toi master

- [ ] Ping duoc `master.tailb6a561.ts.net`
- [ ] `nc -vz master.tailb6a561.ts.net 6443` thanh cong
- [ ] Khong bi firewall chan port `6443`

## 3. Join cluster

- [ ] Lay lenh join moi nhat tu master
- [ ] Chay `sudo kubeadm join master.tailb6a561.ts.net:6443 --token <token> --discovery-token-ca-cert-hash sha256:<hash>`
- [ ] Kiem tra khong bi timeout
- [ ] Kiem tra khong bi loi TLS / x509

## 4. Sau khi join

- [ ] Node xuat hien tren master
- [ ] Node sang `Ready`
- [ ] Kiem tra `kubectl get nodes -o wide` tren master

