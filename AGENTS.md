# AGENTS.md

## Project scope
This repository may contain multiple services. The current active focus is:

- `fms-utm-integration-service`

When a task is ambiguous, prefer working only inside `fms-utm-integration-service` unless the task explicitly requires changes in shared modules.

## Service goal
`fms-utm-integration-service` is the integration boundary between DCS and UTM.

Its responsibilities include:

- manage UTM session lifecycle
- register pilot and drone with UTM
- submit and track flight approval
- report mission runtime events such as airborne, landing, completion, emergency
- forward telemetry to UTM
- receive UTM commands and relay them to DCS/GCS
- receive and process airspace updates such as NFZ and corridor changes

## Tech stack
- Java 21
- Spring Boot
- Maven
- PostgreSQL
- Kafka
- MQTT
- Docker Compose

## Architecture direction
Prefer a domain-oriented structure over generic CRUD scaffolding.

Target business capabilities:

- session
- registry
- approval
- mission
- telemetry
- command
- airspace

Prefer clear business modules and explicit state transitions over broad generic services.

## Important business rules
- A mission must not become `AIRBORNE` until flight approval is `APPROVED`
- Telemetry flow and command flow must remain separated
- External UTM payloads should be wrapped by internal DTOs/adapters
- External interactions should be auditable
- State transitions should be explicit and validated
- Avoid direct leakage of raw external contracts into internal business logic

## Code guidance
- Reuse existing Spring Boot infrastructure where possible
- Do not introduce new frameworks unless clearly needed
- Do not keep `Example*` classes in the final main architecture
- Keep changes scoped and reviewable
- Prefer simple, production-friendly code
- Prefer readable naming over over-abstraction
- Add TODO notes only when strictly necessary and clearly actionable

## Editing rules
When making changes:

1. explain what is being changed
2. keep file edits minimal and relevant
3. avoid unrelated refactors
4. state assumptions clearly
5. run build or tests when possible
6. report any temporary hardcoding or stubs

## Expected output after each task
After completing a task, provide:

1. summary of changes
2. list of files changed
3. assumptions made
4. build/test result
5. remaining risks
6. recommended next step

## Safety for refactors
Before deleting or replacing template code, first confirm whether it is still referenced.
If a template class is still required for compilation, isolate it rather than removing it immediately.

## Preferred implementation order
When the user asks to build the service progressively, prefer this order:

1. phase 1: refactor skeleton into domain foundation
2. phase 2: session management
3. phase 3: approval flow
4. phase 4: mission runtime
5. phase 5: telemetry and command bridge
6. phase 6: airspace update flow
7. phase 7: reliability patterns
8. phase 8: observability and security hardening

## Build discipline
If the task includes code changes, try to validate with the smallest relevant command first, for example:

- module test
- module package
- targeted test class

If full build is too heavy, say what was validated and what was not.