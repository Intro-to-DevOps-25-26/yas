# WORKLOAD CHECKLIST — YAS CI/CD (Simple)

> Mục tiêu: checklist đơn giản để tick tiến độ theo các task trong PROJECT_INSTRUCTION.md.

## Task 1 — Setup repo + CI nền

### 1.1 Fork & repo settings
- [x] Fork repository từ https://github.com/nashtech-garage/yas
- [x] Clone repo về local
- [x] Tạo branch `develop` từ `main`
- [x] Cấu hình rules/branch protection cho `main`:
  - [x] Require a pull request before merging
  - [x] Require 2 approvals before merging
  - [x] Require status checks to pass: `CI Gate`
  - [ ] Block force-push / delete branch (nếu đề yêu cầu)

### 1.2 Workflow CI ban đầu
- [x] Có workflow CI tập trung: `.github/workflows/ci-pipeline.yml`
- [x] Trigger cho push + pull_request
- [x] Permissions đủ cho artifacts + publish status

### 1.3 Monorepo build matrix
- [x] Xác định danh sách module theo `<modules>` trong root `pom.xml`
  - [x] common-library
  - [x] backoffice-bff
  - [x] cart
  - [x] customer
  - [x] inventory
  - [x] location
  - [x] media
  - [x] order
  - [x] payment-paypal
  - [x] payment
  - [x] product
  - [x] promotion
  - [x] rating
  - [x] search
  - [x] storefront-bff
  - [x] tax
  - [x] webhook
  - [x] sampledata
  - [x] recommendation
  - [x] delivery
- [x] Detect thay đổi và chỉ build/test module bị ảnh hưởng
- [ ] (Tuỳ chọn) `paths-ignore` / path filters cho docs-only

## Task 2 — Hoàn thiện CI (Test/Build/Artifacts/Coverage)

### 2.1 Chuẩn hoá test & build
- [x] Phase 1 (Test) chạy ổn (Maven): `mvn test` theo module
- [x] Upload test results (Surefire/Failsafe reports) làm artifact
- [x] Phase 2 (Build) chạy ổn (Maven): `mvn package` theo module (có upload build artifact)
- [ ] (Tuỳ chọn theo đề) Docker build/push image

### 2.2 Coverage (JaCoCo)
- [x] Bật JaCoCo để sinh coverage report (XML/HTML)
- [x] Upload coverage report làm artifact
- [x] Enforce coverage threshold ≥ 70% (hoặc theo đề)
- [x] Có Coverage Summary theo từng module (PASS/FAIL/NO DATA) trong job `Test Summary`
- [ ] (Tuỳ chọn) Coverage badge trên README

### 2.3 Test summary trong PR
- [x] Có PR-visible test summary (JUnit report)

Ghi chú trạng thái hiện tại:
- CI có thể fail do một số module coverage < 70% (xử lý ở Task 4 bằng cách viết thêm unit tests).

## Task 3 — Security / Quality gates

### 3.1 Gitleaks
- [ ] Thêm workflow scan secrets bằng Gitleaks
- [ ] Fail PR nếu phát hiện secret
- [ ] Có screenshot: secrets scan pass/fail + cấu hình (nếu có)

### 3.2 Sonar (SonarQube hoặc SonarCloud)
- [ ] Chọn nền tảng: SonarQube (self-host) hoặc SonarCloud (hosted)
- [ ] Cấu hình token/secret (`SONAR_TOKEN`)
- [ ] Sonar scan chạy được trên PR
- [ ] Quality Gate bật và chặn merge nếu fail
- [ ] Có screenshot: project settings + PR check

### 3.3 Snyk (optional)
- [ ] Cấu hình `SNYK_TOKEN`
- [ ] Snyk scan chạy được trên PR
- [ ] Có screenshot: PR check

## Task 4 — Unit tests theo service (nâng coverage)

### 4.1 Branch theo service
- [x] Tạo branch `feature/add-unit-tests-<service>` cho từng service được phân công

### 4.2 Viết unit tests
- [x] Thêm test cases vào `src/test/java/**`
- [x] Coverage đạt ≥ 70% cho service được phân công
- [ ] CI pass: `CI Gate` + coverage + security (nếu đã bật)

### 4.3 PR & review flow
- [ ] Tạo PR vào `develop`/`main` (theo quy ước nhóm)
- [ ] Có đủ 2 approvals
- [ ] Merge khi CI pass

## Task 5 — Tổng hợp + tối ưu + báo cáo

### 5.1 Path filter / tối ưu monorepo
- [ ] (Tuỳ chọn) Thêm path filters để giảm CI runs khi chỉ đổi docs
- [ ] (Tuỳ chọn) Tối ưu caching/reusable workflow

### 5.2 Badge / notification
- [ ] (Tuỳ chọn) CI badge trên README
- [ ] (Tuỳ chọn) Notification (Slack/Teams)

### 5.3 Báo cáo + tài liệu nộp bài
- [ ] Tổng hợp link PRs + workflow runs
- [ ] Chụp ảnh: rulesets/branch protection + checks + sonar/gitleaks/snyk
- [ ] Viết báo cáo cuối (mô tả kiến trúc CI/CD, quyết định kỹ thuật, cách chạy, minh chứng)
