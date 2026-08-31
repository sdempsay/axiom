# Agent notes

Guidance for AI agents in this repository. Global rules in `~/.grok/AGENTS.md` also apply.

This project is **GitHub**. Use `gh`, not `glab`.

| Repo | URL |
|---|---|
| Umbrella (this tree) | https://github.com/sdempsay/axiom |
| Plugin submodule | https://github.com/sdempsay/axiom-plugin |
| MCP submodule | https://github.com/sdempsay/axiom-mcp |

## Backlog (TODO.md + GitHub Issues)

Same hybrid as review-pipeline / aether, split by repo:

- **Cross-cutting** (docs, Exceptional onboard, dogfood, later org C6–C8) → **this repo** (`TODO.md` + [axiom issues](https://github.com/sdempsay/axiom/issues))
- **Plugin** (schema, annotations, Maven plugin) → `axiom-plugin/TODO.md` + [plugin issues](https://github.com/sdempsay/axiom-plugin/issues)
- **MCP** (ingest, HTTP, MCP, CLI, gaps) → `axiom-mcp/TODO.md` + [mcp issues](https://github.com/sdempsay/axiom-mcp/issues)

`TODO.md` is a thin index: ID, status, issue link (offline-friendly). Acceptance criteria and discussion live on the issue. Never delete a TODO row; mark `complete` or `deferred`.

**When starting work:** read the TODO for **that** repo → `gh issue view N --repo <owner/name>` for full context.

**When shipping:** PR on the repo that owns the code, with `Fixes #N` → issue closes → mark TODO `complete` → one line in that repo’s `ACTIONS.md`.

**When adding work:** open an issue on the **owning** repo first, then add a TODO row there with the issue link. Do not open plugin bugs on the umbrella.

## Session start

- Umbrella: `TODO.md`, `PROJECT.md`, `PLAN.md`, `PRD-updated.md`
- If the task is plugin or MCP, also read that submodule’s `TODO.md` / `AGENTS.md`
- If Java work is in scope, read `~/.grok/rules/maven.md`

## Code review dogfood (`code-review`)

This repo dogfoods [agentic-review-tool](https://github.com/sdempsay/agentic-review-tool) (`code-review` on PATH from `~/tools/code-review`).

- A **pre-commit hook** runs `code-review diff --staged --no-chat` when staged paths include `*.java`, `*.java.ftl`, or `pom.xml`.
- Java lives in the **submodules**; those repos have the same hook. Run `bin/install-hooks` in each clone.
- Docs-only commits skip the hook.
- Bypass a single commit with `SKIP_CODE_REVIEW=1 git commit ...`.
- Verdicts `REQUEST_CHANGES` and `BLOCK` fail the commit. `APPROVE` and `APPROVE_WITH_NITS` pass.
- If `code-review` is missing, the hook fails (do not invent a review).
- New Java patterns learned here should feed **review-pipeline rules**, not one-off essays. `--fail-on` is [agentic-review-tool#6](https://github.com/sdempsay/agentic-review-tool/issues/6).

```bash
code-review diff --staged --no-chat
code-review diff --base origin/master --no-chat
```

## Axiom self-dogfood (after M3)

When `axiom lookup` / MCP works, **this umbrella and its submodules are the first consuming repos** — dispatcher text in `AGENTS.md` here. Tracked as [axiom#2](https://github.com/sdempsay/axiom/issues/2). Until then, do not pretend catalog lookup exists.

## Exceptional

Java I/O and external calls in `axiom-plugin` / `axiom-mcp` use `ExceptionalSupplier.of(...).execute()` and `wasError()` / `response()`. `chain` / `then` for composing failing work. No business `try/catch` on those paths.

## Layout

| Path | Role |
|---|---|
| `prds/` | Component PRDs |
| `axiom-plugin/` | Submodule: schema, annotations, Maven plugin |
| `axiom-mcp/` | Submodule: live HTTP+MCP+CLI |
