# Ubuntu Master CoreDNS Debug Fix Report

Ngay: 2026-07-07

## 1. Trieu chung

- `coredns` ban dau khong on dinh, chi co `1/2 Ready`.
- Pod tren `master` khong goi duoc `ClusterIP` `10.96.0.1:443`.
- DNS noi bo trong cluster bi anh huong, lam cac service phu thuoc vao discovery de bi timeout.
- Test cho thay pod tren `master` khong di qua duoc duong service toi API server khi dung `kubernetes.default.svc.cluster.local`.

## 2. Nhan dang nguyen nhan

- Khong phai CoreDNS loi code.
- CoreDNS bi anh huong boi pod network tren `master`:
  - `Endpoints/default/kubernetes` ban dau tro ve `100.86.160.41` tangnet IP.
  - `kube-apiserver` tren `master` advertise sai dia chi, lam service `kubernetes` khong ra dung LAN IP.
  - `ufw` tren `master` dang `deny` incoming va `deny` routed/forwarded, chan traffic tu pod CIDR `10.244.0.0/16` vao API server local.
- Vi vay pod tren `master` khong cham duoc `10.96.0.1:443`, ke ca khi DNS pod da chay.

## 3. Buoc debug da lam

- Kiem tra `CoreDNS` pod state va discovery service.
- Test `nslookup kubernetes.default.svc.cluster.local 10.96.0.10`.
- Test `nc -vz 10.96.0.1 443` tu pod tren `master`.
- So sanh pod network giua `master` va worker.
- Kiem tra `kube-proxy` config, `Endpoints/default/kubernetes`, `kube-apiserver` manifest, `ufw`, `iptables`, `ss -lntp`.
- Xac nhan `master` host co listen `6443`, nhung pod network bi chan o host firewall.

## 4. Cach fix

- Doi `kube-apiserver` manifest tren `master`:
  - `--advertise-address=192.168.2.16`
  - probe host cung ve `192.168.2.16`
- De kubelet tu recreate mirror pod va cap nhat lai `Endpoints/default/kubernetes` ve `192.168.2.16`.
- Mo rule firewall:
  - `ufw allow from 10.244.0.0/16 to any port 6443 proto tcp`
- Test lai tu pod tren `master`:
  - `10.96.0.1:443` open
  - `192.168.2.16:6443` open
  - `100.86.160.41:6443` open

## 5. Ket qua sau fix

- `coredns` da dat `2/2 Ready`.
- `nslookup kubernetes.default.svc.cluster.local 10.96.0.10` tra ve `10.96.0.1`.
- Pod tren `master` da goi duoc API server va service `kubernetes` qua ClusterIP.
- CoreDNS khong con la diem nghen do pod network tren `master`.

## 6. Ghi chu

- Loi nay la loi host-level networking tren `master`, khong phai loi core DNS image hay deployment manifest.
- Neu doi IP LAN hoac tailnet sau nay, can kiem tra lai:
  - `kube-apiserver` advertise address
  - `Endpoints/default/kubernetes`
  - `ufw` rule cho `6443/tcp`
  - `kube-proxy` config
