# Solution

## Approach
Use an endpoint that exists in the current project for verification, or enable actuator health if we want a real liveness/readiness probe.

## What worked for validation
- `curl -I -H "Host: api.yas.local" http://localhost/product/v3/api-docs` returned `200 OK`.
- `curl -I -H "Host: api.yas.local" http://localhost/product/` returned `401`, which confirms the service is reachable and protected by auth.

## Fix options
1. Quick validation only:
   - Use `/product/v3/api-docs` or another documented route to confirm the service is alive.
2. Proper health probe for Kubernetes:
   - Enable Spring Boot actuator in the service.
   - Expose `/actuator/health`.
   - Then point readiness/liveness probes to that endpoint.
3. If actuator is not required for this demo:
   - Do not use `/actuator/health` in deployment checks.
   - Keep the service validated through its public API routes instead.

## Recommendation
For the current demo stack, keep deployment validation on the documented API routes first, then add actuator health later when preparing Kubernetes probes.
