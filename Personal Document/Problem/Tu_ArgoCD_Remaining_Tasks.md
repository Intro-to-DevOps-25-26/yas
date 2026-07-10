# Tu ArgoCD Remaining Tasks

## Muc tieu

- Hoan tat phan GitOps / ArgoCD theo trang thai thuc te cua repo va cluster.
- Chi chot final khi co immutable tag that va ArgoCD controller / CRD that.

## Con lai phai lam

### 1. Chot immutable tag deploy

- CI/CD phai build image tu `github.sha`.
- CI/CD phai push image len registry voi tag SHA do.
- GitOps values phai tham chieu dung tag SHA nay.

### 2. Dau vao deploy final

- Cap nhat `values-dev.yaml` va `values-staging.yaml` bang tag SHA that.
- Neu can, tao PR/commit GitOps rieng de pin tag.
- Kiem tra lai image ref trong chart truoc khi sync.
- Dung helper `k8s/deploy/sync-gitops-image-tag.sh` de cap nhat overlay cho dev/staging.

### 3. ArgoCD that

- Da cai ArgoCD controller len cluster.
- Da cai CRD `Application` va `AppProject`.
- Can apply `AppProject` va `Application` / `ApplicationSet` that cho yas.

### 4. Sync / rollback that

- Sync thu cong app dau tien.
- Kiem tra `Synced` va `Healthy`.
- Test rollback theo revision.
- Ghi lai ket qua, revision, image tag, namespace.

### 5. Bang chung / report

- Chup screenshot ArgoCD app/project.
- Chup screenshot diff / sync / rollback.
- Cap nhat report final voi bang chung thuc te.

## Dieu kien de chuyen sang buoc tiep theo

- Tag SHA da co trong registry.
- Values dev/staging da pin tag do.
- Chart/manifest va probe da on dinh.
- ArgoCD server da chay on dinh trong namespace `argocd`.
