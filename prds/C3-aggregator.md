# Product Requirements Document (PRD)

## Project: Axiom C3 — Ingest and merge

## Overview

Engine inside `axiom-mcp` that accepts a Maven GAV, fetches `classifier=agent-catalog` with Maven Resolver, stores the catalog on local disk, and merges catalogs into one index. Duplicate intent ids fail the merge. A library can be removed.

This is not a batch job that publishes an org-wide `axiom-index` artifact. The live files on the host **are** the index.

## Purpose

HTTP, MCP, and CLI all read one merged view built from catalogs the operator (or library CI) added, not from whatever a developer remembered to copy.

## Functional Requirements

### FR1: Add by GAV

**API:**

```http
POST /catalogs
Content-Type: application/json

{"groupId":"org.dempsay.utils","artifactId":"exceptional","version":"1.0.7"}
```

```text
axiom catalog add org.dempsay.utils:exceptional:1.0.7
```

**Behavior:**
- Resolves `g:a:v:yaml:agent-catalog` from configured Maven repositories (GitHub Packages / Maven Central as needed).
- Reads snippets from `agent-catalog-examples` when present; otherwise keeps `snippetRef` and inlines nothing.
- Writes the catalog under the data dir (`$AXIOM_DATA` or `~/.axiom/catalogs/g/a/v.yaml`).
- Re-add of the same GAV replaces that version’s file and re-merges.
- Missing classifier is an error (no silent skip).
- No authentication in the pilot.

### FR2: CI push

**Behavior:**
- After a library `mvn deploy`, that library’s CI POSTs the same GAV body to the running `axiom-mcp`.
- The service does not poll CI. CI is just another client of FR1.
- A user can add Exceptional by GAV without waiting for that CI.

### FR3: Remove

**API:**

```http
DELETE /catalogs/org.dempsay.utils/exceptional
```

```text
axiom catalog remove org.dempsay.utils:exceptional
```

**Behavior:**
- Drops all stored versions of that artifact from the data dir and re-merges.
- Lookup no longer returns those intents.
- Missing artifact is an error.

### FR4: Merge rules

**API:**

```yaml
# ~/.axiom/index.yaml  (generated)
schemaVersion: 1
generatedAt: "2026-08-31T17:00:00Z"
intents:
  - id: external_failure
    artifact: { groupId: org.dempsay.utils, artifactId: exceptional, version: "1.0.7" }
    ...
aliases:
  file_read_optional: file_to_optional_string
```

**Behavior:**
- One row per live intent id.
- Duplicate ids with different `blessed.symbol` fail the add that caused the conflict. Print both GAVs. The new catalog is not stored.
- Duplicate ids with identical blessed symbol and same owner: warn, keep first.
- `supersedes` entries populate `aliases`.
- `generatedAt` is written on each successful merge.
- Intent list is sorted by `id`.

### FR5: Persistence

**Behavior:**
- Data dir is local files on the machine running the process.
- Restart reloads catalogs from disk and re-merges. No database.
- No Maven publication of the merged index in the pilot.

## Non-Functional Requirements

### NFR1: Runtime
- Java 21. **Maven Resolver** for artifact fetch (do not call this Aether in Axiom docs).
- No database.

### NFR2: Determinism
- Given the same stored catalogs, `index.yaml` is identical except `generatedAt`.

### NFR3: Time
- Adding one catalog (warm local repo) completes in a few seconds on a laptop.

## Package Structure

```text
axiom-mcp/ingest/src/main/java/org/dempsay/axiom/mcp/ingest/
├── CatalogIngest.java
├── CatalogFetcher.java
├── Merger.java
└── IndexStore.java
```

## Test Coverage

1. **MergerTest**
   - `mergesDistinctIds()`
   - `duplicateSymbolConflictFails()`
   - `supersedesWritesAlias()`
   - `removeDropsIntents()`

2. **CatalogFetcherTest**
   - `missingClassifierFails()`
   - `resolvesAgentCatalogClassifier()`

## Example Usage

```bash
axiom catalog add org.dempsay.utils:exceptional:1.0.7
axiom lookup "IOException in a service method"
axiom catalog remove org.dempsay.utils:exceptional
```
