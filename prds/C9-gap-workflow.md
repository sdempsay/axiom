# Product Requirements Document (PRD)

## Project: Axiom C9 — Gap workflow

Pilot: local files on the `axiom-mcp` host. GitHub issues are later.

## Overview

How a catalog miss becomes a recorded assumption, then an experiment, then either a new intent row or a documented “does not exist.”

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
- Produced by `axiom gap` and MCP/HTTP `catalog_gap`.
- `job` required. Other fields optional but recommended.

### FR2: Local persistence (pilot)

**API:**

```text
$AXIOM_DATA/gaps/<timestamp>-<slug>.yaml
```

**Behavior:**
- Writes the FR1 record plus index `generatedAt` if an index exists.
- Returns the file path.
- Does not open GitHub or GitLab issues in the pilot.
- If no write token / GitHub is configured later, keep this as the fallback.

### FR3: Issue creation (later)

**API:**

```text
GitHub: sdempsay/axiom-mcp
labels: gap, needs-triage
```

**Behavior:**
- Title: `gap: <job>`.
- Body uses template fields from FR1.
- Assignee: catalog owners.

### FR4: Triage states (process, later)

**API:**

```text
needs-triage → exists → published
needs-triage → does-not-exist
needs-triage → rejected-local-clone
```

**Behavior:**
- `exists`: search or test found a blessed symbol. Action: add or fix the intent row in the owning library catalog, bump, re-add GAV.
- `does-not-exist`: experiment found nothing. Action: implement in the owning commons library or close as out of scope with a written reason.
- `rejected-local-clone`: a local helper is not acceptable; caller must wait for or contribute the commons method.

### FR5: Promotion rule

**Behavior:**
- A new intent row cannot merge without: blessed symbol that exists in a published artifact, snippet from a test or example, at least one trigger, and an owner repo.
- Model-written YAML without a symbol in source is invalid.
- `severity: required` additionally needs a human approver.

### FR6: Experiment log

**API:**

```markdown
## Experiment
- axiom search hex → no hit
- repo search decodeHex → …blessed symbol…
```

**Behavior:**
- Gap file or a follow-up records the search and any test.
- That record is the experiment that promotes the assumption to a fact.

## Non-Functional Requirements

### NFR1: Latency of process
- Triage SLA is organizational, not mechanical. Tooling only records the gap.

### NFR2: Access
- Pilot: anyone who can reach `axiom-mcp` can file a gap.
- Merge of catalog rows stays with library owners.

## Package Structure

```text
axiom-mcp/.../GapStore.java
axiom-mcp/catalog-project/ISSUE_TEMPLATE/gap.md   # later
```

## Test Coverage

1. **GapCommandTest**
   - `writesLocalFile()`
   - `requiresJob()`

2. **PromotionReviewTest** (docs/process checklist)
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
