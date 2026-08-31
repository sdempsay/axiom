# Product Requirements Document (PRD)

## Project: Axiom C4 — MCP server

## Overview

MCP server that exposes the aggregated index as tools for coding agents. Same records as the CLI. No separate store.

## Purpose

Let Claude Code, OpenCode, and any other MCP client resolve an intent and file a gap without scraping `AGENTS.md` or GitHub.

## Functional Requirements

### FR1: Process and config

**API:**

```json
{
  "mcpServers": {
    "axiom": {
      "command": "axiom-mcp",
      "args": ["--index", "/opt/axiom/index.yaml"]
    }
  }
}
```

**Behavior:**
- Stdio MCP server.
- `--index` is a file path or HTTP(S) URL to `index.yaml`.
- On start, load index into memory. Refuse to start if the file is missing or invalid.
- `--reload-interval` optional; default no auto-reload. SIGHUP or process restart picks up a new index.

### FR2: catalog_lookup

**API:**

```json
{
  "name": "catalog_lookup",
  "arguments": {
    "query": "read a file into an optional string",
    "language": "java",
    "limit": 3
  }
}
```

**Behavior:**
- Matches `query` against intent `id`, `title`, `triggers`, `blessed.symbol`.
- Filters by `language` when provided.
- Returns 0..`limit` intents (default 3) with: id, title, severity, blessed, GAV, snippet text, antiPatterns, never.
- Empty result is a valid response, not an error. Include a `hint` to call `catalog_search` or `catalog_gap`.

### FR3: catalog_search

**API:**

```json
{
  "name": "catalog_search",
  "arguments": {
    "query": "mac",
    "limit": 10
  }
}
```

**Behavior:**
- Broader ranking than lookup. May return available and required.
- Each hit includes id, title, severity, GAV, score.
- Does not include full snippet unless `includeSnippet: true`.

### FR4: catalog_gap

**API:**

```json
{
  "name": "catalog_gap",
  "arguments": {
    "job": "decode a hex dump to bytes",
    "attempted": "local HexUtils in the service module",
    "searches": ["hex", "decodeHex", "HexFormat"],
    "repo": "team/billing-service"
  }
}
```

**Behavior:**
- Validates required `job`.
- When GitLab token and project are configured, opens an issue using the C9 template and returns the issue URL.
- When not configured, returns a filled markdown issue body for the agent to show the human.

### FR5: catalog_get

**API:**

```json
{
  "name": "catalog_get",
  "arguments": { "id": "external_failure" }
}
```

**Behavior:**
- Exact id or alias lookup.
- Unknown id → error payload with nearby ids from search, not an empty success.

### FR6: Tool descriptions

**Behavior:**
- Each tool description states: call `catalog_lookup` before writing a helper, try/catch for I/O, or a new `*Util` class.
- Descriptions are part of the product. Agents that never read `AGENTS.md` still see them.

## Non-Functional Requirements

### NFR1: Runtime
- Java 21. Official MCP Java SDK or equivalent stdio transport.
- Shares `axiom-model` with CLI.

### NFR2: Latency
- `catalog_lookup` against a 500-intent in-memory index returns in under 50ms after load.

### NFR3: Secrets
- GitLab token only from env `AXIOM_GITLAB_TOKEN`. Never logged.

### NFR4: Compatibility
- Documented client configs for Claude Code and OpenCode in `mcp/README.md`.

## Package Structure

```text
mcp/src/main/java/org/axiom/mcp/
├── AxiomMcpServer.java
├── LookupTool.java
├── SearchTool.java
├── GetTool.java
└── GapTool.java
```

## Test Coverage

1. **LookupToolTest**
   - `matchesTriggerPhrase()`
   - `respectsLanguageFilter()`
   - `emptyResultHasGapHint()`
   - `aliasResolvesViaGet()`

2. **GapToolTest**
   - `missingJobFails()`
   - `withoutTokenReturnsMarkdownBody()`

## Example Usage

Agent-facing result (conceptual):

```text
id: external_failure
severity: required
use: org.dempsay.utils.exceptional.api.ExceptionalSupplier.of
artifact: org.dempsay.utils:exceptional:1.0.7
snippet:
  var response = ExceptionalSupplier.of(() -> fetchData())
      .with(ex -> log.warn("fetch failed", ex))
      .execute();
```
