# Phase 2 - Build session management

## Goal
Implement the first real business module: `session`.

This module manages the lifecycle of connecting the service to UTM.

## Scope
Build the minimum viable session flow.

## Expected capabilities
- connect to UTM
- disconnect from UTM
- persist session status
- expose session status internally
- prepare heartbeat support

## Suggested components

### Domain
- `UtmSessionEntity`
- `SessionStatus`

### Repository
- `UtmSessionRepository`

### Service
- `UtmSessionService`

### Web
- `UtmSessionController`

### Client / adapter
- `UtmSessionRestClient` or similar

## Suggested fields for session entity
- id
- sessionId
- token
- status
- connectedAt
- lastHeartbeatAt
- expiresAt
- failureReason

## Suggested internal endpoints
- `POST /internal/utm/session/connect`
- `POST /internal/utm/session/disconnect`
- `GET /internal/utm/session/status`

## Constraints
- keep implementation simple
- external UTM interaction should be behind an adapter/client
- do not over-engineer heartbeat scheduling in this phase unless already easy to support
- prefer incremental structure that can be extended later

## Done when
- a session record can be created and updated
- internal API exposes session lifecycle
- outbound UTM connect/disconnect is isolated behind a client
- module still validates/builds