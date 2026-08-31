# ACTIONS

## 2026-08-31

- Bumped `axiom-mcp` to master after lookup/search/get (#4).
- Bumped `axiom-mcp` to master after HTTP store (#1) and GAV add (#2). Bumped `axiom-plugin` to master after embed/harvest (#4) and `@since 1.0.0`.
- First public release is 1.0.0; Javadoc `@since` is `1.0.0` (not `0.1.0` / SNAPSHOT).
- Bumped `axiom-plugin` submodule to master after C1 (#1/#2) and C2 (#3) landed. Next plugin work is embed/harvest ([plugin#4](https://github.com/sdempsay/axiom-plugin/issues/4)).
- Adopted review-pipeline hybrid tracker: `TODO.md` index + GitHub Issues. Cross-cutting issues on sdempsay/axiom; plugin T1–T4 on axiom-plugin; MCP T5–T10/T16 on axiom-mcp; T12c on agentic-review-tool#6.
- Wired GitHub remotes: [sdempsay/axiom](https://github.com/sdempsay/axiom) umbrella with submodules [axiom-plugin](https://github.com/sdempsay/axiom-plugin) and [axiom-mcp](https://github.com/sdempsay/axiom-mcp).
- Pinned Exceptional examples and PRD GAVs to 1.0.9.
- Recorded dogfood requirements: pre-commit `code-review` on Java/POM now; this repo is the first Axiom consumer after M3 lookup. Hook lives in `hooks/pre-commit`; `bin/install-hooks` sets `core.hooksPath`.


- Extracted `axiom-docs.zip` and moved product docs to the umbrella root (nested `axiom/` folder duplicated this repo name). Zip stored at `~/axiom-docs.zip`.
- Interviewed hosting, repo split, Exceptional as first catalog, live `axiom-mcp`, and snippet shape.
- Rewrote `README.md`, `PROJECT.md`, `PLAN.md`, and `prds/` to match those decisions.
- Recorded interview outcomes in `PRD-updated.md`.
- Sketched `axiom-plugin/` and `axiom-mcp/` on disk (future git submodules of `sdempsay/axiom-plugin` and `sdempsay/axiom-mcp`).
- Moved schema examples under `axiom-plugin/schema/examples/` with a corrected Exceptional snippet (`of` + `execute`; chain as a second file).
