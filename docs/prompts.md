# Codex CLI Prompts for fms-utm-integration-service

These prompts are designed for iterative work with Codex CLI.

---

## Prompt 1 — Read the current codebase first

```text
Focus only on fms-utm-integration-service.

Read AGENTS.md and the docs for this project first.

Then:
1. analyze the current structure of fms-utm-integration-service
2. identify which parts are template/skeleton code
3. identify which infrastructure pieces can be reused
4. propose the smallest safe refactor to move toward a domain-oriented UTM integration service

Do not change files yet.

Output:
- summary of current state
- reusable parts
- template leftovers
- recommended next implementation task
```

---

## Prompt 2 — Implement phase 1

```text
Focus only on fms-utm-integration-service.

Implement phase 1 from docs/tasks/phase-1.md.

Constraints:
- keep changes minimal
- do not add new frameworks
- preserve existing Spring Boot infrastructure
- avoid unrelated file changes

After editing:
- run module build
- summarize changes
- list files changed
- list remaining risks and next recommended task
```

---

## Prompt 3 — Build session module

```text
Focus only on fms-utm-integration-service.

Implement docs/tasks/phase-2-session.md.

Constraints:
- keep changes scoped to the session module and necessary shared infrastructure
- prefer simple Spring Boot patterns
- avoid unrelated refactors

After editing:
- run the smallest relevant build/test command
- summarize files changed
- explain assumptions and temporary stubs
- recommend the next task
```

---

## Prompt 4 — Build approval module

```text
Focus only on fms-utm-integration-service.

Implement docs/tasks/phase-3-approval.md.

Constraints:
- preserve current module stability
- do not change unrelated modules
- keep UTM outbound integration behind adapters/clients
- use explicit approval status handling

After editing:
- run module validation
- summarize changes
- list files changed
- list remaining gaps
```

---

## Prompt 5 — Build mission module

```text
Focus only on fms-utm-integration-service.

Implement docs/tasks/phase-4-mission.md.

Constraints:
- mission transitions must be explicit
- airborne must require APPROVED approval
- keep changes incremental and reviewable

After editing:
- run module validation
- summarize changes
- list files changed
- list any remaining TODOs
```

---

## Prompt 6 — Build telemetry and command bridge

```text
Focus only on fms-utm-integration-service.

Implement docs/tasks/phase-5-telemetry-command.md.

Constraints:
- keep telemetry and command concerns separated
- keep external MQTT interaction behind adapters/subscribers/publishers
- avoid unrelated threading or performance refactors unless required
- preserve current module stability

After editing:
- run the smallest relevant validation command
- summarize changes
- list files changed
- list remaining gaps and next recommended task
```

---

## Prompt 7 — Build airspace flow

```text
Focus only on fms-utm-integration-service.

Implement docs/tasks/phase-6-airspace.md.

Constraints:
- keep NFZ and corridor handling explicit
- preserve extension points for future propagation
- avoid overbuilding GIS logic
- keep changes incremental

After editing:
- run the smallest relevant validation command
- summarize changes
- list files changed
- list remaining gaps
```

---

## Prompt 8 — Add reliability patterns

```text
Focus only on fms-utm-integration-service.

Implement docs/tasks/phase-7-reliability.md.

Constraints:
- keep the first version simple and explicit
- do not add unnecessary infrastructure
- focus on idempotency, inbox/outbox direction, and bounded retry support
- avoid broad unrelated refactors

After editing:
- run relevant validation
- summarize changes
- list files changed
- explain assumptions
- list remaining risks
```

---

## Prompt 9 — Add observability and security hardening

```text
Focus only on fms-utm-integration-service.

Implement docs/tasks/phase-8-observability-security.md.

Constraints:
- keep changes practical and production-friendly
- prefer structured logging and useful metrics over noisy instrumentation
- avoid breaking the existing local development flow
- preserve current architecture direction

After editing:
- run relevant validation
- summarize changes
- list files changed
- list operational/security improvements made
- list remaining hardening gaps
```

---

## Prompt 10 — Review before coding

```text
Focus only on fms-utm-integration-service.

Review the current implementation of the target module before changing code.

Please provide:
1. architecture issues
2. naming issues
3. state management issues
4. reliability gaps
5. the smallest safe improvement plan

Do not change files yet.
```

---

## Prompt 11 — Tight-scope patch

```text
Focus only on fms-utm-integration-service.

Apply only the smallest safe patch required to complete the requested task.
Do not perform unrelated cleanup.

After editing:
- report exact files changed
- explain why each file changed
- run the smallest relevant validation command
```


---

# Cách chạy thực tế theo từng phase

Thứ tự bạn nên làm:

1. `phase-1.md`
2. `phase-2-session.md`
3. `phase-3-approval.md`
4. `phase-4-mission.md`
5. `phase-5-telemetry-command.md`
6. `phase-6-airspace.md`
7. `phase-7-reliability.md`
8. `phase-8-observability-security.md`

Quy trình mỗi phase:
- dùng prompt review trước
- sau đó dùng prompt implement đúng phase
- cuối cùng đối chiếu `docs/review-checklist.md`

---

# Gợi ý rất thực dụng

Khi làm phase 1, bạn nên đưa Codex prompt này trước tiên:

```text
Focus only on fms-utm-integration-service.

Read AGENTS.md and docs/tasks/phase-1.md first.

Review the current module and explain:
1. which classes are still template code
2. which infrastructure pieces can be reused
3. what the smallest safe phase-1 patch should be

Do not change files yet.