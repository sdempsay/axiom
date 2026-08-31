# Product Requirements Document (PRD)

## Project: Axiom C5 — CLI

## Overview

Command-line interface in `sdempsay/axiom-mcp` over the same model and local store as the HTTP/MCP server. Used by humans, library CI, and agents that cannot speak MCP.

## Purpose

Make lookup, add/remove, validation, and gap filing possible in a terminal without an MCP client.

## Functional Requirements

### FR1: Binary

**API:**

```text
axiom <command> [options]
```

**Behavior:**
- Distributed from `org.dempsay.axiom:axiom-mcp` (executable jar) and documented as `axiom` on PATH.
- Talks to the local data dir by default (`AXIOM_DATA` / `~/.axiom`).
- `--url` optional: call a running HTTP server instead of opening the store directly. Pilot may use either; same results.

### FR2: lookup / search / get

**API:**

```text
axiom lookup "read file to optional string" [--language java] [--limit 3] [--json]
axiom search mac [--json]
axiom get external_failure
```

**Behavior:**
- Same ranking and payloads as the MCP tools.
- Human output: id, severity, symbol, GAV, primary snippet.
- `get` is exact id or alias. Exit 1 if missing.

### FR3: validate

**API:**

```text
axiom validate path/to/catalog.yaml
```

**Behavior:**
- C1 validation via `axiom-model`. Used by library owners before push. Does not require a running server.

### FR4: catalog add / remove

**API:**

```text
axiom catalog add org.dempsay.utils:exceptional:1.0.9
axiom catalog remove org.dempsay.utils:exceptional
```

**Behavior:**
- Delegates to C3. Same process as HTTP.

### FR5: gap

**API:**

```text
axiom gap --job "..." --attempted "..." --repo team/service
```

**Behavior:**
- Same as MCP `catalog_gap` (local file).

## Non-Functional Requirements

### NFR1: Runtime
- Java 21. Picocli. Shares `axiom-model` and C3 ingest.

### NFR2: Exit codes
- 0 success, 1 user/data error, 2 usage error.

### NFR3: Offline
- `lookup`, `search`, `get`, `validate` work against `--data` with no network if catalogs are already stored.
- `catalog add` needs Maven Resolver network unless the artifact is already in the local repo.

## Package Structure

```text
axiom-mcp/cli/src/main/java/org/dempsay/axiom/cli/
├── AxiomMain.java
├── LookupCommand.java
├── SearchCommand.java
├── GetCommand.java
├── ValidateCommand.java
├── CatalogAddCommand.java
├── CatalogRemoveCommand.java
└── GapCommand.java
```

## Test Coverage

1. **LookupCommandTest**
   - `printsSnippet()`
   - `jsonMode()`
   - `emptyStoreExitsZeroWithHint()`

2. **ValidateCommandTest**
   - `invalidYamlExitsOne()`

## Example Usage

```bash
axiom catalog add org.dempsay.utils:exceptional:1.0.9
axiom lookup "IOException in a service method"
axiom get external_failure
```
