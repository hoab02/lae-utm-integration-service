# Phase 4 - Build mission runtime

## Goal
Implement the mission runtime module used for airborne, landing, completion, and emergency reporting.

## Scope
Build the first explicit mission state flow.

## Expected capabilities
- create / track mission runtime state
- report airborne
- report landing
- report completion
- report emergency
- validate mission transitions

## Suggested components

### Domain
- `MissionEntity`
- `MissionState`

### Repository
- `MissionRepository`

### Service
- `MissionService`

### Web
- `MissionController`

### Client / adapter
- `UtmMissionRestClient` or similar

## Suggested fields for mission entity
- id
- missionId
- planId
- droneId
- state
- airborneAt
- landingAt
- completedAt
- emergencyFlag
- emergencyReason

## Suggested internal endpoints
- `POST /internal/utm/missions/airborne`
- `POST /internal/utm/missions/landing`
- `POST /internal/utm/missions/complete`
- `POST /internal/utm/missions/emergency`

## Rules
- `AIRBORNE` must require approval state `APPROVED`
- state transitions should be explicit
- emergency should remain visible as a first-class event/state concern

## Constraints
- keep implementation incremental
- avoid adding large reliability infrastructure in this phase
- preserve clean separation for future telemetry and command modules

## Done when
- mission states exist explicitly
- mission transitions are validated
- airborne / landing / completion / emergency are exposed internally
- module compiles and is ready to integrate with approval dependency