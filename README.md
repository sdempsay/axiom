# Axiom

Org catalog of **intents → blessed implementations → detectors** for humans and coding agents.

This repository ([sdempsay/axiom](https://github.com/sdempsay/axiom)) is the umbrella: product definition and cross-cutting docs. Implementation lives in two component repos, attached here as git submodules.

```bash
git clone --recurse-submodules https://github.com/sdempsay/axiom.git
# existing clone:
git submodule update --init --recursive
```

| Document | What it is |
|---|---|
| [PROJECT.md](PROJECT.md) | Description and high-level goals |
| [PLAN.md](PLAN.md) | Execution plan by phase and repo |
| [PRD-updated.md](PRD-updated.md) | Requirements learned after the original zip |
| [prds/](prds/) | One PRD per actionable component |
| [TODO.md](TODO.md) | Cross-cutting index (plugin/MCP TODOs live in those repos) |
| [ACTIONS.md](ACTIONS.md) | Work log |

| Directory | GitHub | Job |
|---|---|---|
| [axiom-plugin/](axiom-plugin/) | `sdempsay/axiom-plugin` | Schema, annotations, Maven plugin |
| [axiom-mcp/](axiom-mcp/) | `sdempsay/axiom-mcp` | Live catalog service (HTTP + MCP + CLI) |

Start with `PROJECT.md`, then `PLAN.md`, then the PRD for the component you are building.

## Dogfood

- **Now:** `bin/install-hooks` — pre-commit runs `code-review diff --staged` on Java / `pom.xml` (see [AGENTS.md](AGENTS.md)).
- **After M3:** this repo is the first Axiom consumer (`axiom lookup` / MCP against catalogs we publish).
