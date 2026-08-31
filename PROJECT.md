# Axiom

## Project

Axiom — an organization capability catalog for developers and their coding agents.

## Overview

Dozens of repositories implement shared libraries and required patterns. Humans forget them. Agents invent replacements that compile and fail review. Documentation in library repos is invisible to agents working in consuming repos. Prose in `AGENTS.md` / `CLAUDE.md` is loaded and still ignored.

Axiom treats house knowledge as versioned, queryable **facts** keyed by **intent** (the job the caller is trying to do), not by type or method count. A library with 176 methods publishes on the order of 20 intents. Sixty repositories do not each copy those intents. They query one catalog.

Facts are not essays. A fact names the job, the blessed symbol and artifact, a copy-paste snippet, the vernacular forms that are wrong, and whether missing it fails the build.

Knowledge that has not been proven stays an assumption. Search and tests are experiments. Only then does a row enter the catalog.

## Purpose

- Stop agents and humans from reimplementing blessed helpers and required patterns.
- Load only the facts needed for the current job, not the union of every library README.
- Give every developer and every agent in the org the same lookup, the same snippet, and the same detector.
- Make “ignored until review” a CI comment or a failed check, not a hallway conversation.

## Goals

1. **Intent over API surface.** Catalog jobs (`file_to_optional_string`, `external_failure`, `normalize_mac`), not 176 methods and not 60 README files.
2. **One owning repo per intent.** Duplicate intent ids fail aggregation.
3. **Hot dispatcher, cold catalog.** Every consuming repo ships ~15 lines that say “lookup first.” Snippets live in the catalog, not in sixty copies of `AGENTS.md`.
4. **Same records for humans and agents.** MCP, CLI, GitLab pages, and MR comments all render the same YAML facts.
5. **Required vs available.** A short required set can fail the build. The long tail is preferred, not enforced.
6. **Facts expire.** Rows are pinned to artifact versions. When the blessed symbol moves, the published catalog for that version moves with it.
7. **Gaps are first-class.** A miss is an assumption to file, not a license to invent a parallel `*Util`.

## Non-goals

- A general knowledge graph of every symbol in every repo.
- Replacing Javadoc, ADRs, or human design docs.
- Requiring one coding-agent vendor.
- Auto-promoting model prose into facts without an experiment (search, test, or review).
- Documenting every overload and fluent variant as its own intent.

## Users

| User | What they do |
|---|---|
| Library owner | Annotate or declare intents; ship `catalog.yaml` with the artifact |
| App developer | Lookup before writing a helper; read MR comments |
| Coding agent | Call `catalog_lookup` / `catalog_search` because the dispatcher said to |
| Platform owner | Aggregate catalogs, resolve duplicate intents, promote required rows |
| Reviewer | Stop repeating “we already have this”; let lint-diff say it |

## Success

Measured on consuming MRs, not on catalog completeness.

- Lookup or lint-diff fires before human review on helper-shaped diffs.
- Repeat “use the library” review comments decline on onboarded intents.
- New vernacular clones of required intents fail CI or get an MR note with the blessed snippet.
- Catalog misses produce gap issues instead of silent new utilities.

## System shape

```text
Library repo                    Org                    Consuming repo
-------------                   ---                    --------------
catalog.yaml  --publish jar-->  aggregator --MCP/CLI--> dispatcher (AGENTS.md)
examples/*.java                 index.yaml             catalog_lookup
detectors (optional)            GitLab pages           lint-diff on MR
                                gap issues             parent-POM required rules
```

## Components

Each item has its own PRD under `prds/`.

| ID | Component | Job |
|---|---|---|
| C1 | Catalog schema | The fact record and intent index |
| C2 | Maven catalog plugin | Extract and attach catalog to a build |
| C3 | Aggregator | Merge per-artifact catalogs into one org index |
| C4 | MCP server | Agent lookup / search / gap tools |
| C5 | CLI | Same tools for humans and CI |
| C6 | Consumer dispatcher | The 15-line `AGENTS.md` / `CLAUDE.md` contract |
| C7 | GitLab lint-diff | MR comments when a diff matches an anti-pattern |
| C8 | Required detectors | Parent-POM checks for `severity: required` |
| C9 | Gap workflow | File and promote catalog misses |

## Coordinates (working)

Replace the groupId with the org’s platform coordinates when the first repo is created.

```text
org.axiom:axiom-schema
org.axiom:axiom-maven-plugin
org.axiom:axiom-aggregator
org.axiom:axiom-mcp
org.axiom:axiom-cli
org.axiom:axiom-gitlab-ci          (CI include + scripts)
org.axiom:axiom-detectors          (Checkstyle / Error Prone / ArchUnit)
```
