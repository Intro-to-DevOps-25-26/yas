# Problem

## Error log
Repeated logs from multiple services such as `product` and `sampledata` show:
- `Failed to export logs. The request could not be executed. Error message: timeout`
- `java.net.UnknownHostException: collector: Name does not resolve`

## Explanation
The application is configured to export OpenTelemetry data to a host named `collector`, but that host is not available in the current Docker Compose stack.
The DNS lookup fails inside the container, so telemetry export retries and times out.

## Root cause
- `.env` points OTEL to `collector`.
- The compose run used for this deployment does not include an OpenTelemetry Collector container.
- The app still starts, but telemetry export fails in the background.

## Impact
- The business services are still reachable.
- Logs are noisy and can hide real issues.
- Telemetry data is not exported.
