# Axiom — execution plan

## Project

Axiom catalog plugin, live MCP/HTTP service, and Exceptional as the first published catalog.

## Purpose

Ship a plugin that attaches a catalog to a library jar, and a single-host service that ingests those catalogs and answers lookup. Do not involve consumer CI or detectors until lookup works.

## Principles

- Schema first. Nothing else is stable until `catalog.yaml` validates.
- One library onboarded end-to-end before a second library (Exceptional).
- Library POMs depend on the **plugin**, never on `axiom-mcp`.
- `axiom-mcp` is a live process from the start (HTTP + MCP + CLI, local files).
- No merge gate, lint-diff, or dispatcher in the pilot.
- Dispatcher/CI/detectors stay written as PRDs so they are not forgotten.

## Repos

| Repo | Contains | First milestone |
|---|---|---|
| `sdempsay/axiom` | Docs, PRDs, this plan; git submodules | M0 |
| `sdempsay/axiom-plugin` | Schema, model, annotations, Maven plugin | M1–M2 |
| `sdempsay/axiom-mcp` | Ingest/merge, HTTP+MCP server, CLI, local store, gaps | M3 |
| `sdempsay/exceptional-java` | First `catalog.yaml` + examples + CI POST | M4 |

Umbrella layout:

```text
axiom/                          # sdempsay/axiom
├── PROJECT.md
├── PLAN.md
├── prds/
├── axiom-plugin/               # submodule → sdempsay/axiom-plugin
│   ├── schema/
│   ├── model/
│   ├── annotations/
│   └── plugin/
└── axiom-mcp/                  # submodule → sdempsay/axiom-mcp
    ├── server/                 # HTTP + MCP
    ├── ingest/
    └── cli/
```

Until the GitHub component remotes exist, `axiom-plugin/` and `axiom-mcp/` are ordinary directories that will become submodules.

## Phases

### M0 — Coordinates and owners (done in interview)

- groupId `org.dempsay.axiom`; parent `org.dempsay.maven:dempsay-parent`; Java 21.
- Umbrella `sdempsay/axiom`; components `axiom-plugin` and `axiom-mcp`.
- First library: Exceptional. Blessed snippet: `of` + `execute` + `wasError`/`response()`; `chain`/`then` as a second example.
- Service: live, local files, no auth, add/remove by Maven GAV, run on a single dev host.
- GitLab CI include, detectors, dispatcher: later.

**Exit:** this document matches the interview. Layout sketched on disk.

### M1 — Schema and model

**PRD:** [C1 Catalog schema](prds/C1-catalog-schema.md)

- JSON Schema for `catalog.yaml` in `axiom-plugin`.
- Java model `org.dempsay.axiom:axiom-model`.
- Validator used by the plugin and by `axiom validate`.
- Example records for `external_failure` (primary + chain snippets).

**Exit:** a catalog file fails on missing intent id, missing blessed symbol, duplicate ids, missing snippet file, or invalid regex.

### M2 — Maven plugin

**PRD:** [C2 Maven catalog plugin](prds/C2-maven-plugin.md)

- Plugin reads `src/main/resources/agent-catalog/catalog.yaml`.
- Validates against C1, stamps `${project.version}`.
- Attaches `classifier=agent-catalog`, type `yaml`.
- Embeds `META-INF/axiom/catalog.yaml` in the main jar (default).
- Optional `@AgentCapability` harvest off by default.

**Exit:** `mvn verify` on a fixture module publishes a catalog next to the jar. Exceptional is not required to be converted in this phase, but the fixture looks like Exceptional.

### M3 — Live axiom-mcp

**PRDs:** [C3](prds/C3-aggregator.md), [C4](prds/C4-mcp-server.md), [C5](prds/C5-cli.md), [C9](prds/C9-gap-workflow.md) (local files)

- One process: HTTP API + MCP transport + CLI against local files.
- `POST` a Maven GAV → fetch `classifier=agent-catalog` via Maven Resolver → store on disk → merge into the in-memory/on-disk index.
- `DELETE` a library from the index.
- Lookup / search / get.
- `catalog_gap` writes a gap file under the data dir.
- No auth. Data dir on the machine that runs the process (`~/.axiom` or configured).

**Exit:** from this laptop, add `org.dempsay.utils:exceptional` once that GAV has a catalog (or a fixture GAV) and `axiom lookup "IOException"` returns `external_failure`.

### M3.5 — Dogfood this repo

- Install dispatcher text in this umbrella `AGENTS.md` (C6, here only).
- `axiom catalog add` Exceptional (and, when published, axiom-plugin’s own catalog if it has intents).
- Agents in this repo call `catalog_lookup` before writing helpers.
- Keep the `code-review` pre-commit hook; it does not replace lookup.

**Exit:** one recorded session in this repo where lookup happens before a helper is written.

### M4 — Exceptional onboards

- Add `catalog.yaml` + snippets to `sdempsay/exceptional-java`.
- Bind `axiom-maven-plugin` with `required=true`.
- After deploy, CI POSTs the GAV to the local (or agreed) `axiom-mcp` URL.
- Intents: `external_failure` only unless more are already obvious. Cap remains ≤ 20.

**Exit:** Exceptional’s `mvn verify` publishes the catalog; a POST (or CLI add) makes lookup work without a hand-copied YAML on the server.

### M5+ — Later (PRDs exist, do not build now)

- C6 dispatcher text in consuming `AGENTS.md`.
- C7 GitLab lint-diff for private GitLab apps.
- C8 parent-POM detectors for `severity: required`.
- C9 GitHub issues instead of (or in addition to) local gap files.
- Auth on write APIs.
- Second library.

## Order of implementation work

```text
C1 schema (axiom-plugin)
  → C2 plugin (axiom-plugin)
  → C3 ingest/merge + C4 HTTP/MCP + C5 CLI + C9 local gaps (axiom-mcp)
      → M4 Exceptional catalog + CI POST
          → C6 / C7 / C8 later
```

## Staffing (minimum)

| Role | Time | Owns |
|---|---|---|
| One engineer | M1–M4 | plugin, service, Exceptional catalog |
| Exceptional owner | M4 | intent + snippets from real tests |

Do not staff a “knowledge graph team” until lookup is in daily use.

## Risks

| Risk | Mitigation |
|---|---|
| Catalog tries to list every method | Cap Exceptional at a handful of intents; leftover is “search first” |
| Library depends on the MCP service | Plugin only in the library POM; service fetches GAVs |
| Open write API abused | Pilot is a single host; add a token before sharing |
| Local files lost | Data dir is the store; operator backs it up or re-adds GAVs |
| Agents ignore lookup | Later: dispatcher + lint-diff + detectors |
| Maven Resolver confused with Aether persistence | Docs say “Maven Resolver”, never “Aether” |

## Definition of done for the pilot

- `axiom-plugin` validates and attaches `agent-catalog`.
- `axiom-mcp` runs on a single host, add/remove by GAV, lookup via HTTP/MCP/CLI.
- Exceptional publishes `external_failure` with `of`+`execute` as the primary snippet.
- Gaps persist as local files.
- C6–C8 are later for other repos; this repo dogfoods C6 as soon as lookup works.
- Java commits go through `code-review` pre-commit.
