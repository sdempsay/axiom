# Axiom — execution plan

## Project

Axiom catalog, MCP server, Maven plugin, and org rollout.

## Purpose

Build in slices that can ship to one commons library and a few consuming repos before involving all 60 repositories.

## Principles

- Schema first. Nothing else is stable until `catalog.yaml` validates.
- One library onboarded end-to-end before a second library.
- Agents get MCP and CLI in the same increment. Humans get the same index on GitLab pages.
- No merge gate until lint-diff comments are accurate for two weeks.
- Dispatcher text is a template, not a per-repo essay.

## Repos to create

| Repo | Contains | First milestone |
|---|---|---|
| `platform/axiom` | Schema, plugin, aggregator, CLI, MCP, CI include, detectors | M1–M3 |
| `platform/axiom-catalog` | Aggregated `index.yaml`, pages, gap issues | M3 |
| Existing commons library | First `catalog.yaml` + examples | M2 |
| 2–3 consuming services | Dispatcher + CI include | M4 |

Monorepo `platform/axiom` is acceptable for M1–M5. Split artifacts by Maven module, not by git repo, until a second team owns a piece.

## Module layout (`platform/axiom`)

```text
axiom/
├── PROJECT.md
├── PLAN.md
├── prds/
├── schema/                 # JSON Schema + example catalog.yaml
├── plugin/                 # axiom-maven-plugin
├── model/                  # shared Java model for catalog records
├── aggregator/
├── cli/
├── mcp/
├── gitlab-ci/              # include.yml + lint-diff
├── detectors/
└── dispatcher/             # AGENTS.md / CLAUDE.md templates
```

## Phases

### M0 — Decide coordinates and owners (days, not weeks)

- Pick `groupId`, GitLab group, and who may approve `severity: required`.
- Pick the first library (the commons with 176 methods, or Exceptional if that is a faster slice).
- Pick two consuming repos that already get “use the library” review comments.
- Record agent tools in use (OpenCode, Claude Code, other). MCP must support all of them or CLI is the fallback.

**Exit:** written owners, first-library choice, two consumer repos.

### M1 — Schema and model

**PRD:** [C1 Catalog schema](prds/C1-catalog-schema.md)

- JSON Schema for `catalog.yaml`.
- Java model in `axiom-model` (intent, symbol, GAV, triggers, anti-patterns, severity, snippet ref, version).
- Validator CLI command `axiom validate path/to/catalog.yaml`.
- Three example records: `external_failure`, `file_to_optional_string`, `normalize_mac`.

**Exit:** a catalog file that fails on missing intent id, missing blessed symbol, or duplicate ids inside one file.

### M2 — Maven plugin on one library

**PRD:** [C2 Maven catalog plugin](prds/C2-maven-plugin.md)

- Plugin reads `src/main/resources/agent-catalog/catalog.yaml` and optional `@AgentCapability` annotations.
- Validates against C1.
- Attaches `catalog.yaml` as a classified artifact (`classifier=agent-catalog`, type `yaml`) and/or writes `META-INF/axiom/catalog.yaml` into the main jar.
- Fails the library build on invalid catalog.
- Adds `examples/` as resources next to the catalog.

**Exit:** the first library’s `mvn verify` publishes a catalog next to the jar. Exceptional or the commons library has ≤ 20 intents, not 176.

### M3 — Aggregator and human index

**PRD:** [C3 Aggregator](prds/C3-aggregator.md)

- Job reads the org BOM or a configured list of GAVs.
- Fetches each `agent-catalog` artifact.
- Merges to `index.yaml`.
- Fails on duplicate intent ids across artifacts.
- Publishes `index.yaml` to `platform/axiom-catalog` and a generated pages site (intent table + snippet).

**Exit:** a developer can open pages and see the three pilot intents with snippets and coordinates.

### M4 — CLI + dispatcher in consuming repos

**PRDs:** [C5 CLI](prds/C5-cli.md), [C6 Consumer dispatcher](prds/C6-dispatcher.md)

- CLI: `axiom lookup "read file to optional"`, `axiom search mac`, `axiom validate`.
- CLI reads local cache of `index.yaml` (git submodule, package, or HTTP from pages).
- Dispatcher paragraph dropped into consuming `AGENTS.md` and `CLAUDE.md` via template.
- Archetype / repo template updated so new repos are born with the dispatcher.

**Exit:** a human or agent in a consumer repo can resolve `external_failure` to the Exceptional snippet without opening GitHub.

### M5 — MCP server

**PRD:** [C4 MCP server](prds/C4-mcp-server.md)

- Tools: `catalog_lookup`, `catalog_search`, `catalog_gap`.
- Same model and index as the CLI. No second database.
- Document MCP config for Claude Code and OpenCode.
- If a team cannot run MCP, CLI remains sufficient.

**Exit:** one recorded agent session where lookup happens *before* a helper is written.

### M6 — GitLab lint-diff

**PRD:** [C7 GitLab lint-diff](prds/C7-gitlab-lint-diff.md)

- Shared CI include.
- On MR, scan changed files for anti-pattern strings / regex from `severity: required` and `available` rows.
- Note on the MR: intent id, blessed symbol, snippet, GAV.
- `available` → comment. `required` → comment only in M6 (no job failure yet).

**Exit:** two weeks of comments on the pilot consumers. False-positive rate known.

### M7 — Required detectors

**PRD:** [C8 Required detectors](prds/C8-detectors.md)

- Parent POM optional profile or always-on checks for the small required set.
- First detector: vernacular `catch (IOException` / `catch (SQLException` in application packages.
- Second detector only after the first is trusted (file-read or MAC).
- `required` lint-diff job may now fail the pipeline.

**Exit:** a known-bad MR cannot merge without an allowlist note.

### M8 — Gap workflow

**PRD:** [C9 Gap workflow](prds/C9-gap-workflow.md)

- `axiom gap` and MCP `catalog_gap` open an issue in `platform/axiom-catalog`.
- Issue template: job, what the agent/human wrote, searches tried, owning-repo guess.
- Promotion rule: search or test evidence required before a new intent row merges.

**Exit:** at least one miss from a real MR becomes a catalog row instead of a local util.

### M9 — Roll out past the pilot

- Onboard the next two libraries by intent frequency in review comments, not by method count.
- Add dispatcher + CI include to the Maven archetype and to a GitLab push rule or compliance pipeline as warning-only.
- Do not bulk-import 60 catalogs. Invite owners when they next touch the library.

## Order of implementation work

```text
C1 schema
  → C2 plugin (needs C1)
  → C3 aggregator (needs C2 publishes)
      → C5 CLI (needs C3 index)
      → C6 dispatcher (can start in parallel with C5; needs wording only)
      → C4 MCP (same index as C5)
          → C7 lint-diff (needs index + anti-patterns)
              → C8 detectors (needs trusted anti-patterns)
              → C9 gap (needs MCP/CLI + issue project)
```

C6 dispatcher text can be drafted on day one and copied into the two consumer repos as soon as lookup works.

## Staffing (minimum)

| Role | Time | Owns |
|---|---|---|
| One engineer | M1–M5 | schema, plugin, aggregator, CLI, MCP |
| Library owner (first catalog) | M2 | intent clustering, snippets from tests |
| Consumer volunteer | M4–M7 | dispatcher, CI include, false-positive reports |
| Platform approver | M0, M7, M8 | required severity, duplicate intent disputes |

Do not staff a “knowledge graph team” until M5 is in daily use.

## Risks

| Risk | Mitigation |
|---|---|
| Catalog tries to list every method | Cap first library at 20 intents; leftover is “search first” |
| Agents ignore dispatcher again | lint-diff + detectors; dispatcher is necessary but not sufficient |
| Duplicate facts across repos | aggregator fails the merge; one owner per intent id |
| Stale snippets | catalog ships with the artifact version; aggregator uses BOM versions |
| MCP not adopted | CLI + CI comments still work |
| Merge gate too early | comments only until two-week false-positive review |

## Definition of done for the pilot

- First library publishes ≤ 20 intents including `external_failure`.
- Aggregated index is visible to humans (pages) and machines (CLI + MCP).
- Two consuming repos have the dispatcher and lint-diff.
- One required detector exists or is scheduled immediately after comment quality is accepted.
- Gap issues have a template and an owner.
