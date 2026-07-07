# Ubuntu Master YAS Pod Root Cause

## Triệu chứng
- `coredns`, Keycloak, Elasticsearch, Debezium, Redis và nhiều pod app rơi vào `CrashLoopBackOff` hoặc không sẵn sàng.
- Pod thường không chạm ổn định được `ClusterIP` và DNS nội bộ.
- Test cho thấy pod thường không đi xuyên được overlay sang pod IP ở node khác.
- HostNetwork pod trên `worker-1` vẫn chạm được API server qua `10.96.0.1:443`, nên lỗi không nằm ở API server bản thân.

## Nguyên nhân gốc
- Lỗi gốc là đường đi pod-to-pod giữa các node không thông, nhất là VXLAN/Flannel trên các node có `InternalIP` khác mạng nhau.
- Từ `worker-1`, kết nối tới UDP `8472` của `master`, `naul1-pc` và `worker-3` đều timeout.
- Vì overlay không thông, pod thường không tới được pod IP của CoreDNS ở node khác, nên DNS nội bộ và service discovery chết dây chuyền.
- Riêng trên `master`, pod network còn bị chặn ở lớp host firewall: `ufw` để mặc định `deny` incoming/forwarding nên traffic từ pod CIDR không chạm được endpoint local của API server.
- `Endpoints/default/kubernetes` ban đầu trỏ tới `100.86.160.41` (Tailscale IP) nên pod trên `master` càng không chạm được service `10.96.0.1`.
- Sau khi đổi `kube-apiserver` sang advertise IP `192.168.2.16` và mở `ufw allow from 10.244.0.0/16 to any port 6443 proto tcp`, pod trên `master` đã chạm được `10.96.0.1:443`.

## Việc cần sửa trên master
- Đã restart `kube-proxy` và `coredns`.
- Đã patch Flannel để bám `tailscale0` thay vì tự chọn interface node cũ.
- CoreDNS replica chạy trên `master` đã bị loại bỏ khỏi scheduling để tránh node có pod-network lỗi.
- Đã đổi `kube-apiserver` advertise address sang `192.168.2.16` để `Endpoints/default/kubernetes` trỏ về LAN IP đúng.
- Đã mở `ufw allow from 10.244.0.0/16 to any port 6443 proto tcp` để pod trên master đi được vào API server local.
- Master pod network và `10.96.0.1:443` đã thông; phần còn lại là rollout lại workload nếu cần.

## Việc cần làm ở các worker liên quan
- Xác nhận `tailscale0` có mặt và node có thể đi tới các node khác qua tailnet.
- Kiểm tra firewall / routing cho VXLAN `8472` hoặc chuyển toàn bộ node sang một đường IP thống nhất.
- Nếu một worker vẫn không đi được tới pod IP node khác thì cần sửa worker đó trước khi app ổn định.
- Sau khi overlay thông, restart lại các workload đang `CrashLoopBackOff` trên worker.

## Ket Qua Moi Nhat

- `coredns` da duoc pin chay tren `worker-1` va `naul1-pc`, khong con pod CoreDNS tren `master`.
- `coredns` da dat `2/2 Ready`.
- Test tu pod tren `worker-1`:
  - `nslookup kubernetes.default.svc.cluster.local 10.96.0.10` tra ve `10.96.0.1`
  - `nc -vz 10.96.0.1 443` thanh cong
- Nguon loi goc da khoanh duoc va da sua:
  - pod network tren `master` bi UFW chan khi di vao endpoint local cua API server
  - `Endpoints/default/kubernetes` da duoc cap nhat ve `192.168.2.16`
  - sau khi mo rule cho port `6443` tu pod CIDR, pod tren `master` da chạm duoc `10.96.0.1:443`, `192.168.2.16:6443`, va `100.86.160.41:6443`
