# Product Requirements Document (PRD)

## Project: Axiom C5 — CLI

## Overview

Command-line interface over the same model and index as the MCP server. Used by humans, CI jobs, and agents that cannot speak MCP.

## Purpose

Make lookup and validation possible in a terminal, a GitLab job, and a devcontainer without an MCP client.

## Functional Requirements

### FR1: Binary

**API:**

```text
axiom <command> [options]
```

**Behavior:**
- Distributed as `org.axiom:axiom-cli` (executable jar) and documented as `axiom` on PATH in the devcontainer image.
- `--index` default: `AXIOM_INDEX` env, then `./.axiom/index.yaml`, then the org pages URL baked at build time.

### FR2: lookup

**API:**

```text
axiom lookup "read file to optional string" [--language java] [--limit 3] [--json]
```

**Behavior:**
- Same ranking as MCP `catalog_lookup`.
- Human output: id, severity, symbol, GAV, snippet.
- `--json` prints the intent records.

### FR3: search

**API:**

```text
axiom search mac [--json]
```

**Behavior:**
- Same as MCP `catalog_search`. Table output by default.

### FR4: get

**API:**

```text
axiom get external_failure
```

**Behavior:**
- Exact id or alias. Exit 1 if missing.

### FR5: validate

**API:**

```text
axiom validate path/to/catalog.yaml
```

**Behavior:**
- C1 validation. Used by the Maven plugin tests and by library owners before push.

### FR6: aggregate

**API:**

```text
axiom aggregate --sources aggregator-sources.yaml --out index.yaml
```

**Behavior:**
- Delegates to C3. Same process as the aggregator module; CLI is the user-facing entry.

### FR7: gap

**API:**

```text
axiom gap --job "..." --attempted "..." --repo team/service
```

**Behavior:**
- Same as MCP `catalog_gap`.

### FR8: cache

**API:**

```text
axiom cache refresh
axiom cache path
```

**Behavior:**
- Downloads `index.yaml` to `~/.axiom/index.yaml` or `$AXIOM_CACHE`.
- CI jobs call `refresh` once per pipeline.

## Non-Functional Requirements

### NFR1: Runtime
- Java 21. Picocli. Shares `axiom-model`.

### NFR2: Exit codes
- 0 success, 1 user/data error, 2 usage error.

### NFR3: Offline
- `lookup`, `search`, `get`, `validate` work offline if `--index` is a local file.

## Package Structure

```text
cli/src/main/java/org/axiom/cli/
├── AxiomMain.java
├── LookupCommand.java
├── SearchCommand.java
├── GetCommand.java
├── ValidateCommand.java
├── AggregateCommand.java
├── GapCommand.java
└── CacheCommand.java
```

## Test Coverage

1. **LookupCommandTest**
   - `printsSnippet()`
   - `jsonMode()`
   - `missingIndexExitsOne()`

2. **ValidateCommandTest**
   - `invalidYamlExitsOne()`

## Example Usage

```bash
axiom cache refresh
axiom lookup "IOException in a service method"
axiom get external_failure
```
