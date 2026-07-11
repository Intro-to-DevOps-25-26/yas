#!/usr/bin/env sh
set -eu

argocd_ns="${1:-argocd}"
repo_root="$(CDPATH= cd -- "$(dirname "$0")/.." && pwd)"

git -C "$repo_root" fetch origin main --quiet
desired_rev="$(git -C "$repo_root" rev-parse origin/main)"

wait_for_app() {
  app_name="$1"
  app_ns="$2"
  timeout_seconds="${3:-600}"
  interval_seconds=5
  elapsed=0

  while [ "$elapsed" -lt "$timeout_seconds" ]; do
    current_rev="$(kubectl -n "$argocd_ns" get application "$app_name" -o jsonpath='{.status.sync.revision}' 2>/dev/null || true)"
    sync_status="$(kubectl -n "$argocd_ns" get application "$app_name" -o jsonpath='{.status.sync.status}' 2>/dev/null || true)"
    health_status="$(kubectl -n "$argocd_ns" get application "$app_name" -o jsonpath='{.status.health.status}' 2>/dev/null || true)"

    if [ "$current_rev" = "$desired_rev" ] && [ "$sync_status" = "Synced" ]; then
      printf '%s\t%s\t%s\t%s\n' "$app_name" "$current_rev" "$sync_status" "$health_status"
      kubectl -n "$app_ns" rollout status deploy/search --timeout=10m
      kubectl -n "$app_ns" get pods -l app.kubernetes.io/name=search -o wide
      return 0
    fi

    sleep "$interval_seconds"
    elapsed=$((elapsed + interval_seconds))
  done

  printf 'Timed out waiting for %s to reach revision %s\n' "$app_name" "$desired_rev" >&2
  kubectl -n "$argocd_ns" get application "$app_name" \
    -o custom-columns='NAME:.metadata.name,REVISION:.status.sync.revision,SYNC:.status.sync.status,HEALTH:.status.health.status,OP:.status.operationState.phase' \
    --no-headers
  return 1
}

refresh_app() {
  app_name="$1"
  kubectl annotate application -n "$argocd_ns" "$app_name" argocd.argoproj.io/refresh=hard --overwrite >/dev/null
}

printf 'Desired main revision: %s\n' "$desired_rev"

printf '\nRefreshing search-dev...\n'
refresh_app search-dev
wait_for_app search-dev yas-dev

printf '\nRefreshing search-staging...\n'
refresh_app search-staging
wait_for_app search-staging yas-staging

printf '\nFinal ArgoCD state:\n'
kubectl -n "$argocd_ns" get application search-dev search-staging \
  -o custom-columns='NAME:.metadata.name,REVISION:.status.sync.revision,SYNC:.status.sync.status,HEALTH:.status.health.status,OP:.status.operationState.phase' \
  --no-headers
