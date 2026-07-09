#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Usage:
  k8s/deploy/sync-gitops-image-tag.sh <image-tag> [--env dev|staging|both] [--sampledata-tag <tag>]

Examples:
  k8s/deploy/sync-gitops-image-tag.sh 6f2c9a1 --env both
  k8s/deploy/sync-gitops-image-tag.sh 6f2c9a1 --env dev --sampledata-tag 16.3-alpine

Notes:
  - The same immutable image tag is applied to backend and ui overlays.
  - sampledata keeps its own image tag unless explicitly overridden.
EOF
}

if [[ $# -lt 1 ]]; then
  usage
  exit 1
fi

IMAGE_TAG="$1"
shift

TARGET_ENV="both"
SAMPLEDATA_TAG=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --env)
      TARGET_ENV="${2:-}"
      shift 2
      ;;
    --sampledata-tag)
      SAMPLEDATA_TAG="${2:-}"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "ERROR: Unknown argument: $1" >&2
      usage
      exit 1
      ;;
  esac
done

if ! command -v yq >/dev/null 2>&1; then
  echo "ERROR: yq is required but not found in PATH." >&2
  exit 1
fi

update_overlay() {
  local file="$1"
  echo "Updating $file -> backend/ui tag = $IMAGE_TAG"
  yq -i \
    ".backend.image.tag = \"${IMAGE_TAG}\" | .ui.image.tag = \"${IMAGE_TAG}\"" \
    "$file"

  if [[ -n "$SAMPLEDATA_TAG" ]]; then
    echo "Updating $file -> sampledata tag = $SAMPLEDATA_TAG"
    yq -i \
      ".sampledata.image.tag = \"${SAMPLEDATA_TAG}\"" \
      "$file"
  fi
}

case "$TARGET_ENV" in
  dev)
    update_overlay "k8s/charts/values-dev.yaml"
    ;;
  staging)
    update_overlay "k8s/charts/values-staging.yaml"
    ;;
  both)
    update_overlay "k8s/charts/values-dev.yaml"
    update_overlay "k8s/charts/values-staging.yaml"
    ;;
  *)
    echo "ERROR: --env must be dev, staging, or both (got: $TARGET_ENV)" >&2
    exit 1
    ;;
esac

echo "Done. Review git diff before committing."
