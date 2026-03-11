# Phase 7 - Add reliability patterns

## Goal
Add the first reliability layer needed for a production-oriented UTM integration service.

## Scope
Introduce minimal reliability building blocks without overcomplicating the service.

## Expected capabilities
- idempotency for important outbound actions
- inbox-style dedup support for inbound events/messages
- outbox direction for important internal/external event propagation
- bounded retry direction for selected operations

## Priority flows to protect
- flight approval submission
- airborne notification
- landing/completion notification
- emergency reporting
- command processing
- command acknowledgement

## Suggested components
- `IdempotencyKeyEntity`
- `IdempotencyService`
- `InboxMessageEntity`
- `InboxMessageService`
- `OutboxEventEntity`
- `OutboxPublisher` or equivalent worker placeholder
- `RetryPolicy` or scheduler abstraction where needed

## Rules
- duplicate external actions should become visible and controllable
- critical operations should be easy to audit
- reliability logic should not be hidden in unrelated services
- keep the first version simple and explicit

## Constraints
- do not overbuild distributed workflow machinery
- do not add new infrastructure unless necessary
- keep the design compatible with current Spring Boot foundation
- prefer a minimal reliable direction over a large unfinished system

## Done when
- idempotency direction exists for key outbound actions
- inbound dedup direction exists for message-based flows
- outbox/inbox structures are visible in architecture
- service still builds and current flows remain understandable