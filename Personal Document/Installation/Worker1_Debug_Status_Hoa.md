# Worker-1 Debug Status Report — Hòa

**Date**: 2026-07-06
**Branch**: `feat/local-deploy`
**Node**: worker-1 (WSL2 Ubuntu 24.04, Tailscale IP `100.120.154.119`)

---

## 1. Node Recovery Status

| Item | Status |
|------|--------|
| `tailscaled` | ✅ active |
| `containerd` | ✅ active |
| `kubelet` | ✅ active |
| worker-1 node | ✅ Ready |
| Flannel | ✅ `FlannelIsUp` |
| MemoryPressure | ✅ None |
| DiskPressure | ✅ None |
| PIDPressure | ✅ None |
| KUBELET_EXTRA_ARGS | `--node-ip=100.120.154.119` |
| `/etc/hosts` | `100.86.160.41 master.tailb6a561.ts.net master` |
| API server connectivity | ✅ `nc master 6443` succeeded |

### Other nodes (at time of check)

| Node | Status |
|------|--------|
| tu-nguyen-inspiron-15-3567 (master) | ✅ Ready |
| worker-1 | ✅ Ready |
| naul1-pc (worker-2) | ❌ NotReady |
| worker-3 | ❌ NotReady |

---

## 2. Assigned Pods

| Pod | Namespace | Root Cause Analysis |
|-----|-----------|---------------------|
| keycloak-0 | keycloak | Operator-managed probes. Was CrashLoopBackOff/Terminating. Reached DB schema init then JGroups disconnect. Likely cross-node network instability, not config bug. |
| customer | yas | No startupProbe → livenessProbe kills pod before Spring Boot finishes starting |
| inventory | yas | Same as customer |
| product | yas | Same as customer |
| tax | yas | Same as customer |

---

## 3. Code Inspection Findings

### DNS/JDBC — All Correct ✅

All service DNS names in the repo are consistent and correct:

- `postgresql.postgres.svc.cluster.local:5432` — Keycloak, backend, sampledata
- `keycloak-service.keycloak.svc.cluster.local` — issuer-uri, auth-server-url
- `kafka-cluster-kafka-brokers.kafka:9092` — Kafka bootstrap
- `redis-master.redis:6379` — Redis

### Probes — Missing `startupProbe` ❌ (FIXED)

**Before**: Backend chart only had `livenessProbe` and `readinessProbe` with `periodSeconds=10, failureThreshold=12`. No `startupProbe`, no `initialDelaySeconds`. Effective timeout = 120s.

**Problem**: Spring Boot apps on WSL2+Tailscale can take 60-180s to start (JDBC pool, Keycloak JWT issuer resolution). Without startupProbe, livenessProbe kills pods during normal startup.

**Fix Applied**: Added `startupProbe` with `periodSeconds=10, failureThreshold=30` → 300s max startup window. This is the Kubernetes-recommended pattern.

### Keycloak Probes — Cannot Fix via Chart ⚠️

Keycloak operator (`k8s.keycloak.org/v2alpha1`) manages pod probes internally. The CR does not expose probe configuration. If Keycloak-0 keeps crashing, the root cause is likely:
1. Cross-node pod-to-pod network (Flannel VXLAN over Tailscale)
2. PostgreSQL pod unreachable from Keycloak pod
3. Operator reconciliation restarts

**Action needed**: Verify pod-to-pod connectivity from worker-1 to postgres pod node, then restart Keycloak operator/CR.

### Other Finding — Gateway Route Typo

`yas-configuration/values.yaml` line 158: `http://protion` should be `http://promotion`. Outside Hòa's scope — reported to team.

---

## 4. Changes Made

| File | Change |
|------|--------|
| `k8s/charts/backend/values.yaml` | Added `startupProbe` section (300s window) |
| `k8s/charts/backend/templates/deployment.yaml` | Added conditional `startupProbe` rendering |
| `Personal Document/Installation/Worker1_Debug_Status_Hoa.md` | This debug report |

---

## 5. Next Steps

1. **Commit & push** to `feat/local-deploy`
2. **Master (Tú)** pulls and runs:
   ```bash
   cd k8s/deploy
   ./redeploy-yas.sh config   # redeploy yas-configuration
   ./redeploy-yas.sh apps     # redeploy all apps
   ```
3. **Monitor** pod startup: `kubectl get pods -n yas -w`
4. **For Keycloak-0**: Need to verify cross-node connectivity first:
   ```bash
   # From worker-1, test connectivity to postgres pod
   kubectl exec -n yas <any-running-pod> -- nslookup postgresql.postgres.svc.cluster.local
   kubectl exec -n yas <any-running-pod> -- nc -zv postgresql.postgres.svc.cluster.local 5432
   ```
5. If naul1-pc and worker-3 remain NotReady, pods scheduled there will never start — coordinate with Luân and Khoa.
