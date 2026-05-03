# PHÂN CÔNG CÔNG VIỆC — YAS CI/CD (Nhóm 4)

Repo làm bài: `PeriodicallyZoneOut/yas`

> Lưu ý PR/branch:
> - Tạo PR trong **đúng repo** `PeriodicallyZoneOut/yas` (không phải upstream `nashtech-garage/yas`).
> - Target branch theo quy ước nhóm: **`dev`**
> - Branch đặt tên theo module/service: `feature/add-unit-tests-<module>`.
> - Tất cả task đều checkout branch mới từ branch **`dev`**

## Snapshot coverage hiện tại (threshold 70% LINE)

PASS:
- cart, location, payment-paypal, promotion, rating, recommendation, search

NO DATA (chưa có tests/coverage):
- backoffice-bff, delivery, sampledata, storefront-bff

FAIL (coverage < 70%):
- common-library, customer, inventory, media, order, payment, product, tax, webhook

Mục tiêu cho các module FAIL: đạt ≥ 70% (LINE). Bảng dưới đây là **ước lượng số dòng cần cover thêm** để chạm ngưỡng (không phải số test cases).

| Module | Hiện tại | Cần cover thêm (ước lượng) |
|---|---:|---:|
| common-library | 54.46% (55/101) | +16 lines |
| customer | 69.17% (175/253) | +3 lines |
| inventory | 40.12% (138/344) | +103 lines |
| media | 51.28% (80/156) | +30 lines |
| order | 39.65% (291/734) | +223 lines |
| payment | 60.88% (235/386) | +36 lines |
| product | 35.84% (611/1705) | +583 lines |
| tax | 7.07% (14/198) | +125 lines |
| webhook | 34.43% (73/212) | +76 lines |

---

## Thành viên 2 — Task 3 (Security/Quality): Gitleaks + SonarCloud + Snyk (kèm evidence)

### Mục tiêu
- Thêm các security/quality checks theo yêu cầu nâng cao 7.c: **Gitleaks**, **Sonar (SonarCloud)**, **Snyk**.
- Các check phải chạy được trên PR và có bằng chứng (screenshot) khi nộp bài.

### 3.1 Gitleaks (secrets scanning)
Việc cần làm:
- Thêm workflow Gitleaks chạy trên `pull_request` vào `main` và `dev`.
- Fail PR nếu phát hiện secret.

Evidence cần chụp:
- Ảnh PR Checks có job Gitleaks.
- (Tuỳ chọn) Ảnh log minh hoạ 1 rule detect (không commit secret thật).

### 3.2 SonarCloud (code quality + Quality Gate)
Việc cần làm:
- Tạo/đăng nhập SonarCloud, import repo `PeriodicallyZoneOut/yas` (organization tuỳ nhóm).
- Tạo token và lưu vào GitHub Secrets: `SONAR_TOKEN`.
- Thêm workflow scan SonarCloud, chạy trên `pull_request` vào `main` và `dev`.
- Bật Quality Gate và chứng minh nó hoạt động (PR fail nếu gate fail).

Evidence cần chụp:
- Ảnh SonarCloud project settings + organization.
- Ảnh màn hình tạo token (che token) + GitHub Secrets đã set.
- Ảnh PR Checks có job SonarCloud chạy và link sang SonarCloud.
- Ảnh Quality Gate status (pass/fail).

### 3.3 Snyk (dependency vulnerabilities)
Việc cần làm:
- Tạo token và lưu GitHub Secrets: `SNYK_TOKEN`.
- Thêm workflow Snyk scan (Maven) chạy trên `pull_request` vào `main` và `dev`.
- Cấu hình mức severity threshold theo yêu cầu (ví dụ High).

Evidence cần chụp:
- Ảnh GitHub Secrets đã set `SNYK_TOKEN`.
- Ảnh PR Checks có job Snyk.

---

## Thành viên 3 — Task 4 (Unit tests + coverage) — Nhóm A

### Modules phụ trách
- `product` (module lớn)
- `customer` (gần đạt 70%, ưu tiên làm nhanh để có 1 PR pass)
- `common-library`

### Checklist cho mỗi module
- Tạo branch: `feature/add-unit-tests-<module>`
- Thêm unit tests vào: `<module>/src/test/java/**`
- Chạy local (bằng Maven Wrapper của module):
  - `./mvnw -B -ntp -f ../pom.xml -pl <module> -am test`
  - `./mvnw -B -ntp -f ../pom.xml -pl <module> jacoco:report`
- Mở PR vào `dev` và chờ CI pass (`CI Gate`).

### Gợi ý chiến lược test
- Ưu tiên service layer / mapper / util (ít phụ thuộc DB).
- Dùng Mockito để mock dependency.
- Tránh test Integration (IT) nếu mục tiêu là nâng coverage nhanh.

---

## Thành viên 4 — Task 4 (Unit tests + coverage) — Nhóm B

### Modules phụ trách
- `order`
- `inventory`
- `payment`
- `media`
- `tax`
- `webhook`

### Checklist cho mỗi module
- Tạo branch: `feature/add-unit-tests-<module>`
- Thêm unit tests vào: `<module>/src/test/java/**`
- Chạy local (Maven Wrapper):
  - `./mvnw -B -ntp -f ../pom.xml -pl <module> -am test`
  - `./mvnw -B -ntp -f ../pom.xml -pl <module> jacoco:report`
- PR vào `dev` (đúng repo `PeriodicallyZoneOut/yas`).

### Gợi ý ưu tiên (để pass nhanh)
- Làm `payment`/`media` trước (cần cover thêm ít hơn so với `order`/`inventory`).
- `tax` đang rất thấp → cần thêm nhiều unit tests cơ bản (service/util/controller nếu có).

---

## Gợi ý quy trình PR (áp dụng cho cả nhóm)
- 1 PR = 1 module để dễ review và dễ pass CI.
- PR title rõ ràng: `test(<module>): add unit tests to reach 70% coverage`.
- Khi tạo PR:
  - Base repo: `PeriodicallyZoneOut/yas`
  - Base branch: `dev` (hoặc `dev` theo nhóm)
  - Compare branch: `feature/add-unit-tests-<module>`
