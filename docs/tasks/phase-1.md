# Phase 1 - Refactor skeleton into domain foundation

## Goal
Refactor `fms-utm-integration-service` from a template-oriented skeleton into a domain-oriented foundation for UTM integration.

## Background
The service currently started from a generic template.
Before building business flows, the codebase should expose the right package direction and remove `Example*` classes from the main intended architecture.

## Scope
This phase should focus only on foundational refactor.

## Tasks
1. inspect the current package structure of `fms-utm-integration-service`
2. identify reusable infrastructure and configuration
3. isolate, de-emphasize, or remove `Example*` classes from the primary service flow
4. create business module package structure for:
   - session
   - approval
   - mission
5. add core enums:
   - `SessionStatus`
   - `ApprovalStatus`
   - `MissionState`
6. keep the module compiling

## Constraints
- do not introduce new frameworks
- preserve existing working Spring Boot infrastructure
- avoid broad unrelated cleanup
- prefer minimal safe changes

## Deliverables
- package structure exists
- core enums exist
- template code is no longer central to intended architecture
- module still builds

## Done when
- the module compiles
- the direction of the architecture is visible in code
- a next phase can start from the new domain foundation