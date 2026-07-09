# Ubuntu Master YAS Next Actions

## Thu tu uu tien

1. On dinh `worker-1`
   - Khoanh host reboot/flap.
   - Chi tiep tuc khi node giu `Ready` on dinh.

2. Khoi phuc `worker-3`
   - Kiem tra kubelet, CNI, va ly do node bi `NotReady`.
   - Khong schedule app moi len node nay khi chua on dinh.

3. Rerun DNS/CoreDNS check
   - Xac nhan `CoreDNS` con `2/2 Ready`.
   - Test lai `identity.yas.local.com` va `postgresql.postgres.svc.cluster.local`.
   - Test them external DNS de chot upstream.

4. Rollout app lai
   - Re-deploy backend/frontend sau khi node + DNS on dinh.
   - Theo doi `CrashLoopBackOff`, `readiness`, `liveness`, `istiod`.

5. Don dep cuoi
   - Xoa pod test/debug con sot.
   - Cap nhat checklist/trang thai cho dung thuc te.

## Dieu kien bat dau buoc tiep

- `worker-1` va `worker-3` deu phai giu `Ready` on dinh.
- DNS noi bo va external phai resolve binh thuong.
- Chi sau do moi rollout app tiep.
