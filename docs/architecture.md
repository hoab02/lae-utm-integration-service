# UTM Integration Service Architecture

## 1. Role of the service
`fms-utm-integration-service` is the integration boundary between DCS and UTM.

It is responsible for translating internal DCS workflows into UTM-compatible interactions and translating selected UTM feedback back into internal workflows.

This service should be treated as a domain-oriented integration service, not only as a generic CRUD backend or a thin HTTP proxy.

## 2. External interaction model

### REST with UTM
Expected REST-style interactions include:
- session connect / disconnect
- heartbeat
- pilot registration
- drone registration
- flight approval request
- airborne notification
- landing / completion notification
- emergency reporting

### MQTT with UTM
Expected MQTT-style interactions include:
- telemetry uplink
- command downlink
- command acknowledgement
- airspace updates such as NFZ and corridor changes

## 3. Internal capabilities
The service is expected to evolve into the following business capabilities:

### session
Manage UTM session lifecycle:
- connect
- disconnect
- heartbeat
- reconnect
- session status persistence

### registry
Manage pilot and drone registration:
- submit registration
- store status
- track approval / rejection / suspension

### approval
Manage flight approval lifecycle:
- create approval request
- send to UTM
- track status
- prevent invalid mission transitions

### mission
Manage runtime mission events:
- airborne
- in-flight state updates if needed
- landing
- completion
- emergency
- abnormal / unexpected landing

### telemetry
Receive or assemble telemetry from internal sources and forward to UTM using internal-to-external mapping.

### command
Receive UTM commands, validate and persist them, relay them internally, and track acknowledgements.

### airspace
Receive airspace updates such as NFZ and corridor changes, persist/cache them, and propagate updates internally.

## 4. Business rules
- approval must be `APPROVED` before airborne reporting is allowed
- telemetry path and command path must be isolated
- external payloads must be wrapped by internal DTOs/adapters
- every significant external operation should be auditable
- business state transitions should be explicit

## 5. Architectural direction
Prefer business modules over generic layers.

Recommended structure:

```text
com.viettelpost.fms.utm_integration
├─ config
├─ common
│  ├─ exception
│  ├─ util
│  ├─ model
│  └─ constant
├─ session
│  ├─ domain
│  ├─ dto
│  ├─ repository
│  ├─ service
│  ├─ web
│  └─ client
├─ registry
├─ approval
├─ mission
├─ telemetry
├─ command
├─ airspace
└─ infrastructure