#!/usr/bin/env sh
set -eu

namespace="${1:-argocd}"

kubectl annotate application -n "$namespace" search-dev argocd.argoproj.io/refresh=hard --overwrite
kubectl annotate application -n "$namespace" search-staging argocd.argoproj.io/refresh=hard --overwrite

kubectl -n "$namespace" get application search-dev search-staging \
  -o custom-columns='NAME:.metadata.name,REVISION:.status.sync.revision,SYNC:.status.sync.status,HEALTH:.status.health.status,OP:.status.operationState.phase' \
  --no-headers
