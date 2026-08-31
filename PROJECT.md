# Axiom

## Project

Axiom — an organization capability catalog for developers and their coding agents.

## Overview

Dozens of repositories implement shared libraries and required patterns. Humans forget them. Agents invent replacements that compile and fail review. Documentation in library repos is invisible to agents working in consuming repos. Prose in `AGENTS.md` / `CLAUDE.md` is loaded and still ignored.

Axiom treats house knowledge as versioned, queryable **facts** keyed by **intent** (the job the caller is trying to do), not by type or method count. A library with 176 methods publishes on the order of 20 intents. Consuming repositories do not each copy those intents. They query one live catalog service.

Facts are not essays. A fact names the job, the blessed symbol and artifact, a copy-paste snippet, the vernacular forms that are wrong, and whether missing it fails the build.

Knowledge that has not been proven stays an assumption. Search and tests are experiments. Only then does a row enter the catalog.

## Purpose

- Stop agents and humans from reimplementing blessed helpers and required patterns.
- Load only the facts needed for the current job, not the union of every library README.
- Give every developer and every agent the same lookup, the same snippet, and the same detector.
- Make “ignored until review” a CI comment or a failed check, not a hallway conversation. (CI and detectors are **not** in the pilot.)

## Goals

1. **Intent over API surface.** Catalog jobs (`external_failure`, later `file_to_optional_string`), not every method and not every README.
2. **One owning library per intent.** Duplicate intent ids fail the merge on the service.
3. **Library tooling vs catalog service.** Libraries depend on the Maven plugin. They do not depend on the MCP service.
4. **Live registry.** `axiom-mcp` is a running HTTP+MCP process. Catalogs are added and removed; lookup hits local files on that host.
5. **Same records for humans and agents.** HTTP, MCP, and CLI all read the same on-disk catalogs.
6. **Required vs available.** A short required set may fail CI later. The long tail is preferred, not enforced. Pilot does not fail builds.
7. **Gaps are first-class.** A miss is recorded on the service, not a license to invent a parallel `*Util`.
8. **Mixed hosting.** Axiom and public libraries (Exceptional) live on GitHub. Private consuming apps may live on GitLab.

## Non-goals (pilot)

- A general knowledge graph of every symbol in every repo.
- Replacing Javadoc, ADRs, or human design docs.
- Requiring one coding-agent vendor.
- Auto-promoting model prose into facts without an experiment.
- GitLab lint-diff, parent-POM detectors, and the consumer dispatcher template (those are later).
- An org-wide `axiom-index` Maven artifact (libraries publish `classifier=agent-catalog`; the service’s local files are the live index).
- Authentication on the service (anyone who can reach it may add or remove).

## Users

| User | What they do |
|---|---|
| Library owner | Declare intents; ship `catalog.yaml` with the artifact; CI POSTs the GAV to `axiom-mcp` |
| App developer | Lookup before writing a helper (`axiom lookup` or HTTP) |
| Coding agent | Call `catalog_lookup` / `catalog_search` on the same process |
| Operator of `axiom-mcp` | Run the service on a single host; add a GAV by hand; remove a library |
| Reviewer | Later: let lint-diff say “we already have this” |

## Success

Measured on whether lookup happens before a helper is written, not on catalog completeness.

- A running `axiom-mcp` can add Exceptional by GAV and return `external_failure` with the blessed snippet.
- Exceptional’s Maven build publishes `classifier=agent-catalog`.
- A miss can be filed as a local gap record.
- Repeat “use Exceptional” review comments are a later metric, after dispatcher/CI exist.

## System shape

```text
Library repo (GitHub or GitLab)     axiom-mcp (this machine)
-------------------------------     ------------------------
catalog.yaml                        local files (the index)
  --mvn deploy classifier-->        HTTP POST /catalogs (GAV)
  --CI POST GAV ------------------> HTTP + MCP + CLI lookup
                                    local gap files

Umbrella (sdempsay/axiom) holds docs and git submodules:
  axiom-plugin   schema, annotations, Maven plugin
  axiom-mcp      live service
```

## Components

Each item has its own PRD under `prds/`. Pilot is C1–C5 and the local-file slice of C9. C6–C8 wait.

| ID | Component | Repo | Pilot |
|---|---|---|---|
| C1 | Catalog schema | axiom-plugin | yes |
| C2 | Maven catalog plugin | axiom-plugin | yes |
| C3 | Ingest and merge | axiom-mcp | yes (inside the live service) |
| C4 | HTTP + MCP server | axiom-mcp | yes |
| C5 | CLI | axiom-mcp | yes |
| C6 | Consumer dispatcher | later | no |
| C7 | GitLab lint-diff | later | no |
| C8 | Required detectors | later | no |
| C9 | Gap workflow | axiom-mcp | yes (local files only) |

## Dogfood

- **code-review (now).** Every Java/`pom.xml` commit in this tree is reviewed by `code-review diff --staged` (pre-commit hook). Findings here should extend [agentic-review-tool](https://github.com/sdempsay/agentic-review-tool) rules, not rot as one-off comments.
- **Axiom (after lookup works).** This umbrella and `axiom-plugin` / `axiom-mcp` are the first consuming repos. Dispatcher text lands here as soon as `axiom lookup` / MCP returns `external_failure`. Org-wide C6 stays later.

## Coordinates

```text
GitHub
  sdempsay/axiom          umbrella (this repo)
  sdempsay/axiom-plugin   submodule
  sdempsay/axiom-mcp      submodule

Maven (Java 21, parent org.dempsay.maven:dempsay-parent)
  org.dempsay.axiom:axiom-model
  org.dempsay.axiom:axiom-annotations
  org.dempsay.axiom:axiom-maven-plugin
  org.dempsay.axiom:axiom-mcp

First catalog
  org.dempsay.utils:exceptional
  owner.repo: sdempsay/exceptional-java
```

Java packages: `org.dempsay.axiom.*`.
