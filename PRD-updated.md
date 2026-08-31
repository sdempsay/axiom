# PRD updates (post-interview)

Requirements learned while reconciling the original zip with this org. Canonical product text is [PROJECT.md](PROJECT.md) and [PLAN.md](PLAN.md). This file is the decision log.

| ID | Decision |
|---|---|
| U1 | Maven `groupId` is `org.dempsay.axiom`, not `org.axiom`. |
| U2 | Java 21. Parent POM `org.dempsay.maven:dempsay-parent`. Packages `org.dempsay.axiom.*`. |
| U3 | Umbrella GitHub repo `sdempsay/axiom` (this tree). Component repos `sdempsay/axiom-plugin` and `sdempsay/axiom-mcp`, attached as git submodules. |
| U4 | `axiom-plugin` holds schema, annotations, and the Maven plugin only. |
| U5 | `axiom-mcp` holds ingest/merge, HTTP, MCP, and CLI. One process; MCP is another transport on the HTTP app. |
| U6 | GitLab lint-diff, parent-POM detectors, and the consumer dispatcher are **not** in the pilot. |
| U7 | First catalog is Exceptional (`org.dempsay.utils:exceptional`, `sdempsay/exceptional-java` on GitHub). |
| U8 | Axiom is public GitHub. Usage may mix public GitHub libraries with private GitLab apps. |
| U9 | Primary `external_failure` snippet is `ExceptionalSupplier.of(...).execute()` then `wasError()` / `response()`. `chain` / `then` is a second example, not a second intent. |
| U10 | Libraries never depend on `axiom-mcp`. They add the Maven plugin and publish `classifier=agent-catalog`. |
| U11 | Library CI POSTs the GAV to `axiom-mcp` after deploy. A user can also add a Maven GAV by API without waiting for that CI. |
| U12 | Remove-from-catalog is a first-class write on the service. |
| U13 | Pilot store is local files on the machine running `axiom-mcp`. No org-wide `axiom-index` Maven artifact. |
| U14 | Pilot auth: anyone who can reach the service may add or remove. Lookup is equally open. |
| U15 | Pilot hosting: one process on a single dev host. |
| U16 | `catalog_gap` writes a local gap file. GitHub issues are later. |
| U17 | Add-library identifier is a Maven GAV, not a git URL. |
| U18 | Do not call Maven Resolver “Aether” in Axiom docs (name clash with the Aether persistence project). |
| U19 | `ExceptionalResponse` has `wasError()`, not a required `wasNoError()` in the blessed snippet. |
| U20 | This repo dogfoods `code-review` (agentic-review-tool) on every Java/`pom.xml` commit via a pre-commit hook (`code-review diff --staged`). Missing tool fails the commit. `SKIP_CODE_REVIEW=1` is an explicit bypass. |
| U21 | Patterns found while building Axiom feed review-pipeline **rules**, not one-off essays. A later CLI extension is `--fail-on REQUEST_CHANGES` so hooks need not parse Markdown. |
| U22 | After M3 (lookup works), **this repo is the first Axiom consumer** (dispatcher + `axiom catalog add` Exceptional). Org-wide C6/C7/C8 stay later. |
| U23 | First-catalog Exceptional version is **1.0.9** (was 1.0.7 in the original zip). |
| U24 | GitHub remotes: https://github.com/sdempsay/axiom (umbrella), https://github.com/sdempsay/axiom-plugin, https://github.com/sdempsay/axiom-mcp. |
| U25 | Hybrid backlog (same as review-pipeline): GitHub Issues hold AC; `TODO.md` is a thin index. **Cross-cutting issues on sdempsay/axiom. Plugin issues on axiom-plugin. MCP issues on axiom-mcp.** |
| U26 | Official MCP Java SDK: do not depend on `io.modelcontextprotocol.sdk:mcp` (Jackson 3). Use `mcp-core` + `mcp-json-jackson2` so Jackson stays 2.x (`2.18.2`). HTTP in the pilot is JDK `HttpServer`. |
| U27 | First public release of Axiom artifacts is **1.0.0**. Javadoc `@since` is `1.0.0`, not `0.1.0` and not the Maven SNAPSHOT version. |
| U28 | Catalog add resolves from multiple Maven remotes: `--repo` / `AXIOM_REPOS`, `~/.m2/settings.xml` active-profile repositories (with matching `<server>` credentials), then Maven Central. Not Central-only. |
| U29 | Version from **latest git tag**, not the SNAPSHOT string. After tag `x.y.z`, Maven SNAPSHOT is `x.(y+1).0-SNAPSHOT` (always `.0`). New Javadoc `@since` is `x.y.(z+1)`. Exceptional tag `1.0.9` → `1.1.0-SNAPSHOT` / `@since 1.0.10`. Axiom has no tag yet; first public release `@since 1.0.0`. |
