# Phase 6 - Build airspace update flow

## Goal
Implement the airspace update capability for handling data such as NFZ and corridor updates from UTM.

## Scope
Build a minimal flow that can receive, persist, and expose the latest airspace updates.

## Expected capabilities
- receive NFZ updates from UTM
- receive corridor updates from UTM
- persist update metadata
- cache or expose the latest effective version internally
- prepare propagation to other internal services

## Suggested components
- `AirspaceUpdateEntity`
- `AirspaceUpdateType`
- `AirspaceUpdateRepository`
- `AirspaceService`
- `NfzUpdateHandler`
- `CorridorUpdateHandler`
- `AirspaceCacheService` or equivalent simple internal cache abstraction
- `AirspaceController` for internal query if useful

## Suggested fields for airspace update entity
- id
- updateId
- type
- version
- payload
- effectiveFrom
- receivedAt
- source
- status

## Rules
- NFZ and corridor updates should be explicit types
- the latest version should be identifiable
- external payloads should not be scattered across the codebase
- the design should remain easy to extend toward Kafka propagation later

## Constraints
- keep it incremental
- do not overbuild GIS logic in this phase
- avoid adding complex cache infrastructure unless already available
- focus on receiving, storing, and exposing latest updates cleanly

## Done when
- NFZ and corridor updates can be received and processed
- update type is explicit
- latest update query path exists or is easy to add
- module still builds