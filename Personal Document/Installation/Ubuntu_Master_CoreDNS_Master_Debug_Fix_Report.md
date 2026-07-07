# Ubuntu Master CoreDNS Debug Fix Report

Ngay: 2026-07-07

## 1. Trieu chung

- `coredns` ban dau khong on dinh, chi co `1/2 Ready`.
- Pod tren `master` khong goi duoc `ClusterIP` `10.96.0.1:443`.
- DNS noi bo trong cluster bi anh huong, lam cac service phu thuoc vao discovery de bi timeout.
- Test cho thay pod tren `master` khong di qua duoc duong service toi API server khi dung `kubernetes.default.svc.cluster.local`.

## 2. Nhan dang nguyen nhan

- Khong phai CoreDNS loi code.
- Co 2 van de tach biet:
  - Thieu custom DNS record `identity.yas.local.com` trong CoreDNS Corefile.
  - CoreDNS rollout ban dau khong Ready vi mot so node (`worker-3`, `naul1-pc`) khong cham duoc API service `10.96.0.1:443` sau khi endpoint API server thay doi.
- Nguyen nhan ha tang:
  - `Endpoints/default/kubernetes` da duoc quay ve `100.86.160.41` de cac node worker truy cap API qua tailnet.
  - `ufw` tren `master` da mo `6443/tcp` cho `10.244.0.0/16`, de pod tren `master` van chay duoc.
  - `kube-proxy` tren `worker-3` va `naul1-pc` can duoc restart de cap nhat lai bang service/NAT.

## 3. Buoc debug da lam

- Kiem tra `CoreDNS` pod state va discovery service.
- Test `nslookup kubernetes.default.svc.cluster.local 10.96.0.10`.
- Test `nc -vz 10.96.0.1 443` tu pod tren `master`.
- Test `nc -vz 10.96.0.1 443` tren `worker-1`, `worker-3`, `naul1-pc`.
- So sanh pod network giua `master` va worker.
- Kiem tra `kube-proxy` config, `Endpoints/default/kubernetes`, `kube-apiserver` manifest, `ufw`, `iptables`, `ss -lntp`.
- Xac nhan `master` host co listen `6443`, nhung pod network bi chan o host firewall.

## 4. Cach fix

- Doi `kube-apiserver` manifest tren `master`:
  - quay ve `--advertise-address=100.86.160.41`
  - probe host cung ve `100.86.160.41`
- De kubelet tu recreate mirror pod va cap nhat lai `Endpoints/default/kubernetes` ve `100.86.160.41`.
- Mo rule firewall:
  - `ufw allow from 10.244.0.0/16 to any port 6443 proto tcp`
- Restart `kube-proxy` tren `worker-3` va `naul1-pc` de cap nhat lai service rules.
- Them custom record vao CoreDNS Corefile:
  - `10.98.44.199 identity.yas.local.com`
- Restart `coredns` deployment de load Corefile moi.
- Test lai:
  - `10.96.0.1:443` open tren worker nodes
  - `identity.yas.local.com` resolve ve `10.98.44.199`

## 5. Ket qua sau fix

- `coredns` da dat `2/2 Ready`.
- `nslookup kubernetes.default.svc.cluster.local 10.96.0.10` tra ve `10.96.0.1`.
- `nslookup identity.yas.local.com 10.96.0.10` tra ve `10.98.44.199`.
- Pod tren `worker-1`, `worker-3`, `naul1-pc` da goi duoc API service `10.96.0.1:443`.
- CoreDNS khong con la diem nghen DNS cluster nua.

## 6. Ghi chu

- Loi nay la loi host-level networking tren `master`, khong phai loi core DNS image hay deployment manifest.
- Neu doi IP LAN hoac tailnet sau nay, can kiem tra lai:
  - `kube-apiserver` advertise address
  - `Endpoints/default/kubernetes`
  - `ufw` rule cho `6443/tcp`
  - `kube-proxy` config
