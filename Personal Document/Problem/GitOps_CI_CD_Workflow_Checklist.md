# GitOps / CI-CD Workflow Checklist

## Muc tieu

- Chuan hoa image tag theo commit SHA de ArgoCD co nguon chan ly co dinh.
- Giu `latest` chi la tag phu, khong phai tag chot final.
- Tach ro workflow build/push, workflow developer deploy, va workflow cleanup.

## Da chot

- `ci-pipeline.yml`:
  - build/push image theo `github.sha`
  - co the gan them `latest` khi push len `main`
  - phu hop lam pipeline chinh cho immutable tag
- Cac workflow service rieng:
  - da chuyen sang build/push theo `github.sha`
  - van gan them `latest` o `main` de giu tuong thich nguoc
- `developer-build.yml`:
  - deploy dev theo SHA cua branch duoc chon
  - dung cho test local/branch, khong phai final GitOps
- `developer-cleanup.yml`:
  - reset ve `latest` + `ClusterIP` + `Ingress`
  - la workflow tien ich dev, khong dung lam nguon chot GitOps
- `k8s/deploy/sync-gitops-image-tag.sh`:
  - cap nhat overlay dev/staging bang SHA tag
  - la helper de chot deploy final trong GitOps

## Checklist truoc khi chot GitOps

1. Kiem tra tat ca workflow service da push SHA tag.
2. Xac nhan khong con workflow nao chi push `latest` neu do la workflow build chinh.
3. Xac minh image tag dung voi commit hash trong report.
4. Dam bao values dev/staging pin tag co dinh khi test ArgoCD.
5. Dam bao CI/CD co the truyen SHA tag sang buoc deploy final.
6. Chi de `latest` o cac luong phu:
   - cleanup
   - rollback tam thoi
   - test local

## Trang thai hien tai

- Da chuan hoa hieu nhat cac workflow build/push service sang SHA tag.
- `latest` con ton tai nhu tag phu cho main va workflow cleanup.
- Co the dung ngay cho GitOps/ArgoCD khi pair voi tag immutable tu CI/CD va values da duoc pin tag do.

## File lien quan

- [Tu_ArgoCD_GitOps_Report.md](./Tu_ArgoCD_GitOps_Report.md)
- [Tu_ArgoCD_One_Page_Checklist.md](./Tu_ArgoCD_One_Page_Checklist.md)
- [Tu_Immediate_ArgoCD_Manifest_Plan.md](./Tu_Immediate_ArgoCD_Manifest_Plan.md)
