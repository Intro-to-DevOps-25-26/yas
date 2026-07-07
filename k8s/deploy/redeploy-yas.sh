#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
K8S_DIR="${ROOT_DIR}"

usage() {
  cat <<'EOF'
Usage: ./redeploy-yas.sh [all|infra|config|apps|status] [--skip-pull]

Modes:
  all     Pull latest code, then deploy infra, config, and apps.
  infra   Deploy infra only: keycloak, redis, shared cluster services.
  config  Deploy yas-configuration only.
  apps    Deploy YAS applications only.
  status  Show basic cluster status and exit.

Options:
  --skip-pull  Do not run git pull before deploy.
EOF
}

MODE="all"
SKIP_PULL="false"

for arg in "${@:-}"; do
  case "$arg" in
    all|infra|config|apps|status)
      MODE="$arg"
      ;;
    --skip-pull)
      SKIP_PULL="true"
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown argument: $arg" >&2
      usage >&2
      exit 1
      ;;
  esac
done

cd "$K8S_DIR"

if [[ "$SKIP_PULL" != "true" ]]; then
  git pull --ff-only
fi

show_status() {
  kubectl get nodes -o wide
  kubectl get pods -A -o wide
}

deploy_infra() {
  bash "$K8S_DIR/setup-keycloak.sh"
  bash "$K8S_DIR/setup-redis.sh"
  bash "$K8S_DIR/setup-cluster.sh"
}

deploy_config() {
  bash "$K8S_DIR/deploy-yas-configuration.sh"
}

deploy_apps() {
  bash "$K8S_DIR/deploy-yas-applications.sh"
}

case "$MODE" in
  status)
    show_status
    ;;
  infra)
    deploy_infra
    ;;
  config)
    deploy_config
    ;;
  apps)
    deploy_apps
    ;;
  all)
    deploy_infra
    deploy_config
    deploy_apps
    ;;
esac

show_status
