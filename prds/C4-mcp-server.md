# Product Requirements Document (PRD)

## Project: Axiom C4 — HTTP + MCP server

## Overview

One process in `sdempsay/axiom-mcp` that exposes the merged index over HTTP and MCP. Same records as the CLI. Local files are the store (C3). MCP is another transport on this app, not a second binary.

Pilot runs on a single dev host. No authentication.

## Purpose

Let coding agents (Grok, Claude Code, OpenCode) and humans resolve an intent, add/remove a library by GAV, and file a gap without scraping `AGENTS.md`.

## Functional Requirements

### FR1: Process and config

**API:**

```text
axiom-mcp --data ~/.axiom --bind 127.0.0.1:8741
```

```json
{
  "mcpServers": {
    "axiom": {
      "command": "axiom-mcp",
      "args": ["--data", "/Users/shawn/.axiom", "--stdio"]
    }
  }
}
```

**Behavior:**
- Default: HTTP server on localhost plus optional stdio MCP in the same process (`--stdio`).
- `--data` is the C3 data dir. Created if missing.
- On start, load catalogs from disk and merge. Empty store is valid (lookup returns no hits).
- Refuse to start if the data dir is not writable.
- `--reload-interval` optional; default no auto-reload. Writes (add/remove/gap) update disk and memory immediately.

### FR2: catalog_lookup

**API:**

```json
{
  "name": "catalog_lookup",
  "arguments": {
    "query": "handle IOException in a service",
    "language": "java",
    "limit": 3
  }
}
```

```http
GET /lookup?q=handle+IOException&language=java&limit=3
```

**Behavior:**
- Matches `query` against intent `id`, `title`, `triggers`, `blessed.symbol`.
- Filters by `language` when provided.
- Returns 0..`limit` intents (default 3) with: id, title, severity, blessed, GAV, primary snippet text, secondary snippets if present, antiPatterns, never.
- Empty result is a valid response, not an error. Include a `hint` to call `catalog_search` or `catalog_gap`.

### FR3: catalog_search

**API:**

```json
{
  "name": "catalog_search",
  "arguments": { "query": "mac", "limit": 10 }
}
```

**Behavior:**
- Broader ranking than lookup. May return available and required.
- Each hit includes id, title, severity, GAV, score.
- Does not include full snippet unless `includeSnippet: true`.

### FR4: catalog_get

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

### FR5: catalog add / remove

MCP tools `catalog_add` and `catalog_remove` call C3 FR1 / FR3. HTTP routes are defined there. No auth in the pilot.

### FR6: catalog_gap

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
- Writes a YAML/markdown file under `$AXIOM_DATA/gaps/` (C9).
- Returns the file path. Does not open a GitHub issue in the pilot.

### FR7: Tool descriptions

**Behavior:**
- Each tool description states: call `catalog_lookup` before writing a helper, try/catch for I/O, or a new `*Util` class.
- Descriptions are part of the product. Agents that never read `AGENTS.md` still see them.

## Non-Functional Requirements

### NFR1: Runtime
- Java 21. Parent `dempsay-parent`. Official MCP Java SDK or equivalent for stdio; HTTP is the same app.
- Shares `axiom-model` with the plugin.

### NFR2: Latency
- `catalog_lookup` against a 500-intent in-memory index returns in under 50ms after load.

### NFR3: Secrets
- Pilot has no tokens. When auth is added later, never log the token.

### NFR4: Compatibility
- Documented client configs for Claude Code, Grok, and OpenCode in `axiom-mcp/README.md`.

## Package Structure

```text
axiom-mcp/server/src/main/java/org/dempsay/axiom/mcp/
├── AxiomServer.java
├── HttpApi.java
├── LookupTool.java
├── SearchTool.java
├── GetTool.java
├── AddTool.java
├── RemoveTool.java
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
   - `writesGapFile()`

3. **HttpApiTest**
   - `postGavAddsCatalog()`
   - `deleteRemovesCatalog()`

## Example Usage

Agent-facing result (conceptual):

```text
id: external_failure
severity: required
use: org.dempsay.utils.exceptional.api.ExceptionalSupplier.of
artifact: org.dempsay.utils:exceptional:1.0.7
snippet:
  final ExceptionalResponse<Data> response = ExceptionalSupplier.of(() -> fetchData())
      .execute();
```
