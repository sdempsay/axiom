# TODO.md

Cross-cutting backlog for the [Axiom umbrella](https://github.com/sdempsay/axiom).

**Where work lives**

| Kind | Repo | Tracker |
|---|---|---|
| Cross-cutting (docs, onboard, dogfood, later org rollout) | this repo | this file + [axiom issues](https://github.com/sdempsay/axiom/issues) |
| Schema, annotations, Maven plugin | `axiom-plugin` | [plugin TODO](axiom-plugin/TODO.md) + [plugin issues](https://github.com/sdempsay/axiom-plugin/issues) |
| Live HTTP+MCP+CLI | `axiom-mcp` | [mcp TODO](axiom-mcp/TODO.md) + [mcp issues](https://github.com/sdempsay/axiom-mcp/issues) |

**Backlog sync:** Pending work is a GitHub Issue. This file is a thin index (ID, status, issue link) for offline session start. Details live on the issue. When shipping: `Fixes #N` in the PR → close issue → mark row `complete` here. Never delete a row.

## Cross-cutting

| ID | Task | Status | Issue |
|---|---|---|---|
| T0 | Interview mismatches; rewrite umbrella docs; sketch plugin/mcp dirs | complete | — |
| T11 | M4: Exceptional `catalog.yaml` + plugin + CI POST | pending | [#1](https://github.com/sdempsay/axiom/issues/1) |
| T12 | Promote `axiom-plugin` and `axiom-mcp` to GitHub remotes + submodules | complete | — |
| T12a | Pre-commit `code-review` dogfood on Java/POM | complete | — |
| T12b | M3.5: dispatcher + catalog lookup in this repo (first Axiom consumer) | pending | [#2](https://github.com/sdempsay/axiom/issues/2) |
| T12c | Extend code-review with `--fail-on REQUEST_CHANGES` | pending | [agentic-review-tool#6](https://github.com/sdempsay/agentic-review-tool/issues/6) |
| T13 | C6 consumer dispatcher (org-wide) | deferred | [#3](https://github.com/sdempsay/axiom/issues/3) |
| T14 | C7 GitLab lint-diff | deferred | [#4](https://github.com/sdempsay/axiom/issues/4) |
| T15 | C8 required detectors | deferred | [#5](https://github.com/sdempsay/axiom/issues/5) |
