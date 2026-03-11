# fms-utm-integration-service

## Purpose
`fms-utm-integration-service` is the bridge service between DCS and UTM.

It acts as the integration boundary that translates internal DCS workflows into UTM-compatible interactions and receives responses, commands, and airspace updates back from UTM.

## Main responsibilities
- manage UTM session connect / disconnect / heartbeat
- register pilot and drone
- request and track flight approval
- report airborne / landing / completion / emergency
- forward telemetry to UTM
- receive UTM commands and relay them to DCS/GCS
- receive NFZ / corridor updates
- keep auditable records of external interactions

## Current status
This service started from a generic template skeleton and is being refactored into a domain-oriented integration service.

The current development approach is incremental and phase-based.

## Target business modules
- session
- registry
- approval
- mission
- telemetry
- command
- airspace
- infrastructure
- common

## Architecture intent
The service should not remain a simple CRUD service or thin proxy.
It should evolve into a stateful integration service with:

- explicit business states
- validated transitions
- isolated external adapters
- auditable external operations
- separation of telemetry flow and command flow
- production-friendly reliability patterns

## Core business rules
- flight approval must be `APPROVED` before a mission can be reported as `AIRBORNE`
- telemetry flow must not block critical command handling
- external UTM contracts should be wrapped by internal DTOs/adapters
- external interactions should be traceable and auditable

## Suggested package direction
```text
com.viettelpost.fms.utm_integration
├─ config
├─ common
├─ session
├─ registry
├─ approval
├─ mission
├─ telemetry
├─ command
├─ airspace
└─ infrastructure