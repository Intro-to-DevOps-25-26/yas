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
- Mình đã xác nhận `kube-proxy`/`ClusterIP` của API có thể hoạt động lại, nhưng pod network cross-node vẫn là điểm yếu cần tiếp tục xử lý.

## Việc cần sửa trên master
- Đã restart `kube-proxy` và `coredns`.
- Đã patch Flannel để bám `tailscale0` thay vì tự chọn interface node cũ.
- Cần tiếp tục kiểm tra Flannel/kube-proxy nếu còn pod thường không đi được sang node khác.
- Sau khi overlay ổn định hoàn toàn, rollout restart các backend pod còn crash.

## Việc cần làm ở các worker liên quan
- Xác nhận `tailscale0` có mặt và node có thể đi tới các node khác qua tailnet.
- Kiểm tra firewall / routing cho VXLAN `8472` hoặc chuyển toàn bộ node sang một đường IP thống nhất.
- Nếu một worker vẫn không đi được tới pod IP node khác thì cần sửa worker đó trước khi app ổn định.
- Sau khi overlay thông, restart lại các workload đang `CrashLoopBackOff` trên worker.
