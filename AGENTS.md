# Agent notes

Guidance for AI agents in this repository. Global rules in `~/.grok/AGENTS.md` also apply. This project is **GitHub** (`sdempsay/axiom`); use `gh`, not `glab`.

## Session start

- Read `TODO.md`, `PROJECT.md`, `PLAN.md`, `PRD-updated.md`
- If Java work is in scope, read `~/.grok/rules/maven.md`

## Code review dogfood (`code-review`)

This repo dogfoods [agentic-review-tool](https://github.com/sdempsay/agentic-review-tool) (`code-review` on PATH from `~/tools/code-review`).

- A **pre-commit hook** runs `code-review diff --staged --no-chat` when staged paths include `*.java`, `*.java.ftl`, or `pom.xml`.
- Docs-only commits skip the hook.
- Install once per clone: `bin/install-hooks` (sets `core.hooksPath=hooks`).
- Bypass a single commit with `SKIP_CODE_REVIEW=1 git commit ...`.
- Verdicts `REQUEST_CHANGES` and `BLOCK` fail the commit. `APPROVE` and `APPROVE_WITH_NITS` pass.
- If `code-review` is missing, the hook fails (do not invent a review).
- New Java patterns learned here should feed **review-pipeline rules**, not one-off essays in this repo.

Manual equivalents:

```bash
code-review diff --staged --no-chat
code-review diff --base origin/master --no-chat
```

## Axiom self-dogfood (after M3)

When `axiom lookup` / MCP works, **this umbrella and its submodules are the first consuming repos** — dispatcher text in `AGENTS.md` here, Exceptional + Axiom’s own catalogs added by GAV. Do not wait for org-wide C6 rollout.

Until then, do not pretend catalog lookup exists. Use `code-review` and the PRDs.

## Exceptional

Java I/O and external calls in `axiom-plugin` / `axiom-mcp` use `ExceptionalSupplier.of(...).execute()` and `wasError()` / `response()`. `chain` / `then` for composing failing work. No business `try/catch` on those paths.

## Layout

| Path | Role |
|---|---|
| `prds/` | Component PRDs |
| `axiom-plugin/` | Future submodule: schema, annotations, Maven plugin |
| `axiom-mcp/` | Future submodule: live HTTP+MCP+CLI |
