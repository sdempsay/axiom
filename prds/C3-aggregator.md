# Product Requirements Document (PRD)

## Project: Axiom C3 — Aggregator

## Overview

Build that resolves configured artifacts, reads each `agent-catalog` classifier, and writes one org `index.yaml`. Duplicate intent ids fail the merge.

## Purpose

Consumers, CLI, MCP, pages, and lint-diff all read one index built from BOM versions, not from whatever a developer remembered to copy.

## Functional Requirements

### FR1: Source list

**API:**

```yaml
# aggregator-sources.yaml
schemaVersion: 1
bom:
  groupId: org.company
  artifactId: company-bom
  version: "2026.8.1"
artifacts:
  - groupId: org.dempsay.utils
    artifactId: exceptional
  - groupId: org.company.commons
    artifactId: commons-io
```

**Behavior:**
- If `bom` is set, versions for listed artifacts come from the BOM.
- An explicit `version` on an artifact overrides the BOM.
- Artifact with no version and no BOM entry fails the job.

### FR2: Fetch catalogs

**API:**

```text
axiom aggregate --sources aggregator-sources.yaml --out index.yaml
```

**Behavior:**
- Resolves `g:a:v:yaml:agent-catalog` from the org Maven repo.
- Missing classifier fails that artifact (no silent skip) unless `optional: true` on the source row.
- Reads snippets from `agent-catalog-examples` when present; otherwise leaves `snippet` empty and keeps `snippetRef`.

### FR3: Merge rules

**API:**

```yaml
# index.yaml
schemaVersion: 1
generatedAt: "2026-08-31T17:00:00Z"
intents:
  - id: external_failure
    artifact: { groupId: ..., artifactId: ..., version: "1.0.7" }
    ...
aliases:
  file_read_optional: file_to_optional_string
```

**Behavior:**
- One row per live intent id.
- Duplicate ids with different `blessed.symbol` fail the merge. Print both GAVs.
- Duplicate ids with identical blessed symbol and same owner: warn, keep first.
- `supersedes` entries populate `aliases`.
- `generatedAt` is written by the aggregator.

### FR4: Publish

**Behavior:**
- Writes `index.yaml` and optional `index.md` (table of id, title, severity, GAV).
- CI of `platform/axiom-catalog` commits or pages-publishes the output on main.
- Does not rewrite history of library catalogs.

### FR5: Freshness check

**API:**

```text
axiom aggregate --check-stale
```

**Behavior:**
- If a listed artifact has a newer release in the repo than the index, exit 1 and print the GAV pair.
- Used as a scheduled pipeline, not on every consumer MR.

## Non-Functional Requirements

### NFR1: Runtime
- Java 21. Maven resolver (Aether) for artifact fetch.
- No database.

### NFR2: Determinism
- Intent list in `index.yaml` is sorted by `id`.
- Given the same sources and repo contents, output is identical except `generatedAt`.

### NFR3: Time
- Pilot (≤ 20 artifacts) completes in under 2 minutes on GitLab runners with a warm local repo.

## Package Structure

```text
aggregator/src/main/java/org/axiom/agg/
├── AggregateCommand.java
├── CatalogFetcher.java
├── Merger.java
└── IndexWriter.java
```

## Test Coverage

1. **MergerTest**
   - `mergesDistinctIds()`
   - `duplicateSymbolConflictFails()`
   - `supersedesWritesAlias()`
   - `bomVersionApplied()`

2. **CatalogFetcherTest**
   - `missingClassifierFails()`
   - `optionalMissingClassifierSkips()`

## Example Usage

```bash
axiom aggregate \
  --sources aggregator-sources.yaml \
  --repo https://gitlab.example.com/api/v4/groups/platform/-/packages/maven \
  --out target/index.yaml
```
