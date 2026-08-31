# Product Requirements Document (PRD)

## Project: Axiom C9 — Gap workflow

## Overview

How a catalog miss becomes an issue, an experiment, and either a new intent row or a documented “does not exist.”

## Purpose

Close the assumption → experiment → fact loop so a failed lookup does not authorize a new local `*Util`.

## Functional Requirements

### FR1: Gap record

**API:**

```yaml
job: decode hex dump to bytes
attempted: com.billing.HexUtils
searches: [hex, decodeHex, HexFormat]
repo: team/billing-service
language: java
asker: agent|human
```

**Behavior:**
- Produced by `axiom gap` and MCP `catalog_gap`.
- `job` required. Other fields optional but recommended.

### FR2: Issue creation

**API:**

```text
GitLab project: platform/axiom-catalog
labels: gap, needs-triage
```

**Behavior:**
- Title: `gap: <job>`.
- Body uses template fields from FR1 plus index version / `generatedAt`.
- Assignee: catalog owners. Not the asking service team, unless they own a library.

### FR3: Triage states

**API:**

```text
needs-triage → exists → published
needs-triage → does-not-exist
needs-triage → rejected-local-clone
```

**Behavior:**
- `exists`: search or test found a blessed symbol. Action: add or fix the intent row in the owning library catalog, bump, re-aggregate.
- `does-not-exist`: experiment found nothing. Action: either implement in the owning commons library and publish an intent, or close as out of scope with a written reason.
- `rejected-local-clone`: a local helper is not acceptable; caller must wait for or contribute the commons method.

### FR4: Promotion rule

**Behavior:**
- A new intent row cannot merge without: blessed symbol that exists in a published artifact, snippet from a test or example, at least one trigger, and an owner repo.
- Model-written YAML without a symbol in source is invalid.
- `severity: required` additionally needs platform approver review.

### FR5: Experiment log

**API:**

```markdown
## Experiment
- axiom search hex → no hit
- repo search decodeHex → org.company.commons.Bytes.decodeHex
- test: Bytes.decodeHex("0a") == new byte[]{10}
```

**Behavior:**
- Issue body or a follow-up comment records the search and any test.
- That record is the experiment that promotes the assumption to a fact.

### FR6: Feedback to the caller

**Behavior:**
- When the row is published, comment on the gap issue with `axiom get <id>` output.
- No requirement to auto-close the consumer MR.

## Non-Functional Requirements

### NFR1: Latency of process
- Triage SLA is organizational, not mechanical. Tooling only creates the issue.

### NFR2: Access
- Any developer or CI token that can open an issue in `platform/axiom-catalog` can file a gap.
- Merge of catalog rows stays with library owners.

## Package Structure

```text
catalog-project/
├── ISSUE_TEMPLATE/gap.md
└── README.md
```

## Test Coverage

1. **GapCommandTest**
   - `createsIssueWhenTokenPresent()`
   - `printsTemplateWhenTokenMissing()`
   - `requiresJob()`

2. **PromotionReviewTest** (docs/process test, checklist)
   - `rowWithoutSymbolRejected()`
   - `requiredNeedsApprover()`

## Example Usage

```bash
axiom gap \
  --job "decode hex dump to bytes" \
  --attempted "com.billing.HexUtils" \
  --searches hex,decodeHex,HexFormat \
  --repo team/billing-service
```
