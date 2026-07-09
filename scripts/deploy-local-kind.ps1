param(
  [switch]$IncludeObservability
)

$ErrorActionPreference = 'Stop'

function Find-Tool {
  param(
    [Parameter(Mandatory = $true)][string]$Filter
  )
  $root = Join-Path $env:LOCALAPPDATA 'Microsoft\WinGet\Packages'
  $tool = Get-ChildItem -Path $root -Recurse -Filter $Filter -ErrorAction SilentlyContinue | Select-Object -First 1
  if (-not $tool) {
    throw "Could not find tool matching $Filter under $root"
  }
  return $tool.FullName
}

function Ensure-Namespace {
  param([Parameter(Mandatory = $true)][string]$Name)
  $previousErrorActionPreference = $ErrorActionPreference
  $ErrorActionPreference = 'SilentlyContinue'
  & $Kubectl get namespace $Name *> $null
  $exists = ($LASTEXITCODE -eq 0)
  $ErrorActionPreference = $previousErrorActionPreference
  if (-not $exists) {
    & $Kubectl create namespace $Name | Out-Host
  }
}

function Helm-Repo-Add {
  param(
    [Parameter(Mandatory = $true)][string]$Name,
    [Parameter(Mandatory = $true)][string]$Url
  )
  & $Helm repo add $Name $Url | Out-Host
}

function Helm-UpgradeInstall {
  param(
    [Parameter(Mandatory = $true)][string]$Release,
    [Parameter(Mandatory = $true)][string]$Chart,
    [string[]]$ExtraArgs = @()
  )
  $args = @('upgrade', '--install', $Release, $Chart, '--wait', '--timeout', '15m')
  $args += $ExtraArgs
  & $Helm @args | Out-Host
}

function Helm-Dependency-Build {
  param([Parameter(Mandatory = $true)][string]$ChartDir)
  & $Helm dependency build $ChartDir | Out-Host
}

function Wait-For-Crd {
  param([Parameter(Mandatory = $true)][string]$Name)
  & $Kubectl wait --for=condition=Established "crd/$Name" --timeout=180s | Out-Host
}

function Patch-CoreDNSIdentityHost {
  param([Parameter(Mandatory = $true)][string]$KeycloakServiceIp)

  $corefile = @"
.:53 {
    errors
    health {
       lameduck 5s
    }
    ready
    hosts {
       $KeycloakServiceIp identity.yas.local.com
       fallthrough
    }
    kubernetes cluster.local in-addr.arpa ip6.arpa {
       pods insecure
       fallthrough in-addr.arpa ip6.arpa
       ttl 30
    }
    prometheus :9153
    forward . /etc/resolv.conf {
       max_concurrent 1000
    }
    cache 30 {
       disable success cluster.local
       disable denial cluster.local
    }
    loop
    reload
    loadbalance
}
"@

  $patch = @{
    data = @{
      Corefile = $corefile
    }
  } | ConvertTo-Json -Depth 10

  & $Kubectl patch configmap coredns -n kube-system --type merge --patch $patch | Out-Host
  & $Kubectl rollout restart deployment/coredns -n kube-system | Out-Host
  & $Kubectl rollout status deployment/coredns -n kube-system --timeout=120s | Out-Host
}

$RepoRoot = Split-Path -Parent $PSScriptRoot
$DeployDir = Join-Path $RepoRoot 'k8s\deploy'
$ChartDir = Join-Path $RepoRoot 'k8s\charts'
$ClusterConfig = Join-Path $DeployDir 'cluster-config.yaml'

$Kubectl = 'C:\Program Files\Docker\Docker\resources\bin\kubectl.exe'
$Helm = Find-Tool -Filter 'helm.exe'
$Yq = Find-Tool -Filter 'yq.exe'

Write-Host "Using kubectl: $Kubectl"
Write-Host "Using helm:    $Helm"
Write-Host "Using yq:      $Yq"

Set-Location $DeployDir

$Domain = (& $Yq -r '.domain' $ClusterConfig).Trim()
$PostgresUsername = (& $Yq -r '.postgresql.username' $ClusterConfig).Trim()
$PostgresPassword = (& $Yq -r '.postgresql.password' $ClusterConfig).Trim()
$RedisPassword = (& $Yq -r '.redis.password' $ClusterConfig).Trim()
$KeycloakAdminUser = (& $Yq -r '.keycloak.bootstrapAdmin.username' $ClusterConfig).Trim()
$KeycloakAdminPassword = (& $Yq -r '.keycloak.bootstrapAdmin.password' $ClusterConfig).Trim()
$KeycloakBackofficeRedirectUrl = (& $Yq -r '.keycloak.backofficeRedirectUrl' $ClusterConfig).Trim()
$KeycloakStorefrontRedirectUrl = (& $Yq -r '.keycloak.storefrontRedirectUrl' $ClusterConfig).Trim()

Write-Host "Cluster domain: $Domain"

Ensure-Namespace -Name 'postgres'
Ensure-Namespace -Name 'redis'
Ensure-Namespace -Name 'keycloak'
Ensure-Namespace -Name 'kafka'
Ensure-Namespace -Name 'elasticsearch'
Ensure-Namespace -Name 'yas'

Helm-Repo-Add -Name 'postgres-operator-charts' -Url 'https://opensource.zalando.com/postgres-operator/charts/postgres-operator'
Helm-Repo-Add -Name 'strimzi' -Url 'https://strimzi.io/charts/'
Helm-Repo-Add -Name 'elastic' -Url 'https://helm.elastic.co'
Helm-Repo-Add -Name 'bitnami' -Url 'https://charts.bitnami.com/bitnami'
Helm-Repo-Add -Name 'stakater' -Url 'https://stakater.github.io/stakater-charts'
Helm-Repo-Add -Name 'akhq' -Url 'https://akhq.io/'
Helm-Repo-Add -Name 'grafana' -Url 'https://grafana.github.io/helm-charts'
Helm-Repo-Add -Name 'prometheus-community' -Url 'https://prometheus-community.github.io/helm-charts'
Helm-Repo-Add -Name 'open-telemetry' -Url 'https://open-telemetry.github.io/opentelemetry-helm-charts'
Helm-Repo-Add -Name 'jetstack' -Url 'https://charts.jetstack.io'
& $Helm repo update | Out-Host

Write-Host "Installing Postgres operator and cluster..."
Helm-UpgradeInstall -Release 'postgres-operator' -Chart 'postgres-operator-charts/postgres-operator' -ExtraArgs @('--namespace', 'postgres', '--create-namespace')
Helm-UpgradeInstall -Release 'postgres' -Chart (Join-Path $DeployDir 'postgres\postgresql') -ExtraArgs @('--namespace', 'postgres', '--create-namespace', '--set', "replicas=1", '--set', "username=$PostgresUsername", '--set', "password=$PostgresPassword")

Write-Host "Installing Redis..."
Helm-UpgradeInstall -Release 'redis' -Chart 'oci://registry-1.docker.io/bitnamicharts/redis' -ExtraArgs @('--namespace', 'redis', '--create-namespace', '--set', "auth.password=$RedisPassword")

Write-Host "Installing Keycloak CRDs and operator..."
& $Kubectl apply -f 'https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloaks.k8s.keycloak.org-v1.yml' | Out-Host
& $Kubectl apply -f 'https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/keycloakrealmimports.k8s.keycloak.org-v1.yml' | Out-Host
& $Kubectl apply -f 'https://raw.githubusercontent.com/keycloak/keycloak-k8s-resources/26.0.2/kubernetes/kubernetes.yml' -n keycloak | Out-Host
Helm-UpgradeInstall -Release 'keycloak' -Chart (Join-Path $DeployDir 'keycloak\keycloak') -ExtraArgs @('--namespace', 'keycloak', '--create-namespace', '--set', "hostname=identity.$Domain", '--set', "postgresql.username=$PostgresUsername", '--set', "postgresql.password=$PostgresPassword", '--set', "bootstrapAdmin.username=$KeycloakAdminUser", '--set', "bootstrapAdmin.password=$KeycloakAdminPassword", '--set', "backofficeRedirectUrl=$KeycloakBackofficeRedirectUrl", '--set', "storefrontRedirectUrl=$KeycloakStorefrontRedirectUrl")

$KeycloakServiceIp = (& $Kubectl get svc -n keycloak keycloak-service -o jsonpath='{.spec.clusterIP}').Trim()
Patch-CoreDNSIdentityHost -KeycloakServiceIp $KeycloakServiceIp

Write-Host "Installing Kafka operator and cluster..."
Helm-UpgradeInstall -Release 'kafka-operator' -Chart 'strimzi/strimzi-kafka-operator' -ExtraArgs @('--namespace', 'kafka', '--create-namespace')
Wait-For-Crd -Name 'kafkas.kafka.strimzi.io'
Wait-For-Crd -Name 'kafkaconnects.kafka.strimzi.io'
Wait-For-Crd -Name 'kafkaconnectors.kafka.strimzi.io'
Helm-UpgradeInstall -Release 'kafka-cluster' -Chart (Join-Path $DeployDir 'kafka\kafka-cluster') -ExtraArgs @('--namespace', 'kafka', '--create-namespace', '--set', 'kafka.replicas=1', '--set', 'zookeeper.replicas=1', '--set', "postgresql.username=$PostgresUsername", '--set', "postgresql.password=$PostgresPassword")

Write-Host "Installing Elasticsearch operator and cluster..."
Helm-UpgradeInstall -Release 'elastic-operator' -Chart 'elastic/eck-operator' -ExtraArgs @('--namespace', 'elasticsearch', '--create-namespace')
if ((& $Helm list -n elasticsearch -q) -contains 'elasticsearch-cluster') {
  Write-Host "Elasticsearch cluster already exists; skipping reinstall to avoid ECK conflict."
} else {
  Helm-UpgradeInstall -Release 'elasticsearch-cluster' -Chart (Join-Path $DeployDir 'elasticsearch\elasticsearch-cluster') -ExtraArgs @('--namespace', 'elasticsearch', '--create-namespace', '--set', 'elasticsearch.replicas=1', '--set', "kibana.ingress.hostname=kibana.$Domain")
}

if ($IncludeObservability) {
  Write-Host "Installing observability stack..."
  Helm-UpgradeInstall -Release 'loki' -Chart 'grafana/loki' -ExtraArgs @('--namespace', 'observability', '--create-namespace', '-f', (Join-Path $DeployDir 'observability\loki.values.yaml'))
  Helm-UpgradeInstall -Release 'tempo' -Chart 'grafana/tempo' -ExtraArgs @('--namespace', 'observability', '--create-namespace', '-f', (Join-Path $DeployDir 'observability\tempo.values.yaml'))
  Helm-UpgradeInstall -Release 'cert-manager' -Chart 'jetstack/cert-manager' -ExtraArgs @('--namespace', 'cert-manager', '--create-namespace', '--version', 'v1.12.0', '--set', 'installCRDs=true', '--set', 'prometheus.enabled=false', '--set', 'webhook.timeoutSeconds=4', '--set', 'admissionWebhooks.certManager.create=true')
  Helm-UpgradeInstall -Release 'opentelemetry-operator' -Chart 'open-telemetry/opentelemetry-operator' -ExtraArgs @('--namespace', 'observability', '--create-namespace')
  Helm-UpgradeInstall -Release 'opentelemetry-collector' -Chart (Join-Path $DeployDir 'observability\opentelemetry') -ExtraArgs @('--namespace', 'observability', '--create-namespace')
}

Write-Host "Installing shared YAS configuration..."
Helm-Dependency-Build -ChartDir (Join-Path $ChartDir 'yas-configuration')
Helm-UpgradeInstall -Release 'yas-configuration' -Chart (Join-Path $ChartDir 'yas-configuration') -ExtraArgs @('--namespace', 'yas', '--create-namespace')

Write-Host "Installing application charts..."
$apps = @(
  'backoffice-bff',
  'backoffice-ui',
  'storefront-bff',
  'storefront-ui',
  'swagger-ui',
  'cart',
  'customer',
  'inventory',
  'media',
  'order',
  'product',
  'search',
  'tax',
  'sampledata'
)

$backendApps = @(
  'backoffice-bff',
  'storefront-bff',
  'cart',
  'customer',
  'inventory',
  'media',
  'order',
  'product',
  'search',
  'tax',
  'sampledata'
)

foreach ($app in $apps) {
  $appChartDir = Join-Path $ChartDir $app
  Helm-Dependency-Build -ChartDir $appChartDir
  $extraArgs = @('--namespace', 'yas', '--create-namespace')
  if ($backendApps -contains $app) {
    $extraArgs += @('--set', 'backend.serviceMonitor.enabled=false')
  }
  Helm-UpgradeInstall -Release $app -Chart $appChartDir -ExtraArgs $extraArgs
}

Write-Host "Waiting for workloads..."
& $Kubectl get pods -A | Out-Host

Write-Host "Deployment steps finished."
