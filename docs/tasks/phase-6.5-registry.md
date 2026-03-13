# Phase 6.5 - Build registry capability for pilot and drone registration

## Goal
Implement the missing UTM registry capability before reliability hardening.

This phase introduces two explicit registration flows:

- pilot registration
- drone registration

The purpose is to complete the current business capability map of `fms-utm-integration-service` before adding cross-cutting reliability patterns.

## Why this phase exists
The current service already has these major capabilities:

- session
- approval
- mission
- telemetry
- command
- airspace

But it still lacks a dedicated registry capability for the identities and flying assets that typically must be registered with UTM before later workflows become fully compliant.

This phase fills that business gap.

## Scope
Build a minimal registry capability with explicit status tracking and internal submit/query endpoints for:

- pilot registration
- drone registration

This phase should focus on:

- submitting pilot registration to UTM
- submitting drone registration to UTM
- persisting pilot and drone registry state explicitly
- exposing current pilot and drone registry status internally
- isolating outbound UTM registry interactions behind client/adapters

## Out of scope
Do not implement the following in this phase unless strictly needed for compilation:

- full external callback/update flows from UTM
- reliability patterns such as inbox/outbox/idempotency framework
- retry schedulers
- telemetry, command, mission, or airspace changes
- deep approval refactors
- real external UTM REST integration if a stub client boundary is enough for now
- a generic combined registry service abstraction that hides pilot and drone flows behind vague naming

## Architectural direction
The registry capability should live under a dedicated top-level package:

```text
com.viettelpost.fms.utm_integration.registry
```

Recommended capability-first structure:

```text
registry
├─ domain
├─ dto
├─ repository
├─ service
├─ web
└─ client
```

Do not place new registry code in old generic top-level packages such as:

- `domain`
- `repository`
- `service`
- `web`

## Service design choice for this phase
This phase should follow **Option B**:

- `PilotRegistrationService`
- `DroneRegistrationService`

Do **not** implement one combined `RegistryService` as the main business entry point in this phase.

The reason is to keep pilot and drone registration explicit, readable, and easy to evolve independently later.

## Capability model
This phase supports two separate but related business flows.

### Pilot registration
Tracks a pilot’s registration state in UTM.

### Drone registration
Tracks a drone/UAS registration state in UTM.

These flows should remain explicit at the service level even if they share some common patterns.

## Expected business rules
- pilot registration state must be explicit
- drone registration state must be explicit
- registration submission must go through outbound UTM client boundaries
- internal APIs must return internal DTOs, not JPA entities
- raw external UTM contracts must not leak into business logic
- repeated submission policy must be explicit and minimal
- this phase may prepare later approval checks against pilot/drone registry state, but should not deeply refactor approval yet

## Suggested status model
Add one explicit enum for registration state:

- `DRAFT`
- `SUBMITTED`
- `APPROVED`
- `REJECTED`
- `SUSPENDED`

If the project already has a suitable enum with equivalent meaning, reuse it instead of creating another one.

Suggested enum name:

- `RegistrationStatus`

## Suggested entities

### PilotRegistrationEntity
Suggested fields:

- `id`
- `pilotId`
- `utmPilotId`
- `status`
- `submittedAt`
- `approvedAt`
- `rejectedAt`
- `rejectReason`

Entity expectations:

- extend `Auditor`
- store status as `EnumType.STRING`
- use the project’s current ID generation approach
- treat `pilotId` as the internal business lookup key for this phase

### DroneRegistrationEntity
Suggested fields:

- `id`
- `droneId`
- `utmDroneId`
- `status`
- `submittedAt`
- `approvedAt`
- `rejectedAt`
- `rejectReason`

Entity expectations:

- extend `Auditor`
- store status as `EnumType.STRING`
- use the project’s current ID generation approach
- treat `droneId` as the internal business lookup key for this phase

## Suggested repositories
- `PilotRegistrationRepository`
- `DroneRegistrationRepository`

Suggested repository shape:

- lookup by business key:
  - `pilotId`
  - `droneId`
- for this phase, prefer one current record per business key unless the current domain already requires historical records

Examples:

```java
Optional<PilotRegistrationEntity> findByPilotId(String pilotId);
Optional<DroneRegistrationEntity> findByDroneId(String droneId);
```

## Suggested services

### PilotRegistrationService
Expected operations:

- `submit(PilotRegistrationSubmitRequest request)`
- `getByPilotId(String pilotId)`

### DroneRegistrationService
Expected operations:

- `submit(DroneRegistrationSubmitRequest request)`
- `getByDroneId(String droneId)`

Implementation classes may be:

- `PilotRegistrationServiceImpl`
- `DroneRegistrationServiceImpl`

Keep service logic explicit and independent.

Do not hide both flows behind a generic service with vague methods.

## Suggested outbound clients/adapters

### Pilot side
- `UtmPilotRegistryClient`
- `StubUtmPilotRegistryClient`

### Drone side
- `UtmDroneRegistryClient`
- `StubUtmDroneRegistryClient`

If real external integration is not available yet, use minimal stub implementations so the application compiles and starts cleanly.

The stubs should clearly state that real UTM registry integration is not implemented yet.

## Suggested internal DTOs

### Pilot internal API DTOs
- `PilotRegistrationSubmitRequest`
- `PilotRegistrationStatusDto`

### Drone internal API DTOs
- `DroneRegistrationSubmitRequest`
- `DroneRegistrationStatusDto`

### Outbound adapter DTOs
Use small adapter-specific DTOs or records for outbound request/result mapping.

Do not leak raw external UTM contracts into controllers or service return models.

## Suggested internal endpoints

### Pilot registration endpoints
- `POST /internal/utm/pilots`
- `GET /internal/utm/pilots/{pilotId}`

### Drone registration endpoints
- `POST /internal/utm/drones`
- `GET /internal/utm/drones/{droneId}`

These four endpoints are enough for the minimal registry phase:

- submit pilot registration
- query pilot registration status
- submit drone registration
- query drone registration status

## Suggested flow

### Pilot registration submit flow
1. receive internal request
2. validate basic request structure
3. look up existing pilot registration by `pilotId`
4. apply minimal repeated-submission policy
5. call `UtmPilotRegistryClient`
6. persist or update the pilot registration record
7. return `PilotRegistrationStatusDto`

### Drone registration submit flow
1. receive internal request
2. validate basic request structure
3. look up existing drone registration by `droneId`
4. apply minimal repeated-submission policy
5. call `UtmDroneRegistryClient`
6. persist or update the drone registration record
7. return `DroneRegistrationStatusDto`

## Minimal repeated-submission policy
Keep policy simple and explicit for this phase.

Recommended safe rule:

- if no record exists, create a new one
- if a record exists in `SUBMITTED` or `APPROVED`, reject repeated submission
- if a record exists in `REJECTED` or `SUSPENDED`, do not overbuild re-registration policy yet; either reject clearly now or leave a tightly scoped TODO for later refinement

The important points are:

- policy must be explicit
- pilot and drone flows should each enforce it clearly

## Suggested package/file direction

### Domain
```text
registry/domain/RegistrationStatus.java
registry/domain/PilotRegistrationEntity.java
registry/domain/DroneRegistrationEntity.java
```

### Repository
```text
registry/repository/PilotRegistrationRepository.java
registry/repository/DroneRegistrationRepository.java
```

### DTO
```text
registry/dto/PilotRegistrationSubmitRequest.java
registry/dto/PilotRegistrationStatusDto.java
registry/dto/DroneRegistrationSubmitRequest.java
registry/dto/DroneRegistrationStatusDto.java
```

### Client
```text
registry/client/UtmPilotRegistryClient.java
registry/client/StubUtmPilotRegistryClient.java
registry/client/UtmDroneRegistryClient.java
registry/client/StubUtmDroneRegistryClient.java
```

Optional adapter DTOs if needed:

```text
registry/client/PilotRegistrySubmissionResult.java
registry/client/DroneRegistrySubmissionResult.java
```

### Service
```text
registry/service/PilotRegistrationService.java
registry/service/PilotRegistrationServiceImpl.java
registry/service/DroneRegistrationService.java
registry/service/DroneRegistrationServiceImpl.java
```

### Web
For Option B, the preferred direction is:

```text
registry/web/PilotRegistrationController.java
registry/web/DroneRegistrationController.java
```

Do not collapse these flows into a generic `RegistryController` unless there is a strong reason to do so later.

## Reuse from existing codebase
This phase should reuse:

- audited entity base classes such as `Auditor`
- existing repository scanning that already supports domain-oriented modules
- exception handling via `InternalException`
- shared error code approach via `ErrorCode`
- i18n property files:
  - `message_en.properties`
  - `message_vi.properties`
- client/adapter boundary pattern already used by:
  - session
  - approval
  - mission

## Shared changes that may be needed
Only add the smallest shared pieces strictly needed, for example:

- registry-specific error codes such as:
  - pilot registration not found
  - drone registration not found
  - duplicate registration request
- matching i18n messages

Avoid broad shared refactors.

## Constraints
- keep the patch minimal and reviewable
- do not introduce new frameworks
- do not refactor unrelated modules
- do not remove `Example*` classes unless they directly block compilation
- do not add reliability infrastructure yet
- do not implement full callback/update orchestration yet
- do not collapse pilot and drone registration into one vague generic service

## Done when
This phase is complete when all of the following are true:

- a dedicated `registry/...` capability exists
- `PilotRegistrationService` and `DroneRegistrationService` both exist
- pilot registration can be submitted and queried internally
- drone registration can be submitted and queried internally
- registration status is explicit and persisted
- outbound UTM registry boundaries exist behind pilot and drone client/adapters
- the module compiles and fits the current domain-oriented architecture

## Recommended next step after this phase
After registry is implemented, the next logical follow-up is:

1. add a small patch so approval can optionally validate pilot and drone registry state before submission
2. then continue to Phase 7 reliability

The first follow-up should remain narrow and safe.
