# Problem

## Error log
- `curl -I -H "Host: api.yas.local" http://localhost/product/actuator/health`
- Response: `HTTP/1.1 500`
- Product container log:
  - `No static resource actuator/health for request '/product/actuator/health'.`

## Explanation
The `product` service is up, but the health probe we used is not exposed by this application in the current compose setup.
The request was routed to `/product/actuator/health`, and Spring returned a `NoResourceFoundException` because that path is not available.

## Root cause
- The service does not expose the actuator health endpoint in the current runtime configuration, or
- The test used the wrong path for this project.

## Impact
- The container is running, but a deployment health check against `/product/actuator/health` fails.
- If this path is later used as a Kubernetes readiness/liveness probe, the pod may be marked unhealthy even though the app is alive.
