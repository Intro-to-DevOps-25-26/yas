# Installation Plan - YAS Docker Compose

This document is the starting checklist for running YAS locally with Docker Compose.

## 1. Required Downloads / Installs

Install these first:

- Docker Desktop with Compose v2
- Git
- A browser for testing the app

Recommended machine capacity:

- RAM: 32 GB
- Free disk: 30 GB or more

## 2. Clone the Repository

```bash
git clone <repo-url>
cd yas
```

Keep the root `.env` file as-is because it already defines the service URLs and Compose behavior.

## 3. Service Addresses to Set Up

Add these entries to your hosts file:

```text
127.0.0.1 identity
127.0.0.1 api.yas.local
127.0.0.1 pgadmin.yas.local
127.0.0.1 storefront
127.0.0.1 backoffice
127.0.0.1 loki
127.0.0.1 tempo
127.0.0.1 grafana
127.0.0.1 elasticsearch
127.0.0.1 kafka
```

Windows hosts file path:

```text
C:\Windows\System32\drivers\etc\hosts
```

These names match the repo's documented URLs and internal service routing.

## 4. Services Included in Docker Compose

The repo is configured to run these Compose files together:

- `docker-compose.yml`
- `docker-compose.search.yml`
- `docker-compose.o11y.yml`

The root `.env` already sets:

```text
COMPOSE_FILE=docker-compose.yml:docker-compose.search.yml:docker-compose.o11y.yml
```

## 5. Start the System

Run the full stack:

```bash
docker compose up
```

If you want to start only the core services first:

```bash
docker compose -f docker-compose.yml up
```

## 6. Start Source Connectors

After containers are healthy, run:

```bash
./start-source-connectors.sh
```

This is needed for CDC/search synchronization.

## 7. URLs to Check

Open these in your browser:

- http://storefront/
- http://backoffice/
- http://api.yas.local/swagger-ui/
- http://pgadmin.yas.local/
- http://grafana/

Useful admin logins from the repo:

- Storefront / Backoffice: `admin` / `password`
- Keycloak admin: `admin` / `admin`

## 8. Suggested First-Run Order

1. Install Docker Desktop and Git.
2. Clone the repo.
3. Add the hosts entries.
4. Run `docker compose up`.
5. Wait for all containers to start.
6. Run `./start-source-connectors.sh`.
7. Open the storefront and backoffice URLs.

## 9. Troubleshooting Notes

- The repo warns that full Compose needs at least 16 GB RAM.
- If storefront or backoffice behave oddly on first start, stop the stack and run `docker compose up` again.
- `sampledata` is meant to seed data once, then can be turned off.

## 10. Stop the Stack

```bash
docker compose down
```

