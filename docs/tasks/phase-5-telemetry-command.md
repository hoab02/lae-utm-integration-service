# Phase 5 - Build telemetry and command bridge

## Goal
Implement the first version of the realtime bridge between DCS and UTM.

This phase covers:
- telemetry forwarding to UTM
- command receiving from UTM
- command acknowledgement flow direction

## Scope
Build a minimal but explicit separation between telemetry flow and command flow.

## Expected capabilities

### Telemetry
- receive telemetry from internal sources
- map internal telemetry into UTM outbound format
- publish telemetry to UTM through MQTT adapter
- keep telemetry logic isolated from command handling

### Command
- receive command from UTM through MQTT subscriber
- persist command metadata
- expose relay-ready command handling service
- prepare acknowledgement flow

## Suggested components

### Telemetry side
- `TelemetryMessage` or equivalent DTO
- `TelemetryMapper`
- `TelemetryForwardService`
- `UtmTelemetryMqttPublisher`

### Command side
- `UtmCommandEntity`
- `CommandStatus`
- `UtmCommandRepository`
- `UtmCommandService`
- `UtmCommandMqttSubscriber`
- `UtmCommandAckService` or equivalent placeholder

## Suggested fields for command entity
- id
- commandId
- missionId
- commandType
- priority
- payload
- status
- receivedAt
- ackAt
- executedAt
- failureReason

## Rules
- telemetry flow and command flow must remain separated
- telemetry should not block critical command handling
- command status should be explicit
- external MQTT integration should be behind adapters/subscribers/publishers

## Constraints
- do not build the full reliability system yet
- keep implementation incremental
- avoid broad threading optimization in this phase unless the current code already supports it
- preserve later extension points for retry, idempotency, and audit

## Done when
- internal telemetry can be mapped and forwarded through an outbound MQTT adapter
- inbound UTM command handling exists as a clear module
- command state is explicit
- telemetry and command concerns are not mixed
- module still compiles/builds