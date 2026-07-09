# Ubuntu Master YAS Current Remaining Issues

Ngay: 2026-07-08

## 1. Tong Quan Hien Tai

- Cleanup va rollout da duoc chay lai.
- Node hien tai:
  - `worker-1`: `Ready`
  - `worker-3`: `Ready`
  - `naul1-pc`: `Ready,SchedulingDisabled`
- `backoffice-bff`, `backoffice-ui`, `swagger-ui` da duoc rerollout sang node khoe va da chay on dinh.
- `sampledata` da seed thu cong xong vao dung DB `product` va `media`.
- Con lai cac service core khac van can theo doi tiep neu muon chot cluster 100%.

## 2. Cac Nhom Con Loi

### Node / cluster

- `worker-1`: root cause cua dot NotReady truoc do la kubelet stopped posting node status trong luc reboot/flap.
- `worker-3`: root cause cua dot NotReady truoc do la reboot/flap node.
- `naul1-pc`: root cause cua dot NotReady truoc do la disk pressure / image GC failed / reboot.

### App / infra dang con loi

- Nhom con can theo doi neu tiep tuc rollout:
  - `cart`
  - `search`
  - `inventory`
  - cac service core khac neu co pod moi restart
- Cac pod test tam thoi da dung de verify DNS co the xoa sau khi khong can nua.

## 3. Da Lam Gi Roi

- Da force-delete nhieu pod cu / terminating de cluster tao lai pod moi.
- Da restart `kube-proxy` tren `worker-1` va `naul1-pc`.
- Da khoanh duoc log loi chinh:
  - `istiod.istio-system.svc`
  - `10.96.0.10:53`
  - `connection refused` / `i/o timeout`

## 4. Ket Luan Ngan

- Van de chinh truoc do la node / host instability va probe / mesh config cu, khong phai chi loi code app.
- Sau khi rerollout sang node khoe va doi probe phu hop, nhom `backoffice-bff`, `backoffice-ui`, `swagger-ui` da chay on dinh hon.
- `sampledata` khong con blocked vi seed data da nhap thanh cong vao dung DB.
- Van nen theo doi them cac service core khac neu muon chot cluster 100%.

## 5. Luu Y Tam Dung Istio

- Co the tam thoi scale down cac component `istio-system` neu can giam noise / giam tai khi debug node:
  - `istiod`
  - `istio-ingressgateway`
  - `istio-egressgateway`
  - `kiali`
  - `prometheus`
- Luu y:
  - `namespace yas` dang co label `istio-injection=enabled`, nen chi scale control plane chua du neu muon pod moi khong inject sidecar.
  - Trong thuc te, manifest live cua app dang co san `istio-init` va `istio-proxy` trong template, nen muon tat Istio hoan toan phai sua chart/manifests va redeploy lai app, khong chi scale `istio-system`.
- Tat Istio tam thoi co the lam mat mTLS / xDS / webhook / telemetry, nen chi dung khi dang khoanh vung loi ha tang.
