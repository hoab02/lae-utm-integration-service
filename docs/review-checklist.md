# Review Checklist for Codex Changes

Use this checklist after each Codex task before accepting the patch.

## 1. Scope control
- Are the changes limited to the requested phase?
- Did Codex avoid unrelated cleanup?
- Were unrelated modules left untouched?

## 2. Architecture alignment
- Does the change move the service toward a domain-oriented UTM integration service?
- Did it avoid keeping `Example*` classes as part of the main intended architecture?
- Are business capabilities separated clearly?

## 3. Naming
- Are class names business-oriented and readable?
- Are names aligned with UTM integration concepts?
- Are there any generic placeholder names left in active flow?

## 4. State management
- Are states explicit with enums where needed?
- Are transitions validated clearly?
- Are invalid transitions blocked?

## 5. External integration boundaries
- Are outbound UTM calls isolated behind adapters/clients?
- Are raw external payloads kept away from deep business logic?
- Is the internal API using internal DTOs instead of leaking external contracts?

## 6. Persistence and data model
- Are entities aligned with the current phase?
- Are required fields present?
- Is the schema direction reasonable for later phases?

## 7. Reliability awareness
- Even if not fully implemented yet, did the code avoid making future idempotency/retry impossible?
- Are critical flows easy to audit later?
- Did the change avoid mixing command flow with telemetry flow?

## 8. Test and build validation
- Did Codex run the smallest relevant build/test command?
- Is the reported validation result believable and complete?
- If validation was partial, did Codex say what was not validated?

## 9. Temporary shortcuts
- Are hardcoded values clearly marked?
- Are stubs explicit and limited?
- Are TODO notes actionable instead of vague?

## 10. Merge readiness
- Is the diff understandable?
- Can this patch be reviewed and merged independently?
- Is the next step clear?

## Accept / reject note template
Use this after each phase review:

### Accept if
- scope is correct
- architecture direction improved
- build/test passed or partial validation is acceptable
- no dangerous unrelated refactor happened

### Reject if
- patch is too broad
- naming is still template/generic
- business rules are hidden or unclear
- command and telemetry concerns are mixed
- build/test status is unclear