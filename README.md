# Axiom

Org catalog of **intents → blessed implementations → detectors** for humans and coding agents.

This repository (`sdempsay/axiom`) is the umbrella: product definition and cross-cutting docs. Implementation lives in two component repos, attached here as git submodules.

| Document | What it is |
|---|---|
| [PROJECT.md](PROJECT.md) | Description and high-level goals |
| [PLAN.md](PLAN.md) | Execution plan by phase and repo |
| [PRD-updated.md](PRD-updated.md) | Requirements learned after the original zip |
| [prds/](prds/) | One PRD per actionable component |
| [TODO.md](TODO.md) | Work tracking |
| [ACTIONS.md](ACTIONS.md) | Work log |

| Directory | GitHub | Job |
|---|---|---|
| [axiom-plugin/](axiom-plugin/) | `sdempsay/axiom-plugin` | Schema, annotations, Maven plugin |
| [axiom-mcp/](axiom-mcp/) | `sdempsay/axiom-mcp` | Live catalog service (HTTP + MCP + CLI) |

Start with `PROJECT.md`, then `PLAN.md`, then the PRD for the component you are building.
