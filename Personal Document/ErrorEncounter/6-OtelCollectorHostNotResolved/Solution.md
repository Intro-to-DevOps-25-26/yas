# Solution

## Approach
Either add the OpenTelemetry Collector service to the compose stack, or disable OTEL export for the local demo.

## Recommended for this deployment scope
Because observability is not required for the assignment core scope, the simplest fix is to disable telemetry export locally:
- set OTEL exporters to `none`, or
- remove the OpenTelemetry Java agent from the container environment for the local compose run.

## Alternative if telemetry is needed
- Add the `collector` service from the observability compose files.
- Make sure the DNS name `collector` resolves inside the compose network.
- Verify the collector ports match `OTEL_COLLECTOR_PORT_GRPC=5555` and `OTEL_COLLECTOR_PORT_HTTP=6666`.

## Verification
After the fix, container logs should no longer show `UnknownHostException: collector` or exporter timeout messages.
