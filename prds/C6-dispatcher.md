# Product Requirements Document (PRD)

## Project: Axiom C6 — Consumer dispatcher

## Overview

The only Axiom text that belongs in every consuming repository. Tells humans and agents to query the catalog. Does not contain the catalog.

## Purpose

Replace per-repo philosophy sections and copied snippet books with one short contract that stays stable as the index grows.

## Functional Requirements

### FR1: Canonical text

**API:**

```markdown
## Platform catalog (Axiom)

Before writing a helper, a try/catch for I/O or JDBC, or a new utility class:
1. Look up the job: `axiom lookup "<job>"` or MCP `catalog_lookup`.
2. If a required fact returns, use that snippet and dependency. Stop.
3. If an available fact returns, prefer it over a local clone.
4. If nothing returns, `axiom search "<job>"`, then `axiom gap` / `catalog_gap`.
   Do not invent a parallel helper.

This file is not the catalog. The catalog is the lookup result.
```

**Behavior:**
- This text is the product. Repos may add a one-line pointer to local runbooks below it, not above it.
- No intent table in the dispatcher. Tables rot.

### FR2: File placement

**Behavior:**
- Source of truth file: `AGENTS.md` section as above.
- `CLAUDE.md` contains `@AGENTS.md` or the same section if the repo does not use `AGENTS.md`.
- Other agent files (Cursor, Gemini) symlink or copy `AGENTS.md`. Do not fork wording.

### FR3: Template distribution

**API:**

```text
dispatcher/AGENTS.section.md
archetype: src/main/resources/archetype-resources/AGENTS.md
```

**Behavior:**
- Maven archetype and GitLab repo template include the section.
- Existing repos get a one-shot MR from platform, not a required rewrite of the rest of `AGENTS.md`.

### FR4: Exceptional and other required intents

**Behavior:**
- Required intents are not inlined in the dispatcher after Axiom lookup works.
- Until MCP/CLI are available in a repo, a temporary inlined snippet is allowed. It must be removed in the MR that adds the CI include.

### FR5: Negative requirement

**Behavior:**
- Dispatcher must not link to `WhyBeExceptional.md` as the way to learn the default snippet.
- Cold docs may be linked from the catalog record (`docs:` field), not from the dispatcher.

## Non-Functional Requirements

### NFR1: Length
- Dispatcher section ≤ 20 lines.

### NFR2: Stability
- Wording changes go through `platform/axiom`. Consuming repos pull, they do not edit.

## Package Structure

```text
dispatcher/
├── AGENTS.section.md
├── CLAUDE.import.md
└── README.md
```

## Test Coverage

1. **DispatcherLintTest**
   - `sectionWithinTwentyLines()`
   - `containsLookupAndGapVerbs()`
   - `doesNotContainIntentTable()`

## Example Usage

A consuming `AGENTS.md` after adoption:

```markdown
# Developer instructions

## Build
mvn verify

## Platform catalog (Axiom)
Before writing a helper, a try/catch for I/O or JDBC, or a new utility class:
1. Look up the job: `axiom lookup "<job>"` or MCP `catalog_lookup`.
...
```
