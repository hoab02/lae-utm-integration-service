# Phase 8 - Add observability and security hardening

## Goal
Harden the service so it is easier to operate, debug, and secure in production-like environments.

## Scope
Add the most important observability and security improvements needed by the current architecture.

## Expected capabilities

### Observability
- structured logging for important flows
- correlation identifiers such as traceId, missionId, planId, commandId where relevant
- metrics direction for:
    - approval latency
    - command acknowledgement latency
    - telemetry forwarding throughput
    - session heartbeat failures
- clear error logging for UTM interaction boundaries

### Security
- protect internal endpoints according to current project approach
- improve secret handling direction for tokens/credentials
- keep external interaction auditable
- prepare for stronger verification of inbound UTM commands if required later

## Suggested components
- logging improvements in controllers/services/clients
- metrics instrumentation in key flows
- security config refinement where needed
- audit enrichment for important external actions

## Rules
- logs should help explain business events, not only stack traces
- critical flows should be traceable across modules
- security changes should not silently break current development flow
- credentials and tokens should avoid unsafe handling patterns

## Constraints
- keep changes incremental
- do not redesign the entire security model unless required
- prefer practical operational visibility over abstract instrumentation
- avoid noisy or low-value logs

## Done when
- important business flows are easier to trace
- key UTM interactions emit useful structured logs/metrics
- security posture is improved without derailing development
- service still builds and remains understandable