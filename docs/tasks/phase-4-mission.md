# Phase 3 - Build approval flow

## Goal
Implement the approval lifecycle used to request and track flight approval from UTM.

## Scope
Build a minimal but explicit approval module with clear states.

## Expected capabilities
- create approval request
- submit approval request to UTM
- persist approval status
- query approval status internally
- prepare for later callback/update handling

## Suggested components

### Domain
- `FlightApprovalEntity`
- `ApprovalStatus`

### Repository
- `FlightApprovalRepository`

### Service
- `FlightApprovalService`

### Web
- `FlightApprovalController`

### Client / adapter
- `UtmApprovalRestClient` or similar

## Suggested fields for approval entity
- id
- planId
- missionId
- droneId
- pilotId
- utmRequestId
- status
- requestedAt
- approvedAt
- rejectedAt
- rejectReason

## Suggested internal endpoints
- `POST /internal/utm/flight-approvals`
- `GET /internal/utm/flight-approvals/{planId}`

## Rules
- approval submission should be explicit
- approval state must not be hidden in raw response payloads
- later mission airborne flow must depend on `APPROVED`

## Constraints
- keep outbound UTM integration behind an adapter/client
- keep changes scoped
- avoid building the full callback system unless needed for compilation

## Done when
- approval records can be created and updated
- approval state is explicit
- internal API can submit and query approval
- the module compiles and is ready for mission dependency