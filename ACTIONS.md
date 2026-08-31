# ACTIONS

## 2026-08-31

- Recorded dogfood requirements: pre-commit `code-review` on Java/POM now; this repo is the first Axiom consumer after M3 lookup. Hook lives in `hooks/pre-commit`; `bin/install-hooks` sets `core.hooksPath`.


- Extracted `axiom-docs.zip` and moved product docs to the umbrella root (nested `axiom/` folder duplicated this repo name). Zip stored at `~/axiom-docs.zip`.
- Interviewed hosting, repo split, Exceptional as first catalog, live `axiom-mcp`, and snippet shape.
- Rewrote `README.md`, `PROJECT.md`, `PLAN.md`, and `prds/` to match those decisions.
- Recorded interview outcomes in `PRD-updated.md`.
- Sketched `axiom-plugin/` and `axiom-mcp/` on disk (future git submodules of `sdempsay/axiom-plugin` and `sdempsay/axiom-mcp`).
- Moved schema examples under `axiom-plugin/schema/examples/` with a corrected Exceptional snippet (`of` + `execute`; chain as a second file).
